/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.admin.devartifacts;

import java.io.*;
import java.util.*;

import com.onwbp.adaptation.*;
import com.onwbp.base.text.*;
import com.orchestranetworks.instance.*;
import com.orchestranetworks.ps.procedure.*;
import com.orchestranetworks.ps.util.*;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.service.*;
import com.orchestranetworks.service.extensions.*;

/**
 * A servlet for importing of the dev artifacts
 */
@SuppressWarnings("serial")
public class ImportDevArtifactsService extends DevArtifactsService
{
	public static final String PARAM_REPLACE_MODE = "replaceMode";
	public static final String PARAM_SKIP_NONEXISTING_FILES = "skipNonexistingFiles";
	public static final String PARAM_SELECT_ALL_WORKFLOWS = "selectAllWorkflows";
	public static final String PARAM_WORKFLOW_PREFIX = "wf_";

	@Override
	public DevArtifactsConfigFactory getConfigFactory()
	{
		return new ImportDevArtifactsConfigFactory();
	}

	private List<Adaptation> getAllDataSetsToProcess(
		Repository repo,
		Session session,
		List<Adaptation> dataSets,
		ImportDevArtifactsConfig importConfig) throws OperationException
	{
		List<Adaptation> newDataSets = createDataSetsFromDataModels(repo, session, importConfig);
		List<Adaptation> allDataSets;
		if (newDataSets.isEmpty())
		{
			allDataSets = dataSets;
		}
		else
		{
			allDataSets = new ArrayList<Adaptation>(dataSets);
			allDataSets.addAll(newDataSets);
		}
		return allDataSets;
	}

	private List<AdaptationTable> getAllTablesToProcess(
		Repository repo,
		List<AdaptationTable> tables,
		ImportDevArtifactsConfig importConfig) throws OperationException
	{
		Map<DataSetCreationKey, List<String>> createdDataSetTableSpecs = importConfig.getCreatedDataSetTableSpecs();
		List<AdaptationTable> allTables;
		if (createdDataSetTableSpecs.isEmpty())
		{
			allTables = tables;
		}
		else
		{
			allTables = new ArrayList<AdaptationTable>(tables);
			for (DataSetCreationKey key : createdDataSetTableSpecs.keySet())
			{
				List<String> tableSpecs = createdDataSetTableSpecs.get(key);
				for (String tableSpec : tableSpecs)
				{
					HomeKey dataSpaceKey = key.getDataSpaceKey();
					AdaptationHome dataSpace = repo.lookupHome(dataSpaceKey);
					if (dataSpace == null)
					{
						throw OperationException.createError("Can't find data space "
							+ dataSpaceKey.getName() + " for table " + tableSpec + ".");
					}
					AdaptationName dataSetName = key.getDataSetName();
					Adaptation dataSet = dataSpace.findAdaptationOrNull(dataSetName);
					if (dataSet == null)
					{
						throw OperationException.createError("Can't find data set "
							+ dataSetName.getStringName() + " in data space "
							+ dataSpaceKey.getName() + " for table " + tableSpec + ".");
					}
					// Replace wildcard with all tables for that data set
					if (tableSpec.endsWith(DevArtifactsPropertyConstants.WILDCARD))
					{
						allTables.addAll(AdaptationUtil.getAllTables(dataSet));
					}
					else
					{
						AdaptationTable table = dataSet.getTable(Path.parse(tableSpec));
						if (table == null)
						{
							throw OperationException.createError("Can't find table " + tableSpec
								+ ".");
						}
						else
						{
							allTables.add(table);
						}
					}
				}
			}
		}
		return allTables;
	}

	@Override
	protected void processDataTables(
		Repository repo,
		Session session,
		List<AdaptationTable> tables,
		File folder,
		String filePrefix,
		DevArtifactsConfig config) throws OperationException
	{
		ImportDevArtifactsConfig importConfig = (ImportDevArtifactsConfig) config;
		List<AdaptationTable> allTables = getAllTablesToProcess(repo, tables, importConfig);
		super.processDataTables(repo, session, allTables, folder, filePrefix, importConfig);
	}

	@Override
	protected void processTable(
		Session session,
		ProcedureContext pContext,
		AdaptationTable table,
		File folder,
		String filename,
		String predicate,
		AdaptationFilter filter,
		DevArtifactsConfig config) throws OperationException
	{
		ImportSpec spec = createImportSpec(folder, filename, (ImportDevArtifactsConfig) config);
		if (spec == null)
		{
			return;
		}

		spec.setTargetAdaptationTable(table);

		if (pContext == null)
		{
			ImportXMLProcedure proc = new ImportXMLProcedure(spec);
			ProcedureExecutor.executeProcedure(proc, session, table.getContainerAdaptation()
				.getHome());
		}
		else
		{
			doImport(pContext, spec);
		}
	}

	private static void doImport(ProcedureContext pContext, ImportSpec spec)
		throws OperationException
	{
		boolean allPrivileges = pContext.isAllPrivileges();
		pContext.setAllPrivileges(true);
		try
		{
			pContext.doImport(spec);
		}
		finally
		{
			pContext.setAllPrivileges(allPrivileges);
		}
	}

