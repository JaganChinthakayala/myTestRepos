/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.scripttask;

import com.hertz.mdm._hertz.constants.HtzWorkflowConstants;
import com.hertz.mdm.location.constants.LocationConstants;
import com.hertz.mdm.location.path.LocationPaths;
import com.hertz.mdm.location.workflow.LocationProjectWorkflowUtilities;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.workflow.ScriptTask;
import com.orchestranetworks.workflow.ScriptTaskContext;

public class DetermineRequiredApprovalsForLocationMoveScriptTask extends ScriptTask
{
	@Override
	public void executeScript(ScriptTaskContext context) throws OperationException
	{
		String delimiter = "";
		String approvalRoles = "";
		approvalRoles = approvalRoles + delimiter
			+ LocationConstants.APPROVAL_ROLE_REGIONAL_VICE_PRESIDENT;
		delimiter = "&";
		approvalRoles = approvalRoles + delimiter
			+ LocationConstants.APPROVAL_ROLE_REGIONAL_VICE_FINANCE;

		Adaptation projectRecord = LocationProjectWorkflowUtilities
			.getLocationProjectRecord(context, context.getRepository());

		if (projectRecord == null)
		{
			return;
		}

		Adaptation locationRecord = AdaptationUtil
			.followFK(projectRecord, LocationPaths._Root_LocationProject._Location);

		if (locationRecord == null)
		{
			return;
		}

		if (locationRecord.get_boolean(LocationPaths._Root_Location._IsOnAirport))
		{
			approvalRoles = approvalRoles + delimiter + LocationConstants.APPROVAL_ROLE_CONCESSIONS;
		}
		else if (locationRecord.get_boolean(LocationPaths._Root_Location._IsCarSalesLocation))
		{
			approvalRoles = approvalRoles + delimiter + LocationConstants.APPROVAL_ROLE_CAR_SALES;
		}
		else
		{
			approvalRoles = approvalRoles + delimiter + LocationConstants.APPROVAL_ROLE_REAL_ESTATE;
		}

		context.setVariableString(HtzWorkflowConstants.DATA_CONTEXT_APPROVAL_ROLES, approvalRoles);

		return;
	}
}
