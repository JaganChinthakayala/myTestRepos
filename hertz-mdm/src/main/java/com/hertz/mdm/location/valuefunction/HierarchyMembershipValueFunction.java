package com.hertz.mdm.location.valuefunction;

import java.util.ArrayList;

import com.hertz.mdm.location.constants.LocationConstants;
import com.hertz.mdm.location.path.LocationPaths;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.schema.ValueFunction;
import com.orchestranetworks.schema.ValueFunctionContext;

public class HierarchyMembershipValueFunction implements ValueFunction
{
	@Override
	public Object getValue(Adaptation adaptation)
	{
		Adaptation hierarchyStructureDefinitionRecord = adaptation;

		//If this is a "Hierarchy" Node, return this node's name
		if (LocationConstants.HIERARCHY_NODE_TYPE_HIERARCHY.equals(
			hierarchyStructureDefinitionRecord.getString(
				LocationPaths._Root_HierarchyDefinitionData_HierarchyStructureDefinition._HierarchyNodeType)))
		{
			return hierarchyStructureDefinitionRecord.getString(
				LocationPaths._Root_HierarchyDefinitionData_HierarchyStructureDefinition._Name);
		}

		//Create an ArrayList of this node's parents
		ArrayList<Adaptation> parents = (ArrayList<Adaptation>) AdaptationUtil.getLinkedRecordList(
			hierarchyStructureDefinitionRecord,
			LocationPaths._Root_HierarchyDefinitionData_HierarchyStructureDefinition._Parents);

		String hierarchyMembership = "";
		String member = "";
		String delimiter = "";

		//Iterate through each Parent node and get the Parent node Hierarchy Membership and concatenate it the hierarchyMembersip string
		for (Adaptation parent : parents)
		{
			member = parent.getString(
				LocationPaths._Root_HierarchyDefinitionData_HierarchyStructureDefinition._HierarchyMembership);
			if (member != null)
			{
				if (!hierarchyMembership.contains(member))
				{
					hierarchyMembership = hierarchyMembership + delimiter + member;
					delimiter = ", ";
				}
			}
		}
		return hierarchyMembership;
	}

	@Override
	public void setup(ValueFunctionContext aContext)
	{
		// TODO Auto-generated method stub
	}
}