	@Override
	protected void processGroup(
		Session session,
		Adaptation dataSet,
		SchemaNode groupNode,
		File folder,
		String filename,
		DevArtifactsConfig config) throws OperationException
	{
		ImportSpec spec = createImportSpec(folder, filename, (ImportDevArtifactsConfig) config);
		if (spec == null)
		{
			return;
		}

		spec.setTargetAdaptation(dataSet);

		ImportXMLProcedure proc = new ImportXMLProcedure(spec);
		ProcedureExecutor.executeProcedure(proc, session, dataSet.getHome());
	}

	private static ImportSpec createImportSpec(
		File folder,
		String filename,
		ImportDevArtifactsConfig importConfig) throws OperationException
	{
		File srcFile = new File(folder, filename + ".xml");
		// Only throw an error if they don't want to skip non-existent files
		if (!srcFile.exists())
		{
			if (importConfig.isSkipNonExistingFiles())
			{
				return null;
			}
			throw OperationException.createError("Import file doesn't exist: "
				+ srcFile.getAbsolutePath());

		}

		ImportSpec spec = new ImportSpec();
		spec.setImportMode(importConfig.getImportMode());
		spec.setSourceFile(srcFile);
		return spec;
	}

	@Override
	protected void processDataSetDataXML(
		Session session,
		Adaptation dataSet,
		File folder,
		String filePrefix,
		DevArtifactsConfig config) throws OperationException
	{
		ImportSpec spec = createImportSpec(folder, filePrefix
			+ dataSet.getAdaptationName().getStringName(), (ImportDevArtifactsConfig) config);
		if (spec == null)
		{
			return;
		}

		spec.setTargetAdaptation(dataSet);

		ImportXMLProcedure proc = new ImportXMLProcedure(spec);
		ProcedureExecutor.executeProcedure(proc, session, dataSet.getHome());
	}

	@Override
	protected Properties processDataSetDataPropertiesFile(
		Session session,
		AdaptationHome dataSpace,
		AdaptationName dataSetName,
		File folder,
		String filePrefix,
		DevArtifactsConfig config) throws IOException, OperationException
	{
		Properties props = new Properties();
		File propertiesFile = new File(folder, filePrefix + dataSetName.getStringName()
			+ DevArtifactsService.DATA_SET_PROPERTIES_SUFFIX);
		if (propertiesFile.exists())
		{
			BufferedReader reader = new BufferedReader(new FileReader(propertiesFile));

			try
			{
				props.load(reader);
			}
			finally
			{
				reader.close();
			}
		}
		return props;
	}

	@Override
	protected void processDataSetDataProperties(
		Session session,
		Properties props,
		Adaptation dataSet,
		File folder,
		String filePrefix,
		DevArtifactsConfig config) throws IOException, OperationException
	{
		String label = getPropertyValueOrNull(props, DevArtifactsService.DATA_SET_PROPERTY_LABEL);
		String owner = getPropertyValueOrNull(props, DevArtifactsService.DATA_SET_PROPERTY_OWNER);

		if ((label != null && !label.equals(dataSet.getLabel(session.getLocale())) || (owner != null && !owner.equals(dataSet.getOwner()
			.format()))))
		{
			SetDataSetPropertiesProcedure setDataSetPropsProc = new SetDataSetPropertiesProcedure(
				dataSet,
				label,
				owner);
			ProcedureExecutor.executeProcedure(setDataSetPropsProc, session, dataSet);
		}
	}

	private static String getPropertyValueOrNull(Properties props, String key)
	{
		String value = props.getProperty(key);
		if (value != null)
		{
			value = value.trim();
			if ("".equals(value))
			{
				value = null;
			}
		}
		return value;
	}

	@Override
	protected void processDataSetPermissions(
		Session session,
		Adaptation dataSet,
		File folder,
		DevArtifactsConfig config) throws OperationException
	{
		super.processDataSetPermissions(session, dataSet, folder, config);
		if (config.isProcessDataSetPermissionsInChildDataSpaces())
		{
			AdaptationHome dataSpace = dataSet.getHome();
			// Will never handle child of administrative data spaces, so can skip checking for these
			if (!AdminUtil.isAdminDataSpace(dataSpace))
			{
				@SuppressWarnings("unchecked")
				List<AdaptationHome> snapshots = dataSpace.getVersionChildren();
				for (AdaptationHome snapshot : snapshots)
				{
					@SuppressWarnings("unchecked")
					List<AdaptationHome> childDataSpaces = snapshot.getBranchChildren();
					for (AdaptationHome childDataSpace : childDataSpaces)
					{
						if (childDataSpace.isOpen())
						{
							Adaptation childDataSet = childDataSpace.findAdaptationOrNull(dataSet.getAdaptationName());
							if (childDataSet != null)
							{
								processDataSetPermissions(session, childDataSet, folder, config);
							}
						}
					}
				}
			}
		}
	}

	@Override
	protected void processTasksData(
		Repository repo,
		Session session,
		File folder,
		DevArtifactsConfig config) throws OperationException
	{
		if (config.isProcessTasksData())
		{
			Adaptation taskSchedulerDataSet = AdminUtil.getTaskSchedulerDataSet(repo);

			// Can't use replace mode since there's the built-in purge task
			AdaptationTable tasksTable = AdminUtil.getTasksTable(taskSchedulerDataSet);
			ImportDevArtifactsConfig importConfig = (ImportDevArtifactsConfig) config;
			ImportSpecMode importMode = importConfig.getImportMode();
			if (ImportSpecMode.REPLACE.equals(importMode))
			{
				importConfig.setImportMode(ImportSpecMode.UPDATE_OR_INSERT);
			}
			processTable(session, null, tasksTable, folder, DATA_PREFIX
				+ getTableFilename(tasksTable), null, null, config);
			if (ImportSpecMode.REPLACE.equals(importMode))
			{
				importConfig.setImportMode(importMode);
			}
		}
	}

