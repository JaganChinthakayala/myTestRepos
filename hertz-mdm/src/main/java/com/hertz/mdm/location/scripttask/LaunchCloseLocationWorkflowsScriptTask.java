/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.scripttask;

import java.util.HashMap;

import com.hertz.mdm._hertz.constants.HtzWorkflowConstants;
import com.hertz.mdm._hertz.enums.Domains;
import com.hertz.mdm.location.constants.LocationProjectWorkflowConstants;
import com.hertz.mdm.location.enums.DependentChildDisposition;
import com.hertz.mdm.location.enums.LocationProjectTypes;
import com.hertz.mdm.location.path.LocationPaths;
import com.hertz.mdm.location.util.HtzLocationUtilities;
import com.hertz.mdm.location.workflow.LocationProjectWorkflowUtilities;
import com.hertz.mdm.location.workflow.launcher.AsynchronousLocationWorkflowLauncher;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.RequestResult;
import com.orchestranetworks.ps.procedure.ModifyValuesProcedure;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.ps.workflow.WorkflowLauncherContext;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.workflow.ScriptTask;
import com.orchestranetworks.workflow.ScriptTaskContext;

public class LaunchCloseLocationWorkflowsScriptTask extends ScriptTask
{
	String projectNamePrefix = Domains.LOCATION + " - ";
	String projectType = "";

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

		String locationId = locationRecord.get(LocationPaths._Root_Location._Id).toString();

		AdaptationTable locationLocationRelationshipTable = locationRecord.getContainer().getTable(
			LocationPaths._Root_LocationData_LocationLocationRelationship.getPathInSchema());

		// Query the Location/Location Relationship table for this Location with a Dependent Child Disposition
		RequestResult requestResult = locationLocationRelationshipTable.createRequestResult(
			LocationPaths._Root_LocationData_LocationLocationRelationship._ParentLocation.format()
				+ "='" + locationId + "' and "
				+ LocationPaths._Root_LocationData_LocationLocationRelationship._IsRecordActive
					.format()
				+ "=true");

		for (int i = 0; i < requestResult.getSize(); i++)
		{
			Adaptation locationLocationRelationshopRecord = requestResult.nextAdaptation();

			String dispositionOfChildWhenParentClosed = locationLocationRelationshopRecord
				.getString(
					LocationPaths._Root_LocationData_LocationLocationRelationship._DispositionOfChildWhenParentClosed);

			String launchWorkflow = null;

			if (DependentChildDisposition.CLOSE.equals(dispositionOfChildWhenParentClosed))
			{
				launchWorkflow = LocationProjectWorkflowConstants.WF_KEY_CLOSE_LOCATION_PROJECT_MASTER;
				projectNamePrefix = projectNamePrefix + LocationProjectTypes.CLOSE_LOCATION;
				projectType = LocationProjectTypes.CLOSE_LOCATION;
			}
			else if (DependentChildDisposition.MOVE.equals(dispositionOfChildWhenParentClosed))
			{
				launchWorkflow = LocationProjectWorkflowConstants.WF_KEY_MOVE_LOCATION_PROJECT_MASTER;
				projectNamePrefix = projectNamePrefix + LocationProjectTypes.MOVE_LOCATION;
				projectType = LocationProjectTypes.MOVE_LOCATION;
			}

			if (launchWorkflow != null)
			{
				AsynchronousLocationWorkflowLauncher workflowLauncher = new AsynchronousLocationWorkflowLauncher(
					context,
					launchWorkflow);

				WorkflowLauncherContext workflowLauncherContext = HtzLocationUtilities
					.createWorkflowLauncherContext(
						projectRecord,
						locationRecord,
						context.getSession(),
						context.getRepository(),
						projectType,
						projectNamePrefix);

				//Set the Selected Location context variable to the child Location
				context.setVariableString(
					HtzWorkflowConstants.DATA_CONTEXT_LAUNCH_LOCATION_ID,
					locationLocationRelationshopRecord.getString(
						LocationPaths._Root_LocationData_LocationLocationRelationship._SubLocation));

				workflowLauncher.execute(workflowLauncherContext, false);
			}
		}

		//Clear the CurrentProjectType from the Location
		HashMap<Path, Object> pathValueMap = new HashMap<Path, Object>();
		pathValueMap.put(LocationPaths._Root_Location._CurrentProjectType, null);
		ModifyValuesProcedure
			.execute(locationRecord, pathValueMap, context.getSession(), true, false);
	}
}
