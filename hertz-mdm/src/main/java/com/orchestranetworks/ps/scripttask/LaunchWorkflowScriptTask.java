package com.orchestranetworks.ps.scripttask;

import java.util.*;

import com.onwbp.base.text.*;
import com.orchestranetworks.instance.*;
import com.orchestranetworks.ps.workflow.*;
import com.orchestranetworks.service.*;
import com.orchestranetworks.workflow.*;

// TODO: Refactor this to use WorkflowLauncher & WorkflowLauncherContext
public class LaunchWorkflowScriptTask extends ScriptTaskBean
{
	private String workflow;
	protected String workflowDescription = null;

	@Override
	public void executeScript(ScriptTaskBeanContext aContext) throws OperationException
	{
		final Repository repository = aContext.getRepository();
		final Session session = aContext.getSession();
		final WorkflowEngine engine = WorkflowEngine.getFromRepository(repository, session);
		String workflowToLaunch = getWorkflowToLaunch();
		final ProcessLauncher launcher = engine.getProcessLauncher(PublishedProcessKey.forName(workflowToLaunch));

		final ProcessInstance process = aContext.getProcessInstance();
		final DataContextReadOnly dataContext = process.getDataContext();
		initWorkflowParameters(dataContext, launcher);

		//Assign WorkflowInstanceLabel
		String workflowInstanceLabel = engine.getPublishedProcess(
			PublishedProcessKey.forName(workflowToLaunch))
			.getLabel()
			.formatMessage(Locale.getDefault());
		String recordNameValue = dataContext.getVariableString(WorkflowConstants.PARAM_RECORD_NAME_VALUE);
		if (recordNameValue != null)
		{
			workflowInstanceLabel = recordNameValue + ": " + workflowInstanceLabel;
			workflowInstanceLabel = appendAdditionalData(workflowInstanceLabel, dataContext);
		}
		launcher.setLabel(UserMessage.createInfo(workflowInstanceLabel));
		if (workflowDescription != null)
		{
			launcher.setDescription(UserMessage.createInfo(workflowDescription));
		}

		launcher.launchProcess();
	}
	/**
	 * By default, will simply return the specified workflowInstanceLabel. But can be overridden to have additional logic.
	 */
	protected String appendAdditionalData(
		String workflowInstanceLabel,
		DataContextReadOnly dataContext)
	{
		return workflowInstanceLabel;
	}

	/**
	 * By default, will simply return the specified workflow. But can be overridden to have additional logic.
	 */
	protected String getWorkflowToLaunch()
	{
		return workflow;
	}

	protected List<String> getWorkflowParametersToCopy(DataContextReadOnly dataContext)
	{
		ArrayList<String> params = new ArrayList<String>();
		@SuppressWarnings("unchecked")
		final Iterator<String> iterator = dataContext.getVariableNames();
		while (iterator.hasNext())
		{
			params.add(iterator.next());
		}
		return params;
	}

	protected void initWorkflowParameters(DataContextReadOnly dataContext, ProcessLauncher launcher)
	{
		List<String> paramNames = getWorkflowParametersToCopy(dataContext);
		for (String paramName : paramNames)
		{
			launcher.setInputParameter(paramName, dataContext.getVariableString(paramName));
		}
	}

	public final String getWorkflow()
	{
		return this.workflow;
	}

	public final void setWorkflow(String workflow)
	{
		this.workflow = workflow;
	}
}
