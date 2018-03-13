/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.scripttask;

import com.hertz.mdm._hertz.constants.HtzWorkflowConstants;
import com.hertz.mdm.location.constants.LocationConstants;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.workflow.ScriptTask;
import com.orchestranetworks.workflow.ScriptTaskContext;

public class DetermineRequiredApprovalsForProjectedCloseDateChangeScriptTask extends ScriptTask
{
	@Override
	public void executeScript(ScriptTaskContext context) throws OperationException
	{
		String delimiter = "";
		String approvalRoles = "";
		approvalRoles = approvalRoles + delimiter
			+ LocationConstants.APPROVAL_ROLE_REGIONAL_VICE_PRESIDENT;

		context.setVariableString(HtzWorkflowConstants.DATA_CONTEXT_APPROVAL_ROLES, approvalRoles);

		return;
	}
}
