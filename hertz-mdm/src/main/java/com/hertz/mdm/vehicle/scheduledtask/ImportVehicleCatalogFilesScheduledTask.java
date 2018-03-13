/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.vehicle.scheduledtask;

import java.io.File;

import com.hertz.mdm._hertz.constants.HtzConstants;
import com.hertz.mdm._hertz.util.HtzUtilities;
import com.hertz.mdm.common.path.CommonReferencePaths;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationHome;
import com.onwbp.adaptation.AdaptationTable;
import com.orchestranetworks.addon.adix.AdixFactory;
import com.orchestranetworks.addon.adix.AdixImport;
import com.orchestranetworks.addon.adix.AdixImportResult;
import com.orchestranetworks.addon.adix.AdixImportSpec;
import com.orchestranetworks.addon.adix.ImportDataAccessSpec;
import com.orchestranetworks.addon.adix.ImportType;
import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.scheduler.ScheduledExecutionContext;
import com.orchestranetworks.scheduler.ScheduledTask;
import com.orchestranetworks.scheduler.ScheduledTaskInterruption;
import com.orchestranetworks.service.OperationException;

/**
 * A scheduled task to perform import of files using Data Exchange
 */
public class ImportVehicleCatalogFilesScheduledTask extends ScheduledTask
{
	@Override
	public void execute(ScheduledExecutionContext context)
		throws OperationException, ScheduledTaskInterruption
	{
		Repository repository = context.getRepository();
		AdaptationHome dataSpace = AdaptationUtil.getDataSpaceOrThrowOperationException(
			repository,
			HtzConstants.COMMON_REFERENCE_DATA_SPACE);
		Adaptation dataSet = AdaptationUtil
			.getDataSetOrThrowOperationException(dataSpace, HtzConstants.COMMON_REFERENCE_DATA_SET);
		AdaptationTable table = dataSet.getTable(CommonReferencePaths._Root_City.getPathInSchema());

		final AdixImportSpec adixImportSpec = new AdixImportSpec();
		adixImportSpec.setImportType(ImportType.EXCEL_FOR_SINGLE_TABLE);
		adixImportSpec.setImportPreference("JeffTestDX");
		adixImportSpec.setImportedFile(new File("/Users/jeffsimon/downloads/city.csv"));
		ImportDataAccessSpec importDataAccessSpec = new ImportDataAccessSpec(
			dataSet,
			table,
			context.getSession().getLocale(),
			context.getSession());
		adixImportSpec.setDataAccessSpec(importDataAccessSpec);

		AdixImport adixImport = AdixFactory.getAdixImport();
		AdixImportResult adixImportResult = adixImport.execute(adixImportSpec);

		HtzUtilities.writeToFileInFileSystem(
			"\n\nAdixImportResult = " + adixImportResult.getImportResults().toString());

		// you can look into the methods of AdixImportResult if you want to explore the results of the Import process 

	}
}
