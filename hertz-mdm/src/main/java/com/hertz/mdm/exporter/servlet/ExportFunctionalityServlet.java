package com.hertz.mdm.exporter.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationHome;
import com.onwbp.adaptation.AdaptationName;
import com.orchestranetworks.instance.HomeKey;
import com.orchestranetworks.instance.Repository;
import com.hertz.mdm.exporter.ExportFunctionalityConfigFactory;
import com.hertz.mdm.exporter.ExportFunctionalityExporter;
import com.hertz.mdm.exporter.DefaultTableExporterFactory;
import com.hertz.mdm.exporter.ExportFunctionalityConfig;
import com.hertz.mdm.exporter.TableExporterFactory;
import com.orchestranetworks.service.LoggingCategory;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.ServiceContext;
import com.orchestranetworks.service.Session;

/**
 * A servlet for invoking the data set export. It is not required for invoking the exporter, but is
 * one way in which it can be invoked. It is required if you wish to invoke it via the GUI services menu.
 * 
 */
public class ExportFunctionalityServlet extends HttpServlet {
	// These are the parameters for the servlet
	public static final String PARAM_EXPORT_CONFIG_DATA_SPACE = "exportConfigDataSpace";
	public static final String PARAM_EXPORT_CONFIG_DATA_SET = "exportConfigDataSet";
	public static final String PARAM_EXPORT_CONFIG_ID = "exportConfigID";
	public static final String PARAM_VALIDATION_ACTION = "validationAction";
	
	public static final String VALIDATION_ACTION_NONE = "none";
	public static final String VALIDATION_ACTION_CONTINUE = "continue";
	public static final String VALIDATION_ACTION_STOP = "stop";
	
	protected static final LoggingCategory LOG = LoggingCategory.getKernel();
	
	private static final long serialVersionUID = 1L;
	public static final String PARAM_MERGE_README = "mergeReadme";
	public static final String PARAM_SQL_REPORTS = "runSQLReports";
	public static final String PARAM_HT_GEN = "runHTGenerator";
	
	private String exportConfigDataSpace;
	private String exportConfigDataSet;
	private String exportConfigID;
	
	protected boolean mergeReadme;
	protected boolean runSQLReports;
	protected boolean runHTGenerator;
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		System.out.println("Inside Servlet Class.....................");
		LOG.info("DataSetExportServlet: service");
		final PrintWriter out = res.getWriter();
		final ServiceContext sContext = ServiceContext.getServiceContext(req);
		final Adaptation dataSet = sContext.getCurrentAdaptation();
		
		exportConfigDataSpace = req.getParameter(PARAM_EXPORT_CONFIG_DATA_SPACE);
		exportConfigDataSet = req.getParameter(PARAM_EXPORT_CONFIG_DATA_SET);
		exportConfigID = req.getParameter(PARAM_EXPORT_CONFIG_ID);
		//final String validationAction = req.getParameter(PARAM_VALIDATION_ACTION);
		final String mergeReadmeString = req.getParameter(PARAM_MERGE_README);
		mergeReadme = (mergeReadmeString == null || "".equals(mergeReadmeString))? false : Boolean.parseBoolean(mergeReadmeString);
		
		LOG.info("mergeReadme in DataSetExportServlet = " + mergeReadme);
		
		final String runHTGenString = req.getParameter(PARAM_HT_GEN);
		runHTGenerator = (runHTGenString == null || "".equals(runHTGenString))? false : Boolean.parseBoolean(runHTGenString);
		
		final String runSQLReportString = req.getParameter(PARAM_SQL_REPORTS);
		runSQLReports = (runSQLReportString == null || "".equals(runSQLReportString))? false : Boolean.parseBoolean(runSQLReportString);
		 	
		try {
			export(sContext.getSession(), sContext.getCurrentHome().getRepository(), dataSet);
		} catch (final OperationException ex) {
			throw new ServletException(ex);
		}
		
		writeRedirectionOnEnding(out, sContext);
	}
	
	protected void export(Session session, Repository repo, Adaptation dataSet)
			throws IOException, OperationException {
		LOG.info("DataSetExportServlet: export");
		final TableExporterFactory tableExporterFactory = createTableExporterFactory();
		final ExportFunctionalityConfig config = createConfig(repo);
		final ExportFunctionalityExporter exporter = createExporter(tableExporterFactory, config);
		exporter.exportDataSet(session, dataSet);
	}
	
	protected ExportFunctionalityExporter createExporter(TableExporterFactory tableExporterFactory, ExportFunctionalityConfig config) {
		LOG.info("DataSetExportServlet: createExporter");
		return new ExportFunctionalityExporter(tableExporterFactory, config);
	}
	
	protected TableExporterFactory createTableExporterFactory() {
		LOG.info("DataSetExportServlet: createTableExporterFactory");
		return new DefaultTableExporterFactory();
	}
	
	protected ExportFunctionalityConfig createConfig(Repository repo)
			throws OperationException {
		LOG.info("DataSetExportServlet: createConfig");
		// Look up the data space and data set for the export config records
		final AdaptationHome exportConfigDataSpaceRef = repo.lookupHome(
				HomeKey.forBranchName(exportConfigDataSpace));
		final Adaptation exportConfigDataSetRef = exportConfigDataSpaceRef.findAdaptationOrNull(
				AdaptationName.forName(exportConfigDataSet));
		final ExportFunctionalityConfigFactory configFactory = new ExportFunctionalityConfigFactory();
		return configFactory.createFromEBXRecord(
				exportConfigDataSetRef, exportConfigID);
	}
	
	protected void writeRedirectionOnEnding(PrintWriter out, ServiceContext sContext) {
		LOG.info("DataSetExportServlet: writeRedirectionOnEnding");
		LOG.debug(" sContext.getURLForEndingService()  " +  sContext.getURLForEndingService() );
		out.print("<script>window.location.href='" + sContext.getURLForEndingService() + "';</script>");
	}
}
