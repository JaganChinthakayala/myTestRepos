package com.hertz.mdm.location.valuefunction;

import com.hertz.mdm.location.enums.RentalTypes;
import com.hertz.mdm.location.path.LocationReferencePaths;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.ValueFunction;
import com.orchestranetworks.schema.ValueFunctionContext;

public class LocationTypeRentalTypeValueFunction implements ValueFunction
{
	@Override
	public Object getValue(Adaptation adaptation)
	{
		String locationTypeGroupPath = adaptation.getString(
			LocationReferencePaths._Root_LocationTypes_LocationTypeGroup._LocationTypeGroupPath);

		locationTypeGroupPath = locationTypeGroupPath + "|";

		if (locationTypeGroupPath.indexOf("|Airport Rental|") > 0)
		{
			return RentalTypes.AIRPORT;
		}

		if (locationTypeGroupPath.indexOf("|Non-Airport Rental|") > 0)
		{
			return RentalTypes.NON_AIRPORT;
		}

		return RentalTypes.NOT_APPLICABLE;
	}

	@Override
	public void setup(ValueFunctionContext aContext)
	{
		// TODO Auto-generated method stub
	}
}
