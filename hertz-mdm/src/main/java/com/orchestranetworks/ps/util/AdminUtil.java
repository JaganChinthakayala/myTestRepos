/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.util;

import com.onwbp.adaptation.*;
import com.orchestranetworks.instance.*;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.service.directory.*;
import com.orchestranetworks.service.preferences.*;

/**
 * A utility class for use with administration data. Many of its functions rely on things that aren't
 * part of the public API and are subject to change. Defining them here at least keeps it all in one place. 
 */
public class AdminUtil
{
	private static final String DIRECTORY_DATA_SPACE = "ebx-directory";
	private static final String DIRECTORY_DATA_SET = "ebx-directory";
	private static final Path DIRECTORY_USERS_TABLE_PATH = DirectoryPaths._Directory_Users.getPathInSchema();
	private static final Path DIRECTORY_USERS_LOGIN_PATH = DirectoryPaths._Directory_Users._Login;
	private static final Path DIRECTORY_USERS_PASSWORD_LAST_UPDATE_PATH = DirectoryPaths._Directory_Users._PasswordLastUpdate;
	private static final Path DIRECTORY_USERS_EMAIL_PATH = DirectoryPaths._Directory_Users._Email;
	private static final Path DIRECTORY_USERS_ROLES_TABLE_PATH = DirectoryPaths._Directory_UsersRoles.getPathInSchema();
	private static final Path DIRECTORY_USERS_ROLES_ROLE_PATH = DirectoryPaths._Directory_UsersRoles._Role;
	private static final Path DIRECTORY_USERS_ROLES_USER_PATH = DirectoryPaths._Directory_UsersRoles._User;
	private static final Path DIRECTORY_ROLES_TABLE_PATH = DirectoryPaths._Directory_Roles.getPathInSchema();
	private static final Path DIRECTORY_ROLES_NAME_PATH = DirectoryPaths._Directory_Roles._Name;
	private static final Path DIRECTORY_ROLES_INCLUSIONS_TABLE_PATH = DirectoryPaths._Directory_RolesInclusions.getPathInSchema();
	private static final Path DIRECTORY_SALUTATIONS_TABLE_PATH = DirectoryPaths._Directory_Salutations.getPathInSchema();
	private static final Path DIRECTORY_MAILING_LIST_GROUP_PATH = DirectoryPaths._Directory_MailingList;
	private static final Path DIRECTORY_POLICY_GROUP_PATH = DirectoryPaths._Directory_Policy;

	private static final String WORKFLOW_MODELS_DATA_SPACE = "ebx-workflow-definitions";
	private static final String WORKFLOW_MODELS_SCHEMA_LOCATION = "urn:ebx:module:ebx-root-1.0:/WEB-INF/ebx/schemas/workflow/workflow_definition_1.1.xsd";

	private static final String DMA_DATA_SPACE = "ebx-dma";

	private static final String GLOBAL_PERMISSIONS_DATA_SPACE = "ebx-globalPermissions";
	private static final String GLOBAL_PERMISSIONS_DATA_SET = "ebx-globalPermissions";
	private static final Path GLOBAL_PERMISSIONS_TABLE_PATH = Path.parse("/globalPermissions/globalPermissionsTable");

	private static final Path PERSPECTIVES_MENU_GROUP_PATH = Path.parse("/domain/menuItem");
	private static final Path PERSPECTIVES_ALLOWED_PROFILES_GROUP_PATH = Path.parse("/domain/properties");
	private static final Path PERSPECTIVES_ERGONOMICS_GROUP_PATH = Path.parse("/domain/ergonomicPolicy");
	private static final Path PERSPECTIVES_DEFAULT_OPTIONS_GROUP_PATH = Path.parse("/domain/optionsDefault");
	private static final Path PERSPECTIVES_COLORS_GROUP_PATH = Path.parse("/domain/customCSS");

	private static final String PERSPECTIVES_DATA_SPACE = "ebx-manager";
	private static final String PERSPECTIVES_DATA_SET = "ebx-manager";

	private static final String VIEWS_DATA_SPACE = "ebx-views";
	private static final String VIEWS_DATA_SET = "ebx-views";
	private static final Path CUSTOM_VIEWS_TABLE_PATH = ViewsPreferencesPaths_5_0._Preferences_TableViews.getPathInSchema();
	private static final Path DEFAULT_VIEWS_TABLE_PATH = ViewsPreferencesPaths_5_0._Preferences_TableViewsPreferences.getPathInSchema();

