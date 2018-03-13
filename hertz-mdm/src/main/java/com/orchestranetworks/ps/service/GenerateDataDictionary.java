package com.orchestranetworks.ps.service;

import java.io.*;
import java.text.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.lang.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import com.onwbp.adaptation.*;
import com.onwbp.base.schema.definition.*;
import com.onwbp.base.schema.view.*;
import com.onwbp.base.text.*;
import com.onwbp.org.apache.commons.io.*;
import com.orchestranetworks.instance.*;
import com.orchestranetworks.ps.admin.devartifacts.*;
import com.orchestranetworks.ps.filetransfer.*;
import com.orchestranetworks.ps.util.*;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.schema.info.*;
import com.orchestranetworks.service.*;

/**
 * Export data set to an excel workbook.  Give details of tables and fields and recurse into reachable data sets.
 */
public class GenerateDataDictionary
{
	public static final String DEFAULT_EXPORT_DIR_NAME = "libraryExport";
	public static final String DEFAULT_FILE_DOWNLOADER_SERVLET = "/FileDownloader";

	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected String exportDirName;
	protected boolean appendTimestamp;
	protected String fileDownloaderServlet;
	private static final String[] ELEMENTS_HEADERS = new String[] { "Data Set", "Table", "Group",
			"Field Order", "Field", "Label", "Description", "PK", "Unique", "FK", "CA", "Type",
			"Max Length", "Min Occurs", "Max Occurs", "Enumeration", "Other Facets",
			"Default Value", "Auto-Increment", "Inherited", "Information", "History",
			"Replication", "Hidden" };
	private static final String[] TABLES_HEADERS = new String[] { "Data Set", "Table", "Label",
			"Primary Key", "Description", "Constraints", "Information" };
	private static final String[] COMPLEX_TYPES_HEADERS = new String[] { "Name", "Label", "Field",
			"Description", "Type", "Max Length", "Min Occurs", "Max Occurs", "Enumeration",
			"Other Facets", "Default Value", "Auto-Increment" };
	private static final String[] SIMPLE_TYPES_HEADERS = new String[] { "Name", "Label",
			"Description", "BaseType", "Max Length", "Min Occurs", "Max Occurs", "Enumeration",
			"Other Facets", "Default Value", "Auto-Increment" };

	public GenerateDataDictionary(
		final HttpServletRequest request,
		final HttpServletResponse response)
	{
		this(
			request,
			response,
			DEFAULT_EXPORT_DIR_NAME,
			false,
			false,
			DEFAULT_FILE_DOWNLOADER_SERVLET);
	}

	public GenerateDataDictionary(
		final HttpServletRequest request,
		final HttpServletResponse response,
		String exportDirName,
		boolean appendTimestamp,
		boolean includePK,
		String fileDownloaderServlet)
	{
		this.request = request;
		this.response = response;
		this.exportDirName = exportDirName;
		this.appendTimestamp = appendTimestamp;
		this.fileDownloaderServlet = fileDownloaderServlet;
	}

	public void execute() throws ServletException
	{
		final ServiceContext sContext = ServiceContext.getServiceContext(request);

		try
		{
			Adaptation dataSet = sContext.getCurrentAdaptation();
			XSSFWorkbook workbook = generateLibrary(dataSet);
			File outputFile = saveWorkbook(workbook, dataSet);
			generateUI(sContext, outputFile);
		}
		catch (OperationException e)
		{
			LoggingCategory.getKernel().error("Error occurred generating excel for library.", e);
			throw new ServletException(e);
		}
	}

	private static String createFileName(Adaptation dataSet)
	{
		String label = dataSet.getLabel(Locale.getDefault());
		label = StringUtils.substringBefore(label, " Data Set");
		return label + " Data Dictionary.xlsx";
	}

	private File saveWorkbook(XSSFWorkbook workbook, Adaptation dataSet)
	{
		OutputStream os = null;
		File file = null;
		try
		{
			File dir = new File(getWorkbookFolder(), exportDirName);
			dir.mkdirs();
			file = new File(dir, createFileName(dataSet));
			if (!file.exists())
				file.createNewFile();
			os = new FileOutputStream(file);
			workbook.write(os);
			os.flush();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			IOUtils.closeQuietly(os);
		}
		return file;
	}

