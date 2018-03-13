/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.usertask;

import com.hertz.mdm.common.util.HtzCommonUtilities;
import com.hertz.mdm.location.path.LocationPaths;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.RequestResult;
import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.workflow.UserTaskBeforeWorkItemCompletionContext;
/**
 */
public class MoveLocationProjectMaintenanceUserTask extends LocationProjectMaintenanceUserTask
{
	@Override
	public void checkBeforeWorkItemCompletion(UserTaskBeforeWorkItemCompletionContext context)
	{
		if (isCompletionCriteriaIgnored())
		{
			return;
		}

		if (context.isAcceptAction())
		{
			RequestResult requestResult = null;

			Adaptation projectRecord = getProjectRecordFromUserTaskBeforeWorkItemCompletionContext(
				context);

			if (projectRecord == null)
			{
				return;
			}

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._MoveLocation_ExistingLocation_ProjectedCloseDate,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._MoveLocation_ExistingLocation_IsFacilitiesInvolvedInRestoration,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._MoveLocation_ExistingLocation_IsTempCloseRequired,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._MoveLocation_ExistingLocation_DoesLocationHaveCopiersPrinters,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._MoveLocation_ExistingLocation_ProjectedCloseDate,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._MoveLocation_NewLocation_EnvironmentalAffairs_VehicleOilChangesOnSite,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._MoveLocation_NewLocation_EnvironmentalAffairs_VehiclesRefueledOnSite,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._MoveLocation_NewLocation_EnvironmentalAffairs_VehiclesWashedOnsite,
				true,
				true);

			String locationProjectId = projectRecord.get(LocationPaths._Root_LocationProject._Id)
				.toString();

			AdaptationTable locationProjectAddressTable = projectRecord.getContainer()
				.getTable(LocationPaths._Root_ProjectData_Address.getPathInSchema());

			// Query the LocationProjectAddressRelationship table for this Location Project
			requestResult = locationProjectAddressTable.createRequestResult(
				LocationPaths._Root_ProjectData_Address._LocationProject.format() + "='"
					+ locationProjectId + "'");

			if (requestResult.isEmpty())
			{
				context.reportMessage(
					UserMessage
						.createError("A new Location Address must be specified for the Location"));
			}
			else
			{
				Adaptation projectAddressRecord = requestResult.nextAdaptation();

				checkFieldRequired(
					context,
					projectAddressRecord,
					LocationPaths._Root_ProjectData_Address._Address_Country,
					true,
					true);
				checkFieldRequired(
					context,
					projectAddressRecord,
					LocationPaths._Root_ProjectData_Address._Address_City,
					true,
					true);
				if (HtzCommonUtilities.countryHasStatesOrProvinces(
					projectAddressRecord,
					LocationPaths._Root_ProjectData_Address._Address_Country))
				{
					checkFieldRequired(
						context,
						projectAddressRecord,
						LocationPaths._Root_ProjectData_Address._Address_StateProvince,
						true,
						true);
				}
				checkFieldRequired(
					context,
					projectAddressRecord,
					LocationPaths._Root_ProjectData_Address._Address_PostalCode,
					true,
					true);
			}
		}
		super.checkBeforeWorkItemCompletion(context);
	}
}