	private static final String TASK_SCHEDULER_DATA_SPACE = "ebx-scheduler";
	private static final String TASK_SCHEDULER_DATA_SET = "ebx-scheduler";
	private static final Path TASKS_TABLE_PATH = Path.parse("/scheduler/task");
	private static final Path TASKS_NAME_PATH = Path.parse("./name");
	private static final String TASKS_NAME_VALUE_FOR_REPOSITORY_CLEANUP = "Repository clean-up";

	private static final String ADDON_DAQA_DATA_SPACE = "ebx-addon-daqa";
	private static final String ADDON_DAQA_CONFIGURATION_DATA_SET = "ebx-addon-daqa-configuration-v2";
	private static final String ADDON_DAQA_REFERENCE_DATA_SET = "ebx-addon-daqa-reference-data";
	private static final String ADDON_DAQA_STATE_MACHINE_DATA_SET = "ebx-addon-daqa-statemachine";

	private static final String ADDON_ADIX_PREFERENCES_DATA_SPACE = "ebx-addon-adix";
	private static final String ADDON_ADIX_PREFERENCES_DATA_SET = "ebx-addon-adix";
	private static final String ADDON_ADIX_DATA_EXCHANGE_DATA_SPACE = "ebx-addon-adix-dataexchange";
	private static final String ADDON_ADIX_DATA_EXCHANGE_DATA_SET = "ebx-addon-adix-dataexchange";
	private static final String ADDON_ADIX_DATA_MODELING_DATA_SPACE = "ebx-addon-adix-datamodeler";
	private static final String ADDON_ADIX_DATA_MODELING_DATA_SET = "ebx-addon-adix-datamodeler";

	private static final String ADDON_DQID_DATA_SPACE = "ebx-addon-dqid-configuration";
	private static final String ADDON_DQID_DATA_SET = "ebx-addon-dqid-configuration";

	public static AdaptationHome getDirectoryDataSpace(Repository repo)
	{
		return repo.lookupHome(HomeKey.forBranchName(DIRECTORY_DATA_SPACE));
	}

	public static Adaptation getDirectoryDataSet(Repository repo)
	{
		AdaptationHome directoryDataSpace = getDirectoryDataSpace(repo);
		return getDirectoryDataSet(directoryDataSpace);
	}

	public static Adaptation getDirectoryDataSet(AdaptationHome directoryDataSpace)
	{
		return directoryDataSpace.findAdaptationOrNull(AdaptationName.forName(DIRECTORY_DATA_SET));
	}

	public static AdaptationTable getDirectoryUsersTable(Repository repo)
	{
		Adaptation directoryDataSet = getDirectoryDataSet(repo);
		return getDirectoryUsersTable(directoryDataSet);
	}

	public static AdaptationTable getDirectoryUsersTable(Adaptation directoryDataSet)
	{
		return directoryDataSet.getTable(DIRECTORY_USERS_TABLE_PATH);
	}

	public static Path getDirectoryUsersPasswordLastUpdatePath()
	{
		return DIRECTORY_USERS_PASSWORD_LAST_UPDATE_PATH;
	}

	public static Path getDirectoryUsersLoginPath()
	{
		return DIRECTORY_USERS_LOGIN_PATH;
	}

	public static Path getDirectoryUsersEmailPath()
	{
		return DIRECTORY_USERS_EMAIL_PATH;
	}

	public static AdaptationTable getDirectoryUsersRolesTable(Adaptation directoryDataSet)
	{
		return directoryDataSet.getTable(DIRECTORY_USERS_ROLES_TABLE_PATH);
	}

	public static Path getDirectoryUsersRolesRolePath()
	{
		return DIRECTORY_USERS_ROLES_ROLE_PATH;
	}

	public static Path getDirectoryUsersRolesUserPath()
	{
		return DIRECTORY_USERS_ROLES_USER_PATH;
	}

	public static AdaptationTable getDirectoryRolesInclusionsTable(Adaptation directoryDataSet)
	{
		return directoryDataSet.getTable(DIRECTORY_ROLES_INCLUSIONS_TABLE_PATH);
	}

	public static AdaptationTable getDirectoryRolesTable(Repository repo)
	{
		Adaptation directoryDataSet = getDirectoryDataSet(repo);
		return getDirectoryRolesTable(directoryDataSet);
	}

	public static AdaptationTable getDirectoryRolesTable(Adaptation directoryDataSet)
	{
		return directoryDataSet.getTable(DIRECTORY_ROLES_TABLE_PATH);
	}

	public static Path getDirectoryRolesNamePath()
	{
		return DIRECTORY_ROLES_NAME_PATH;
	}

