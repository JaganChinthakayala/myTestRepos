package com.orchestranetworks.ps.workflow;

import java.util.*;

import com.onwbp.adaptation.*;
import com.orchestranetworks.instance.*;
import com.orchestranetworks.interactions.InteractionHelper.ParametersMap;
import com.orchestranetworks.interactions.*;
import com.orchestranetworks.ps.accessrule.*;
import com.orchestranetworks.ps.util.*;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.service.*;
import com.orchestranetworks.service.directory.*;
import com.orchestranetworks.workflow.*;
import com.orchestranetworks.workflow.UserTask.WorkItem;

/**
 * Methods that help interact with workflow data context values
 */
public class WorkflowUtilities
{
	private static final String ROLE_PERMISSIONS_USER = "Permissions User";

	private static final String PROP_IGNORE_COMPLETION_CRITERIA = "ignoreCompletionCriteria";

	public static Adaptation getRecord(DataContextReadOnly dataContext, Repository repo)
		throws OperationException
	{

		return getRecord(
			dataContext,
			repo,
			WorkflowConstants.PARAM_RECORD,
			WorkflowConstants.PARAM_WORKING_DATA_SPACE);
	}

	public static Adaptation getRecord(
		DataContextReadOnly dataContext,
		Repository repo,
		String recordParamName,
		String dataSpaceParam) throws OperationException
	{
		return getRecord(dataContext, repo, recordParamName, dataSpaceParam, true);
	}

	public static Adaptation getRecord(
		DataContextReadOnly dataContext,
		Repository repo,
		String recordParamName,
		String dataSpaceParam,
		boolean errorIfNotFound) throws OperationException
	{
		String recordXpath = dataContext.getVariableString(recordParamName);
		Adaptation dataSet = getDataSet(dataContext, repo, dataSpaceParam);
		return AdaptationUtil.getRecord(recordXpath, dataSet, true, errorIfNotFound);
	}

	/**
	 * Get the record from a workflow service, which uses a session interaction.
	 * This assumes the standard EBX parameters to workflow services.
	 * 
	 * @param interaction the session interaction which contains the parameters for the workflow service
	 * @param repo the repository
	 * @return the record
	 * @throws OperationException if an error occurs retrieving the record
	 */
	public static Adaptation getRecordFromSessionInteraction(
		SessionInteraction interaction,
		Repository repo) throws OperationException
	{
		return getRecordFromSessionInteraction(
			interaction,
			getDataSetFromSessionInteraction(interaction, repo));
	}

	/**
	 * Get the record from a workflow service, which uses a session interaction.
	 * This uses the data set passed in instead of the standard EBX paramters to workflow services.
	 * 
	 * @param interaction the session interaction which contains the parameters for the workflow service
	 * @param dataSet the data set
	 * @return the record
	 * @throws OperationException if an error occurs retrieving the record
	 */
	public static Adaptation getRecordFromSessionInteraction(
		SessionInteraction interaction,
		Adaptation dataSet) throws OperationException
	{
		String recordXPath = null;
		// First try to get it from the record being created, for a create step
		if (ServiceKey.CREATE.equals(interaction.getServiceKey()))
		{
			ParametersMap internalParamMap = interaction.getInternalParameters();
			if (internalParamMap != null)
			{
				recordXPath = internalParamMap.getVariableString(WorkflowConstants.SESSION_PARAM_CREATED);
			}
		}
		// If null, it might not be a create step, or created yet, so get it from the input param
		if (recordXPath == null)
		{
			ParametersMap inputParamMap = interaction.getInputParameters();
			if (inputParamMap != null)
			{
				// Custom services can use a special record param so check that next
				String xpath = inputParamMap.getVariableString(WorkflowConstants.SESSION_PARAM_WORKFLOW_RECORD);
				if (isXPathForRecord(xpath))
				{
					recordXPath = xpath;
				}
				// Else use the normal xpath param
				else
				{
					xpath = inputParamMap.getVariableString(WorkflowConstants.SESSION_PARAM_XPATH);
					if (isXPathForRecord(xpath))
					{
						recordXPath = xpath;
					}
				}
			}
		}
		return recordXPath == null ? null : AdaptationUtil.getRecord(
			recordXPath,
			dataSet,
			true,
			true);
	}

	public static String getSessionInteractionParameter(Session session, String parameterName)
	{
		SessionInteraction interaction = session.getInteraction(true);
		if (interaction == null)
		{
			//Execution is not part of a workflow
			return null;
		}
		return interaction.getInputParameters().getVariableString(parameterName);
	}

