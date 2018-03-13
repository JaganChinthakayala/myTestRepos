package com.hertz.mdm.exporter;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationFilter;
import com.onwbp.adaptation.AdaptationHome;
import com.onwbp.adaptation.AdaptationName;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.PrimaryKey;
import com.orchestranetworks.instance.HomeKey;
import com.hertz.mdm.exporter.ExportFunctionalityTableConfig.ExportType;
import com.hertz.mdm.exporter.ExportFunctionalityExportPaths;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.service.LoggingCategory;
import com.orchestranetworks.service.OperationException;

/**
 * Factory for creating a DataSetExportConfig
 */
public class ExportFunctionalityConfigFactory {
	protected static final LoggingCategory LOG = LoggingCategory.getKernel();
	
	// These constants must match the strings in EBX for type, column, pattern
	private static final String TYPE_BASIC = "basic";
	private static final String TYPE_FORMATTED = "formatted";
	private static final String TYPE_MULTI_TABLE_JOIN = "multi_table_join";
	
	private static final String COLUMN_HEADER_NONE = "none";
	private static final String COLUMN_HEADER_LABEL = "label";
	private static final String COLUMN_HEADER_XPATH = "xpath";
	
	private static final String PATTERN_TYPE_BOOLEAN = "boolean";
	private static final String PATTERN_TYPE_DATE = "date";
	private static final String PATTERN_TYPE_DATE_TIME = "dateTime";
	private static final String PATTERN_TYPE_DECIMAL = "decimal";
	private static final String PATTERN_TYPE_INTEGER = "integer";
	private static final String PATTERN_TYPE_TIME = "time";
	
	/**
	 * Create the DataSetExportConfig from an export config record in EBX
	 * 
	 * @param exportConfigDataSet the data set for the export config records
	 * @param exportConfigID the ID of the export config record
	 * @return the created DataSetExportConfig
	 */
	public ExportFunctionalityConfig createFromEBXRecord(final Adaptation exportConfigDataSet,
			final String exportConfigID) throws OperationException {
		LOG.info("DataSetExportConfigFactory: createFromEBXRecord");
		// Find the  record for the given ID
		final Adaptation configAdaptation = loadExportConfigAdaptation(exportConfigDataSet, exportConfigID);
		@SuppressWarnings("unchecked")
		// Get the list of foreign keys to the table configs and load all of those records
		final List<String> fks = configAdaptation.getList(ExportFunctionalityExportPaths._Root_ExportConfig._ExportTableConfig);
		final Set<Adaptation> tableConfigAdaptations = loadExportTableConfigAdaptations(exportConfigDataSet, fks);
		
		final ExportFunctionalityConfig config = new ExportFunctionalityConfig();
		
		final String folderName = configAdaptation.getString(ExportFunctionalityExportPaths._Root_ExportConfig._FolderName);
		// Get the folder, and if it doesn't exist, create it
		final File folder = new File(folderName);
		if (! folder.exists()) {
			folder.mkdir();
		}
		config.setFolder(folder);
		
		HashSet<ExportFunctionalityTableConfig> tableConfigs = new HashSet<ExportFunctionalityTableConfig>();
		// Create the table configs from the records we got from EBX
		for (Adaptation adaptation: tableConfigAdaptations) {
			tableConfigs.add(createExportTableConfigFromAdaptation(adaptation, exportConfigDataSet));
		}
		config.setTableConfigs(tableConfigs);
		config.setExportConfigID(exportConfigID);
		
		return config;
	}
	
