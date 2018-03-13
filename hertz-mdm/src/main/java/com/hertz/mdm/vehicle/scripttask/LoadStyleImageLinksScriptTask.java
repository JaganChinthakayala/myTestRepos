package com.hertz.mdm.vehicle.scripttask;

import static com.hertz.mdm._hertz.constants.HtzConstants.COUNTRY_ALPHA2_CA;
import static com.hertz.mdm._hertz.constants.HtzConstants.COUNTRY_ALPHA2_US;
import static com.hertz.mdm.vehicle.constants.VehicleWorkflowConstants.FIRST_YEAR_PROPERTY;
import static com.hertz.mdm.vehicle.constants.VehicleWorkflowConstants.IMAGE_URI_PREFIX_PROPERTY;
import static com.hertz.mdm.vehicle.constants.VehicleWorkflowConstants.INPUT_DIRECTORY_PROPERTY;

import java.net.URI;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.hertz.mdm._hertz.constants.HtzConstants;
import com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.repository.RepositoryUtils;
import com.hertz.mdm._hertz.util.HtzUtilities;
import com.hertz.mdm.vehicle.path.VehicleReferencePaths;
import com.hertz.mdm.vehicle.scripttask.ImageGalleryMappingFileReader.ImageGalleryMappingRecord;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.PrimaryKey;
import com.orchestranetworks.ps.procedure.CreateRecordProcedure;
import com.orchestranetworks.ps.procedure.ModifyValuesProcedure;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.Session;
import com.orchestranetworks.workflow.ScriptTask;
import com.orchestranetworks.workflow.ScriptTaskContext;

public class LoadStyleImageLinksScriptTask extends ScriptTask
{
	private static final Pattern IMAGE_NUMBER_PATTERN = Pattern.compile("_(\\d+)\\.");

	private static final String FRONT_VIEW = "Front";
	private static final String REAR_VIEW = "Rear";
	private static final String SIDE_VIEW = "Side";
	private static final String INTERIOR_VIEW = "Interior";
	private static final String INVALID_IMAGE_NUMBER = "XX";

	@Override
	public void executeScript(ScriptTaskContext context) throws OperationException
	{
		Session session = context.getSession();
		AdaptationTable styleImageLinksTable = RepositoryUtils.getTable(
			HtzConstants.VEHICLE_DATA_SPACE,
			HtzConstants.VEHICLE_REFERENCE_DATA_SET,
			VehicleReferencePaths._Root_ChromeNewVehicleData_StyleImageLinks.getPathInSchema()
				.format());

		int finalYear = Calendar.getInstance().get(Calendar.YEAR) + 1;
		Properties properties = HtzUtilities.getProperties();
		ImageGalleryMappingFileReader mapFileReader = new ImageGalleryMappingFileReader(
			properties.getProperty(INPUT_DIRECTORY_PROPERTY));
		String imageUriPrefix = StringUtils
			.stripEnd(properties.getProperty(IMAGE_URI_PREFIX_PROPERTY), "/");
		int firstYear = Integer.valueOf(properties.getProperty(FIRST_YEAR_PROPERTY)).intValue();

		for (int year = firstYear; year <= finalYear; ++year)
		{
			loadRecordsFromFile(
				mapFileReader,
				year,
				COUNTRY_ALPHA2_US,
				imageUriPrefix,
				styleImageLinksTable,
				session);

			loadRecordsFromFile(
				mapFileReader,
				year,
				COUNTRY_ALPHA2_CA,
				imageUriPrefix,
				styleImageLinksTable,
				session);
		}
	}

	private void loadRecordsFromFile(
		ImageGalleryMappingFileReader mapFileReader,
		int year,
		String country,
		String imageUriPrefix,
		AdaptationTable styleImageLinksTable,
		Session session) throws OperationException
	{
		Set<ImageGalleryMappingRecord> mappingRecords = mapFileReader.readFile(country, year);
		if (mappingRecords.size() < 1)
		{
			return;
		}
		for (ImageGalleryMappingRecord mappingRecord : mappingRecords)
		{
			StyleImageLink link = toStyleImageLink(mappingRecord, imageUriPrefix);
			insertOrUpdateRecord(link, styleImageLinksTable, session);
		}
	}

	private StyleImageLink toStyleImageLink(
		ImageGalleryMappingRecord mappingRecord,
		String imageUriPrefix)
	{
		String imageNumber = extractImageNumber(mappingRecord.fileName);
		String linkId = mappingRecord.imageId + "_" + imageNumber;
		String viewType = deriveViewType(imageNumber);
		String uriString = String
			.format("%s/%s/%s", imageUriPrefix, mappingRecord.imageId, mappingRecord.fileName);
		URI imageUri = URI.create(uriString);
		return new StyleImageLink(mappingRecord.styleId, linkId, viewType, imageUri);
	}

	private void insertOrUpdateRecord(
		StyleImageLink link,
		AdaptationTable styleImageLinksTable,
		Session session) throws OperationException
	{
		Map<Path, Object> recordMap = new HashMap<>();
		recordMap.put(
			VehicleReferencePaths._Root_ChromeNewVehicleData_StyleImageLinks._Root_ChromeNewVehicleData_StyleImageLinks_StyleID,
			link.styleId);
		recordMap.put(
			VehicleReferencePaths._Root_ChromeNewVehicleData_StyleImageLinks._Root_ChromeNewVehicleData_StyleImageLinks_ImageID,
			link.imageId);
		recordMap.put(
			VehicleReferencePaths._Root_ChromeNewVehicleData_StyleImageLinks._Root_ChromeNewVehicleData_StyleImageLinks_ViewType,
			link.viewType);
		recordMap.put(
			VehicleReferencePaths._Root_ChromeNewVehicleData_StyleImageLinks._Root_ChromeNewVehicleData_StyleImageLinks_ImageLink,
			link.imageUri);

		Adaptation record = styleImageLinksTable.lookupAdaptationByPrimaryKey(
			PrimaryKey.parseString(link.styleId + "|" + link.imageId));
		if (record == null)
		{
			CreateRecordProcedure.execute(styleImageLinksTable, recordMap, session);
		}
		else
		{
			ModifyValuesProcedure.execute(record, recordMap, session);
		}
	}

	private String extractImageNumber(String imageName)
	{
		// numbers between underscore and period
		Matcher m = IMAGE_NUMBER_PATTERN.matcher(imageName);
		if (m.find())
		{
			return m.group(1);
		}
		else
		{
			return INVALID_IMAGE_NUMBER;
		}
	}

	private String deriveViewType(String imageNumber)
	{
		String viewType;
		switch (imageNumber)
		{
		case "01":
			viewType = FRONT_VIEW;
			break;
		case "02":
			viewType = REAR_VIEW;
			break;
		case "03":
			viewType = SIDE_VIEW;
			break;
		case "12":
			viewType = INTERIOR_VIEW;
			break;
		default:
			viewType = imageNumber;
			break;
		}
		return viewType;
	}

	public class StyleImageLink
	{
		public final String styleId;
		public final String imageId;
		public final String viewType;
		public final URI imageUri;

		public StyleImageLink(
			String styleId,
			String linkId,
			String viewType,
			URI imageUri)
		{
			this.styleId = styleId;
			this.imageId = linkId;
			this.viewType = viewType;
			this.imageUri = imageUri;
		}
	}

}
