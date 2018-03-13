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
import com.orchestranetworks.schema.trigger.BeforeDeleteOccurrenceContext;
import com.orchestranetworks.service.OperationException;

/**
 */
public class LocationLocationTypeGroupTableTrigger extends BaseTableTrigger
{
	private static Adaptation savedLocationRecord;
	private static String adminUser = "admin";

	@Override
	public void handleAfterCreate(AfterCreateOccurrenceContext context) throws OperationException
	{
		Adaptation locationLocationTypeGroupTableRecord = context.getAdaptationOccurrence();

		Adaptation locationRecord = AdaptationUtil.followFK(
			locationLocationTypeGroupTableRecord,
			LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship._Location);

		HtzLocationUtilities.writeToUpdateNotificationTable(
			locationRecord,
			LocationUpdateTypes.LOCATION_CREATE,
			context.getAdaptationOccurrence().toString(),
			context.getProcedureContext());

		//If the User "admin" is doing the load, do not automatically set values
		//	if (context.getSession().isUserInRole(Role.forSpecificRole(HtzConstants.ROLE_TECH_ADMIN)))
		//	{
		//		return;
		//	}

		//If the User "admin" is doing the load, do not automatically set values
		if (adminUser.equals(context.getSession().getUserReference().getUserId()))
		{
			return;
		}

		HtzLocationUtilities.alignIntelligentLocationIdsWithLocationTypes(
			locationRecord,
			context.getProcedureContext(),
			null);

		HtzLocationUtilities
			.updateLocationRecordForIntelligentIds(locationRecord, context.getSession());

		super.handleAfterCreate(context);
	}

	@Override
	public void handleAfterModify(AfterModifyOccurrenceContext context) throws OperationException
	{
		Adaptation locationLocationTypeGroupTableRecord = context.getAdaptationOccurrence();

		Adaptation locationRecord = AdaptationUtil.followFK(
			locationLocationTypeGroupTableRecord,
			LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship._Location);

		HtzLocationUtilities.writeToUpdateNotificationTable(
			locationRecord,
			LocationUpdateTypes.LOCATION_UPDATE,
			context.getAdaptationOccurrence().toString(),
			context.getProcedureContext());

		//If the User "admin" is doing the load, do not automatically set values
		if (adminUser.equals(context.getSession().getUserReference().getUserId()))
		{
			return;
		}

		HtzLocationUtilities.alignIntelligentLocationIdsWithLocationTypes(
			locationRecord,
			context.getProcedureContext(),
			null);

		HtzLocationUtilities
			.updateLocationRecordForIntelligentIds(locationRecord, context.getSession());

		HtzLocationUtilities.setReplicatedBooleanValues(locationRecord, context.getSession());

		super.handleAfterModify(context);
	}

	@Override
	public void handleBeforeDelete(BeforeDeleteOccurrenceContext context) throws OperationException
	{
		//If the User "admin" is doing the load, do not automatically set values
		if (adminUser.equals(context.getSession().getUserReference().getUserId()))
		{
			return;
		}

		Adaptation locationLocationTypeGroupTableRecord = context.getAdaptationOccurrence();

		LocationLocationTypeGroupTableTrigger.savedLocationRecord = AdaptationUtil.followFK(
			locationLocationTypeGroupTableRecord,
			LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship._Location);

		super.handleBeforeDelete(context);
	}

	@Override
	public void handleAfterDelete(AfterDeleteOccurrenceContext context) throws OperationException
	{
		//If the User "admin" is doing the load, do not automatically set values
		if (!adminUser.equals(context.getSession().getUserReference().getUserId()))
		{

			HtzLocationUtilities.alignIntelligentLocationIdsWithLocationTypes(
				LocationLocationTypeGroupTableTrigger.savedLocationRecord,
				context.getProcedureContext(),
				null);

			HtzLocationUtilities
				.updateLocationRecordForIntelligentIds(savedLocationRecord, context.getSession());

			HtzLocationUtilities
				.setReplicatedBooleanValues(savedLocationRecord, context.getSession());
		}

		super.handleAfterDelete(context);

		ValueContext valueContext = context.getOccurrenceContext();

		Adaptation locationRecord = AdaptationUtil.followFK(
			valueContext,
			LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship._Location);

		HtzLocationUtilities.writeToUpdateNotificationTable(
			locationRecord,
			LocationUpdateTypes.LOCATION_DELETE,
			valueContext.toString(),
			context.getProcedureContext());
	}
}
