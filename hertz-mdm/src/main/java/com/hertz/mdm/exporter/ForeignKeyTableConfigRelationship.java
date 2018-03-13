package com.hertz.mdm.exporter;

/**
 * A class that associates a foreign key field with a table config
 */
public class ForeignKeyTableConfigRelationship {
	protected String foreignKeyPath;
	protected ExportFunctionalityTableConfig tableConfig;
	
	public ForeignKeyTableConfigRelationship() {
	}
	
	public ForeignKeyTableConfigRelationship(final String foreignKeyPath, final ExportFunctionalityTableConfig tableConfig) {
		this.foreignKeyPath = foreignKeyPath;
		this.tableConfig = tableConfig;
	}

	/**
	 * Get the foreign key path
	 * 
	 * @return the foreign key path
	 */
	public String getForeignKeyPath() {
		return foreignKeyPath;
	}

	/**
	 * Set the foreign key path
	 * 
	 * @param foreignKeyPath the foreign key path
	 */
	public void setForeignKeyPath(String foreignKeyPath) {
		this.foreignKeyPath = foreignKeyPath;
	}

	/**
	 * Get the linked table config
	 * 
	 * @return the linked table config
	 */
	public ExportFunctionalityTableConfig getTableConfig() {
		return tableConfig;
	}

	/**
	 * Set the linked table config
	 * 
	 * @param tableConfig the linked table config
	 */
	public void setTableConfig(ExportFunctionalityTableConfig tableConfig) {
		this.tableConfig = tableConfig;
	}
}