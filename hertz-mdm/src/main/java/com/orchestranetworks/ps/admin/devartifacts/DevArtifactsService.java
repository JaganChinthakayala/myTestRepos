/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.admin.devartifacts;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.onwbp.adaptation.*;
import com.orchestranetworks.instance.*;
import com.orchestranetworks.ps.accessrule.*;
import com.orchestranetworks.ps.adaptationfilter.*;
import com.orchestranetworks.ps.procedure.*;
import com.orchestranetworks.ps.util.*;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.service.*;
import com.orchestranetworks.ui.*;

/**
 * A servlet for processing of the dev artifacts
 */
@SuppressWarnings("serial")
public abstract class DevArtifactsService extends HttpServlet
{
	public static final String PARAM_PROPERTIES_FILE = "propFile";
	public static final String PARAM_ENVIRONMENT_COPY = "environmentCopy";

	public static final String DATA_PREFIX = "Data_";
	public static final String DATA_SET_ARCHIVE_PREFIX = "DSet_";
	public static final String PERMISSIONS_DATA_SET_PREFIX = "DSet_Perm_";
	public static final String PERMISSIONS_DATA_SPACE_PREFIX = "DSpc_Perm_";
	public static final String WORKFLOW_PREFIX = "WF_";
	public static final String PERSPECTIVE_PREFIX = "Persp_";
	public static final String DATA_SET_PROPERTIES_SUFFIX = ".properties";
	public static final String DATA_SET_PROPERTY_LABEL = "label";
	public static final String DATA_SET_PROPERTY_OWNER = "owner";
	public static final String DATA_SET_PROPERTY_PARENT_DATA_SET = "parentDataSet";
	public static final String DATA_SET_PROPERTY_CHILD_DATA_SETS = "childDataSets";
	public static final String ENV_COPY_ARCHIVE_PREFIX = "EnvCopy_";
	public static final String ADDON_ADIX_PREFIX = "Addon_Adix_";
	public static final String ADDON_DAQA_PREFIX = "Addon_Daqa_";
	public static final String ADDON_DQID_PREFIX = "Addon_Dqid_";

	public static final String SERVICE_COMPLETE_MSG = "Service complete.";

	protected static final String DATA_SET_PROPERTIES_FILE_CHILD_DATA_SETS_SEPARATOR = "\\|";

	private static final LoggingCategory LOG = LoggingCategory.getKernel();

