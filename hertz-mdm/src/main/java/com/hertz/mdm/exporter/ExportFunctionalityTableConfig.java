package com.hertz.mdm.exporter;

import java.util.List;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationFilter;
import com.orchestranetworks.schema.Path;

/**
 * Encapsulates configuration details for a table export
 */
public class ExportFunctionalityTableConfig {
	public enum ExportType {
		BASIC, FORMATTED, MULTI_TABLE_JOIN
	}
	
	public enum ColumnHeaderType {
		NONE, LABEL, XPATH
	}
	
	private ExportType type;
	private Adaptation externalDataSet;
	private String tablePath;
	private String fileName;
	private List<Path> includeFieldPaths;
	private List<Path> excludeFieldPaths;
	private String fileEncoding;
	private ColumnHeaderType columnHeader;
	private char separator;
	private AdaptationFilter filter;
	private BasicExportFunctionalityOptions basicOptions;
	private FormattedDataSetExportOptions formattedOptions;
	private MultiTableJoinExportFunctionalityOptions multiTableJoinOptions;
	
	/**
	 * Get the type of export
	 * 
	 * @return the type of export
	 */
	public ExportType getType() {
		return type;
	}
	
	/**
	 * Set the type of export
	 * 
	 * @param type the type of export
	 */
	public void setType(ExportType type) {
		this.type = type;
	}

	/**
	 * Get the external data set to look the table up in. If not specified, the current data set will be assumed.
	 * 
	 * @return the external data set
	 */
	public Adaptation getExternalDataSet() {
		return externalDataSet;
	}

	/**
	 * Set the external data set to look the table up in. If not specified, the current data set will be assumed.
	 * 
	 * @param externalDataSet the external data set
	 */
	public void setExternalDataSet(Adaptation externalDataSet) {
		this.externalDataSet = externalDataSet;
	}

	/**
	 * Get the table path. This isn't necessarily an EBX path because it could be null or be a path
	 * followed by an asterick (i.e. /root/*).
	 * 
	 * @return the table path
	 */
	public String getTablePath() {
		return tablePath;
	}
	
	/**
	 * Set the table path. This isn't necessarily an EBX path because it could be null or be a path
	 * followed by an asterick (i.e. "/root/*").
	 * 
	 * @param tablePath the table path
	 */
	public void setTablePath(String tablePath) {
		this.tablePath = tablePath;
	}
	
	/**
	 * Get the filename. This can be an actual file name (without directory path), or it can be null,
	 * or it can contain a token to be replaced by the exporter (i.e. "[NAME].dat").
	 * 
	 * @return the filename
	 */
	public String getFileName() {
		return fileName;
	}
	
	/**
	 * Set the filename. This can be an actual file name (without directory path), or it can be null,
	 * or it can contain a token to be replaced by the exporter (i.e. "[NAME].dat").
	 * 
	 * @param fileName the filename
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	/**
	 * Get the list of field paths to include in the export. If specified, only these fields will be exported.
	 * This parameter shouldn't be specified at the same time as excludeFieldPaths.
	 * 
	 * @return the list of field paths to include
	 */
	public List<Path> getIncludeFieldPaths() {
		return includeFieldPaths;
	}
	
	/**
	 * Set the list of field paths to include in the export. If specified, only these fields will be exported.
	 * This parameter shouldn't be specified at the same time as excludeFieldPaths.
	 * 
	 * @param includeFieldPaths the list of field paths to include
	 */
	public void setIncludeFieldPaths(List<Path> includeFieldPaths) {
		this.includeFieldPaths = includeFieldPaths;
	}
	
	/**
	 * Get the list of field paths to exclude from the export. If specified, all fields except for these will be exported.
	 * This parameter shouldn't be specified at the same time as includeFieldPaths.
	 * 
	 * @return the list of field paths to exclude
	 */
	public List<Path> getExcludeFieldPaths() {
		return excludeFieldPaths;
	}
	
	/**
	 * Set the list of field paths to exclude from the export. If specified, all fields except for these will be exported.
	 * This parameter shouldn't be specified at the same time as includeFieldPaths.
	 * 
	 * @param excludeFieldPaths the list of field paths to exclude
	 */
	public void setExcludeFieldPaths(List<Path> excludeFieldPaths) {
		this.excludeFieldPaths = excludeFieldPaths;
	}
	
	/**
	 * Get the file encoding (i.e. "UTF-8").
	 * 
	 * @return the file encoding
	 */
	public String getFileEncoding() {
		return fileEncoding;
	}
	
	/**
	 * Set the file encoding (i.e. "UTF-8").
	 * 
	 * @param fileEncoding the file encoding
	 */
	public void setFileEncoding(String fileEncoding) {
		this.fileEncoding = fileEncoding;
	}
	
	/**
	 * Get the column header type
	 * 
	 * @return the column header type
	 */
	public ColumnHeaderType getColumnHeader() {
		return columnHeader;
	}
	
	/**
	 * Set the column header type
	 * 
	 * @param columnHeader the column header type
	 */
	public void setColumnHeader(ColumnHeaderType columnHeader) {
		this.columnHeader = columnHeader;
	}
	
	/**
	 * Get the character to use as a separator between columns
	 * 
	 * @return the character to use as a separator
	 */
	public char getSeparator() {
		return separator;
	}
	
	/**
	 * Set the character to use as a separator between columns
	 * 
	 * @param separator the character to use as a separator
	 */
	public void setSeparator(char separator) {
		this.separator = separator;
	}
	
	/**
	 * Get the filter to apply to the export (or null if all rows are to be exported)
	 * 
	 * @return the filter
	 */
	public AdaptationFilter getFilter() {
		return filter;
	}
	
	/**
	 * Set the filter to apply to the export (or null if all rows are to be exported)
	 * 
	 * @param filter the filter
	 */
	public void setFilter(AdaptationFilter filter) {
		this.filter = filter;
	}
	
	/**
	 * Get the basic export options
	 * 
	 * @return the basic export options
	 */
	public BasicExportFunctionalityOptions getBasicOptions() {
		return basicOptions;
	}
	
	/**
	 * Set the basic export options
	 * 
	 * @param basicOptions the basic export options
	 */
	public void setBasicOptions(BasicExportFunctionalityOptions basicOptions) {
		this.basicOptions = basicOptions;
	}
	
	/**
	 * Get the formatted export options
	 * 
	 * @return the formatted export options
	 */
	public FormattedDataSetExportOptions getFormattedOptions() {
		return formattedOptions;
	}
	
	/**
	 * Set the formatted export options
	 * 
	 * @param formattedOptions the formatted export options
	 */
	public void setFormattedOptions(FormattedDataSetExportOptions formattedOptions) {
		this.formattedOptions = formattedOptions;
	}

	/**
	 * Get the multi table join export options
	 * 
	 * @return the multi table join export options
	 */
	public MultiTableJoinExportFunctionalityOptions getMultiTableJoinOptions() {
		return multiTableJoinOptions;
	}

	/**
	 * Set the multi table join export options
	 * 
	 * @param multiTableJoinOptions the multi table join export options
	 */
	public void setMultiTableJoinOptions(
			MultiTableJoinExportFunctionalityOptions multiTableJoinOptions) {
		this.multiTableJoinOptions = multiTableJoinOptions;
	}
}
