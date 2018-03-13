package com.hertz.mdm.location.valuefunction;

import com.hertz.mdm.common.path.CommonReferencePaths;
import com.hertz.mdm.location.constants.LocationConstants;
import com.hertz.mdm.location.enums.ContactPirmarySecondary;
import com.hertz.mdm.location.path.LocationPaths;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.RequestResult;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.schema.ValueFunction;
import com.orchestranetworks.schema.ValueFunctionContext;

public class LocationAddressValueFunction implements ValueFunction
{
	@Override
	public Object getValue(Adaptation adaptation)
	{
		String address = null;

		Adaptation locationRecord = adaptation;

		String locationId = locationRecord.get(LocationPaths._Root_Location._Id).toString();

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

		if (requestResult.isEmpty())
		{
			return address;
		}

		Adaptation locationAddressRecord = requestResult.nextAdaptation();

		Adaptation country = (Adaptation) AdaptationUtil.followFK(
			locationAddressRecord,
			LocationPaths._Root_LocationData_Address._Address_Country);

		Adaptation stateProvince = (Adaptation) AdaptationUtil.followFK(
			locationAddressRecord,
			LocationPaths._Root_LocationData_Address._Address_StateProvince);

		address = locationAddressRecord
			.getString(LocationPaths._Root_LocationData_Address._Address_StreetNumber)
			+ " "
			+ locationAddressRecord
				.getString(LocationPaths._Root_LocationData_Address._Address_AddressLine1)
			+ " "
			+ locationAddressRecord
				.getString(LocationPaths._Root_LocationData_Address._Address_AddressLine2)
			+ " "
			+ locationAddressRecord
				.getString(LocationPaths._Root_LocationData_Address._Address_AddressLine3)
			+ " " + locationAddressRecord
				.getString(LocationPaths._Root_LocationData_Address._Address_City)
			+ ",";

		if (stateProvince != null)

		{
			address = address + " " + stateProvince.getString(
				CommonReferencePaths._Root_Subdivision_ISO31662._ISO31662_SubdivisionName);
		}

		address = address + "  " + locationAddressRecord
			.getString(LocationPaths._Root_LocationData_Address._Address_PostalCode);

		if (country != null)

		{
			address = address + "  "
				+ country.getString(CommonReferencePaths._Root_Country_ISO31661._ISO31661_alpha3);
		}

		address = address.replace("null ", "");
		address = address.replace("null,", "");

		return address;
	}

	@Override
	public void setup(ValueFunctionContext aContext)
	{
		// TODO Auto-generated method stub
	}
}
