package com.hertz.mdm.location.valuefunction;

import com.hertz.mdm.location.util.HtzLocationUtilities;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.ValueFunction;
import com.orchestranetworks.schema.ValueFunctionContext;

public class IsThisAreaAssignedToALocationValueFunction implements ValueFunction
{
	@Override
	public Object getValue(Adaptation adaptation)
	{
		Adaptation areaRecord = adaptation;

		return HtzLocationUtilities
			.isAreaAssignedToAnotherLocationInChildOrMasterDataSpace(areaRecord, null);
	}

	@Override
	public void setup(ValueFunctionContext aContext)
	{
		// TODO Auto-generated method stub
	}
}
