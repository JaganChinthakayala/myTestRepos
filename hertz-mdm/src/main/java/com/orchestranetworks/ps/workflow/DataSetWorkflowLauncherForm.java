/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.workflow;

import org.apache.commons.lang.*;

import com.orchestranetworks.service.*;
import com.orchestranetworks.ui.*;

/**
 */
public abstract class DataSetWorkflowLauncherForm extends WorkflowLauncherForm
{
	private static final String DEFAULT_WORKFLOW_INSTANCE_NAME_LABEL = "Name";
	private static final String DEFAULT_WORKFLOW_INSTANCE_DESCRIPTION_LABEL = "Description";

	private String workflowInstanceNameLabel;
	private String workflowInstanceDescriptionLabel;

	protected DataSetWorkflowLauncherForm()
	{
		this(DEFAULT_WORKFLOW_INSTANCE_NAME_LABEL, DEFAULT_WORKFLOW_INSTANCE_DESCRIPTION_LABEL);
	}

	protected DataSetWorkflowLauncherForm(
		String workflowInstanceNameLabel,
		String workflowInstanceDescriptionLabel)
	{
		this.workflowInstanceNameLabel = workflowInstanceNameLabel;
		this.workflowInstanceDescriptionLabel = workflowInstanceDescriptionLabel;
	}

	@Override
	protected void writeForm(ServiceContext sContext) throws OperationException
	{
		writeWorkflowInstanceNameRow(sContext);
		writeWorkflowInstanceDescriptionRow(sContext);
	}

	protected void writeWorkflowInstanceNameRow(ServiceContext sContext)
	{
		UIServiceComponentWriter writer = sContext.getUIComponentWriter();
		writer.startFormRow(new UIFormLabelSpec(workflowInstanceNameLabel));
		String initialWorkflowInstanceName = getInitialWorkflowInstanceName(sContext);
		writer.add_cr("<input id='"
			+ DataSetWorkflowLauncher.PARAM_WORKFLOW_INSTANCE_NAME
			+ "' name='"
			+ DataSetWorkflowLauncher.PARAM_WORKFLOW_INSTANCE_NAME
			//			+ "' type='text' class='ebx_APV' maxLength='"
			+ "' type='text' class='ebx_InputLabelLocalizedBlock' maxLength='"
			+ getWorkflowInstanceNameFieldMaxLength(sContext)
			+ "'"
			// TODO: Apache doesn't handle escaping apostrophes (says it's not technically part of standard)
			//       so need to enclose in a quotation mark. Perhaps should go through rest of code and do the same
			+ (initialWorkflowInstanceName == null ? "" : " value=\""
				+ StringEscapeUtils.escapeHtml(initialWorkflowInstanceName) + "\"") + "/>");
		writer.endFormRow();
	}

	protected void writeWorkflowInstanceDescriptionRow(ServiceContext sContext)
	{
		UIServiceComponentWriter writer = sContext.getUIComponentWriter();
		writer.startFormRow(new UIFormLabelSpec(workflowInstanceDescriptionLabel));
		String initialWorkflowInstanceDescription = getInitialWorkflowInstanceDescription(sContext);
		writer.add_cr("<textarea id='"
			+ DataSetWorkflowLauncher.PARAM_WORKFLOW_INSTANCE_DESCRIPTION
			+ "' name='"
			+ DataSetWorkflowLauncher.PARAM_WORKFLOW_INSTANCE_DESCRIPTION
			+ "' cols='60' rows='5' maxLength='"
			+ getWorkflowInstanceDescriptionFieldMaxLength(sContext)
			+ "'>"
			+ (initialWorkflowInstanceDescription == null ? ""
				: StringEscapeUtils.escapeHtml(initialWorkflowInstanceDescription)) + "</textarea>");
		writer.endFormRow();
	}

	protected String getInitialWorkflowInstanceName(ServiceContext sContext)
	{
		return null;
	}

	protected String getInitialWorkflowInstanceDescription(ServiceContext sContext)
	{
		return null;
	}

	protected int getWorkflowInstanceNameFieldMaxLength(ServiceContext sContext)
	{
		return 80;
	}

	protected int getWorkflowInstanceDescriptionFieldMaxLength(ServiceContext sContext)
	{
		return 2000;
	}
}
