package com.hertz.mdm.exporter;

import com.onwbp.adaptation.AdaptationTable;
import com.hertz.mdm.exporter.ExportFunctionalityTableConfig;
import com.orchestranetworks.service.LoggingCategory;

/**
 * A default implementation of a TableExporterFactory
 */
public class DefaultTableExporterFactory implements TableExporterFactory {
	protected static final LoggingCategory LOG = LoggingCategory.getKernel();
	
	@Override
	public TableExporter getTableExporter(ExportFunctionalityTableConfig tableConfig, AdaptationTable table) {
		LOG.info("DefaultTableExporterFactory: getTableExporter");
		// This simply creates an exporter based on type.
		LOG.info("DefaultTableExporterFactory: type = " + tableConfig.getType());
		TableExporter tableExporter = null;
		switch (tableConfig.getType()) {
		case BASIC:
			tableExporter = new BasicTableExporter();
			break;
			
		case FORMATTED:
			tableExporter = new FormattedTableExporter();
			break;
			
		case MULTI_TABLE_JOIN:
			tableExporter = new MultiTableJoinTableExporter(this);
			break;
		}
		return tableExporter;
	}
}