	public static AdaptationTable getDirectorySalutationsTable(Repository repo)
	{
		Adaptation directoryDataSet = getDirectoryDataSet(repo);
		return getDirectorySalutationsTable(directoryDataSet);
	}

	public static AdaptationTable getDirectorySalutationsTable(Adaptation directoryDataSet)
	{
		return directoryDataSet.getTable(DIRECTORY_SALUTATIONS_TABLE_PATH);
	}

	public static SchemaNode getDirectoryMailingListGroup(Adaptation directoryDataSet)
	{
		return directoryDataSet.getSchemaNode().getNode(DIRECTORY_MAILING_LIST_GROUP_PATH);
	}

	public static SchemaNode getDirectoryPolicyGroup(Adaptation directoryDataSet)
	{
		return directoryDataSet.getSchemaNode().getNode(DIRECTORY_POLICY_GROUP_PATH);
	}

	public static AdaptationHome getWorkflowModelsDataSpace(Repository repo)
	{
		return repo.lookupHome(HomeKey.forBranchName(WORKFLOW_MODELS_DATA_SPACE));
	}

	public static SchemaLocation getWorkflowModelsSchemaLocation()
	{
		return SchemaLocation.parse(WORKFLOW_MODELS_SCHEMA_LOCATION);
	}

	public static AdaptationHome getDMADataSpace(Repository repo)
	{
		return repo.lookupHome(HomeKey.forBranchName(DMA_DATA_SPACE));
	}

	public static AdaptationHome getGlobalPermissionsDataSpace(Repository repo)
	{
		return repo.lookupHome(HomeKey.forBranchName(GLOBAL_PERMISSIONS_DATA_SPACE));
	}

	public static Adaptation getGlobalPermissionsDataSet(Repository repo)
	{
		AdaptationHome globalPermissionsDataSpace = getGlobalPermissionsDataSpace(repo);
		return getGlobalPermissionsDataSet(globalPermissionsDataSpace);
	}

	public static Adaptation getGlobalPermissionsDataSet(AdaptationHome globalPermissionsDataSpace)
	{
		return globalPermissionsDataSpace.findAdaptationOrNull(AdaptationName.forName(GLOBAL_PERMISSIONS_DATA_SET));
	}

	public static AdaptationTable getGlobalPermissionsTable(Adaptation globalPermissionsDataSet)
	{
		return globalPermissionsDataSet.getTable(GLOBAL_PERMISSIONS_TABLE_PATH);
	}

	public static AdaptationHome getPerspectivesDataSpace(Repository repo)
	{
		return repo.lookupHome(HomeKey.forBranchName(PERSPECTIVES_DATA_SPACE));
	}

	public static Adaptation getPerspectivesDataSet(Repository repo)
	{
		AdaptationHome perspectivesDataSpace = getPerspectivesDataSpace(repo);
		return getPerspectivesDataSet(perspectivesDataSpace);
	}

	public static Adaptation getPerspectivesDataSet(AdaptationHome perspectivesDataSpace)
	{
		return perspectivesDataSpace.findAdaptationOrNull(AdaptationName.forName(PERSPECTIVES_DATA_SET));
	}

	public static SchemaNode getPerspectivesAllowedProfilesGroup(Adaptation perspectivesDataSet)
	{
		return perspectivesDataSet.getSchemaNode()
			.getNode(PERSPECTIVES_ALLOWED_PROFILES_GROUP_PATH);
	}

	public static SchemaNode getPerspectivesMenuGroup(Adaptation perspectivesDataSet)
	{
		return perspectivesDataSet.getSchemaNode().getNode(PERSPECTIVES_MENU_GROUP_PATH);
	}

	public static SchemaNode getPerspectivesErgonomicsGroup(Adaptation perspectivesDataSet)
	{
		return perspectivesDataSet.getSchemaNode().getNode(PERSPECTIVES_ERGONOMICS_GROUP_PATH);
	}

	public static SchemaNode getPerspectivesDefaultOptionsGroup(Adaptation perspectivesDataSet)
	{
		return perspectivesDataSet.getSchemaNode().getNode(PERSPECTIVES_DEFAULT_OPTIONS_GROUP_PATH);
	}

	public static SchemaNode getPerspectivesColorsGroup(Adaptation perspectivesDataSet)
	{
		return perspectivesDataSet.getSchemaNode().getNode(PERSPECTIVES_COLORS_GROUP_PATH);
	}

