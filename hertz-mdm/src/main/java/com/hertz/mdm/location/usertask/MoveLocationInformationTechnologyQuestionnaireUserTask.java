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
public class MoveLocationInformationTechnologyQuestionnaireUserTask
	extends
	MoveLocationProjectMaintenanceUserTask
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
			/*
						checkFieldRequired(
							context,
							projectRecord,
							LocationPaths._Root_LocationProject._CloseLocation_IT_HasEquipmentToBeReturned,
							true,
							true);
			
						checkFieldRequired(
							context,
							projectRecord,
							LocationPaths._Root_LocationProject._CloseLocation_IT_DataLineAction,
							true,
							true);
			
						checkFieldRequired(
							context,
							projectRecord,
							LocationPaths._Root_LocationProject._CloseLocation_IT_HasDataLineToBeTerminated,
							true,
							true);
			
						checkFieldRequired(
							context,
							projectRecord,
							LocationPaths._Root_LocationProject._CloseLocation_IT_HasPhoneLinesToBeDisconnected,
							true,
							true);
			
						checkFieldRequired(
							context,
							projectRecord,
							LocationPaths._Root_LocationProject._CloseLocation_IT_PhoneLineAction,
							true,
							true);
			
						checkFieldRequired(
							context,
							projectRecord,
							LocationPaths._Root_LocationProject._CloseLocation_IT_HasPrintersCopiersToBePickedUp,
							true,
							true);
			
						checkFieldRequired(
							context,
							projectRecord,
							LocationPaths._Root_LocationProject._CloseLocation_IT_RelocationOfEquipment,
							true,
							true);
							*/
		}
		super.checkBeforeWorkItemCompletion(context);
	}
}