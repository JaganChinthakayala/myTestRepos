package com.hertz.mdm.location.valuefunction;

import com.hertz.mdm.location.util.HtzLocationUtilities;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.ValueFunction;
import com.orchestranetworks.schema.ValueFunctionContext;

public class isOnAirportValueFunction implements ValueFunction
{
	@Override
	public Object getValue(Adaptation adaptation)
	{
		boolean isOnAirport = false;

		try
		{
			//	isOnAirport = HtzLocationUtilities.isLSubFunctionTypeAssociatedWithLocaton(
			//		adaptation,
			//		LocationConstants.LOCATION_SUBTYPE_AIRPORT);
			isOnAirport = HtzLocationUtilities.isLocationOnAirport(adaptation);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return isOnAirport;
	}

	@Override
	public void setup(ValueFunctionContext aContext)
	{
		// TODO Auto-generated method stub
	}
}
