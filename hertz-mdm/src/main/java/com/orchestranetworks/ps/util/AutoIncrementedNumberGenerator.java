/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.util;

import java.util.*;

import com.onwbp.adaptation.*;
import com.orchestranetworks.ps.procedure.*;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.service.*;

/**
 */
public class AutoIncrementedNumberGenerator
{
	private AdaptationTable table;
	private boolean deleteRecord;

	public AutoIncrementedNumberGenerator(AdaptationTable table)
	{
		this(table, true);
	}

	public AutoIncrementedNumberGenerator(AdaptationTable table, boolean deleteRecord)
	{
		this.table = table;
		this.deleteRecord = deleteRecord;
	}

	public String assignGeneratedNumberWithPrefixAsString(
		ProcedureContext pContext,
		Adaptation record,
		Path fieldPath,
		String prefix) throws OperationException
	{
		Integer generatedNum;
		Session session = pContext.getSession();

		if (!record.getHome().getKey().equals(table.getContainerAdaptation().getHome().getKey()))
		{
			generatedNum = generateNumber(session);
		}
		else
		{
			generatedNum = generateNumber(pContext);
		}

		String generatedNumStringWithPrefix = prefix + generatedNum.toString();

		HashMap<Path, Object> pathValueMap = new HashMap<Path, Object>();
		pathValueMap.put(fieldPath, generatedNumStringWithPrefix);
		ModifyValuesProcedure.execute(pContext, record, pathValueMap);

		return generatedNumStringWithPrefix;
	}

	public void assignGeneratedNumber(ProcedureContext pContext, Adaptation record, Path fieldPath)
		throws OperationException
	{
		Integer generatedNum;
		Session session = pContext.getSession();
		if (!record.getHome().getKey().equals(table.getContainerAdaptation().getHome().getKey()))
		{
			generatedNum = generateNumber(session);
		}
		else
		{
			generatedNum = generateNumber(pContext);
		}
		HashMap<Path, Object> pathValueMap = new HashMap<Path, Object>();
		pathValueMap.put(fieldPath, generatedNum);
		ModifyValuesProcedure.execute(pContext, record, pathValueMap);
	}

	public Integer generateNumber(Session session) throws OperationException
	{
		GenerateNumberProcedure proc = new GenerateNumberProcedure();
		ProcedureExecutor.executeProcedure(proc, session, table.getContainerAdaptation());
		return proc.getGeneratedNum();
	}

	public Integer generateNumber(ProcedureContext pContext) throws OperationException
	{
		Adaptation record = CreateRecordProcedure.execute(
			pContext,
			table,
			new HashMap<Path, Object>(),
			true,
			false);
		Path pkPath = table.getPrimaryKeySpec()[0];
		Integer value = (Integer) record.get(pkPath);
		if (deleteRecord)
		{
			DeleteRecordProcedure.execute(pContext, record);
		}
		return value;
	}

	private class GenerateNumberProcedure implements Procedure
	{
		private Integer generatedNum;

		@Override
		public void execute(ProcedureContext pContext) throws Exception
		{
			generatedNum = generateNumber(pContext);
		}

		public Integer getGeneratedNum()
		{
			return this.generatedNum;
		}
	}
}