	/**
	 * Execute the servlet
	 */
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		ServiceContext sContext = ServiceContext.getServiceContext(request);
		Repository repo;
		Session session = null;
		// This will only be null when called from outside of EBX
		if (sContext == null)
		{
			repo = Repository.getDefault();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
				request.getInputStream()));
			String line;
			try
			{
				line = reader.readLine();
			}
			finally
			{
				reader.close();
			}
			String login = null;
			if (line != null)
			{
				int sepInd = line.indexOf(' ');
				login = line.substring(0, sepInd);
				String password = line.substring(sepInd + 1);

				session = repo.createSessionFromLoginPassword(login, password);
			}
			if (session == null)
			{
				String msg = "Failed to login user " + login;
				LOG.error(msg);
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, msg);
				return;
			}
		}
		else
		{
			repo = sContext.getCurrentHome().getRepository();
			session = sContext.getSession();
		}

		// Make sure only admins can execute
		if (!session.isUserInRole(Role.ADMINISTRATOR))
		{
			String msg = "User doesn't have permission to execute service.";
			LOG.error(msg);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, msg);
			return;
		}

		try
		{
			DevArtifactsConfig config = getConfigFactory().createConfig(
				repo,
				session,
				request.getParameterMap());

			if (config.isEnvironmentCopy())
			{
				copyEnvironment(repo, session, config);
			}
			processArtifacts(repo, session, config);
		}
		catch (OperationException ex)
		{
			String msg = "Error processing artifacts.";
			LOG.error(msg, ex);
			response.sendError(
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
				msg + ": " + ex.getMessage());
			return;
		}

		// When called from outside EBX simply print this on the response
		if (sContext == null)
		{
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().println(SERVICE_COMPLETE_MSG);
			response.getWriter().flush();
		}
		else
		{
			UIComponentWriter writer = sContext.getUIComponentWriter();
			writer.addJS("alert('" + SERVICE_COMPLETE_MSG + "');");
			writer.addJS("window.location.href='" + sContext.getURLForEndingService() + "';");
		}
	}

	/**
	 * Get the factory to use to create the configuration
	 * 
	 * @return the factory
	 */
	public abstract DevArtifactsConfigFactory getConfigFactory();

	/**
	 * Process the given table. If no procedure context is specified, will execute in new procedure
	 * 
	 * @param session the session
	 * @param pContext the procedure context, or null to execute in new procedure
	 * @param table the table
	 * @param folder the folder to read from or write to
	 * @param filename the filename to read from or write to
	 * @param predicate the predicate to apply to the table, or <code>null</code>. (If specified, will take precedence over <code>filter</code>.)
	 * @param filter the programmatic filter to apply to the table, or <code>null</code>
	 * @param config the configuration
	 * @throws OperationException if there was an exception
	 */
	protected abstract void processTable(
		Session session,
		ProcedureContext pContext,
		AdaptationTable table,
		File folder,
		String filename,
		String predicate,
		AdaptationFilter filter,
		DevArtifactsConfig config) throws OperationException;

	/**
	 * Process the given group
	 * 
	 * @param session the session
	 * @param dataSet the data set
	 * @param groupNode the node for the group
	 * @param folder the folder to read from or write to
	 * @param filename the filename to read from or write to
	 * @param config the configuration
	 * @throws OperationException if there was an exception
	 */
	protected abstract void processGroup(
		Session session,
		Adaptation dataSet,
		SchemaNode groupNode,
		File folder,
		String filename,
		DevArtifactsConfig config) throws OperationException;

	/**
	 * Process the given data set as XML
	 * 
	 * @param session the session
	 * @param dataSet the data set
	 * @param folder the folder to read from or write to
	 * @param filePrefix the prefix of the file (prior to the data set name)
	 * @param config the configuration
	 * @throws OperationException if there was an exception
	 */
	protected abstract void processDataSetDataXML(
		Session session,
		Adaptation dataSet,
		File folder,
		String filePrefix,
		DevArtifactsConfig config) throws OperationException;

	/**
	 * Process the given data set's file for its properties (such as label and owner)
	 * 
	 * @param session the session
	 * @param dataSpace the data space
	 * @param dataSetName the data set name
	 * @param folder the folder to read from or write to
	 * @param filePrefix the prefix of the file (prior to the data set name)
	 * @param config the configuration
	 * @return the properties
	 * @throws IOException if there was an exception
	 * @throws OperationException if there was an exception
	 */
	protected abstract Properties processDataSetDataPropertiesFile(
		Session session,
		AdaptationHome dataSpace,
		AdaptationName dataSetName,
		File folder,
		String filePrefix,
		DevArtifactsConfig config) throws IOException, OperationException;

	/**
	 * Process the given data set's properties (such as label and owner)
	 * 
	 * @param session the session
	 * @param props the properties
	 * @param dataSet the data set
	 * @param folder the folder to read from or write to
	 * @param filePrefix the prefix of the file (prior to the data set name)
	 * @param config the configuration
	 * @throws IOException if there was an exception
	 * @throws OperationException if there was an exception
	 */
	protected abstract void processDataSetDataProperties(
		Session session,
		Properties props,
		Adaptation dataSet,
		File folder,
		String filePrefix,
		DevArtifactsConfig config) throws IOException, OperationException;

	/**
	 * Process the tasks
	 * 
	 * @param repo the repository
	 * @param session the session
	 * @param folder the folder to read from or write to
	 * @param config the configuration
	 * @throws OperationException if there was an exception
	 */
	protected abstract void processTasksData(
		Repository repo,
		Session session,
		File folder,
		DevArtifactsConfig config) throws OperationException;

	/**
	 * Do an environment copy
	 * 
	 * @param repo the repository
	 * @param session the session
	 * @param config the configuration
	 * @throws OperationException if there was an exception
	 */
	protected abstract void copyEnvironment(
		Repository repo,
		Session session,
		DevArtifactsConfig config) throws OperationException;

	/**
	 * Process all of the artifacts
	 * 
	 * @param repo the repository
	 * @param session the session
	 * @param config the configuration
	 * @throws OperationException if there was an exception
	 */
	protected void processArtifacts(Repository repo, Session session, DevArtifactsConfig config)
		throws OperationException
	{
		processAdminData(repo, session, config.getAdminFolder(), config);
		processDataSpacesPermissions(
			session,
			config.getDataSpacesForPermissions(),
			config.getPermissionsFolder(),
			config);
		processDataSetPermissions(
			repo,
			session,
			config.getDataSetsForPermissions(),
			config.getPermissionsFolder(),
			config);
		processDataTables(
			repo,
			session,
			config.getTablesForData(),
			config.getDataFolder(),
			DATA_PREFIX,
			config);
		processWorkflowModels(
			repo,
			session,
			config.getWorkflowModels(),
			config.getWorkflowsFolder(),
			WORKFLOW_PREFIX,
			config);

		postProcess(repo, session, config);
	}

	/**
	 * Process the administration data
	 * 
	 * @param repo the repository
	 * @param session the session
	 * @param folder the folder to read from or write to
	 * @throws OperationException if there was an exception
	 */
	protected void processAdminData(
		Repository repo,
		Session session,
		File folder,
		DevArtifactsConfig config) throws OperationException
	{
		processDirectoryData(repo, session, folder, config);
		processGlobalPermissionsData(repo, session, folder, config);
		processViewsData(repo, session, folder, config);
		processTasksData(repo, session, folder, config);
		processPerspectivesData(repo, session, folder, config);
		processAddonsData(repo, session, folder, config);
	}

	/**
	 * Process the directory data
	 * 
	 * @param repo the repository
	 * @param session the session
	 * @param folder the folder to read from or write to
	 * @param config the configuration
	 * @throws OperationException if there was an exception
	 */
	protected void processDirectoryData(
		Repository repo,
		Session session,
		File folder,
		DevArtifactsConfig config) throws OperationException
	{
		if (config.isProcessDirectoryData())
		{
			Adaptation directoryDataSet = AdminUtil.getDirectoryDataSet(repo);
			AdaptationTable usersRolesTable = AdminUtil.getDirectoryUsersRolesTable(directoryDataSet);
			AdaptationTable rolesInclusionsTable = AdminUtil.getDirectoryRolesInclusionsTable(directoryDataSet);
			AdaptationTable rolesTable = AdminUtil.getDirectoryRolesTable(directoryDataSet);
			AdaptationTable salutationsTable = AdminUtil.getDirectorySalutationsTable(directoryDataSet);
			SchemaNode mailingListGroup = AdminUtil.getDirectoryMailingListGroup(directoryDataSet);
			SchemaNode policyGroup = AdminUtil.getDirectoryPolicyGroup(directoryDataSet);

			processUsersRolesTable(session, usersRolesTable, folder, DATA_PREFIX, config);
			processTable(session, null, rolesInclusionsTable, folder, DATA_PREFIX
				+ getTableFilename(rolesInclusionsTable), null, null, config);
			processTable(session, null, rolesTable, folder, DATA_PREFIX
				+ getTableFilename(rolesTable), null, null, config);
			processTable(session, null, salutationsTable, folder, DATA_PREFIX
				+ getTableFilename(salutationsTable), null, null, config);
			processGroup(session, directoryDataSet, mailingListGroup, folder, DATA_PREFIX
				+ directoryDataSet.getAdaptationName().getStringName() + "_"
				+ getGroupFilename(mailingListGroup), config);
			processGroup(session, directoryDataSet, policyGroup, folder, DATA_PREFIX
				+ directoryDataSet.getAdaptationName().getStringName() + "_"
				+ getGroupFilename(policyGroup), config);
		}
	}

	/**
	 * Process the users roles table, and the users associated with them
	 * 
	 * @param session the session
	 * @param usersRolesTable the table
	 * @param folder the folder to read from or write to
	 * @param filePrefix the prefix of the file (prior to the table name)
	 * @param config the configuration
	 * @throws OperationException if there was an exception
	 */
	protected abstract void processUsersRolesTable(
		Session session,
		AdaptationTable usersRolesTable,
		File folder,
		String filePrefix,
		DevArtifactsConfig config) throws OperationException;

	/**
	 * Get the filter used to decide which users to process. By default, returns a {@link FieldValueInCollectionAdaptationFilter}
	 * which processes all users in the given set, but can be overridden for different behavior.
	 * 
	 * @param userPath the path to the user field
	 * @param users the users
	 * @return the filter
	 */
	protected AdaptationFilter getUsersFilter(Path userPath, Set<String> users)
	{
		return new FieldValueInCollectionAdaptationFilter(userPath, users);
	}

	/**
	 * Process the global permissions data
	 * 
	 * @param repo the repository
	 * @param session the session
	 * @param folder the folder to read from or write to
	 * @param config the configuration
	 * @throws OperationException if there was an exception
	 */
	protected void processGlobalPermissionsData(
		Repository repo,
		Session session,
		File folder,
		DevArtifactsConfig config) throws OperationException
	{
		if (config.isProcessGlobalPermissionsData())
		{
			Adaptation globalPermissionsDataSet = AdminUtil.getGlobalPermissionsDataSet(repo);

			AdaptationTable globalPermissionsTable = AdminUtil.getGlobalPermissionsTable(globalPermissionsDataSet);
			processTable(session, null, globalPermissionsTable, folder, DATA_PREFIX
				+ getTableFilename(globalPermissionsTable), null, null, config);
		}
	}

	/**
	 * Process the views data
	 * 
	 * @param repo the repository
	 * @param session the session
	 * @param folder the folder to read from or write to
	 * @param config the configuration
	 * @throws OperationException if there was an exception
	 */
	protected void processViewsData(
		Repository repo,
		Session session,
		File folder,
		DevArtifactsConfig config) throws OperationException
	{
		if (config.isProcessViewsData())
		{
			Adaptation viewsDataSet = AdminUtil.getViewsDataSet(repo);
			AdaptationTable customViewsTable = AdminUtil.getCustomViewsTable(viewsDataSet);
			String customTablePredicate = null;
			if (config.isProcessPublishedViewsDataOnly())
			{
				customTablePredicate = "osd:is-not-null(./publicationName)";
			}
			processTable(session, null, customViewsTable, folder, DATA_PREFIX
				+ getTableFilename(customViewsTable), customTablePredicate, null, config);

			// If we only exported published views, then we only want to export
			// the default views associated to those tables.
			// So get which tables we're exporting, then filter the default views
			HashSet<String> tablePaths = new HashSet<String>();
			if (customTablePredicate != null)
			{
				RequestResult reqRes = customViewsTable.createRequestResult(customTablePredicate);
				try
				{
					for (Adaptation record; (record = reqRes.nextAdaptation()) != null;)
					{
						tablePaths.add(record.getString(Path.parse("./tablePath")));
					}
				}
				finally
				{
					reqRes.close();
				}
			}
			String defaultTablePredicate = null;
			if (customTablePredicate != null)
			{
				if (tablePaths.isEmpty())
				{
					// This should never be null which is the point.
					// It will run an export with no records.
					defaultTablePredicate = "osd:is-null(./Id)";
				}
				else
				{
					StringBuilder bldr = new StringBuilder();
					Iterator<String> iter = tablePaths.iterator();
					while (iter.hasNext())
					{
						String tablePath = iter.next();
						bldr.append("./tablePath='" + tablePath + "'");
						if (iter.hasNext())
						{
							bldr.append(" or ");
						}
					}
					defaultTablePredicate = bldr.toString();
				}
			}

			AdaptationTable defaultViewsTable = AdminUtil.getDefaultViewsTable(viewsDataSet);
			processTable(session, null, defaultViewsTable, folder, DATA_PREFIX
				+ getTableFilename(defaultViewsTable), defaultTablePredicate, null, config);
		}
	}

	/**
	 * Process the perspectives data
	 * 
	 * @param repo the repository
	 * @param session the session
	 * @param folder the folder to read from or write to
	 * @param config the configuration
	 * @throws OperationException if there was an exception
	 */
	protected void processPerspectivesData(
		Repository repo,
		Session session,
		File folder,
		DevArtifactsConfig config) throws OperationException
	{
		if (config.isProcessPerspectivesData())
		{
			Adaptation perspectivesDataSet = AdminUtil.getPerspectivesDataSet(repo);

			doProcessPerspectivesDataSet(
				session,
				folder,
				perspectivesDataSet.getHome(),
				perspectivesDataSet.getAdaptationName().getStringName(),
				config);
		}
	}

	protected void processPerspectivesDataGroups(
		Session session,
		File folder,
		Adaptation dataSet,
		DevArtifactsConfig config) throws OperationException
	{
		SchemaNode allowedProfilesGroup = AdminUtil.getPerspectivesAllowedProfilesGroup(dataSet);
		processGroup(session, dataSet, allowedProfilesGroup, folder, PERSPECTIVE_PREFIX
			+ dataSet.getAdaptationName().getStringName() + "_"
			+ getGroupFilename(allowedProfilesGroup), config);

		SchemaNode menuGroup = AdminUtil.getPerspectivesMenuGroup(dataSet);
		processGroup(
			session,
			dataSet,
			menuGroup,
			folder,
			PERSPECTIVE_PREFIX + dataSet.getAdaptationName().getStringName() + "_"
				+ getGroupFilename(menuGroup),
			config);

		SchemaNode ergonomicsGroup = AdminUtil.getPerspectivesErgonomicsGroup(dataSet);
		processGroup(
			session,
			dataSet,
			ergonomicsGroup,
			folder,
			PERSPECTIVE_PREFIX + dataSet.getAdaptationName().getStringName() + "_"
				+ getGroupFilename(ergonomicsGroup),
			config);

		SchemaNode defaultOptionsGroup = AdminUtil.getPerspectivesDefaultOptionsGroup(dataSet);
		processGroup(session, dataSet, defaultOptionsGroup, folder, PERSPECTIVE_PREFIX
			+ dataSet.getAdaptationName().getStringName() + "_"
			+ getGroupFilename(defaultOptionsGroup), config);

		SchemaNode colorsGroup = AdminUtil.getPerspectivesColorsGroup(dataSet);
		processGroup(
			session,
			dataSet,
			colorsGroup,
			folder,
			PERSPECTIVE_PREFIX + dataSet.getAdaptationName().getStringName() + "_"
				+ getGroupFilename(colorsGroup),
			config);
	}

	protected abstract void doProcessPerspectivesDataSet(
		Session session,
		File folder,
		AdaptationHome dataSpace,
		String dataSetName,
		DevArtifactsConfig config) throws OperationException;

	/**
	 * Process the addons data
	 * 
	 * @param repo the repository
	 * @param session the session
	 * @param folder the folder to read from or write to
	 * @param config the configuration
	 * @throws OperationException if there was an exception
	 */
	protected void processAddonsData(
		Repository repo,
		Session session,
		File folder,
		DevArtifactsConfig config) throws OperationException
	{
		if (config.isProcessAddonAdixData())
		{
			processAddonAdixData(repo, session, folder, config);
		}
		if (config.isProcessAddonDaqaData())
		{
			processAddonDaqaData(repo, session, folder, config);
		}
		if (config.isProcessAddonDqidData())
		{
			processAddonDqidData(repo, session, folder, config);
		}
	}

	/**
	 * Process the adix (Data Exchange) addon data
	 * 
	 * @param repo the repository
	 * @param session the session
	 * @param folder the folder to read from or write to
	 * @param config the configuration
	 * @throws OperationException if there was an exception
	 */
	protected void processAddonAdixData(
		Repository repo,
		Session session,
		File folder,
		DevArtifactsConfig config) throws OperationException
	{
		AdaptationHome adixPreferencesDataSpace = AdminUtil.getAddonAdixPreferencesDataSpace(repo);
		processDataSetDataXML(
			session,
			AdminUtil.getAddonAdixPreferencesDataSet(adixPreferencesDataSpace),
			folder,
			ADDON_ADIX_PREFIX,
			config);

		AdaptationHome adixDataExchangeDataSpace = AdminUtil.getAddonAdixDataExchangeDataSpace(repo);
		processDataSetDataXML(
			session,
			AdminUtil.getAddonAdixDataExchangeDataSet(adixDataExchangeDataSpace),
			folder,
			ADDON_ADIX_PREFIX,
			config);

		AdaptationHome adixDataModelingDataSpace = AdminUtil.getAddonAdixDataModelingDataSpace(repo);
		processDataSetDataXML(
			session,
			AdminUtil.getAddonAdixDataModelingDataSet(adixDataModelingDataSpace),
			folder,
			ADDON_ADIX_PREFIX,
			config);
	}

	/**
	 * Process the daqa (Match & Cleanse) addon data
	 * 
	 * @param repo the repository
	 * @param session the session
	 * @param folder the folder to read from or write to
	 * @param config the configuration
	 * @throws OperationException if there was an exception
	 */
	protected void processAddonDaqaData(
		Repository repo,
		Session session,
		File folder,
		DevArtifactsConfig config) throws OperationException
	{
		AdaptationHome daqaDataSpace = AdminUtil.getAddonDaqaDataSpace(repo);

		processDataSetDataXML(
			session,
			AdminUtil.getAddonDaqaConfigurationDataSet(daqaDataSpace),
			folder,
			ADDON_DAQA_PREFIX,
			config);
		processDataSetDataXML(
			session,
			AdminUtil.getAddonDaqaReferenceDataSet(daqaDataSpace),
			folder,
			ADDON_DAQA_PREFIX,
			config);
		processDataSetDataXML(
			session,
			AdminUtil.getAddonDaqaStateMachineDataSet(daqaDataSpace),
			folder,
			ADDON_DAQA_PREFIX,
			config);
	}

	/**
	 * Process the dqid (Insight) addon data
	 * 
	 * @param repo the repository
	 * @param session the session
	 * @param folder the folder to read from or write to
	 * @param config the configuration
	 * @throws OperationException if there was an exception
	 */
	protected void processAddonDqidData(
		Repository repo,
		Session session,
		File folder,
		DevArtifactsConfig config) throws OperationException
	{
		AdaptationHome dqidDataSpace = AdminUtil.getAddonDqidDataSpace(repo);
		processDataSetDataXML(
			session,
			AdminUtil.getAddonDqidDataSet(dqidDataSpace),
			folder,
			ADDON_DQID_PREFIX,
			config);
	}

	/**
	 * Process the data tables
	 * 
	 * @param session the session
	 * @param tables the tables
	 * @param folder the folder to read from or write to
	 * @param filePrefix the prefix of the file (prior to the table names)
	 * @param config the configuration
	 * @throws OperationException if there was an exception
	 */
	protected void processDataTables(
		Repository repo,
		Session session,
		List<AdaptationTable> tables,
		final File folder,
		final String filePrefix,
		final DevArtifactsConfig config) throws OperationException
	{
		if (!tables.isEmpty())
		{
			HashMap<AdaptationHome, Set<AdaptationTable>> dataSpaceTableMap = new HashMap<AdaptationHome, Set<AdaptationTable>>();
			for (AdaptationTable table : tables)
			{
				AdaptationHome dataSpace = table.getContainerAdaptation().getHome();
				Set<AdaptationTable> tableSet = dataSpaceTableMap.get(dataSpace);
				if (tableSet == null)
				{
					tableSet = new HashSet<AdaptationTable>();
					dataSpaceTableMap.put(dataSpace, tableSet);
				}
				tableSet.add(table);
			}
			for (Map.Entry<AdaptationHome, Set<AdaptationTable>> entry : dataSpaceTableMap.entrySet())
			{
				AdaptationHome dataSpace = entry.getKey();
				final Set<AdaptationTable> dataSpaceTables = entry.getValue();
				Procedure proc = new Procedure()
				{
					@Override
					public void execute(ProcedureContext pContext) throws Exception
					{
						for (AdaptationTable table : dataSpaceTables)
						{
							processDataTable(pContext, table, folder, filePrefix
								+ getTableFilename(table), config);
						}
					}
				};
				ProcedureExecutor.executeProcedure(proc, session, dataSpace);
			}
		}
	}

	/**
	 * Process a data table
	 * 
	 * @param pContext the procedure context
	 * @param table the table
	 * @param folder the folder to read from or write to
	 * @param filename the filename to read from or write to
	 * @param config the configuration
	 * @throws OperationException if there was an exception
	 */
	protected void processDataTable(
		ProcedureContext pContext,
		AdaptationTable table,
		File folder,
		String filename,
		DevArtifactsConfig config) throws OperationException
	{
		processTable(
			null,
			pContext,
			table,
			folder,
			filename,
			null,
			getFilterForDataTable(table),
			config);
	}

	/**
	 * Get the filter to use when processing the specified data table.
	 * By default returns <code>null</code>, but can be overridden to do specific filtering.
	 * 
	 * @param table the table
	 * @return the filter
	 */
	protected AdaptationFilter getFilterForDataTable(AdaptationTable table)
	{
		return null;
	}

	/**
	 * Process the permissions for a list of data sets
	 * 
	 * @param repo the repository
	 * @param session the session
	 * @param dataSets the data sets
	 * @param folder the folder to read from or write to
	 * @param config the configuration
	 * @throws OperationException if there was an exception
	 */
	protected void processDataSetPermissions(
		Repository repo,
		Session session,
		List<Adaptation> dataSets,
		File folder,
		DevArtifactsConfig config) throws OperationException
	{
		for (Adaptation dataSet : dataSets)
		{
			processDataSetPermissions(session, dataSet, folder, config);
		}
	}

	/**
	 * Process the permissions for a data set
	 * 
	 * @param session the session
	 * @param dataSet the data set
	 * @param folder the folder to read from or write to
	 * @param config the configuration
	 * @throws OperationException if there was an exception
	 */
	protected void processDataSetPermissions(
		Session session,
		Adaptation dataSet,
		File folder,
		DevArtifactsConfig config) throws OperationException
	{
		AdaptationTable permissionsTable = AdminPermissionsUtil.getDataSetPermissionsTable(dataSet);
		processTable(session, null, permissionsTable, folder, PERMISSIONS_DATA_SET_PREFIX
			+ dataSet.getAdaptationName().getStringName(), null, null, config);

		try
		{
			Properties props = processDataSetDataPropertiesFile(
				session,
				dataSet.getHome(),
				dataSet.getAdaptationName(),
				folder,
				PERMISSIONS_DATA_SET_PREFIX,
				config);
			processDataSetDataProperties(
				session,
				props,
				dataSet,
				folder,
				PERMISSIONS_DATA_SET_PREFIX,
				config);
		}
		catch (IOException ex)
		{
			throw OperationException.createError(ex);
		}
	}

	/**
	 * Process the permissions for the data spaces
	 * 
	 * @param session the session
	 * @param dataSpaces the data spaces
	 * @param folder the folder to read from or write to
	 * @param config the configuration
	 * @throws OperationException if there was an exception
	 */
	protected void processDataSpacesPermissions(
		Session session,
		final List<AdaptationHome> dataSpaces,
		final File folder,
		final DevArtifactsConfig config) throws OperationException
	{
		if (dataSpaces.isEmpty())
		{
			return;
		}
		Repository repo = dataSpaces.get(0).getRepository();
		// This utilizes a non-public API method, but there's no way to do it currently otherwise
		final AdaptationTable permissionsTable = AdminPermissionsUtil.getDataSpacesPermissionsTable(repo);

		Procedure proc = new Procedure()
		{
			@Override
			public void execute(ProcedureContext pContext) throws Exception
			{
				for (AdaptationHome dataSpace : dataSpaces)
				{
					processDataSpacePermissions(
						pContext,
						dataSpace,
						folder,
						permissionsTable,
						config);
				}
			}
		};
		ProcedureExecutor.executeProcedure(proc, session, permissionsTable.getContainerAdaptation()
			.getHome());
	}

	/**
	 * Process the permissions for a single data space
	 * 
	 * @param session the session
	 * @param dataSpace the data spaces
	 * @param folder the folder to read from or write to
	 * @param permissionsTable the permissions table
	 * @param config the configuration
	 * @throws OperationException if there was an exception
	 */
	protected void processDataSpacePermissions(
		ProcedureContext pContext,
		AdaptationHome dataSpace,
		File folder,
		AdaptationTable permissionsTable,
		DevArtifactsConfig config) throws OperationException
	{
		// There is one permissions table so you need to use a predicate to filter to the data space you want
		processTable(
			null,
			pContext,
			permissionsTable,
			folder,
			PERMISSIONS_DATA_SPACE_PREFIX + dataSpace.getKey().getName(),
			"homeKey='" + dataSpace.getKey().format() + "'",
			null,
			config);
	}

	/**
	 * Process the workflow models
	 * 
	 * @param repo the repository
	 * @param session the session
	 * @param wfModels the names of the workflow models
	 * @param folder the folder to read from or write to
	 * @param filePrefix the prefix of the file (prior to the workflow model names)
	 * @param config the configuration
	 * @throws OperationException if there was an exception
	 */
	protected void processWorkflowModels(
		Repository repo,
		Session session,
		List<String> wfModels,
		File folder,
		String filePrefix,
		DevArtifactsConfig config) throws OperationException
	{
		AdaptationHome wfDataSpace = AdminUtil.getWorkflowModelsDataSpace(repo);

		for (String wfModel : wfModels)
		{
			processWorkflowModel(session, wfModel, folder, filePrefix, config, wfDataSpace);
		}
	}

	/**
	 * Process the given workflow model
	 * 
	 * @param session the session
	 * @param wfModels the names of the workflow models
	 * @param folder the folder to read from or write to
	 * @param filePrefix the prefix of the file (prior to the workflow model names)
	 * @param config the configuration
	 * @throws OperationException if there was an exception
	 */
	protected abstract void processWorkflowModel(
		Session session,
		String wfModel,
		File folder,
		String filePrefix,
		DevArtifactsConfig config,
		AdaptationHome wfDataSpace) throws OperationException;

	/**
	 * Do the copy environment for the data tables
	 * 
	 * @param session the session
	 * @param tablesForData the tables to process
	 * @param config the configuration
	 * @throws OperationException if there was an exception
	 */
	protected void doEnvCopyDataTables(
		Repository repo,
		Session session,
		List<AdaptationTable> tablesForData,
		DevArtifactsConfig config) throws OperationException
	{
		// key = data set name, value = tables to process from data set
		Map<Adaptation, List<AdaptationTable>> dataSetTableMap = createMapOfDataSetTables(tablesForData);

		for (Map.Entry<Adaptation, List<AdaptationTable>> entry : dataSetTableMap.entrySet())
		{
			Adaptation dataSet = entry.getKey();
			List<AdaptationTable> tableList = entry.getValue();
			List<AdaptationTable> allTables = AdaptationUtil.getAllTables(dataSet);
			// Only do xml for tables when we're processing data set permissions in the child data spaces
			// (because in those cases the archive will only contain permissions)
			// or when the whole data set isn't being processed (because those tables will be handled by the archive).
			if (config.isProcessDataSetPermissionsInChildDataSpaces()
				|| tableList.size() != allTables.size())
			{
				processDataTables(
					repo,
					session,
					tableList,
					config.getCopyEnvironmentFolder(),
					DATA_PREFIX,
					config);
			}
		}
	}

	/**
	 * Create a map of a data set and its tables, from the given tables (which can belong to different data sets).
	 * 
	 * @param tables the tables
	 * @return a map of key = data set and value = list of tables for that data set
	 */
	protected static Map<Adaptation, List<AdaptationTable>> createMapOfDataSetTables(
		List<AdaptationTable> tables)
	{
		HashMap<Adaptation, List<AdaptationTable>> dataSetMap = new HashMap<Adaptation, List<AdaptationTable>>();
		for (AdaptationTable table : tables)
		{
			Adaptation dataSet = table.getContainerAdaptation();
			List<AdaptationTable> tableList;
			if (dataSetMap.containsKey(dataSet))
			{
				tableList = dataSetMap.get(dataSet);
			}
			else
			{
				tableList = new ArrayList<AdaptationTable>();
			}
			tableList.add(table);
			dataSetMap.put(dataSet, tableList);
		}
		return dataSetMap;
	}

	/**
	 * Get the filename for the given table
	 * 
	 * @param table the table
	 * @return the filename
	 */
	protected static String getTableFilename(AdaptationTable table)
	{
		return table.getTablePath().getLastStep().format();
	}

	/**
	 * Get the filename for the given group
	 * 
	 * @param groupNode the node for the group
	 * @return the filename
	 */
	protected static String getGroupFilename(SchemaNode groupNode)
	{
		return groupNode.getPathInSchema().getLastStep().format();
	}

	/**
	 * Performs any post-processing required by the service.
	 * By default, clears the cache on {@link DefaultPermissionsUserManager}, but can be overridden to perform other actions.
	 * 
	 * @param repo the repository
	 * @param session the session
	 * @param config the configuration
	 * @throws OperationException if there was an exception
	 */
	protected void postProcess(Repository repo, Session session, DevArtifactsConfig config)
		throws OperationException
	{
		DefaultPermissionsUserManager.getInstance().clearCache();
	}
}
