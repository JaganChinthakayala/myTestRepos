/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.scripttask;

import com.hertz.mdm._hertz.constants.HtzConstants;
import com.hertz.mdm._hertz.enums.Domains;
import com.hertz.mdm.location.constants.LocationProjectWorkflowConstants;
import com.hertz.mdm.location.enums.LocationProjectTypes;
import com.hertz.mdm.location.path.LocationPaths;
import com.hertz.mdm.location.util.HtzLocationUtilities;
import com.hertz.mdm.location.workflow.LocationProjectWorkflowUtilities;
import com.hertz.mdm.location.workflow.launcher.AsynchronousLocationWorkflowLauncher;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.RequestResult;
import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.ps.workflow.WorkflowLauncherContext;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.workflow.ScriptTask;
import com.orchestranetworks.workflow.ScriptTaskContext;

/**
 */
public class LaunchCloseLocationNotificationsScriptTask extends ScriptTask
{
	private static final String PROJECT_NAME_PREFIX = Domains.LOCATION + " - " + "Close Location ";

	@Override
	public void executeScript(ScriptTaskContext context) throws OperationException
	{
		Repository repo = context.getRepository();

		Adaptation projectRecord = LocationProjectWorkflowUtilities
			.getLocationProjectRecord(context, context.getRepository());

		if (projectRecord == null)
		{
			return;
		}

		Adaptation locationRecord = AdaptationUtil
			.followFK(projectRecord, LocationPaths._Root_LocationProject._Location);

		if (locationRecord == null)
		{
			return;
		}

		//Launch the Bank Reconciliation Workflow 
		AsynchronousLocationWorkflowLauncher workflowLauncher = new AsynchronousLocationWorkflowLauncher(
			context,
			LocationProjectWorkflowConstants.WF_KEY_BANK_RECONCILIATION);

		WorkflowLauncherContext workflowLauncherContext = HtzLocationUtilities
			.createWorkflowLauncherContext(
				projectRecord,
				locationRecord,
				context.getSession(),
				repo,
				LocationProjectTypes.CLOSE_LOCATION,
				PROJECT_NAME_PREFIX);

		workflowLauncher.execute(workflowLauncherContext, false);

		//Launch the Cars+ Admin Workflow For Canada Only
		if (HtzConstants.COUNTRY_CODE_CANADA.equals(
			locationRecord
				.getString(LocationPaths._Root_Location._LocationInformation__locationCountry)))
		{
			workflowLauncher = new AsynchronousLocationWorkflowLauncher(
				context,
				LocationProjectWorkflowConstants.WF_KEY_UPDATE_CARS_ADMIN);

			workflowLauncherContext = HtzLocationUtilities.createWorkflowLauncherContext(
				projectRecord,
				locationRecord,
				context.getSession(),
				repo,
				LocationProjectTypes.CLOSE_LOCATION,
				PROJECT_NAME_PREFIX);

			workflowLauncher.execute(workflowLauncherContext, false);
		}

		//Launch the Load Dash Workflow 
		workflowLauncher = new AsynchronousLocationWorkflowLauncher(
			context,
			LocationProjectWorkflowConstants.WF_KEY_UPDATE_DASH);

		workflowLauncherContext = HtzLocationUtilities.createWorkflowLauncherContext(
			projectRecord,
			locationRecord,
			context.getSession(),
			repo,
			LocationProjectTypes.CLOSE_LOCATION,
			PROJECT_NAME_PREFIX);

		workflowLauncher.execute(workflowLauncherContext, false);

		//Launch the Load HR Oracle Workflow 
		workflowLauncher = new AsynchronousLocationWorkflowLauncher(
			context,
			LocationProjectWorkflowConstants.WF_KEY_UPDATE_HR_ORACLE);

		workflowLauncherContext = HtzLocationUtilities.createWorkflowLauncherContext(
			projectRecord,
			locationRecord,
			context.getSession(),
			repo,
			LocationProjectTypes.CLOSE_LOCATION,
			PROJECT_NAME_PREFIX);

		workflowLauncher.execute(workflowLauncherContext, false);

		//Launch the MFD Help Desk Workflow 
		workflowLauncher = new AsynchronousLocationWorkflowLauncher(
			context,
			LocationProjectWorkflowConstants.WF_KEY_MFD_HELP_DESK);

		workflowLauncherContext = HtzLocationUtilities.createWorkflowLauncherContext(
			projectRecord,
			locationRecord,
			context.getSession(),
			repo,
			LocationProjectTypes.CLOSE_LOCATION,
			PROJECT_NAME_PREFIX);

		workflowLauncher.execute(workflowLauncherContext, false);

		//Launch the Order Fuel Cards if not Served By
		workflowLauncher = new AsynchronousLocationWorkflowLauncher(
			context,
			LocationProjectWorkflowConstants.WF_KEY_HANDLE_FUEL_CARDS);

		workflowLauncherContext = HtzLocationUtilities.createWorkflowLauncherContext(
			projectRecord,
			locationRecord,
			context.getSession(),
			repo,
			LocationProjectTypes.CLOSE_LOCATION,
			PROJECT_NAME_PREFIX);

		workflowLauncher.execute(workflowLauncherContext, false);

		//Launch the Publish to the Web if there are websites
		AdaptationTable locationWebsiteRelationshipTable = locationRecord.getContainer().getTable(
			LocationPaths._Root_LocationData_LocationWebsiteRelationship.getPathInSchema());

		String locationId = locationRecord.get(LocationPaths._Root_Location._Id).toString();

		// Query the Location/Location Relationship table for THIS Location of type "SERVEDBY"
		RequestResult requestResult = locationWebsiteRelationshipTable.createRequestResult(
			LocationPaths._Root_LocationData_LocationWebsiteRelationship._Location.format() + "='"
				+ locationId + "'");

		if (!requestResult.isEmpty())
		{
			requestResult.close();

			workflowLauncher = new AsynchronousLocationWorkflowLauncher(
				context,
				LocationProjectWorkflowConstants.WF_KEY_UPDATE_WEBSITES);

			workflowLauncherContext = HtzLocationUtilities.createWorkflowLauncherContext(
				projectRecord,
				locationRecord,
				context.getSession(),
				repo,
				LocationProjectTypes.CLOSE_LOCATION,
				PROJECT_NAME_PREFIX);

			workflowLauncher.execute(workflowLauncherContext, false);
		}

		//Launch the Reservation Delivery Workflow
		workflowLauncher = new AsynchronousLocationWorkflowLauncher(
			context,
			LocationProjectWorkflowConstants.WF_KEY_UPDATE_RESERVATION_DELIVERY);

		workflowLauncherContext = HtzLocationUtilities.createWorkflowLauncherContext(
			projectRecord,
			locationRecord,
			context.getSession(),
			repo,
			LocationProjectTypes.CLOSE_LOCATION,
			PROJECT_NAME_PREFIX);

		workflowLauncher.execute(workflowLauncherContext, false);

		//Launch the Treasury Workflow
		workflowLauncher = new AsynchronousLocationWorkflowLauncher(
			context,
			LocationProjectWorkflowConstants.WF_KEY_UPDATE_TREASURY);

		workflowLauncherContext = HtzLocationUtilities.createWorkflowLauncherContext(
			projectRecord,
			locationRecord,
			context.getSession(),
			repo,
			LocationProjectTypes.CLOSE_LOCATION,
			PROJECT_NAME_PREFIX);

		workflowLauncher.execute(workflowLauncherContext, false);

		//Launch the Update ResMaint Workflow
		workflowLauncher = new AsynchronousLocationWorkflowLauncher(
			context,
			LocationProjectWorkflowConstants.WF_KEY_UPDATE_RESMAINT);

		workflowLauncherContext = HtzLocationUtilities.createWorkflowLauncherContext(
			projectRecord,
			locationRecord,
			context.getSession(),
			repo,
			LocationProjectTypes.CLOSE_LOCATION,
			PROJECT_NAME_PREFIX);

		workflowLauncher.execute(workflowLauncherContext, false);

		//Launch the Update Rentworks Workflow
		workflowLauncher = new AsynchronousLocationWorkflowLauncher(
			context,
			LocationProjectWorkflowConstants.WF_KEY_UPDATE_RENT_WORKS);

		workflowLauncherContext = HtzLocationUtilities.createWorkflowLauncherContext(
			projectRecord,
			locationRecord,
			context.getSession(),
			repo,
			LocationProjectTypes.CLOSE_LOCATION,
			PROJECT_NAME_PREFIX);

		workflowLauncher.execute(workflowLauncherContext, false);

		//Launch the Update HLES Workflow
		workflowLauncher = new AsynchronousLocationWorkflowLauncher(
			context,
			LocationProjectWorkflowConstants.WF_KEY_UPDATE_HLES);

		workflowLauncherContext = HtzLocationUtilities.createWorkflowLauncherContext(
			projectRecord,
			locationRecord,
			context.getSession(),
			repo,
			LocationProjectTypes.CLOSE_LOCATION,
			PROJECT_NAME_PREFIX);

		workflowLauncher.execute(workflowLauncherContext, false);

		//Launch the Update RMS Workflow
		workflowLauncher = new AsynchronousLocationWorkflowLauncher(
			context,
			LocationProjectWorkflowConstants.WF_KEY_UPDATE_RMS);

		workflowLauncherContext = HtzLocationUtilities.createWorkflowLauncherContext(
			projectRecord,
			locationRecord,
			context.getSession(),
			repo,
			LocationProjectTypes.CLOSE_LOCATION,
			PROJECT_NAME_PREFIX);

		workflowLauncher.execute(workflowLauncherContext, false);
	}
}
