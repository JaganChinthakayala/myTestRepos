package com.hertz.mdm.exporter;

/**
 * Encapsulates options specific to the basic export
 */
public class BasicExportFunctionalityOptions {
	private boolean userFriendlyMode;
	private boolean includeTechnicalData;
	private boolean includeComputedValues;
	
	/**
	 * Gets whether user friendly mode is being used. See EBX documentation on the CSV export for details.
	 * 
	 * @return whether user friendly mode is being used
	 */
	public boolean isUserFriendlyMode() {
		return userFriendlyMode;
	}
	
	/**
	 * Sets whether user friendly mode is being used. See EBX documentation on the CSV export for details.
	 * 
	 * @param userFriendlyMode whether user friendly mode is being used
	 */
	public void setUserFriendlyMode(boolean userFriendlyMode) {
		this.userFriendlyMode = userFriendlyMode;
	}
	
	/**
	 * Gets whether it includes technical data. See EBX documentation on the CSV export for details.
	 * 
	 * @return whether it includes technical data
	 */
	public boolean isIncludeTechnicalData() {
		return includeTechnicalData;
	}
	
	/**
	 * Sets whether it includes technical data. See EBX documentation on the CSV export for details.
	 * 
	 * @param includeTechnicalData whether it includes technical data
	 */
	public void setIncludeTechnicalData(boolean includeTechnicalData) {
		this.includeTechnicalData = includeTechnicalData;
	}
	
	/**
	 * Gets whether it includes computed values. See EBX documentation on the CSV export for details.
	 * 
	 * @return whether it includes computed values
	 */
	public boolean isIncludeComputedValues() {
		return includeComputedValues;
	}
	
	/**
	 * Sets whether it includes computed values. See EBX documentation on the CSV export for details.
	 * 
	 * @param includeComputedValues whether it includes computed values
	 */
	public void setIncludeComputedValues(boolean includeComputedValues) {
		this.includeComputedValues = includeComputedValues;
	}
}
