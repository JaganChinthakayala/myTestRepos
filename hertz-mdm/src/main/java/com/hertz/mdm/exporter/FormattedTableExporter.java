package com.hertz.mdm.exporter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.hertz.mdm.common.constants.HtzCommonConstants;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationFilter;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.Request;
import com.onwbp.adaptation.RequestResult;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.PathAccessException;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.schema.SchemaTypeName;
import com.orchestranetworks.service.LoggingCategory;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.Session;

/**
 * Runs a request and loops through the results to a file, outputting each value according to specified formatting
 */
public class FormattedTableExporter implements TableExporter {
	
	private final String PRT_EFFDT = "/PRT_EFFDT";
	private final String SPACE = "/SPACE";
	private final String BLANK = "/BLANK";
	
	protected static final LoggingCategory LOG = LoggingCategory.getKernel();

	@Override
	public void exportTable(Session session, AdaptationTable table,ExportFunctionalityBean exportBean,
			OutputStream out, ExportFunctionalityTableConfig tableConfig,
			TableExporterFactory tableExporterFactory)
			throws IOException, OperationException {
		LOG.debug("FormattedTableExporter: exportTable");
		LOG.debug("FormattedTableExporter: table = " + table.getTablePath().format());
		// Get the nodes to be exported
		final List<SchemaNode> fieldNodes = ExporterUtils.getFieldNodes(table.getTableNode(),
				tableConfig.getIncludeFieldPaths(), tableConfig.getExcludeFieldPaths());

		final Charset charset = Charset.forName(tableConfig.getFileEncoding());
		final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, charset));
		
		
		
		try {
			writeHeader(writer, table, tableConfig, fieldNodes, session.getLocale());
		
			// If you specify no predicate, it will request every record in the table
			// (that meets any filter you set)
			final Request request = table.createRequest();
			final AdaptationFilter filter = tableConfig.getFilter();
			
			/** set tree effective date to last month for specfic filters */
			
			if (filter != null) {
				request.setSpecificFilter(filter);
			}
			final RequestResult reqRes = request.execute();
			exportBean.getFileNamesAndCount().put(tableConfig.getFileName(), reqRes.getSize());
			try {
				writeResults(writer, tableConfig, reqRes, fieldNodes);
			} finally {
				reqRes.close();
			}
		} finally {
			writer.close();
			LOG.info("File writing complete for " + tableConfig.getFileName());
		}
	
	}
	
	
	/**
	 * Write the header. Won't write anything if column header is NONE.
	 * 
	 * @param writer the writer
	 * @param tableConfig the table config
	 * @param fieldNodes the field nodes to export
	 * @param locale the locale
	 * @throws IOException if there's an exception writing the output
	 * @throws OperationException if an error occurs getting the header
	 */
	protected void writeHeader(BufferedWriter writer, AdaptationTable table, ExportFunctionalityTableConfig tableConfig,
			List<SchemaNode> fieldNodes, Locale locale) throws IOException, OperationException {
		LOG.debug("FormattedTableExporter: writeHeader");
		if (tableConfig.getColumnHeader() != ExportFunctionalityTableConfig.ColumnHeaderType.NONE) {
			final String header = getHeader(table, tableConfig, fieldNodes, locale);
			LOG.debug("FormattedTableExporter: header = " + header);
			writer.write(header);
			writer.newLine();
		}
	}
	
	/**
	 * Write the results, one line per record. The caller of this method is responsible for closing the
	 * RequestResult when finished.
	 * 
	 * @param writer the writer
	 * @param tableConfig the table config
	 * @param reqRes the results from the query
	 * @param fieldNodes the field nodes to export
	 * @throws IOException if there's an exception writing the output
	 * @throws OperationException if an error happens while exporting
	 */
	protected void writeResults(BufferedWriter writer, ExportFunctionalityTableConfig tableConfig, RequestResult reqRes,
			List<SchemaNode> fieldNodes) throws IOException, OperationException {
		LOG.debug("FormattedTableExporter: writeResults");
		for (Adaptation adaptation; (adaptation = reqRes.nextAdaptation()) != null;) {
			writeRecord(writer, tableConfig, adaptation, fieldNodes);
		}
	}
	
	/**
	 * Write a record
	 * 
	 * @param writer the writer
	 * @param tableConfig the table config
	 * @param record the record
	 * @param fieldNodes the field nodes to export
	 * @throws IOException if there's an exception writing the output
	 * @throws OperationException if an error happens while exporting
	 */
	protected void writeRecord(BufferedWriter writer, ExportFunctionalityTableConfig tableConfig, Adaptation record,
			List<SchemaNode> fieldNodes) throws IOException, OperationException {
		if (LOG.isDebug()) {
			LOG.debug("FormattedTableExporter: writeRecord");
			LOG.debug("FormattedTableExporter: record = " + record.getOccurrencePrimaryKey().format());
		}
		final String recordLine = getRecordLine(record, tableConfig, fieldNodes);
		if (LOG.isDebug()) {
			LOG.debug("FormattedTableExporter: recordLine = " + recordLine);
		}
		writer.write(recordLine);
		writer.newLine();
	}
	
	/**
	 * Get the string representing the header
	 * 
	 * @param table the table
	 * @param tableConfig the table config
	 * @param fieldNodes the field nodes to export
	 * @param locale the locale
	 * @return the string representing the header
	 * @throws OperationException if an error occurs while getting the header
	 */
	protected String getHeader(AdaptationTable table, ExportFunctionalityTableConfig tableConfig,
			List<SchemaNode> fieldNodes, Locale locale) throws OperationException {
		LOG.debug("FormattedTableExporter: getHeader");
		final StringBuffer strBuff = new StringBuffer();
		// Get the individual column values
		final List<String> headerValues = getHeaderValues(
				tableConfig.getColumnHeader(), fieldNodes, locale);
		// Loop through and append them, with the separator in between
		for (int i = 0; i < headerValues.size(); i++) {
			strBuff.append(headerValues.get(i));
			if (i < headerValues.size() - 1) {
				strBuff.append(tableConfig.getSeparator());
			}
		}
		return strBuff.toString();
	}
	
	/**
	 * Get the values for the columns of the header in a list
	 * 
	 * @param columnHeader the column header type
	 * @param fieldNodes the field nodes to export
	 * @param locale the locale
	 * @return the values for the columns of the header
	 */
	protected List<String> getHeaderValues(ExportFunctionalityTableConfig.ColumnHeaderType columnHeader,
			List<SchemaNode> fieldNodes, Locale locale) {
		LOG.debug("FormattedTableExporter: getHeaderValues");
		final ArrayList<String> headerValues = new ArrayList<String>();
		// Loop through each field node and add either its label or xpath based on the column type
		for (int i = 0; i < fieldNodes.size(); i++) {
			final SchemaNode fieldNode = fieldNodes.get(i);
			switch (columnHeader) {
			case LABEL:
				final String fieldLabel = fieldNode.getLabel(locale);
				headerValues.add(fieldLabel);
				break;
			case XPATH:
				final String fieldPath = fieldNode.getPathInAdaptation().format();
				headerValues.add(fieldPath);
				break;
			}
		}
		return headerValues;
	}
	
	/**
	 * Get the line representing a record
	 * 
	 * @param record the record
	 * @param tableConfig the table config
	 * @param fieldNodes the field nodes to export
	 * @return the line representing a record
	 * @throws OperationException if an error happens while exporting
	 */
	protected String getRecordLine(Adaptation record, ExportFunctionalityTableConfig tableConfig,
			List<SchemaNode> fieldNodes) throws OperationException {
		LOG.debug("FormattedTableExporter: getRecordLine");
		final StringBuffer strBuff = new StringBuffer();
		// Get the individual column values
		final List<String> recordValues = getRecordValues(record,
				tableConfig.getFormattedOptions().getPatterns(), fieldNodes);
		// Loop through and append them, with the separator in between
		for (int i = 0; i < recordValues.size(); i++) {
			String recordValue = recordValues.get(i);
			
			strBuff.append(recordValue);
			if (i < recordValues.size() - 1) {
				strBuff.append(tableConfig.getSeparator());
			}
		}
		return strBuff.toString();
		
	}
	
	/**
	 * Get the values for the columns of a record in a list
	 * 
	 * @param record the record
	 * @param patterns the patterns to apply
	 * @param fieldNodes the field nodes to export
	 * @return the values for the columns of a record
	 * @throws OperationException if an error happens while exporting
	 */
	protected List<String> getRecordValues(Adaptation record,
			Map<FormattedDataSetExportOptions.PatternType, String> patterns,
			List<SchemaNode> fieldNodes) throws OperationException {
		LOG.debug("FormattedTableExporter: getRecordValues");
		final ArrayList<String> recordValues = new ArrayList<String>();
		
		// Loop through each field node and add the formatted string representation of its value
		for (int i = 0; i < fieldNodes.size(); i++) {
			final SchemaNode fieldNode = fieldNodes.get(i);
			final Path fieldPath = fieldNode.getPathInAdaptation();
			
			if(null != record){
				try {
					
					Object value = record.get(fieldPath);
					
					
					/** Custom effective date print for Hierarchical Records
					if(null != printEffDateField && printEffDateField.equalsIgnoreCase(String.valueOf(value)) ){
						value = printEffectiveDate;
					//	LOG.debug("replaced Print effective date column");
					}
					
					 */
					
					if(PRT_EFFDT.equalsIgnoreCase(fieldPath.format())){
						value = printEffectiveDate;
					}
					else if(SPACE.equalsIgnoreCase(fieldPath.format())){
						value = HtzCommonConstants.SPACE;
					}
					else if(BLANK.equalsIgnoreCase(fieldPath.format())){
						value = HtzCommonConstants.BLANK_STRING;
					}
					
					final SchemaTypeName type = fieldNode.getXsTypeName();
					final String str = FormattedExporterUtils.getStringForType(type, patterns, value);
					String replacedString= str;
					if(null!=str)
					{
						
						if(str.contains(HtzCommonConstants.NEXT_LINE))
						{
							
							replacedString= replacedString.replaceAll("[\\n\\r]+" , HtzCommonConstants.SPACE);
							
							LOG.debug("removed the new line characters of " + replacedString);
						}
						if(str.contains("\r"))
						{
							
							replacedString= replacedString.replaceAll("[\\n|\\r]+" , HtzCommonConstants.SPACE);
							
							LOG.debug("removed the new for cartage char line characters of " + replacedString);
						}
					}
					recordValues.add(replacedString);
				} catch (PathAccessException e) {
					LOG.error("path access exception " + e.getMessage());
				}
			}
			
		}
		return recordValues;
	}
	
//	private final static String printEffDateField = ResourceBundle.getBundle("com.citi.ebx.dsmt.util.ApplicationResources").getString("hierarchy.print.effective.date.field");
	private String printEffectiveDate = null ; // = new DSMTPropertyHelper().getProp("hierarchy.print.effective.date");
}
