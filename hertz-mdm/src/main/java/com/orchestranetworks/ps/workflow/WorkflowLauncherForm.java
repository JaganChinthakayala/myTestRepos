/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.workflow;

import javax.servlet.http.*;

import com.onwbp.base.text.*;
import com.orchestranetworks.service.*;
import com.orchestranetworks.ui.*;

/**
 */
public abstract class WorkflowLauncherForm
{
	protected abstract String getServletName();

	public void execute(HttpServletRequest request) throws OperationException
	{
		ServiceContext sContext = ServiceContext.getServiceContext(request);

		UIServiceComponentWriter writer = sContext.getUIComponentWriter();

		UIFormSpec formSpec = createFormSpec(sContext);
		writer.startForm(formSpec);
		writeForm(sContext);
		writer.endForm();

		writeFormButtonsJS(sContext);
	}

	protected abstract void writeForm(ServiceContext sContext) throws OperationException;

	protected UIFormSpec createFormSpec(ServiceContext sContext)
	{
		UIFormSpec formSpec = new UIFormSpec();
		formSpec.setURLForAction(sContext.getURLForIncludingResource(getServletName()));
		UIButtonSpec cancelButtonSpec = new UIButtonSpecJSAction(
			UserMessage.createInfo("Cancel"),
			"cancelForm();");
		formSpec.addActionBackInBottomBar(cancelButtonSpec);
		UIButtonSpec submitButtonSpec = new UIButtonSpecSubmit(
			UserMessage.createInfo("Submit"),
			"submit",
			"submit");
		formSpec.addActionForwardInBottomBar(submitButtonSpec);
		return formSpec;
	}

	protected void writeFormButtonsJS(ServiceContext sContext)
	{
		UIServiceComponentWriter writer = sContext.getUIComponentWriter();
		// On cancel, just redirect the screen
		writer.addJS_cr("function cancelForm()");
		writer.addJS_cr("{");
		writer.addJS_cr("  window.location.href='" + sContext.getURLForEndingService() + "';");
		writer.addJS_cr("}");
	}

	protected void writeHiddenInput(ServiceContext sContext, String id, String value)
	{
		UIServiceComponentWriter writer = sContext.getUIComponentWriter();
		writer.add_cr("<input type='hidden' id='" + id + "' name='" + id + "' value='" + value
			+ "'/>");
	}
}
