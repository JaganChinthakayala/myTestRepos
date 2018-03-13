package com.hertz.mdm.exporter;

/**
 * A bean class for specifying a parameter to an AdaptationFilter class
 */
public class FilterClassParameter {
	private String name;
	private String value;
	
	/**
	 * Get the parameter name
	 * 
	 * @return the parameter name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Set the parameter name
	 * 
	 * @param name the parameter name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Get the parameter value
	 * 
	 * @return the parameter value
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * Set the parameter value
	 * 
	 * @param value the parameter value
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
