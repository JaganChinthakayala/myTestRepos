/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.trigger;

import com.hertz.mdm.common.trigger.HtzSubjectTableTrigger;
import com.orchestranetworks.ps.trigger.TriggerActionValidator;
import com.orchestranetworks.ps.trigger.TriggerActionValidator.TriggerAction;

/**
 */
public abstract class LocationSubjectTableTrigger extends HtzSubjectTableTrigger
{
	@Override
	protected TriggerActionValidator createTriggerActionValidator(TriggerAction action)
	{
		TriggerActionValidator nestedTriggerActionValidator = super.createTriggerActionValidator(
			action);
		return new LocationSubjectRecordMatchesTriggerActionValidator(
			null,
			nestedTriggerActionValidator);
	}
}
