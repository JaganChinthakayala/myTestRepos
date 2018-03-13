/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.admin.cleanworkflows;

import java.io.*;

import com.orchestranetworks.instance.*;
import com.orchestranetworks.service.*;

/**
 * A factory that can initialize configs from a properties file
 */
public class DefaultCleanWorkflowsConfigFactory implements CleanWorkflowsConfigFactory
{
	protected String propertiesFolder;
	protected String propertiesFile;

	public DefaultCleanWorkflowsConfigFactory()
	{
		this(null, null);
	}

	public DefaultCleanWorkflowsConfigFactory(String propertiesFolder, String propertiesFile)
	{
		this.propertiesFolder = propertiesFolder;
		this.propertiesFile = propertiesFile;
	}

	@Override
	public CleanWorkflowsConfig createConfig(Repository repo, Session session)
		throws OperationException
	{
		CleanWorkflowsConfig config = new CleanWorkflowsConfig();
		initConfig(config, repo, session);
		return config;
	}

	/**
	 * Initialize the configuration from http params
	 * 
	 * @param config the config to initialize
	 * @param repo the repository
	 * @param session the session
	 */
	protected void initConfig(CleanWorkflowsConfig config, Repository repo, Session session)
		throws OperationException
	{
		// Get properties file from system property
		String folder = System.getProperty(CleanWorkflowsPropertyFileHelper.PROPERTIES_FOLDER_SYSTEM_PROPERTY);
		// If it wasn't specified as a system property, then use the one passed in
		if (folder == null)
		{
			folder = propertiesFolder;
		}
		String file = System.getProperty(CleanWorkflowsPropertyFileHelper.PROPERTIES_FILE_SYSTEM_PROPERTY);
		// If it wasn't specified as a system property, then use the one passed in
		if (file == null)
		{
			file = propertiesFile;
		}
		loadProperties(config, repo, session, folder + "/" + file);
	}

	/**
	 * Load properties from the given file into the config
	 * 
	 * @param config the config
	 * @param repo the repository
	 * @param session the session
	 * @param propertiesFolder the properties folder
	 * @param propertiesFile the properties file
	 */
	protected void loadProperties(
		CleanWorkflowsConfig config,
		Repository repo,
		Session session,
		String fullFilePath) throws OperationException
	{
		try
		{
			CleanWorkflowsPropertyFileHelper helper = new CleanWorkflowsPropertyFileHelper(
				fullFilePath);
			helper.initConfig(config, repo, session);
		}
		catch (IOException ex)
		{
			throw OperationException.createError(ex);
		}
	}
}
