package com.hertz.mdm.location.valuefunction;

import com.hertz.mdm.location.constants.LocationConstants;
import com.hertz.mdm.location.path.LocationReferencePaths;
import com.hertz.mdm.location.util.HtzLocationUtilities;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.ValueFunction;
import com.orchestranetworks.schema.ValueFunctionContext;
import com.orchestranetworks.service.OperationException;

public class LocationMaximumAgeForRentalValueFunction implements ValueFunction
{
	@Override
	public Object getValue(Adaptation adaptation)
	{
		Adaptation countryMinimumMaximumAgeRecord = null;
		try
		{
			countryMinimumMaximumAgeRecord = HtzLocationUtilities
				.getCountryMinimumMaximumAgeRow(adaptation);
		}
		catch (OperationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (countryMinimumMaximumAgeRecord == null)
		{
			return LocationConstants.LOCATION_DEFAULT_MAXIMUM_RENTAL_AGE;
		}
		else
		{
			return countryMinimumMaximumAgeRecord
				.get(LocationReferencePaths._Root_CountryMinimumMaximumAge._MaximumAge)
				.toString();
		}
	}

	@Override
	public void setup(ValueFunctionContext aContext)
	{
		// TODO Auto-generated method stub
	}
}
