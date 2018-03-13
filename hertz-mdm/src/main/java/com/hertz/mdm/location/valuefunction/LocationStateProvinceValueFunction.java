package com.hertz.mdm.location.valuefunction;

import com.hertz.mdm.common.path.CommonReferencePaths;
import com.hertz.mdm.location.constants.LocationConstants;
import com.hertz.mdm.location.path.LocationPaths;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.RequestResult;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.schema.ValueFunction;
import com.orchestranetworks.schema.ValueFunctionContext;

public class LocationStateProvinceValueFunction implements ValueFunction
{
	@Override
	public Object getValue(Adaptation adaptation)
	{

		Adaptation locationRecord = adaptation;

		AdaptationTable locationAddressTable = locationRecord.getContainer()
			.getTable(LocationPaths._Root_LocationData_Address.getPathInSchema());

		String locationId = locationRecord.get(LocationPaths._Root_Location._Id).toString();

		// Query the LocationAddress table for a row of type "BUSINESS"
		RequestResult requestResult = locationAddressTable.createRequestResult(
			LocationPaths._Root_LocationData_Address._Location.format() + "='" + locationId
				+ "' and " + LocationPaths._Root_LocationData_Address._Type.format() + "='"
				+ LocationConstants.LOCATION_ADDRESS_TYPE_BUSINESS + "' and "
				+ LocationPaths._Root_LocationData_Address._IsRecordActive.format() + "=true");

		if (requestResult.isEmpty())
		{
			requestResult.close();
			return null;
		}

		Adaptation locationAddressRecord = requestResult.nextAdaptation();

		requestResult.close();

		Adaptation stateProvinceRecord = AdaptationUtil.followFK(
			locationAddressRecord,
			LocationPaths._Root_LocationData_Address._Address_StateProvince);

		if (stateProvinceRecord == null)
		{
			return null;
		}

		return stateProvinceRecord
			.getString(CommonReferencePaths._Root_Subdivision_ISO31662._ISO31662_Subdivision);
	}

	@Override
	public void setup(ValueFunctionContext aContext)
	{
		// TODO Auto-generated method stub
	}
}
