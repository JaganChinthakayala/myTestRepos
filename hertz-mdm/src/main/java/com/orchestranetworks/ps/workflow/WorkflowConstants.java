/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.workflow;

import java.text.*;

/**
 */
public class WorkflowConstants
{
	public static final String DATA_CONTEXT_NULL_VALUE = "null";

	public static final String PARAM_WORKING_DATA_SPACE = "workingDataSpace";
	public static final String PARAM_RECORD_NAME_VALUE = "recordNameValue";
	public static final String PARAM_CURRENT_USER_ID = "currentUserId";
	public static final String PARAM_CURRENT_USER_LABEL = "currentUserLabel";
	public static final String PARAM_CURRENT_APPROVER_ID = "currentApproverId";;
	public static final String PARAM_XPATH_TO_TABLE = "xpathToTable";
	public static final String PARAM_MASTER_DATA_SPACE = "masterDataSpace";
	public static final String PARAM_RECORD = "record";
	public static final String PARAM_DATA_SET = "dataSet";
	public static final String PARAM_USER_TASK_CREATE_DATE_TIME = "userTaskCreateDateTime";
	public static final String PARAM_WORKFLOW_INSTANCE_CREATE_DATE_TIME = "workflowInstanceCreateDateTime";
	public static final String PARAM_CURRENT_PERMISSIONS_USER = "currentPermissionsUser";
	public static final String PARAM_ADDITIONAL_NOTIFICATION_INFO = "additionalNotificationInfo";

	public static final String SESSION_PARAM_CREATED = "created";
	public static final String SESSION_PARAM_XPATH = "xpath";
	public static final String SESSION_PARAM_RECORD = PARAM_RECORD;
	public static final String SESSION_PARAM_DATA_SPACE = "branch";
	public static final String SESSION_PARAM_SNAPSHOT = "version";
	public static final String SESSION_PARAM_DATA_SET = "instance";
	// These can be used by custom services when the built-in params are already
	// in use
	public static final String SESSION_PARAM_WORKFLOW_RECORD = "workflowRecord";
	public static final String SESSION_PARAM_WORKFLOW_DATA_SPACE = "workflowDataSpace";
	public static final String SESSION_PARAM_WORKFLOW_DATA_SET = "workflowDataSet";

	public static final String DATA_CONTEXT_DATE_TIME_FORMAT_STRING = "MM/dd/yyyy 'at' HH:mm:ss";
	public static final DateFormat DATA_CONTEXT_DATE_TIME_FORMAT = new SimpleDateFormat(
		DATA_CONTEXT_DATE_TIME_FORMAT_STRING);

	public static final String ROLE_WORKFLOW_MANAGER = "Workflow Manager";
	public static final String ROLE_WORKFLOW_MONITOR = "Workflow Monitor";

	protected WorkflowConstants()
	{
		// do nothing
	}
}
