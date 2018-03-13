package com.orchestranetworks.ps.procedure;

import java.util.*;

import com.onwbp.adaptation.*;
import com.orchestranetworks.ps.trigger.*;
import com.orchestranetworks.ps.trigger.TriggerActionValidator.TriggerAction;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.service.*;

public class CreateRecordProcedure implements Procedure
{
	private Adaptation createdRecord;
	private AdaptationTable adaptationTable;
	private Map<Path, Object> pathValueMap;
	private boolean enableAllPrivileges;
	private boolean disableTriggerActivation;

	/**
	 * @deprecated Use {@link #execute(AdaptationTable, Map, Session)} instead.
	 * The Adaptation parameter is redundant because it can be derived from the AdaptationTable.
	 */
	@Deprecated
	public static Adaptation execute(
		final Adaptation adaptation,
		final AdaptationTable adaptationTable,
		final Map<Path, Object> pathValueMap,
		Session session) throws OperationException
	{
		return CreateRecordProcedure.execute(adaptationTable, pathValueMap, session);
	}

	public static Adaptation execute(
		final AdaptationTable adaptationTable,
		final Map<Path, Object> pathValueMap,
		Session session) throws OperationException
	{
		return CreateRecordProcedure.execute(adaptationTable, pathValueMap, session, true, false);
	}

	/**
	 * @deprecated Use {@link #execute(AdaptationTable, Map, Session, boolean, boolean)} instead.
	 * The Adaptation parameter is redundant because it can be derived from the AdaptationTable.
	 */
	@Deprecated
	public static Adaptation execute(
		final Adaptation adaptation,
		final AdaptationTable adaptationTable,
		final Map<Path, Object> pathValueMap,
		Session session,
		boolean enableAllPrivileges,
		boolean disableTriggerActivation) throws OperationException
	{
		return CreateRecordProcedure.execute(
			adaptationTable,
			pathValueMap,
			session,
			enableAllPrivileges,
			disableTriggerActivation);
	}

	public static Adaptation execute(
		final AdaptationTable adaptationTable,
		final Map<Path, Object> pathValueMap,
		Session session,
		boolean enableAllPrivileges,
		boolean disableTriggerActivation) throws OperationException
	{
		CreateRecordProcedure procedure = new CreateRecordProcedure(adaptationTable, pathValueMap);
		procedure.enableAllPrivileges = enableAllPrivileges;
		procedure.disableTriggerActivation = disableTriggerActivation;
		ProcedureExecutor.executeProcedure(
			procedure,
			session,
			adaptationTable.getContainerAdaptation());
		return procedure.getCreatedRecord();
	}

	public CreateRecordProcedure()
	{
	}

	public CreateRecordProcedure(
		final AdaptationTable adaptationTable,
		final Map<Path, Object> pathValueMap)
	{
		this.adaptationTable = adaptationTable;
		this.pathValueMap = pathValueMap;
	}

	public Adaptation getCreatedRecord()
	{
		return this.createdRecord;
	}

	public void setCreatedRecord(Adaptation createdRecord)
	{
		this.createdRecord = createdRecord;
	}

	public AdaptationTable getAdaptationTable()
	{
		return adaptationTable;
	}

	public void setAdaptationTable(AdaptationTable adaptationTable)
	{
		this.adaptationTable = adaptationTable;
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

	@Override
	public void execute(ProcedureContext pContext) throws Exception
	{
		createdRecord = CreateRecordProcedure.execute(
			pContext,
			adaptationTable,
			pathValueMap,
			enableAllPrivileges,
			disableTriggerActivation);
	}

	public static Adaptation execute(
		ProcedureContext pContext,
		AdaptationTable adaptationTable,
		final Map<Path, Object> pathValueMap) throws OperationException
	{
		return CreateRecordProcedure.execute(pContext, adaptationTable, pathValueMap, true, false);
	}

	public static Adaptation execute(
		ProcedureContext pContext,
		AdaptationTable adaptationTable,
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
			// Note that even if disableTriggerActivation is true, the handleNewContext will still get called.
			// That is usually the desired behavior, but if that situation arises, this class can be modified to allow for it.
			// We would call pContext.getContext in that case.
			final ValueContextForUpdate vc = pContext.getContextForNewOccurrence(adaptationTable);
			for (Map.Entry<Path,Object> entry : pathValueMap.entrySet())
			{
				vc.setValue(entry.getValue(), entry.getKey());
			}
			newRecord = doCreate(pContext, adaptationTable, vc, enableAllPrivileges);
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
