package com.hertz.mdm.exporter;

import java.io.IOException;
import java.io.OutputStream;

import com.hertz.mdm.exporter.ExportFunctionalityBean;
import com.onwbp.adaptation.AdaptationTable;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.Session;

/**
 * An interface for exporting a table
 */
public interface TableExporter {
	/**
	 * Export the given table
	 * 
	 * @param session the session
	 * @param table the table
	 * @param out the output stream to export to
	 * @param tableConfig the export table config
	 * @param tableExporterFactory the table exporter factory
	 * @throws IOException if there's an exception writing the output
	 * @throws OperationException if an error happens while exporting
	 */
	void exportTable(Session session, AdaptationTable table,ExportFunctionalityBean exportBean,
			OutputStream out, ExportFunctionalityTableConfig tableConfig,
			TableExporterFactory tableExporterFactory)
			throws IOException, OperationException;
}
