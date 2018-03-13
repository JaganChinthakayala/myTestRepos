package com.hertz.mdm.location.workflow.launcher;

import com.hertz.mdm._hertz.constants.HtzWorkflowConstants;
import com.hertz.mdm.location.constants.LocationProjectWorkflowConstants;
import com.hertz.mdm.location.path.LocationPathConfig;
import com.orchestranetworks.ps.project.path.SubjectPathConfig;
import com.orchestranetworks.ps.workflow.WorkflowLauncherContext;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.workflow.ProcessLauncher;
import com.orchestranetworks.workflow.ScriptTaskContext;

public class AsynchronousLocationWorkflowLauncher extends LocationSubjectWorkflowLauncher
{
	private ScriptTaskContext scriptTaskContext = null;
	private String workFlowKey = null;

	public AsynchronousLocationWorkflowLauncher(
		ScriptTaskContext scriptTaskContext,
		String workflowKey)
	{
		this.scriptTaskContext = scriptTaskContext;
		this.workFlowKey = workflowKey;
	}

	private static final long serialVersionUID = 1L;

	public void execute(WorkflowLauncherContext context) throws OperationException
	{
		super.execute(context);
	}

	public void execute(WorkflowLauncherContext context, boolean checkCurrentProjectType)
		throws OperationException
	{
		super.execute(context, checkCurrentProjectType);
	}

	protected void setAdditionalContextVariables(ProcessLauncher processLauncher)
		throws OperationException
	{
		processLauncher.setInputParameter(
			LocationProjectWorkflowConstants.PARAM_DATA_SET,
			scriptTaskContext.getVariableString(LocationProjectWorkflowConstants.PARAM_DATA_SET));
		processLauncher.setInputParameter(
			LocationProjectWorkflowConstants.PARAM_RECORD,
			scriptTaskContext.getVariableString(LocationProjectWorkflowConstants.PARAM_RECORD));
		processLauncher.setInputParameter(
			LocationProjectWorkflowConstants.PARAM_WORKING_DATA_SPACE,
			scriptTaskContext
				.getVariableString(LocationProjectWorkflowConstants.PARAM_WORKING_DATA_SPACE));
		processLauncher.setInputParameter(
			LocationProjectWorkflowConstants.PARAM_MASTER_DATA_SPACE,
			scriptTaskContext
				.getVariableString(LocationProjectWorkflowConstants.PARAM_MASTER_DATA_SPACE));
		processLauncher.setInputParameter(
			LocationProjectWorkflowConstants.PARAM_CURRENT_USER_ID,
			scriptTaskContext
				.getVariableString(LocationProjectWorkflowConstants.PARAM_CURRENT_USER_ID));
		processLauncher.setInputParameter(
			LocationProjectWorkflowConstants.PARAM_RECORD_NAME_VALUE,
			scriptTaskContext
				.getVariableString(LocationProjectWorkflowConstants.PARAM_RECORD_NAME_VALUE));
		processLauncher.setInputParameter(
			LocationProjectWorkflowConstants.PARAM_USER_TASK_CREATE_DATE_TIME,
			scriptTaskContext.getVariableString(
				LocationProjectWorkflowConstants.PARAM_USER_TASK_CREATE_DATE_TIME));
		processLauncher.setInputParameter(
			LocationProjectWorkflowConstants.PARAM_CURRENT_USER_LABEL,
			scriptTaskContext
				.getVariableString(LocationProjectWorkflowConstants.PARAM_CURRENT_USER_LABEL));
		processLauncher.setInputParameter(
			LocationProjectWorkflowConstants.PARAM_CURRENT_APPROVER_ID,
			scriptTaskContext
				.getVariableString(LocationProjectWorkflowConstants.PARAM_CURRENT_APPROVER_ID));
		processLauncher.setInputParameter(
			LocationProjectWorkflowConstants.PARAM_WORKFLOW_INSTANCE_CREATE_DATE_TIME,
			scriptTaskContext.getVariableString(
				LocationProjectWorkflowConstants.PARAM_WORKFLOW_INSTANCE_CREATE_DATE_TIME));
		processLauncher.setInputParameter(
			LocationProjectWorkflowConstants.PARAM_XPATH_TO_TABLE,
			scriptTaskContext
				.getVariableString(LocationProjectWorkflowConstants.PARAM_XPATH_TO_TABLE));
		processLauncher.setInputParameter(
			LocationProjectWorkflowConstants.PARAM_IS_CANCELLED,
			scriptTaskContext
				.getVariableString(LocationProjectWorkflowConstants.PARAM_IS_CANCELLED));
		processLauncher.setInputParameter(
			LocationProjectWorkflowConstants.PARAM_PROJECT_TEAM_MEMBERS,
			scriptTaskContext
				.getVariableString(LocationProjectWorkflowConstants.PARAM_PROJECT_TEAM_MEMBERS));
		processLauncher.setInputParameter(
			LocationProjectWorkflowConstants.PARAM_PROJECT_TYPE,
			scriptTaskContext
				.getVariableString(LocationProjectWorkflowConstants.PARAM_PROJECT_TYPE));

		//Set the Selected Location context variable to the child Location
		if (scriptTaskContext
			.getVariableString(HtzWorkflowConstants.DATA_CONTEXT_LAUNCH_LOCATION_ID) != null)
		{
			processLauncher.setInputParameter(
				HtzWorkflowConstants.DATA_CONTEXT_LAUNCH_LOCATION_ID,
				scriptTaskContext
					.getVariableString(HtzWorkflowConstants.DATA_CONTEXT_LAUNCH_LOCATION_ID));
		}
	}

