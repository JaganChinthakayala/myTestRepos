/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.usertask;

import java.util.Date;

import com.hertz.mdm._hertz.constants.HtzConstants;
import com.hertz.mdm._hertz.constants.HtzWorkflowConstants;
import com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.repository.RepositoryUtils;
import com.hertz.mdm.businessparty.path.BusinessPartyPaths;
import com.hertz.mdm.location.path.LocationPaths;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.RequestResult;
import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.workflow.UserTaskBeforeWorkItemCompletionContext;
/**
 */
public class CloseLocationProjectMaintenanceUserTask extends LocationProjectMaintenanceUserTask
{
	protected boolean locationHasDependentChildren = false;

	@Override
	public void checkBeforeWorkItemCompletion(UserTaskBeforeWorkItemCompletionContext context)
	{
		if (isCompletionCriteriaIgnored())
		{
			return;
		}

		if (context.isAcceptAction())
		{
			//If Closing a Location that has Dependent Children, do not need to check exit criteria
			if (!locationHasDependentChildren)
			{
				RequestResult requestResult = null;

				Adaptation projectRecord = getProjectRecordFromUserTaskBeforeWorkItemCompletionContext(
					context);

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

				//Location Project Record Exit Criteria

				checkFieldRequired(
					context,
					projectRecord,
					LocationPaths._Root_LocationProject._CloseLocation_ProjectedCloseDate,
					true,
					true);

				checkFieldRequired(
					context,
					projectRecord,
					LocationPaths._Root_LocationProject._CloseLocation_LeaseTerminationDate,
					true,
					true);

				Date projectedCloseDate = projectRecord
					.getDate(LocationPaths._Root_LocationProject._CloseLocation_ProjectedCloseDate);
				Date leaseTerminationDate = projectRecord.getDate(
					LocationPaths._Root_LocationProject._CloseLocation_LeaseTerminationDate);

				if (projectedCloseDate != null && leaseTerminationDate != null
					&& !leaseTerminationDate.after(projectedCloseDate))
				{
					context.reportMessage(
						UserMessage.createError(
							"The Lease Termination Date must be later than the Projected Close Date"));
				}

				if (projectRecord
					.get(LocationPaths._Root_LocationProject._CloseLocation_IsGoingDark) == null)
				{
					checkFieldRequired(
						context,
						projectRecord,
						LocationPaths._Root_LocationProject._CloseLocation_IsGoingDark,
						true,
						true);
				}
				else if (projectRecord
					.get_boolean(LocationPaths._Root_LocationProject._CloseLocation_IsGoingDark))
				{
					if (projectedCloseDate == null)
					{
						context.reportMessage(
							UserMessage.createError(
								"Projected Close Date required if Location is Going Dark"));
					}
				}

				checkFieldRequired(
					context,
					projectRecord,
					LocationPaths._Root_LocationProject._CloseLocation_ReasonForClosing,
					true,
					true);

				checkFieldRequired(
					context,
					projectRecord,
					LocationPaths._Root_LocationProject._CloseLocation_ClosestRentalLocation,
					true,
					true);

				if (projectRecord
					.get(LocationPaths._Root_LocationProject._ProjectOriginator) == null)
				{
					String currentUserName = context
						.getVariableString(HtzWorkflowConstants.DATA_CONTEXT_PARM_CURRENT_USER_ID);

					AdaptationTable businessPartyTable = RepositoryUtils.getTable(
						RepositoryUtils.getDataSet(
							HtzConstants.BUSINESS_PARTY_DATA_SPACE,
							HtzConstants.BUSINESS_PARTY_DATA_SET),
						BusinessPartyPaths._Root_BusinessParty.getPathInSchema().format());

					// Query the Business Party Table to get this User's User Id
					requestResult = businessPartyTable.createRequestResult(
						BusinessPartyPaths._Root_BusinessParty._EbxUser.format() + "='"
							+ currentUserName + "' and"
							+ BusinessPartyPaths._Root_BusinessParty._IsRecordActive.format()
							+ "=true");

					if (requestResult.isEmpty())
					{
						context.reportMessage(
							UserMessage.createError(
								"A Project Originator could not be set for this User"));
					}

				}
			}
		}
		super.checkBeforeWorkItemCompletion(context);
	}
}
