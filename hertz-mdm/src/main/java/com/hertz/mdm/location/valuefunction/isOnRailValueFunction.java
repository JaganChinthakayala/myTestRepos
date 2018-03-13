package com.hertz.mdm.location.valuefunction;

import com.hertz.mdm.location.util.HtzLocationUtilities;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.ValueFunction;
import com.orchestranetworks.schema.ValueFunctionContext;

public class isOnRailValueFunction implements ValueFunction
{
	@Override
	public Object getValue(Adaptation adaptation)
	{
		boolean isOnRail = false;

		try
		{
			//isOnRail = HtzLocationUtilities.isLSubFunctionTypeAssociatedWithLocaton(
			//	adaptation,
			//	LocationConstants.LOCATION_SUBTYPE_AIRPORT);
			isOnRail = HtzLocationUtilities.isLocationOnRail(adaptation);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return isOnRail;
	}

	@Override
	public void setup(ValueFunctionContext aContext)
	{
		// TODO Auto-generated method stub
	}
}
