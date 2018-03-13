package com.orchestranetworks.ps.scripttask;

import com.onwbp.adaptation.*;
import com.orchestranetworks.ps.procedure.*;
import com.orchestranetworks.ps.util.*;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.service.*;
import com.orchestranetworks.workflow.*;

/**
 */
public class SetValueScriptTask extends ScriptTaskBean
{
	private String dataspace;
	private String dataset;
	private String xpath;
	private String path;
	private String value;

	@Override
	public void executeScript(ScriptTaskBeanContext aContext) throws OperationException
	{
		final Adaptation record = AdaptationUtil.getOneRecordOrThrowOperationException(
			aContext.getRepository(),
			dataspace,
			dataset,
			xpath);

		final AdaptationHome home = AdaptationUtil.getDataSpaceOrThrowOperationException(
			aContext.getRepository(),
			dataspace);

		SchemaNode node = null;

		try
		{
			node = record.getSchemaNode().getNode(Path.parse(path));
		}
		catch (Exception ex)
		{
			throw OperationException.createError(ex.getMessage());
		}
		final Procedure proc = new SetValueFromStringProcedure(record, node, value);
		ProcedureExecutor.executeProcedure(proc, aContext.getSession(), home);
	}
	public String getDataspace()
	{
		return this.dataspace;
	}

	public void setDataspace(String dataspace)
	{
		this.dataspace = dataspace;
	}

	public String getDataset()
	{
		return this.dataset;
	}

	public void setDataset(String dataset)
	{
		this.dataset = dataset;
	}

	public String getXpath()
	{
		return this.xpath;
	}

	public void setXpath(String xpath)
	{
		this.xpath = xpath;
	}

	public String getPath()
	{
		return this.path;
	}

	public void setPath(String path)
	{
		this.path = path;
	}

	public String getValue()
	{
		return this.value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

}
