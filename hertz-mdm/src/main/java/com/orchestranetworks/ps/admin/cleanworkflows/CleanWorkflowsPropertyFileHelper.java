/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.admin.cleanworkflows;

import java.io.*;
import java.util.*;

import com.onwbp.adaptation.*;
import com.orchestranetworks.instance.*;
import com.orchestranetworks.ps.admin.*;
import com.orchestranetworks.service.*;
import com.orchestranetworks.workflow.*;

/**
 * A class to help read values from the properties file
 */
public class CleanWorkflowsPropertyFileHelper extends PropertyFileHelper
{
	public static final String PROPERTIES_FOLDER_SYSTEM_PROPERTY = "clean.workflows.properties.folder";
	public static final String PROPERTIES_FILE_SYSTEM_PROPERTY = "clean.workflows.properties.file";
	public static final String DEFAULT_PROPERTIES_FOLDER = System.getProperty("ebx.home");
	public static final String DEFAULT_PROPERTIES_FILE = "clean-workflows.properties";
	private static final String ALL_WORKFLOW_PUBLICATIONS = "all";

	public CleanWorkflowsPropertyFileHelper(String propertiesFile) throws IOException
	{
		super(propertiesFile);
	}

	/**
	 * Initialize the configuration from the loaded properties
	 * 
	 * @param config the configuration
	 * @param repo the repository
	 */
	public void initConfig(CleanWorkflowsConfig config, Repository repo, Session session)
		throws IOException
	{
		initMasterDataSpace(config, repo);
		initChildDataSpacesToSkip(config, repo);
		initWorkflowPublications(config, repo, session);
	}

	private void initMasterDataSpace(CleanWorkflowsConfig config, Repository repo)
		throws IOException
	{
		String dataSpaceValue = getRequiredProperty(CleanWorkflowsPropertyConstants.PROPERTY_MASTER_DATA_SPACE);
		config.setMasterDataSpace(getDataSpaceFromProperty(dataSpaceValue, repo));
	}

	private void initChildDataSpacesToSkip(CleanWorkflowsConfig config, Repository repo)
	{
		String[] dataSpaceValues = getPropertyAsArray(CleanWorkflowsPropertyConstants.PROPERTY_CHILD_DATA_SPACES_TO_SKIP);
		ArrayList<AdaptationHome> dataSpaces = new ArrayList<AdaptationHome>();
		for (String dataSpaceValue : dataSpaceValues)
		{
			dataSpaces.add(getDataSpaceFromProperty(dataSpaceValue, repo));
		}
		config.setChildDataSpacesToSkip(dataSpaces);
	}

	private void initWorkflowPublications(
		CleanWorkflowsConfig config,
		Repository repo,
		Session session)
	{
		String[] publicationValues = getPropertyAsArray(CleanWorkflowsPropertyConstants.PROPERTY_WORKFLOW_PUBLICATIONS);
		ArrayList<PublishedProcess> publications = new ArrayList<PublishedProcess>();
		if (isAllWorkflowPublications(publicationValues))
		{
			WorkflowEngine wfEngine = WorkflowEngine.getFromRepository(repo, session);
			@SuppressWarnings("unchecked")
			List<PublishedProcessKey> publishedProcessKeys = wfEngine.getPublishedKeys(false);
			for (PublishedProcessKey publishedProcessKey : publishedProcessKeys)
			{
				publications.add(wfEngine.getPublishedProcess(publishedProcessKey));
			}
		}
		else
		{
			for (String publicationValue : publicationValues)
			{
				List<PublishedProcess> publishedProcesses = getWorkflowPublicationsFromProperty(
					publicationValue,
					repo,
					session);
				for (PublishedProcess publishedProcess : publishedProcesses)
				{
					publications.add(publishedProcess);
				}
			}
		}
		config.setWorkflowPublications(publications);
	}

	private static boolean isAllWorkflowPublications(String[] publicationValues)
	{
		return publicationValues.length == 1
			&& ALL_WORKFLOW_PUBLICATIONS.equals(publicationValues[0].toLowerCase());
	}
}
