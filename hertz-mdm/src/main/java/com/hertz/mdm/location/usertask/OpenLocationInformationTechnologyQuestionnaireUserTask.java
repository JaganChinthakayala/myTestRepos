/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.usertask;

import com.hertz.mdm._hertz.enums.Domains;
import com.hertz.mdm.location.path.LocationPaths;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.workflow.UserTaskBeforeWorkItemCompletionContext;
/**
 */
public class OpenLocationInformationTechnologyQuestionnaireUserTask
	extends
	OpenLocationProjectMaintenanceUserTask
{
	@Override
	protected String getDomain()
	{
		return Domains.LOCATION;
	}

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
				LocationPaths._Root_LocationProject._InformationTechnology_NewEquipmentRequired_CounterWorkstationCount,
				true,
				true);
			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._InformationTechnology_NewEquipmentRequired_BackOfficePCCount,
				true,
				true);
			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._InformationTechnology_NewEquipmentRequired_MotionTabletCount,
				true,
				true);
			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._InformationTechnology_NewEquipmentRequired_PrinterScannerCount,
				true,
				true);
			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._InformationTechnology_NewEquipmentRequired_PrinterScannerCount,
				true,
				true);
			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._InformationTechnology_NewEquipmentRequired_InformationMonitorsVideoWallCount,
				true,
				true);
			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._InformationTechnology_NewEquipmentRequired_ExpressRentKioskCount,
				true,
				true);
			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._InformationTechnology_NewEquipmentRequired_ComboPCCount,
				true,
				true);
			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._InformationTechnology_ReuseOfExistingEquipment_ResuedSignatureCapturePadCount,
				true,
				true);
			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._InformationTechnology_ReuseOfExistingEquipment_ReusedWhiteASAPCardReaderCount,
				true,
				true);
			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._InformationTechnology_ReuseOfExistingEquipment_ReusedBlackHLECardReaderCount,
				true,
				true);
			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._InformationTechnology_NetworkPhones_BusinessAllowsUseOfNetwork,
				true,
				true);
			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._InformationTechnology_NetworkPhones_VoiceLineCount,
				true,
				true);
			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._InformationTechnology_NetworkPhones_CordedPhoneCount,
				true,
				true);
			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._InformationTechnology_NetworkPhones_CordlessPhoneCount,
				true,
				true);
			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._InformationTechnology_NetworkPhones_IsVoicemailRequired,
				true,
				true);
			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._InformationTechnology_NetworkPhones_IsAutomatedAttendantRequired,
				true,
				true);
			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._InformationTechnology_NetworkPhones_IsDialerPhoneRequired,
				true,
				true);
			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._InformationTechnology_NetworkPhones_IsNewDataLineRequired,
				true,
				true);
			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._InformationTechnology_AdditionalNetworking_AdditionalDataDropCount,
				true,
				true);
			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._InformationTechnology_AdditionalNetworking_ConferenceRoomCount,
				true,
				true);
			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._InformationTechnology_AdditionalNetworking_TabletUsageArea,
				true,
				true);
			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._InformationTechnology_AdditionalNetworking_TabletUsageAreaUom,
				true,
				true);
			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._InformationTechnology_AdditionalNetworking_DoesSiteRequireWiTech,
				true,
				true);
			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._InformationTechnology_AdditionalNetworking_PhoneLinesRequiredForAlarmFire,
				true,
				true);
			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._InformationTechnology_AdditionalNetworking_FaxLineCount,
				true,
				true);
		}
		super.checkBeforeWorkItemCompletion(context);
	}
}
