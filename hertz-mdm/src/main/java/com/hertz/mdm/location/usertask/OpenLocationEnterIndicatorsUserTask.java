/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.usertask;

import com.hertz.mdm.location.path.LocationPaths;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.RequestResult;
import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.workflow.UserTaskBeforeWorkItemCompletionContext;
/**
 */
public class OpenLocationEnterIndicatorsUserTask extends OpenLocationProjectMaintenanceUserTask
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

			String locationId = locationRecord.get(LocationPaths._Root_Location._Id).toString();

			// At least on Indicator must be selected for the Location 
			AdaptationTable locationIndicatorTable = locationRecord.getContainer().getTable(
				LocationPaths._Root_LocationData_LocationIndicatorRelationship.getPathInSchema());

			// Query the Location/Indicator Relationship for any Indicator assigned to this Location
			RequestResult requestResult = locationIndicatorTable.createRequestResult(
				LocationPaths._Root_LocationData_LocationIndicatorRelationship._Location.format()
					+ "='" + locationId + "'");

			if (requestResult.isEmpty())
			{
				context.reportMessage(
					UserMessage
						.createError("At least one Indicator needs to selected for the Locaton."));
			}
			if (requestResult != null)
			{
				requestResult.close();
			}
		}
		super.checkBeforeWorkItemCompletion(context);
	}
}