	@Override
	protected void processViewsData(
		Repository repo,
		Session session,
		File folder,
		DevArtifactsConfig config) throws OperationException
	{
		// Turn off replace mode for views
		ImportDevArtifactsConfig importConfig = (ImportDevArtifactsConfig) config;
		ImportSpecMode importMode = importConfig.getImportMode();
		if (ImportSpecMode.REPLACE.equals(importMode))
		{
			importConfig.setImportMode(ImportSpecMode.UPDATE_OR_INSERT);
		}

		super.processViewsData(repo, session, folder, config);

		if (ImportSpecMode.REPLACE.equals(importMode))
		{
			importConfig.setImportMode(importMode);
		}
	}

	@Override
	protected void copyEnvironment(Repository repo, Session session, DevArtifactsConfig config)
		throws OperationException
	{
		ImportDevArtifactsConfig importConfig = (ImportDevArtifactsConfig) config;
		ImportSpecMode importMode = importConfig.getImportMode();
		importConfig.setImportMode(ImportSpecMode.REPLACE);

		List<AdaptationTable> tablesForData = config.getTablesForData();

		createDataSetsFromDataModels(repo, session, importConfig);
		List<AdaptationTable> allTables = getAllTablesToProcess(repo, tablesForData, importConfig);

		try
		{
			doEnvCopyDataSpacePermissions(session, config);
			doEnvCopyDirectory(repo, session, config);
			doEnvCopyViews(repo, session, config);
			doEnvCopyTasks(repo, session, config);
			doEnvCopyPerspectives(repo, session, config);
			doEnvCopyDataTables(repo, session, allTables, config);
		}
		finally
		{
			importConfig.setImportMode(importMode);
		}

		HashSet<AdaptationHome> dataSpacesToCopy = new HashSet<AdaptationHome>();

		prepareEnvCopyWFModels(repo, dataSpacesToCopy);
		prepareEnvCopyDMAModels(repo, dataSpacesToCopy);
		prepareEnvCopyGlobalPermissions(repo, dataSpacesToCopy);
		prepareEnvCopyDataSpaces(repo, allTables, config, dataSpacesToCopy);
		prepareEnvCopyAddons(repo, config, dataSpacesToCopy);

		for (AdaptationHome dataSpace : dataSpacesToCopy)
		{
			File srcFile = new File(config.getCopyEnvironmentFolder(), ENV_COPY_ARCHIVE_PREFIX
				+ dataSpace.getKey().getName() + ".ebx");
			importArchive(session, dataSpace, srcFile, config);
		}
	}

	@Override
	protected void processArtifacts(Repository repo, Session session, DevArtifactsConfig config)
		throws OperationException
	{
		// For import, if it's an environment copy we skip all other artifacts
		if (!config.isEnvironmentCopy())
		{
			super.processArtifacts(repo, session, config);
		}
	}

	@Override
	protected void processDataSetPermissions(
		Repository repo,
		Session session,
		List<Adaptation> dataSets,
		File folder,
		DevArtifactsConfig config) throws OperationException
	{
		ImportDevArtifactsConfig importConfig = (ImportDevArtifactsConfig) config;
		List<Adaptation> allDataSets = getAllDataSetsToProcess(
			repo,
			session,
			dataSets,
			importConfig);
		super.processDataSetPermissions(repo, session, allDataSets, folder, config);

		if (!importConfig.getDataSetsToCreate().isEmpty())
		{
			repo.refreshSchemas(true);
		}
	}

	private List<Adaptation> createDataSetsFromDataModels(
		Repository repo,
		Session session,
		ImportDevArtifactsConfig importConfig) throws OperationException
	{
		List<DataSetCreationInfo> dataSetsToCreate = importConfig.getDataSetsToCreate();
		List<Adaptation> dataSets = new ArrayList<Adaptation>();
		for (DataSetCreationInfo dataSetCreationInfo : dataSetsToCreate)
		{
			DataSetCreationKey key = dataSetCreationInfo.getDataSetCreationKey();
			HomeKey dataSpaceKey = key.getDataSpaceKey();
			AdaptationName dataSetName = key.getDataSetName();

			AdaptationHome dataSpace = repo.lookupHome(dataSpaceKey);
			if (dataSpace == null)
			{
				throw OperationException.createError("Can't create data set "
					+ dataSetName.getStringName() + " because data space " + dataSpaceKey.getName()
					+ " doesn't exist.");
			}

			File folder = importConfig.getPermissionsFolder();
			Properties props;
			try
			{
				props = processDataSetDataPropertiesFile(
					session,
					dataSpace,
					dataSetName,
					folder,
					DevArtifactsService.PERMISSIONS_DATA_SET_PREFIX,
					importConfig);
			}
			catch (IOException ex)
			{
				throw OperationException.createError(ex);
			}
			Adaptation dataSet = getOrCreateDataSetFromProperties(
				session,
				dataSetName.getStringName(),
				props,
				dataSpace,
				SchemaLocation.parse("urn:ebx:module:" + dataSetCreationInfo.getDataModelXSD()));
			if (dataSet != null)
			{
				dataSets.add(dataSet);
			}
		}
		return dataSets;
	}

