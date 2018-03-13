/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.trigger;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.onwbp.adaptation.*;
import com.orchestranetworks.ps.procedure.*;
import com.orchestranetworks.service.*;
import com.orchestranetworks.ui.*;

/**
 * A service that cascade deletes a record
 */
@SuppressWarnings("serial")
public class CascadeDeleteService extends HttpServlet
{
	public static final String CASCADE_DELETE_SERVICE_TRACKING_INFO = "CascadeDeleteService";

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException
	{
		ServiceContext sContext = ServiceContext.getServiceContext(req);
		@SuppressWarnings("unchecked")
		final List<Adaptation> records = sContext.getSelectedOccurrences();

		CascadeDeleteServiceProcedure proc = new CascadeDeleteServiceProcedure(records);
		try
		{
			ProcedureExecutor.executeProcedure(
				proc,
				sContext.getSession(),
				sContext.getCurrentHome(),
				CASCADE_DELETE_SERVICE_TRACKING_INFO);
		}
		catch (OperationException ex)
		{
			throw new ServletException(ex);
		}

		UIServiceComponentWriter writer = sContext.getUIComponentWriter();
		writer.addJS_cr("window.location.href='" + sContext.getURLForEndingService() + "';");
	}

	private class CascadeDeleteServiceProcedure implements Procedure
	{
		private List<Adaptation> records;

		public CascadeDeleteServiceProcedure(List<Adaptation> records)
		{
			this.records = records;
		}

		@Override
		public void execute(ProcedureContext pContext) throws Exception
		{
			for (Adaptation record : records)
			{
				CascadeDeleter.invokeCascadeDelete(record, pContext);
			}
		}
	}
}
