package com.hertz.mdm.location.valuefunction;

import com.hertz.mdm.location.path.LocationPaths;
import com.hertz.mdm.location.path.LocationReferencePaths;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.schema.ValueFunction;
import com.orchestranetworks.schema.ValueFunctionContext;

public class IsIStaffedByHertzPersonnelValueFunction implements ValueFunction
{
	@Override
	public Object getValue(Adaptation adaptation)
	{
		Adaptation locationRecord = adaptation;

		if (locationRecord.get_boolean(LocationPaths._Root_Location._IsServedBy))
		{
			return false;
		}

		Adaptation operatorOwnershipTypeRecord = AdaptationUtil
			.followFK(locationRecord, LocationPaths._Root_Location._OperationsOwnershipType);

		if (operatorOwnershipTypeRecord == null)
		{
			return false;
		}

		return operatorOwnershipTypeRecord.get_boolean(
			LocationReferencePaths._Root_OperationsOwnershipType._IsStaffedByHertzPersonnel);
	}

	@Override
	public void setup(ValueFunctionContext aContext)
	{
		// TODO Auto-generated method stub
	}
}
