/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.admin.devartifacts;

import java.io.*;
import java.util.*;

import com.onwbp.adaptation.*;

/**
 * Contains configuration for a dev artifacts import/export
 */
public abstract class DevArtifactsConfig
{
	private File dataFolder;
	private File permissionsFolder;
	private File workflowsFolder;
	private File adminFolder;
	private File copyEnvironmentFolder;
	private String lineSeparator;
	private List<AdaptationTable> tablesForData;
	private boolean processDataSetPermissionsInChildDataSpaces;
	private List<Adaptation> dataSetsForPermissions;
	private List<AdaptationHome> dataSpacesForPermissions;
	private List<String> workflowModels;
	private boolean processDirectoryData;
	private boolean processGlobalPermissionsData;
	private boolean processViewsData;
	private boolean processPublishedViewsDataOnly;
	private boolean processTasksData;
	private boolean processUserInterfaceData;
	private boolean processPerspectivesData;
	private boolean environmentCopy;
	private String usersRolesPredicate;
	private boolean processAddonAdixData;
	private boolean processAddonDaqaData;
	private boolean processAddonDqidData;

	public File getDataFolder()
	{
		return this.dataFolder;
	}

	public void setDataFolder(File dataFolder)
	{
		this.dataFolder = dataFolder;
	}

	public File getPermissionsFolder()
	{
		return this.permissionsFolder;
	}

	public void setPermissionsFolder(File permissionsFolder)
	{
		this.permissionsFolder = permissionsFolder;
	}

	public File getWorkflowsFolder()
	{
		return this.workflowsFolder;
	}

	public void setWorkflowsFolder(File workflowsFolder)
	{
		this.workflowsFolder = workflowsFolder;
	}

	public File getAdminFolder()
	{
		return this.adminFolder;
	}

	public void setAdminFolder(File adminFolder)
	{
		this.adminFolder = adminFolder;
	}

	public File getCopyEnvironmentFolder()
	{
		return this.copyEnvironmentFolder;
	}

	public void setCopyEnvironmentFolder(File copyEnvironmentFolder)
	{
		this.copyEnvironmentFolder = copyEnvironmentFolder;
	}

	public String getLineSeparator()
	{
		return this.lineSeparator;
	}

	public void setLineSeparator(String lineSeparator)
	{
		this.lineSeparator = lineSeparator;
	}

	public List<AdaptationTable> getTablesForData()
	{
		return this.tablesForData;
	}

	public void setTablesForData(List<AdaptationTable> tablesForData)
	{
		this.tablesForData = tablesForData;
	}

	public boolean isProcessDataSetPermissionsInChildDataSpaces()
	{
		return this.processDataSetPermissionsInChildDataSpaces;
	}

	public void setProcessDataSetPermissionsInChildDataSpaces(
		boolean processDataSetPermissionsInChildDataSpaces)
	{
		this.processDataSetPermissionsInChildDataSpaces = processDataSetPermissionsInChildDataSpaces;
	}

	public List<Adaptation> getDataSetsForPermissions()
	{
		return this.dataSetsForPermissions;
	}

	public void setDataSetsForPermissions(List<Adaptation> dataSetsForPermissions)
	{
		this.dataSetsForPermissions = dataSetsForPermissions;
	}

	public List<AdaptationHome> getDataSpacesForPermissions()
	{
		return this.dataSpacesForPermissions;
	}

	public void setDataSpacesForPermissions(List<AdaptationHome> dataSpacesForPermissions)
	{
		this.dataSpacesForPermissions = dataSpacesForPermissions;
	}

	public List<String> getWorkflowModels()
	{
		return this.workflowModels;
	}

	public void setWorkflowModels(List<String> workflowModels)
	{
		this.workflowModels = workflowModels;
	}

	public boolean isProcessDirectoryData()
	{
		return this.processDirectoryData;
	}

	public void setProcessDirectoryData(boolean processDirectoryData)
	{
		this.processDirectoryData = processDirectoryData;
	}

	public boolean isProcessGlobalPermissionsData()
	{
		return this.processGlobalPermissionsData;
	}

	public void setProcessGlobalPermissionsData(boolean processGlobalPermissionsData)
	{
		this.processGlobalPermissionsData = processGlobalPermissionsData;
	}

	public boolean isProcessViewsData()
	{
		return this.processViewsData;
	}

	public void setProcessViewsData(boolean processViewsData)
	{
		this.processViewsData = processViewsData;
	}

	public boolean isProcessPublishedViewsDataOnly()
	{
		return this.processPublishedViewsDataOnly;
	}

	public void setProcessPublishedViewsDataOnly(boolean processPublishedViewsDataOnly)
	{
		this.processPublishedViewsDataOnly = processPublishedViewsDataOnly;
	}

	public boolean isProcessTasksData()
	{
		return this.processTasksData;
	}

	public void setProcessTasksData(boolean processTasksData)
	{
		this.processTasksData = processTasksData;
	}

	public boolean isProcessUserInterfaceData()
	{
		return this.processUserInterfaceData;
	}

	public void setProcessUserInterfaceData(boolean processUserInterfaceData)
	{
		this.processUserInterfaceData = processUserInterfaceData;
	}

	public boolean isProcessPerspectivesData()
	{
		return this.processPerspectivesData;
	}

	public void setProcessPerspectivesData(boolean processPerspectivesData)
	{
		this.processPerspectivesData = processPerspectivesData;
	}

	public boolean isEnvironmentCopy()
	{
		return this.environmentCopy;
	}

	public void setEnvironmentCopy(boolean environmentCopy)
	{
		this.environmentCopy = environmentCopy;
	}

	public String getUsersRolesPredicate()
	{
		return this.usersRolesPredicate;
	}

	public void setUsersRolesPredicate(String usersRolesPredicate)
	{
		this.usersRolesPredicate = usersRolesPredicate;
	}

	public boolean isProcessAddonAdixData()
	{
		return this.processAddonAdixData;
	}

	public void setProcessAddonAdixData(boolean processAddonAdixData)
	{
		this.processAddonAdixData = processAddonAdixData;
	}

	public boolean isProcessAddonDaqaData()
	{
		return this.processAddonDaqaData;
	}

	public void setProcessAddonDaqaData(boolean processAddonDaqaData)
	{
		this.processAddonDaqaData = processAddonDaqaData;
	}

	public boolean isProcessAddonDqidData()
	{
		return this.processAddonDqidData;
	}

	public void setProcessAddonDqidData(boolean processAddonDqidData)
	{
		this.processAddonDqidData = processAddonDqidData;
	}
}
