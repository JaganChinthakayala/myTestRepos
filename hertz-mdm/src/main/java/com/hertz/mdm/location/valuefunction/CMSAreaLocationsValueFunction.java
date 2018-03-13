package com.hertz.mdm.location.valuefunction;

import com.hertz.mdm.location.constants.LocationConstants;
import com.hertz.mdm.location.util.HtzLocationUtilities;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.ValueFunction;
import com.orchestranetworks.schema.ValueFunctionContext;

public class CMSAreaLocationsValueFunction implements ValueFunction
{
	@Override
	public Object getValue(Adaptation adaptation)
	{
		return HtzLocationUtilities
			.areaLocations(adaptation, LocationConstants.OPERATION_AREA_TYPE_CMS);
	}

	@Override
	public void setup(ValueFunctionContext aContext)
	{
		// TODO Auto-generated method stub
	}
}
