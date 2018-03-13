package com.orchestranetworks.ps.procedure;

import java.util.*;

import com.onwbp.adaptation.*;
import com.orchestranetworks.ps.trigger.*;
import com.orchestranetworks.ps.trigger.TriggerActionValidator.TriggerAction;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.service.*;

public class ModifyValuesProcedure implements Procedure
{
	private Adaptation adaptation;
	private Adaptation modifiedAdaptation;
	private Map<Path, Object> pathValueMap;
	private boolean enableAllPrivileges;
	private boolean disableTriggerActivation;

	public static Adaptation execute(
		final Adaptation adaptation,
		final Map<Path, Object> pathValueMap,
		Session session) throws OperationException
	{
		return ModifyValuesProcedure.execute(adaptation, pathValueMap, session, true, false);
	}

	public static Adaptation execute(
		final Adaptation adaptation,
		final Map<Path, Object> pathValueMap,
		Session session,
		boolean enableAllPrivileges,
		boolean disableTriggerActivation) throws OperationException
	{
		ModifyValuesProcedure procedure = new ModifyValuesProcedure(adaptation, pathValueMap);
		procedure.enableAllPrivileges = enableAllPrivileges;
		procedure.disableTriggerActivation = disableTriggerActivation;
		ProcedureExecutor.executeProcedure(procedure, session, adaptation);
		return procedure.getModifiedAdaptation();
	}

	public ModifyValuesProcedure()
	{
	}

	public ModifyValuesProcedure(final Adaptation adaptation, final Map<Path, Object> pathValueMap)
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

	public Adaptation getModifiedAdaptation()
	{
		return this.modifiedAdaptation;
	}

	public void setModifiedAdaptation(Adaptation modifiedAdaptation)
	{
		this.modifiedAdaptation = modifiedAdaptation;
	}

	@Override
	public void execute(ProcedureContext pContext) throws Exception
	{
		modifiedAdaptation = ModifyValuesProcedure.execute(
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
		return ModifyValuesProcedure.execute(pContext, adaptation, pathValueMap, true, false);
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
		Adaptation modifiedAdaptation;
		try
		{
			final ValueContextForUpdate vc = pContext.getContext(adaptation.getAdaptationName());
			for (Map.Entry<Path,Object> entry : pathValueMap.entrySet())
			{
				vc.setValue(entry.getValue(), entry.getKey());
			}

			modifiedAdaptation = doModify(pContext, adaptation, vc, enableAllPrivileges);
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
		return modifiedAdaptation;
	}

	private static Adaptation doModify(
		ProcedureContext pContext,
		Adaptation adaptation,
		ValueContextForUpdate vc,
		boolean enableAllPrivileges) throws OperationException
	{
		Adaptation modifiedAdaptation;
		if (enableAllPrivileges)
		{
			Session session = pContext.getSession();
			TriggerAction[] ignoreActions = (TriggerAction[]) session.getAttribute(BaseTableTrigger.IGNORE_VALIDATE_ACTION_SESSION_ATTRIBUTE);
			session.setAttribute(
				BaseTableTrigger.IGNORE_VALIDATE_ACTION_SESSION_ATTRIBUTE,
				new TriggerAction[] { TriggerAction.MODIFY });
			try
			{
				modifiedAdaptation = pContext.doModifyContent(adaptation, vc);
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
			modifiedAdaptation = pContext.doModifyContent(adaptation, vc);
		}
		return modifiedAdaptation;
	}
}
