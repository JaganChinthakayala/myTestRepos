package com.hertz.mdm.exporter;

/**
 * A class that associates a foreign key field with a table config, for use with the
 * multi-table join export
 */
public class ForeignKeyFieldConfig {
	private String fkFieldPath;
	private String exportTableConfig;
	
	/**
	 * Get the foreign key field path
	 * 
	 * @return the foreign key field path
	 */
	public String getFkFieldPath() {
		return fkFieldPath;
	}
	
	/**
	 * Set the foreign key field path
	 * 
	 * @param fkFieldPath the foreign key field path
	 */
	public void setFkFieldPath(String fkFieldPath) {
		this.fkFieldPath = fkFieldPath;
	}
	
	/**
	 * Get the table config
	 * 
	 * @return the table config
	 */
	public String getExportTableConfig() {
		return exportTableConfig;
	}
	
	/**
	 * Set the table config
	 * 
	 * @param exportTableConfig the table config
	 */
	public void setExportTableConfig(String exportTableConfig) {
		this.exportTableConfig = exportTableConfig;
	}
}
