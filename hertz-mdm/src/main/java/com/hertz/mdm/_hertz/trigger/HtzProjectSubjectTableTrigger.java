/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm._hertz.trigger;

import com.hertz.mdm._hertz.constants.HtzConstants;
import com.orchestranetworks.ps.project.trigger.ProjectSubjectTableTrigger;
import com.orchestranetworks.ps.workflow.WorkflowUtilities;
import com.orchestranetworks.service.Session;

/**
 */
public abstract class HtzProjectSubjectTableTrigger extends ProjectSubjectTableTrigger
{
	@Override
	protected boolean checkIfNewSubjectProjectType(Session session)
	{
		boolean returnVal = super.checkIfNewSubjectProjectType(session);
		if (returnVal)
		{
			// Don't check if it's the super user because he can detach or delete subjects regardless
			String permissionsUser = WorkflowUtilities.getTrackingInfoPermissionsUser(session);
			if (HtzConstants.PERMISSIONS_USER_UPDATE_MASTER_DATA.equals(permissionsUser))
			{
				return false;
			}
		}
		return returnVal;
	}
}