	private static ExportFunctionalityTableConfig createExportTableConfigFromAdaptation(
			final Adaptation adaptation, final Adaptation exportConfigDataSet)
			throws OperationException {
		LOG.debug("DataSetExportConfigFactory: createExportTableConfigFromAdaptation");
		final ExportFunctionalityTableConfig tableConfig = new ExportFunctionalityTableConfig();
		
		final String adaptationType = adaptation.getString(ExportFunctionalityExportPaths._Root_ExportTableConfig._Type);
		final ExportType type = convertToType(adaptationType);
		tableConfig.setType(type);
		tableConfig.setTablePath(adaptation.getString(
				ExportFunctionalityExportPaths._Root_ExportTableConfig._TablePath));
		LOG.info("DataSetExportConfigFactory: table path = " + tableConfig.getTablePath());
		
		final String externalDataSpace = adaptation.getString(
				ExportFunctionalityExportPaths._Root_ExportTableConfig._ExternalDataSpace);
		if (externalDataSpace != null) {
			
			AdaptationHome externalDataSpaceRef = adaptation.getHome().getRepository()
					.lookupHome(HomeKey.forBranchName(externalDataSpace));
			if (externalDataSpaceRef == null) {
				
				// set the snapshot in case external data space is not found.
				externalDataSpaceRef = adaptation.getHome().getRepository().lookupHome(HomeKey.forVersionName(externalDataSpace));
				LOG.info("found external data space reference for Version " + externalDataSpace);
				if(null == externalDataSpaceRef){
					throw OperationException.createError("Could not find external data space " + externalDataSpace);
				}
			}
			LOG.info("found external data space reference for Branch " + externalDataSpace);
			final String externalDataSet = adaptation.getString(
					ExportFunctionalityExportPaths._Root_ExportTableConfig._ExternalDataSet);
			if (externalDataSet == null) {
				throw OperationException.createError(
						"External data set must be specified if external data space is specified.");
			}
			final Adaptation externalDataSetRef = externalDataSpaceRef.findAdaptationOrNull(
					AdaptationName.forName(externalDataSet));
			if (externalDataSetRef == null) {
				throw OperationException.createError("Could not find external data set " + externalDataSet
						+ " in data space " + externalDataSpace + ".");
			}
			tableConfig.setExternalDataSet(externalDataSetRef);
		}
		
		tableConfig.setFileName(adaptation.getString(
				ExportFunctionalityExportPaths._Root_ExportTableConfig._FileName));
		LOG.info("DataSetExportConfigFactory: filename = " + tableConfig.getFileName());
		tableConfig.setFileEncoding(adaptation.getString(
				ExportFunctionalityExportPaths._Root_ExportTableConfig._FileEncoding));
		final String adaptationColumnHeader = adaptation.getString(
				ExportFunctionalityExportPaths._Root_ExportTableConfig._ColumnHeader);
		final ExportFunctionalityTableConfig.ColumnHeaderType columnHeader = convertToColumnHeader(adaptationColumnHeader);
		tableConfig.setColumnHeader(columnHeader);
		tableConfig.setSeparator(adaptation.getString(
				ExportFunctionalityExportPaths._Root_ExportTableConfig._Separator).charAt(0));
		
		@SuppressWarnings("unchecked")
		final List<String> adaptationIncludeFieldPaths = adaptation.getList(
				ExportFunctionalityExportPaths._Root_ExportTableConfig._IncludeFieldPath);
		final ArrayList<Path> includeFieldPaths = new ArrayList<Path>();
		for (String adaptationIncludeFieldPath: adaptationIncludeFieldPaths) {
			includeFieldPaths.add(Path.parse(adaptationIncludeFieldPath));
		}
		tableConfig.setIncludeFieldPaths(includeFieldPaths);
		
		@SuppressWarnings("unchecked")
		final List<String> adaptationExcludeFieldPaths = adaptation.getList(
				ExportFunctionalityExportPaths._Root_ExportTableConfig._ExcludeFieldPath);
		final ArrayList<Path> excludeFieldPaths = new ArrayList<Path>();
		for (String adaptationExcludeFieldPath: adaptationExcludeFieldPaths) {
			excludeFieldPaths.add(Path.parse(adaptationExcludeFieldPath));
		}
		tableConfig.setExcludeFieldPaths(excludeFieldPaths);
		
		final String filterClassName = adaptation.getString(
				ExportFunctionalityExportPaths._Root_ExportTableConfig._Filter_FilterClass);
		if (filterClassName != null && ! "".equals(filterClassName)) {
			@SuppressWarnings("unchecked")
			final List<FilterClassParameter> filterParams = adaptation.getList(
					ExportFunctionalityExportPaths._Root_ExportTableConfig._Filter_FilterClassParameter);
			final AdaptationFilter filter = createFilter(filterClassName, filterParams);
			tableConfig.setFilter(filter);
		}
		
		switch (type) {
		case BASIC:
			tableConfig.setBasicOptions(createBasicOptions(adaptation));
			break;
			
		case FORMATTED:
			tableConfig.setFormattedOptions(
					createFormattedOptions(adaptation, exportConfigDataSet));
			break;
			
		case MULTI_TABLE_JOIN:
			// Multi-table also utilizes the formatted options
			tableConfig.setFormattedOptions(
					createFormattedOptions(adaptation, exportConfigDataSet));
			tableConfig.setMultiTableJoinOptions(
					createMultiTableJoinOptions(adaptation, exportConfigDataSet));
			break;
		}
		
		return tableConfig;
	}
	
	private static Adaptation loadExportConfigAdaptation(final Adaptation dataSet, final String pk) {
		LOG.info("DataSetExportConfigFactory: loadExportConfigAdaptation");
		final AdaptationTable exportConfigTable = dataSet.getTable(
				ExportFunctionalityExportPaths._Root_ExportConfig.getPathInSchema());
		final Adaptation adaptation = exportConfigTable.lookupAdaptationByPrimaryKey(
				PrimaryKey.parseString(pk));
		return adaptation;
	}
	
