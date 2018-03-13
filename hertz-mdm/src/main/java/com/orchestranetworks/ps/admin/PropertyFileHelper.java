/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.admin;

import java.io.*;
import java.util.*;

import com.onwbp.adaptation.*;
import com.orchestranetworks.instance.*;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.service.*;
import com.orchestranetworks.workflow.*;

/**
 * A class to help read values from the properties file.
 * It can be subclassed to handle specific properties.
 */
public class PropertyFileHelper
{
	private static final String PROPERTY_VALUE_SEPARATOR = "\\s*,\\s*";
	private static final String PROPERTY_TOKEN_SEPARATOR = "\\s*\\|\\s*";

	protected static final int PROPERTY_TOKEN_INDEX_DATA_SPACE_NAME = 0;
	protected static final int PROPERTY_TOKEN_INDEX_DATA_SET_NAME = 1;
	protected static final int PROPERTY_TOKEN_INDEX_TABLE_NAME = 2;
	protected static final int PROPERTY_TOKEN_INDEX_DATA_MODEL_XSD = 2;

	protected Properties props;

	public PropertyFileHelper(String propertiesFile) throws IOException
	{
		// Load the properties
		props = new Properties();
		InputStream in = new FileInputStream(propertiesFile);
		try
		{
			props.load(in);
		}
		finally
		{
			in.close();
		}
	}

	public Properties getProperties()
	{
		return props;
	}

	public String getRequiredProperty(String propertyName) throws IOException
	{
		String propertyValue = props.getProperty(propertyName);
		if (propertyValue == null || "".equals(propertyValue))
		{
			throw new IOException("Value must be specified for property " + propertyName + ".");
		}
		return propertyValue;
	}

	public String[] getPropertyAsArray(String propertyName)
	{
		String propertyValue = props.getProperty(propertyName);
		if (propertyValue == null || "".equals(propertyValue))
		{
			return new String[0];
		}
		return propertyValue.split(PROPERTY_VALUE_SEPARATOR);
	}

	public static String[] getPropertyValueTokens(String propertyValue)
	{
		return propertyValue.split(PROPERTY_TOKEN_SEPARATOR);
	}

	public static AdaptationHome getDataSpaceFromProperty(String propertyValue, Repository repo)
	{
		String[] tokens = getPropertyValueTokens(propertyValue);
		return repo.lookupHome(HomeKey.forBranchName(tokens[PROPERTY_TOKEN_INDEX_DATA_SPACE_NAME]));
	}

	public static Adaptation getDataSetFromProperty(String propertyValue, Repository repo)
	{
		String[] tokens = getPropertyValueTokens(propertyValue);
		AdaptationHome dataSpace = repo.lookupHome(HomeKey.forBranchName(tokens[PROPERTY_TOKEN_INDEX_DATA_SPACE_NAME]));
		return dataSpace == null ? null
			: dataSpace.findAdaptationOrNull(AdaptationName.forName(tokens[PROPERTY_TOKEN_INDEX_DATA_SET_NAME]));
	}

	public static AdaptationTable getTableFromProperty(String propertyValue, Repository repo)
	{
		String[] tokens = getPropertyValueTokens(propertyValue);
		AdaptationHome dataSpace = repo.lookupHome(HomeKey.forBranchName(tokens[PROPERTY_TOKEN_INDEX_DATA_SPACE_NAME]));
		if (dataSpace == null)
		{
			return null;
		}
		Adaptation dataSet = dataSpace.findAdaptationOrNull(AdaptationName.forName(tokens[PROPERTY_TOKEN_INDEX_DATA_SET_NAME]));
		return (dataSet == null || dataSet.hasSevereError()) ? null
			: dataSet.getTable(Path.parse(tokens[PROPERTY_TOKEN_INDEX_TABLE_NAME]));
	}

	public static List<PublishedProcess> getWorkflowPublicationsFromProperty(
		String propertyValue,
		Repository repo,
		Session session)
	{
		WorkflowEngine wfEngine = WorkflowEngine.getFromRepository(repo, session);
		ArrayList<PublishedProcess> publishedProcesses = new ArrayList<PublishedProcess>();
		@SuppressWarnings("unchecked")
		List<PublishedProcessKey> publishedKeys = wfEngine.getPublishedKeys(false);
		for (PublishedProcessKey publishedKey : publishedKeys)
		{
			PublishedProcess publishedProcess = wfEngine.getPublishedProcess(publishedKey);
			if (propertyValue.equals(publishedProcess.getAdaptationName().getStringName()))
			{
				publishedProcesses.add(publishedProcess);
			}
		}
		return publishedProcesses;
	}
}
