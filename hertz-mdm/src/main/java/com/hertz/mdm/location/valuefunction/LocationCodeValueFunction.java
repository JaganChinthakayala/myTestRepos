package com.hertz.mdm.location.valuefunction;

import com.hertz.mdm.location.path.LocationPaths;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.ValueFunction;
import com.orchestranetworks.schema.ValueFunctionContext;

public class LocationCodeValueFunction implements ValueFunction
{
	@Override
	public Object getValue(Adaptation adaptation)
	{
		Adaptation locationRecord = adaptation;

		String areaCode = locationRecord
			.getString(LocationPaths._Root_Location._LocationInformation_Area);
		String locationNumber = locationRecord
			.getString(LocationPaths._Root_Location._LocationNumber);

		if (areaCode == null || locationNumber == null)
		{
			return null;
		}

		return areaCode + locationNumber;
	}

	@Override
	public void setup(ValueFunctionContext aContext)
	{
		// TODO Auto-generated method stub
	}
}
