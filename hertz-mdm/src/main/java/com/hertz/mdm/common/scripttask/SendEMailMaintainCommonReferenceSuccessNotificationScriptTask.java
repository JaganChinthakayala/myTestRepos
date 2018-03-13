/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.common.scripttask;

import java.util.ArrayList;

import com.hertz.mdm._hertz.constants.HtzWorkflowConstants;
import com.hertz.mdm._hertz.util.HtzUtilities;
import com.hertz.mdm.vehicle.util.HtzVehicleUtilities;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.workflow.ScriptTask;
import com.orchestranetworks.workflow.ScriptTaskContext;

public class SendEMailMaintainCommonReferenceSuccessNotificationScriptTask extends ScriptTask
{
	@Override
	public void executeScript(ScriptTaskContext context) throws OperationException
	{
		String projectTypeEvent = "Success";

		String domain = context.getVariableString(HtzWorkflowConstants.DATA_CONTEXT_DOMAIN);
		String projectType = context
			.getVariableString(HtzWorkflowConstants.DATA_CONTEXT_PROJECT_TYPE);
		String currentUserId = context
			.getVariableString(HtzWorkflowConstants.PARAM_CURRENT_USER_ID);

		ArrayList<String> emailDistributionList = new ArrayList<String>();

		String emailAddress = HtzUtilities.getPrimaryEMailAddressforUserId(currentUserId);

		if (emailAddress != null && !emailAddress.equals(""))
		{
			emailDistributionList.add(emailAddress);
		}

		try
		{
			HtzVehicleUtilities.sendVehicleEmailNotification(
				context,
				domain,
				projectType,
				projectTypeEvent,
				emailDistributionList);
		}
		catch (Exception e)
		{
			//Throw exception causing the rest of the emails from being sent.
			throw new RuntimeException("", e);
		}
	}
}
