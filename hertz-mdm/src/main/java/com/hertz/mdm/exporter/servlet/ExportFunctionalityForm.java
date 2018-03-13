package com.hertz.mdm.exporter.servlet;

import java.io.BufferedWriter;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.orchestranetworks.service.LoggingCategory;
import com.orchestranetworks.service.ServiceContext;

/**
 * A servlet that presents a form to the user, allowing them to specify the data space and data set
 * of the export config records, and the ID of the export config to execute. Upon submitting the form,
 * the ExportFunctionalityServlet is invoked. This is not required to execute the exporter, but it is required
 * if you wish to allow the user to invoke it via the services menu and you wish them to be able to
 * choose the export config rather than have it hardcoded by the jsp.
 * 
 */
public class ExportFunctionalityForm extends HttpServlet {
	protected static final LoggingCategory LOG = LoggingCategory.getKernel();
	
	private static final String DEFAULT_EXPORT_CONFIG_DATASPACE = "DataSetExport";
	private static final String DEFAULT_EXPORT_CONFIG_DATASET = "DataSetExport";
	private static final String DEFAULT_EXPORT_CONFIG_ID = "";
	private static final String DEFAULT_SERVLET = "/ExportFunctionality";
	private static final String DEFAULT_VALIDATION_ACTION = ExportFunctionalityServlet.VALIDATION_ACTION_NONE;
//	protected String mergeReadme = "true";
	
	private static final long serialVersionUID = 1L;
	
	protected BufferedWriter out;
	
	protected String exportConfigDataSpace = DEFAULT_EXPORT_CONFIG_DATASPACE;
	protected String exportConfigDataSet = DEFAULT_EXPORT_CONFIG_DATASET;
	protected String exportConfigID = DEFAULT_EXPORT_CONFIG_ID;
	protected String servlet = DEFAULT_SERVLET;
	
	/**
	 * Get the string identifying the servlet to invoke upon submit. This needs to match the name configured
	 * in the application server config (i.e. server.xml). For example, "/ExportFunctionality".
	 * 
	 * @return the servlet
	 */
	public String getServlet() {
		return servlet;
	}

	/**
	 * Set the string identifying the servlet to invoke upon submit. This needs to match the name configured
	 * in the application server config (i.e. server.xml). For example, "/ExportFunctionality".
	 * 
	 * @param servlet the servlet
	 */
	public void setServlet(String servlet) {
		this.servlet = servlet;
	}

	/**
	 * Get the ID of the data space for the export config records
	 * 
	 * @return the ID of the data space
	 */
	public String getExportConfigDataSpace() {
		return exportConfigDataSpace;
	}

	/**
	 * Set the ID of the data space for the export config records
	 * 
	 * @param exportConfigDataSpace the ID of the data space
	 */
	public void setExportConfigDataSpace(String exportConfigDataSpace) {
		this.exportConfigDataSpace = exportConfigDataSpace;
	}

	/**
	 * Get the ID of the data set for the export config records
	 * 
	 * @return the ID of the data set
	 */
	public String getExportConfigDataSet() {
		return exportConfigDataSet;
	}

	/**
	 * Set the ID of the data set for the export config records
	 * 
	 * @param exportConfigDataSet the ID of the data set
	 */
	public void setExportConfigDataSet(String exportConfigDataSet) {
		this.exportConfigDataSet = exportConfigDataSet;
	}
	
	/**
	 * Get the ID of the export config
	 * 
	 * @return the ID
	 */
	public String getExportConfigID() {
		return exportConfigID;
	}
	
	/**
	 * Set the ID of the export config
	 * 
	 * @param exportConfigID the ID
	 */
	public void setExportConfigID(String exportConfigID) {
		this.exportConfigID = exportConfigID;
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		LOG.info("servlet"+servlet);
		out = new BufferedWriter(res.getWriter());
		final ServiceContext sContext = ServiceContext.getServiceContext(req);
		writeForm(sContext);
		out.flush();
	}
	
	protected void writeForm(ServiceContext sContext) throws IOException {
		LOG.debug("DataSetExportForm: writeForm");
		writeln("<form id=\"form\" action=\"" + sContext.getURLForIncludingResource(servlet)
				+ "\" method=\"post\" autocomplete=\"off\">");
		writeFields(sContext);
		writeSubmitButton(sContext);
		writeln("</form>");
	}
	
