package com.hertz.mdm.exporter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.hertz.mdm.exporter.ExportFunctionalityBean;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.service.LoggingCategory;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.Session;

/**
 * This exports multiple tables from a data set using a specified configuration
 */
public class ExportFunctionalityExporter {
	protected static final LoggingCategory LOG = LoggingCategory.getKernel();
	
	private static final String FILE_NAME_NAME_VAR = "[NAME]";
	private static final String FILE_NAME_LABEL_VAR = "[LABEL]";
	private static final String DEFAULT_FILE_NAME = "[NAME].dat";
	
	protected ExportFunctionalityConfig config;
	protected TableExporterFactory tableExporterFactory;
	
	public ExportFunctionalityExporter() {
		this.tableExporterFactory = new DefaultTableExporterFactory();
	}
	
	public ExportFunctionalityExporter(final TableExporterFactory tableExporterFactory,
			final ExportFunctionalityConfig config) {
		this.tableExporterFactory = tableExporterFactory;
		this.config = config;
		System.out.println("Exporter Class called");
	}
	
	/**
	 * Get the config
	 * 
	 * @return the config
	 */
	public ExportFunctionalityConfig getConfig() {
		return config;
	}

	/**
	 * Set the config
	 * 
	 * @param config the config
	 */
	public void setConfig(ExportFunctionalityConfig config) {
		this.config = config;
	}
	
	/**
	 * Get the factory used to create the table exporters
	 * 
	 * @return the factory
	 */
	public TableExporterFactory getTableExporterFactory() {
		return tableExporterFactory;
	}

	/**
	 * Set the factory used to create the table exporters
	 * 
	 * @param tableExporterFactory the factory
	 */
	public void setTableExporterFactory(TableExporterFactory tableExporterFactory) {
		this.tableExporterFactory = tableExporterFactory;
	}

	public ExportFunctionalityBean exportDataSet(Session session, Adaptation dataSet) throws IOException, OperationException {
		LOG.info("DataSetExporter: exportDataSet");
		
		LOG.info("User running the export is " + session.getUserReference().getUserId() + ", export Config ID = " + config.getExportConfigID());
		
		LOG.info("DataSetExporter: dataSet = " + dataSet.getAdaptationName().getStringName());
		final Set<ExportFunctionalityTableConfig> tableConfigs = config.getTableConfigs();
		
		ExportFunctionalityBean htBean = new ExportFunctionalityBean();
		htBean.setOutputFilePath(config.getFolder().getAbsolutePath());
		htBean.setConfigID(config.getExportConfigID());
		System.out.println("htbean class" + htBean.toString());
		
		// The same table path can apply to multiple table configs, so we will load them into a map
		// with the table path as key and a set of table configs as value.
		final HashMap<String, Set<ExportFunctionalityTableConfig>> tableConfigMap = new HashMap<String, Set<ExportFunctionalityTableConfig>>();
		for (ExportFunctionalityTableConfig tableConfig: tableConfigs) {
			// Table configs for an external data set have to be treated special
			// so don't put them in the same collection with the others
			if (tableConfig.getExternalDataSet() == null) {
				final String tablePath = tableConfig.getTablePath();
				Set<ExportFunctionalityTableConfig> tableConfigSet = tableConfigMap.get(tablePath);
				if (tableConfigSet == null) {
					tableConfigSet = new HashSet<ExportFunctionalityTableConfig>();
					tableConfigSet.add(tableConfig);
					tableConfigMap.put(tablePath,  tableConfigSet);
				} else {
					tableConfigSet.add(tableConfig);
				}
			}
		}
		
		// Find all the table nodes in the data set. They could be within groups as well.
		final Set<SchemaNode> allTableNodes = ExporterUtils.getAllTableNodes(dataSet.getSchemaNode());
		for (SchemaNode tableNode: allTableNodes) {
			final Path tablePath = tableNode.getPathInSchema();
			// Find the table configs for the table node's path.
			final Set<ExportFunctionalityTableConfig> tableConfigSet =
					getTableConfigsForPath(tablePath, tablePath.format(), tableConfigMap);
			if (tableConfigSet != null) {
				final AdaptationTable table = dataSet.getTable(tablePath);
				for (ExportFunctionalityTableConfig tableConfig: tableConfigSet) {
					exportTable(session, table,htBean, tableConfig);
				}
			}
		}
		
		final List<String> fileNames = new ArrayList<String>();
		// Handle the table configs that are for an external data set (which we skipped earlier)
		for (ExportFunctionalityTableConfig tableConfig: tableConfigs) {
			
			fileNames.add(tableConfig.getFileName()); 
			
			if (tableConfig.getExternalDataSet() != null) {
				final Path tablePath = Path.parse(tableConfig.getTablePath());
				final AdaptationTable table = tableConfig.getExternalDataSet().getTable(tablePath);
				
				exportTable(session, table,htBean, tableConfig);
			}
		}
		htBean.setFileNames(fileNames);
		
		return htBean;
	}
	
