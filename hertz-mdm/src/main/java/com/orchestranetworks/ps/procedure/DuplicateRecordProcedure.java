package com.orchestranetworks.ps.procedure;

import java.util.*;

import com.onwbp.adaptation.*;
import com.orchestranetworks.ps.trigger.*;
import com.orchestranetworks.ps.trigger.TriggerActionValidator.TriggerAction;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.service.*;

public class DuplicateRecordProcedure implements Procedure
{
	private Adaptation adaptation;
	private Adaptation createdRecord;
	private Map<Path, Object> pathValueMap;
	private boolean enableAllPrivileges;
	private boolean disableTriggerActivation;

	public static Adaptation execute(
		final Adaptation adaptation,
		final Map<Path, Object> pathValueMap,
		Session session) throws OperationException
	{
		return DuplicateRecordProcedure.execute(adaptation, pathValueMap, session, true, false);
	}

	public static Adaptation execute(
		final Adaptation adaptation,
		final Map<Path, Object> pathValueMap,
		Session session,
		boolean enableAllPrivileges,
		boolean disableTriggerActivation) throws OperationException
	{
		DuplicateRecordProcedure procedure = new DuplicateRecordProcedure(adaptation, pathValueMap);
		procedure.enableAllPrivileges = enableAllPrivileges;
		procedure.disableTriggerActivation = disableTriggerActivation;
		ProcedureExecutor.executeProcedure(procedure, session, adaptation);
		return procedure.getCreatedRecord();
	}

	public DuplicateRecordProcedure()
	{
	}

	public DuplicateRecordProcedure(
		final Adaptation adaptation,
		final Map<Path, Object> pathValueMap)
	{
		this.adaptation = adaptation;
		this.pathValueMap = pathValueMap;
	}

	public Adaptation getAdaptation()
	{
		return adaptation;
	}

	public void setAdaptation(Adaptation adaptation)
	{
		this.adaptation = adaptation;
	}

	public Map<Path, Object> getPathValueMap()
	{
		return pathValueMap;
	}

	public void setPathValueMap(Map<Path, Object> pathValueMap)
	{
		this.pathValueMap = pathValueMap;
	}

	public boolean isEnableAllPrivileges()
	{
		return enableAllPrivileges;
	}

	public void setEnableAllPrivileges(boolean enableAllPrivileges)
	{
		this.enableAllPrivileges = enableAllPrivileges;
	}

	public boolean isDisableTriggerActivation()
	{
		return disableTriggerActivation;
	}

	public void setDisableTriggerActivation(boolean disableTriggerActivation)
	{
		this.disableTriggerActivation = disableTriggerActivation;
	}

	public Adaptation getCreatedRecord()
	{
		return this.createdRecord;
	}

	public void setCreatedRecord(Adaptation createdRecord)
	{
		this.createdRecord = createdRecord;
	}

	@Override
	public void execute(ProcedureContext pContext) throws Exception
	{
		createdRecord = DuplicateRecordProcedure.execute(
			pContext,
			adaptation,
			pathValueMap,
			enableAllPrivileges,
			disableTriggerActivation);
	}

	public static Adaptation execute(
		ProcedureContext pContext,
		Adaptation adaptation,
		final Map<Path, Object> pathValueMap) throws OperationException
	{
		return DuplicateRecordProcedure.execute(pContext, adaptation, pathValueMap, true, false);
	}

	public static Adaptation execute(
		ProcedureContext pContext,
		Adaptation adaptation,
		final Map<Path, Object> pathValueMap,
		boolean enableAllPrivileges,
		boolean disableTriggerActivation) throws OperationException
	{
		boolean origAllPrivileges = pContext.isAllPrivileges();
		boolean origTriggerActivation = pContext.isTriggerActivation();
		if (enableAllPrivileges)
		{
			pContext.setAllPrivileges(true);
		}
		if (disableTriggerActivation)
		{
			pContext.setTriggerActivation(false);
		}
		Adaptation newRecord;
		try
		{
			final AdaptationTable table = adaptation.getContainerTable();
			final ValueContextForUpdate vc = pContext.getContextForNewOccurrence(adaptation, table);
			for (Map.Entry<Path,Object> entry : pathValueMap.entrySet())
			{
				vc.setValue(entry.getValue(), entry.getKey());
			}
			newRecord = doCreate(pContext, table, vc, enableAllPrivileges);
		}
		finally
		{
			if (enableAllPrivileges)
			{
				pContext.setAllPrivileges(origAllPrivileges);
			}
			if (disableTriggerActivation)
			{
				pContext.setTriggerActivation(origTriggerActivation);
			}
		}
		return newRecord;
	}

	private static Adaptation doCreate(
		ProcedureContext pContext,
		AdaptationTable adaptationTable,
		ValueContextForUpdate vc,
		boolean enableAllPrivileges) throws OperationException
	{
		Adaptation newRecord;
		if (enableAllPrivileges)
		{
			Session session = pContext.getSession();
			TriggerAction[] ignoreActions = (TriggerAction[]) session.getAttribute(BaseTableTrigger.IGNORE_VALIDATE_ACTION_SESSION_ATTRIBUTE);
			session.setAttribute(
				BaseTableTrigger.IGNORE_VALIDATE_ACTION_SESSION_ATTRIBUTE,
				new TriggerAction[] { TriggerAction.CREATE });
			try
			{
				newRecord = pContext.doCreateOccurrence(vc, adaptationTable);
			}
			finally
			{
				session.setAttribute(
					BaseTableTrigger.IGNORE_VALIDATE_ACTION_SESSION_ATTRIBUTE,
					ignoreActions);
			}
		}
		else
		{
			newRecord = pContext.doCreateOccurrence(vc, adaptationTable);
		}
		return newRecord;
	}
}
