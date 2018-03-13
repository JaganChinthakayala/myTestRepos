package com.hertz.mdm.exporter.RDM;

import java.io.IOException;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationHome;
import com.onwbp.adaptation.AdaptationName;
import com.onwbp.adaptation.AdaptationTable;
import com.orchestranetworks.instance.HomeKey;
import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.instance.ValueContextForValidation;
import com.hertz.mdm.exporter.ExportFunctionalityConfig;
import com.hertz.mdm.exporter.ExportFunctionalityConfigFactory;
import com.hertz.mdm.exporter.ExportFunctionalityExporter;
import com.hertz.mdm.exporter.DefaultTableExporterFactory;
import com.hertz.mdm.exporter.TableExporterFactory;
import com.orchestranetworks.scheduler.ScheduledExecutionContext;
import com.orchestranetworks.scheduler.ScheduledTask;
import com.orchestranetworks.scheduler.ScheduledTaskInterruption;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.service.LoggingCategory;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.Session;

/**
 * A scheduled task that invokes the data set exporter. It is not required for executing
 * the exporter, but it is required if you wish to do it as a scheduled task within EBX.
 * 
 * EBX will introspect the class for its bean properties in order to allow the parameters
 * to be configured within EBX.
 */
public class DataSetExporterScheduledTask extends ScheduledTask {
	
	protected static final LoggingCategory LOG = LoggingCategory.getKernel();
	
	protected String dataSpace;
	protected String dataSet;
	
	private static final String DEFAULT_EXPORT_CONFIG_DATA_SPACE = "DataSetExport";
	private static final String DEFAULT_EXPORT_CONFIG_DATA_SET = "DataSetExport";	
	
	protected String exportConfigDataSpace = DEFAULT_EXPORT_CONFIG_DATA_SPACE;
	protected String exportConfigDataSet = DEFAULT_EXPORT_CONFIG_DATA_SET;
	protected String exportConfigID;	
	

	/**
	 * Get the ID of the data space to export
	 * 
	 * @return the ID of the data space
	 */
	public String getDataSpace() {
		return dataSpace;
	}

	
	/**
	 * Set the ID of the data space to export
	 * 
	 * @param dataSpace the ID of the data space
	 */
	public void setDataSpace(String dataSpace) {
		this.dataSpace = dataSpace;
	}

	
	/**
	 * Get the ID of the data set to export
	 * 
	 * @return the ID of the data set
	 */
	public String getDataSet() {
		return dataSet;
	}

	
	/**
	 * Set the ID of the data set to export
	 * 
	 * @param dataSet the ID of the data set
	 */
	public void setDataSet(String dataSet) {
		this.dataSet = dataSet;
	}

	
	/**
	 * Get the ID of the data space for the export config records
	 * 
	 * @return the ID of the data space
	 */
	public String getExportConfigDataSpace() {
		return exportConfigDataSpace;
	}

	
	/**
	 * Set the ID of the data space for the export config records
	 * 
	 * @param exportConfigDataSpace the ID of the data space
	 */
	public void setExportConfigDataSpace(String exportConfigDataSpace) {
		this.exportConfigDataSpace = exportConfigDataSpace;
	}

	
	/**
	 * Get the ID of the data set for the export config records
	 * 
	 * @return the ID of the data set
	 */
	public String getExportConfigDataSet() {
		return exportConfigDataSet;
	}

	
	/**
	 * Set the ID of the data set for the export config records
	 * 
	 * @param exportConfigDataSet the ID of the data set
	 */
	public void setExportConfigDataSet(String exportConfigDataSet) {
		this.exportConfigDataSet = exportConfigDataSet;
	}

	
	/**
	 * Get the ID of the export config
	 * 
	 * @return the ID
	 */
	public String getExportConfigID() {
		return exportConfigID;
	}

	
	/**
	 * Set the ID of the export config
	 * 
	 * @param exportConfigID the ID
	 */
	public void setExportConfigID(String exportConfigID) {
		this.exportConfigID = exportConfigID;
	}

	
	@Override
	public void execute( ScheduledExecutionContext context ) throws OperationException, ScheduledTaskInterruption {
	
		LOG.info("DataSetExporterScheduledTask: execute");
		
		final Repository repo = context.getRepository();
		final Session session = context.getSession();
		final Path translationTablePath = Path.parse( "/root/Cross_Reference_Data/RDM_ANCILLARY_TRANSLATION" );
		
		final AdaptationHome dataSpaceRef = repo.lookupHome( HomeKey.forBranchName( dataSpace ) );
		final Adaptation dataSetRef = dataSpaceRef.findAdaptationOrNull( AdaptationName.forName( dataSet ) );
		AdaptationTable table = dataSetRef.getTable( translationTablePath );
		RDMTranslationTableExport exporter = new RDMTranslationTableExport();
		
		try{

			exporter.exportTable( session, table );
			
		} catch( final IOException ex ){
			throw OperationException.createError(ex);
		}
	}
	
	
	@Override
	public void validate( ValueContextForValidation context ) {
	
		if (dataSpace == null) {
			context.addError("dataSpace parameter is required.");
		}
		if (dataSet == null) {
			context.addError("dataSet parameter is required.");
		}
		if (exportConfigDataSpace == null) {
			context.addError("exportConfigDataSpace parameter is required.");
		}
		if (exportConfigDataSet == null) {
			context.addError("exportConfigDataSet parameter is required.");
		}
		if (exportConfigID == null) {
			context.addError("exportConfigID parameter is required.");
		}
	}
	
	
	/**
	 * Create Exporter 
	 * 
	 * @param tableExporterFactory
	 * @param config
	 * @return
	 */
	protected ExportFunctionalityExporter createExporter( TableExporterFactory tableExporterFactory, ExportFunctionalityConfig config ) {
		LOG.info("DataSetExporterScheduledTask: createExporter");
		
		return new ExportFunctionalityExporter( tableExporterFactory, config );
	}
	
	
	/**
	 * Create Table Exporter Factory
	 * @return
	 */
	protected TableExporterFactory createTableExporterFactory() {
		
		LOG.info("DataSetExporterScheduledTask: createTableExporterFactory");
		
		return new DefaultTableExporterFactory();
	}

	
	/**
	 * Create Config
	 *  
	 * @param repo
	 * @return
	 * @throws OperationException
	 */
	protected ExportFunctionalityConfig createConfig( Repository repo ) throws OperationException {
		
		LOG.info("DataSetExporterScheduledTask: createConfig");
		
		// Look up the data space and data set for the export config records
		final AdaptationHome exportConfigDataSpaceRef = repo.lookupHome(
				HomeKey.forBranchName(exportConfigDataSpace));
		
		final Adaptation exportConfigDataSetRef = exportConfigDataSpaceRef.findAdaptationOrNull(
				AdaptationName.forName(exportConfigDataSet));
		
		final ExportFunctionalityConfigFactory configFactory = new ExportFunctionalityConfigFactory();
		
		return configFactory.createFromEBXRecord( exportConfigDataSetRef, exportConfigID );
	}
}
