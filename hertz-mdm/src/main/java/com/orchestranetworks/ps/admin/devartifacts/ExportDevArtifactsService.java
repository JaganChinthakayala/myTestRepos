/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.admin.devartifacts;

import java.io.*;
import java.util.*;

import com.onwbp.adaptation.*;
import com.orchestranetworks.instance.*;
import com.orchestranetworks.ps.procedure.*;
import com.orchestranetworks.ps.util.*;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.service.*;

/**
 * A servlet for exporting of the dev artifacts
 */
@SuppressWarnings("serial")
public class ExportDevArtifactsService extends DevArtifactsService
{
	@Override
	public DevArtifactsConfigFactory getConfigFactory()
	{
		return new ExportDevArtifactsConfigFactory();
	}

	@Override
	protected void processArtifacts(Repository repo, Session session, DevArtifactsConfig config)
		throws OperationException
	{
		createArtifactsFolders(config);
		super.processArtifacts(repo, session, config);
	}

	@Override
	protected void processUsersRolesTable(
		Session session,
		AdaptationTable usersRolesTable,
		File folder,
		String filePrefix,
		DevArtifactsConfig config) throws OperationException
	{
		AdaptationTable usersTable = AdminUtil.getDirectoryUsersTable(usersRolesTable.getContainerAdaptation());
		String usersRolesPredicate = config.getUsersRolesPredicate();
		String usersTableFilename = filePrefix + getTableFilename(usersTable);
		String usersRolesTableFilename = filePrefix + getTableFilename(usersRolesTable);

		AdaptationFilter usersRolesFilter = null;
		AdaptationFilter usersFilter = null;

		// It's unlikely that all users would want to be processed, but if for some reason they wish to,
		// the filter will be null so they'll all be processed
		if (usersRolesPredicate != null)
		{
			// Use a set to avoid duplicates
			HashSet<String> users = new HashSet<String>();

			RequestResult requestResult = usersRolesTable.createRequestResult(usersRolesPredicate);
			try
			{
				// Put users from every row that matches the predicate into the set
				for (Adaptation usersRolesRecord; (usersRolesRecord = requestResult.nextAdaptation()) != null;)
				{
					String user = usersRolesRecord.getString(AdminUtil.getDirectoryUsersRolesUserPath());
					users.add(user);
				}
			}
			finally
			{
				requestResult.close();
			}

			// Create filters that only process the users we got from the query.
			// (Not same as simply processing the query because we want all other rows for those same users)
			usersRolesFilter = getUsersFilter(AdminUtil.getDirectoryUsersRolesUserPath(), users);
			usersFilter = getUsersFilter(AdminUtil.getDirectoryUsersLoginPath(), users);
		}

		// usersRoles table
		processTable(
			session,
			null,
			usersRolesTable,
			folder,
			usersRolesTableFilename,
			null,
			usersRolesFilter,
			config);

		// users table
		processTable(
			session,
			null,
			usersTable,
			folder,
			usersTableFilename,
			null,
			usersFilter,
			config);
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
		ExportSpec spec = createExportSpec(folder, filename);

		Request request = table.createRequest();
		if (predicate == null)
		{
			if (filter != null)
			{
				request.setSpecificFilter(filter);
			}
		}
		else
		{
			request.setXPathFilter(predicate);
		}
		spec.setRequest(request);

		if (pContext == null)
		{
			ExportXMLProcedure proc = new ExportXMLProcedure(spec, config.getLineSeparator());
			ProcedureExecutor.executeProcedure(proc, session, table.getContainerAdaptation()
				.getHome());
		}
		else
		{
			try
			{
				doExport(pContext, spec, config.getLineSeparator());
			}
			catch (IOException ex)
			{
				throw OperationException.createError(ex);
			}
		}
	}

