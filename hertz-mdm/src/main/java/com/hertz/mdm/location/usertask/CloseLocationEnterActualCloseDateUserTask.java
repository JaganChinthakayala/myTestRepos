/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.usertask;

import java.util.Date;
import java.util.HashMap;

import com.hertz.mdm.location.enums.LocationStatuses;
import com.hertz.mdm.location.path.LocationPaths;
import com.hertz.mdm.location.workflow.LocationProjectWorkflowUtilities;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.RequestResult;
import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.ps.procedure.ModifyValuesProcedure;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.workflow.UserTaskBeforeWorkItemCompletionContext;
import com.orchestranetworks.workflow.UserTaskWorkItemCompletionContext;
/**
 */
public class CloseLocationEnterActualCloseDateUserTask
	extends
	CloseLocationProjectMaintenanceUserTask
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

			Date actualCloseDate = projectRecord
				.getDate(LocationPaths._Root_LocationProject._CloseLocation_ActualCloseDate);
			Date projectedCloseDate = projectRecord
				.getDate(LocationPaths._Root_LocationProject._CloseLocation_ProjectedCloseDate);
			Date today = new Date();

			if (today.before(projectedCloseDate))
			{
				context.reportMessage(
					UserMessage.createError(
						"Cannot Close the Location Before the Projected Close Date: "
							+ projectedCloseDate.toString()));
			}
			else if (actualCloseDate == null)
			{
				checkFieldRequired(
					context,
					projectRecord,
					LocationPaths._Root_LocationProject._CloseLocation_ActualCloseDate,
					true,
					true);
			}

			else if (projectedCloseDate.after(actualCloseDate))
			{
				context.reportMessage(
					UserMessage.createError(
						"The Actual Close Date cannot be earlier than the Projected Close Date"));
			}
		}

		super.checkBeforeWorkItemCompletion(context);
	}

	@Override
	public void handleWorkItemCompletion(UserTaskWorkItemCompletionContext context)
		throws OperationException
	{
		super.handleWorkItemCompletion(context);

		Adaptation projectRecord = LocationProjectWorkflowUtilities
			.getLocationProjectRecord(context, context.getRepository());

		if (projectRecord == null)
		{
			return;
		}

		Adaptation locationRecord = AdaptationUtil
			.followFK(projectRecord, LocationPaths._Root_LocationProject._Location);

		String locationId = locationRecord.get(LocationPaths._Root_Location._Id).toString();

		if (context.getCompletedWorkItem().isAccepted())
		{
			HashMap<Path, Object> pathValueMap = new HashMap<Path, Object>();
			pathValueMap.put(LocationPaths._Root_Location._OpenCloseDate_EndDate, new Date());
			pathValueMap.put(LocationPaths._Root_Location._Status, LocationStatuses.CLOSED);
			pathValueMap.put(LocationPaths._Root_Location._CurrentProjectType, null);
			ModifyValuesProcedure
				.execute(locationRecord, pathValueMap, context.getSession(), true, false);

			//Expire all the Relationships that this Location has with other Locations
			AdaptationTable locationLocatonRelationshipTable = locationRecord.getContainer()
				.getTable(
					LocationPaths._Root_LocationData_LocationLocationRelationship
						.getPathInSchema());

			RequestResult requestResult = locationLocatonRelationshipTable.createRequestResult(
				LocationPaths._Root_LocationData_LocationLocationRelationship._SubLocation.format()
					+ "='" + locationId + "' and "
					+ LocationPaths._Root_LocationData_LocationLocationRelationship._IsRecordActive
						.format()
					+ "=true");

			for (int i = 0; i < requestResult.getSize(); i++)
			{
				Adaptation locationLocationRecord = requestResult.nextAdaptation();

				pathValueMap.put(LocationPaths._Root_Location._OpenCloseDate_EndDate, new Date());

				ModifyValuesProcedure.execute(
					locationLocationRecord,
					pathValueMap,
					context.getSession(),
					true,
					false);
			}

			requestResult = locationLocatonRelationshipTable.createRequestResult(
				LocationPaths._Root_LocationData_LocationLocationRelationship._ParentLocation
					.format() + "='" + locationId + " and "
					+ LocationPaths._Root_LocationData_LocationLocationRelationship._IsRecordActive
						.format()
					+ "=true");

			for (int i = 0; i < requestResult.getSize(); i++)
			{
				Adaptation locationLocationRecord = requestResult.nextAdaptation();

				pathValueMap.put(LocationPaths._Root_Location._OpenCloseDate_EndDate, new Date());

				ModifyValuesProcedure.execute(
					locationLocationRecord,
					pathValueMap,
					context.getSession(),
					true,
					false);
			}
		}
	}
}