package com.hertz.mdm.location.accessrule;

import com.hertz.mdm.location.constants.LocationConstants;
import com.hertz.mdm.location.path.LocationPaths;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.ps.constants.CommonConstants;
import com.orchestranetworks.ps.workflow.WorkflowUtilities;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.service.AccessPermission;
import com.orchestranetworks.service.AccessRule;
import com.orchestranetworks.service.Role;
import com.orchestranetworks.service.Session;

public class HierarchyStructureDefinitionHierarchyNodeAccessRule implements AccessRule
{
	@Override
	public AccessPermission getPermission(Adaptation adaptation, Session session, SchemaNode node)
	{
		//		if (adaptation.isSchemaInstance() || adaptation.isHistory()
		//			|| isUserAlwaysReadWrite(session))
		//		{
		//			return AccessPermission.getReadWrite();
		//	}

		if (adaptation.isSchemaInstance())
		{
			return AccessPermission.getReadWrite();
		}

		Adaptation hierarchyStructureDefinitionRecord = adaptation;

		String hierarchyNodeType = hierarchyStructureDefinitionRecord.getString(
			LocationPaths._Root_HierarchyDefinitionData_HierarchyStructureDefinition._HierarchyNodeType);

		if (hierarchyNodeType == null)
		{
			return AccessPermission.getReadOnly();
		}
		else if (LocationConstants.HIERARCHY_NODE_TYPE_HIERARCHY.equals(hierarchyNodeType))
		{
			return AccessPermission.getHidden();
		}
		return AccessPermission.getReadWrite();
	}

	protected boolean isUserAlwaysReadWrite(Session session)
	{
		// This method will be called back into for the permissions user being applied
		// when used in conjunction with a WorkflowAccessRule, so for more efficiency, return R/W for those.
		return session.isUserInRole(Role.forSpecificRole(CommonConstants.ROLE_TECH_ADMIN))
			|| WorkflowUtilities.isPermissionsUser(session);
	}
}
