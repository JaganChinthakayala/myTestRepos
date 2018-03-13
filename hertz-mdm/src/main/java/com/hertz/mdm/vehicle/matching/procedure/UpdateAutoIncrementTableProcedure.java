/*
 * Copyright Orchestra Networks 2000-2013. All rights reserved.
 */
package com.hertz.mdm.vehicle.matching.procedure;

import com.onwbp.adaptation.*;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.service.*;

/**
 */
public class UpdateAutoIncrementTableProcedure implements Procedure
{

	AdaptationTable autoIncrementTable;
	AdaptationHome autoIncrementDataSpace;
	Adaptation autoIncrementDataSet;
	int masterPkey;
	int incrementStep;
	Adaptation firstAutoIncrementRecord;
	public UpdateAutoIncrementTableProcedure(
		final AdaptationTable autoIncrementTable,
		final AdaptationHome autoIncrementDataSpace,
		final Adaptation autoIncrementDataSet,
		final Adaptation firstAutoIncrementRecord,
		final int incrementStep,
		final int masterPkey)
	{
		this.autoIncrementTable = autoIncrementTable;
		this.autoIncrementDataSpace = autoIncrementDataSpace;
		this.autoIncrementDataSet = autoIncrementDataSet;
		this.incrementStep = incrementStep;

		this.masterPkey = masterPkey;
		this.firstAutoIncrementRecord = firstAutoIncrementRecord;

	}

	@Override
	public void execute(ProcedureContext pContext) throws Exception
	{
		// TODO Auto-generated method stub

		ValueContextForUpdate vc = pContext.getContextForNewOccurrence(autoIncrementTable);
		vc.setValue(this.masterPkey + this.incrementStep, Path.parse("/currentValue"));
		pContext.doModifyContent(this.firstAutoIncrementRecord, vc);

	}

}
