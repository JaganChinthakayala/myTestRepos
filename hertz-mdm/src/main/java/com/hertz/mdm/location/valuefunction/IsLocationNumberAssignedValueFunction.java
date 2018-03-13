package com.hertz.mdm.location.valuefunction;

import com.hertz.mdm.location.path.LocationPaths;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.schema.ValueFunction;
import com.orchestranetworks.schema.ValueFunctionContext;

public class IsLocationNumberAssignedValueFunction implements ValueFunction
{
	@Override
	public Object getValue(Adaptation adaptation)
	{
		Adaptation projectRecord = adaptation;

		if (projectRecord == null)
		{
			return false;
		}

		Adaptation locationRecord = AdaptationUtil
			.followFK(projectRecord, LocationPaths._Root_LocationProject._Location);

		if (locationRecord == null)
		{
			return false;
		}

		if (locationRecord.get(LocationPaths._Root_Location._LocationNumber) == null)
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
