/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.usertask;

import java.util.HashMap;

import com.hertz.mdm.location.path.LocationPaths;
import com.hertz.mdm.location.util.HtzLocationUtilities;
import com.hertz.mdm.location.workflow.LocationProjectWorkflowUtilities;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.ps.procedure.ModifyValuesProcedure;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.workflow.UserTaskBeforeWorkItemCompletionContext;
import com.orchestranetworks.workflow.UserTaskWorkItemCompletionContext;
/**
 */
public class OpenLocationEnterAreaNumberUserTask extends OpenLocationProjectMaintenanceUserTask
{
	@Override
	public void checkBeforeWorkItemCompletion(UserTaskBeforeWorkItemCompletionContext context)
	{
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

			String areaId = locationRecord
				.getString(LocationPaths._Root_Location._LocationInformation_Area);

			if (areaId == null)
			{
				checkFieldRequired(
					context,
					locationRecord,
					LocationPaths._Root_Location._LocationInformation_Area,
					true,
					true);
			}

			Adaptation areaRecord = AdaptationUtil
				.followFK(locationRecord, LocationPaths._Root_Location._LocationInformation_Area);

			if (areaRecord == null)
			{
				return;
			}

			if (HtzLocationUtilities.isAreaAssignedToAnotherLocationInChildOrMasterDataSpace(
				areaRecord,
				locationRecord))
			{
				context.reportMessage(
					UserMessage.createError(
						"The new Area entered is alread assigned to another Location."));
			}
		}
		super.checkBeforeWorkItemCompletion(context);
	}

	@Override
	public void handleWorkItemCompletion(UserTaskWorkItemCompletionContext context)
		throws OperationException
	{
		if (context.getCompletedWorkItem().isAccepted())
		{
			Adaptation projectRecord = LocationProjectWorkflowUtilities
				.getLocationProjectRecord(context, context.getRepository());

			Adaptation locationRecord = AdaptationUtil
				.followFK(projectRecord, LocationPaths._Root_LocationProject._Location);

			Adaptation areaRecord = AdaptationUtil
				.followFK(locationRecord, LocationPaths._Root_Location._LocationInformation_Area);

			HashMap<Path, Object> pathValueMap = new HashMap<Path, Object>();

			ModifyValuesProcedure
				.execute(areaRecord, pathValueMap, context.getSession(), true, true);

			isAreaSet = true;

			super.handleWorkItemCompletion(context);
		}
	}

}