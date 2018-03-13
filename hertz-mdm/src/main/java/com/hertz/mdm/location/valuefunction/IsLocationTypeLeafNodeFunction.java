package com.hertz.mdm.location.valuefunction;

import com.hertz.mdm.location.path.LocationReferencePaths;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.RequestResult;
import com.orchestranetworks.schema.ValueFunction;
import com.orchestranetworks.schema.ValueFunctionContext;

public class IsLocationTypeLeafNodeFunction implements ValueFunction
{
	@Override
	public Object getValue(Adaptation adaptation)
	{
		Adaptation locationTypeGroup = adaptation;

		String locationTypeGroupId = locationTypeGroup
			.get(
				LocationReferencePaths._Root_LocationTypes_LocationTypeGroup._LocationTypeGroupType)
			.toString();

		AdaptationTable locationTypeGroupTable = locationTypeGroup.getContainer().getTable(
			LocationReferencePaths._Root_LocationTypes_LocationTypeGroup.getPathInSchema());

		// Query the LocationHierarchy table for the Revenue Area parent
		RequestResult requestResult = locationTypeGroupTable.createRequestResult(
			LocationReferencePaths._Root_LocationTypes_LocationTypeGroup._LocationTypeGroupParent
				.format() + "='" + locationTypeGroupId + "' and "
				+ LocationReferencePaths._Root_LocationTypes_LocationTypeGroup._IsRecordActive
					.format()
				+ "=true");

		if (requestResult.isEmpty())
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public void setup(ValueFunctionContext aContext)
	{
		// TODO Auto-generated method stub
	}
}
