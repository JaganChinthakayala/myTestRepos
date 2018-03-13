/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.admin.servicepermission;

import java.io.*;
import java.util.*;

import org.apache.commons.lang.*;

import com.onwbp.adaptation.*;
import com.onwbp.base.text.*;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.service.*;

/**
 * Configured with a semi-colon separated list of backend modes to disable, and using ebx.properties
 * to look for what the actual backend.mode property value is, if the current backend mode is in the collection
 * of modes to disable, then the service permission will be hidden.  Otherwise the service will be enabled.
 */
public class BackendModeServicePermission implements ServicePermission
{
	private static final String EBX_PROPERTIES_FILE = System.getProperty("ebx.home")
		+ "/ebx.properties";
	private static final String PROPERTY_BACKEND_MODE = "backend.mode";
	private static final String BACKEND_MODE_SEPARATOR = ";";

	private String backendModesToDisable;

	@Override
	public ActionPermission getPermission(SchemaNode node, Adaptation adaptation, Session session)
	{
		Properties props;
		try
		{
			props = loadEBXPropertiesFile();
		}
		catch (IOException ex)
		{
			LoggingCategory.getKernel().error("Error loading ebx.properties file.", ex);
			return ActionPermission.getHidden();
		}
		String[] backendModesToDisableArr = backendModesToDisable.split(BACKEND_MODE_SEPARATOR);
		String backendMode = props.getProperty(PROPERTY_BACKEND_MODE);
		return !ArrayUtils.contains(backendModesToDisableArr, backendMode) ? ActionPermission.getEnabled()
			: ActionPermission.getHidden(UserMessage.createInfo("Can't launch service on a "
				+ backendMode + " instance."));
	}

	private static Properties loadEBXPropertiesFile() throws IOException
	{
		Properties props = new Properties();
		FileInputStream in = new FileInputStream(EBX_PROPERTIES_FILE);
		try
		{
			props.load(in);
		}
		finally
		{
			in.close();
		}
		return props;
	}

	public String getBackendModesToDisable()
	{
		return this.backendModesToDisable;
	}

	public void setBackendModesToDisable(String backendModesToDisable)
	{
		this.backendModesToDisable = backendModesToDisable;
	}
}