	protected void writeFields(ServiceContext sContext) throws IOException {
		// Much of this HTML is replicating what gets produced by EBX to make it look
		// similar to the general look of EBX. However, It is not guaranteed that EBX
		// will continue to use these same css tags, etc in future versions.
		LOG.debug("DataSetExportForm: writeFields");
		writeln("  <table>");
		writeln("    <tbody>");
		
		writeln("      <tr class=\"ebx_Field\">");
		writeln("        <td class=\"ebx_Label\">");
		writeln("          <label for=\"" + ExportFunctionalityServlet.PARAM_EXPORT_CONFIG_DATA_SPACE
				+ "\">Export Config Data Space</label>");
		writeln("        </td>");
		writeln("        <td class=\"ebx_Input\">");
		writeln("          <input value=\"" + exportConfigDataSpace
				+ "\" type=\"text\" id=\"" + ExportFunctionalityServlet.PARAM_EXPORT_CONFIG_DATA_SPACE
				+ "\" name=\"" + ExportFunctionalityServlet.PARAM_EXPORT_CONFIG_DATA_SPACE + "\"/>");
		writeln("        </td>");
		writeln("      </tr>");
		
		writeln("      <tr class=\"ebx_Field\">");
		writeln("        <td class=\"ebx_Label\">");
		writeln("          <label for=\"" + ExportFunctionalityServlet.PARAM_EXPORT_CONFIG_DATA_SET
				+ "\">Export Config Data Set</label>");
		writeln("        </td>");
		writeln("        <td class=\"ebx_Input\">");
		writeln("          <input value=\"" + exportConfigDataSet
				+ "\" type=\"text\" id=\"" + ExportFunctionalityServlet.PARAM_EXPORT_CONFIG_DATA_SET
				+ "\" name=\"" + ExportFunctionalityServlet.PARAM_EXPORT_CONFIG_DATA_SET + "\"/>");;
		writeln("        </td>");
		writeln("      </tr>");
		
		writeln("      <tr class=\"ebx_Field\">");
		writeln("        <td class=\"ebx_Label\">");
		writeln("          <label for=\"" + ExportFunctionalityServlet.PARAM_EXPORT_CONFIG_ID
				+ "\">Export Config ID</label>");
		writeln("        </td>");
		writeln("        <td class=\"ebx_Input\">");
		writeln("          <input value=\"" + exportConfigID
				+ "\" type=\"text\" id=\"" + ExportFunctionalityServlet.PARAM_EXPORT_CONFIG_ID
				+ "\" name=\"" + ExportFunctionalityServlet.PARAM_EXPORT_CONFIG_ID + "\"/>");
		writeln("        </td>");
		writeln("      </tr>");
		
		writeln("      <tr class=\"ebx_Field\">");
		writeln("        <td class=\"ebx_Label\">");
		writeln("          <label for=\"" + ExportFunctionalityServlet.PARAM_SQL_REPORTS
				+ "\">Execute SQL Reports</label>");
		writeln("        </td>");
		writeln("        <td class=\"ebx_Input\">");
		writeln("          <input value=\"true\" type=\"checkbox\" checked=\"checked\" id=\""
				+ ExportFunctionalityServlet.PARAM_SQL_REPORTS
				+ "\" name=\"" + ExportFunctionalityServlet.PARAM_SQL_REPORTS + "\"/>");
		writeln("        </td>");
		
		writeln("      </tr>");
		
		
		writeln("      <tr class=\"ebx_Field\">");
		writeln("        <td class=\"ebx_Label\">");
		writeln("          <label for=\"" + ExportFunctionalityServlet.PARAM_HT_GEN
				+ "\">Execute HT Generator</label>");
		writeln("        </td>");
		writeln("        <td class=\"ebx_Input\">");
		writeln("          <input value=\"true\" type=\"checkbox\" checked=\"checked\" id=\""
				+ ExportFunctionalityServlet.PARAM_HT_GEN
				+ "\" name=\"" + ExportFunctionalityServlet.PARAM_HT_GEN + "\"/>");
		writeln("        </td>");
		
		writeln("      </tr>");
		
		
		writeln("      <tr class=\"ebx_Field\" style=\"visibility:hidden\">");
		writeln("        <td class=\"ebx_Label\">");
		writeln("          <label for=\"" + ExportFunctionalityServlet.PARAM_VALIDATION_ACTION
				+ "\">Validation Action</label>");
		writeln("        </td>");
		writeln("        <td class=\"ebx_Input\">");
		writeln("          <input value=\"" + DEFAULT_VALIDATION_ACTION
				+ "\" type=\"text\" id=\"" + ExportFunctionalityServlet.PARAM_VALIDATION_ACTION
				+ "\" name=\"" + ExportFunctionalityServlet.PARAM_VALIDATION_ACTION + "\"/>");
		writeln("        </td>");
		writeln("      </tr>");
		
		writeln("    </tbody>");
		writeln("  </table>");
	}
	
	protected void writeSubmitButton(ServiceContext sContext) throws IOException {
		LOG.debug("DataSetExportForm: writeSubmitButton");
		writeln("  <button type=\"submit\" class=\"ebx_Button\">Export</button>");
	}
	
	protected void write(final String str) throws IOException {
		out.write(str);
	}
	
	protected void writeln(final String str) throws IOException {
		write(str);
		out.newLine();
	}
}
