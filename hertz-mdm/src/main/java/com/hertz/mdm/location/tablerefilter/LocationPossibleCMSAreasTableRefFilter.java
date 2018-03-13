package com.hertz.mdm.location.tablerefilter;

import java.util.ArrayList;
import java.util.Locale;

import com.hertz.mdm.location.constants.LocationConstants;
import com.hertz.mdm.location.path.LocationPaths;
import com.hertz.mdm.location.util.HtzLocationUtilities;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.schema.InvalidSchemaException;
import com.orchestranetworks.schema.TableRefFilter;
import com.orchestranetworks.schema.TableRefFilterContext;

public class LocationPossibleCMSAreasTableRefFilter implements TableRefFilter
{
	@Override
	public boolean accept(Adaptation record, ValueContext context)
	{
		String locationPath = null;
		boolean isHierarchyValidForThisAreaType = false;

		Adaptation areaRecord = record;

		@SuppressWarnings("unchecked")
		ArrayList<String> areaHierarchyPaths = (ArrayList<String>) areaRecord
			.get(LocationPaths._Root_Area._HierarchyPaths);

		for (int i = 0; i < areaHierarchyPaths.size(); i++)
		{
			locationPath = areaHierarchyPaths.get(i);

			isHierarchyValidForThisAreaType = HtzLocationUtilities.isHierarchyValidForThisAreaType(
				locationPath,
				LocationConstants.OPERATION_AREA_TYPE_CMS);

			if (isHierarchyValidForThisAreaType)
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public void setup(TableRefFilterContext arg0)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public String toUserDocumentation(Locale arg0, ValueContext arg1) throws InvalidSchemaException
	{
		// TODO Auto-generated method stub
		return null;
	}
}
