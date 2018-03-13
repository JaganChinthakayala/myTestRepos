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
public class OpenLocationEnterCMSAndYieldDataUserTask extends OpenLocationProjectMaintenanceUserTask
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
				LocationPaths._Root_Location._ProductsAndServices_ContributionManagementSystem_CmsLocationGroup,
				true,
				true);

			if (locationRecord.get(
				LocationPaths._Root_Location._ProductsAndServices_ContributionManagementSystem_CmsPool) == null)
			{
				checkFieldRequired(
					context,
					locationRecord,
					LocationPaths._Root_Location._ProductsAndServices_ContributionManagementSystem_CmsPool,
					true,
					true);
			}
			else
			{
				if (locationRecord
					.get(
						LocationPaths._Root_Location._ProductsAndServices_ContributionManagementSystem_CmsPool)
					.toString()
					.length() != 2)
				{
					context.reportMessage(
						UserMessage.createError(
							"'" + getFieldLabel(
								locationRecord,
								LocationPaths._Root_Location._ProductsAndServices_ContributionManagementSystem_CmsPool,
								context.getSession(),
								true) + "' must be a two digit integer."));
				}
			}

			if (locationRecord.get(
				LocationPaths._Root_Location._ProductsAndServices_ContributionManagementSystem_CmsPool) == null)
			{
				checkFieldRequired(
					context,
					locationRecord,
					LocationPaths._Root_Location._ProductsAndServices_Yield_FleetFrom,
					true,
					true);
			}

			if (locationRecord
				.get(LocationPaths._Root_Location._ProductsAndServices_Yield_VawPool) == null)
			{
				checkFieldRequired(
					context,
					locationRecord,
					LocationPaths._Root_Location._ProductsAndServices_Yield_VawPool,
					true,
					true);
			}
			else
			{
				if (locationRecord
					.get(LocationPaths._Root_Location._ProductsAndServices_Yield_VawPool)
					.toString()
					.length() != 2)
				{
					context.reportMessage(
						UserMessage.createError(
							"'" + getFieldLabel(
								locationRecord,
								LocationPaths._Root_Location._ProductsAndServices_Yield_VawPool,
								context.getSession(),
								true) + "' must be a two digit integer."));
				}
			}
		}
		super.checkBeforeWorkItemCompletion(context);
	}
}