	public static boolean isSessionInteractionParameterDefined(Session session, String parameterName)
	{
		SessionInteraction interaction = session.getInteraction(true);
		if (interaction == null)
		{
			//Execution is not part of a workflow
			return false;
		}
		@SuppressWarnings("unchecked")
		Iterator<String> iter = interaction.getInputParameters().getVariableNames();
		boolean returnVal = false;
		// Only way to tell if a parameter is defined is to loop through its iterator until we find it
		while (!returnVal && iter.hasNext())
		{
			if (parameterName.equals(iter.next()))
			{
				returnVal = true;
			}
		}
		return returnVal;
	}

	public static boolean isXPathForRecord(String xpath)
	{
		return xpath != null && xpath.contains("[");
	}

	public static AdaptationTable getAdaptationTable(
		DataContextReadOnly dataContext,
		Repository repo,
		String dataSpaceParam) throws OperationException
	{
		final String xpathToTable = dataContext.getVariableString(WorkflowConstants.PARAM_XPATH_TO_TABLE);
		if (xpathToTable == null)
		{
			return null;
		}
		return getAdaptationTable(dataContext, repo, dataSpaceParam, Path.parse(xpathToTable));
	}

	public static AdaptationTable getAdaptationTable(
		DataContextReadOnly dataContext,
		Repository repo,
		String dataSpaceParam,
		Path tablePath) throws OperationException
	{
		Adaptation currentDataSet = getDataSet(dataContext, repo, dataSpaceParam);
		if (tablePath == null)
		{
			return null;
		}
		// get table adaptation
		return currentDataSet.getTable(tablePath);
	}

	/**
	 * Get the data space from the data context
	 * 
	 * @param dataContext the data context
	 * @param repo the repository
	 * @param dataSpaceParam the data space data context parameter
	 * @return the data space
	 * @throws OperationException if an error occurs retrieving the data space,
	 *         or if the data space can't be found
	 */
	public static AdaptationHome getDataSpace(
		DataContextReadOnly dataContext,
		Repository repo,
		String dataSpaceParam) throws OperationException
	{
		String dataSpaceId = dataContext.getVariableString(dataSpaceParam);

		AdaptationHome home = repo.lookupHome(HomeKey.forBranchName(dataSpaceId));
		if (home == null)
		{
			throw OperationException.createError("DataSpace " + dataSpaceId + " has not been found");
		}

		return home;
	}

	/**
	 * Get the data space from a workflow service, which uses a session interaction
	 * 
	 * @param interaction the session interaction which contains the parameters for the workflow service
	 * @param repo the repository
	 * @return the data space
	 * @throws OperationException if an error occurs retrieving the data space,
	 *         or if the data space can't be found
	 */
	public static AdaptationHome getDataSpaceFromSessionInteraction(
		SessionInteraction interaction,
		Repository repo) throws OperationException
	{
		ParametersMap paramMap = interaction.getInputParameters();
		if (paramMap == null)
		{
			throw OperationException.createError("No input parameters found for session");
		}
		// Custom services can use a special data space param so check that first
		String dataSpaceId = paramMap.getVariableString(WorkflowConstants.SESSION_PARAM_WORKFLOW_DATA_SPACE);
		if (dataSpaceId == null)
		{
			dataSpaceId = paramMap.getVariableString(WorkflowConstants.SESSION_PARAM_DATA_SPACE);
			if (dataSpaceId == null)
			{
				dataSpaceId = paramMap.getVariableString(WorkflowConstants.SESSION_PARAM_SNAPSHOT);
				if (dataSpaceId == null)
				{
					throw OperationException.createError("Could not find input parameter "
						+ WorkflowConstants.SESSION_PARAM_WORKFLOW_DATA_SPACE + " or "
						+ WorkflowConstants.SESSION_PARAM_DATA_SPACE + " or "
						+ WorkflowConstants.SESSION_PARAM_SNAPSHOT + ".");
				}
			}
		}

		AdaptationHome home = repo.lookupHome(HomeKey.forBranchName(dataSpaceId));
		if (home == null)
		{
			throw OperationException.createError("DataSpace " + dataSpaceId + " has not been found");
		}

		return home;
	}

	/**
	 * Get the data set from the data context
	 * 
	 * @param dataContext the data context
	 * @param repo the repository
	 * @param dataSpaceParam the data space parameter
	 * @return the data set
	 * @throws OperationException if an error occurs retrieving the data set,
	 *         or if the data set can't be found
	 */
	public static Adaptation getDataSet(
		DataContextReadOnly dataContext,
		Repository repo,
		String dataSpaceParam) throws OperationException
	{
		AdaptationHome home = getDataSpace(dataContext, repo, dataSpaceParam);
		String dataSet = dataContext.getVariableString(WorkflowConstants.PARAM_DATA_SET);
		Adaptation currentDataSet = home.findAdaptationOrNull(AdaptationName.forName(dataSet));
		if (currentDataSet == null)
		{
			throw OperationException.createError(dataSet + " DataSet has not been found");
		}

		return currentDataSet;
	}

