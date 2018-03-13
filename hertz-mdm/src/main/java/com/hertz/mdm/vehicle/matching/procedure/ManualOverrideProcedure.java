package com.hertz.mdm.vehicle.matching.procedure;

import com.onwbp.adaptation.*;
import com.orchestranetworks.addon.daqa.*;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.service.*;

public class ManualOverrideProcedure implements Procedure
{

	private String ManualSource = "MANUAL";
	private Adaptation recordToModify;
	private Adaptation dataSet;
	private AdaptationTable matchTable;
	public Adaptation overRiddenRecord;
	private Path sourceSystem = Path.parse("./sourceSystem");

	public ManualOverrideProcedure(
		Adaptation record,
		AdaptationTable matchTable,
		Adaptation dataSet)
	{

		this.dataSet = dataSet;
		this.matchTable = matchTable;
		this.recordToModify = record;
		this.overRiddenRecord = null;

	}

	@Override
	public void execute(ProcedureContext pContext) throws Exception
	{
		pContext.setAllPrivileges(true);

		MatchingOperations operations = MatchingOperationsFactory.getMatchingOperations();
		RecordContext recordContext = new RecordContext(this.recordToModify, pContext);

		//Create a new record
		ValueContextForUpdate vContextForNewRecord = pContext
			.getContextForNewOccurrence(this.recordToModify, this.matchTable);
		vContextForNewRecord.setValue(this.ManualSource, this.sourceSystem);

		this.overRiddenRecord = pContext.doCreateOccurrence(vContextForNewRecord, this.matchTable);
		RecordContext overRiddenRecordContext = new RecordContext(this.overRiddenRecord, pContext);
		//operations.setAtOnceUnmatched(overRiddenRecordContext);

		pContext.setAllPrivileges(false);

	}

	public Adaptation getOverRiddenRecord()
	{
		return this.overRiddenRecord;
	}

}
