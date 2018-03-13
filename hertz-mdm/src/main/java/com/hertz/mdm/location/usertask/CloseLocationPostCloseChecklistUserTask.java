/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.usertask;

import com.hertz.mdm.location.path.LocationPaths;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.workflow.UserTaskBeforeWorkItemCompletionContext;
/**
 */
public class CloseLocationPostCloseChecklistUserTask extends CloseLocationProjectMaintenanceUserTask
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

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._CloseLocation_Checklists_PostCloseChecklist_HasRentalCounterBeenHandled,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._CloseLocation_Checklists_PostCloseChecklist_HasFunitureBeenHandled,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._CloseLocation_Checklists_PostCloseChecklist_HaveNotifiedTreasuryOfClosingDepositoryAccount,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._CloseLocation_Checklists_PostCloseChecklist_HaveCompletedFormsAndDocuments,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._CloseLocation_Checklists_PostCloseChecklist_HaveHandledDispositionOfEmployees,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._CloseLocation_Checklists_PostCloseChecklist_RequestedCancellationOfFuelWashCards,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._CloseLocation_Checklists_PostCloseChecklist_RequestedCancellationOfGenericEmailAddress,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._CloseLocation_Checklists_PostCloseChecklist_HasManagerFundBeenHandled,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._CloseLocation_Checklists_PostCloseChecklist_HavePettyCashChecksBeenHandled,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._CloseLocation_Checklists_PostCloseChecklist_HaveBadgesBeenCollectedAndDestroyed,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._CloseLocation_Checklists_PostCloseChecklist_HasPurchasingCardBeenHandled,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._CloseLocation_Checklists_PostCloseChecklist_HaveRepeatingPOsBeenHandled,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._CloseLocation_Checklists_PostCloseChecklist_HaveDrawdownPOsBeenHandled,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._CloseLocation_Checklists_PostCloseChecklist_HasShreddingServicesBeenHandled,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._CloseLocation_Checklists_PostCloseChecklist_HaveRadiosAndCellPhonesBeenHandled,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._CloseLocation_Checklists_PostCloseChecklist_HaveTemporarySignageMapBeenHandled,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._CloseLocation_Checklists_PostCloseChecklist_HaveWorksCompIssuesBeenHandled,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._CloseLocation_Checklists_PostCloseChecklist_HaveManualRentalAgreementsBeenHandled,
				true,
				true);
		}
		super.checkBeforeWorkItemCompletion(context);
	}
}