	@Override
	protected void processDataSpacePermissions(
		ProcedureContext pContext,
		AdaptationHome dataSpace,
		File folder,
		AdaptationTable permissionsTable,
		DevArtifactsConfig config) throws OperationException
	{
		// Data space permissions can't use replace mode so manually delete all rows for
		// this data space that aren't owner, then change mode to update/insert and import
		final ImportDevArtifactsConfig importConfig = (ImportDevArtifactsConfig) config;
		ImportSpecMode importMode = importConfig.getImportMode();
		if (ImportSpecMode.REPLACE.equals(importMode))
		{
			// TODO: Should probably define these fields in AdminPermissionUtil.
			final RequestResult requestResult = permissionsTable.createRequestResult("homeKey='"
				+ dataSpace.getKey().format() + "' and not(profile='Bowner')");
			try
			{
				// Delete all but the owner row for this data space
				for (Adaptation record; (record = requestResult.nextAdaptation()) != null;)
				{
					DeleteRecordProcedure.execute(pContext, record, true, false, false);
				}
				try
				{
					importConfig.setImportMode(ImportSpecMode.UPDATE_OR_INSERT);
					super.processDataSpacePermissions(
						pContext,
						dataSpace,
						folder,
						permissionsTable,
						config);
				}
				finally
				{
					importConfig.setImportMode(importMode);
				}
			}
			finally
			{
				requestResult.close();
			}
		}
		else
		{
			super.processDataSpacePermissions(pContext, dataSpace, folder, permissionsTable, config);
		}
	}

	@Override
	protected void processUsersRolesTable(
		Session session,
		final AdaptationTable usersRolesTable,
		File folder,
		String filePrefix,
		DevArtifactsConfig config) throws OperationException
	{
		AdaptationTable usersTable = AdminUtil.getDirectoryUsersTable(usersRolesTable.getContainerAdaptation());
		String usersTableFilename = filePrefix + getTableFilename(usersTable);

		ImportDevArtifactsConfig importConfig = (ImportDevArtifactsConfig) config;
		ImportSpecMode importMode = importConfig.getImportMode();
		if (ImportSpecMode.REPLACE.equals(importMode))
		{
			ReplaceUsersRolesProcedure replaceUsersRolesProc = new ReplaceUsersRolesProcedure(
				usersRolesTable,
				importConfig,
				folder,
				filePrefix);
			ProcedureExecutor.executeProcedure(
				replaceUsersRolesProc,
				session,
				usersTable.getContainerAdaptation());
		}
		else
		{
			// usersRoles table
			processTable(session, null, usersRolesTable, folder, filePrefix
				+ getTableFilename(usersRolesTable), null, null, importConfig);

			// users table
			processTable(
				session,
				null,
				usersTable,
				folder,
				usersTableFilename,
				null,
				null,
				importConfig);
		}
		// If the import mode is anything that allows an insert, then do the users import again in update mode.
		// This is because the password last update date gets changed when importing a new record
		// but doing the import again will update it to the original value from the file.
		if (!ImportSpecMode.UPDATE.equals(importMode))
		{
			importConfig.setImportMode(ImportSpecMode.UPDATE);
			try
			{
				processTable(
					session,
					null,
					usersTable,
					folder,
					usersTableFilename,
					null,
					null,
					importConfig);
			}
			finally
			{
				importConfig.setImportMode(importMode);
			}
		}
	}

	private Adaptation processDataSetFromFile(
		Session session,
		String dataSetName,
		Properties dataSetProps,
		File folder,
		String filePrefix,
		DevArtifactsConfig config,
		AdaptationHome dataSpace,
		SchemaLocation schemaLocation) throws OperationException
	{
		Adaptation dataSet = getOrCreateDataSetFromProperties(
			session,
			dataSetName,
			dataSetProps,
			dataSpace,
			schemaLocation);

		ImportDevArtifactsConfig importConfig = (ImportDevArtifactsConfig) config;
		ImportSpecMode importMode = importConfig.getImportMode();
		importConfig.setImportMode(ImportSpecMode.REPLACE);

		processDataSetDataXML(session, dataSet, folder, filePrefix, config);

		importConfig.setImportMode(importMode);

		try
		{
			processDataSetDataProperties(session, dataSetProps, dataSet, folder, filePrefix, config);
		}
		catch (IOException ex)
		{
			throw OperationException.createError(ex);
		}
		return dataSet;
	}

	private Adaptation getOrCreateDataSetFromProperties(
		Session session,
		String dataSetName,
		Properties dataSetProps,
		AdaptationHome dataSpace,
		SchemaLocation schemaLocation) throws OperationException
	{
		AdaptationReference adaptationRef = AdaptationReference.forPersistentName(dataSetName);
		Adaptation dataSet = dataSpace.findAdaptationOrNull(adaptationRef);
		if (dataSet == null)
		{
			String parentDataSetName = getPropertyValueOrNull(
				dataSetProps,
				DevArtifactsService.DATA_SET_PROPERTY_PARENT_DATA_SET);
			CreateDataSetProcedure createDataSetProc;
			if (parentDataSetName == null)
			{
				createDataSetProc = new CreateRootDataSetProcedure(adaptationRef, schemaLocation);
			}
			else
			{
				createDataSetProc = new CreateChildDataSetProcedure(
					adaptationRef,
					AdaptationName.forName(parentDataSetName));
			}
			ProcedureExecutor.executeProcedure(createDataSetProc, session, dataSpace);
			dataSet = createDataSetProc.getDataSet();
		}
		return dataSet;
	}

