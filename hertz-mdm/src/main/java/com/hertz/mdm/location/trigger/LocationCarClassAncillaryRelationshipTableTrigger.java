/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.trigger;

import com.orchestranetworks.ps.trigger.BaseTableTrigger;

/**
 */
public class LocationCarClassAncillaryRelationshipTableTrigger extends BaseTableTrigger
{
	/*
	@Override
	public void handleAfterCreate(AfterCreateOccurrenceContext context) throws OperationException
	{
		Adaptation locationRecord = AdaptationUtil.followFK(
			context.getAdaptationOccurrence(),
			LocationPaths._Root_LocationData_LocationCarClassAncillaryRelationship._LocationCarClassRelationship);
	
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
			LocationPaths._Root_LocationData_LocationCarClassAncillaryRelationship._LocationCarClassRelationship);
	
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
	
		Adaptation locationRecord = AdaptationUtil.followFK(
			valueContext,
			LocationPaths._Root_LocationData_LocationCarClassAncillaryRelationship._LocationCarClassRelationship);
	
		HtzLocationUtilities.writeToUpdateNotificationTable(
			locationRecord,
			LocationUpdateTypes.LOCATION_DELETE,
			valueContext.toString(),
			context.getProcedureContext());
	}
	*/
}
