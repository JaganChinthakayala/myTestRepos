/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.trigger;

import com.hertz.mdm.location.enums.LocationUpdateTypes;
import com.hertz.mdm.location.path.LocationPaths;
import com.hertz.mdm.location.util.HtzLocationUtilities;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.ps.trigger.BaseTableTrigger;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.schema.trigger.AfterCreateOccurrenceContext;
import com.orchestranetworks.schema.trigger.AfterDeleteOccurrenceContext;
import com.orchestranetworks.schema.trigger.AfterModifyOccurrenceContext;
import com.orchestranetworks.service.OperationException;

/**
 */
public class LocationPhoneTableTrigger extends BaseTableTrigger
{
	@Override
	public void handleAfterCreate(AfterCreateOccurrenceContext context) throws OperationException
	{
		Adaptation locationRecord = AdaptationUtil.followFK(
			context.getAdaptationOccurrence(),
			LocationPaths._Root_LocationData_Phone._Location);

		HtzLocationUtilities.writeToUpdateNotificationTable(
			locationRecord,
			LocationUpdateTypes.LOCATION_CREATE,
			context.getAdaptationOccurrence().toString(),
			context.getProcedureContext());

		super.handleAfterCreate(context);
	}

	@Override
	public void handleAfterModify(AfterModifyOccurrenceContext context) throws OperationException
	{
		Adaptation locationRecord = AdaptationUtil.followFK(
			context.getAdaptationOccurrence(),
			LocationPaths._Root_LocationData_Phone._Location);

		HtzLocationUtilities.writeToUpdateNotificationTable(
			locationRecord,
			LocationUpdateTypes.LOCATION_UPDATE,
			context.getAdaptationOccurrence().toString(),
			context.getProcedureContext());

		super.handleAfterModify(context);
	}

	@Override
	public void handleAfterDelete(AfterDeleteOccurrenceContext context) throws OperationException
	{
		super.handleAfterDelete(context);

		ValueContext valueContext = context.getOccurrenceContext();

		Adaptation locationRecord = AdaptationUtil
			.followFK(valueContext, LocationPaths._Root_LocationData_Phone._Location);

		HtzLocationUtilities.writeToUpdateNotificationTable(
			locationRecord,
			LocationUpdateTypes.LOCATION_DELETE,
			valueContext.toString(),
			context.getProcedureContext());
	}
}
