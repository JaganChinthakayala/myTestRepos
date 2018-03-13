/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.trigger;

import java.util.HashMap;
import java.util.Map;

import com.hertz.mdm._hertz.constants.HtzConstants;
import com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.repository.RepositoryUtils;
import com.hertz.mdm.common.path.CommonReferencePaths;
import com.hertz.mdm.location.constants.LocationConstants;
import com.hertz.mdm.location.enums.LocationProjectTypes;
import com.hertz.mdm.location.enums.LocationUpdateTypes;
import com.hertz.mdm.location.path.LocationPaths;
import com.hertz.mdm.location.util.HtzLocationUtilities;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.RequestResult;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.ps.procedure.CreateRecordProcedure;
import com.orchestranetworks.ps.procedure.ModifyValuesProcedure;
import com.orchestranetworks.ps.trigger.BaseTableTrigger;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.trigger.AfterCreateOccurrenceContext;
import com.orchestranetworks.schema.trigger.AfterDeleteOccurrenceContext;
import com.orchestranetworks.schema.trigger.AfterModifyOccurrenceContext;
import com.orchestranetworks.schema.trigger.NewTransientOccurrenceContext;
import com.orchestranetworks.schema.trigger.ValueChanges;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.ProcedureContext;
import com.orchestranetworks.service.ValueContextForUpdate;

/**
 */
public class LocationAddressTableTrigger extends BaseTableTrigger
{
	private static String adminUser = "admin";

	@Override
	public void handleNewContext(NewTransientOccurrenceContext context)
	{
		//If a TechAdmin is doing a load, do not automatically set values
		//	if (context.getSession().isUserInRole(Role.forSpecificRole(HtzConstants.ROLE_TECH_ADMIN)))
		//	{
		//	return;
		//}

		//If the User "admin" is doing the load, do not automatically set values
		if (adminUser.equals(context.getSession().getUserReference().getUserId()))
		{
			return;
		}

		ValueContextForUpdate updateContext = context.getOccurrenceContextForUpdate();

		updateContext.setValue(
			HtzConstants.COUNTRY_CODE_US,
			LocationPaths._Root_LocationData_Address._Address_Country);

		super.handleNewContext(context);
	}