	public static AdaptationHome getViewsDataSpace(Repository repo)
	{
		return repo.lookupHome(HomeKey.forBranchName(VIEWS_DATA_SPACE));
	}

	public static Adaptation getViewsDataSet(Repository repo)
	{
		AdaptationHome viewsDataSpace = getViewsDataSpace(repo);
		return getViewsDataSet(viewsDataSpace);
	}

	public static Adaptation getViewsDataSet(AdaptationHome viewsDataSpace)
	{
		return viewsDataSpace.findAdaptationOrNull(AdaptationName.forName(VIEWS_DATA_SET));
	}

	public static AdaptationTable getCustomViewsTable(Adaptation viewsDataSet)
	{
		return viewsDataSet.getTable(CUSTOM_VIEWS_TABLE_PATH);
	}

	public static AdaptationTable getDefaultViewsTable(Adaptation viewsDataSet)
	{
		return viewsDataSet.getTable(DEFAULT_VIEWS_TABLE_PATH);
	}

	public static AdaptationHome getTaskSchedulerDataSpace(Repository repo)
	{
		return repo.lookupHome(HomeKey.forBranchName(TASK_SCHEDULER_DATA_SPACE));
	}

	public static Adaptation getTaskSchedulerDataSet(Repository repo)
	{
		AdaptationHome taskSchedulerDataSpace = getTaskSchedulerDataSpace(repo);
		return getTaskSchedulerDataSet(taskSchedulerDataSpace);
	}

	public static Adaptation getTaskSchedulerDataSet(AdaptationHome taskSchedulerDataSpace)
	{
		return taskSchedulerDataSpace.findAdaptationOrNull(AdaptationName.forName(TASK_SCHEDULER_DATA_SET));
	}

	public static AdaptationTable getTasksTable(Adaptation viewsDataSet)
	{
		return viewsDataSet.getTable(TASKS_TABLE_PATH);
	}

	public static Path getTasksNamePath()
	{
		return TASKS_NAME_PATH;
	}

	public static String getTasksNameValueForRepositoryCleanup()
	{
		return TASKS_NAME_VALUE_FOR_REPOSITORY_CLEANUP;
	}

	public static AdaptationHome getAddonAdixPreferencesDataSpace(Repository repo)
	{
		return repo.lookupHome(HomeKey.forBranchName(ADDON_ADIX_PREFERENCES_DATA_SPACE));
	}

	public static Adaptation getAddonAdixPreferencesDataSet(Repository repo)
	{
		return getAddonAdixPreferencesDataSet(getAddonAdixPreferencesDataSpace(repo));
	}

	public static Adaptation getAddonAdixPreferencesDataSet(
		AdaptationHome addonAdixPreferencesDataSpace)
	{
		return addonAdixPreferencesDataSpace.findAdaptationOrNull(AdaptationName.forName(ADDON_ADIX_PREFERENCES_DATA_SET));
	}

	public static AdaptationHome getAddonAdixDataExchangeDataSpace(Repository repo)
	{
		return repo.lookupHome(HomeKey.forBranchName(ADDON_ADIX_DATA_EXCHANGE_DATA_SPACE));
	}

	public static Adaptation getAddonAdixDataExchangeDataSet(Repository repo)
	{
		return getAddonAdixDataExchangeDataSet(getAddonAdixDataExchangeDataSpace(repo));
	}

	public static Adaptation getAddonAdixDataExchangeDataSet(
		AdaptationHome addonAdixDataExchangeDataSpace)
	{
		return addonAdixDataExchangeDataSpace.findAdaptationOrNull(AdaptationName.forName(ADDON_ADIX_DATA_EXCHANGE_DATA_SET));
	}

	public static AdaptationHome getAddonAdixDataModelingDataSpace(Repository repo)
	{
		return repo.lookupHome(HomeKey.forBranchName(ADDON_ADIX_DATA_MODELING_DATA_SPACE));
	}

	public static Adaptation getAddonAdixDataModelingDataSet(Repository repo)
	{
		return getAddonAdixDataModelingDataSet(getAddonAdixDataModelingDataSpace(repo));
	}

	public static Adaptation getAddonAdixDataModelingDataSet(
		AdaptationHome addonAdixDataModelingDataSpace)
	{
		return addonAdixDataModelingDataSpace.findAdaptationOrNull(AdaptationName.forName(ADDON_ADIX_DATA_MODELING_DATA_SET));
	}

	public static AdaptationHome getAddonDaqaDataSpace(Repository repo)
	{
		return repo.lookupHome(HomeKey.forBranchName(ADDON_DAQA_DATA_SPACE));
	}

