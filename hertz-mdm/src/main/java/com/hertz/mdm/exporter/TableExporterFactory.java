package com.hertz.mdm.exporter;

import com.onwbp.adaptation.AdaptationTable;
import com.hertz.mdm.exporter.ExportFunctionalityConfig;

/**
 * An interface for creating table exporters
 */
public interface TableExporterFactory {
	/**
	 * Create a table exporter
	 * 
	 * @param tableConfig the export table config
	 * @param table the table
	 * @return the table exporter
	 */
	public TableExporter getTableExporter(ExportFunctionalityTableConfig tableConfig, AdaptationTable table);
}
