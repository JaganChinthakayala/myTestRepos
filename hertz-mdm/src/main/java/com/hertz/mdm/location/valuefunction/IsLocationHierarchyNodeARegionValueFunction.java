package com.hertz.mdm.location.valuefunction;

import com.hertz.mdm.location.constants.LocationConstants;
import com.hertz.mdm.location.path.LocationPaths;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.RequestResult;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.ValueFunction;
import com.orchestranetworks.schema.ValueFunctionContext;
import com.orchestranetworks.service.directory.DirectoryPaths;

public class IsLocationHierarchyNodeARegionValueFunction implements ValueFunction
{
	public static final Path DIRECTORY_ROLES_NAME_PATH = DirectoryPaths._Directory_Roles._Name;

	@Override
	public Object getValue(Adaptation adaptation)
	{
		Adaptation locationHierarchy = adaptation;

		AdaptationTable locationHierarchyBusinessPartyRelationshipTable = locationHierarchy
			.getContainer().getTable(
				LocationPaths._Root_HierarchyDefinitionData_LocationHierarchyBusinessPartyRelationship
					.getPathInSchema());

		String locationHieararchyId = locationHierarchy
			.get(LocationPaths._Root_LocationHierarchy._Id).toString();

		// Query the Location Hierarchy Business Party Relationship Table for a Regional VP Role
		RequestResult requestResult = locationHierarchyBusinessPartyRelationshipTable
			.createRequestResult(
				LocationPaths._Root_HierarchyDefinitionData_LocationHierarchyBusinessPartyRelationship._LocationHierarchy
					.format() + "='" + locationHieararchyId
					+ "' and"
					+ LocationPaths._Root_HierarchyDefinitionData_LocationHierarchyBusinessPartyRelationship._ContactType
						.format()
					+ "='" + LocationConstants.CONTACT_TYPE_REGIONAL_VICE_PRESIDENT + "' and"
					+ LocationPaths._Root_HierarchyDefinitionData_LocationHierarchyBusinessPartyRelationship._IsRecordActive
						.format()
					+ "=true");

		if (requestResult.isEmpty())
		{
			return false;
		}

		return true;

		/*
		return (com.hertz.mdm.location.util.HtzLocationUtilities.isRoleInLocationHierarchy(
			locationHierarchy,
			com.hertz.mdm.location.constants.LocationConstants.ROLE_REGIONAL_VICE_PRESIDENT)
			|| com.hertz.mdm.location.util.HtzLocationUtilities.isRoleInLocationHierarchy(
				locationHierarchy,
				com.hertz.mdm.location.constants.LocationConstants.ROLE_REGIONAL_FINANCE));
		*/
	}

	@Override
	public void setup(ValueFunctionContext aContext)
	{
		// TODO Auto-generated method stub
	}
}
