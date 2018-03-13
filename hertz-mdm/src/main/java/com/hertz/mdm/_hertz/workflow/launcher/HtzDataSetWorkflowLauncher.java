package com.hertz.mdm._hertz.workflow.launcher;

import java.util.Locale;

import com.orchestranetworks.ps.workflow.DataSetWorkflowLauncher;
import com.orchestranetworks.service.OperationException;

public abstract class HtzDataSetWorkflowLauncher extends DataSetWorkflowLauncher
{
	private static final long serialVersionUID = 1L;

	public static final String PARAM_BRAND = "brand";

	protected abstract String getDomain();

	protected abstract String getWorkflowKeySuffix();

	@Override
	protected String getWorkflowKey()
	{
		return getDomain() + "_" + getWorkflowKeySuffix();
	}

	@Override
	protected void setAdditionalContextVariables() throws OperationException
	{
		super.setAdditionalContextVariables();
		/*
		String brand = workflowLauncherContext.getParameter(PARAM_BRAND);
		if (brand != null)
		{
			launcher.setInputParameter(BkWorkflowConstants.PARAM_BRAND, brand);
		}*/
	}

	@Override
	protected String enrichWorkflowInstanceName(String workflowInstanceName, Locale locale)
	{
		StringBuilder bldr = new StringBuilder();
		bldr.append(super.enrichWorkflowInstanceName(workflowInstanceName, locale));
		String brand = workflowLauncherContext.getParameter(PARAM_BRAND);
		if (brand != null)
		{
			bldr.append(" - Brand: ");
			bldr.append(brand);
		}
		return bldr.toString();
	}
}
