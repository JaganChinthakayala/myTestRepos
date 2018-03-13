/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.usertask;

import com.hertz.mdm._hertz.constants.HtzConstants;
import com.hertz.mdm.location.path.LocationPaths;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.workflow.UserTaskBeforeWorkItemCompletionContext;
/**
 */
public class OpenLocationEnterTaxDataUserTask extends OpenLocationProjectMaintenanceUserTask
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
				LocationPaths._Root_LocationProject._Tax_HasBusinessLicenseBeenAppliedFor,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._Tax_HasTaxRegistrationBeenObtained,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._Tax_IsLegalEntity,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._Tax_TransactionFee,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._Tax_VehicleLicenseFee,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._Tax_RentalAgreementTaxRateAssessed,
				true,
				true);

			if (locationRecord.get(
				LocationPaths._Root_Location._LocationInformation__locationStateProvince) != null
				&& locationRecord
					.get(LocationPaths._Root_Location._LocationInformation__locationStateProvince)
					.toString()
					.equals(HtzConstants.STATE_PROVINCE_CODE_CALIFORNIA))
			{
				checkFieldRequired(
					context,
					projectRecord,
					LocationPaths._Root_LocationProject._Tax_TaxRateChange_CaliforniaLocalFuelRate,
					true,
					true);
			}
			else if (projectRecord.get(
				LocationPaths._Root_LocationProject._Tax_TaxRateChange_CaliforniaLocalFuelRate) != null)
			{
				context.reportMessage(
					UserMessage.createError(
						getFieldLabel(
							projectRecord,
							LocationPaths._Root_LocationProject._Tax_TaxRateChange_CaliforniaLocalFuelRate,
							context.getSession(),
							true) + " is only valid in the state of California"));
			}

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._Tax_TaxRateChange_MotorVehicleRentalTaxRate,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._Tax_TaxRateChange_MotorVehicleSurcharge,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._Tax_TaxRateChange_MotorVehicleSurchargePercent,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._Tax_TaxRateChange_MotorVehicleRecovery,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._Tax_TaxRateChange_MotorVehicleRecoveryPercent,
				true,
				true);

		}
		super.checkBeforeWorkItemCompletion(context);
	}
}