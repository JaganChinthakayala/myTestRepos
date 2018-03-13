/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.scripttask;

import com.hertz.mdm._hertz.enums.Domains;
import com.hertz.mdm.location.constants.LocationProjectWorkflowConstants;
import com.hertz.mdm.location.enums.LocationProjectTypes;
import com.hertz.mdm.location.path.LocationPaths;
import com.hertz.mdm.location.util.HtzLocationUtilities;
import com.hertz.mdm.location.workflow.LocationProjectWorkflowUtilities;
import com.hertz.mdm.location.workflow.launcher.AsynchronousLocationWorkflowLauncher;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.ps.workflow.WorkflowLauncherContext;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.workflow.ScriptTask;
import com.orchestranetworks.workflow.ScriptTaskContext;

/**
 */
public class LaunchCloseLocationResFixPostCloseChecklistFlowScriptTask extends ScriptTask
{
	private static final String PROJECT_NAME_PREFIX = Domains.LOCATION + " - " + "Close Location ";

	@Override
	public void executeScript(ScriptTaskContext context) throws OperationException
	{
		Repository repo = context.getRepository();

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

		AsynchronousLocationWorkflowLauncher workflowLauncher = new AsynchronousLocationWorkflowLauncher(
			context,
			LocationProjectWorkflowConstants.WF_KEY_POSTCLOSE_CHECKLIST);
		WorkflowLauncherContext workflowLauncherContext = HtzLocationUtilities
			.createWorkflowLauncherContext(
				projectRecord,
				locationRecord,
				context.getSession(),
				repo,
				LocationProjectTypes.CLOSE_LOCATION,
				PROJECT_NAME_PREFIX);
		workflowLauncher.execute(workflowLauncherContext, false);

		//Only Execute RESFIX if this is a CLOSE rather than a MOVE/CLOSE
		if (LocationProjectTypes.MOVE_LOCATION
			.equals(projectRecord.getString(LocationPaths._Root_LocationProject._ProjectType)))
		{
			return;
		}

		workflowLauncher = new AsynchronousLocationWorkflowLauncher(
			context,
			LocationProjectWorkflowConstants.WF_KEY_RESFIX);
		workflowLauncherContext = HtzLocationUtilities.createWorkflowLauncherContext(
			projectRecord,
			locationRecord,
			context.getSession(),
			repo,
			LocationProjectTypes.CLOSE_LOCATION,
			PROJECT_NAME_PREFIX);
		workflowLauncher.execute(workflowLauncherContext, false);
	}
}
