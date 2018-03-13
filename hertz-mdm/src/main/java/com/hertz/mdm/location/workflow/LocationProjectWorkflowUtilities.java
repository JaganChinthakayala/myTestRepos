/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.workflow;

import com.hertz.mdm.location.path.LocationProjectPathConfig;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.interactions.SessionInteraction;
import com.orchestranetworks.ps.project.workflow.ProjectWorkflowUtilities;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.workflow.DataContextReadOnly;
import com.orchestranetworks.workflow.ProcessExecutionContext;

/**
 */
public class LocationProjectWorkflowUtilities extends ProjectWorkflowUtilities
{
	public static void notifyLocationProjectTeam(
		DataContextReadOnly dataContext,
		ProcessExecutionContext processExecutionContext,
		int notificationTemplate) throws OperationException
	{
		notifyProjectTeam(
			dataContext,
			processExecutionContext,
			notificationTemplate,
			LocationProjectPathConfig.getInstance());
	}

	public static Adaptation getLocationProjectRecord(
		DataContextReadOnly dataContext,
		Repository repo) throws OperationException
	{
		return getProjectRecord(dataContext, repo, LocationProjectPathConfig.getInstance());
	}

	public static Adaptation getLocationProjectRecord(
		DataContextReadOnly dataContext,
		Repository repo,
		String dataSpaceParam) throws OperationException
	{
		return getProjectRecord(
			dataContext,
			repo,
			LocationProjectPathConfig.getInstance(),
			dataSpaceParam);
	}

	public static Adaptation getLocationProjectRecordFromSessionInteraction(
		SessionInteraction interaction,
		Adaptation dataSet) throws OperationException
	{
		return getProjectRecordFromSessionInteraction(
			interaction,
			dataSet,
			LocationProjectPathConfig.getInstance());
	}
}
