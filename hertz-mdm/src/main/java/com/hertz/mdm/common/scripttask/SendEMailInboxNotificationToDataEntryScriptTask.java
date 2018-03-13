/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.common.scripttask;

import com.hertz.mdm._hertz.constants.HtzWorkflowConstants;
import com.hertz.mdm._hertz.util.HtzUtilities;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.workflow.ScriptTask;
import com.orchestranetworks.workflow.ScriptTaskContext;

public class SendEMailInboxNotificationToDataEntryScriptTask extends ScriptTask
{
	@Override
	public void executeScript(ScriptTaskContext context) throws OperationException
	{
		HtzUtilities.sendEmailInboxNotificationToDataEntry(
			context,
			context.getVariableString(HtzWorkflowConstants.DATA_CONTEXT_DOMAIN),
			context.getVariableString(HtzWorkflowConstants.DATA_CONTEXT_PROJECT_TYPE));
	}
}
