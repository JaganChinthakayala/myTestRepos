/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm._hertz.trigger;

import java.util.Date;

import com.hertz.mdm.location.path.LocationPaths;
import com.orchestranetworks.ps.trigger.BaseTableTrigger;
import com.orchestranetworks.schema.trigger.NewTransientOccurrenceContext;
import com.orchestranetworks.service.ValueContextForUpdate;

/**
 */
public abstract class HtzReferenceAndRelationshipTableTrigger extends BaseTableTrigger
{
	@Override
	public void handleNewContext(NewTransientOccurrenceContext context)
	{
		super.handleNewContext(context);

		ValueContextForUpdate vc = context.getOccurrenceContextForUpdate();

		vc.setValue(
			new Date(),
			LocationPaths._Root_LocationProject._EffectiveStartEndDates_StartDate);
	}
}