	protected File getFileForTable(SchemaNode tableNode, ExportFunctionalityTableConfig tableConfig, Locale locale) {
		LOG.debug("DataSetExporter: getFileForTable");
		LOG.debug("DataSetExporter: tableNode = " + tableNode.getPathInSchema().format());
		String fileName = tableConfig.getFileName();
		LOG.debug("DataSetExporter: filename from config = " + fileName);
		// If not specifed, use default
		if (fileName == null) {
			fileName = DEFAULT_FILE_NAME;
		}
		if (fileName.contains(FILE_NAME_NAME_VAR)) {
			// Substitute the name of the table
			fileName = fileName.replace(FILE_NAME_NAME_VAR, tableNode.getPathInSchema().getLastStep().format());
		} else if (fileName.contains(FILE_NAME_LABEL_VAR)) {
			// Substitute the label of the table
			fileName = fileName.replace(FILE_NAME_LABEL_VAR, tableNode.getLabel(locale));
		}
		LOG.debug("DataSetExporter: filename = " + fileName);
		final File file = new File(config.getFolder(), fileName);
		file.setReadable(true, false);
		file.setWritable(true, true);
		file.setExecutable(true, false);
		return file;
	}
	
	private void exportTable(final Session session, final AdaptationTable table,ExportFunctionalityBean exportBean,
			final ExportFunctionalityTableConfig tableConfig) throws IOException, OperationException {
		// Get the file to output to
		final File file = getFileForTable(table.getTableNode(), tableConfig, session.getLocale());
		final FileOutputStream fileOut = new FileOutputStream(file);
		
		try {
			final TableExporter tableExporter = tableExporterFactory.getTableExporter(
					tableConfig, table);
			tableExporter.exportTable(session,table,exportBean, fileOut, tableConfig, tableExporterFactory);
		} finally {
			fileOut.close();
		}
	}
	
	private static Set<ExportFunctionalityTableConfig> getTableConfigsForPath(
			final Path tablePath, final String mapKey,
			final Map<String, Set<ExportFunctionalityTableConfig>> tableConfigMap) {
		LOG.debug("DataSetExporter: getTableConfigsForPath");
		LOG.debug("DataSetExporter: tablePath = " + tablePath.format());
		LOG.debug("DataSetExporter: mapKey = " + mapKey);
		// The table path may be a complete path to a table. It could match multiple
		// tables, such as '/root/*' or '/root/groupA/*'. So we start at the last step
		// and recursively move up the tree until we find one that matches.
		// This way we find the most specific match.
		final Set<ExportFunctionalityTableConfig> tableConfigs = tableConfigMap.get(mapKey);
		if (tableConfigs == null) {
			Path parentPath = tablePath.getPathWithoutLastStep();
			if (parentPath != null && ! "/".equals(parentPath.format())) {
				final String key = parentPath.format() + "/*";
				return getTableConfigsForPath(parentPath, key, tableConfigMap);
			}
		}
		return tableConfigs;
	}
}
