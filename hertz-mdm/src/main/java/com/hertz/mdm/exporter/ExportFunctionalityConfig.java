package com.hertz.mdm.exporter;

import java.io.File;
import java.util.Set;

/**
 * Encapsulates configuration details for a data set export
 */
public class ExportFunctionalityConfig {
	private File folder;
	private Set<ExportFunctionalityTableConfig> tableConfigs;
	private String exportConfigID;
	
	/**
	 * Get the folder to export it to
	 * 
	 * @return the folder
	 */
	public File getFolder() {
		return folder;
	}
	
	/**
	 * Set the folder to export it to
	 * 
	 * @param folder the folder
	 */
	public void setFolder(File folder) {
		this.folder = folder;
	}

	/**
	 * Get the configs for the tables
	 * 
	 * @return the configs for the tables
	 */
	public Set<ExportFunctionalityTableConfig> getTableConfigs() {
		return tableConfigs;
	}
	
	/**
	 * Set the configs for the tables
	 * 
	 * @param tableConfigs the configs for the tables
	 */
	public void setTableConfigs(Set<ExportFunctionalityTableConfig> tableConfigs) {
		this.tableConfigs = tableConfigs;
	}

	public String getExportConfigID() {
		return exportConfigID;
	}

	/**
	 * Set the configs id  
	 * 
	 * @param exportConfigID of exporter
	 */
	public void setExportConfigID(String exportConfigID) {
		this.exportConfigID = exportConfigID;
	}
}
