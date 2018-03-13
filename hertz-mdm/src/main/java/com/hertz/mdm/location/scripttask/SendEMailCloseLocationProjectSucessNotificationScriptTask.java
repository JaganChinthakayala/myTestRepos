/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.scripttask;

import com.hertz.mdm._hertz.constants.HtzWorkflowConstants;
import com.hertz.mdm._hertz.enums.ProjectTypeEvent;
import com.hertz.mdm.location.util.HtzLocationUtilities;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.workflow.ScriptTask;
import com.orchestranetworks.workflow.ScriptTaskContext;

public class SendEMailCloseLocationProjectSucessNotificationScriptTask extends ScriptTask
{
	@Override
	public void executeScript(ScriptTaskContext context) throws OperationException
	{
		String domain = context.getVariableString(HtzWorkflowConstants.DATA_CONTEXT_DOMAIN);
		String projectType = context
			.getVariableString(HtzWorkflowConstants.DATA_CONTEXT_PROJECT_TYPE);

		try
		{
			HtzLocationUtilities.sendLocationEmailNotification(
				context,
				domain,
				projectType,
				ProjectTypeEvent.PROJECT_TYPE_EVENT_SUCCESS);
		}
		catch (Exception e)
		{
			//Throw exception causing the rest of the emails from being sent.
			throw new RuntimeException("", e);
		}
	}
}