	// TODO: Would like if this could use its own properties file to specify the location,
	// or else some system property, rather than rely on dev artifacts
	protected File getWorkbookFolder() throws IOException
	{
		// Get properties file from system property
		String propertiesFile = System.getProperty(DevArtifactsPropertyFileHelper.PROPERTIES_FILE_SYSTEM_PROPERTY);
		// If it wasn't specified as a system property, then use the configured value
		if (propertiesFile == null)
		{
			propertiesFile = DevArtifactsPropertyFileHelper.DEFAULT_PROPERTIES_FILE;
		}
		DevArtifactsPropertyFileHelper helper = new DevArtifactsPropertyFileHelper(propertiesFile);
		Properties props = helper.getProperties();
		String dataFoldername = props.getProperty(DevArtifactsPropertyConstants.PROPERTY_DATA_FOLDER);
		return new File(dataFoldername);
	}

	protected void generateUI(ServiceContext sContext, File outputFile)
	{
		if (outputFile != null)
		{
			String downloadURL = sContext.getURLForResource(fileDownloaderServlet) + "?"
				+ FileDownloader.FILE_PATH_PARAM_NAME + "=" + outputFile.getAbsolutePath();
			downloadURL = downloadURL.replaceAll("\\\\", "/");

			sContext.getUIComponentWriter().add_cr(
				"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href='" + downloadURL + "'>Download Report</a>");
		}
	}

	protected XSSFWorkbook generateLibrary(Adaptation dataSet) throws OperationException
	{
		XSSFWorkbook workbook = new XSSFWorkbook();
		Sheet tables = workbook.createSheet("Tables");
		writeHeaders(tables, TABLES_HEADERS);
		Sheet elements = workbook.createSheet("Elements");
		writeHeaders(elements, ELEMENTS_HEADERS);
		Sheet ctypes = workbook.createSheet("Complex Types");
		writeHeaders(ctypes, COMPLEX_TYPES_HEADERS);
		Sheet stypes = workbook.createSheet("Simple Types");
		writeHeaders(stypes, SIMPLE_TYPES_HEADERS);
		Set<Adaptation> dataSets = new LinkedHashSet<Adaptation>();
		dataSets.add(dataSet);
		Set<Adaptation> processedDataSets = new HashSet<Adaptation>();
		Map<SchemaTypeName, SchemaNode> sharedTypes = new LinkedHashMap<SchemaTypeName, SchemaNode>();
		while (hasDataSetToProcess(dataSets, processedDataSets))
		{
			List<Adaptation> temp = new ArrayList<Adaptation>(dataSets);
			dataSets.clear();
			for (Adaptation ds : temp)
			{
				processDataSet(ds, dataSets, processedDataSets, elements, tables, sharedTypes);
			}
		}
		writeTypes(sharedTypes, ctypes, stypes);
		return workbook;
	}

	private void writeHeaders(Sheet sheet, String[] headers)
	{
		//write the header row
		Row headerRow = sheet.createRow(0);
		Workbook wb = sheet.getWorkbook();
		Font font = wb.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		CellStyle style = wb.createCellStyle();
		style.setFont(font);
		for (int i = 0; i < headers.length; i++)
		{
			Cell cell = headerRow.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(headers[i]);
		}
	}

	private static void processDataSet(
		Adaptation dataSet,
		Set<Adaptation> dataSets,
		Set<Adaptation> processedDataSets,
		Sheet elements,
		Sheet tables,
		Map<SchemaTypeName, SchemaNode> sharedTypes)
	{
		if (!processedDataSets.add(dataSet))
			return;
		List<AdaptationTable> tableNodes = AdaptationUtil.getAllTables(dataSet);
		String dataSetName = dataSet.getLabel(Locale.getDefault());
		writeElements(elements, dataSetName, tableNodes, dataSets, sharedTypes);
		writeTables(tables, dataSetName, tableNodes);
	}

	private static boolean hasDataSetToProcess(
		Set<Adaptation> dataSets,
		Set<Adaptation> processedDataSets)
	{
		return !processedDataSets.containsAll(dataSets);
	}

