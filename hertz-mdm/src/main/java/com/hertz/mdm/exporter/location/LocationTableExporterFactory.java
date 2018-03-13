package com.hertz.mdm.exporter.location;

import com.onwbp.adaptation.AdaptationTable;
import com.hertz.mdm.exporter.ExportFunctionalityTableConfig;
import com.hertz.mdm.exporter.DefaultTableExporterFactory;
import com.hertz.mdm.exporter.TableExporter;

/**
 * A factory for table exporters that handles function table exports
 */
public class LocationTableExporterFactory extends DefaultTableExporterFactory {
	/**
	 * Overridden in order to handle function exports
	 */
	@Override
	public TableExporter getTableExporter(ExportFunctionalityTableConfig tableConfig, AdaptationTable table) {
		
		return (TableExporter) new LocationFlatTableExporter();
	}
}
