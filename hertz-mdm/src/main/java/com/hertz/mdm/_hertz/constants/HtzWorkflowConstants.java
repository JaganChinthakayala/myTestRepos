/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm._hertz.constants;

import com.orchestranetworks.ps.project.workflow.ProjectWorkflowConstants;
import com.orchestranetworks.ps.workflow.WorkflowConstants;

/**
 * This class is different from {@link HtzProjectWorkflowConstants} because it is used by non-project-related workflows
 * and extends {@link WorkflowConstants} rather than {@link ProjectWorkflowConstants}
 */
public class HtzWorkflowConstants extends WorkflowConstants
{
	//Hertz Standard Data Context Variables
	public static final String DATA_CONTEXT_PARM_DATA_SET = "dataSet";
	public static final String DATA_CONTEXT_PARM_RECORD = "record";
	public static final String DATA_CONTEXT_PARM_WORKING_DATA_SPACE = "workingDataSpace";
	public static final String DATA_CONTEXT_PARM_MASTER_DATA_SPACE = "masterDataSpace";
	public static final String DATA_CONTEXT_PARM_CURRENT_USER_ID = "currentUserId";
	public static final String DATA_CONTEXT_PARM_RECORD_NAME_VALUE = "recordNameValue";
	public static final String DATA_CONTEXT_PARM_TASK_CREATE_DATE_TIME = "userTaskCreateDateTime";
	public static final String DATA_CONTEXT_PARM_CURENT_USER_LABEL = "currentUserLabel";
	public static final String DATA_CONTEXT_PARM_CURENT_APPROVER_ID = "currentApproverId";
	public static final String DATA_CONTEXT_PARM_WORKFLOW_INSTANCE_CREATE_DATE_TIME = "workflowInstanceCreateDateTime";
	public static final String DATA_CONTEXT_PARM_XPATH_TO_TABLE = "xpathToTable";
	public static final String DATA_CONTEXT_PARM_IS_CANCELLED = "isCancelled";
	public static final String DATA_CONTEXT_PROJECT_TEAM_MEMBERS = "projectTeamMembers";
	public static final String DATA_CONTEXT_PROJECT_TYPE = "projectType";
	public static final String DATA_CONTEXT_PROJECT_NAME = "projectName";
	public static final String DATA_CONTEXT_DOMAIN = "domain";
	public static final String DATA_CONTEXT_APPROVAL_ROLES = "approvalRoles";
	public static final String DATA_CONTEXT_PROJECT_USER_TASK_LOG_ID = "projectUserTaskLogId";
	public static final String DATA_CONTEXT_USER_TASK_NAME = "userTaskName";
	public static final String DATA_CONTEXT_LAUNCH_LOCATION_ID = "launchLocationId";

	//Required for PUSH logic
	public static final String DATA_CONTEXT_PARM_DELTA_RESULT = "deltaResult";

	public static final String WF_KEY_SEND_EMAIL_NOTIFICATION = "";

	public static final String WF_KEY_DATA_STEWARD_MASTER_UPDATE_SUFFIX = "DataStewardMasterUpdate";
	public static final String WF_KEY_MAINTAIN_REFERENCE_DATA_SUFFIX = "MaintainReferenceData";

	//Project Message Variables
	public static final String WF_MESSAGE_VARIABLE_START_DELIMITER = "</";
	public static final String WF_MESSAGE_VARIABLE_END_DELIMITER = ">";

	//Email Notification Variables
	public static final String WF_MESSAGE_VARIABLE_TODAYSDATE = "TODAYSDATE";
	public static final String WF_MESSAGE_VARIABLE_CANCEL_REASON = "CANCELREASON";
	public static final String WF_MESSAGE_VARIABLE_TABLE = "TABLE";
	public static final String WF_MESSAGE_VARIABLE_USER = "USER";
	public static final String WF_MESSAGE_VARIABLE_APPROVER = "APPROVER";
	public static final String WF_MESSAGE_VARIABLE_RECORD_NAME = "RECORDNAME";
	public static final String WF_MESSAGE_VARIABLE_PROJECT_TYPE = "PROJECTTYPE";
	public static final String WF_MESSAGE_VARIABLE_DOMAIN = "DOMAIN";

	protected HtzWorkflowConstants()
	{
		// do nothing
	}
}
