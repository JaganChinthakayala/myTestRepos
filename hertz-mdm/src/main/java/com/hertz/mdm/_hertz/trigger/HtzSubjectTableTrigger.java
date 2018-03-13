/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm._hertz.trigger;

import com.hertz.mdm._hertz.constants.HtzConstants;
import com.hertz.mdm.common.tablereffilter.HtzSubjectPathConfig;
import com.orchestranetworks.ps.project.trigger.SubjectTableTrigger;
import com.orchestranetworks.ps.workflow.WorkflowUtilities;
import com.orchestranetworks.schema.trigger.NewTransientOccurrenceContext;
import com.orchestranetworks.service.Session;

/**
 */
public abstract class HtzSubjectTableTrigger extends SubjectTableTrigger
{
	@Override
	public void handleNewContext(NewTransientOccurrenceContext context)
	{
		super.handleNewContext(context);
		HtzSubjectPathConfig subjectPathConfig = (HtzSubjectPathConfig) getSubjectPathConfig();
	}

	@Override
	protected boolean checkIfNewSubjectProjectType(Session session)
	{
		boolean returnVal = super.checkIfNewSubjectProjectType(session);
		if (returnVal)
		{
			// Don't check if it's the super user because he can add/delete subjects regardless
			String permissionsUser = WorkflowUtilities.getTrackingInfoPermissionsUser(session);
			if (HtzConstants.PERMISSIONS_USER_UPDATE_MASTER_DATA.equals(permissionsUser))
			{
				return false;
			}
		}
		return returnVal;
	}
}
