package com.hertz.mdm.location.valuefunction;

import com.hertz.mdm.location.path.LocationReferencePaths;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.schema.ValueFunction;
import com.orchestranetworks.schema.ValueFunctionContext;

public class NoMultiplesLocationTypeGroupValueFunction implements ValueFunction
{
	@Override
	public Object getValue(Adaptation adaptation)
	{
		Adaptation locationTypeGroupRecord = adaptation;
		Adaptation locationTypeGroupParentRecord = adaptation;

		do
		{
			locationTypeGroupParentRecord = AdaptationUtil.followFK(
				locationTypeGroupRecord,
				LocationReferencePaths._Root_LocationTypes_LocationTypeGroup._LocationTypeGroupParent);

			if (locationTypeGroupParentRecord == null)
			{
				return null;
			}
			locationTypeGroupRecord = locationTypeGroupParentRecord;
		}
		while (locationTypeGroupParentRecord.get_boolean(
			LocationReferencePaths._Root_LocationTypes_LocationTypeGroup.__areMultipleSelectionsAllowed));

		return locationTypeGroupParentRecord.get(
			LocationReferencePaths._Root_LocationTypes_LocationTypeGroup._LocationTypeGroupType);
	}

	@Override
	public void setup(ValueFunctionContext aContext)
	{
		// TODO Auto-generated method stub
	}
}
