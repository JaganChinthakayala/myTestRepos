/*
 * Copyright Orchestra Networks 2000-2013. All rights reserved.
 */
package com.hertz.mdm.vehicle.matching.procedure;

import com.hertz.mdm.vehicle.matching.constant.*;
import com.onwbp.adaptation.*;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.service.*;

/**
 */
public class FindManualRecordProcedure implements Procedure
{
	public Adaptation overRiddenRecord;
	private Path sourceSystem = Path.parse("./sourceSystem");
	private Adaptation record;
	private AdaptationTable matchTable;

	private Adaptation dataSet;

	public FindManualRecordProcedure(
		Adaptation record,
		AdaptationTable matchTable,
		Adaptation dataSet)
	{
		this.dataSet = dataSet;
		this.record = record;
		this.overRiddenRecord = null;
	}

	@Override
	public void execute(ProcedureContext context) throws Exception
	{
		context.setAllPrivileges(true);
		this.matchTable = record.getContainerTable();
		String recordClusterId = record.get(Path.parse("./DaqaMetaData/ClusterId")).toString();
		String condition = Path.parse("./DaqaMetaData/ClusterId").format() + " = '"
			+ recordClusterId + "'";
		if (!this.matchTable.createRequestResult(condition).isEmpty())
		{
			RequestResult recordSet = matchTable.createRequestResult(condition);

			for (Adaptation record1; (record1 = recordSet.nextAdaptation()) != null;)
			{
				@SuppressWarnings("unused")
				String lastUser = record1.getLastUser().format();
				if (record1.get(sourceSystem).toString().equalsIgnoreCase(
					DaqaConstant.SOURCE_MANUAL))
				{
					this.overRiddenRecord = record1;
				}

			}
		}

		context.setTriggerActivation(Boolean.TRUE);

	}
	public Adaptation getOverRiddenRecord()
	{
		return this.overRiddenRecord;
	}

}
