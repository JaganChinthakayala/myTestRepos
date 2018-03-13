package com.hertz.mdm.location.valuefunction;

import com.hertz.mdm.location.path.LocationPaths;
import com.hertz.mdm.location.util.HtzLocationUtilities;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.ValueFunction;
import com.orchestranetworks.schema.ValueFunctionContext;

public class LocationCountryValueFunction implements ValueFunction
{
	@Override
	public Object getValue(Adaptation adaptation)
	{
		Adaptation locationRecord = adaptation;

		Adaptation locationPrimaryBusinessAddress = HtzLocationUtilities
			.locationPrimaryBusinessAddress(locationRecord);
		/*
				AdaptationTable locationAddressTable = locationRecord.getContainer()
					.getTable(LocationPaths._Root_LocationData_Address.getPathInSchema());
		
				String locationId = locationRecord.get(LocationPaths._Root_Location._Id).toString();
		
				// Query the LocatinAddress table for a row of type "BUSINESS"
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
		
				Adaptation countryRecord = AdaptationUtil.followFK(
					locationAddressRecord,
					LocationPaths._Root_LocationData_Address._Address_Country);
					
					*/

		if (locationPrimaryBusinessAddress == null)
		{
			return null;
		}

		return locationPrimaryBusinessAddress
			.get(LocationPaths._Root_LocationData_Address._Address_Country).toString();
	}

	@Override
	public void setup(ValueFunctionContext aContext)
	{
		// TODO Auto-generated method stub
	}
}