	@Override
	public void handleAfterCreate(AfterCreateOccurrenceContext context) throws OperationException
	{
		//If a TechAdmin is doing a load, do not automatically set values
		//	if (context.getSession().isUserInRole(Role.forSpecificRole(HtzConstants.ROLE_TECH_ADMIN)))
		//	{
		//		return;
		//	}

		Adaptation locationAddressRecord = context.getAdaptationOccurrence();

		//If this is not an OPEN Workflow, do not create the Records below
		Adaptation locationRecord = AdaptationUtil
			.followFK(locationAddressRecord, LocationPaths._Root_LocationData_Address._Location);

		HtzLocationUtilities.writeToUpdateNotificationTable(
			locationRecord,
			LocationUpdateTypes.LOCATION_CREATE,
			context.getAdaptationOccurrence().toString(),
			context.getProcedureContext());

		//If the User "admin" is doing the load, do not automatically set values
		if (adminUser.equals(context.getSession().getUserReference().getUserId()))
		{
			return;
		}

		if (!LocationProjectTypes.OPEN_LOCATION
			.equals(locationRecord.get(LocationPaths._Root_Location._CurrentProjectType)))
		{
			return;
		}

		String stateProvinceId = null;

		String addressType = locationAddressRecord
			.getString(LocationPaths._Root_LocationData_Address._Type);

		if (!LocationConstants.LOCATION_ADDRESS_TYPE_BUSINESS.equals(addressType))
		{
			return;
		}

		String locationId = locationAddressRecord
			.getString(LocationPaths._Root_LocationData_Address._Location);

		Adaptation countryRecord = AdaptationUtil.followFK(
			locationAddressRecord,
			LocationPaths._Root_LocationData_Address._Address_Country);

		if (countryRecord == null)
		{
			return;
		}

		String currencyCode = countryRecord
			.getString(CommonReferencePaths._Root_Country_ISO31661._CurrencyAlphaCode);

		AdaptationTable locationCurrencyRelationshipTable = locationAddressRecord.getContainer()
			.getTable(
				LocationPaths._Root_LocationData_LocationCurrencyRelationship.getPathInSchema());

		RequestResult requestResult = locationCurrencyRelationshipTable
			.createRequestResult(
				LocationPaths._Root_LocationData_LocationCurrencyRelationship._Location.format()
					+ "='" + locationId + "' and "
					+ LocationPaths._Root_LocationData_LocationCurrencyRelationship._Currency
						.format()
					+ "='" + currencyCode + "' and "
					+ LocationPaths._Root_LocationData_LocationCurrencyRelationship._IsRecordActive
						.format()
					+ "=true");

		if (requestResult.isEmpty())
		{
			HashMap<Path, Object> pathValueMapCurrency = new HashMap<Path, Object>();

			pathValueMapCurrency.put(
				LocationPaths._Root_LocationData_LocationCurrencyRelationship._Location,
				locationId);
			pathValueMapCurrency.put(
				LocationPaths._Root_LocationData_LocationCurrencyRelationship._Currency,
				currencyCode);
			pathValueMapCurrency.put(
				LocationPaths._Root_LocationData_LocationCurrencyRelationship._IsPrimaryCurrency,
				true);

			CreateRecordProcedure.execute(
				context.getProcedureContext(),
				locationCurrencyRelationshipTable,
				pathValueMapCurrency,
				true,
				false);
		}

		//Set the Default MeasurementType and Language on Location based up the Business Address Country
		if (LocationConstants.LOCATION_ADDRESS_TYPE_BUSINESS.equals(
			locationAddressRecord.getString(LocationPaths._Root_LocationData_Address._Type)))
		{
			HashMap<Path, Object> pathValueMapMeasurementAndLanguage = new HashMap<Path, Object>();

			if (HtzConstants.COUNTRY_CODE_US.equals(
				locationAddressRecord
					.getString(LocationPaths._Root_LocationData_Address._Address_Country)))
			{
				pathValueMapMeasurementAndLanguage.put(
					LocationPaths._Root_Location._LocationInformation_MeasurementSystem,
					HtzConstants.MEASUREMENT_TYPE_US);

				pathValueMapMeasurementAndLanguage.put(
					LocationPaths._Root_Location._LocationInformation_Language,
					HtzConstants.LANGUAGE_ENGLISH);
			}
			else
			{
				pathValueMapMeasurementAndLanguage.put(
					LocationPaths._Root_Location._LocationInformation_MeasurementSystem,
					HtzConstants.MEASUREMENT_TYPE_METRIC);
			}

			ModifyValuesProcedure.execute(
				context.getProcedureContext(),
				locationRecord,
				pathValueMapMeasurementAndLanguage,
				true,
				false);

			String googleMapsStateProvince = locationAddressRecord
				.getString(LocationPaths._Root_LocationData_Address._Region);

			String countryId = locationAddressRecord
				.get(LocationPaths._Root_LocationData_Address._Address_Country)
				.toString();

			AdaptationTable commonReferenceStateProvinceRelationshipTable = RepositoryUtils
				.getTable(
					RepositoryUtils.getDataSet(
						HtzConstants.COMMON_REFERENCE_DATA_SPACE,
						HtzConstants.COMMON_REFERENCE_DATA_SET),
					CommonReferencePaths._Root_Subdivision_ISO31662.getPathInSchema().format());

			// Query the State/Province Relationship table for this Country and State/Province
			requestResult = commonReferenceStateProvinceRelationshipTable.createRequestResult(
				CommonReferencePaths._Root_Subdivision_ISO31662._ISO31661_Integer.format() + "='"
					+ countryId + "' and "
					+ CommonReferencePaths._Root_Subdivision_ISO31662._ISO31662_SubdivisionName
						.format()
					+ "='" + googleMapsStateProvince + "'");

			Map<Path, Object> pathValueMap = new HashMap<Path, Object>();

			if (requestResult.isEmpty())
			{
				return;
			}

			Adaptation stateProvinceRecord = requestResult.nextAdaptation();

			stateProvinceId = stateProvinceRecord
				.getString(CommonReferencePaths._Root_Subdivision_ISO31662._ISO31662_Subdivision);

			pathValueMap.put(
				LocationPaths._Root_LocationData_Address._Address_StateProvince,
				stateProvinceId);

			ModifyValuesProcedure.execute(
				context.getProcedureContext(),
				locationAddressRecord,
				pathValueMap,
				true,
				false);

			setLocationDefaultTimezone(
				locationRecord,
				stateProvinceId,
				context.getProcedureContext());
		}

		super.handleAfterCreate(context);
	}

