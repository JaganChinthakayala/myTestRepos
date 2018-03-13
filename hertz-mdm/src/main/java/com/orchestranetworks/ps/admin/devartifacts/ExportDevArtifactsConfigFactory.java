/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.admin.devartifacts;

import java.util.*;

import com.onwbp.adaptation.*;
import com.orchestranetworks.instance.*;
import com.orchestranetworks.ps.util.*;
import com.orchestranetworks.service.*;

/**
 * A factory that creates <code>ExportDevArtifactConfig</code>s.
 */
public class ExportDevArtifactsConfigFactory extends AbstractDevArtifactsConfigFactory
{
	@Override
	public DevArtifactsConfig createConfig(
		Repository repo,
		Session session,
		Map<String, String[]> paramMap) throws OperationException
	{
		ExportDevArtifactsConfig config = new ExportDevArtifactsConfig();
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

		config.setWorkflowModels(getAllWorkflowModels(repo));
	}

	private List<String> getAllWorkflowModels(Repository repo)
	{
		AdaptationHome wfDataSpace = AdminUtil.getWorkflowModelsDataSpace(repo);
		@SuppressWarnings("unchecked")
		List<Adaptation> dataSets = wfDataSpace.findAllRoots();
		ArrayList<String> dataSetNames = new ArrayList<String>();
		for (Adaptation dataSet : dataSets)
		{
			dataSetNames.add(dataSet.getAdaptationName().getStringName());
		}
		return dataSetNames;
	}
}
