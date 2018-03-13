/*
 * Copyright Orchestra Networks 2000-2013. All rights reserved.
 */
package com.hertz.mdm.vehicle.matching.trigger;

import org.apache.commons.lang.*;

import com.hertz.mdm.vehicle.matching.procedure.*;
import com.onwbp.adaptation.*;
import com.orchestranetworks.instance.*;
import com.orchestranetworks.ps.util.*;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.schema.trigger.*;
import com.orchestranetworks.service.*;

/**
 */
public class AutoIncrementIdentifierTrigger extends TableTrigger
{
	private String lookupDataspaceString;
	private String lookupDatasetString;
	private String lookupTableString;
	private String primaryKeyfieldPath;
	private String localIdPath;
	private Integer incrementStep;
	private Integer masterPkey;

	private String autoIncrementDataSpaceString = "ebx-autoIncrements";
	private String autoIncrementDataSetString = "ebx-autoIncrements";
	private String autoIncrementTableString = "/repository/sequences";

	private AdaptationHome autoIncrementDataSpace;
	private Adaptation autoIncrementDataSet;
	private AdaptationTable autoIncrementTable;
	private Adaptation firstAutoIncrementRecord;

	@Override
	public void setup(TriggerSetupContext arg0)
	{
		// TODO Auto-generated method stub

	}

	public void handleAfterCreate(AfterCreateOccurrenceContext context) throws OperationException
	{

		Adaptation currentRecord = context.getAdaptationOccurrence();
		AdaptationHome lookupDataspace = context.getAdaptationHome().getRepository().lookupHome(
			HomeKey.forBranchName(this.lookupDataspaceString));
		Adaptation lookupDataset = AdaptationUtil
			.getDataSetOrThrowOperationException(lookupDataspace, this.lookupDatasetString);
		AdaptationTable lookupTable = lookupDataset.getTable(Path.parse(this.lookupTableString));

		this.autoIncrementDataSpace = context.getAdaptationHome().getRepository().lookupHome(
			HomeKey.forBranchName(this.autoIncrementDataSpaceString));
		this.autoIncrementDataSet = AdaptationUtil.getDataSetOrThrowOperationException(
			autoIncrementDataSpace,
			this.autoIncrementDataSetString);
		this.autoIncrementTable = autoIncrementDataSet
			.getTable(Path.parse(this.autoIncrementTableString));

		String moduleName = context.getTable().getContainerAdaptation().getModuleName();

		String schemaLocation = StringUtils.substringAfter(
			context.getTable().getContainerAdaptation().getSchemaLocation().format(),
			moduleName + ":");
		final String condition = "osd:contains-case-insensitive(./schema,'" + schemaLocation + "')";

		this.firstAutoIncrementRecord = this
			.getAutoIncrementTableAutoIncrementIntIdentifier(autoIncrementTable, condition);
		//this.autoIncrementKey = this.firstAutoIncrementRecord.get_int(Path.parse("./currentValue"));

		this.masterPkey = this.getMasterAutoIncrementIntIdentifier(lookupTable);

		int autoIncrementKey = currentRecord.get_int(Path.parse(localIdPath));
		if (this.masterPkey != null)
		{
			if (autoIncrementKey <= this.masterPkey)
			{

				final ProcedureContext pContext = context.getProcedureContext();
				ValueContextForUpdate vcContext = pContext
					.getContextForNewOccurrence(currentRecord.getContainerTable());

				try
				{

					vcContext.setValue(this.masterPkey + incrementStep, Path.parse(localIdPath));

				}

				catch (Exception ex)
				{
					// TODO Auto-generated catch block
					throw new RuntimeException(ex);
				}
				pContext.doModifyContent(currentRecord, vcContext);

				UpdateAutoIncrementTableProcedure updateAutoIncProc = new UpdateAutoIncrementTableProcedure(
					autoIncrementTable,
					autoIncrementDataSpace,
					autoIncrementDataSet,
					firstAutoIncrementRecord,
					incrementStep,
					masterPkey);

				ProgrammaticService programmaticService = ProgrammaticService
					.createForSession(context.getSession(), this.autoIncrementDataSpace);
				programmaticService.execute(updateAutoIncProc);

			}
		}

	}

	public Integer getMasterAutoIncrementIntIdentifier(AdaptationTable lookupTable)
		throws OperationException
	{

		Request request = lookupTable.createRequest();
		RequestSortCriteria sort = new RequestSortCriteria();
		sort.add(Path.parse(primaryKeyfieldPath), Boolean.FALSE);
		request.setSortCriteria(sort);
		RequestResult result = request.execute();

		try
		{
			final Adaptation firstRecord = result.nextAdaptation();
			masterPkey = Integer.parseInt(firstRecord.getOccurrencePrimaryKey().format());
		}
		catch (Exception e)
		{
			result.close();
		}
		return masterPkey;

	}

	public Adaptation getAutoIncrementTableAutoIncrementIntIdentifier(
		AdaptationTable autoIncrementTable,
		String condition)
		throws OperationException
	{

		int size = autoIncrementTable.createRequestResult(condition).getSize();

		Request request = autoIncrementTable.createRequest();
		request.setXPathFilter(condition);
		RequestResult result = request.execute();

		try
		{
			this.firstAutoIncrementRecord = result.nextAdaptation();
		}
		catch (Exception e)
		{
			result.close();
		}
		return this.firstAutoIncrementRecord;

	}

	public String getLookupDataspaceString()
	{
		return this.lookupDataspaceString;
	}
	public void setLookupDataspaceString(String lookupDataspaceString)
	{
		this.lookupDataspaceString = lookupDataspaceString;
	}
	public String getLookupDatasetString()
	{
		return this.lookupDatasetString;
	}
	public void setLookupDatasetString(String lookupDatasetString)
	{
		this.lookupDatasetString = lookupDatasetString;
	}
	public String getLookupTableString()
	{
		return this.lookupTableString;
	}
	public void setLookupTableString(String lookupTableString)
	{
		this.lookupTableString = lookupTableString;
	}
	public String getPrimaryKeyfieldPath()
	{
		return this.primaryKeyfieldPath;
	}
	public void setPrimaryKeyfieldPath(String primaryKeyfieldPath)
	{
		this.primaryKeyfieldPath = primaryKeyfieldPath;
	}
	public Integer getIncrementStep()
	{
		return this.incrementStep;
	}
	public void setIncrementStep(Integer incrementStep)
	{
		this.incrementStep = incrementStep;
	}
	public String getLocalIdPath()
	{
		return this.localIdPath;
	}
	public void setLocalIdPath(String localIdPath)
	{
		this.localIdPath = localIdPath;
	}
}
