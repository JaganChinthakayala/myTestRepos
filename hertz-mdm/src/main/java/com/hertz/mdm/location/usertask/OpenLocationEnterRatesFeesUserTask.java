/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.usertask;

import com.hertz.mdm.location.path.LocationPaths;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.workflow.UserTaskBeforeWorkItemCompletionContext;
/**
 */
public class OpenLocationEnterRatesFeesUserTask extends OpenLocationProjectMaintenanceUserTask
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

			if (projectRecord.get(LocationPaths._Root_LocationProject._Rate_DefaultTemplate) == null
				&& projectRecord.get(LocationPaths._Root_LocationProject._Rate_RatesFrom) == null)
			{
				context.reportMessage(
					UserMessage.createError(
						"Either '"
							+ getFieldLabel(
								projectRecord,
								LocationPaths._Root_LocationProject._Rate_DefaultTemplate,
								context.getSession(),
								true)
							+ "' or '"
							+ getFieldLabel(
								projectRecord,
								LocationPaths._Root_LocationProject._Rate_RatesFrom,
								context.getSession(),
								true)
							+ "' must be entered."));
			}
			else if (projectRecord
				.get(LocationPaths._Root_LocationProject._Rate_DefaultTemplate) != null
				&& projectRecord.get(LocationPaths._Root_LocationProject._Rate_RatesFrom) != null)
			{
				context.reportMessage(
					UserMessage.createError(
						"Either '"
							+ getFieldLabel(
								projectRecord,
								LocationPaths._Root_LocationProject._Rate_DefaultTemplate,
								context.getSession(),
								true)
							+ "' or '"
							+ getFieldLabel(
								projectRecord,
								LocationPaths._Root_LocationProject._Rate_RatesFrom,
								context.getSession(),
								true)
							+ "' can be entered."));
			}

			checkFieldRequired(
				context,
				locationRecord,
				LocationPaths._Root_Location._LocationInformation_FleetArea,
				true,
				true);

		}
		super.checkBeforeWorkItemCompletion(context);
	}
}