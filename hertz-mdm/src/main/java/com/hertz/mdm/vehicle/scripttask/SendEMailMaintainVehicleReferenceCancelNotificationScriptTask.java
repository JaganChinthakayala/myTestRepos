/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.vehicle.scripttask;

import java.util.ArrayList;

import com.hertz.mdm._hertz.constants.HtzWorkflowConstants;
import com.hertz.mdm._hertz.util.HtzUtilities;
import com.hertz.mdm.vehicle.util.HtzVehicleUtilities;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.workflow.ScriptTask;
import com.orchestranetworks.workflow.ScriptTaskContext;

public class SendEMailMaintainVehicleReferenceCancelNotificationScriptTask extends ScriptTask
{
	@Override
	public void executeScript(ScriptTaskContext context) throws OperationException
	{
		String projectTypeEvent = "Cancel";

		String domain = context.getVariableString(HtzWorkflowConstants.DATA_CONTEXT_DOMAIN);
		String projectType = context
			.getVariableString(HtzWorkflowConstants.DATA_CONTEXT_PROJECT_TYPE);
		String currentApproverId = context
			.getVariableString(HtzWorkflowConstants.PARAM_CURRENT_APPROVER_ID);

		ArrayList<String> distibutionList = new ArrayList<String>();

		String emailAddress = HtzUtilities.getPrimaryEMailAddressforUserId(currentApproverId);

		if (emailAddress != null && !emailAddress.equals(""))
		{
			distibutionList.add(emailAddress);
		}

		try
		{
			HtzVehicleUtilities.sendVehicleEmailNotification(
				context,
				domain,
				projectType,
				projectTypeEvent,
				distibutionList);
		}
		catch (Exception e)
		{
			//Throw exception causing the rest of the emails from being sent.
			throw new RuntimeException("", e);
		}
	}
}