	@SuppressWarnings("unchecked")
	private static void writeTypes(Map<SchemaTypeName, SchemaNode> types, Sheet ctypes, Sheet stypes)
	{
		int crow = 1;
		int srow = 1;
		for (Map.Entry<SchemaTypeName, SchemaNode> entry : types.entrySet())
		{
			SchemaNode node = entry.getValue();
			Locale locale = node.getSchemaDefaultLocale();
			if (node instanceof TNode)
			{
				Type type = ((TNode) node).getType();
				if (type.isComplexType())
				{
					//populate complex type sheet
					ComplexType ctype = (ComplexType) type;
					List<Path> fields = ctype.getSubTypePathList();
					for (Path path : fields)
					{
						Row row = ctypes.createRow(crow++);
						/*
							private static final String[] COMPLEX_TYPES_HEADERS = new String[] { "Name", "Label", "Field",  "Description",
									"Type", "Max Length", "Min Occurs", "Max Occurs",
									"Enumeration", "Default Value", "Auto-Increment" };
						 */
						int col = 0;
						addCell(row, col++, ctype.getTypeName().getName()); //typename
						addCell(row, col++, ctype.getLabel().formatMessage(locale)); //label
						addCell(row, col++, path.format()); //field
						SchemaNode fieldNode = node.getNode(Path.SELF.add(path));
						addCell(row, col++, fieldNode.getDescription(locale)); //description
						addCommonFieldColumns(
							row,
							col,
							getType(fieldNode, null),
							fieldNode,
							node.getTableNode());
					}
				}
				else
				{
					//populate simple type sheet
					Row row = stypes.createRow(srow++);
					/*
					private static final String[] SIMPLE_TYPES_HEADERS = new String[] { "Name", "Label",
					"Description", "BaseType", "Max Length", "Min Occurs", "Max Occurs",
					"Enumeration" };
					 */
					int col = 0;
					addCell(row, col++, type.getTypeName().getName()); //typename
					addCell(row, col++, type.getLabel().formatMessage(locale)); //label
					SchemaNode fieldNode = node;
					addCell(row, col++, fieldNode.getDescription(locale)); //description
					addCommonFieldColumns(
						row,
						col,
						type.getBaseTypeName().getName(),
						fieldNode,
						null);
				}
			}
		}
	}

	private static int addCommonFieldColumns(
		Row row,
		int col,
		String type,
		SchemaNode fieldNode,
		SchemaNode tableNode)
	{
		addCell(row, col++, type); //basetype
		SchemaFacetMaxLength maxLen = fieldNode.getFacetMaxLength();
		addCell(row, col++, maxLen != null ? maxLen.getValue() : ""); //maxLength
		addCell(row, col++, fieldNode.getMinOccurs()); //minOccurs
		int maxOccurs = fieldNode.getMaxOccurs();
		if (maxOccurs == Integer.MAX_VALUE || maxOccurs == 0)
			addCell(row, col++, "unbounded"); //maxOccurs
		else
			addCell(row, col++, maxOccurs); //maxOccurs
		addCell(row, col++, getEnumeration(fieldNode)); //enumeration
		addCell(row, col++, getOtherFacets(fieldNode)); //other facets
		addCell(row, col++, getDefaultValue(fieldNode)); //default value
		addCell(row, col++, getAutoIncrement(type, fieldNode)); //auto-increment?
		return col;
	}

	@SuppressWarnings("rawtypes")
	private static Object getDefaultValue(SchemaNode fieldNode)
	{
		Object defaultValue = fieldNode.getDefaultValue();
		if (defaultValue instanceof Collection && ((Collection) defaultValue).isEmpty())
			return null;
		return defaultValue;
	}

	private static Boolean getAutoIncrement(String type, SchemaNode fieldNode)
	{
		if ("Integer".equals(type))
			return ((TNode) fieldNode).isAutoIncrement();
		else
			return null;
	}

	private static void writeTables(
		Sheet tablesSheet,
		String dataSetName,
		List<AdaptationTable> tables)
	{
		int rowNum = tablesSheet.getLastRowNum();
		for (AdaptationTable table : tables)
		{
			Row row = tablesSheet.createRow(++rowNum);
			writeTable(row, dataSetName, table);
		}
	}

