/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.admin.devartifacts;

import java.util.*;

import com.orchestranetworks.service.*;

/**
* A config for importing dev artifacts.
*/
public class ImportDevArtifactsConfig extends DevArtifactsConfig
{
	private ImportSpecMode importMode;
	private boolean skipNonExistingFiles;
	private List<DataSetCreationInfo> dataSetsToCreate;
	private Map<DataSetCreationKey, List<String>> createdDataSetTableSpecs;

	public ImportSpecMode getImportMode()
	{
		return this.importMode;
	}

	public void setImportMode(ImportSpecMode importMode)
	{
		this.importMode = importMode;
	}

	public boolean isSkipNonExistingFiles()
	{
		return this.skipNonExistingFiles;
	}

	public void setSkipNonExistingFiles(boolean skipNonExistingFiles)
	{
		this.skipNonExistingFiles = skipNonExistingFiles;
	}

	public List<DataSetCreationInfo> getDataSetsToCreate()
	{
		return this.dataSetsToCreate;
	}

	public void setDataSetsToCreate(List<DataSetCreationInfo> dataSetsToCreate)
	{
		this.dataSetsToCreate = dataSetsToCreate;
	}

	public Map<DataSetCreationKey, List<String>> getCreatedDataSetTableSpecs()
	{
		return this.createdDataSetTableSpecs;
	}

	public void setCreatedDataSetTableSpecs(
		Map<DataSetCreationKey, List<String>> createdDataSetTableSpecs)
	{
		this.createdDataSetTableSpecs = createdDataSetTableSpecs;
	}
}
