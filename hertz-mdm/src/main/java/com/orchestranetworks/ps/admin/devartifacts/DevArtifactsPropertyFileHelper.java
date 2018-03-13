/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.admin.devartifacts;

import java.io.*;
import java.util.*;

import com.onwbp.adaptation.*;
import com.onwbp.base.text.*;
import com.orchestranetworks.instance.*;
import com.orchestranetworks.ps.admin.*;
import com.orchestranetworks.ps.util.*;
import com.orchestranetworks.service.*;

/**
 * A class to help read values from the properties file
 */
public class DevArtifactsPropertyFileHelper extends PropertyFileHelper
{
	public static final String PROPERTIES_FILE_SYSTEM_PROPERTY = "dev.artifacts.properties";
	public static final String DEFAULT_PROPERTIES_FILE = System.getProperty("ebx.home")
		+ "/dev-artifacts.properties";

	protected static final int PROPERTY_TOKEN_INDEX_PARENT_DATA_SPACE_NAME = 1;
	protected static final int PROPERTY_TOKEN_INDEX_DATA_SPACE_LABEL = 2;
	protected static final int PROPERTY_TOKEN_INDEX_DATA_SPACE_OWNER = 3;

	private static final String DEFAULT_VALUE_CREATE_DATA_SPACES = "false";
	private static final String DEFAULT_VALUE_CREATE_DATA_SETS = "false";

	public DevArtifactsPropertyFileHelper(String propertiesFile) throws IOException
	{
		super(propertiesFile);
	}

	/**
	 * Initialize the configuration from the loaded properties
	 * 
	 * @param config the configuration
	 * @param repo the repository
	 */
	public void initConfig(DevArtifactsConfig config, Repository repo, Session session)
		throws IOException, OperationException
	{
		initFolders(config);
		initLineSeparator(config);
		initDataSpacesForPermissions(config, repo, session);

		config.setProcessDataSetPermissionsInChildDataSpaces(Boolean.valueOf(
			props.getProperty(
				DevArtifactsPropertyConstants.PROPERTY_DATA_SET_PERMISSIONS_IN_CHILD_DATA_SPACES,
				"true")).booleanValue());
		initDataSetsForPermissions(config, repo, session);

		initTablesForData(config, repo);
		initAdminFlags(config, repo);

		String usersRolesPredicate = props.getProperty(DevArtifactsPropertyConstants.PROPERTY_USERS_ROLES_PREDICATE);
		config.setUsersRolesPredicate(usersRolesPredicate != null
			&& usersRolesPredicate.trim().length() == 0 ? null : usersRolesPredicate);
	}

	private void initFolders(DevArtifactsConfig config) throws IOException
	{
		config.setDataFolder(new File(
			getRequiredProperty(DevArtifactsPropertyConstants.PROPERTY_DATA_FOLDER)));
		config.setPermissionsFolder(new File(
			getRequiredProperty(DevArtifactsPropertyConstants.PROPERTY_PERMISSIONS_FOLDER)));
		config.setWorkflowsFolder(new File(
			getRequiredProperty(DevArtifactsPropertyConstants.PROPERTY_WORKFLOWS_FOLDER)));
		config.setAdminFolder(new File(
			getRequiredProperty(DevArtifactsPropertyConstants.PROPERTY_ADMIN_FOLDER)));
		config.setCopyEnvironmentFolder(new File(
			getRequiredProperty(DevArtifactsPropertyConstants.PROPERTY_COPY_ENVIRONMENT_FOLDER)));
	}

	private void initLineSeparator(DevArtifactsConfig config) throws IOException
	{
		String lineSepType = getRequiredProperty(DevArtifactsPropertyConstants.PROPERTY_LINE_SEPARATOR);
		String lineSep = DevArtifactsPropertyConstants.LINE_SEP_TYPE_WINDOWS.equals(lineSepType) ? "\r\n"
			: "\n";
		config.setLineSeparator(lineSep);
	}

