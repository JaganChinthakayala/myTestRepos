/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.condition;

import java.util.Date;

import com.hertz.mdm.location.path.LocationPaths;
import com.hertz.mdm.location.workflow.LocationProjectWorkflowUtilities;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.workflow.Condition;
import com.orchestranetworks.workflow.ConditionContext;

public class HasLocationProjectExpiredCondition extends Condition
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

		Date expirationDate = projectRecord
			.getDate(LocationPaths._Root_LocationProject._ProjectExpirationDate);

		if (expirationDate == null)
		{
			return false;
		}

		return expirationDate.before(new Date());
	}
}
