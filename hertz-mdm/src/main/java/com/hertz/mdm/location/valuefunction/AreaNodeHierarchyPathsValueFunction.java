package com.hertz.mdm.location.valuefunction;

import java.util.ArrayList;

import com.hertz.mdm.location.path.LocationPaths;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.RequestResult;
import com.orchestranetworks.schema.ValueFunction;
import com.orchestranetworks.schema.ValueFunctionContext;

public class AreaNodeHierarchyPathsValueFunction implements ValueFunction
{
	@SuppressWarnings("unchecked")
	@Override
	public Object getValue(Adaptation adaptation)
	{
		Adaptation areaRecord = adaptation;
		Adaptation locationHierarchyRecord = adaptation;
		ArrayList<String> revenueAreaHierarchyPaths = new ArrayList<String>();
		ArrayList<String> locationHierarchyPaths = new ArrayList<String>();
		RequestResult requestResult = null;

		ArrayList<String> parentHierarchyPaths = (ArrayList<String>) areaRecord
			.get(LocationPaths._Root_Area._Parents);

		if (parentHierarchyPaths == null || parentHierarchyPaths.isEmpty())
		{
			revenueAreaHierarchyPaths
				.add(areaRecord.getString(LocationPaths._Root_Area._Base_Name));
			return revenueAreaHierarchyPaths;
		}

		AdaptationTable locationHierarchyTable = areaRecord.getContainer()
			.getTable(LocationPaths._Root_LocationHierarchy.getPathInSchema());

		//Iterate through all of the LocationHierarchy records that are this Revenue Area record parents
		for (String parentId : parentHierarchyPaths)
		{
			// Query the LocationHierarchy table for the Revenue Area parent
			requestResult = locationHierarchyTable.createRequestResult(
				LocationPaths._Root_LocationHierarchy._Id.format() + "='" + parentId + "' and "
					+ LocationPaths._Root_LocationHierarchy._IsRecordActive.format() + "=true");

			if (requestResult.isEmpty())
			{
				revenueAreaHierarchyPaths
					.add(areaRecord.getString(LocationPaths._Root_Area._Base_Name));
				return revenueAreaHierarchyPaths;
			}
			locationHierarchyRecord = requestResult.nextAdaptation();
			locationHierarchyPaths = (ArrayList<String>) locationHierarchyRecord
				.get(LocationPaths._Root_LocationHierarchy._HierarchyPaths);

			//Iterate through all of the LocationHierarchyPaths and add this Revenue Area node name to create the Revenue Area Hierarchy Path
			for (String locationHierarchyPath : locationHierarchyPaths)
			{
				revenueAreaHierarchyPaths.add(
					locationHierarchyPath + "|"
						+ areaRecord.getString(LocationPaths._Root_Area._Base_Name));
			}
		}

		if (requestResult != null)
		{
			requestResult.close();
		}
		return revenueAreaHierarchyPaths;
	}

	@Override
	public void setup(ValueFunctionContext aContext)
	{
		// TODO Auto-generated method stub
	}
}