	private void initTablesForData(DevArtifactsConfig config, Repository repo)
	{
		ImportDevArtifactsConfig importConfig = (config instanceof ImportDevArtifactsConfig) ? (ImportDevArtifactsConfig) config
			: null;
		String[] tableValues = getPropertyAsArray(DevArtifactsPropertyConstants.PROPERTY_TABLES_FOR_DATA);
		ArrayList<AdaptationTable> tables = new ArrayList<AdaptationTable>();
		HashMap<DataSetCreationKey, List<String>> createdDataSetTableSpecs = new HashMap<DataSetCreationKey, List<String>>();
		for (String tableValue : tableValues)
		{
			// Replace wildcard with all tables for that data set
			if (tableValue.endsWith(DevArtifactsPropertyConstants.WILDCARD))
			{
				Adaptation dataSet = getDataSetFromProperty(tableValue, repo);
				if (dataSet == null)
				{
					addTableSpecToMap(tableValue, createdDataSetTableSpecs);
				}
				else
				{
					tables.addAll(AdaptationUtil.getAllTables(dataSet));
				}
			}
			else
			{
				AdaptationTable table = getTableFromProperty(tableValue, repo);
				if (table == null)
				{
					addTableSpecToMap(tableValue, createdDataSetTableSpecs);
				}
				else
				{
					tables.add(table);
				}
			}
		}
		config.setTablesForData(tables);
		if (importConfig != null)
		{
			importConfig.setCreatedDataSetTableSpecs(createdDataSetTableSpecs);
		}
	}

	private void addTableSpecToMap(
		String tableValue,
		Map<DataSetCreationKey, List<String>> createdDataSetTableSpecs)
	{
		String[] tokens = getPropertyValueTokens(tableValue);
		String dataSpaceName = tokens[PROPERTY_TOKEN_INDEX_DATA_SPACE_NAME];
		String dataSetName = tokens[PROPERTY_TOKEN_INDEX_DATA_SET_NAME];
		String tableName = tokens[PROPERTY_TOKEN_INDEX_TABLE_NAME];

		DataSetCreationKey dataSetCreationKey = new DataSetCreationKey(dataSpaceName, dataSetName);
		List<String> tableSpecs = createdDataSetTableSpecs.get(dataSetCreationKey);
		if (tableSpecs == null)
		{
			tableSpecs = new ArrayList<String>();
			createdDataSetTableSpecs.put(dataSetCreationKey, tableSpecs);
		}
		tableSpecs.add(tableName);
	}

	private void initDataSetsForPermissions(
		DevArtifactsConfig config,
		Repository repo,
		Session session) throws OperationException
	{
		ImportDevArtifactsConfig importConfig = (config instanceof ImportDevArtifactsConfig) ? (ImportDevArtifactsConfig) config
			: null;
		String[] dataSetValues = getPropertyAsArray(DevArtifactsPropertyConstants.PROPERTY_DATA_SETS_FOR_PERMISSIONS);
		ArrayList<Adaptation> dataSets = new ArrayList<Adaptation>();
		ArrayList<DataSetCreationInfo> dataSetsToCreate = new ArrayList<DataSetCreationInfo>();
		for (String dataSetValue : dataSetValues)
		{
			Adaptation dataSet = getDataSetFromProperty(dataSetValue, repo);
			if (dataSet == null)
			{
				boolean throwError = true;
				if (importConfig != null)
				{
					boolean createDataSets = Boolean.valueOf(
						props.getProperty(
							DevArtifactsPropertyConstants.PROPERTY_CREATE_DATA_SETS,
							DEFAULT_VALUE_CREATE_DATA_SETS)).booleanValue();
					if (createDataSets)
					{
						String[] tokens = getPropertyValueTokens(dataSetValue);
						if (tokens.length > PROPERTY_TOKEN_INDEX_DATA_MODEL_XSD)
						{
							String dataModelXSD = tokens[PROPERTY_TOKEN_INDEX_DATA_MODEL_XSD];
							if (dataModelXSD.length() > 0)
							{
								String dataSpaceName = tokens[PROPERTY_TOKEN_INDEX_DATA_SPACE_NAME];
								String dataSetName = tokens[PROPERTY_TOKEN_INDEX_DATA_SET_NAME];

								dataSetsToCreate.add(new DataSetCreationInfo(
									new DataSetCreationKey(dataSpaceName, dataSetName),
									dataModelXSD));
								throwError = false;
							}
						}
					}
				}
				if (throwError)
				{
					throw OperationException.createError("Data set " + dataSetValue
						+ " not found during Dev Artifacts processing.");
				}
			}
			else
			{
				dataSets.add(dataSet);
			}
		}
		config.setDataSetsForPermissions(dataSets);
		if (importConfig != null)
		{
			importConfig.setDataSetsToCreate(dataSetsToCreate);
		}
	}

