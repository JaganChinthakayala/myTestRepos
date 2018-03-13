package com.hertz.mdm.location.valuefunction;

import java.util.ArrayList;

import com.hertz.mdm.location.path.LocationPaths;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.RequestResult;
import com.orchestranetworks.schema.ValueFunction;
import com.orchestranetworks.schema.ValueFunctionContext;

public class HierarchyNodeChildrenValueFunction implements ValueFunction
{
	@SuppressWarnings("unchecked")
	@Override
	public Object getValue(Adaptation adaptation)
	{
		Adaptation locationHiearchyRecord = adaptation;
		Adaptation childLocationHierarchy;
		Integer locationHierarchyRecordId = locationHiearchyRecord
			.get_int(LocationPaths._Root_LocationHierarchy._Id);
		String locationHierarchyRecordIdString = locationHierarchyRecordId.toString();
		ArrayList<String> parents = new ArrayList<String>();
		ArrayList<String> children = new ArrayList<String>();

		AdaptationTable locationHiearchyTable = locationHiearchyRecord.getContainer()
			.getTable(LocationPaths._Root_LocationHierarchy.getPathInSchema());

		// Query the LocationHierarchy table for all rows other than this one
		RequestResult requestResult = locationHiearchyTable.createRequestResult(
			"osd:is-not-null(" + LocationPaths._Root_LocationHierarchy._Parents.format() + ") and "
				+ LocationPaths._Root_LocationHierarchy._IsRecordActive.format() + "=true");

		if (requestResult.isEmpty())
		{
			requestResult.close();
			return children;
		}

		do
		{
			childLocationHierarchy = requestResult.nextAdaptation();
			if (childLocationHierarchy != null)
			{
				parents = (ArrayList<String>) childLocationHierarchy
					.get(LocationPaths._Root_LocationHierarchy._Parents);
				for (String parentNode : parents)
				{
					if (parentNode.equals(locationHierarchyRecordIdString))
					{
						Integer locationHierarchyChildRecordId = childLocationHierarchy
							.get_int(LocationPaths._Root_LocationHierarchy._Id);
						String locationHierarchyRecordChildIdString = locationHierarchyChildRecordId
							.toString();
						children.add(locationHierarchyRecordChildIdString);
					}
				}
			}
		}
		while (childLocationHierarchy != null);

		requestResult.close();
		return children;
	}

	@Override
	public void setup(ValueFunctionContext aContext)
	{
		// TODO Auto-generated method stub
	}
}
