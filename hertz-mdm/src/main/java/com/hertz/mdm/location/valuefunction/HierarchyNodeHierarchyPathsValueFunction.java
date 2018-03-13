package com.hertz.mdm.location.valuefunction;

import java.util.ArrayList;

import com.hertz.mdm.location.util.HtzLocationUtilities;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.ValueFunction;
import com.orchestranetworks.schema.ValueFunctionContext;

public class HierarchyNodeHierarchyPathsValueFunction implements ValueFunction
{
	@Override
	public Object getValue(Adaptation adaptation)
	{
		Adaptation locationHierarchyRecord = adaptation;
		String hierarchyPath = "";
		String delimiter = "";
		ArrayList<String> hierarchyPaths = new ArrayList<String>();
		ArrayList<String> flattenedHierarchyPaths = new ArrayList<String>();

		hierarchyPaths = HtzLocationUtilities
			.determineHierarchyPaths(locationHierarchyRecord, hierarchyPaths, false);

		for (String pathNode : hierarchyPaths)
		{
			if (pathNode == "|")
			{
				if (hierarchyPath != "")
				{
					flattenedHierarchyPaths.add(hierarchyPath);
					hierarchyPath = "";
					delimiter = "";
				}
			}
			else
			{
				hierarchyPath = hierarchyPath + delimiter + pathNode;
				delimiter = "|";
			}
		}
		flattenedHierarchyPaths.add(hierarchyPath);
		return flattenedHierarchyPaths;
	}

	@Override
	public void setup(ValueFunctionContext aContext)
	{
		// TODO Auto-generated method stub
	}
}