	private void initDataSpacesForPermissions(
		DevArtifactsConfig config,
		Repository repo,
		Session session) throws OperationException
	{
		ImportDevArtifactsConfig importConfig = (config instanceof ImportDevArtifactsConfig) ? (ImportDevArtifactsConfig) config
			: null;
		String[] dataSpaceValues = getPropertyAsArray(DevArtifactsPropertyConstants.PROPERTY_DATA_SPACES_FOR_PERMISSIONS);
		ArrayList<AdaptationHome> dataSpaces = new ArrayList<AdaptationHome>();
		for (String dataSpaceValue : dataSpaceValues)
		{
			AdaptationHome dataSpace = getDataSpaceFromProperty(dataSpaceValue, repo);
			if (importConfig != null)
			{
				if (dataSpace == null)
				{
					boolean createDataSpaces = Boolean.valueOf(
						props.getProperty(
							DevArtifactsPropertyConstants.PROPERTY_CREATE_DATA_SPACES,
							DEFAULT_VALUE_CREATE_DATA_SPACES)).booleanValue();
					if (createDataSpaces)
					{
						dataSpace = createDataSpace(
							getPropertyValueTokens(dataSpaceValue),
							repo,
							session);
					}
				}
				else
				{
					updateDataSpaceInfo(
						dataSpace,
						getPropertyValueTokens(dataSpaceValue),
						repo,
						session);
				}
			}
			if (dataSpace == null)
			{
				throw OperationException.createError("Data space " + dataSpaceValue
					+ " not found during Dev Artifacts processing.");
			}
			dataSpaces.add(dataSpace);
		}
		config.setDataSpacesForPermissions(dataSpaces);
	}

	private static AdaptationHome createDataSpace(
		String[] dataSpaceTokens,
		Repository repo,
		Session session) throws OperationException
	{
		String parentDataSpaceName = dataSpaceTokens[PROPERTY_TOKEN_INDEX_PARENT_DATA_SPACE_NAME];
		if (parentDataSpaceName == null || parentDataSpaceName.trim().length() == 0)
		{
			parentDataSpaceName = "Reference";
		}
		AdaptationHome parentDataSpace = repo.lookupHome(HomeKey.forBranchName(parentDataSpaceName));
		if (parentDataSpace == null)
		{
			throw OperationException.createError("Could not find data space " + parentDataSpaceName
				+ ".");
		}
		HomeKey dataSpaceKey = HomeKey.forBranchName(dataSpaceTokens[PROPERTY_TOKEN_INDEX_DATA_SPACE_NAME]);
		UserMessage label = UserMessage.createInfo(dataSpaceTokens[PROPERTY_TOKEN_INDEX_DATA_SPACE_LABEL]);
		Profile owner = Profile.parse(dataSpaceTokens[PROPERTY_TOKEN_INDEX_DATA_SPACE_OWNER]);
		return repo.createHome(parentDataSpace, dataSpaceKey, owner, session, label, null);
	}

	private static void updateDataSpaceInfo(
		AdaptationHome dataSpace,
		String[] dataSpaceTokens,
		Repository repo,
		Session session) throws OperationException
	{
		String label = dataSpaceTokens[PROPERTY_TOKEN_INDEX_DATA_SPACE_LABEL];
		repo.setDocumentationLabel(dataSpace, label, session.getLocale(), session);
		// TODO: No way in API to set owner

	}
	private void initAdminFlags(DevArtifactsConfig config, Repository repo)
	{
		config.setProcessDirectoryData(Boolean.valueOf(
			props.getProperty(DevArtifactsPropertyConstants.PROPERTY_DIRECTORY, "false"))
			.booleanValue());
		config.setProcessGlobalPermissionsData(Boolean.valueOf(
			props.getProperty(DevArtifactsPropertyConstants.PROPERTY_GLOBAL_PERMISSIONS, "false"))
			.booleanValue());
		config.setProcessViewsData(Boolean.valueOf(
			props.getProperty(DevArtifactsPropertyConstants.PROPERTY_VIEWS, "false"))
			.booleanValue());
		config.setProcessPublishedViewsDataOnly(Boolean.valueOf(
			props.getProperty(DevArtifactsPropertyConstants.PROPERTY_PUBLISHED_VIEWS_ONLY, "false"))
			.booleanValue());
		config.setProcessTasksData(Boolean.valueOf(
			props.getProperty(DevArtifactsPropertyConstants.PROPERTY_TASKS, "false"))
			.booleanValue());
		config.setProcessPerspectivesData(Boolean.valueOf(
			props.getProperty(DevArtifactsPropertyConstants.PROPERTY_PERSPECTIVES, "false"))
			.booleanValue());
		config.setProcessAddonAdixData(Boolean.valueOf(
			props.getProperty(DevArtifactsPropertyConstants.PROPERTY_ADDON_ADIX, "false"))
			.booleanValue());
		config.setProcessAddonDaqaData(Boolean.valueOf(
			props.getProperty(DevArtifactsPropertyConstants.PROPERTY_ADDON_DAQA, "false"))
			.booleanValue());
		config.setProcessAddonDqidData(Boolean.valueOf(
			props.getProperty(DevArtifactsPropertyConstants.PROPERTY_ADDON_DQID, "false"))
			.booleanValue());
	}
}