	@Override
	public void handleAfterModify(AfterModifyOccurrenceContext context) throws OperationException
	{
		//super.handleAfterModify(context);

		Adaptation locationRecord = AdaptationUtil.followFK(
			context.getAdaptationOccurrence(),
			LocationPaths._Root_LocationData_Address._Location);

		HtzLocationUtilities.writeToUpdateNotificationTable(
			locationRecord,
			LocationUpdateTypes.LOCATION_UPDATE,
			context.getAdaptationOccurrence().toString(),
			context.getProcedureContext());

		String stateProvinceId = null;

		Adaptation locationAddressRecord = context.getAdaptationOccurrence();

		ValueChanges changes = context.getChanges();

		if (changes.getChange(LocationPaths._Root_LocationData_Address._Region) != null)
		{
			String googleMapsStateProvince = locationAddressRecord
				.getString(LocationPaths._Root_LocationData_Address._Region);

			String countryId = locationAddressRecord
				.get(LocationPaths._Root_LocationData_Address._Address_Country)
				.toString();

			AdaptationTable commonReferenceTable = RepositoryUtils.getTable(
				RepositoryUtils.getDataSet(
					HtzConstants.COMMON_REFERENCE_DATA_SPACE,
					HtzConstants.COMMON_REFERENCE_DATA_SET),
				CommonReferencePaths._Root_Subdivision_ISO31662.getPathInSchema().format());

			// Query the LocationHierarchy/Business Party Relationship table for the location Region
			RequestResult requestResult = commonReferenceTable.createRequestResult(
				CommonReferencePaths._Root_Subdivision_ISO31662._ISO31661_Integer.format() + "='"
					+ countryId + "' and "
					+ CommonReferencePaths._Root_Subdivision_ISO31662._ISO31662_SubdivisionName
						.format()
					+ "='" + googleMapsStateProvince + "'");

			Map<Path, Object> pathValueMap = new HashMap<Path, Object>();

			if (!requestResult.isEmpty())
			{
				Adaptation stateProvinceRecord = requestResult.nextAdaptation();

				stateProvinceId = stateProvinceRecord.getString(
					CommonReferencePaths._Root_Subdivision_ISO31662._ISO31662_Subdivision);

				pathValueMap.put(
					LocationPaths._Root_LocationData_Address._Address_StateProvince,
					stateProvinceId);
			}

			ModifyValuesProcedure.execute(
				context.getProcedureContext(),
				locationAddressRecord,
				pathValueMap,
				true,
				false);

			//	Adaptation locationRecord = AdaptationUtil.followFK(
			//		locationAddressRecord,
			//		LocationPaths._Root_LocationData_Address._Location);

			setLocationDefaultTimezone(
				locationRecord,
				stateProvinceId,
				context.getProcedureContext());

			super.handleAfterModify(context);
		}
	}

	@Override
	public void handleAfterDelete(AfterDeleteOccurrenceContext context) throws OperationException
	{
		super.handleAfterDelete(context);

		ValueContext valueContext = context.getOccurrenceContext();

		Adaptation locationRecord = AdaptationUtil
			.followFK(valueContext, LocationPaths._Root_LocationData_Address._Location);

		HtzLocationUtilities.writeToUpdateNotificationTable(
			locationRecord,
			LocationUpdateTypes.LOCATION_DELETE,
			valueContext.toString(),
			context.getProcedureContext());
	}

	private static void setLocationDefaultTimezone(
		Adaptation locationRecord,
		String locationStateProvinceId,
		ProcedureContext pContext)
		throws OperationException
	{
		if (locationStateProvinceId == null)
		{
			return;
		}

		AdaptationTable stateProvinceTimeZoneRelationshipTable = RepositoryUtils.getTable(
			RepositoryUtils.getDataSet(
				HtzConstants.COMMON_REFERENCE_DATA_SPACE,
				HtzConstants.COMMON_REFERENCE_DATA_SET),
			CommonReferencePaths._Root_StateProvinceTimeZoneRelationship.getPathInSchema()
				.format());

		RequestResult requestResult = stateProvinceTimeZoneRelationshipTable.createRequestResult(
			CommonReferencePaths._Root_StateProvinceTimeZoneRelationship._StateProvince.format()
				+ "= '" + locationStateProvinceId + "' and "
				+ CommonReferencePaths._Root_StateProvinceTimeZoneRelationship._IsRecordActive
					.format()
				+ "=true");

		if (requestResult.isEmpty() || requestResult.getSize() > 1)
		{
			return;
		}

		Adaptation stateProviceTimeZoneRelationshipRecord = requestResult.nextAdaptation();

		String timeZoneKey = stateProviceTimeZoneRelationshipRecord
			.get(CommonReferencePaths._Root_StateProvinceTimeZoneRelationship._StateProvince)
			.toString()
			+ "|"
			+ stateProviceTimeZoneRelationshipRecord
				.get(CommonReferencePaths._Root_StateProvinceTimeZoneRelationship._Timezone)
				.toString();

		HashMap<Path, Object> pathValue = new HashMap<Path, Object>();

		pathValue.put(
			LocationPaths._Root_Location._LocationInformation_StateProvinceTimeZone,
			timeZoneKey);

		ModifyValuesProcedure.execute(pContext, locationRecord, pathValue, true, false);
	}
}