	/**
	 * Get the data set from a workflow service, which uses a session interaction
	 * 
	 * @param interaction the session interaction which contains the parameters for the workflow service
	 * @param repo the repository
	 * @return the data set
	 * @throws OperationException if an error occurs retrieving the data set,
	 *         or if the data set can't be found
	 */
	public static Adaptation getDataSetFromSessionInteraction(
		SessionInteraction interaction,
		Repository repo) throws OperationException
	{
		AdaptationHome home = getDataSpaceFromSessionInteraction(interaction, repo);
		ParametersMap paramMap = interaction.getInputParameters();
		if (paramMap == null)
		{
			throw OperationException.createError("No input parameters found for session");
		}
		// Custom services can use a special data set param so check that first
		String dataSetName = paramMap.getVariableString(WorkflowConstants.SESSION_PARAM_WORKFLOW_DATA_SET);
		if (dataSetName == null)
		{
			dataSetName = paramMap.getVariableString(WorkflowConstants.SESSION_PARAM_DATA_SET);
			if (dataSetName == null)
			{
				throw OperationException.createError("Could not find input parameter "
					+ WorkflowConstants.SESSION_PARAM_WORKFLOW_DATA_SET + " or "
					+ WorkflowConstants.SESSION_PARAM_DATA_SET + ".");
			}
		}
		Adaptation dataSet = home.findAdaptationOrNull(AdaptationName.forName(dataSetName));
		if (dataSet == null)
		{
			throw OperationException.createError(dataSet + " DataSet has not been found");
		}

		return dataSet;
	}

	public static UserReference getCurrentUserReference(
		DataContextReadOnly dataContext,
		Repository repo) throws OperationException
	{
		return getDesiredUserReference(dataContext, repo, WorkflowConstants.PARAM_CURRENT_USER_ID);
	}

	public static UserReference getCurrentApproverReference(
		DataContextReadOnly dataContext,
		Repository repo) throws OperationException
	{
		return getDesiredUserReference(
			dataContext,
			repo,
			WorkflowConstants.PARAM_CURRENT_APPROVER_ID);
	}

	public static UserReference getDesiredUserReference(
		DataContextReadOnly dataContext,
		Repository repo,
		String paramUserId) throws OperationException
	{
		String currentUserid = dataContext.getVariableString(paramUserId);
		if (currentUserid == null)
		{
			return null;
		}
		return getUserReference(currentUserid, repo);
	}

	public static UserReference getUserReference(String userid, Repository repo)
		throws OperationException
	{
		if (userid == null)
		{
			return null;
		}
		UserReference userReference = UserReference.forUser(userid);
		DirectoryHandler directoryHandler = DirectoryHandler.getInstance(repo);
		if (!directoryHandler.isUserDefined(userReference))
		{
			return null;
		}
		return userReference;
	}

	public static void setWorkflowInstanceCreateDateTime(
		DataContext dataContext,
		ProcessExecutionContext processContext)
	{
		if (dataContext.isVariableDefined(WorkflowConstants.PARAM_WORKFLOW_INSTANCE_CREATE_DATE_TIME))
		{
			String workflowInstanceCreateDateTime = dataContext.getVariableString(WorkflowConstants.PARAM_WORKFLOW_INSTANCE_CREATE_DATE_TIME);
			if (workflowInstanceCreateDateTime == null || workflowInstanceCreateDateTime.isEmpty()
				|| workflowInstanceCreateDateTime.equals(WorkflowConstants.DATA_CONTEXT_NULL_VALUE))
			{
				workflowInstanceCreateDateTime = WorkflowConstants.DATA_CONTEXT_DATE_TIME_FORMAT.format(processContext.getProcessInstance()
					.getCreationDate());
				dataContext.setVariableString(
					WorkflowConstants.PARAM_WORKFLOW_INSTANCE_CREATE_DATE_TIME,
					workflowInstanceCreateDateTime);
			}
		}

	}
	public static void setUserTaskCreateDateTime(DataContext context)
	{
		Calendar cal = Calendar.getInstance();
		String userTaskCreateDateTime = WorkflowConstants.DATA_CONTEXT_DATE_TIME_FORMAT.format(cal.getTime());
		context.setVariableString(
			WorkflowConstants.PARAM_USER_TASK_CREATE_DATE_TIME,
			userTaskCreateDateTime);
	}