	private static void doExport(ProcedureContext pContext, ExportSpec spec, String lineSeparator)
		throws IOException, OperationException
	{
		boolean allPrivileges = pContext.isAllPrivileges();
		pContext.setAllPrivileges(true);
		try
		{
			pContext.doExport(spec);
		}
		finally
		{
			pContext.setAllPrivileges(allPrivileges);
		}
		if (!System.getProperty("line.separator").equals(lineSeparator))
		{
			convertLineSeparator(spec.getDestinationFile(), lineSeparator);
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
		ExportSpec spec = createExportSpec(folder, filename);

		spec.setSourceAdaptation(dataSet);
		spec.setSelection(groupNode);

		ExportXMLProcedure proc = new ExportXMLProcedure(spec, config.getLineSeparator());
		ProcedureExecutor.executeProcedure(proc, session, dataSet.getHome());
	}

	private static ExportSpec createExportSpec(File folder, String filename)
	{
		ExportSpec spec = new ExportSpec();
		spec.setIncludesTechnicalData(false);
		spec.setIncludesComputedValues(false);
		spec.setOmitXMLComment(true);
		File destFile = new File(folder, filename + ".xml");
		spec.setDestinationFile(destFile);
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
		ExportSpec spec = createExportSpec(folder, filePrefix
			+ dataSet.getAdaptationName().getStringName());
		spec.setSourceAdaptation(dataSet);

		ExportXMLProcedure proc = new ExportXMLProcedure(spec, config.getLineSeparator());
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
		Adaptation dataSet = dataSpace.findAdaptationOrNull(dataSetName);
		String label = dataSet.getLabel(session.getLocale());
		if (label == null)
		{
			label = "";
		}
		Profile owner = dataSet.getOwner();

		Properties props = new Properties();
		props.put(DevArtifactsService.DATA_SET_PROPERTY_LABEL, label);
		props.put(DevArtifactsService.DATA_SET_PROPERTY_OWNER, owner.format());

		Adaptation parentDataSet = dataSet.getParent();
		String parentDataSetName = parentDataSet == null ? "" : parentDataSet.getAdaptationName()
			.getStringName();
		props.put(DevArtifactsService.DATA_SET_PROPERTY_PARENT_DATA_SET, parentDataSetName);

		@SuppressWarnings("unchecked")
		Iterator<Adaptation> childDataSetIter = dataSpace.findAllChildren(dataSet).iterator();
		StringBuilder bldr = new StringBuilder();
		while (childDataSetIter.hasNext())
		{
			Adaptation childDataSet = childDataSetIter.next();
			bldr.append(childDataSet.getAdaptationName().getStringName());
			if (childDataSetIter.hasNext())
			{
				bldr.append(DevArtifactsService.DATA_SET_PROPERTIES_FILE_CHILD_DATA_SETS_SEPARATOR);
			}
		}
		props.put(DevArtifactsService.DATA_SET_PROPERTY_CHILD_DATA_SETS, bldr.toString());
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
		File propertiesFile = new File(folder, filePrefix
			+ dataSet.getAdaptationName().getStringName()
			+ DevArtifactsService.DATA_SET_PROPERTIES_SUFFIX);

		BufferedWriter writer = new BufferedWriter(new FileWriter(propertiesFile));

		// We can't use Properties methods to write it out because we need to specify the line separator
		try
		{
			@SuppressWarnings("unchecked")
			Enumeration<String> keys = (Enumeration<String>) props.propertyNames();
			while (keys.hasMoreElements())
			{
				String key = keys.nextElement();
				writer.append(key);
				writer.append("=");
				writer.append(props.getProperty(key));
				writer.append(config.getLineSeparator());
			}
			writer.flush();
		}
		finally
		{
			writer.close();
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

			AdaptationTable tasksTable = AdminUtil.getTasksTable(taskSchedulerDataSet);
			// Make sure to skip the built-in task for repository clean-up
			processTable(session, null, tasksTable, folder, DATA_PREFIX
				+ getTableFilename(tasksTable), AdminUtil.getTasksNamePath().format() + " != '"
				+ AdminUtil.getTasksNameValueForRepositoryCleanup() + "'", null, config);
		}
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
		AdaptationName wfModelName = AdaptationName.forName(wfModel);
		Adaptation wfDataSet = wfDataSpace.findAdaptationOrNull(wfModelName);
		processDataSetDataXML(session, wfDataSet, folder, filePrefix, config);
		try
		{
			Properties props = processDataSetDataPropertiesFile(
				session,
				wfDataSpace,
				wfModelName,
				folder,
				filePrefix,
				config);
			processDataSetDataProperties(session, props, wfDataSet, folder, filePrefix, config);
		}
		catch (IOException ex)
		{
			throw OperationException.createError(ex);
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
		Adaptation dataSet = dataSpace.findAdaptationOrNull(AdaptationName.forName(dataSetName));
		processPerspectivesDataGroups(session, folder, dataSet, config);

		Properties props;
		try
		{
			props = processDataSetDataPropertiesFile(
				session,
				dataSet.getHome(),
				dataSet.getAdaptationName(),
				folder,
				PERSPECTIVE_PREFIX,
				config);
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

		processPerspectivesChildDataSets(session, folder, config, dataSet);
	}

	private void processPerspectivesChildDataSets(
		Session session,
		File folder,
		DevArtifactsConfig config,
		Adaptation parentDataSet) throws OperationException
	{
		AdaptationHome perspectivesDataSpace = parentDataSet.getHome();
		@SuppressWarnings("unchecked")
		List<Adaptation> childPerspectivesDataSets = perspectivesDataSpace.findAllChildren(parentDataSet);
		for (Adaptation childPerspectivesDataSet : childPerspectivesDataSets)
		{
			doProcessPerspectivesDataSet(
				session,
				folder,
				childPerspectivesDataSet.getHome(),
				childPerspectivesDataSet.getAdaptationName().getStringName(),
				config);
		}
	}

	@Override
	protected void copyEnvironment(Repository repo, Session session, DevArtifactsConfig config)
		throws OperationException
	{
		createArtifactsFolders(config);

		doEnvCopyDataSpacePermissions(session, config);
		doEnvCopyDirectory(repo, session, config);
		doEnvCopyViews(repo, session, config);
		doEnvCopyTasks(repo, session, config);
		doEnvCopyPerspectives(repo, session, config);
		doEnvCopyDataTables(repo, session, config.getTablesForData(), config);

		// Set up a hash map of all the data spaces to copy

		// <key = data space, value = <key=data set name, value=whether to copy data>>
		HashMap<AdaptationHome, Map<AdaptationName, boolean[]>> dataSpacesToCopy = new HashMap<AdaptationHome, Map<AdaptationName, boolean[]>>();

		prepareEnvCopyWFModels(repo, dataSpacesToCopy);
		prepareEnvCopyDMAModels(repo, dataSpacesToCopy);
		prepareEnvCopyGlobalPermissions(repo, dataSpacesToCopy);
		prepareEnvCopyDataSpaces(config, dataSpacesToCopy);
		prepareEnvCopyAddons(config, repo, dataSpacesToCopy);

		// Loop through the hash map and export the data spaces
		for (Map.Entry<AdaptationHome, Map<AdaptationName, boolean[]>> entry : dataSpacesToCopy.entrySet())
		{
			AdaptationHome dataSpace = entry.getKey();
			// Only include the data sets specified
			Map<AdaptationName, boolean[]> dataSetMap = entry.getValue();
			if (!dataSetMap.isEmpty())
			{
				File destFile = new File(config.getCopyEnvironmentFolder(), ENV_COPY_ARCHIVE_PREFIX
					+ dataSpace.getKey().getName() + ".ebx");
				exportArchive(session, dataSpace, dataSetMap, destFile);
			}
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

	private void doEnvCopyPerspectives(Repository repo, Session session, DevArtifactsConfig config)
		throws OperationException
	{
		processPerspectivesData(repo, session, config.getCopyEnvironmentFolder(), config);
	}

	private void doEnvCopyTasks(Repository repo, Session session, DevArtifactsConfig config)
		throws OperationException
	{
		processTasksData(repo, session, config.getCopyEnvironmentFolder(), config);
	}

	private void prepareEnvCopyWFModels(
		Repository repo,
		Map<AdaptationHome, Map<AdaptationName, boolean[]>> dataSpacesToCopy)
		throws OperationException
	{
		AdaptationHome wfDataSpace = AdminUtil.getWorkflowModelsDataSpace(repo);
		addAllDataSets(wfDataSpace, dataSpacesToCopy, true, false);
	}

	private void prepareEnvCopyDMAModels(
		Repository repo,
		Map<AdaptationHome, Map<AdaptationName, boolean[]>> dataSpacesToCopy)
		throws OperationException
	{
		AdaptationHome dmaDataSpace = AdminUtil.getDMADataSpace(repo);
		addAllDataSets(dmaDataSpace, dataSpacesToCopy, true, false);
	}

	private void prepareEnvCopyGlobalPermissions(
		Repository repo,
		Map<AdaptationHome, Map<AdaptationName, boolean[]>> dataSpacesToCopy)
		throws OperationException
	{
		AdaptationHome globalPermissionsDataSpace = AdminUtil.getGlobalPermissionsDataSpace(repo);
		addAllDataSets(globalPermissionsDataSpace, dataSpacesToCopy, true, false);
	}

	private void prepareEnvCopyDataSpaces(
		DevArtifactsConfig config,
		Map<AdaptationHome, Map<AdaptationName, boolean[]>> dataSpacesToCopy)
		throws OperationException
	{
		boolean[] booleanArr = new boolean[] { false, false };
		List<Adaptation> dataSetsForPerm = config.getDataSetsForPermissions();
		for (Adaptation dataSet : dataSetsForPerm)
		{
			AdaptationHome dataSpace = dataSet.getHome();
			Map<AdaptationName, boolean[]> dataSetMap = dataSpacesToCopy.get(dataSpace);
			if (dataSetMap == null)
			{
				dataSetMap = new HashMap<AdaptationName, boolean[]>();
				dataSpacesToCopy.put(dataSpace, dataSetMap);
			}
			dataSetMap.put(dataSet.getAdaptationName(), booleanArr);
		}

		// Only export archive of data sets that we're exporting all tables from
		// and if not processing data set permissions in child data spaces.
		// Otherwise, they'll be exported as individual xml files.
		if (!config.isProcessDataSetPermissionsInChildDataSpaces())
		{
			List<AdaptationTable> tablesForData = config.getTablesForData();
			// key = data set name, value = tables to export from data set
			Map<Adaptation, List<AdaptationTable>> dataSetTableMap = createMapOfDataSetTables(tablesForData);

			booleanArr = new boolean[] { true, false };
			for (Map.Entry<Adaptation, List<AdaptationTable>> entry : dataSetTableMap.entrySet())
			{
				Adaptation dataSet = entry.getKey();
				List<AdaptationTable> tableList = entry.getValue();
				List<AdaptationTable> allTables = AdaptationUtil.getAllTables(dataSet);
				if (tableList.size() == allTables.size())
				{
					AdaptationHome dataSpace = dataSet.getHome();

					Map<AdaptationName, boolean[]> dataSetMap = dataSpacesToCopy.get(dataSpace);
					if (dataSetMap == null)
					{
						dataSetMap = new HashMap<AdaptationName, boolean[]>();
						dataSpacesToCopy.put(dataSpace, dataSetMap);
					}
					dataSetMap.put(dataSet.getAdaptationName(), booleanArr);
				}
			}
		}
	}

	private void prepareEnvCopyAddons(
		DevArtifactsConfig config,
		Repository repo,
		Map<AdaptationHome, Map<AdaptationName, boolean[]>> dataSpacesToCopy)
		throws OperationException
	{
		if (config.isProcessAddonAdixData())
		{
			AdaptationHome adixPreferencesDataSpace = AdminUtil.getAddonAdixPreferencesDataSpace(repo);
			addAllDataSets(adixPreferencesDataSpace, dataSpacesToCopy, true, false);

			AdaptationHome adixDataExchangeDataSpace = AdminUtil.getAddonAdixDataExchangeDataSpace(repo);
			addAllDataSets(adixDataExchangeDataSpace, dataSpacesToCopy, true, false);

			AdaptationHome adixDataModelingDataSpace = AdminUtil.getAddonAdixDataModelingDataSpace(repo);
			addAllDataSets(adixDataModelingDataSpace, dataSpacesToCopy, true, false);
		}

		if (config.isProcessAddonDaqaData())
		{
			AdaptationHome daqaDataSpace = AdminUtil.getAddonDaqaDataSpace(repo);
			addAllDataSets(daqaDataSpace, dataSpacesToCopy, true, false);
		}

		if (config.isProcessAddonDqidData())
		{
			AdaptationHome dqidDataSpace = AdminUtil.getAddonDqidDataSpace(repo);
			addAllDataSets(dqidDataSpace, dataSpacesToCopy, true, false);
		}
	}

	private void exportArchive(
		Session session,
		AdaptationHome dataSpace,
		Map<AdaptationName, boolean[]> dataSetMap,
		File destFile) throws OperationException
	{
		ArchiveExportSpec spec = new ArchiveExportSpec();
		Archive archive = Archive.forFile(destFile);
		spec.setArchive(archive);
		for (Map.Entry<AdaptationName, boolean[]> entry : dataSetMap.entrySet())
		{
			AdaptationName dataSetName = entry.getKey();
			InstanceContentSpec instanceContentSpec = new InstanceContentSpec();
			boolean[] booleanArr = entry.getValue();
			instanceContentSpec.setIncludeValues(booleanArr[0]);
			instanceContentSpec.setIncludeAllDescendants(booleanArr[1]);
			spec.addInstance(dataSetName, instanceContentSpec);
		}

		ExportArchiveProcedure proc = new ExportArchiveProcedure(spec);
		ProcedureExecutor.executeProcedure(proc, session, dataSpace);
	}

	private static void addAllDataSets(
		AdaptationHome dataSpace,
		Map<AdaptationHome, Map<AdaptationName, boolean[]>> dataSpacesToCopy,
		boolean includeData,
		boolean includeChildDataSets)
	{
		boolean[] booleanArr = { includeData, includeChildDataSets };

		@SuppressWarnings("unchecked")
		List<Adaptation> dataSets = dataSpace.findAllRoots();
		HashMap<AdaptationName, boolean[]> dataSetMap = new HashMap<AdaptationName, boolean[]>();
		for (Adaptation dataSet : dataSets)
		{
			dataSetMap.put(dataSet.getAdaptationName(), booleanArr);
		}
		dataSpacesToCopy.put(dataSpace, dataSetMap);
	}

	private static void createArtifactsFolders(DevArtifactsConfig config) throws OperationException
	{
		boolean success;
		try
		{
			success = createFolder(config.getDataFolder())
				&& createFolder(config.getPermissionsFolder())
				&& createFolder(config.getWorkflowsFolder())
				&& createFolder(config.getCopyEnvironmentFolder())
				&& createFolder(config.getAdminFolder());
		}
		catch (IOException ex)
		{
			throw OperationException.createError(ex);
		}

		if (!success)
		{
			throw OperationException.createError("Error creating artifacts folders.");
		}
	}

	private static boolean createFolder(File folder) throws IOException
	{
		// Return true if it already exists, otherwise return result
		// of creating the folder
		return folder.exists() || folder.mkdirs();
	}

	private static void convertLineSeparator(File file, String lineSeparator) throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(file));
		StringBuilder bldr = new StringBuilder();
		try
		{
			for (String line; (line = reader.readLine()) != null;)
			{
				bldr.append(line);
				bldr.append(lineSeparator);
			}
		}
		finally
		{
			reader.close();
		}

		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		try
		{
			writer.write(bldr.toString());
			writer.flush();
		}
		finally
		{
			writer.close();
		}
	}

	private class ExportXMLProcedure implements Procedure
	{
		private ExportSpec spec;
		private String lineSeparator;

		public ExportXMLProcedure(ExportSpec spec, String lineSeparator)
		{
			this.spec = spec;
			this.lineSeparator = lineSeparator;
		}

		@Override
		public void execute(ProcedureContext pContext) throws Exception
		{
			doExport(pContext, spec, lineSeparator);
		}
	}

	private class ExportArchiveProcedure implements Procedure
	{
		private ArchiveExportSpec spec;

		public ExportArchiveProcedure(ArchiveExportSpec spec)
		{
			this.spec = spec;
		}

		@Override
		public void execute(ProcedureContext pContext) throws Exception
		{
			pContext.doExportArchive(spec);
		}
	}
}
