package com.hertz.mdm.location.valuefunction;

import com.hertz.mdm.location.path.LocationReferencePaths;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.ValueFunction;
import com.orchestranetworks.schema.ValueFunctionContext;

public class isLocationTypeGroupAirportOperationsValueFunction implements ValueFunction
{
	@Override
	public Object getValue(Adaptation adaptation)
	{
		Adaptation locationTypeGroup = adaptation;

		return locationTypeGroup
			.getString(
				LocationReferencePaths._Root_LocationTypes_LocationTypeGroup._LocationTypeGroupPath)
			.contains("|Airport Operations");
	}

	@Override
	public void setup(ValueFunctionContext aContext)
	{
		// TODO Auto-generated method stub
	}
}
