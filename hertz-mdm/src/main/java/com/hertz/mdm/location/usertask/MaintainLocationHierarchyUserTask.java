/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.usertask;

import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.ps.usertask.GeneralMaintenanceUserTask;
import com.orchestranetworks.workflow.UserTaskBeforeWorkItemCompletionContext;
/**
 */
public class MaintainLocationHierarchyUserTask extends GeneralMaintenanceUserTask
{
	static boolean isAreaSet = true;

	@Override
	public void checkBeforeWorkItemCompletion(UserTaskBeforeWorkItemCompletionContext context)
	{
		if (isCompletionCriteriaIgnored())
		{
			return;
		}

		if (context.isAcceptAction())
		{
			context.reportMessage(UserMessage.createError("Entered Maint Hier UT"));
		}
		super.checkBeforeWorkItemCompletion(context);
	}
}
