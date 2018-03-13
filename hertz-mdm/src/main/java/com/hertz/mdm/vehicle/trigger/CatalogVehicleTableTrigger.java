package com.hertz.mdm.vehicle.trigger;

import java.util.*;

import com.hertz.mdm._hertz.constants.*;
import com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.repository.*;
import com.hertz.mdm.vehicle.path.*;
import com.hertz.mdm.vehicle.util.*;
import com.onwbp.adaptation.*;
import com.orchestranetworks.ps.procedure.*;
import com.orchestranetworks.ps.trigger.*;
import com.orchestranetworks.ps.util.*;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.schema.trigger.*;
import com.orchestranetworks.service.*;

public class CatalogVehicleTableTrigger extends BaseTableTrigger{

	// Reduce overhead by using a single script engine for each new vehicle record
	private ChromeOptionEvaluator conditionMatcher = new ChromeOptionEvaluator();

	@Override
	public void handleAfterCreate(AfterCreateOccurrenceContext context) throws OperationException {

		super.handleAfterCreate(context);

		// Get new vehicle record from context
		Adaptation catalogVehicleRecord = context.getAdaptationOccurrence();
		Adaptation dataSet = catalogVehicleRecord.getContainer();
		String vin = catalogVehicleRecord.getString(VehiclePaths._Root_VehicleData_CatalogVehicle._VehicleIdentificationNumber);

		// Get list of Chrome Options from /root/HertzVehicleOptionRelationship for the VIN
		ChromeOptionCodes vehicleOptionCodes = fetchChromeOptionCodes(dataSet, vin);

		String styleId = catalogVehicleRecord.getString(VehiclePaths._Root_VehicleData_CatalogVehicle._StyleID);
		ProcedureContext procedureContext = context.getProcedureContext();

		//TODO: Currently assuming there aren't already records. Need to handle case where relationships already exist

		List<Map<Path, Object>> relationshipRecords;

		/* --== Match Prices for this Style ==-- */
		if (!vehicleOptionCodes.isEmpty()) {
			Map<String, List<RecordChoice>> optionPriceChoices = fetchPriceRecords(styleId, vehicleOptionCodes);
			relationshipRecords = derivePriceRelationshipRecords(optionPriceChoices, vin, styleId, vehicleOptionCodes);
			createNewRelationshipRecords(procedureContext,
					dataSet.getTable(VehiclePaths._Root_VehicleData_CatalogVehicleData_CatalogVehicleOptionPriceRelationship.getPathInSchema()), relationshipRecords);
		}

		/* --== Match TechSpecs for this Style ==-- */
		Map<String, List<RecordChoice>> techSpecOptions = fetchTechSpecRecords(styleId);
		relationshipRecords = deriveTechSpecRelationshipRecords(techSpecOptions, vin, styleId, vehicleOptionCodes);
		createNewRelationshipRecords(procedureContext,
				dataSet.getTable(VehiclePaths._Root_VehicleData_CatalogVehicleData_CatalogVehicleTechSpecRelationship.getPathInSchema()), relationshipRecords);

		/* --== TODO: Match colors ==-- */
	}

	private ChromeOptionCodes fetchChromeOptionCodes(Adaptation dataSet, String vin) {

		// Get the relationship table from the Data Set
		AdaptationTable vehicleOptionRelationshipTable =
				dataSet.getTable(VehiclePaths._Root_VehicleData_CatalogVehicleOption.getPathInSchema());

		// Fetch all the option relationship records for the VIN
		RequestResult requestResult = vehicleOptionRelationshipTable
				.createRequestResult(VehiclePaths._Root_VehicleData_CatalogVehicleOption._CatalogVehicle.format() + "='" + vin + "'");

		List<String> rawOptionCodes = new ArrayList<>();
		try {
			if (requestResult.isEmpty()) {
				return ChromeOptionCodes.NONE;
			}

			// Gather the Chrome option codes for the VIN
			Adaptation relationshipRecord = requestResult.nextAdaptation();
			while (relationshipRecord != null) {
				Adaptation optionRecord = AdaptationUtil.followFK(relationshipRecord, VehiclePaths._Root_VehicleData_CatalogVehicleOption._Option);
				if (optionRecord != null) {
					String optionCode =
							(String) optionRecord.get(VehicleReferencePaths._Root_ChromeNewVehicleData_Options._Root_ChromeNewVehicleData_Options_OptionCode);
					rawOptionCodes.add(optionCode);
				}
				relationshipRecord = requestResult.nextAdaptation();
			}

		} finally {
			// Make sure we always close the request cursor
			requestResult.close();
		}
		return new ChromeOptionCodes(rawOptionCodes);
	}