	@Override
	protected void processWorkflowModel(
		Session session,
		String wfModel,
		File folder,
		String filePrefix,
		DevArtifactsConfig config,
		AdaptationHome wfDataSpace) throws OperationException
	{
		Properties props;
		try
		{
			props = processDataSetDataPropertiesFile(
				session,
				wfDataSpace,
				AdaptationName.forName(wfModel),
				folder,
				filePrefix,
				config);
		}
		catch (IOException ex)
		{
			throw OperationException.createError(ex);
		}
		processDataSetFromFile(
			session,
			wfModel,
			props,
			folder,
			filePrefix,
			config,
			wfDataSpace,
			AdminUtil.getWorkflowModelsSchemaLocation());
	}

	@Override
	protected void processPerspectivesData(
		Repository repo,
		Session session,
		File folder,
		DevArtifactsConfig config) throws OperationException
	{
		super.processPerspectivesData(repo, session, folder, config);

		if (config.isProcessPerspectivesData())
		{
			Adaptation perspectivesDataSet = AdminUtil.getPerspectivesDataSet(repo);

			AdaptationTreeOptimizerSpec_RemoveDuplicates optimizer = new AdaptationTreeOptimizerSpec_RemoveDuplicates(
				perspectivesDataSet,
				true);
			AdaptationTreeOptimizerIterator iter = optimizer.createOptimizerIterator(session);
			iter.executeAll();
		}
	}

	@Override
	protected void doProcessPerspectivesDataSet(
		Session session,
		File folder,
		AdaptationHome dataSpace,
		String dataSetName,
		DevArtifactsConfig config) throws OperationException
	{
		AdaptationName perspectiveName = AdaptationName.forName(dataSetName);
		Properties props;
		try
		{
			props = processDataSetDataPropertiesFile(
				session,
				dataSpace,
				perspectiveName,
				folder,
				PERSPECTIVE_PREFIX,
				config);
		}
		catch (IOException ex)
		{
			throw OperationException.createError(ex);
		}
		Adaptation dataSet = getOrCreateDataSetFromProperties(
			session,
			dataSetName,
			props,
			dataSpace,
			null);

		processPerspectivesDataGroups(session, folder, dataSet, config);
		try
		{
			processDataSetDataProperties(
				session,
				props,
				dataSet,
				folder,
				PERSPECTIVE_PREFIX,
				config);
		}
		catch (IOException ex)
		{
			throw OperationException.createError(ex);
		}

		processPerspectivesChildDataSets(session, folder, config, dataSet, props);
	}

	private void processPerspectivesChildDataSets(
		Session session,
		File folder,
		DevArtifactsConfig config,
		Adaptation parentDataSet,
		Properties parentDataSetProperties) throws OperationException
	{
		AdaptationHome perspectivesDataSpace = parentDataSet.getHome();
		String childDataSetsStr = getPropertyValueOrNull(
			parentDataSetProperties,
			DevArtifactsService.DATA_SET_PROPERTY_CHILD_DATA_SETS);
		if (childDataSetsStr != null)
		{
			String[] childPerspectives = childDataSetsStr.split(DevArtifactsService.DATA_SET_PROPERTIES_FILE_CHILD_DATA_SETS_SEPARATOR);
			for (String childPerspective : childPerspectives)
			{
				doProcessPerspectivesDataSet(
					session,
					folder,
					perspectivesDataSpace,
					childPerspective,
					config);
			}
		}
	}

	// Overridden in order to simulate replace mode when a filter is provided (otherwise would delete other records as well)
	@Override
	protected void processDataTable(
		ProcedureContext pContext,
		AdaptationTable table,
		File folder,
		String filename,
		DevArtifactsConfig config) throws OperationException
	{
		ImportDevArtifactsConfig importConfig = (ImportDevArtifactsConfig) config;
		if (ImportSpecMode.REPLACE.equals(importConfig.getImportMode()))
		{
			AdaptationFilter filter = getFilterForDataTable(table);
			if (filter == null)
			{
				super.processDataTable(pContext, table, folder, filename, config);
			}
			else
			{
				// Delete all existing records that match the filter
				Request request = table.createRequest();
				request.setSpecificFilter(filter);
				RequestResult requestResult = request.execute();
				try
				{
					for (Adaptation record; (record = requestResult.nextAdaptation()) != null;)
					{
						DeleteRecordProcedure.execute(pContext, record, true, false, false);
					}
				}
				finally
				{
					requestResult.close();
				}

				// Import in update or insert mode
				importConfig.setImportMode(ImportSpecMode.UPDATE_OR_INSERT);
				try
				{
					super.processDataTable(pContext, table, folder, filename, config);
				}
				finally
				{
					importConfig.setImportMode(ImportSpecMode.REPLACE);
				}
			}
		}
		else
		{
			super.processDataTable(pContext, table, folder, filename, config);
		}
	}

