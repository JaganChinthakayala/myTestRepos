/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.scripttask;

import java.util.Date;
import java.util.HashMap;

import com.hertz.mdm.location.constants.LocationConstants;
import com.hertz.mdm.location.enums.ContactPirmarySecondary;
import com.hertz.mdm.location.enums.LocationProjectStatuses;
import com.hertz.mdm.location.path.LocationPaths;
import com.hertz.mdm.location.workflow.LocationProjectWorkflowUtilities;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.RequestResult;
import com.orchestranetworks.ps.procedure.CreateRecordProcedure;
import com.orchestranetworks.ps.procedure.ModifyValuesProcedure;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.workflow.ScriptTask;
import com.orchestranetworks.workflow.ScriptTaskContext;

public class CompleteMoveLocationProcessScriptTask extends ScriptTask
{
	@Override
	public void executeScript(ScriptTaskContext context) throws OperationException
	{
		Adaptation projectRecord = LocationProjectWorkflowUtilities
			.getLocationProjectRecord(context, context.getRepository());

		if (projectRecord == null)
		{
			return;
		}

		Adaptation locationRecord = AdaptationUtil
			.followFK(projectRecord, LocationPaths._Root_LocationProject._Location);

		if (locationRecord == null)
		{
			return;
		}

		String locationId = locationRecord.get(LocationPaths._Root_Location._Id).toString();
		String projectId = projectRecord.get(LocationPaths._Root_Location._Id).toString();

		//Get the Primary Business Address for the Location
		AdaptationTable locationAddressTable = locationRecord.getContainer()
			.getTable(LocationPaths._Root_LocationData_Address.getPathInSchema());

		// Query the LocationAddress table for a row of type "BUSINESS"
		RequestResult requestResult = locationAddressTable.createRequestResult(
			LocationPaths._Root_LocationData_Address._Location.format() + "='" + locationId
				+ "' and " + LocationPaths._Root_LocationData_Address._Type.format() + "='"
				+ LocationConstants.LOCATION_ADDRESS_TYPE_BUSINESS + "' and "
				+ LocationPaths._Root_LocationData_Address._PrimarySecondary.format() + " = '"
				+ ContactPirmarySecondary.PRIMARY + "' and "
				+ LocationPaths._Root_LocationData_Address._IsRecordActive.format() + "=true");

		if (!requestResult.isEmpty())
		{
			//Expire the current Primary Business Address Record
			Adaptation locationAddressRecord = requestResult.nextAdaptation();

			HashMap<Path, Object> pathValueMapLocationAddress = new HashMap<Path, Object>();

			pathValueMapLocationAddress.put(
				LocationPaths._Root_LocationProject._EffectiveStartEndDates_EndDate,
				new Date());

			ModifyValuesProcedure.execute(
				locationAddressRecord,
				pathValueMapLocationAddress,
				context.getSession(),
				true,
				false);
		}

		//Create the new Location Address Record from the Project Address Record

		//Get the Primary Business Address for the Project
		AdaptationTable projectAddressTable = locationRecord.getContainer()
			.getTable(LocationPaths._Root_ProjectData_Address.getPathInSchema());

		// Query the LocationAddress table for a row of type "BUSINESS"
		requestResult = projectAddressTable.createRequestResult(
			LocationPaths._Root_ProjectData_Address._LocationProject.format() + "='" + projectId
				+ "' and " + LocationPaths._Root_ProjectData_Address._Type.format() + "='"
				+ LocationConstants.LOCATION_ADDRESS_TYPE_BUSINESS + "' and "
				+ LocationPaths._Root_ProjectData_Address._PrimarySecondary.format() + " = '"
				+ ContactPirmarySecondary.PRIMARY + "'");

		if (!requestResult.isEmpty())
		{
			Adaptation projectAddressRecord = requestResult.nextAdaptation();

			HashMap<Path, Object> pathValueMapProjectAddress = new HashMap<Path, Object>();

			pathValueMapProjectAddress
				.put(LocationPaths._Root_LocationData_Address._Location, locationId);
			pathValueMapProjectAddress.put(
				LocationPaths._Root_LocationData_Address._Type,
				projectAddressRecord.get(LocationPaths._Root_ProjectData_Address._Type));
			pathValueMapProjectAddress.put(
				LocationPaths._Root_LocationData_Address._PrimarySecondary,
				projectAddressRecord
					.get(LocationPaths._Root_ProjectData_Address._PrimarySecondary));
			pathValueMapProjectAddress.put(
				LocationPaths._Root_LocationData_Address._Region,
				projectAddressRecord.get(LocationPaths._Root_ProjectData_Address._Region));
			pathValueMapProjectAddress.put(
				LocationPaths._Root_LocationData_Address._Address_StreetNumber,
				projectAddressRecord
					.get(LocationPaths._Root_ProjectData_Address._Address_StreetNumber));
			pathValueMapProjectAddress.put(
				LocationPaths._Root_LocationData_Address._Address_AddressLine1,
				projectAddressRecord
					.get(LocationPaths._Root_ProjectData_Address._Address_AddressLine1));
			pathValueMapProjectAddress.put(
				LocationPaths._Root_LocationData_Address._Address_AddressLine2,
				projectAddressRecord
					.get(LocationPaths._Root_ProjectData_Address._Address_AddressLine2));
			pathValueMapProjectAddress.put(
				LocationPaths._Root_LocationData_Address._Address_AddressLine3,
				projectAddressRecord
					.get(LocationPaths._Root_ProjectData_Address._Address_AddressLine3));
			pathValueMapProjectAddress.put(
				LocationPaths._Root_LocationData_Address._Address_Country,
				projectAddressRecord.get(LocationPaths._Root_ProjectData_Address._Address_Country));
			pathValueMapProjectAddress.put(
				LocationPaths._Root_LocationData_Address._Address_StateProvince,
				projectAddressRecord
					.get(LocationPaths._Root_ProjectData_Address._Address_StateProvince));
			pathValueMapProjectAddress.put(
				LocationPaths._Root_LocationData_Address._Address_County,
				projectAddressRecord.get(LocationPaths._Root_ProjectData_Address._Address_County));
			pathValueMapProjectAddress.put(
				LocationPaths._Root_LocationData_Address._Address_City,
				projectAddressRecord.get(LocationPaths._Root_ProjectData_Address._Address_City));
			pathValueMapProjectAddress.put(
				LocationPaths._Root_LocationData_Address._Address_PostalCode,
				projectAddressRecord
					.get(LocationPaths._Root_ProjectData_Address._Address_PostalCode));

			if (projectAddressRecord
				.get(LocationPaths._Root_ProjectData_Address._GeographicPoint_Latitude) != null)
			{
				pathValueMapProjectAddress.put(
					LocationPaths._Root_LocationData_Address._GeographicPoint_Latitude,
					projectAddressRecord
						.get(LocationPaths._Root_ProjectData_Address._GeographicPoint_Latitude));
			}
			if (projectAddressRecord
				.get(LocationPaths._Root_ProjectData_Address._GeographicPoint_Longitude) != null)
			{
				pathValueMapProjectAddress.put(
					LocationPaths._Root_LocationData_Address._GeographicPoint_Longitude,
					projectAddressRecord
						.get(LocationPaths._Root_ProjectData_Address._GeographicPoint_Longitude));
			}

			CreateRecordProcedure.execute(
				locationAddressTable,
				pathValueMapProjectAddress,
				context.getSession(),
				true,
				false);
		}

		//Expire the Move Project Record
		HashMap<Path, Object> pathValueMapProject = new HashMap<Path, Object>();

		pathValueMapProject
			.put(LocationPaths._Root_LocationProject._EffectiveStartEndDates_EndDate, new Date());
		pathValueMapProject.put(
			LocationPaths._Root_LocationProject._ProjectStatus,
			LocationProjectStatuses.LOCATION_PROJECT_STATUS_COMPLETE);

		ModifyValuesProcedure
			.execute(projectRecord, pathValueMapProject, context.getSession(), true, false);

		//Clear the CurrentProjectType from the Location
		HashMap<Path, Object> pathValueMapLocation = new HashMap<Path, Object>();
		pathValueMapLocation.put(LocationPaths._Root_Location._CurrentProjectType, null);
		ModifyValuesProcedure
			.execute(locationRecord, pathValueMapLocation, context.getSession(), true, false);
	}
}