	private Map<String, List<RecordChoice>> fetchPriceRecords(String styleId, ChromeOptionCodes optCodes) {
		// Get the Price table
		AdaptationTable pricesTable = RepositoryUtils.getTable(HtzConstants.VEHICLE_DATA_SPACE, HtzConstants.VEHICLE_REFERENCE_DATA_SET,
				VehicleReferencePaths._Root_ChromeNewVehicleData_Prices.getPathInSchema().format());

		// Fetch all the price records for the Style Id
		RequestResult priceRecords = pricesTable.createRequestResult(
						VehicleReferencePaths._Root_ChromeNewVehicleData_Prices._Root_ChromeNewVehicleData_Prices_StyleID.format()
						+ "='" + styleId + "'");
		Map<String, List<RecordChoice>> priceOptions;
		try {
			priceOptions = groupPriceRecordsByOptionCode(priceRecords, optCodes);
		} finally {
			// Make sure we always close the request cursor
			priceRecords.close();
		}
		return priceOptions;
	}

	private Map<String, List<RecordChoice>> groupPriceRecordsByOptionCode(RequestResult priceRecords, ChromeOptionCodes vehicleOptionCodes) {
		Map<String, List<RecordChoice>> priceOptions = new HashMap<>();

		// Gather the Chrome Option Prices for vehicle for each Option Code
		Adaptation priceRecord = priceRecords.nextAdaptation();
		while (priceRecord != null) {
			String optionCode = (String) priceRecord.get(VehicleReferencePaths._Root_ChromeNewVehicleData_Prices._Root_ChromeNewVehicleData_Prices_OptionCode);
			if (vehicleOptionCodes.contains(optionCode)) {
				List<RecordChoice> optionChoices = priceOptions.get(optionCode);
				if (optionChoices == null) {
					optionChoices = new ArrayList<>();
					priceOptions.put(optionCode, optionChoices);
				}
				Integer sequenceNumber = (Integer) priceRecord.get(VehicleReferencePaths._Root_ChromeNewVehicleData_Prices._Root_ChromeNewVehicleData_Prices_Sequence);
				String condition = (String) priceRecord.get(VehicleReferencePaths._Root_ChromeNewVehicleData_Prices._Root_ChromeNewVehicleData_Prices_Condition);
				optionChoices.add(new RecordChoice(sequenceNumber.intValue(), condition));
			}

			priceRecord = priceRecords.nextAdaptation();
		}

		return priceOptions;
	}

	private List<Map<Path,Object>> derivePriceRelationshipRecords(Map<String, List<RecordChoice>> priceOptions, String vin, String styleId,
			ChromeOptionCodes vehicleOptionCodes) {

		// Create price relationship records for each Option Code
		List<Map<Path, Object>> relationshipRecords = new ArrayList<>(priceOptions.size());
		for (String optionCode : priceOptions.keySet()) {
			List<RecordChoice> pricesForOptionCode = priceOptions.get(optionCode);

			// Per rules from Chrome NVD Prices table, first match in sequence wins
			Collections.sort(pricesForOptionCode);

			// Disambiguate multiple matches for a given Option Code
			int sequenceNumber = -1;
			boolean foundMatch = false;
			for (RecordChoice priceChoice : pricesForOptionCode) {
				// null or empty condition is always a match
				if (conditionMatcher.isMatch(priceChoice.condition, vehicleOptionCodes)) {
					sequenceNumber = priceChoice.sequenceNumber;
					foundMatch = true;
					break;
				}
			}

			// if a match was found, create a relationship record
			if (foundMatch) {
				Map<Path, Object> priceRelationshipRecord = new HashMap<>();
				priceRelationshipRecord.put(VehiclePaths._Root_VehicleData_CatalogVehicleData_CatalogVehicleOptionPriceRelationship._CatalogVehicle, vin);
				priceRelationshipRecord.put(VehiclePaths._Root_VehicleData_CatalogVehicleData_CatalogVehicleOptionPriceRelationship._Price, styleId + "|" + sequenceNumber + "|" + optionCode);
				relationshipRecords.add(priceRelationshipRecord);
			}
		}

		return relationshipRecords;
	}

	private void createNewRelationshipRecords(ProcedureContext procedureContext, AdaptationTable relationshipTable,
			List<Map<Path, Object>> records) throws OperationException {

		for (Map<Path, Object> record : records) {
			CreateRecordProcedure.execute(procedureContext, relationshipTable, record);
		}
	}