	private void doEnvCopyDataSpacePermissions(Session session, DevArtifactsConfig config)
		throws OperationException
	{
		List<AdaptationHome> dataSpacesForPerm = config.getDataSpacesForPermissions();
		processDataSpacesPermissions(
			session,
			dataSpacesForPerm,
			config.getCopyEnvironmentFolder(),
			config);
	}

	private void doEnvCopyDirectory(Repository repo, Session session, DevArtifactsConfig config)
		throws OperationException
	{
		processDirectoryData(repo, session, config.getCopyEnvironmentFolder(), config);
	}

	private void doEnvCopyViews(Repository repo, Session session, DevArtifactsConfig config)
		throws OperationException
	{
		processViewsData(repo, session, config.getCopyEnvironmentFolder(), config);
	}

	// TODO: Why aren't I doing these at parent level because both import/export seem to be the same
	private void doEnvCopyTasks(Repository repo, Session session, DevArtifactsConfig config)
		throws OperationException
	{
		processTasksData(repo, session, config.getCopyEnvironmentFolder(), config);
	}

	private void doEnvCopyPerspectives(Repository repo, Session session, DevArtifactsConfig config)
		throws OperationException
	{
		processPerspectivesData(repo, session, config.getCopyEnvironmentFolder(), config);
	}

	private void prepareEnvCopyWFModels(Repository repo, Set<AdaptationHome> dataSpacesToCopy)
		throws OperationException
	{
		AdaptationHome wfDataSpace = AdminUtil.getWorkflowModelsDataSpace(repo);
		dataSpacesToCopy.add(wfDataSpace);
	}

	private void prepareEnvCopyDMAModels(Repository repo, Set<AdaptationHome> dataSpacesToCopy)
		throws OperationException
	{
		AdaptationHome dmaDataSpace = AdminUtil.getDMADataSpace(repo);
		dataSpacesToCopy.add(dmaDataSpace);
	}

	private void prepareEnvCopyGlobalPermissions(
		Repository repo,
		Set<AdaptationHome> dataSpacesToCopy) throws OperationException
	{
		AdaptationHome globalPermissionsDataSpace = AdminUtil.getGlobalPermissionsDataSpace(repo);
		dataSpacesToCopy.add(globalPermissionsDataSpace);
	}

	private void prepareEnvCopyDataSpaces(
		Repository repo,
		List<AdaptationTable> tablesForData,
		DevArtifactsConfig config,
		Set<AdaptationHome> dataSpacesToCopy) throws OperationException
	{
		ImportDevArtifactsConfig importConfig = (ImportDevArtifactsConfig) config;
		List<Adaptation> dataSetsForPerm = importConfig.getDataSetsForPermissions();
		for (Adaptation dataSet : dataSetsForPerm)
		{
			dataSpacesToCopy.add(dataSet.getHome());
		}

		List<DataSetCreationInfo> dataSetsToCreate = importConfig.getDataSetsToCreate();
		for (DataSetCreationInfo dataSetCreationInfo : dataSetsToCreate)
		{
			DataSetCreationKey key = dataSetCreationInfo.getDataSetCreationKey();
			HomeKey dataSpaceKey = key.getDataSpaceKey();
			AdaptationHome dataSpace = repo.lookupHome(dataSpaceKey);
			if (dataSpace != null)
			{
				dataSpacesToCopy.add(dataSpace);
			}
		}

		// Only process archive of data sets that we're processing all tables from
		// and if we're not processing data set permissions in child data spaces.
		// Otherwise, they'll be processed as individual xml files.
		if (!importConfig.isProcessDataSetPermissionsInChildDataSpaces())
		{
			// key = data set name, value = tables to process from data set
			Map<Adaptation, List<AdaptationTable>> dataSetTableMap = createMapOfDataSetTables(tablesForData);

			for (Map.Entry<Adaptation, List<AdaptationTable>> entry : dataSetTableMap.entrySet())
			{
				Adaptation dataSet = entry.getKey();
				List<AdaptationTable> tableList = entry.getValue();
				List<AdaptationTable> allTables = AdaptationUtil.getAllTables(dataSet);
				if (tableList.size() == allTables.size())
				{
					dataSpacesToCopy.add(dataSet.getHome());
				}
			}
		}
	}

	private void prepareEnvCopyAddons(
		Repository repo,
		DevArtifactsConfig config,
		Set<AdaptationHome> dataSpacesToCopy) throws OperationException
	{
		if (config.isProcessAddonAdixData())
		{
			dataSpacesToCopy.add(AdminUtil.getAddonAdixPreferencesDataSpace(repo));
			dataSpacesToCopy.add(AdminUtil.getAddonAdixDataExchangeDataSpace(repo));
			dataSpacesToCopy.add(AdminUtil.getAddonAdixDataModelingDataSpace(repo));
		}

		if (config.isProcessAddonDaqaData())
		{
			dataSpacesToCopy.add(AdminUtil.getAddonDaqaDataSpace(repo));
		}

		if (config.isProcessAddonDqidData())
		{
			dataSpacesToCopy.add(AdminUtil.getAddonDqidDataSpace(repo));
		}
	}