	public static Adaptation getAddonDaqaConfigurationDataSet(Repository repo)
	{
		return getAddonDaqaConfigurationDataSet(getAddonDaqaDataSpace(repo));
	}

	public static Adaptation getAddonDaqaConfigurationDataSet(AdaptationHome addonDaqaDataSpace)
	{
		return addonDaqaDataSpace.findAdaptationOrNull(AdaptationName.forName(ADDON_DAQA_CONFIGURATION_DATA_SET));
	}

	public static Adaptation getAddonDaqaReferenceDataSet(Repository repo)
	{
		return getAddonDaqaReferenceDataSet(getAddonDaqaDataSpace(repo));
	}

	public static Adaptation getAddonDaqaReferenceDataSet(AdaptationHome addonDaqaDataSpace)
	{
		return addonDaqaDataSpace.findAdaptationOrNull(AdaptationName.forName(ADDON_DAQA_REFERENCE_DATA_SET));
	}

	public static Adaptation getAddonDaqaStateMachineDataSet(Repository repo)
	{
		return getAddonDaqaStateMachineDataSet(getAddonDaqaDataSpace(repo));
	}

	public static Adaptation getAddonDaqaStateMachineDataSet(AdaptationHome addonDaqaDataSpace)
	{
		return addonDaqaDataSpace.findAdaptationOrNull(AdaptationName.forName(ADDON_DAQA_STATE_MACHINE_DATA_SET));
	}

	public static AdaptationHome getAddonDqidDataSpace(Repository repo)
	{
		return repo.lookupHome(HomeKey.forBranchName(ADDON_DQID_DATA_SPACE));
	}

	public static Adaptation getAddonDqidDataSet(Repository repo)
	{
		return getAddonDqidDataSet(getAddonDqidDataSpace(repo));
	}

	public static Adaptation getAddonDqidDataSet(AdaptationHome addonDqidDataSpace)
	{
		return addonDqidDataSpace.findAdaptationOrNull(AdaptationName.forName(ADDON_DQID_DATA_SET));
	}

	public static boolean isAdminDataSpace(AdaptationHome dataSpace)
	{
		String dataSpaceName = dataSpace.getKey().getName();
		return DIRECTORY_DATA_SPACE.equals(dataSpaceName) || DMA_DATA_SPACE.equals(dataSpaceName)
			|| GLOBAL_PERMISSIONS_DATA_SPACE.equals(dataSpaceName)
			|| TASK_SCHEDULER_DATA_SPACE.equals(dataSpaceName)
			|| PERSPECTIVES_DATA_SPACE.equals(dataSpaceName)
			|| VIEWS_DATA_SPACE.equals(dataSpaceName)
			|| WORKFLOW_MODELS_DATA_SPACE.equals(dataSpaceName)
			|| ADDON_ADIX_PREFERENCES_DATA_SPACE.equals(dataSpaceName)
			|| ADDON_ADIX_DATA_EXCHANGE_DATA_SPACE.equals(dataSpaceName)
			|| ADDON_ADIX_DATA_MODELING_DATA_SPACE.equals(dataSpaceName)
			|| ADDON_DAQA_DATA_SPACE.equals(dataSpaceName)
			|| ADDON_DQID_DATA_SPACE.equals(dataSpaceName);
	}

	/**
	 * Looks up a record in the directory's users table based on the supplied predicate.
	 * It will return the first record that matches the predicate.
	 * 
	 * @param repo the repository
	 * @param predicate the predicate
	 * @return the user record, or null if not found
	 */
	public static Adaptation lookupUserRecord(Repository repo, String predicate)
	{
		AdaptationTable usersTable = getDirectoryUsersTable(repo);
		RequestResult reqRes = usersTable.createRequestResult(predicate);
		Adaptation userRecord;
		try
		{
			userRecord = reqRes.nextAdaptation();
		}
		finally
		{
			reqRes.close();
		}
		return userRecord;
	}

	/**
	 * Looks up a user id in the directory from the email address
	 * 
	 * @param repo the repository
	 * @param email the email address
	 * @return the user id
	 */
	public static String lookupUserIdFromEmail(Repository repo, String email)
	{
		Adaptation userRecord = lookupUserRecord(repo, "osd:is-equal-case-insensitive("
			+ getDirectoryUsersEmailPath().format() + ",'" + email + "')");
		return userRecord == null ? null
			: userRecord.getString(AdminUtil.getDirectoryUsersLoginPath());
	}

	private AdminUtil()
	{
	}
}