	private static void writeTable(Row row, String dataSetName, AdaptationTable table)
	{
		/*
		private static final String[] TABLES_HEADERS = new String[] { "Data Set", "Table", "Label",
			"Primary Key", "Description", "Constraints", "Information" };
		 */
		int col = 0;
		SchemaNode tableNode = table.getTableNode();
		Locale locale = tableNode.getSchemaDefaultLocale();
		addCell(row, col++, dataSetName); //data set
		addCell(row, col++, table.getTablePath().format()); //table
		addCell(row, col++, tableNode.getLabel(locale)); //label
		addCell(row, col++, getKeySpec(table)); //PK
		addCell(row, col++, tableNode.getDescription(locale)); //description
		addCell(row, col++, getOtherFacets(tableNode)); //constraints
		addCell(row, col++, getRules(tableNode)); //information
	}

	private static String getKeySpec(AdaptationTable table)
	{
		StringBuilder sb = new StringBuilder();
		for (Path path : table.getPrimaryKeySpec())
		{
			if (sb.length() > 0)
				sb.append(", ");
			sb.append(path.format());
		}
		return sb.toString();
	}

	private static void writeElements(
		Sheet elements,
		String dataSetName,
		List<AdaptationTable> tables,
		Set<Adaptation> dataSets,
		Map<SchemaTypeName, SchemaNode> sharedTypes)
	{
		for (AdaptationTable table : tables)
		{
			writeElementsForTable(elements, dataSetName, table, dataSets, sharedTypes);
		}
	}

	private static void writeElementsForTable(
		Sheet sheet,
		String dataSetName,
		AdaptationTable table,
		Set<Adaptation> dataSets,
		Map<SchemaTypeName, SchemaNode> sharedTypes)
	{
		List<SchemaNode> fields = new ArrayList<SchemaNode>();
		collectFields(
			fields,
			table.getTableNode().getTableOccurrenceRootNode().getNodeChildren(),
			sharedTypes);
		int rowNum = sheet.getLastRowNum();
		for (int i = 0; i < fields.size(); i++)
		{
			SchemaNode field = fields.get(i);
			Row row = sheet.createRow(++rowNum);
			writeElementsForField(
				row,
				dataSetName,
				table,
				field,
				i + 1,
				isPK(table, field),
				dataSets);
		}
	}

	private static boolean isPK(AdaptationTable table, SchemaNode field)
	{
		Path[] keyPaths = table.getPrimaryKeySpec();
		Path fieldPath = field.getPathInAdaptation();
		for (Path path : keyPaths)
		{
			if (fieldPath.equals(path))
				return true;
		}
		return false;
	}

	private static void collectFields(
		List<SchemaNode> fields,
		SchemaNode[] children,
		Map<SchemaTypeName, SchemaNode> sharedTypes)
	{
		for (SchemaNode child : children)
		{
			SchemaTypeName stn = child.getXsTypeName();
			if (stn != null && stn.getBuiltInTypeLabel() == null && !sharedTypes.containsKey(stn))
			{
				sharedTypes.put(stn, child);
			}
			if (child.isComplex())
			{
				SchemaNode[] subchildren = child.getNodeChildren();
				collectFields(fields, subchildren, sharedTypes);
			}
			else if (child.isTerminalValue() || child.isAssociationNode())
			{
				fields.add(child);
			}
		}
	}