	private void importArchive(
		Session session,
		AdaptationHome dataSpace,
		File srcFile,
		DevArtifactsConfig config) throws OperationException
	{
		if (!srcFile.exists())
		{
			throw OperationException.createError("Import file doesn't exist: "
				+ srcFile.getAbsolutePath());
		}

		ArchiveImportSpec spec = new ArchiveImportSpec();
		Archive archive = Archive.forFile(srcFile);
		spec.setArchive(archive);

		ImportArchiveProcedure proc = new ImportArchiveProcedure(spec);
		ProcedureExecutor.executeProcedure(proc, session, dataSpace);
		if (config.isProcessDataSetPermissionsInChildDataSpaces())
		{
			@SuppressWarnings("unchecked")
			List<AdaptationHome> snapshots = dataSpace.getVersionChildren();
			for (AdaptationHome snapshot : snapshots)
			{
				@SuppressWarnings("unchecked")
				List<AdaptationHome> childDataSpaces = snapshot.getBranchChildren();
				for (AdaptationHome childDataSpace : childDataSpaces)
				{
					if (childDataSpace.isOpen())
					{
						importArchive(session, childDataSpace, srcFile, config);
					}
				}
			}
		}
	}

	private class ImportXMLProcedure implements Procedure
	{
		private ImportSpec spec;

		public ImportXMLProcedure(ImportSpec spec)
		{
			this.spec = spec;
		}

		@Override
		public void execute(ProcedureContext pContext) throws Exception
		{
			doImport(pContext, spec);
		}
	}

	private class ImportArchiveProcedure implements Procedure
	{
		private ArchiveImportSpec spec;

		public ImportArchiveProcedure(ArchiveImportSpec spec)
		{
			this.spec = spec;
		}

		@Override
		public void execute(ProcedureContext pContext) throws Exception
		{
			pContext.doImportArchive(spec);
		}
	}

	/**
	 * This procedure handles importing usersRoles & users, simulating a "replace mode" but only for the predicate specified
	 */
	private class ReplaceUsersRolesProcedure implements Procedure
	{
		private AdaptationTable usersRolesTable;
		private ImportDevArtifactsConfig config;
		private File folder;
		private String filePrefix;

		public ReplaceUsersRolesProcedure(
			AdaptationTable usersRolesTable,
			ImportDevArtifactsConfig config,
			File folder,
			String filePrefix)
		{
			this.usersRolesTable = usersRolesTable;
			this.config = config;
			this.folder = folder;
			this.filePrefix = filePrefix;
		}

