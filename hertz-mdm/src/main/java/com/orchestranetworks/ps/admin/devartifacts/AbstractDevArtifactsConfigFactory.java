/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.admin.devartifacts;

import java.io.*;
import java.util.*;

import com.orchestranetworks.instance.*;
import com.orchestranetworks.service.*;

/**
 * An abstract factory that can initialize configs from http parameters and properties file
 */
public abstract class AbstractDevArtifactsConfigFactory implements DevArtifactsConfigFactory
{
	/**
	 * Initialize the configuration from http params
	 * 
	 * @param config the config to initialize
	 * @param repo the repository
	 * @param session the user's session
	 * @param paramMap the map containing the http parameters.
	 *                 See {@link javax.servlet.ServletRequest#getParameterMap()}.
	 */
	protected void initConfig(
		DevArtifactsConfig config,
		Repository repo,
		Session session,
		Map<String, String[]> paramMap) throws OperationException
	{
		String[] environmentCopyValues = paramMap.get(DevArtifactsService.PARAM_ENVIRONMENT_COPY);
		config.setEnvironmentCopy(isBooleanParamSet(environmentCopyValues));

		// Get properties file from system property
		String propertiesFile = System.getProperty(DevArtifactsPropertyFileHelper.PROPERTIES_FILE_SYSTEM_PROPERTY);
		// If it wasn't specified as a system property, then get it from the servlet parameters
		if (propertiesFile == null)
		{
			String[] propertiesFileValues = paramMap.get(DevArtifactsService.PARAM_PROPERTIES_FILE);
			if (propertiesFileValues == null || propertiesFileValues.length == 0
				|| "".equals(propertiesFileValues[0]))
			{
				propertiesFileValues = new String[] { DevArtifactsPropertyFileHelper.DEFAULT_PROPERTIES_FILE };
			}
			propertiesFile = propertiesFileValues[0];
		}
		loadProperties(config, repo, session, propertiesFile);
	}

	/**
	 * Load properties from the given file into the config
	 * 
	 * @param config the config
	 * @param repo the repository
	 * @param session the user's session
	 * @param propertiesFile the properties file
	 */
	protected void loadProperties(
		DevArtifactsConfig config,
		Repository repo,
		Session session,
		String propertiesFile) throws OperationException
	{
		try
		{
			DevArtifactsPropertyFileHelper helper = new DevArtifactsPropertyFileHelper(
				propertiesFile);
			helper.initConfig(config, repo, session);
		}
		catch (IOException ex)
		{
			throw OperationException.createError(ex);
		}
	}

	/**
	 * Convenience method for checking if an http parameter's value is true.
	 * The parameter map contains an array of strings for each parameter's value,
	 * but for the boolean parameters we know it will only contain one value with either "false" or "true".
	 * 
	 * @param paramValues the array of values. See {@link javax.servlet.ServletRequest#getParameterMap()}.
	 */
	protected static boolean isBooleanParamSet(String[] paramValues)
	{
		return paramValues != null && paramValues.length != 0 && paramValues[0].equals("true");
	}
}
