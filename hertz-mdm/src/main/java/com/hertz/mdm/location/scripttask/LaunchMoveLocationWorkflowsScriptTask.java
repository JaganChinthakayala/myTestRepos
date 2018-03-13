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
public class LaunchMoveLocationWorkflowsScriptTask extends ScriptTask
{
	private static final String PROJECT_NAME_PREFIX = Domains.LOCATION + " - " + "Move Location ";

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

		//Launch Obtain Business License Workflow
		AsynchronousLocationWorkflowLauncher workflowLauncher = new AsynchronousLocationWorkflowLauncher(
			context,
			LocationProjectWorkflowConstants.WF_KEY_OBTAIN_BUSINESS_LICENSE);

		WorkflowLauncherContext workflowLauncherContext = HtzLocationUtilities
			.createWorkflowLauncherContext(
				projectRecord,
				locationRecord,
				context.getSession(),
				repo,
				LocationProjectTypes.MOVE_LOCATION,
				PROJECT_NAME_PREFIX);

		workflowLauncher.execute(workflowLauncherContext, false);

		//Launch IT Questionnaire Workflow
		workflowLauncher = new AsynchronousLocationWorkflowLauncher(
			context,
			LocationProjectWorkflowConstants.WF_KEY_IT_QUESTIONNAIRE_AND_PRECLOSE_CHECKLIST);

		workflowLauncherContext = HtzLocationUtilities.createWorkflowLauncherContext(
			projectRecord,
			locationRecord,
			context.getSession(),
			repo,
			LocationProjectTypes.MOVE_LOCATION,
			PROJECT_NAME_PREFIX);

		workflowLauncher.execute(workflowLauncherContext, false);

		//Launch Update HR Oracle Workflow
		workflowLauncher = new AsynchronousLocationWorkflowLauncher(
			context,
			LocationProjectWorkflowConstants.WF_KEY_UPDATE_HR_ORACLE);

		workflowLauncherContext = HtzLocationUtilities.createWorkflowLauncherContext(
			projectRecord,
			locationRecord,
			context.getSession(),
			repo,
			LocationProjectTypes.MOVE_LOCATION,
			PROJECT_NAME_PREFIX);

		workflowLauncher.execute(workflowLauncherContext, false);

		//Launch the Oracle HR Workflow
		workflowLauncher = new AsynchronousLocationWorkflowLauncher(
			context,
			LocationProjectWorkflowConstants.WF_KEY_UPDATE_HR_ORACLE);

		workflowLauncherContext = HtzLocationUtilities.createWorkflowLauncherContext(
			projectRecord,
			locationRecord,
			context.getSession(),
			repo,
			LocationProjectTypes.MOVE_LOCATION,
			PROJECT_NAME_PREFIX);

		workflowLauncher.execute(workflowLauncherContext, false);

		//Launch the Update Verisae Workflow
		workflowLauncher = new AsynchronousLocationWorkflowLauncher(
			context,
			LocationProjectWorkflowConstants.WF_KEY_EXECUTE_UPDATE_VERISAE);

		workflowLauncherContext = HtzLocationUtilities.createWorkflowLauncherContext(
			projectRecord,
			locationRecord,
			context.getSession(),
			repo,
			LocationProjectTypes.MOVE_LOCATION,
			PROJECT_NAME_PREFIX);

		workflowLauncher.execute(workflowLauncherContext, false);

		//Launch the Update Offsell Workflow
		workflowLauncher = new AsynchronousLocationWorkflowLauncher(
			context,
			LocationProjectWorkflowConstants.WF_KEY_EXECUTE_UPDATE_OFFSELL);

		workflowLauncherContext = HtzLocationUtilities.createWorkflowLauncherContext(
			projectRecord,
			locationRecord,
			context.getSession(),
			repo,
			LocationProjectTypes.MOVE_LOCATION,
			PROJECT_NAME_PREFIX);

		workflowLauncher.execute(workflowLauncherContext, false);
	}
}
