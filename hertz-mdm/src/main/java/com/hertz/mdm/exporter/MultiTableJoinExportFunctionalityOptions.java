package com.hertz.mdm.exporter;

import java.util.List;

/**
 * Encapsulates options specific to the multi table join export
 */
public class MultiTableJoinExportFunctionalityOptions {
	private List<ForeignKeyTableConfigRelationship> linkedTableConfigs;
	
	/**
	 * Get the table configs for the foreign keys.
	 * 
	 * @return the table configs for the foreign keys
	 */
	public List<ForeignKeyTableConfigRelationship> getLinkedTableConfigs() {
		return linkedTableConfigs;
	}
	
	/**
	 * Set the table configs for the foreign keys.
	 * 
	 * @param linkedTableConfigs the table configs for the foreign keys
	 */
	public void setLinkedTableConfigs(
			List<ForeignKeyTableConfigRelationship> linkedTableConfigs) {
		this.linkedTableConfigs = linkedTableConfigs;
	}
}
