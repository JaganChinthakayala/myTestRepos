package com.hertz.mdm.exporter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.hertz.mdm.exporter.ExportFunctionalityBean;
import com.onwbp.adaptation.AdaptationFilter;
import com.onwbp.adaptation.AdaptationHome;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.Request;
import com.hertz.mdm.exporter.ExportFunctionalityTableConfig.ColumnHeaderType;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.service.ExportImportCSVSpec;
import com.orchestranetworks.service.ExportImportCSVSpec.Header;
import com.orchestranetworks.service.ExportSpec;
import com.orchestranetworks.service.LoggingCategory;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.ProcedureContext;
import com.orchestranetworks.service.ProcedureResult;
import com.orchestranetworks.service.ProgrammaticService;
import com.orchestranetworks.service.ReadOnlyProcedure;
import com.orchestranetworks.service.Session;

/**
 * Uses the built-in EBX CSV export capabilities to export a table
 */
public class BasicTableExporter implements TableExporter {
	protected static final LoggingCategory LOG = LoggingCategory.getKernel();

	@Override
	public void exportTable(Session session, AdaptationTable table,ExportFunctionalityBean exportBean,
			OutputStream out, ExportFunctionalityTableConfig tableConfig,
			TableExporterFactory tableExporterFactory)
			throws IOException, OperationException {
		LOG.debug("BasicTableExporter: exportTable");
		LOG.info("BasicTableExporter: table = " + table.getTablePath().format());
		final ExportSpec exportSpec = new ExportSpec();
		final ExportImportCSVSpec csvSpec = new ExportImportCSVSpec();
		csvSpec.setFieldSeparator(tableConfig.getSeparator());
		final Header csvHeader = convertToCSVHeader(tableConfig.getColumnHeader());
		csvSpec.setHeader(csvHeader);
		csvSpec.setEncoding(tableConfig.getFileEncoding());
		exportSpec.setCSVSpec(csvSpec);
		
		// If you specify no predicate, it will request every record in the table
		// (that meets any filter you set)
		final Request request = table.createRequest();
		final AdaptationFilter filter = tableConfig.getFilter();
		if (filter != null) {
			request.setSpecificFilter(filter);
		}
		exportSpec.setRequest(request);
		exportSpec.setDestinationStream(out);
		final BasicExportFunctionalityOptions basicOptions = tableConfig.getBasicOptions();
		exportSpec.setIncludesComputedValues(basicOptions.isIncludeComputedValues());
		exportSpec.setIncludesTechnicalData(basicOptions.isIncludeTechnicalData());
		exportSpec.setSessionDependingDisplay(basicOptions.isUserFriendlyMode());
		
		final List<Path> includeFieldPaths = tableConfig.getIncludeFieldPaths();
		final List<Path> excludeFieldPaths = tableConfig.getExcludeFieldPaths();
		if ((includeFieldPaths != null && ! includeFieldPaths.isEmpty()) ||
				(excludeFieldPaths != null && ! excludeFieldPaths.isEmpty())) {
			// If one of these are set, then it means we are specifying specific fields.
			// Since you could be specifying a field that's computed, we need to turn this setting
			// to true, otherwise it would be contradictory.
			exportSpec.setIncludesComputedValues(true);
			// Figure out which fields should be shown based on the config
			final List<SchemaNode> fieldNodes = ExporterUtils.getFieldNodes(table.getTableNode(),
					includeFieldPaths, excludeFieldPaths);
			exportSpec.setRequestSchemaNodes(fieldNodes);
		}
		
		doExportProcedure(session, table.getContainerAdaptation().getHome(), exportSpec);
	}
	
	private static void doExportProcedure(final Session session, final AdaptationHome dataSpace, final ExportSpec exportSpec)
			throws OperationException {
		LOG.debug("BasicTableExporter: doExportProcedure");
		// Create a procedure to execute it. (It must be executed by a procedure.)
		final ReadOnlyProcedure exportProc = new ReadOnlyProcedure() {
			
			public void execute(ProcedureContext pContext) throws Exception {
				pContext.doExport(exportSpec);
			}
		};
	
		// Create a service and execute the procedure we created above.
		final ProgrammaticService exportSvc = ProgrammaticService.createForSession(session, dataSpace);
		final ProcedureResult procRes = exportSvc.execute(exportProc);
		final OperationException exception = procRes.getException();
		if (exception != null) {
			throw exception;
		}
	}
	
	private static Header convertToCSVHeader(ColumnHeaderType columnHeader) {
		Header csvHeader = null;
		switch (columnHeader) {
		case NONE:
			csvHeader = Header.NONE;
			break;
		case LABEL:
			csvHeader = Header.LABEL;
			break;
		case XPATH:
			csvHeader = Header.PATH_IN_TABLE;
			break;
		}
		return csvHeader;
	}
}
