package com.orchestranetworks.ps.codegen;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.onwbp.adaptation.*;
import com.orchestranetworks.ps.util.*;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.service.*;
import com.orchestranetworks.ui.*;

/**
 * This service implementation is meant to act on a data set.  For each table in the data set,
 * it will generate a java bean class for that table.
 * @see JavaBeanExporter
 */
public class GenerateBeansForTables
{
	public static final String DEFAULT_EXPORT_DIR_NAME = "beanExport";

	public GenerateBeansForTables(
		final HttpServletRequest request,
		final HttpServletResponse response) throws IOException, ServletException
	{
		this(
			request,
			response,
			request.getSession().getServletContext().getRealPath("/"),
			DEFAULT_EXPORT_DIR_NAME,
			false);
	}

	public GenerateBeansForTables(
		final HttpServletRequest request,
		final HttpServletResponse response,
		String exportParentDirName,
		String exportDirName,
		boolean appendTimestamp) throws IOException, ServletException
	{
		final ServiceContext sContext = ServiceContext.getServiceContext(request);

		final Adaptation container = sContext.getCurrentAdaptation();
		String lastFileName = null;
		List<AdaptationTable> tables = AdaptationUtil.getAllTables(container);
		for (AdaptationTable table : tables)
		{
			SchemaNode tableNode = table.getTableNode();
			JavaBeanExporter exporter;
			try
			{
				exporter = new JavaBeanExporter(
					exportParentDirName,
					exportDirName,
					sContext.getLocale(),
					appendTimestamp,
					tableNode);
				lastFileName = exporter.doExport();
			}
			catch (final IOException e)
			{
				LoggingCategory.getKernel().error("Error occurred generating bean.", e);
				throw new ServletException(e);
			}
		}
		final UIComponentWriter uiComponentWriter = sContext.getUIComponentWriter();
		StringBuilder message = new StringBuilder();
		if (lastFileName == null)
			message.append("No tables were generated as java beans");
		else
		{
			File file = new File(lastFileName);
			message.append("Java beans generated to directory ").append(file.getParent());
		}
		uiComponentWriter.add_cr(message.toString());
	}
}
