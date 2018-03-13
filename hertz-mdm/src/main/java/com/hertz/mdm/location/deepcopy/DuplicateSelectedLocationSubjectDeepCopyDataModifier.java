/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.deepcopy;

import com.hertz.mdm.location.path.LocationPaths;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.ps.deepcopy.DeepCopyConfig;
import com.orchestranetworks.ps.deepcopy.DefaultDeepCopyDataModifier;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.service.ValueContextForUpdate;

/**
 */
public class DuplicateSelectedLocationSubjectDeepCopyDataModifier
	extends
	DefaultDeepCopyDataModifier
{
	private String subjectName;

	public DuplicateSelectedLocationSubjectDeepCopyDataModifier(String subjectName)
	{
		this.subjectName = subjectName;
	}

	@Override
	public void modifyDuplicateRecordContext(
		ValueContextForUpdate context,
		Adaptation origRecord,
		DeepCopyConfig config)
	{
		super.modifyDuplicateRecordContext(context, origRecord, config);

		Path tablePath = context.getAdaptationTable().getTablePath();
		if (LocationPaths._Root_Location.getPathInSchema().equals(tablePath))
		{
			modifyLocation(context);
		}
	}

	private void modifyLocation(ValueContextForUpdate context)
	{
		/*	context
				.setValue(subjectName, BkFinancePaths._Root_GeneralLedgerAccount._AccountDescription);
			context.setValue(
				FinanceSubjectStatuses.DRAFT,
				BkFinancePaths._Root_GeneralLedgerAccount._GeneralLedgerAccountStatus);
			context
				.setValue(null, BkFinancePaths._Root_GeneralLedgerAccount._GeneralLedgerAccountNumber);
			context.setValue(
				null,
				BkFinancePaths._Root_GeneralLedgerAccount._HyperionGeneralLedgerAccount);
			context.setValue(
				Boolean.FALSE,
				BkFinancePaths._Root_GeneralLedgerAccount._BlockGeneralLedgerAccount);*/
	}
}
