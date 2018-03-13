/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.scripttask;

import java.util.Date;
import java.util.HashMap;

import com.hertz.mdm.location.enums.LocationProjectStatuses;
import com.hertz.mdm.location.path.LocationPaths;
import com.hertz.mdm.location.workflow.LocationProjectWorkflowUtilities;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationHome;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.RequestResult;
import com.orchestranetworks.ps.procedure.ModifyValuesProcedure;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.workflow.ScriptTask;
import com.orchestranetworks.workflow.ScriptTaskContext;

public class CancelLocationProjectScriptTask extends ScriptTask
{
	@Override
	public void executeScript(ScriptTaskContext context) throws OperationException
	{
		Adaptation projectRecord = LocationProjectWorkflowUtilities
			.getLocationProjectRecord(context, context.getRepository());

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

		HashMap<Path, Object> pathValueMapProject = new HashMap<Path, Object>();
		pathValueMapProject
			.put(LocationPaths._Root_LocationProject._EffectiveStartEndDates_EndDate, new Date());
		pathValueMapProject.put(
			LocationPaths._Root_LocationProject._ProjectStatus,
			LocationProjectStatuses.LOCATION_PROJECT_STATUS_CANCELLED);
		ModifyValuesProcedure
			.execute(projectRecord, pathValueMapProject, context.getSession(), true, false);

		//Using the Location Record, get the Master DataSpace
		AdaptationHome masterDataSpace = locationRecord.getHome().getParentBranch();

		//Get the Master DataSet
		Adaptation masterLocationDataSet = masterDataSpace
			.findAdaptationOrNull(locationRecord.getContainer().getAdaptationName());

		AdaptationTable masterLocationTable = masterLocationDataSet
			.getTable(LocationPaths._Root_Location.getPathInSchema());

		String locationId = locationRecord.get(LocationPaths._Root_Location._Id).toString();

		//Get the Location from the Master DataSet Location Table
		RequestResult requestResult = masterLocationTable.createRequestResult(
			LocationPaths._Root_Location._Id.format() + "='" + locationId + "'");

		Adaptation masterLocationRecord = requestResult.nextAdaptation();

		HashMap<Path, Object> pathValueMapMasterLocation = new HashMap<Path, Object>();
		pathValueMapMasterLocation.put(LocationPaths._Root_Location._CurrentProjectType, null);
		ModifyValuesProcedure.execute(
			masterLocationRecord,
			pathValueMapMasterLocation,
			context.getSession(),
			true,
			false);
	}
}
