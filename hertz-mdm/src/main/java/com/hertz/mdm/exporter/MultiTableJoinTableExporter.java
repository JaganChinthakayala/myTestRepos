package com.hertz.mdm.exporter;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationFilter;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.PrimaryKey;
import com.onwbp.adaptation.Request;
import com.onwbp.adaptation.RequestResult;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.service.OperationException;

/**
 * This is a FormattedTableExporter that can follow foreign key paths to their related tables and
 * join each row with that result.
 * 
 * Note that in order to do this, all records of the related tables must be read into memory, so there
 * could be performance implications for large sets of data.
 */
public class MultiTableJoinTableExporter extends FormattedTableExporter {
	/**
	 * This stores the results of each table, with the key being the foreign key field path + " " + index,
	 * where index is 0-based and indicates which one to use when the same fk field is listed more than once
	 */
	protected Map<String, CachedResults> cachedResultsMap;
	
	protected TableExporterFactory tableExporterFactory;
	
	public MultiTableJoinTableExporter(TableExporterFactory tableExporterFactory) {
		this.tableExporterFactory = tableExporterFactory;
	}
	
	/**
	 * Get the table exporter factory used to create the linked table configs
	 * 
	 * @return the table exporter factory
	 */
	public TableExporterFactory getTableExporterFactory() {
		return tableExporterFactory;
	}

	/**
	 * Set the table exporter factory used to create the linked table configs
	 * 
	 * @param tableExporterFactory the table exporter factory
	 */
	public void setTableExporterFactory(TableExporterFactory tableExporterFactory) {
		this.tableExporterFactory = tableExporterFactory;
	}

	/**
	 * Overridden to include the header from the linked tables
	 */
	@Override
	protected String getHeader(AdaptationTable table, ExportFunctionalityTableConfig tableConfig,
			List<SchemaNode> fieldNodes, Locale locale) throws OperationException {
		LOG.info("MultiTableJoinTableExporter: getHeader");
		final StringBuffer strBuff = new StringBuffer();
		final List<String> headerValues = getHeaderValues(
				tableConfig.getColumnHeader(), fieldNodes, locale);
		final List<ForeignKeyTableConfigRelationship> linkedTableConfigs =
				tableConfig.getMultiTableJoinOptions().getLinkedTableConfigs();
		// This is a map to keep track of how many of each fk field we've encountered
		final HashMap<String, Integer> fkFieldCountMap = new HashMap<String, Integer>();
		// Loop through the field nodes
		for (int i = 0; i < fieldNodes.size(); i++) {
			if (LOG.isDebug()) {
				LOG.debug("MultiTableJoinTableExporter: fieldNodes[" + i + "] = " + fieldNodes.get(i).getPathInSchema().format());
			}
			final SchemaNode fieldNode = fieldNodes.get(i);
			final String fieldPath = Path.SELF.add(
					fieldNode.getPathInAdaptation()).format();
			Integer count = fkFieldCountMap.get(fieldPath);
			if (count == null) {
				count = Integer.valueOf(0);
			}
			// See if there's a linked table config for this field
			final ExportFunctionalityTableConfig linkedTableConfig =
					findLinkedTableConfig(linkedTableConfigs, fieldPath, count);
			// If there is no linked table config, simply add the header value as normal
			if (linkedTableConfig == null) {
				LOG.debug("MultiTableJoinTableExporter: No linked table config found.");
				strBuff.append(headerValues.get(i));
			// Else it's a fk node with a linked table so we need to add that table's header
			} else {
				LOG.debug("MultiTableJoinTableExporter: Linked table config found.");
				if (cachedResultsMap == null) {
					initCachedResultsMap(table, tableConfig);
				}
				// Get the cached results, which will include the nodes for that table
				final CachedResults cachedResults = cachedResultsMap.get(
						Path.SELF.add(fieldNode.getPathInAdaptation()).format() + " " + count);
				// Call the table exporter to get the header for that table
				final String header = cachedResults.tableExporter.getHeader(
						cachedResults.table, linkedTableConfig, cachedResults.fieldNodes, locale);
				LOG.debug("MultiTableJoinTableExporter: Appending " + header);
				strBuff.append(header);
				// Increase count for this fk field
				fkFieldCountMap.put(fieldPath, Integer.valueOf(count.intValue() + 1));
			}
			if (i < fieldNodes.size() - 1) {
				strBuff.append(tableConfig.getSeparator());
			}
		}
		return strBuff.toString();
	}