	private static Set<Adaptation> loadExportTableConfigAdaptations(final Adaptation dataSet, final List<String> pks) {
		LOG.info("DataSetExportConfigFactory: loadExportTableConfigAdaptations");
		final AdaptationTable exportTableConfigTable = dataSet.getTable(
				ExportFunctionalityExportPaths._Root_ExportTableConfig.getPathInSchema());
		final HashSet<Adaptation> adaptations = new HashSet<Adaptation>();
		for (String pk: pks) {
			final Adaptation adaptation = exportTableConfigTable.lookupAdaptationByPrimaryKey(
					PrimaryKey.parseString(pk));
			adaptations.add(adaptation);
		}
		
		return adaptations;
	}
	
	private static ExportType convertToType(final String adaptationType) {
		if (TYPE_BASIC.equals(adaptationType)) {
			return ExportFunctionalityTableConfig.ExportType.BASIC;
		}
		if (TYPE_FORMATTED.equals(adaptationType)) {
			return ExportFunctionalityTableConfig.ExportType.FORMATTED;
		}
		if (TYPE_MULTI_TABLE_JOIN.equals(adaptationType)) {
			return ExportFunctionalityTableConfig.ExportType.MULTI_TABLE_JOIN;
		}
		throw new IllegalArgumentException(adaptationType + " isn't a valid type");
	}
	
	private static ExportFunctionalityTableConfig.ColumnHeaderType convertToColumnHeader(final String adaptationColumnHeader) {
		if (COLUMN_HEADER_NONE.equals(adaptationColumnHeader)) {
			return ExportFunctionalityTableConfig.ColumnHeaderType.NONE;
		}
		if (COLUMN_HEADER_LABEL.equals(adaptationColumnHeader)) {
			return ExportFunctionalityTableConfig.ColumnHeaderType.LABEL;
		}
		if (COLUMN_HEADER_XPATH.equals(adaptationColumnHeader)) {
			return ExportFunctionalityTableConfig.ColumnHeaderType.XPATH;
		}
		throw new IllegalArgumentException(adaptationColumnHeader + " isn't a valid column header");
	}
	
	private static AdaptationFilter createFilter(final String filterClassName, final List<FilterClassParameter> filterParams) {
		//LOG.info("DataSetExportConfigFactory: createFilter");
		// This creates an AdaptationFilter class based on the specified class name and parameters
		try {
			@SuppressWarnings("rawtypes")
			final Class filterClass = Class.forName(filterClassName);
			final AdaptationFilter filter = (AdaptationFilter) filterClass.newInstance();
			
			final HashMap<String,String> paramMap = new HashMap<String,String>();
			if (filterParams != null) {
				// Load the parameters into a map with their specified values
				for (FilterClassParameter filterParam: filterParams) {
					final String value = filterParam.getValue();
					if (value != null && ! "".equals(value)) {
						paramMap.put(filterParam.getName(), value);
					}
				}
			}
			
			if (! paramMap.isEmpty()) {
				final BeanInfo beanInfo = Introspector.getBeanInfo(filterClass);
				final PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
				// Call all the setters on the filter with the specified values
				for (PropertyDescriptor propDesc: propertyDescriptors) {
					final String propDescName = propDesc.getName();
					if (paramMap.containsKey(propDescName)) {
						final String value = paramMap.get(propDescName);
						final Method setterMethod = propDesc.getWriteMethod();
						try {
							setterMethod.invoke(filter, new Object[] { value });
						} catch (final InvocationTargetException ex) {
							LOG.error("Error invoking setter for filter class parameter " + propDescName + ".", ex);
						}
					}
				}
			}
			return filter;
			
		} catch (final ClassNotFoundException ex) {
			LOG.error("Filter class " + filterClassName + " not found.", ex);
		} catch (final IllegalAccessException ex) {
			LOG.error("Constructor for " + filterClassName + "is not accessible.", ex);
		} catch (final InstantiationException ex) {
			LOG.error("Error instantiating filter class " + filterClassName + ".", ex);
		} catch (final IntrospectionException ex) {
			LOG.error("Error instrospecting " + filterClassName + " bean.", ex);
		}
		return null;
	}
	
	private static BasicExportFunctionalityOptions createBasicOptions(final Adaptation adaptation) {
		LOG.info("DataSetExportConfigFactory: createBasicOptions");
		final BasicExportFunctionalityOptions basicOptions = new BasicExportFunctionalityOptions();
		basicOptions.setUserFriendlyMode(adaptation.get_boolean(
				ExportFunctionalityExportPaths._Root_ExportTableConfig._BasicOptions_UserFriendlyMode));
		basicOptions.setIncludeTechnicalData(adaptation.get_boolean(
				ExportFunctionalityExportPaths._Root_ExportTableConfig._BasicOptions_IncludeTechnicalData));
		basicOptions.setIncludeComputedValues(adaptation.get_boolean(
				ExportFunctionalityExportPaths._Root_ExportTableConfig._BasicOptions_IncludeComputedValues));
		return basicOptions;
	}
	