	private static void writeElementsForField(
		Row row,
		String dataSetName,
		AdaptationTable table,
		SchemaNode fieldNode,
		int index,
		boolean isPK,
		Set<Adaptation> dataSets)
	{
		/*
		private static final String[] ELEMENTS_HEADERS = new String[] { "Data Set", "Table", "Group",
			"Field Order", "Field", "Label", "Description", "PK", "Unique", "FK", "CA", "Type",
			"Max Length", "Min Occurs", "Max Occurs", "Enumeration", "Other Facets",
			"Default Value", "Inherited", "Information", "History", "Replication", "Hidden" };
		 */
		int col = 0;
		Locale locale = fieldNode.getSchemaDefaultLocale();
		TNode tnode = fieldNode instanceof TNode ? (TNode) fieldNode : null;
		Path path = fieldNode.getPathInAdaptation();
		addCell(row, col++, dataSetName); //data set
		addCell(row, col++, table.getTableNode().getLabel(locale)); //table
		String group = null;
		if (path.getSize() > 1)
		{
			Path groupPath = path.getPathWithoutLastStep();
			TNode groupNode = tnode != null ? tnode.getParent() : null;
			group = getGroup(groupPath, groupNode, locale);
		}
		addCell(row, col++, group); //group
		addCell(row, col++, index); //field order
		addCell(row, col++, path.format()); //field path
		addCell(row, col++, fieldNode.getLabel(locale)); //label
		addCell(row, col++, fieldNode.getDescription(locale)); //description
		addCell(row, col++, isPK); //pk
		addCell(row, col++, tnode != null && tnode.isPartOfUniquenessConstraint(), false); //unique
		SchemaFacetTableRef ref = fieldNode.getFacetOnTableReference();
		if (ref != null)
		{
			AdaptationReference container = ref != null ? ref.getContainerReference() : null;
			if (container != null)
			{
				HomeKey branch = ref.getContainerHome();
				AdaptationHome dataSpace = table.getContainerAdaptation().getHome();
				if (branch != null)
				{
					dataSpace = dataSpace.getRepository().lookupHome(branch);
				}
				Adaptation dataSet = dataSpace.findAdaptationOrNull(container);
				if (dataSet != null)
					dataSets.add(dataSet);
			}
		}
		addCell(row, col++, ref != null, false); //fk
		addCell(row, col++, (fieldNode.isValueFunction() && !fieldNode.isAssociationNode()), false); //ca
		col = addCommonFieldColumns(row, col, getType(fieldNode, table.getContainerAdaptation()), fieldNode, null);
		SchemaInheritanceProperties ip = fieldNode.getInheritanceProperties();
		String inode = null;
		if (ip != null)
		{
			SchemaNode in = ip.getInheritedNode();
			SchemaNode itn = in.getTableNode();
			inode = itn.getLabel(locale) + "/" + in.getLabel(locale);
		}
		addCell(row, col++, inode); //inherited
		addCell(row, col++, getRules(fieldNode)); //information
		addCell(row, col++, !fieldNode.isHistoryDisabled(), false); //history
		addCell(row, col++, tnode == null || !tnode.isReplicationDisabled(), false); //replication
		DefaultViewProperties dvp = tnode != null ? tnode.getDefaultViewProperties() : null;
		boolean hidden = false;
		if (dvp != null)
			hidden = dvp.isHidden();
		addCell(row, col++, hidden, false); //hidden
	}

	@SuppressWarnings("unchecked")
	private static Object getOtherFacets(final SchemaNode fieldNode)
	{
		StringBuilder sb = new StringBuilder();
		for (Iterator<SchemaFacet> iterator = fieldNode.getFacets(); iterator.hasNext();)
		{
			SchemaFacet facet = iterator.next();
			if (facet.isFacetMaxLength() || facet.isEnumerationList())
				continue; // covered as a primary column
			if (facet.isFacetMinLength())
				sb.append("Length >= ")
					.append(((SchemaFacetMinLength) facet).getValue())
					.append("; ");
			else if (facet.isFacetLength())
				sb.append("Length = ").append(((SchemaFacetLength) facet).getValue()).append("; ");
			else if (facet.isFacetTotalDigits())
				sb.append("TotalDigits <= ")
					.append(((SchemaFacetTotalDigits) facet).getTotalDigits())
					.append("; ");
			else if (facet.isFacetFractionDigits())
				sb.append("FractionDigits <= ")
					.append(((SchemaFacetFractionDigits) facet).getFractionDigits())
					.append("; ");
			else if (facet.isFacetPattern())
				sb.append("Pattern = ")
					.append(((SchemaFacetPattern) facet).getPatternString())
					.append("; ");
			else if (facet.isFacetBoundaryMinInclusive())
				sb.append("Value >= ").append(getBound((SchemaFacetBoundary) facet)).append("; ");
			else if (facet.isFacetBoundaryMinExclusive())
				sb.append("Value > ").append(getBound((SchemaFacetBoundary) facet)).append("; ");
			else if (facet.isFacetBoundaryMaxInclusive())
				sb.append("Value <= ").append(getBound((SchemaFacetBoundary) facet)).append("; ");
			else if (facet.isFacetBoundaryMaxExclusive())
				sb.append("Value < ").append(getBound((SchemaFacetBoundary) facet)).append("; ");
			else
			{
				String facetString = facet.toString();
				if (!"FacetOnMandatoryField[]".equals(facetString)
					&& !"FacetGeneric[com.orchestranetworks.cce.validation.constraint.j]".equals(facetString)
					&& !StringUtils.startsWith(facetString, "FacetGeneric[com.onwbp."))
				{
					facetString = StringUtils.substringBefore(
						StringUtils.substringAfter(facetString, "FacetGeneric["),
						"]");
					if (!StringUtils.isEmpty(facetString))
						sb.append(facetString).append("; ");
				}
			}
		}
		return sb.toString();
	}
	private static String getBound(SchemaFacetBoundary boundary)
	{
		Object bound = boundary.getBound();
		if (bound != null)
		{
			if (bound instanceof Date)
				return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(
					(Date) bound);
			return bound.toString();
		}
		Path path = boundary.getPath();
		return path != null ? path.format() : "?";
	}

