/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.scheduledtask;

import com.hertz.mdm.location.constants.LocationProjectWorkflowConstants;
import com.orchestranetworks.scheduler.ScheduledExecutionContext;
import com.orchestranetworks.scheduler.ScheduledTask;
import com.orchestranetworks.scheduler.ScheduledTaskInterruption;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.workflow.ProcessLauncher;
import com.orchestranetworks.workflow.PublishedProcessKey;
import com.orchestranetworks.workflow.WorkflowEngine;

public class ExecuteSendEscalationEMailScheduledTask extends ScheduledTask
{
	@Override
	public void execute(ScheduledExecutionContext context)
		throws OperationException, ScheduledTaskInterruption
	{
		{
			WorkflowEngine workflowEngine = WorkflowEngine
				.getFromRepository(context.getRepository(), context.getSession());
			ProcessLauncher launcher = workflowEngine.getProcessLauncher(
				PublishedProcessKey
					.forName(LocationProjectWorkflowConstants.WF_KEY_SEND_ESCALATION_EMAILS));
			// set input parameters is any
			//launcher.setInputParameter("name", "value");
			launcher.launchProcess();
		}

	}
}