	/**
	 * Gets the permissions user from the tracking info, assuming it is the first segment of the tracking info
	 * 
	 * @param session the session
	 * @return the user from the tracking info
	 */
	public static String getTrackingInfoPermissionsUser(Session session)
	{
		return getTrackingInfoPermissionsUser(session, new FirstSegmentTrackingInfoHelper(
			WorkflowAccessRule.SEGMENT_WORKFLOW_PERMISSIONS_USERS));
	}

	/**
	 * Gets the permissions user from the tracking info. A tracking info helper is passed in that defines
	 * the structure of the tracking info. It must define a segment with key
	 * <code>WorkflowAccessRule.SEGMENT_WORKFLOW_PERMISSIONS_USER</code>.
	 * 
	 * @param session the session
	 * @param trackingInfoHelper the tracking info helper
	 * @return the user from the tracking info
	 */
	public static String getTrackingInfoPermissionsUser(
		Session session,
		TrackingInfoHelper trackingInfoHelper)
	{
		String trackingInfo = session.getTrackingInfo();
		if (trackingInfo == null)
		{
			return null;
		}
		trackingInfoHelper.initTrackingInfo(trackingInfo);
		return trackingInfoHelper.getTrackingInfoSegment(WorkflowAccessRule.SEGMENT_WORKFLOW_PERMISSIONS_USERS);
	}

	public static boolean isPermissionsUser(Session session)
	{
		return session.isUserInRole(Role.forSpecificRole(ROLE_PERMISSIONS_USER));
	}

	// Returns subworkflows at every level below the given workflow (not just one level down)
	public static List<ProcessInstance> getCurrentSubWorkflows(ProcessInstance processInstance)
	{
		ArrayList<ProcessInstance> allSubworkflows = new ArrayList<ProcessInstance>();
		List<ProcessInstance> subWorkflows = processInstance.getCurrentSubWorkflows();
		for (ProcessInstance subWorkflow : subWorkflows)
		{
			allSubworkflows.add(subWorkflow);
			allSubworkflows.addAll(getCurrentSubWorkflows(subWorkflow));
		}
		return allSubworkflows;
	}

	public static List<WorkItem> getCurrentWorkItems(ProcessInstance processInstance)
	{
		if (processInstance.isCompleted())
		{
			return new ArrayList<WorkItem>();
		}

		@SuppressWarnings("unchecked")
		List<WorkItem> workItems = processInstance.getWorkItems();
		// If the workflow has no work items currently running
		if (workItems.isEmpty())
		{
			ArrayList<WorkItem> allWorkItems = new ArrayList<WorkItem>();
			// Loop through its currently running subworkflows.
			// (A workflow can't have both a running work item and a running subworkflow.)
			List<ProcessInstance> subWorkflows = processInstance.getCurrentSubWorkflows();
			for (ProcessInstance subWorkflow : subWorkflows)
			{
				// If there are any currently running work items in the subworkflow, then
				// add it to the list (will recurse through its subworkflows as well)ß
				List<WorkItem> subWorkflowWorkItems = getCurrentWorkItems(subWorkflow);
				if (!subWorkflowWorkItems.isEmpty())
				{
					allWorkItems.addAll(subWorkflowWorkItems);
				}
			}
			return allWorkItems;
		}
		return workItems;
	}

	public static Set<UserReference> getWorkItemUsers(List<WorkItem> workItems)
	{
		HashSet<UserReference> users = new HashSet<UserReference>();
		for (WorkItem workItem : workItems)
		{
			UserReference user = workItem.getUserReference();
			if (user != null)
			{
				users.add(user);
			}
		}
		return users;
	}

	public static boolean isCompletionCriteriaIgnored()
	{
		String ignoreStr = System.getProperty(PROP_IGNORE_COMPLETION_CRITERIA);
		return ignoreStr != null && "true".equalsIgnoreCase(ignoreStr);
	}

	/**
	 * As of EBX 5.7, work items can be offered to multiple profiles.
	 * So far our code relies on each work item being offered to one and only one profile, which is a role.
	 * This method is used to retrieve that role, assuming the work item passed in conforms to that.
	 * 
	 * @param workItem the work item
	 * @return the role
	 */
	public static Role getWorkItemOfferedToRole(WorkItem workItem)
	{
		Iterator<Profile> iterator = workItem.getOfferedToProfiles().iterator();
		return iterator.hasNext() ? (Role) iterator.next() : null;
	}
}
