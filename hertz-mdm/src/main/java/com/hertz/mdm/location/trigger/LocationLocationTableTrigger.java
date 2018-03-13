/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.trigger;

import java.util.HashMap;

import com.hertz.mdm.location.constants.LocationConstants;
import com.hertz.mdm.location.enums.LocationUpdateTypes;
import com.hertz.mdm.location.path.LocationPaths;
import com.hertz.mdm.location.util.HtzLocationUtilities;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.ps.procedure.ModifyValuesProcedure;
import com.orchestranetworks.ps.trigger.BaseTableTrigger;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.trigger.AfterCreateOccurrenceContext;
import com.orchestranetworks.schema.trigger.AfterDeleteOccurrenceContext;
import com.orchestranetworks.schema.trigger.AfterModifyOccurrenceContext;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.ProcedureContext;

/**
 */
public class LocationLocationTableTrigger extends BaseTableTrigger
{
	@Override
	public void handleAfterCreate(AfterCreateOccurrenceContext context) throws OperationException
	{
		super.handleAfterCreate(context);

		Adaptation locationLocationRelationshipRecord = context.getAdaptationOccurrence();

		String relationshipType = locationLocationRelationshipRecord.getString(
			LocationPaths._Root_LocationData_LocationLocationRelationship._LocationRelationshipType);

		Adaptation locationRecord = AdaptationUtil.followFK(
			locationLocationRelationshipRecord,
			LocationPaths._Root_LocationData_LocationLocationRelationship._ParentLocation);

		Adaptation childLocationRecord = AdaptationUtil.followFK(
			locationLocationRelationshipRecord,
			LocationPaths._Root_LocationData_LocationLocationRelationship._SubLocation);

		HtzLocationUtilities.writeToUpdateNotificationTable(
			locationRecord,
			LocationUpdateTypes.LOCATION_CREATE,
			context.getAdaptationOccurrence().toString(),
			context.getProcedureContext());

		HtzLocationUtilities.writeToUpdateNotificationTable(
			childLocationRecord,
			LocationUpdateTypes.LOCATION_CREATE,
			context.getAdaptationOccurrence().toString(),
			context.getProcedureContext());

		if (LocationConstants.LOCATION_RELATIONSHIP_TYPE_SERVEDBY.equals(relationshipType)
			|| LocationConstants.LOCATION_RELATIONSHIP_TYPE_SATELLITE.equals(relationshipType))
		{
			locationRecord = AdaptationUtil.followFK(
				locationLocationRelationshipRecord,
				LocationPaths._Root_LocationData_LocationLocationRelationship._SubLocation);

			Adaptation parentLocationRecord = AdaptationUtil.followFK(
				locationLocationRelationshipRecord,
				LocationPaths._Root_LocationData_LocationLocationRelationship._ParentLocation);

			setRegionAndAreaForServeByAndSaltellite(
				locationRecord,
				parentLocationRecord,
				context.getProcedureContext());
		}
	}

	@Override
	public void handleAfterModify(AfterModifyOccurrenceContext context) throws OperationException
	{
		Adaptation locationLocationRelationshipRecord = context.getAdaptationOccurrence();

		String relationshipType = locationLocationRelationshipRecord.getString(
			LocationPaths._Root_LocationData_LocationLocationRelationship._LocationRelationshipType);

		Adaptation childLocationRecord = AdaptationUtil.followFK(
			locationLocationRelationshipRecord,
			LocationPaths._Root_LocationData_LocationLocationRelationship._SubLocation);

		Adaptation parentLocationRecord = AdaptationUtil.followFK(
			locationLocationRelationshipRecord,
			LocationPaths._Root_LocationData_LocationLocationRelationship._ParentLocation);

		HtzLocationUtilities.writeToUpdateNotificationTable(
			parentLocationRecord,
			LocationUpdateTypes.LOCATION_UPDATE,
			context.getAdaptationOccurrence().toString(),
			context.getProcedureContext());

		HtzLocationUtilities.writeToUpdateNotificationTable(
			childLocationRecord,
			LocationUpdateTypes.LOCATION_UPDATE,
			context.getAdaptationOccurrence().toString(),
			context.getProcedureContext());

		if (LocationConstants.LOCATION_RELATIONSHIP_TYPE_SERVEDBY.equals(relationshipType)
			|| LocationConstants.LOCATION_RELATIONSHIP_TYPE_SATELLITE.equals(relationshipType))
		{
			setRegionAndAreaForServeByAndSaltellite(
				childLocationRecord,
				parentLocationRecord,
				context.getProcedureContext());
		}

		super.handleAfterModify(context);
	}

	@Override
	public void handleAfterDelete(AfterDeleteOccurrenceContext context) throws OperationException
	{
		super.handleAfterDelete(context);

		ValueContext valueContext = context.getOccurrenceContext();

		Adaptation childLocationRecord = AdaptationUtil.followFK(
			valueContext,
			LocationPaths._Root_LocationData_LocationLocationRelationship._SubLocation);

		Adaptation parentLocationRecord = AdaptationUtil.followFK(
			valueContext,
			LocationPaths._Root_LocationData_LocationLocationRelationship._ParentLocation);

		HtzLocationUtilities.writeToUpdateNotificationTable(
			parentLocationRecord,
			LocationUpdateTypes.LOCATION_DELETE,
			valueContext.toString(),
			context.getProcedureContext());

		HtzLocationUtilities.writeToUpdateNotificationTable(
			childLocationRecord,
			LocationUpdateTypes.LOCATION_DELETE,
			valueContext.toString(),
			context.getProcedureContext());
	}

	private static void setRegionAndAreaForServeByAndSaltellite(
		Adaptation locationRecord,
		Adaptation parentLocationRecord,
		ProcedureContext procedureContext)
		throws OperationException
	{
		HashMap<Path, Object> pathValueMapRegionArea = new HashMap<Path, Object>();

		pathValueMapRegionArea.put(
			LocationPaths._Root_Location._LocationInformation_Region,
			parentLocationRecord.get(LocationPaths._Root_Location._LocationInformation_Region));

		pathValueMapRegionArea.put(
			LocationPaths._Root_Location._LocationInformation_Area,
			parentLocationRecord.get(LocationPaths._Root_Location._LocationInformation_Area));

		pathValueMapRegionArea.put(
			LocationPaths._Root_Location._LocationInformation_ReservationArea,
			parentLocationRecord
				.get(LocationPaths._Root_Location._LocationInformation_ReservationArea));

		ModifyValuesProcedure
			.execute(procedureContext, locationRecord, pathValueMapRegionArea, true, false);
	}
}