		@Override
		public void execute(ProcedureContext pContext) throws Exception
		{
			AdaptationTable usersTable = AdminUtil.getDirectoryUsersTable(usersRolesTable.getContainerAdaptation());
			String usersRolesFilename = filePrefix + getTableFilename(usersRolesTable);
			String usersFilename = filePrefix + getTableFilename(usersTable);
			String usersRolesPredicate = config.getUsersRolesPredicate();

			// It's unlikely that all users would want to be processed, but if for some reason they wish to,
			// they'll all be processed
			if (usersRolesPredicate == null)
			{
				processTable(
					null,
					pContext,
					usersRolesTable,
					folder,
					usersRolesFilename,
					null,
					null,
					config);
				processTable(null, pContext, usersTable, folder, usersFilename, null, null, config);
			}
			else
			{
				ImportSpecMode importMode = config.getImportMode();
				// Disable replace mode for import of users since each server has different users
				config.setImportMode(ImportSpecMode.UPDATE_OR_INSERT);

				try
				{
					// Do the import on usersRoles
					processTable(
						null,
						pContext,
						usersRolesTable,
						folder,
						usersRolesFilename,
						null,
						null,
						config);

					// Read the file and store the usersRoles pks from it
					HashSet<String> fileUsersRolesPKs = new HashSet<String>();
					String usersRolesTag = "<"
						+ usersRolesTable.getTablePath().getLastStep().format() + ">";
					String roleTag = "<"
						+ AdminUtil.getDirectoryUsersRolesRolePath().getLastStep().format() + ">";
					String userTag = "<"
						+ AdminUtil.getDirectoryUsersRolesUserPath().getLastStep().format() + ">";

					BufferedReader reader = new BufferedReader(new FileReader(new File(
						folder,
						usersRolesFilename + ".xml")));
					try
					{
						for (String line; (line = reader.readLine()) != null;)
						{
							String trimmedLine = line.trim();
							if (trimmedLine.startsWith(usersRolesTag))
							{
								line = reader.readLine();
								if (line == null)
								{
									throw OperationException.createError("Incomplete "
										+ usersRolesTag + " entry.");
								}
								trimmedLine = line.trim();
								String role = trimmedLine.substring(
									roleTag.length(),
									trimmedLine.lastIndexOf("<"));

								line = reader.readLine();
								if (line == null)
								{
									throw OperationException.createError("Incomplete "
										+ usersRolesTag + " entry.");
								}
								trimmedLine = line.trim();
								String user = trimmedLine.substring(
									userTag.length(),
									trimmedLine.lastIndexOf("<"));

								fileUsersRolesPKs.add(role + PrimaryKey.SEPARATOR + user);
							}
						}
					}
					finally
					{
						reader.close();
					}

					// Loop through all usersRoles in EBX that match the predicate and store their users
					HashSet<String> users = new HashSet<String>();
					RequestResult reqRes = usersRolesTable.createRequestResult(usersRolesPredicate);
					try
					{
						for (Adaptation usersRolesRecord; (usersRolesRecord = reqRes.nextAdaptation()) != null;)
						{
							String user = usersRolesRecord.getString(AdminUtil.getDirectoryUsersRolesUserPath());
							users.add(user);
						}
					}
					finally
					{
						reqRes.close();
					}

					AdaptationFilter usersRolesFilter = getUsersFilter(
						AdminUtil.getDirectoryUsersRolesUserPath(),
						users);
					// Loop through all current usersRoles for the predicate's users, after the import finished
					Request req = usersRolesTable.createRequest();
					req.setSpecificFilter(usersRolesFilter);
					reqRes = req.execute();
					try
					{
						for (Adaptation usersRolesRecord; (usersRolesRecord = reqRes.nextAdaptation()) != null;)
						{
							// If the usersRole isn't in the input file then it should be deleted.
							// This would normally be handled by a replace.
							if (!fileUsersRolesPKs.contains(usersRolesRecord.getOccurrencePrimaryKey()
								.format()))
							{
								DeleteRecordProcedure.execute(pContext, usersRolesRecord);
							}
						}
					}
					finally
					{
						reqRes.close();
					}

					// Now do a similar thing for the users table. We know the users we need though from the previous queries.

					processTable(
						null,
						pContext,
						usersTable,
						folder,
						usersFilename,
						null,
						null,
						config);

					// TODO: I think there's a bug in here somewhere. When a user has an &amp; in its name, it breaks this.

					HashSet<String> fileUsers = new HashSet<String>();
					String usersTag = "<" + usersTable.getTablePath().getLastStep().format() + ">";
					String loginTag = "<"
						+ AdminUtil.getDirectoryUsersLoginPath().getLastStep().format() + ">";

					reader = new BufferedReader(new FileReader(new File(folder, usersFilename
						+ ".xml")));
					try
					{
						for (String line; (line = reader.readLine()) != null;)
						{
							String trimmedLine = line.trim();
							if (trimmedLine.startsWith(usersTag))
							{
								line = reader.readLine();
								if (line == null)
								{
									throw OperationException.createError("Incomplete " + usersTag
										+ " entry.");
								}
								trimmedLine = line.trim();
								String user = trimmedLine.substring(
									loginTag.length(),
									trimmedLine.lastIndexOf("<"));

								fileUsers.add(user);
							}
						}
					}
					finally
					{
						reader.close();
					}

					AdaptationFilter usersFilter = getUsersFilter(
						AdminUtil.getDirectoryUsersLoginPath(),
						users);
					req = usersTable.createRequest();
					req.setSpecificFilter(usersFilter);
					reqRes = req.execute();
					try
					{
						for (Adaptation usersRecord; (usersRecord = reqRes.nextAdaptation()) != null;)
						{
							if (!fileUsers.contains(usersRecord.getOccurrencePrimaryKey().format()))
							{
								DeleteRecordProcedure.execute(pContext, usersRecord);
							}
						}
					}
					finally
					{
						reqRes.close();
					}
				}
				finally
				{
					// Set the mode back to what it should be
					config.setImportMode(importMode);
				}
			}
		}
	}

	private abstract class CreateDataSetProcedure implements Procedure
	{
		protected AdaptationReference dataSetName;
		protected Adaptation dataSet;

		protected CreateDataSetProcedure(AdaptationReference dataSetName)
		{
			this.dataSetName = dataSetName;
		}

		public Adaptation getDataSet()
		{
			return this.dataSet;
		}
	}

	private class CreateRootDataSetProcedure extends CreateDataSetProcedure
	{
		private SchemaLocation schemaLocation;

		public CreateRootDataSetProcedure(
			AdaptationReference dataSetName,
			SchemaLocation schemaLocation)
		{
			super(dataSetName);
			this.schemaLocation = schemaLocation;
		}

		@Override
		public void execute(ProcedureContext pContext) throws Exception
		{
			dataSet = pContext.doCreateRoot(schemaLocation, dataSetName, pContext.getSession()
				.getUserReference());
		}
	}

	private class CreateChildDataSetProcedure extends CreateDataSetProcedure
	{
		private AdaptationName parentDataSetName;

		public CreateChildDataSetProcedure(
			AdaptationReference dataSetName,
			AdaptationName parentDataSetName)
		{
			super(dataSetName);
			this.parentDataSetName = parentDataSetName;
		}

		@Override
		public void execute(ProcedureContext pContext) throws Exception
		{
			dataSet = pContext.doCreateChild(parentDataSetName, dataSetName, pContext.getSession()
				.getUserReference());
		}
	}

	private class SetDataSetPropertiesProcedure implements Procedure
	{
		private Adaptation dataSet;
		private String label;
		private String owner;

		public SetDataSetPropertiesProcedure(Adaptation dataSet, String label, String owner)
		{
			this.dataSet = dataSet;
			this.label = label;
			this.owner = owner;
		}

		@Override
		public void execute(ProcedureContext pContext) throws Exception
		{
			if (label != null && !label.equals(dataSet.getLabel(pContext.getSession().getLocale())))
			{
				pContext.setInstanceLabel(dataSet, UserMessage.createInfo(label));
			}
			if (owner != null && !owner.equals(dataSet.getOwner().format()))
			{
				Profile ownerProfile = Profile.parse(owner);
				pContext.setInstanceOwner(dataSet, ownerProfile);
			}
		}
	}
}
