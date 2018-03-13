package com.hertz.mdm.exporter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.orchestranetworks.schema.SchemaTypeName;
import com.orchestranetworks.service.OperationException;

public abstract class FormattedExporterUtils {
	private static final char BOOLEAN_FORMAT_SEPARATOR = '/';
	
	private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
	private static final String DEFAULT_TIME_FORMAT = "HH:mm:ss.SSS";
	private static final String DEFAULT_DATE_TIME_FORMAT =
			DEFAULT_DATE_FORMAT + "'T'" + DEFAULT_TIME_FORMAT;
	
	/**
	 * Determine what the output string is based on the schema type and the patterns
	 * 
	 * @param type the schema type
	 * @param patterns the patterns
	 * @param value the value
	 * @return the formatted string
	 * @throws OperationException if an error happens while formatting
	 */
	public static String getStringForType(final SchemaTypeName type,
			final Map<FormattedDataSetExportOptions.PatternType, String> patterns,
			final Object value) throws OperationException {
		if (value == null) {
			return "";
		}
		if (SchemaTypeName.XS_DATE.equals(type)) {
			final String pattern = patterns.get(FormattedDataSetExportOptions.PatternType.DATE);
			final SimpleDateFormat dateFormat = new SimpleDateFormat(
					pattern == null ? DEFAULT_DATE_FORMAT : pattern);
			return dateFormat.format((Date) value);
		}
		if (SchemaTypeName.XS_DATETIME.equals(type)) {
			final String pattern = patterns.get(FormattedDataSetExportOptions.PatternType.DATE_TIME);
			final SimpleDateFormat dateFormat = new SimpleDateFormat(
					pattern == null ? DEFAULT_DATE_TIME_FORMAT : pattern);
			return dateFormat.format((Date) value);
		}
		if (SchemaTypeName.XS_TIME.equals(type)) {
			final String pattern = patterns.get(FormattedDataSetExportOptions.PatternType.TIME);
			final SimpleDateFormat dateFormat = new SimpleDateFormat(
					pattern == null ? DEFAULT_TIME_FORMAT : pattern);
			return dateFormat.format((Date) value);
		}
		if (SchemaTypeName.XS_BOOLEAN.equals(type)) {
			// If a boolean pattern was specified, the first part will be the true value, followed by the false value
			final String pattern = patterns.get(FormattedDataSetExportOptions.PatternType.BOOLEAN);
			if (pattern == null) {
				return value.toString();
			}
			final int sepIndex = pattern.indexOf(BOOLEAN_FORMAT_SEPARATOR);
			if (sepIndex == -1) {
				throw OperationException.createError("Invalid boolean format: " + pattern);
			}
			final String trueValue = pattern.substring(0, sepIndex);
			final String falseValue = pattern.substring(sepIndex + 1);
			return ((Boolean) value).booleanValue() ? trueValue : falseValue;
		}
		if (SchemaTypeName.XS_INTEGER.equals(type) || SchemaTypeName.XS_INT.equals(type)) {
			final String pattern = patterns.get(FormattedDataSetExportOptions.PatternType.INTEGER);
			if (pattern == null) {
				return value.toString();
			}
			final DecimalFormat numberFormat = new DecimalFormat(pattern);
			return numberFormat.format((Integer) value);
		}
		if (SchemaTypeName.XS_DECIMAL.equals(type)) {
			final String pattern = patterns.get(FormattedDataSetExportOptions.PatternType.DECIMAL);
			if (pattern == null) {
				return value.toString();
			}
			final DecimalFormat numberFormat = new DecimalFormat(pattern);
			return numberFormat.format(((BigDecimal) value).doubleValue());
		}
		return value.toString();
	}
}
