package com.orchestranetworks.ps.workflow;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.service.LoggingCategory;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.ServiceContext;
import com.orchestranetworks.service.Session;
import com.orchestranetworks.service.UserReference;
import com.orchestranetworks.service.directory.DirectoryHandler;
import com.orchestranetworks.ui.UIHttpManagerComponent;
import com.orchestranetworks.ui.UIServiceComponentWriter;
import com.orchestranetworks.workflow.ProcessInstance;
import com.orchestranetworks.workflow.ProcessInstanceKey;
import com.orchestranetworks.workflow.ProcessLauncher;
import com.orchestranetworks.workflow.PublishedProcessKey;
import com.orchestranetworks.workflow.UserTask;
import com.orchestranetworks.workflow.WorkflowEngine;

public class WorkflowLauncher extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	protected enum WorkItemRedirectPolicyEnum {
		NONE, MASTER_WORKFLOW, FIRST_SUB_WORKFLOW
	}

	public static final String PARAM_WORKFLOW_NAME = "workflowName";
	public static final String PARAM_XPATH_TO_TABLE = "xpathToTable";

	private static final long WAIT_FOR_SUB_PROCESS_MILLIS = 2000;
	private static final int MAX_NUM_OF_SUB_PROCESS_WAITS = 15;
	private static final long WAIT_FOR_WORK_ITEM_MILLIS = 2000;
	private static final int MAX_NUM_OF_WORK_ITEM_WAITS = 15;

	private static final String ALERT_MESSAGE = "Workflow launched.";

	protected WorkflowLauncherContext workflowLauncherContext = null;
	protected WorkflowEngine wfEngine = null;
	protected ProcessLauncher launcher = null;
	protected String workflowName = null;
	protected String workflowDescription = null;
	protected WorkItemRedirectPolicyEnum workItemRedirectPolicy;
	protected boolean takeAndStartWorkItem = true;
	protected boolean showAlert = true;
	protected boolean warnIfNotRedirected = true;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		try
		{
			execute(request);
		}
		catch (OperationException ex)
		{
			throw new ServletException(ex);
		}
	}

	/**
	 * When executing from a servlet, the workflow name, table xpath, and workflow instance name must be specified by
	 * http request parameters. This method takes those parameters and calls into
	 * {@link #execute(WorkflowLauncherContext,String,String,String)}.
	 * 
	 * @param request the http request
	 * @throws OperationException if an error occurred while executing
	 */
	public void execute(HttpServletRequest request) throws OperationException
	{
		execute(
			new WorkflowLauncherContext(request),
			request.getParameter(PARAM_WORKFLOW_NAME),
			request.getParameter(PARAM_XPATH_TO_TABLE),
			request.getParameter(PARAM_WORKFLOW_NAME));
	}

	/**
	 * This is a convenience method that simply creates a {@link WorkflowLauncherContext} from the given request and calls
	 * {@link #execute(WorkflowLauncherContext,String,String,String)}.
	 * 
	 * @param request the http request
	 * @param inWorkflowName the workflow name
	 * @param xpathToTable the table xpath
	 * @param workflowInstanceName the name of the workflow instance
	 * @throws OperationException if an error occurred while executing
	 */
	public void execute(
		HttpServletRequest request,
		String inWorkflowName,
		String xpathToTable,
		String workflowInstanceName) throws OperationException
	{
		execute(
			new WorkflowLauncherContext(request),
			inWorkflowName,
			xpathToTable,
			workflowInstanceName);
	}

	/**
	 * Executes the workflow launch
	 * 
	 * @param context the launcher context, that encapsulates info about the launch such as current data space, current adaptation,
	 * and request parameters
	 * @param inWorkflowName the workflow name
	 * @param xpathToTable the table xpath
	 * @param workflowInstanceName the name of the workflow instance
	 * @throws OperationException if an error occurred while executing
	 */
	public void execute(
		WorkflowLauncherContext context,
		String inWorkflowName,
		String xpathToTable,
		String workflowInstanceName) throws OperationException
	{
		this.workflowLauncherContext = context;
		Adaptation adaptation = workflowLauncherContext.getCurrentAdaptation();
		initRedirectionPolicy();
		if (adaptation.isTableOccurrence())
		{
			preventMultipleLaunches();
		}
		workflowName = inWorkflowName;

		wfEngine = context.createWorkflowEngine();
		launcher = wfEngine.getProcessLauncher(PublishedProcessKey.forName(workflowName));

		Session session = context.getSession();
		UserReference userReference = session.getUserReference();
		launcher
			.setInputParameter(WorkflowConstants.PARAM_CURRENT_USER_ID, userReference.getUserId());
		DirectoryHandler dirHandler = session.getDirectory();
		launcher.setInputParameter(
			WorkflowConstants.PARAM_CURRENT_USER_LABEL,
			dirHandler.displayUser(userReference, session.getLocale()));
		if (xpathToTable != null)
		{
			launcher.setInputParameter(WorkflowConstants.PARAM_XPATH_TO_TABLE, xpathToTable);
		}
		String masterDataSpaceName = context.getCurrentDataSpace().getKey().getName();
		launcher.setInputParameter(WorkflowConstants.PARAM_MASTER_DATA_SPACE, masterDataSpaceName);
		// By default, working data space will be master
		launcher.setInputParameter(WorkflowConstants.PARAM_WORKING_DATA_SPACE, masterDataSpaceName);
		assignWorkflowDescription();
		if (workflowDescription != null)
		{
			launcher.setDescription(UserMessage.createInfo(workflowDescription));
		}
		workflowInstanceName = enrichWorkflowInstanceName(
			workflowInstanceName,
			session.getLocale());
		if (adaptation.isTableOccurrence())
		{
			launcher.setLabel(
				UserMessage.createInfo(assignLabelForUpdate(workflowInstanceName, session)));
			launcher
				.setInputParameter(WorkflowConstants.PARAM_RECORD, adaptation.toXPathExpression());
			launcher.setInputParameter(
				WorkflowConstants.PARAM_DATA_SET,
				adaptation.getContainer().getAdaptationName().getStringName());
		}
		else
		{
			if (xpathToTable == null)
			{
				launcher.setLabel(
					UserMessage.createInfo(assignLabelForDataSetWorkflow(workflowInstanceName)));
			}
			else
			{
				launcher.setLabel(
					UserMessage
						.createInfo(assignLabelForCreate(xpathToTable, workflowInstanceName)));
			}
			launcher.setInputParameter(
				WorkflowConstants.PARAM_DATA_SET,
				adaptation.getAdaptationName().getStringName());
		}
		UserReference user = session.getUserReference();
		launcher.setCreator(user);
		setAdditionalContextVariables();

		ProcessInstanceKey processInstanceKey = launcher.launchProcess();

		ServiceContext sContext = context.getServiceContext();
		if (sContext != null)
		{
			UIServiceComponentWriter writer = sContext.getUIComponentWriter();
			String topFrameRedirectURL = sContext.getURLForEndingService();
			String innerFrameRedirectURL = topFrameRedirectURL;
			if (workItemRedirectPolicy == WorkItemRedirectPolicyEnum.MASTER_WORKFLOW
				|| workItemRedirectPolicy == WorkItemRedirectPolicyEnum.FIRST_SUB_WORKFLOW)
			{
				ProcessInstance processInstance = getProcessInstanceForFirstWorkItem(
					processInstanceKey);
				if (processInstance != null)
				{
					UserTask.WorkItem firstWorkItem = null;
					try
					{
						int i;
						//for (i = 0; i < MAX_NUM_OF_WORK_ITEM_WAITS
						for (i = 0; i < 25 && (firstWorkItem = getFirstWorkItem(
							processInstance.getProcessInstanceKey())) == null; i++)
						{
							LoggingCategory.getWorkflow().debug(
								"Sleeping " + WAIT_FOR_WORK_ITEM_MILLIS
									+ " millis for first work item to be created. Attempt #" + i);
							Thread.sleep(WAIT_FOR_WORK_ITEM_MILLIS);
						}
						if (i == MAX_NUM_OF_WORK_ITEM_WAITS)
						{
							LoggingCategory.getWorkflow().error(
								"Max number of retries reached for wait of first user task work item");
						}
					}
					catch (InterruptedException ex)
					{
						LoggingCategory.getWorkflow()
							.error("Waiting for first user task work item interrupted", ex);
					}
					if (firstWorkItem != null)
					{
						if (session.isUserInRole(
							WorkflowUtilities.getWorkItemOfferedToRole(firstWorkItem)))
						{
							topFrameRedirectURL = sContext.getURLForSelection(
								firstWorkItem.getWorkItemKey(),
								takeAndStartWorkItem);
							UIHttpManagerComponent uiMgr = sContext
								.createWebComponentForSubSession();
							uiMgr.setRedirectionURI(sContext.getURLForEndingService());
							uiMgr.selectWorkItem(true, firstWorkItem.getWorkItemKey());
							innerFrameRedirectURL = uiMgr.getURIWithParameters();
						}
						else
						{
							if (warnIfNotRedirected)
							{
								LoggingCategory.getWorkflow().warn(
									"User that launched service is not in role for first work item");
							}
							if (showAlert)
							{
								writer.addJS_cr("alert('" + ALERT_MESSAGE + "');");
							}
						}
					}
				}
			}
			else if (showAlert)
			{
				writer.addJS_cr("alert('" + ALERT_MESSAGE + "');");
			}

			writer.addJS_cr("if (window.self == window.top)");
			writer.addJS_cr("  window.location.href='" + topFrameRedirectURL + "';");
			writer.addJS_cr("else");
			writer.addJS_cr("  window.location.href='" + innerFrameRedirectURL + "';");
		}
	}

	protected void initRedirectionPolicy()
	{
		workItemRedirectPolicy = WorkItemRedirectPolicyEnum.MASTER_WORKFLOW;
	}

	protected void preventMultipleLaunches() throws OperationException
	{
		// do nothing
	}

	protected ProcessInstance getProcessInstanceForFirstWorkItem(
		ProcessInstanceKey processInstanceKey)
	{
		if (workItemRedirectPolicy == WorkItemRedirectPolicyEnum.FIRST_SUB_WORKFLOW)
		{
			List<ProcessInstance> subProcessInstances = null;
			try
			{
				int i;
				// Need to fetch process instance each time through loop.
				// Can't do once & store in a variable because object needs
				// to be constructed from current state each time
				for (i = 0; i < MAX_NUM_OF_SUB_PROCESS_WAITS
					&& (subProcessInstances = wfEngine.getProcessInstance(processInstanceKey)
						.getCurrentSubWorkflows()).isEmpty(); i++)
				{
					LoggingCategory.getWorkflow().debug(
						"Sleeping " + WAIT_FOR_SUB_PROCESS_MILLIS
							+ " millis for sub-workflow to start. Attempt #" + i);
					Thread.sleep(WAIT_FOR_SUB_PROCESS_MILLIS);
				}
				if (i == MAX_NUM_OF_SUB_PROCESS_WAITS)
				{
					LoggingCategory.getWorkflow()
						.error("Max number of retries reached for wait of first sub-workflow");
				}
			}
			catch (InterruptedException ex)
			{
				LoggingCategory.getWorkflow()
					.error("Waiting for first sub-workflow interrupted", ex);
			}
			return (subProcessInstances == null || subProcessInstances.isEmpty()) ? null
				: subProcessInstances.get(0);
		}
		return wfEngine.getProcessInstance(processInstanceKey);
	}

	protected UserTask.WorkItem getFirstWorkItem(ProcessInstanceKey processInstanceKey)
	{
		// We must look up the process instance again because it needs to be refreshed,
		// otherwise we'd be looking up work items in a stale process instance
		ProcessInstance processInstance = wfEngine.getProcessInstance(processInstanceKey);
		@SuppressWarnings("unchecked")
		List<UserTask.WorkItem> workItems = processInstance.getWorkItems();
		return workItems.isEmpty() ? null : workItems.get(0);
	}

	protected void assignWorkflowDescription()
	{
		// override in subclass
	}

	protected String enrichWorkflowInstanceName(String workflowInstanceName, Locale locale)
	{
		if (workflowInstanceName != null)
		{
			return workflowInstanceName;
		}
		return getWorkflowModelLabelForInstanceName(workflowName, locale);
	}

	/**
	 * Get the workflow model label as it should appear in the workflow instance name.
	 * By default, this simply returns the workflow model's label but can be sub-classed to massage the label.
	 * 
	 * @param workflowModelName the name of the workflow model
	 * @param locale the locale
	 * @return the label as it should appear in the workflow instance name
	 */
	protected String getWorkflowModelLabelForInstanceName(String workflowModelName, Locale locale)
	{
		return wfEngine.getPublishedProcess(PublishedProcessKey.forName(workflowName))
			.getLabel()
			.formatMessage(locale);
	}

	// override if needed
	protected void setAdditionalContextVariables() throws OperationException
	{
		// do nothing
	}

	protected String assignLabelForCreate(String xpathToTable, String workflowInstanceName)
	{
		AdaptationTable table = workflowLauncherContext.getCurrentAdaptation()
			.getTable(Path.parse(xpathToTable));
		Session session = workflowLauncherContext.getSession();
		String tableName = table.getTableNode().getLabel(session.getLocale());
		String label = (workflowInstanceName != null) ? workflowInstanceName
			: "Create New " + tableName + " Record";
		return "${" + WorkflowConstants.PARAM_RECORD_NAME_VALUE + "} " + label + ", launched by "
			+ session.getUserReference().getUserId();
	}

	protected String assignLabelForUpdate(String workflowInstanceName, Session session)
	{
		Adaptation adaptation = workflowLauncherContext.getCurrentAdaptation();
		String tableName = adaptation.getContainerTable()
			.getTableNode()
			.getLabel(session.getLocale());
		String recordLabel = getRecordLabel(adaptation, session);
		launcher.setInputParameter(WorkflowConstants.PARAM_RECORD_NAME_VALUE, recordLabel);
		String label = (workflowInstanceName != null) ? workflowInstanceName
			: "Update " + tableName + " Record";
		return "${" + WorkflowConstants.PARAM_RECORD_NAME_VALUE + "} " + label + ", launched by "
			+ session.getUserReference().getUserId();
	}

	protected String assignLabelForDataSetWorkflow(String workflowInstanceName)
	{
		Session session = workflowLauncherContext.getSession();
		return workflowInstanceName + ", launched by " + session.getUserReference().getUserId();
	}

	protected String getRecordLabel(Adaptation record, Session session)
	{
		return record.getLabel(session.getLocale());
	}

	public WorkItemRedirectPolicyEnum getWorkItemRedirectPolicy()
	{
		return this.workItemRedirectPolicy;
	}

	public void setWorkItemRedirectPolicy(WorkItemRedirectPolicyEnum workItemRedirectPolicy)
	{
		this.workItemRedirectPolicy = workItemRedirectPolicy;
	}

	public boolean isTakeAndStartWorkItem()
	{
		return this.takeAndStartWorkItem;
	}

	public void setTakeAndStartWorkItem(boolean takeAndStartWorkItem)
	{
		this.takeAndStartWorkItem = takeAndStartWorkItem;
	}

}
