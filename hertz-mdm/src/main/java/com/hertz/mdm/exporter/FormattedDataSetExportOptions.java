package com.hertz.mdm.exporter;

import java.util.Map;

/**
 * Encapsulates options specific to the formatted export
 */
public class FormattedDataSetExportOptions {
	public enum PatternType {
		BOOLEAN, DATE, DATE_TIME, DECIMAL, INTEGER, TIME
	}
	
	private Map<PatternType, String> patterns;

	/**
	 * Get the patterns
	 * 
	 * @return the patterns
	 */
	public Map<PatternType, String> getPatterns() {
		return patterns;
	}

	/**
	 * Set the patterns
	 * 
	 * @param patterns the patterns
	 */
	public void setPatterns(Map<PatternType, String> patterns) {
		this.patterns = patterns;
	}
}
