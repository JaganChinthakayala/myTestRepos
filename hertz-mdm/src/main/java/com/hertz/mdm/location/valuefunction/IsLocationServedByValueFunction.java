package com.hertz.mdm.location.valuefunction;

import com.hertz.mdm.location.constants.LocationConstants;
import com.hertz.mdm.location.util.HtzLocationUtilities;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.ValueFunction;
import com.orchestranetworks.schema.ValueFunctionContext;

public class IsLocationServedByValueFunction implements ValueFunction
{
	@Override
	public Object getValue(Adaptation adaptation)
	{
		Adaptation locationRecord = adaptation;

		Adaptation locationLocationRelationshipRecord = HtzLocationUtilities
			.getLocationToLocationRelationshipRecordForType(
				locationRecord,
				LocationConstants.LOCATION_RELATIONSHIP_TYPE_SERVEDBY);

		if (locationLocationRelationshipRecord == null)
		{
			return false;
		}

		return true;
	}

	@Override
	public void setup(ValueFunctionContext aContext)
	{
		// TODO Auto-generated method stub
	}
}
