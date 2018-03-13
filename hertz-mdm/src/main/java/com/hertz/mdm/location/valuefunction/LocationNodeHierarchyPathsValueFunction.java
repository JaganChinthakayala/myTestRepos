package com.hertz.mdm.location.valuefunction;

import java.util.ArrayList;

import com.hertz.mdm.location.path.LocationPaths;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.schema.ValueFunction;
import com.orchestranetworks.schema.ValueFunctionContext;

public class LocationNodeHierarchyPathsValueFunction implements ValueFunction
{
	@SuppressWarnings("unchecked")
	@Override
	public Object getValue(Adaptation adaptation)
	{
		Adaptation locationRecord = adaptation;

		Adaptation areaRecord = AdaptationUtil
			.followFK(locationRecord, LocationPaths._Root_Location._LocationInformation_Area);

		ArrayList<String> locationPaths = new ArrayList<String>();

		if (areaRecord != null)
		{
			ArrayList<String> areaPaths = (ArrayList<String>) areaRecord
				.get(LocationPaths._Root_Area._HierarchyPaths);

			//Iterate through all of the Revenue Area HierarchyPaths and add this Location node name to create the Location Hierarchy Path
			for (String areaHierarchyPath : areaPaths)
			{
				locationPaths.add(
					areaHierarchyPath + "|"
						+ locationRecord.getString(LocationPaths._Root_Location._Name));
			}
		}
		return locationPaths;
	}

	@Override
	public void setup(ValueFunctionContext aContext)
	{
		// TODO Auto-generated method stub
	}
}
