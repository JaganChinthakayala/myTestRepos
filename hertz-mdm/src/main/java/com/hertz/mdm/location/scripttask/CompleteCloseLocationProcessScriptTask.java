/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.scripttask;

import java.util.Date;
import java.util.HashMap;

import com.hertz.mdm.location.constants.LocationConstants;
import com.hertz.mdm.location.enums.AddressPirmarySecondary;
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

public class CompleteCloseLocationProcessScriptTask extends ScriptTask
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

		//Create a new Primary Business Address for the location
		Adaptation projectAddressRecord = AdaptationUtil.followFK(
			projectRecord,
			LocationPaths._Root_LocationProject._MoveLocation_NewLocation_LocationMoveToAddress);

		String locationId = locationRecord.get(LocationPaths._Root_Location._Id).toString();

		HashMap<Path, Object> pathValueMapProjectAddress = new HashMap<Path, Object>();

		pathValueMapProjectAddress
			.put(LocationPaths._Root_LocationData_Address._Location, locationId);
		pathValueMapProjectAddress.put(
			LocationPaths._Root_LocationData_Address._Type,
			LocationConstants.LOCATION_ADDRESS_TYPE_BUSINESS);
		pathValueMapProjectAddress.put(
			LocationPaths._Root_LocationData_Address._PrimarySecondary,
			AddressPirmarySecondary.PRIMARY);
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
			projectAddressRecord.get(LocationPaths._Root_ProjectData_Address._Address_PostalCode));
		pathValueMapProjectAddress.put(
			LocationPaths._Root_LocationData_Address._Address_Attention,
			projectAddressRecord.get(LocationPaths._Root_ProjectData_Address._Address_Attention));
		pathValueMapProjectAddress.put(
			LocationPaths._Root_LocationData_Address._Address_Status,
			projectAddressRecord.get(LocationPaths._Root_ProjectData_Address._Address_Status));
		pathValueMapProjectAddress.put(
			LocationPaths._Root_LocationData_Address._GeographicPoint_Latitude,
			projectAddressRecord
				.get(LocationPaths._Root_ProjectData_Address._GeographicPoint_Longitude));
		pathValueMapProjectAddress.put(
			LocationPaths._Root_LocationData_Address._GeographicPoint_Latitude,
			projectAddressRecord
				.get(LocationPaths._Root_ProjectData_Address._GeographicPoint_Longitude));
		pathValueMapProjectAddress.put(
			LocationPaths._Root_LocationData_Address._Region,
			projectAddressRecord.get(LocationPaths._Root_ProjectData_Address._Region));
		pathValueMapProjectAddress.put(
			LocationPaths._Root_LocationData_Address._Address_AdditionalInformation,
			projectAddressRecord
				.get(LocationPaths._Root_ProjectData_Address._AdditionalInformation));

		AdaptationTable locationProjectAddressTable = projectRecord.getContainer()
			.getTable(LocationPaths._Root_ProjectData_Address.getPathInSchema());

		CreateRecordProcedure.execute(
			locationProjectAddressTable,
			pathValueMapProjectAddress,
			context.getSession(),
			true,
			false);

		//Expire the existing Primary Business Address record
		AdaptationTable locationAddressTable = locationRecord.getContainer()
			.getTable(LocationPaths._Root_LocationData_Address.getPathInSchema());

		RequestResult requestResult = locationAddressTable.createRequestResult(
			LocationPaths._Root_LocationData_Address._Location.format() + "='" + locationId
				+ "' and " + LocationPaths._Root_LocationData_Address._Type.format() + "='"
				+ LocationConstants.LOCATION_ADDRESS_TYPE_BUSINESS + "' and " + "' and "
				+ LocationPaths._Root_LocationData_Address._PrimarySecondary.format() + "='"
				+ AddressPirmarySecondary.PRIMARY + "' and "
				+ LocationPaths._Root_LocationData_Address._IsRecordActive.format() + "=true");

		if (!requestResult.isEmpty())
		{
			HashMap<Path, Object> pathValueMapLocationAddress = new HashMap<Path, Object>();
			pathValueMapLocationAddress.put(
				LocationPaths._Root_LocationProject._EffectiveStartEndDates_EndDate,
				new Date());
			ModifyValuesProcedure.execute(
				projectRecord,
				pathValueMapLocationAddress,
				context.getSession(),
				true,
				false);
		}

		HashMap<Path, Object> pathValueMapProject = new HashMap<Path, Object>();
		pathValueMapProject
			.put(LocationPaths._Root_LocationProject._EffectiveStartEndDates_EndDate, new Date());
		pathValueMapProject.put(
			LocationPaths._Root_LocationProject._ProjectStatus,
			LocationProjectStatuses.LOCATION_PROJECT_STATUS_COMPLETE);
		ModifyValuesProcedure
			.execute(projectRecord, pathValueMapProject, context.getSession(), true, false);

		HashMap<Path, Object> pathValueMapLocation = new HashMap<Path, Object>();
		pathValueMapLocation.put(LocationPaths._Root_Location._CurrentProjectType, null);
		ModifyValuesProcedure
			.execute(locationRecord, pathValueMapLocation, context.getSession(), true, false);
	}
}
