package com.hertz.mdm.exporter.location;

import java.io.IOException;
import java.util.HashMap;
import com.hertz.mdm.exporter.location.LocationTableConfig.LocationExportType;
import com.hertz.mdm.exporter.ExportFunctionalityBean;
import com.onwbp.adaptation.Adaptation;
import com.hertz.mdm.exporter.ExportFunctionalityConfig;
import com.hertz.mdm.exporter.ExportFunctionalityTableConfig;
import com.hertz.mdm.exporter.ExportFunctionalityTableConfig.ColumnHeaderType;
import com.hertz.mdm.exporter.ExportFunctionalityTableConfig.ExportType;
import com.hertz.mdm.exporter.ExportFunctionalityExporter;
import com.hertz.mdm.exporter.FormattedDataSetExportOptions;
import com.hertz.mdm.exporter.FormattedDataSetExportOptions.PatternType;
import com.hertz.mdm.exporter.TableExporterFactory;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.Session;

/**
 * This extends DataSetExporter in order to implement a Function export
 */

public class LocationExporter extends ExportFunctionalityExporter {

	private static final char SEPARATOR = '|';
	
	private static final String LOCATION_FLAT_DATE_PATTERN = "yyyy-MM-dd";
	private static final String location_hierarchy_filename="DSMT_FLAT_FUNC.csv";
	private static final String location_hierarchy_xpath="/root/Location";
	public static final Path REPORT_NAME = Path.parse("./NAME");
	public static final Path REPORT_LOCATION = Path.parse("./LOCATION");
	public static final Path REPORT_ROW_COUNT = Path.parse("./ROW_COUNT");
	
	public LocationExporter() {
		super();
	}
	
	public LocationExporter(final TableExporterFactory tableExporterFactory,
			final ExportFunctionalityConfig config) {
		
		super(tableExporterFactory, config);
	}

	/**
	 * Overridden in order to produce the Function exports
	 */
	@Override
	public ExportFunctionalityBean exportDataSet(Session session, Adaptation dataSet)
			throws IOException, OperationException {
	
		final ExportFunctionalityTableConfig functionFlatTableConfig = createFunctionFlatTableConfig();
		config.getTableConfigs().add(functionFlatTableConfig);
		
		return null;
	}
	
	// Create the table config for the location flat export
	// This config is not read from EBX, but is constructed here
	private ExportFunctionalityTableConfig createFunctionFlatTableConfig() {
		
		final LocationTableConfig tableConfig = new LocationTableConfig();
		tableConfig.setLocationExportType(LocationExportType.FLAT);
		tableConfig.setType(ExportType.FORMATTED);
		tableConfig.setTablePath(location_hierarchy_xpath);
		tableConfig.setFileName(location_hierarchy_filename);
		tableConfig.setColumnHeader(ColumnHeaderType.NONE);
		tableConfig.setSeparator(SEPARATOR);
		final FormattedDataSetExportOptions formattedOptions = new FormattedDataSetExportOptions();
		final HashMap<PatternType, String> patterns = new HashMap<PatternType, String>();
		patterns.put(PatternType.DATE, LOCATION_FLAT_DATE_PATTERN);
		formattedOptions.setPatterns(patterns);
		tableConfig.setFormattedOptions(formattedOptions);
		LOG.info("tableConfig" +tableConfig);
		return tableConfig;
	}
}
