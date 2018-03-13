/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.admin.devartifacts;

import java.io.*;
import java.util.*;

import com.orchestranetworks.instance.*;
import com.orchestranetworks.service.*;

/**
 * A factory that creates <code>ImportDevArtifactConfig</code>s.
 */
public class ImportDevArtifactsConfigFactory extends AbstractDevArtifactsConfigFactory
{
	private static final String DATA_SET_FILE_SUFFIX = ".xml";

	@Override
	public DevArtifactsConfig createConfig(
		Repository repo,
		Session session,
		Map<String, String[]> paramMap) throws OperationException
	{
		ImportDevArtifactsConfig config = new ImportDevArtifactsConfig();
		initConfig(config, repo, session, paramMap);
		return config;
	}

	@Override
	protected void initConfig(
		DevArtifactsConfig config,
		Repository repo,
		Session session,
		Map<String, String[]> paramMap) throws OperationException
	{
		super.initConfig(config, repo, session, paramMap);

		// If replace mode, import all models from the folder
		if (isBooleanParamSet(paramMap.get(ImportDevArtifactsService.PARAM_REPLACE_MODE)))
		{
			config.setWorkflowModels(getWorkflowsFromFolder(config.getWorkflowsFolder()));
		}
		// Otherwise just import the ones specified by the http params
		else
		{
			config.setWorkflowModels(getWorkflowModelsFromParamMap(paramMap));
		}

		// Cast the config and set additional params
		ImportDevArtifactsConfig importConfig = (ImportDevArtifactsConfig) config;

		if (isBooleanParamSet(paramMap.get(ImportDevArtifactsService.PARAM_REPLACE_MODE)))
		{
			importConfig.setImportMode(ImportSpecMode.REPLACE);
		}
		else
		{
			importConfig.setImportMode(ImportSpecMode.UPDATE_OR_INSERT);
		}

		boolean skipNonExistingFiles = isBooleanParamSet(paramMap.get(ImportDevArtifactsService.PARAM_SKIP_NONEXISTING_FILES));
		importConfig.setSkipNonExistingFiles(skipNonExistingFiles);
	}

	public static List<String> getWorkflowsFromFolder(File folder)
	{
		return getArtifactsFromFolder(folder, new DevArtifactsFilenameFilter(
			DevArtifactsService.WORKFLOW_PREFIX,
			DATA_SET_FILE_SUFFIX));
	}

	// Get the list of workflow model names from the HTTP parameter map.
	// They will start with <code>PARAM_WORKFLOW_PREFIX</code>.
	private List<String> getWorkflowModelsFromParamMap(Map<String, String[]> paramMap)
	{
		ArrayList<String> workflows = new ArrayList<String>();
		for (Map.Entry<String,String[]> entry : paramMap.entrySet())
		{
			String paramName = entry.getKey();
			if (paramName.startsWith(ImportDevArtifactsService.PARAM_WORKFLOW_PREFIX))
			{
				if (isBooleanParamSet(entry.getValue()))
				{
					workflows.add(paramName.substring(ImportDevArtifactsService.PARAM_WORKFLOW_PREFIX.length()));
				}
			}
		}
		return workflows;
	}

	public static List<String> getArtifactsFromFolder(
		File folder,
		DevArtifactsFilenameFilter filenameFilter)
	{
		if (folder.exists())
		{
			// Get all filenames that match the filter
			String[] filenames = folder.list(filenameFilter);
			if (filenames == null || filenames.length == 0)
			{
				return new ArrayList<String>();
			}
			ArrayList<String> artifacts = new ArrayList<String>();
			for (String filename : filenames)
			{
				// Take off the prefix and the suffix to get artifact name
				String artifact = filename.substring(
					filenameFilter.getPrefix().length(),
					filename.length() - filenameFilter.getSuffix().length());
				artifacts.add(artifact);
			}
			Collections.sort(artifacts);
			return artifacts;
		}
		return new ArrayList<String>();
	}
}
