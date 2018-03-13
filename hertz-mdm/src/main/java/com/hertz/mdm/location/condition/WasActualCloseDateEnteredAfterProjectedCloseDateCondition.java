/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.condition;

import java.util.Date;

import com.hertz.mdm.location.path.LocationPaths;
import com.hertz.mdm.location.workflow.LocationProjectWorkflowUtilities;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.workflow.Condition;
import com.orchestranetworks.workflow.ConditionContext;

public class WasActualCloseDateEnteredAfterProjectedCloseDateCondition extends Condition
{
	@Override
	public boolean evaluateCondition(ConditionContext context) throws OperationException
	{
		Adaptation projectRecord = LocationProjectWorkflowUtilities
			.getLocationProjectRecord(context, context.getRepository());

		if (projectRecord == null)
		{
			return false;
		}

		Adaptation locationRecord = AdaptationUtil
			.followFK(projectRecord, LocationPaths._Root_LocationProject._Location);

		if (locationRecord == null)
		{
			return false;
		}

		Date actualCloseDate = locationRecord
			.getDate(LocationPaths._Root_Location._OpenCloseDate_EndDate);

		if (actualCloseDate == null)
		{
			return false;
		}

		return actualCloseDate.after(new Date());
	}
}
