package com.hertz.mdm.location.valuefunction;

import com.hertz.mdm.location.util.HtzLocationUtilities;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.ValueFunction;
import com.orchestranetworks.schema.ValueFunctionContext;

public class isAirportOperationsValueFunction implements ValueFunction
{
	@Override
	public Object getValue(Adaptation adaptation)
	{
		boolean isAirportOperations = false;

		try
		{
			isAirportOperations = HtzLocationUtilities.isLocationAirportOperations(adaptation);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return isAirportOperations;
	}

	@Override
	public void setup(ValueFunctionContext aContext)
	{
		// TODO Auto-generated method stub
	}
}
