/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.usertask;

import java.util.Date;
import java.util.HashMap;

import com.hertz.mdm.location.path.LocationPaths;
import com.hertz.mdm.location.workflow.LocationProjectWorkflowUtilities;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.ps.procedure.ModifyValuesProcedure;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.workflow.UserTaskBeforeWorkItemCompletionContext;
import com.orchestranetworks.workflow.UserTaskWorkItemCompletionContext;
/**
 */
public class OpenLocationOpenTheLocationUserTask extends OpenLocationProjectMaintenanceUserTask
{
	@Override
	public void checkBeforeWorkItemCompletion(UserTaskBeforeWorkItemCompletionContext context)
	{
		super.checkBeforeWorkItemCompletion(context);
	}

	@Override
	public void handleWorkItemCompletion(UserTaskWorkItemCompletionContext context)
		throws OperationException
	{
		Adaptation projectRecord = LocationProjectWorkflowUtilities
			.getLocationProjectRecord(context, context.getRepository());

		if (projectRecord == null)
		{
			return;
		}

		//Set the Status and End Dates on the Project record
		HashMap<Path, Object> pathValueMapProject = new HashMap<Path, Object>();

		pathValueMapProject
			.put(LocationPaths._Root_LocationProject._ProjectStartEndDates_EndDate, new Date());

		pathValueMapProject
			.put(LocationPaths._Root_LocationProject._EffectiveStartEndDates_EndDate, new Date());

		pathValueMapProject.put(
			LocationPaths._Root_LocationProject._ProjectStatus,
			com.hertz.mdm._hertz.enums.CompletionStatuses.COMPLETE);

		ModifyValuesProcedure
			.execute(projectRecord, pathValueMapProject, context.getSession(), true, false);

		//Set the Status and Open Date on the Location record
		Adaptation locationRecord = AdaptationUtil
			.followFK(projectRecord, LocationPaths._Root_LocationProject._Location);

		if (locationRecord == null)
		{
			return;
		}

		HashMap<Path, Object> pathValueMapLocation = new HashMap<Path, Object>();

		pathValueMapLocation.put(LocationPaths._Root_Location._OpenCloseDate_StartDate, new Date());

		pathValueMapLocation.put(
			LocationPaths._Root_Location._Status,
			com.hertz.mdm.location.enums.LocationStatuses.OPEN);

		pathValueMapLocation.put(LocationPaths._Root_Location._CurrentProjectType, null);

		ModifyValuesProcedure
			.execute(locationRecord, pathValueMapLocation, context.getSession(), true, false);

		//Null out the AreaHelfForLocation on the Area Record
		Adaptation areaRecord = AdaptationUtil
			.followFK(locationRecord, LocationPaths._Root_Location._LocationInformation_Area);

		HashMap<Path, Object> pathValueMap = new HashMap<Path, Object>();

		//pathValueMap.put(LocationPaths._Root_Area._AreaHeldForLocation, null);

		ModifyValuesProcedure.execute(areaRecord, pathValueMap, context.getSession(), true, true);

		super.handleWorkItemCompletion(context);
	}
}