	/**
	 * Overridden to include the record values from the linked tables
	 */
	@Override
	protected String getRecordLine(Adaptation record,
			ExportFunctionalityTableConfig tableConfig, List<SchemaNode> fieldNodes)
			throws OperationException {
		LOG.debug("MultiTableJoinTableExporter: getRecordLine");
		final StringBuffer strBuff = new StringBuffer();
		final List<String> recordValues = getRecordValues(record,
				tableConfig.getFormattedOptions().getPatterns(), fieldNodes);
		final List<ForeignKeyTableConfigRelationship> linkedTableConfigs =
				tableConfig.getMultiTableJoinOptions().getLinkedTableConfigs();
		// This is a map to keep track of how many of each fk field we've encountered
		final HashMap<String, Integer> fkFieldCountMap = new HashMap<String, Integer>();
		// Loop through the field nodes
		for (int i = 0; i < fieldNodes.size(); i++) {
			if (LOG.isDebug()) {
				LOG.debug("MultiTableJoinTableExporter: fieldNodes[" + i + "] = " + fieldNodes.get(i).getPathInSchema().format());
			}
			final SchemaNode fieldNode = fieldNodes.get(i);
			final String fieldPath = Path.SELF.add(
					fieldNode.getPathInAdaptation()).format();
			Integer count = fkFieldCountMap.get(fieldPath);
			if (count == null) {
				count = Integer.valueOf(0);
			}
			// See if there's a linked table config for this field
			final ExportFunctionalityTableConfig linkedTableConfig =
					findLinkedTableConfig(linkedTableConfigs, fieldPath, count);
			// If there is no linked table config, simply add the record value as normal
			if (linkedTableConfig == null) {
				LOG.debug("MultiTableJoinTableExporter: No linked table config found.");
				strBuff.append(recordValues.get(i));
			// Else it's a fk node with a linked table so we need to add that table's record values
			} else {
				LOG.debug("MultiTableJoinTableExporter: Linked table config found.");
				if (cachedResultsMap == null) {
					initCachedResultsMap(record.getContainerTable(), tableConfig);
				}
				// Get the cached results, which will include the record and nodes for that table
				final CachedResults cachedResults = cachedResultsMap.get(
						Path.SELF.add(fieldNode.getPathInAdaptation()).format() + " " + count);
				// Get the record from the linked table by looking it up in the cached map based
				// on primary key. Since this field is a foreign key, the value of the field in this table
				// will be the primary key in the linked table.
				final Adaptation linkedRecord = cachedResults.results.get(
						PrimaryKey.parseString(recordValues.get(i)));
				// Call the table exporter to get the record line for that table
				final String line = cachedResults.tableExporter.getRecordLine(
						linkedRecord, linkedTableConfig, cachedResults.fieldNodes);
				LOG.debug("MultiTableJoinTableExporter: Appending " + line);
				strBuff.append(line);
				// Increase count for this fk field
				fkFieldCountMap.put(fieldPath, Integer.valueOf(count.intValue() + 1));
			}
			if (i < fieldNodes.size() - 1) {
				strBuff.append(tableConfig.getSeparator());
			}
		}
		return strBuff.toString();
	}
	