	@Override
	protected void setAdditionalContextVariables() throws OperationException
	{
		super.setAdditionalContextVariables();

		launcher.setInputParameter(
			LocationProjectWorkflowConstants.PARAM_DATA_SET,
			scriptTaskContext.getVariableString(LocationProjectWorkflowConstants.PARAM_DATA_SET));
		launcher.setInputParameter(
			LocationProjectWorkflowConstants.PARAM_RECORD,
			scriptTaskContext.getVariableString(LocationProjectWorkflowConstants.PARAM_RECORD));
		launcher.setInputParameter(
			LocationProjectWorkflowConstants.PARAM_WORKING_DATA_SPACE,
			scriptTaskContext
				.getVariableString(LocationProjectWorkflowConstants.PARAM_WORKING_DATA_SPACE));
		launcher.setInputParameter(
			LocationProjectWorkflowConstants.PARAM_MASTER_DATA_SPACE,
			scriptTaskContext
				.getVariableString(LocationProjectWorkflowConstants.PARAM_MASTER_DATA_SPACE));
		launcher.setInputParameter(
			LocationProjectWorkflowConstants.PARAM_CURRENT_USER_ID,
			scriptTaskContext
				.getVariableString(LocationProjectWorkflowConstants.PARAM_CURRENT_USER_ID));
		launcher.setInputParameter(LocationProjectWorkflowConstants.PARAM_CURRENT_USER_ID, "admin");
		launcher.setInputParameter(
			LocationProjectWorkflowConstants.PARAM_RECORD_NAME_VALUE,
			scriptTaskContext
				.getVariableString(LocationProjectWorkflowConstants.PARAM_RECORD_NAME_VALUE));
		launcher.setInputParameter(
			LocationProjectWorkflowConstants.PARAM_USER_TASK_CREATE_DATE_TIME,
			scriptTaskContext.getVariableString(
				LocationProjectWorkflowConstants.PARAM_USER_TASK_CREATE_DATE_TIME));
		launcher.setInputParameter(
			LocationProjectWorkflowConstants.PARAM_CURRENT_USER_LABEL,
			scriptTaskContext
				.getVariableString(LocationProjectWorkflowConstants.PARAM_CURRENT_USER_LABEL));
		launcher.setInputParameter(
			LocationProjectWorkflowConstants.PARAM_CURRENT_APPROVER_ID,
			scriptTaskContext
				.getVariableString(LocationProjectWorkflowConstants.PARAM_CURRENT_APPROVER_ID));
		launcher.setInputParameter(
			LocationProjectWorkflowConstants.PARAM_WORKFLOW_INSTANCE_CREATE_DATE_TIME,
			scriptTaskContext.getVariableString(
				LocationProjectWorkflowConstants.PARAM_WORKFLOW_INSTANCE_CREATE_DATE_TIME));
		launcher.setInputParameter(
			LocationProjectWorkflowConstants.PARAM_XPATH_TO_TABLE,
			scriptTaskContext
				.getVariableString(LocationProjectWorkflowConstants.PARAM_XPATH_TO_TABLE));
		launcher.setInputParameter(
			LocationProjectWorkflowConstants.PARAM_IS_CANCELLED,
			scriptTaskContext
				.getVariableString(LocationProjectWorkflowConstants.PARAM_IS_CANCELLED));
		launcher.setInputParameter(
			LocationProjectWorkflowConstants.PARAM_PROJECT_TEAM_MEMBERS,
			scriptTaskContext
				.getVariableString(LocationProjectWorkflowConstants.PARAM_PROJECT_TEAM_MEMBERS));
		launcher.setInputParameter(
			LocationProjectWorkflowConstants.PARAM_PROJECT_TYPE,
			scriptTaskContext
				.getVariableString(LocationProjectWorkflowConstants.PARAM_PROJECT_TYPE));

		//Set the Selected Location context variable to the child Location
		if (scriptTaskContext
			.getVariableString(HtzWorkflowConstants.DATA_CONTEXT_LAUNCH_LOCATION_ID) != null)
		{
			launcher.setInputParameter(
				HtzWorkflowConstants.DATA_CONTEXT_LAUNCH_LOCATION_ID,
				scriptTaskContext
					.getVariableString(HtzWorkflowConstants.DATA_CONTEXT_LAUNCH_LOCATION_ID));
		}
	}

	@Override
	public SubjectPathConfig getSubjectPathConfig()
	{
		return LocationPathConfig.getInstance();
	}

	@Override
	protected String getProjectMasterWorkflowKey(WorkflowLauncherContext context)
	{
		return workFlowKey;
	}
}