	private Map<String, List<RecordChoice>> fetchTechSpecRecords(String styleId) {
		// Get the TechSpec table
		AdaptationTable techSpecTable = RepositoryUtils.getTable(HtzConstants.VEHICLE_DATA_SPACE, HtzConstants.VEHICLE_REFERENCE_DATA_SET,
				VehicleReferencePaths._Root_ChromeNewVehicleData_TechSpecs.getPathInSchema().format());

		// Fetch all the techSpec records for the Style Id
		RequestResult techSpecRecords = techSpecTable.createRequestResult(
						VehicleReferencePaths._Root_ChromeNewVehicleData_TechSpecs._Root_ChromeNewVehicleData_TechSpecs_StyleID.format()
						+ "='" + styleId + "'");
		Map<String, List<RecordChoice>> techSpecOptions;
		try {
			techSpecOptions = groupTechSpecRecordsByTitle(techSpecRecords);
		} finally {
			// Make sure we always close the request cursor
			techSpecRecords.close();
		}
		return techSpecOptions;
	}

	private Map<String, List<RecordChoice>> groupTechSpecRecordsByTitle(RequestResult techSpecRecords) {
		Map<String, List<RecordChoice>> techSpecOptions = new HashMap<>();

		// Gather the Chrome Option techSpec for vehicle for each Title Id
		Adaptation techSpecRecord = techSpecRecords.nextAdaptation();
		while (techSpecRecord != null) {
			String titleId = (String) techSpecRecord.get(VehicleReferencePaths._Root_ChromeNewVehicleData_TechSpecs._Root_ChromeNewVehicleData_TechSpecs_TitleID);
			List<RecordChoice> optionChoices = techSpecOptions.get(titleId);
			if (optionChoices == null) {
				optionChoices = new ArrayList<>();
				techSpecOptions.put(titleId, optionChoices);
			}
			Integer sequenceNumber = (Integer) techSpecRecord.get(VehicleReferencePaths._Root_ChromeNewVehicleData_TechSpecs._Root_ChromeNewVehicleData_TechSpecs_Sequence);
			String condition = (String) techSpecRecord.get(VehicleReferencePaths._Root_ChromeNewVehicleData_TechSpecs._Root_ChromeNewVehicleData_TechSpecs_Condition);
			optionChoices.add(new RecordChoice(sequenceNumber.intValue(), condition));

			techSpecRecord = techSpecRecords.nextAdaptation();
		}

		return techSpecOptions;
	}

	private List<Map<Path,Object>> deriveTechSpecRelationshipRecords(Map<String, List<RecordChoice>> techSpecChoices, String vin, String styleId,
			ChromeOptionCodes vehicleOptionCodes) {

		// Create techSpec relationship records for each Title Id
		List<Map<Path, Object>> relationshipRecords = new ArrayList<>(techSpecChoices.size());
		for (String titleId : techSpecChoices.keySet()) {
			List<RecordChoice> techSpecsForTitleId = techSpecChoices.get(titleId);

			// Per rules from Chrome NVD techSpec table, first match in sequence wins
			Collections.sort(techSpecsForTitleId);

			// Disambiguate multiple matches for a given Option Code
			Integer sequenceNumber = null;
			for (RecordChoice techSpecChoice : techSpecsForTitleId) {
				// null or empty condition is always a match
				if (conditionMatcher.isMatch(techSpecChoice.condition, vehicleOptionCodes)) {
					sequenceNumber = Integer.valueOf(techSpecChoice.sequenceNumber);
					break;
				}
			}

			// if a match was found, create a relationship record
			if (sequenceNumber != null) {
				Map<Path, Object> techSpecRelationshipRecord = new HashMap<>();
				techSpecRelationshipRecord.put(VehiclePaths._Root_VehicleData_CatalogVehicleData_CatalogVehicleTechSpecRelationship._CatalogVehicle, vin);
				techSpecRelationshipRecord.put(VehiclePaths._Root_VehicleData_CatalogVehicleData_CatalogVehicleTechSpecRelationship._TechSpec, styleId + "|" + titleId + "|" + sequenceNumber);
				relationshipRecords.add(techSpecRelationshipRecord);
			}
		}

		return relationshipRecords;
	}

	/**
	 * Organize potential record matches by sorting on Sequence Number
	 * @author pbranch
	 *
	 */
	static class RecordChoice implements Comparable<RecordChoice> {
		public final int sequenceNumber;
		public final String condition;

		public RecordChoice(int sequenceNumber, String condition) {
			this.sequenceNumber = sequenceNumber;
			this.condition = condition != null ? condition : "";
		}

		@Override
		public int compareTo(RecordChoice o) {
			return (int) Math.signum(this.sequenceNumber - o.sequenceNumber);
		}
	}

}
