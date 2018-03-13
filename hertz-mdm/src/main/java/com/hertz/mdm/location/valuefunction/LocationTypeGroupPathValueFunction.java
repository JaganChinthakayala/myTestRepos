package com.hertz.mdm.location.valuefunction;

import com.hertz.mdm.location.path.LocationReferencePaths;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.schema.ValueFunction;
import com.orchestranetworks.schema.ValueFunctionContext;

public class LocationTypeGroupPathValueFunction implements ValueFunction
{
	@Override
	public Object getValue(Adaptation adaptation)
	{
		String locationTypeGroupPath = null;
		Adaptation locationTypeGroup = adaptation;
		Adaptation locationTypeGroupParent = locationTypeGroup;
		int nodeCount = 0;

		while (locationTypeGroupParent != null || nodeCount > 100)
		{
			locationTypeGroup = locationTypeGroupParent;
			Adaptation locationTypeGroupType = AdaptationUtil.followFK(
				locationTypeGroup,
				LocationReferencePaths._Root_LocationTypes_LocationTypeGroup._LocationTypeGroupType);
			if (locationTypeGroupPath == null)
			{
				locationTypeGroupPath = locationTypeGroupType.getString(
					LocationReferencePaths._Root_LocationTypes_LocationTypeGroupType._Base_Name);
			}
			else
			{
				locationTypeGroupPath = locationTypeGroupType.getString(
					LocationReferencePaths._Root_LocationTypes_LocationTypeGroupType._Base_Name)
					+ "|" + locationTypeGroupPath;
			}
			locationTypeGroupParent = AdaptationUtil.followFK(
				locationTypeGroup,
				LocationReferencePaths._Root_LocationTypes_LocationTypeGroup._LocationTypeGroupParent);
		}
		return locationTypeGroupPath;
	}

	@Override
	public void setup(ValueFunctionContext aContext)
	{
		// TODO Auto-generated method stub
	}
}
