package com.orchestranetworks.ps.procedure;

import java.util.*;

import com.onwbp.adaptation.*;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.service.*;

/**
 */
public final class SetValueFromStringProcedure implements Procedure
{
	private final Adaptation record;
	private Adaptation modifiedRecord;
	private final SchemaNode node;
	private final String value;

	public SetValueFromStringProcedure(
		final Adaptation aRecord,
		final SchemaNode aNode,
		final String aValue)
	{
		this.record = aRecord;
		this.node = aNode;
		this.value = aValue;
	}

	@Override
	public final void execute(ProcedureContext pContext) throws Exception
	{
		Object obj = (value == null) ? null : node.parseXsString(value);
		HashMap<Path, Object> pathValueMap = new HashMap<Path, Object>();
		pathValueMap.put(node.getPathInAdaptation(), obj);
		modifiedRecord = ModifyValuesProcedure.execute(pContext, record, pathValueMap, true, false);
	}

	public Adaptation getModifiedRecord()
	{
		return this.modifiedRecord;
	}
}
