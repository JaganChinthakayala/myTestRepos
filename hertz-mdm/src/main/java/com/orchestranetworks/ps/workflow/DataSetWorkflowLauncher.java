package com.orchestranetworks.ps.workflow;

import java.util.*;

import javax.servlet.http.*;

import com.orchestranetworks.service.*;

public abstract class DataSetWorkflowLauncher extends WorkflowLauncher
{
	private static final long serialVersionUID = 1L;

	public static final String PARAM_WORKFLOW_INSTANCE_NAME = "workflowInstanceName";
	public static final String PARAM_WORKFLOW_INSTANCE_DESCRIPTION = "workflowInstanceDescription";

	protected abstract String getWorkflowKey();

	public void execute(HttpServletRequest request) throws OperationException
	{
		execute(
			new WorkflowLauncherContext(request),
			getWorkflowKey(),
			null,
			request.getParameter(PARAM_WORKFLOW_INSTANCE_NAME));
	}

	public void execute(WorkflowLauncherContext context) throws OperationException
	{
		super.execute(
			context,
			getWorkflowKey(),
			null,
			context.getParameter(PARAM_WORKFLOW_INSTANCE_NAME));
	}

	@Override
	protected String enrichWorkflowInstanceName(String workflowInstanceName, Locale locale)
	{
		return workflowLauncherContext.getParameter(PARAM_WORKFLOW_INSTANCE_NAME) + " "
			+ super.enrichWorkflowInstanceName(workflowInstanceName, locale);
	}

	@Override
	protected void assignWorkflowDescription()
	{
		String description = workflowLauncherContext.getParameter(PARAM_WORKFLOW_INSTANCE_DESCRIPTION);
		if (description != null && description.trim().length() != 0)
		{
			workflowDescription = description;
		}
	}
}
