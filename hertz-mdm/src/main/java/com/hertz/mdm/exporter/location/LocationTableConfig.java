package com.hertz.mdm.exporter.location;

import com.hertz.mdm.exporter.ExportFunctionalityTableConfig;

/**
 * A table config for use with the function table
 */
public class LocationTableConfig extends ExportFunctionalityTableConfig {
	public enum LocationExportType {
		// Currently only one type but there will be more
		FLAT
	}
	
	private static final String DEFAULT_TABLE_EXPORT_NAME = "LOCATION";
	
	protected LocationExportType locationExportType;
	protected String tableExportName = DEFAULT_TABLE_EXPORT_NAME;

	/**
	 * Get the Location export type
	 * 
	 * @return the Location export type
	 */
	public LocationExportType getLocationExportType() {
		return locationExportType;
	}

	/**
	 * @param LocationExportType the Location export type
	 */
	public void setLocationExportType(LocationExportType locationExportType) {
		this.locationExportType = locationExportType;
	}
	
	/**
	 * Get the name of the table. This is how it should show in the export, which may not
	 * correspond to its ID or label in EBX.
	 * 
	 * @return the name of the table
	 */
	public String getTableExportName() {
		return tableExportName;
	}

	/**
	 * Set the name of the table. This is how it should show in the export, which may not
	 * correspond to its ID or label in EBX.
	 * 
	 * @param tableExportName the name of the table
	 */
	public void setTableExportName(String tableExportName) {
		this.tableExportName = tableExportName;
	}
}
