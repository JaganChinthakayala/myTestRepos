package com.hertz.mdm.exporter;

import com.orchestranetworks.schema.Path;
/**
 * Generated by EBX5 5.4.1 [0919], at  2014/01/22 19:36:57 [EST].
 * WARNING: Any manual changes to this class may be overwritten by generation process.
 * DO NOT MODIFY THIS CLASS.
 * 
 * This interface defines constants related to schema [Publication: DataSetExport].
 * 
 * Root paths in this interface: 
 * 	'/'   relativeToRoot: false
 * 
 */
public interface ExportFunctionalityExportPaths
{
	// ===============================================
	// Constants for nodes under '/'.
	// Prefix:  ''.
	// Statistics:
	//		59 path constants.
	//		25 leaf nodes.
	public static final Path _Root = Path.parse("/root");

	// Table type path
	public final class _Root_ExportConfig {
		private static final Path _Root_ExportConfig = _Root.add("ExportConfig");
		public static Path getPathInSchema()
		{
			return _Root_ExportConfig;
		}
	public static final Path _Id = Path.parse("./id");
	public static final Path _FolderName = Path.parse("./folderName");
	public static final Path _ExportTableConfig = Path.parse("./exportTableConfig");
	} 

	// Table type path
	public final class _Root_ExportTableConfig {
		private static final Path _Root_ExportTableConfig = _Root.add("ExportTableConfig");
		public static Path getPathInSchema()
		{
			return _Root_ExportTableConfig;
		}
	public static final Path _ExportName = Path.parse("./exportName");
	public static final Path _TablePath = Path.parse("./tablePath");
	public static final Path _Type = Path.parse("./type");
	public static final Path _FileName = Path.parse("./fileName");
	public static final Path _FileEncoding = Path.parse("./fileEncoding");
	public static final Path _ColumnHeader = Path.parse("./columnHeader");
	public static final Path _Separator = Path.parse("./separator");
	public static final Path _IncludeFieldPath = Path.parse("./includeFieldPath");
	public static final Path _ExcludeFieldPath = Path.parse("./excludeFieldPath");
	public static final Path _ExternalDataSpace = Path.parse("./externalDataSpace");
	public static final Path _ExternalDataSet = Path.parse("./externalDataSet");
	public static final Path _Filter = Path.parse("./filter");
	public static final Path _Filter_FilterClass = Path.parse("./filter/filterClass");
	public static final Path _Filter_FilterClassParameter = Path.parse("./filter/filterClassParameter");
	public static final Path _Filter_FilterClassParameter_Name = Path.parse("./filter/filterClassParameter/name");
	public static final Path _Filter_FilterClassParameter_Value = Path.parse("./filter/filterClassParameter/value");
	public static final Path _BasicOptions = Path.parse("./basicOptions");
	public static final Path _BasicOptions_UserFriendlyMode = Path.parse("./basicOptions/userFriendlyMode");
	public static final Path _BasicOptions_IncludeTechnicalData = Path.parse("./basicOptions/includeTechnicalData");
	public static final Path _BasicOptions_IncludeComputedValues = Path.parse("./basicOptions/includeComputedValues");
	public static final Path _FormattedOptions = Path.parse("./formattedOptions");
	public static final Path _FormattedOptions_Pattern = Path.parse("./formattedOptions/pattern");
	public static final Path _MultiTableJoinOptions = Path.parse("./multiTableJoinOptions");
	public static final Path _MultiTableJoinOptions_FkFieldConfig = Path.parse("./multiTableJoinOptions/fkFieldConfig");
	public static final Path _MultiTableJoinOptions_FkFieldConfig_FkFieldPath = Path.parse("./multiTableJoinOptions/fkFieldConfig/fkFieldPath");
	public static final Path _MultiTableJoinOptions_FkFieldConfig_ExportTableConfig = Path.parse("./multiTableJoinOptions/fkFieldConfig/exportTableConfig");
	} 

	// Table type path
	public final class _Root_FormattedExportPattern {
		private static final Path _Root_FormattedExportPattern = _Root.add("FormattedExportPattern");
		public static Path getPathInSchema()
		{
			return _Root_FormattedExportPattern;
		}
	public static final Path _Type = Path.parse("./type");
	public static final Path _Pattern = Path.parse("./pattern");
	} 
	// ===============================================

}
