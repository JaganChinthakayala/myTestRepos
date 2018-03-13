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
public class OpenLocationEnterLocationNumberUserTask extends OpenLocationProjectMaintenanceUserTask
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
				locationRecord,
				LocationPaths._Root_Location._LocationNumber,
				true,
				true);

			checkFieldRequired(
				context,
				locationRecord,
				LocationPaths._Root_Location._LocationInformation_LocationCity,
				true,
				true);

			checkFieldRequired(
				context,
				locationRecord,
				LocationPaths._Root_Location._LocationInformation_ReservationArea,
				true,
				true);

			checkFieldRequired(
				context,
				locationRecord,
				LocationPaths._Root_Location._AlphaCode,
				true,
				true);
		}
		super.checkBeforeWorkItemCompletion(context);
	}
}