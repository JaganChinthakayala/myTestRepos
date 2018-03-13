package com.hertz.mdm._hertz.workflow.launcher;

import java.util.Locale;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.ps.project.path.ProjectPathConfig;
import com.orchestranetworks.ps.project.workflow.launcher.ProjectWorkflowLauncher;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.Session;

public abstract class HtzProjectWorkflowLauncher extends ProjectWorkflowLauncher
{
	private static final long serialVersionUID = 1L;

	protected HtzProjectWorkflowLauncher()
	{
		super();
	}

	protected HtzProjectWorkflowLauncher(boolean resuming)
	{
		super(resuming);
	}

	protected abstract String getDomain();

	@Override
	protected String getWorkflowModelLabelForInstanceName(String workflowModelName, Locale locale)
	{
		// Prefix for workflow models is domain name + " - ".
		// We want to remove this from workflow label.
		return super.getWorkflowModelLabelForInstanceName(workflowModelName, locale)
			.substring(getDomain().length() + 3);
	}

	@Override
	protected String getRecordLabel(Adaptation record, Session session)
	{
		ProjectPathConfig pathConfig = getProjectPathConfig();
		return record.getOccurrencePrimaryKey().format() + " - "
			+ record.getString(pathConfig.getProjectNameFieldPath());
	}

	@Override
	protected void setAdditionalContextVariables() throws OperationException
	{
		super.setAdditionalContextVariables();
	}
}
