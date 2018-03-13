/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.usertask;

import com.hertz.mdm.location.constants.LocationConstants;
import com.hertz.mdm.location.path.LocationPaths;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.RequestResult;
import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.workflow.UserTaskBeforeWorkItemCompletionContext;

/**
 */
public class CloseLocationHandleDependentChildrenUserTask
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

			String locationId = locationRecord.get(LocationPaths._Root_Location._Id).toString();

			AdaptationTable locationLocationRelationshipTable = locationRecord.getContainer()
				.getTable(
					LocationPaths._Root_LocationData_LocationLocationRelationship
						.getPathInSchema());

			// Query the Location/Location Relationship table for this Location with a Dependent Child Disposition
			RequestResult requestResult = locationLocationRelationshipTable.createRequestResult(
				LocationPaths._Root_LocationData_LocationLocationRelationship._ParentLocation
					.format() + "='" + locationId + "' and "
					+ LocationPaths._Root_LocationData_LocationLocationRelationship._IsRecordActive
						.format()
					+ "=true");

			for (int i = 0; i < requestResult.getSize(); i++)
			{
				Adaptation locationLocationRelationshopRecord = requestResult.nextAdaptation();

				if (locationLocationRelationshopRecord.get(
					LocationPaths._Root_LocationData_LocationLocationRelationship._DispositionOfChildWhenParentClosed) == null
					&& (LocationConstants.LOCATION_RELATIONSHIP_TYPE_SERVEDBY.equals(
						locationLocationRelationshopRecord.getString(
							LocationPaths._Root_LocationData_LocationLocationRelationship._LocationRelationshipType))
						|| LocationConstants.LOCATION_RELATIONSHIP_TYPE_SATELLITE.equals(
							locationLocationRelationshopRecord.getString(
								LocationPaths._Root_LocationData_LocationLocationRelationship._LocationRelationshipType))))
				{
					context.reportMessage(
						UserMessage
							.createError("All Dependent Child Locations must have a disposition."));
					break;
				}

			}
		}
		//To Suppress the typical exit criteria checks for a Close Location 
		locationHasDependentChildren = true;
		super.checkBeforeWorkItemCompletion(context);
		locationHasDependentChildren = false;
	}
}