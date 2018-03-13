package com.orchestranetworks.ps.condition;

import com.onwbp.adaptation.*;
import com.orchestranetworks.ps.util.*;
import com.orchestranetworks.service.*;
import com.orchestranetworks.workflow.*;

/**
 * Configured with a name of a dataspace, a dataset, xpath for identifying a single record and an xpath predicate,
 * this condition bean will return true if the record matches the predicate.
 */
public class PredicateCondition extends ConditionBean
{
	private String dataspace;
	private String dataset;
	private String xpath;
	private String predicate;

	@Override
	public final boolean evaluateCondition(ConditionBeanContext aContext) throws OperationException
	{
		final Adaptation record = AdaptationUtil.getOneRecordOrThrowOperationException(
			aContext.getRepository(),
			dataspace,
			dataset,
			xpath);
		try
		{
			return record.matches(predicate);
		}
		catch (Exception ex)
		{
			throw OperationException.createError(ex.getMessage());
		}
	}

	public final String getDataset()
	{
		return this.dataset;
	}

	public final String getDataspace()
	{
		return this.dataspace;
	}

	public final String getPredicate()
	{
		return this.predicate;
	}

	public final String getXpath()
	{
		return this.xpath;
	}

	public final void setDataset(String dataset)
	{
		this.dataset = dataset;
	}

	public final void setDataspace(String dataspace)
	{
		this.dataspace = dataspace;
	}

	public final void setPredicate(String predicate)
	{
		this.predicate = predicate;
	}

	public final void setXpath(String xpath)
	{
		this.xpath = xpath;
	}
}
