package com.hertz.mdm.location.valuefunction;

import com.hertz.mdm.location.path.LocationPaths;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.ValueFunction;
import com.orchestranetworks.schema.ValueFunctionContext;

public class LocationTypeValueFunction implements ValueFunction
{
	@Override
	public Object getValue(Adaptation adaptation)
	{
		String locationTypeGroupPath = adaptation.getString(
			LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship._LocationTypeGroup);

		int delimiterIndex = locationTypeGroupPath.lastIndexOf('|');

		locationTypeGroupPath = locationTypeGroupPath.substring(delimiterIndex + 1);

		return locationTypeGroupPath;
	}

	@Override
	public void setup(ValueFunctionContext aContext)
	{
		// TODO Auto-generated method stub
	}
}