	private static String getGroup(Path groupPath, TNode groupNode, Locale locale)
	{
		if (groupNode != null)
		{
			TNode[] path = new TNode[groupPath.getSize()];
			for (int i = path.length; --i >= 0;)
			{
				path[i] = groupNode;
				groupNode = groupNode.getParent();
			}
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < path.length; i++)
			{
				if (i > 0)
					sb.append("/");
				sb.append(path[i].getLabel(locale));
			}
			return sb.toString();
		}
		else
			return groupPath.format();
	}

	private static Object getRules(SchemaNode node)
	{
		SchemaNodeInformation info = node.getInformation();
		if (info != null)
			return info.getInformation();
		return null;
	}

	@SuppressWarnings("rawtypes")
	private static String getEnumeration(SchemaNode fieldNode)
	{
		SchemaFacetEnumeration enumeration = fieldNode.getFacetEnumeration();
		if (enumeration != null)
		{
			List values = enumeration.getValues();
			if (values != null) //static enumeration
			{
				return values.toString();
			}
		}
		return "";
	}

	private static String getType(SchemaNode fieldNode, Adaptation dataSet)
	{
		String baseName;
		Locale locale = fieldNode.getSchemaDefaultLocale();
			SchemaNode tableNode = AdaptationUtil.getTableNodeForRelated(fieldNode, dataSet);
			if (tableNode != null)
			{
				baseName = tableNode.getLabel(locale);
			}
			else
			{
				SchemaTypeName typeName = fieldNode.getXsTypeName();
				UserMessage builtIn = typeName != null ? typeName.getBuiltInTypeLabel() : null;
				if (builtIn != null)
				{
					baseName = builtIn.formatMessage(locale);
				}
				else
				{
					baseName = typeName != null ? typeName.toString() : "String";
				}
			}
			if (fieldNode.getMaxOccurs() != 1)
			{
				return "List<" + baseName + ">";
			}
			else
				return baseName;
		}

	private static void addCell(Row row, int index, Boolean value, boolean showFalse)
	{
		String svalue = null;
		if (value.booleanValue())
			svalue = "Yes";
		else if (showFalse)
			svalue = "No";
		addCell(row, index, svalue);
	}

	private static void addCell(Row row, int index, Object value)
	{
		Cell cell = row.createCell(index);
		if (value instanceof Number)
			cell.setCellValue(((Number) value).doubleValue());
		else if (value instanceof Boolean)
			cell.setCellValue(((Boolean) value).booleanValue() ? "Yes" : "No");
		else
			cell.setCellValue(value == null ? "" : value.toString());
	}

	public String getExportDirName()
	{
		return this.exportDirName;
	}

	public void setExportDirName(String exportDirName)
	{
		this.exportDirName = exportDirName;
	}

	public boolean isAppendTimestamp()
	{
		return this.appendTimestamp;
	}

	public void setAppendTimestamp(boolean appendTimestamp)
	{
		this.appendTimestamp = appendTimestamp;
	}

	public String getFileDownloaderServlet()
	{
		return this.fileDownloaderServlet;
	}

	public void setFileDownloaderServlet(String fileDownloaderServlet)
	{
		this.fileDownloaderServlet = fileDownloaderServlet;
	}

}
