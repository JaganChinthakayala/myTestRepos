/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.usertask;

import com.hertz.mdm.location.path.LocationPaths;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.workflow.UserTaskBeforeWorkItemCompletionContext;
/**
 */
public class CloseLocationPreCloseChecklistUserTask extends CloseLocationProjectMaintenanceUserTask
{
	@Override
	public void checkBeforeWorkItemCompletion(UserTaskBeforeWorkItemCompletionContext context)
	{
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

			Adaptation locationRecord = AdaptationUtil
				.followFK(projectRecord, LocationPaths._Root_LocationProject._Location);

			if (locationRecord == null)
			{
				return;
			}

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._CloseLocation_Checklists_PreCloseChecklist_IsSignageRemoved,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._CloseLocation_Checklists_PreCloseChecklist_AreOpenRentalAgreementsHandled,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._CloseLocation_Checklists_PreCloseChecklist_HasInventoryOfHertzPropertyBeenHandled,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._CloseLocation_Checklists_PreCloseChecklist_HasAlarmSystemCancelRequestBeenMade,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._CloseLocation_Checklists_PreCloseChecklist_HasLeaseBeenTerminated,
				true,
				true);
		}
		super.checkBeforeWorkItemCompletion(context);
	}
}