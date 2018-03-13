/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.condition;

import com.hertz.mdm.common.util.HtzCommonUtilities;
import com.hertz.mdm.location.constants.LocationConstants;
import com.hertz.mdm.location.path.LocationPaths;
import com.hertz.mdm.location.workflow.LocationProjectWorkflowUtilities;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.RequestResult;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.workflow.Condition;
import com.orchestranetworks.workflow.ConditionContext;

public class IsLocationAPotentialDuplicateCondition extends Condition
{
	@Override
	public boolean evaluateCondition(ConditionContext context) throws OperationException
	{
		final Adaptation projectRecord = LocationProjectWorkflowUtilities
			.getLocationProjectRecord(context, context.getRepository());

		if (projectRecord == null)
		{
			return false;
		}

		Adaptation locationRecord = AdaptationUtil
			.followFK(projectRecord, LocationPaths._Root_LocationProject._Location);

		if (locationRecord == null)
		{
			return false;
		}

		AdaptationTable locationAddressTable = locationRecord.getContainer()
			.getTable(LocationPaths._Root_LocationData_Address.getPathInSchema());

		String locationId = locationRecord.get(LocationPaths._Root_Location._Id).toString();

		// Query the LocationAddress table for THIS Location of type "BUSINESS"
		RequestResult requestResult = locationAddressTable.createRequestResult(
			LocationPaths._Root_LocationData_Address._Location.format() + "='" + locationId
				+ "' and " + LocationPaths._Root_LocationData_Address._Type.format() + "='"
				+ LocationConstants.LOCATION_ADDRESS_TYPE_BUSINESS + "' and "
				+ LocationPaths._Root_LocationData_Address._IsRecordActive.format() + "=true");

		if (requestResult.isEmpty())
		{
			requestResult.close();
			return false;
		}

		Adaptation locationAddressRecord = requestResult.nextAdaptation();

		String stateOrProvincePredicate = null;

		if (HtzCommonUtilities.countryHasStatesOrProvinces(
			locationAddressRecord,
			LocationPaths._Root_LocationData_Address._Address_Country))
		{
			stateOrProvincePredicate = "' and "
				+ LocationPaths._Root_LocationData_Address._Address_StateProvince.format() + "='"
				+ locationAddressRecord
					.getString(LocationPaths._Root_LocationData_Address._Address_StateProvince);
		}

		// Search for any Location that has its BUSINESS Address the same City,State/Province, Country and Postal Code as this Location's BUSINESS address
		requestResult = locationAddressTable.createRequestResult(
			LocationPaths._Root_LocationData_Address._Location.format() + " !=' " + locationId
				+ "' and " + LocationPaths._Root_LocationData_Address._Type.format() + "='"
				+ LocationConstants.LOCATION_ADDRESS_TYPE_BUSINESS + "' and "
				+ LocationPaths._Root_LocationData_Address._Address_City.format() + "='"
				+ locationAddressRecord
					.getString(LocationPaths._Root_LocationData_Address._Address_City)
				+ stateOrProvincePredicate + "' and "
				+ LocationPaths._Root_LocationData_Address._Address_PostalCode.format() + "='"
				+ locationAddressRecord.getString(
					LocationPaths._Root_LocationData_Address._Address_PostalCode)
				+ "' and " + LocationPaths._Root_LocationData_Address._Address_Country.format()
				+ "='"
				+ locationAddressRecord
					.getString(LocationPaths._Root_LocationData_Address._Address_Country)
				+ "'and " + LocationPaths._Root_LocationData_Address._IsRecordActive.format()
				+ "=true");

		if (requestResult.isEmpty())
		{
			requestResult.close();
			return false;
		}

		requestResult.close();
		return true;
	}
}