	private static FormattedDataSetExportOptions createFormattedOptions(
			final Adaptation adaptation, final Adaptation exportConfigDataSet) {
		// LOG.info("DataSetExportConfigFactory: createFormattedOptions");
		final FormattedDataSetExportOptions formattedOptions = new FormattedDataSetExportOptions();
		final AdaptationTable patternTable = exportConfigDataSet.getTable(
				ExportFunctionalityExportPaths._Root_FormattedExportPattern.getPathInSchema());
		@SuppressWarnings("unchecked")
		final List<String> fks = adaptation.getList(
				ExportFunctionalityExportPaths._Root_ExportTableConfig._FormattedOptions_Pattern);
		final HashMap<FormattedDataSetExportOptions.PatternType, String> patterns =
				new HashMap<FormattedDataSetExportOptions.PatternType, String>();
		for (String fk: fks) {
			// Get the pattern from the record linked to by the foreign key
			final Adaptation patternAdaptation = patternTable.lookupAdaptationByPrimaryKey(
					PrimaryKey.parseString(fk));
			final String adaptationPatternType = patternAdaptation.getString(
					ExportFunctionalityExportPaths._Root_FormattedExportPattern._Type);
			final FormattedDataSetExportOptions.PatternType patternType =
					convertToPatternType(adaptationPatternType);
			final String pattern = patternAdaptation.getString(
					ExportFunctionalityExportPaths._Root_FormattedExportPattern._Pattern);
			patterns.put(patternType, pattern);
		}
		formattedOptions.setPatterns(patterns);
		
		return formattedOptions;
	}
	
	private static MultiTableJoinExportFunctionalityOptions createMultiTableJoinOptions(
			final Adaptation adaptation, final Adaptation exportConfigDataSet)
			throws OperationException {
		LOG.info("DataSetExportConfigFactory: createMultiTableJoinOptions");
		final MultiTableJoinExportFunctionalityOptions multiTableJoinOptions = new MultiTableJoinExportFunctionalityOptions();
		@SuppressWarnings("unchecked")
		final List<ForeignKeyFieldConfig> fkFieldConfigs = adaptation.getList(
				ExportFunctionalityExportPaths._Root_ExportTableConfig._MultiTableJoinOptions_FkFieldConfig);
		final AdaptationTable tableConfigTable = adaptation.getContainerTable();
		
		final ArrayList<ForeignKeyTableConfigRelationship> linkedTableConfigs
				= new ArrayList<ForeignKeyTableConfigRelationship>();
		// Look up the table config records linked to by the foreign key values
		// and create those table configs
		for (ForeignKeyFieldConfig fkFieldConfig: fkFieldConfigs) {
			final String fkKey = fkFieldConfig.getFkFieldPath();
			final String fkValue = fkFieldConfig.getExportTableConfig();
			final Adaptation linkedAdaptation = tableConfigTable.lookupAdaptationByPrimaryKey(
					PrimaryKey.parseString(fkValue));
			final ExportFunctionalityTableConfig linkedTableConfig =
					createExportTableConfigFromAdaptation(linkedAdaptation, exportConfigDataSet);
			linkedTableConfigs.add(new ForeignKeyTableConfigRelationship(fkKey, linkedTableConfig));
		}
		multiTableJoinOptions.setLinkedTableConfigs(linkedTableConfigs);
		return multiTableJoinOptions;
	}
	
	private static FormattedDataSetExportOptions.PatternType convertToPatternType(
			final String adaptationPatternType) {
		if (PATTERN_TYPE_BOOLEAN.equals(adaptationPatternType)) {
			return FormattedDataSetExportOptions.PatternType.BOOLEAN;
		}
		if (PATTERN_TYPE_DATE.equals(adaptationPatternType)) {
			return FormattedDataSetExportOptions.PatternType.DATE;
		}
		if (PATTERN_TYPE_DATE_TIME.equals(adaptationPatternType)) {
			return FormattedDataSetExportOptions.PatternType.DATE_TIME;
		}
		if (PATTERN_TYPE_DECIMAL.equals(adaptationPatternType)) {
			return FormattedDataSetExportOptions.PatternType.DECIMAL;
		}
		if (PATTERN_TYPE_INTEGER.equals(adaptationPatternType)) {
			return FormattedDataSetExportOptions.PatternType.INTEGER;
		}
		if (PATTERN_TYPE_TIME.equals(adaptationPatternType)) {
			return FormattedDataSetExportOptions.PatternType.TIME;
		}
		throw new IllegalArgumentException(adaptationPatternType + " isn't a valid pattern type");
	}
}