	private void initCachedResultsMap(final AdaptationTable table, final ExportFunctionalityTableConfig tableConfig)
			throws OperationException {
		if (LOG.isDebug()) {
			LOG.debug("MultiTableJoinTableExporter: initCachedResultsMap");
			LOG.debug("MultiTableJoinTableExporter: table = " + table.getTablePath().format());
		}
		cachedResultsMap = new HashMap<String, CachedResults>();
		final List<ForeignKeyTableConfigRelationship> linkedTableConfigRelationships =
				tableConfig.getMultiTableJoinOptions().getLinkedTableConfigs();
		final SchemaNode tableNode = table.getTableNode();
		// This is a map to keep track of how many of each fk field we've encountered
		final HashMap<String, Integer> fkFieldCountMap = new HashMap<String, Integer>();
		// Loop through each foreign key field that is being linked to another table config
		for (ForeignKeyTableConfigRelationship linkedTableConfigRelationship: linkedTableConfigRelationships) {
			final String fkFieldPath = linkedTableConfigRelationship.getForeignKeyPath();
			LOG.info("MultiTableJoinTableExporter: fkFieldPath = " + fkFieldPath);
			final CachedResults cachedResults = new CachedResults();
			final ExportFunctionalityTableConfig linkedTableConfig = linkedTableConfigRelationship.getTableConfig();
			final SchemaNode fkFieldNode = tableNode.getNode(Path.parse(fkFieldPath));
			// Get the table node that the foreign key references
			final SchemaNode linkedTableNode = fkFieldNode.getFacetOnTableReference().getTableNode();
			if (LOG.isDebug()) {
				LOG.debug("MultiTableJoinTableExporter: linkedTableNode = " + linkedTableNode.getPathInSchema().format());
			}
			// Get the field nodes to export for that table node, based on the linked table config
			cachedResults.fieldNodes = ExporterUtils.getFieldNodes(linkedTableNode,
					linkedTableConfig.getIncludeFieldPaths(), linkedTableConfig.getExcludeFieldPaths());
			
			// Get the table being linked to based on the data set & table node
			final Adaptation dataSet;
			if (linkedTableConfig.getExternalDataSet() == null) {
				dataSet = table.getContainerAdaptation();
			} else {
				dataSet = linkedTableConfig.getExternalDataSet();
			}
			cachedResults.table = dataSet.getTable(linkedTableNode.getPathInSchema());
			// Use the table exporter factory to create a table exporter for the table.
			// Note that the multi-table join export only supports formatted exporters (or subclasses).
			cachedResults.tableExporter = (FormattedTableExporter) tableExporterFactory
					.getTableExporter(linkedTableConfig, cachedResults.table);
			// Create the request against the linked table (with no predicate so all rows will be returned, except those
			// filtered out).
			final Request request = cachedResults.table.createRequest();
			final AdaptationFilter filter = linkedTableConfig.getFilter();
			if (filter != null) {
				request.setSpecificFilter(filter);
			}
			cachedResults.results = new HashMap<PrimaryKey, Adaptation>();
			final RequestResult reqRes = request.execute();
			// Save the results in the cached results map, with key being the primary key of the record
			try {
				LOG.debug("MultiTableJoinTableExporter: Caching results from table.");
				for (Adaptation adaptation; (adaptation = reqRes.nextAdaptation()) != null;) {
					if (LOG.isDebug()) {
						LOG.debug("MultiTableJoinTableExporter: adaptation = " + adaptation.getOccurrencePrimaryKey().format());
					}
					cachedResults.results.put(adaptation.getOccurrencePrimaryKey(), adaptation);
				}
			} finally {
				reqRes.close();
			}
			Integer count = fkFieldCountMap.get(fkFieldPath);
			if (count == null) {
				count = Integer.valueOf(0);
			}
			cachedResultsMap.put(fkFieldPath + " " + count, cachedResults);
			// Increase count for this fk field
			fkFieldCountMap.put(fkFieldPath, Integer.valueOf(count.intValue() + 1));
		}
	}
	
	// Find the table config for the specified foreign key and index (starting at 0)
	private static ExportFunctionalityTableConfig findLinkedTableConfig(final List<ForeignKeyTableConfigRelationship> linkedTableConfigs,
			final String fkFieldPath, final int index) {
		int numFound = 0;
		for (ForeignKeyTableConfigRelationship linkedTableConfig: linkedTableConfigs) {
			if (fkFieldPath.equals(linkedTableConfig.getForeignKeyPath())) {
				if (numFound == index) {
					return linkedTableConfig.getTableConfig();
				}
				numFound++;
			}
		}
		return null;
	}
	
	/**
	 * A class that caches results from an EBX request, for use with the multi table join export
	 */
	protected class CachedResults {
		/**
		 * This is the table the results are for
		 */
		protected AdaptationTable table;
		
		/**
		 * This map stores the records of a table,
		 * with key = the primary key of the record and value = the record itself
		 */
		protected Map<PrimaryKey, Adaptation> results;
		
		/**
		 * The exporter to use for the table
		 */
		protected FormattedTableExporter tableExporter;
		
		/**
		 * The field nodes to export for the table
		 */
		protected List<SchemaNode> fieldNodes;
	}
}
