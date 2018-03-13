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
public class LaunchOpenLocationBusinessLicenseTrashUtilsWorkFlowScriptTask extends ScriptTask
{
	private static final String PROJECT_NAME_PREFIX = Domains.LOCATION + " - " + "Open Location ";

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
		AsynchronousLocationWorkflowLauncher workflowLauncher;
		WorkflowLauncherContext workflowLauncherContext;

		if (!LocationProjectTypes.MOVE_LOCATION
			.equals(projectRecord.getString(LocationPaths._Root_LocationProject._ProjectStatus)))
		{
			workflowLauncher = new AsynchronousLocationWorkflowLauncher(
				context,
				LocationProjectWorkflowConstants.WF_KEY_OBTAIN_BUSINESS_LICENSE);
			workflowLauncherContext = HtzLocationUtilities.createWorkflowLauncherContext(
				projectRecord,
				locationRecord,
				context.getSession(),
				repo,
				LocationProjectTypes.OPEN_LOCATION,
				PROJECT_NAME_PREFIX);
			workflowLauncher.execute(workflowLauncherContext, false);
		}

		//Launch the OrderTrashAndUtilities Workflow if not Served By
		if (!projectRecord.get_boolean(LocationPaths._Root_LocationProject._IsLocationServedBy))
		{
			workflowLauncher = new AsynchronousLocationWorkflowLauncher(
				context,
				LocationProjectWorkflowConstants.WF_KEY_HANDLE_TRASH_UTILITIES);

			workflowLauncherContext = HtzLocationUtilities.createWorkflowLauncherContext(
				projectRecord,
				locationRecord,
				context.getSession(),
				repo,
				LocationProjectTypes.OPEN_LOCATION,
				PROJECT_NAME_PREFIX);
			workflowLauncher.execute(workflowLauncherContext, false);
		}
	}
}
