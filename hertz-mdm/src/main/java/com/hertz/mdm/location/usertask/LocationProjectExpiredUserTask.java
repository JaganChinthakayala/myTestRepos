/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.usertask;

import java.util.Date;

import com.hertz.mdm.location.path.LocationPaths;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.workflow.UserTaskBeforeWorkItemCompletionContext;
/**
 */
public class LocationProjectExpiredUserTask extends LocationProjectMaintenanceUserTask
{
	@Override
	public void checkBeforeWorkItemCompletion(UserTaskBeforeWorkItemCompletionContext context)
	{
		String message = "A future dated Expiration Date must be specified";
		if (isCompletionCriteriaIgnored())
		{
			return;
		}

		if (context.isAcceptAction())
		{
			Adaptation projectRecord = getProjectRecordFromUserTaskBeforeWorkItemCompletionContext(
				context);
			if (projectRecord == null)
			{
				return;
			}

			Date expirationDate = (Date) projectRecord
				.getDate(LocationPaths._Root_LocationProject._ProjectExpirationDate);

			if (expirationDate == null)
			{
				context.reportMessage(UserMessage.createError(message));

			}

			if (expirationDate.before(new Date()))
			{
				context.reportMessage(UserMessage.createError(message));
			}

			return;
		}
		super.checkBeforeWorkItemCompletion(context);
	}
}