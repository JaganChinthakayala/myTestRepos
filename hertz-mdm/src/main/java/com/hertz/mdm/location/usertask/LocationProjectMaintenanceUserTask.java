/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.usertask;

import java.util.HashMap;

import com.hertz.mdm._hertz.constants.HtzConstants;
import com.hertz.mdm._hertz.constants.HtzWorkflowConstants;
import com.hertz.mdm._hertz.enums.Domains;
import com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.repository.RepositoryUtils;
import com.hertz.mdm._hertz.usertask.HtzProjectMaintenanceUserTask;
import com.hertz.mdm.businessparty.path.BusinessPartyPaths;
import com.hertz.mdm.location.enums.LocationProjectTypes;
import com.hertz.mdm.location.path.LocationPaths;
import com.hertz.mdm.location.path.LocationProjectPathConfig;
import com.hertz.mdm.location.util.HtzLocationUtilities;
import com.hertz.mdm.location.workflow.LocationProjectWorkflowUtilities;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.RequestResult;
import com.orchestranetworks.ps.procedure.ModifyValuesProcedure;
import com.orchestranetworks.ps.project.path.ProjectPathConfig;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.Session;
import com.orchestranetworks.workflow.UserTaskCreationContext;
import com.orchestranetworks.workflow.UserTaskWorkItemCompletionContext;
/**
 */
public class LocationProjectMaintenanceUserTask extends HtzProjectMaintenanceUserTask
{
	@Override
	public ProjectPathConfig getProjectPathConfig()
	{
		return LocationProjectPathConfig.getInstance();
	}

	@Override
	protected String getDomain()
	{
		return Domains.LOCATION;
	}

	@Override
	public void handleCreate(UserTaskCreationContext context) throws OperationException
	{
		super.handleCreate(context);

		Session session = context.getSession();

		Adaptation projectRecord = LocationProjectWorkflowUtilities
			.getLocationProjectRecord(context, context.getRepository());

		HtzLocationUtilities
			.createLocationProjectUserTaskLog(projectRecord, session, context, workflowTask);
	}

	@Override
	public void handleWorkItemCompletion(UserTaskWorkItemCompletionContext context)
		throws OperationException
	{
		//Clear the Launched_Location_Id 
		context.setVariableString(HtzWorkflowConstants.DATA_CONTEXT_LAUNCH_LOCATION_ID, null);

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

		if (context.getCompletedWorkItem().isAccepted())
		{
			//If the ProjectOrignator has not been set, then set it to the CurrentUserId from the Data Context
			if (projectRecord.get(LocationPaths._Root_LocationProject._ProjectOriginator) == null)
			{
				String currentUserName = context
					.getVariableString(HtzWorkflowConstants.DATA_CONTEXT_PARM_CURRENT_USER_ID);

				AdaptationTable businessPartyTable = RepositoryUtils.getTable(
					RepositoryUtils.getDataSet(
						HtzConstants.BUSINESS_PARTY_DATA_SPACE,
						HtzConstants.BUSINESS_PARTY_DATA_SET),
					BusinessPartyPaths._Root_BusinessParty.getPathInSchema().format());

				// Query the Business Party Table to get this User's User Id
				RequestResult requestResult = businessPartyTable.createRequestResult(
					BusinessPartyPaths._Root_BusinessParty._EbxUser.format() + "='"
						+ currentUserName + "' and "
						+ BusinessPartyPaths._Root_BusinessParty._IsRecordActive.format()
						+ "=true");

				if (!requestResult.isEmpty())
				{
					Adaptation businessParty = requestResult.nextAdaptation();
					String currentUserId = businessParty
						.get(BusinessPartyPaths._Root_BusinessParty._Id).toString();
					HashMap<Path, Object> pathValueMap = new HashMap<Path, Object>();
					pathValueMap
						.put(LocationPaths._Root_LocationProject._ProjectOriginator, currentUserId);
					ModifyValuesProcedure
						.execute(projectRecord, pathValueMap, context.getSession(), true, false);
				}
			}

			String projectType = projectRecord
				.getString(LocationPaths._Root_LocationProject._ProjectType);

			if (LocationProjectTypes.OPEN_LOCATION.equals(projectType))
			{
				String region = locationRecord
					.getString(LocationPaths._Root_Location._LocationInformation_Region);

				if (region != null)
				{
					com.hertz.mdm.location.util.HtzLocationUtilities
						.updateProjectTeamRoleWithRegionUsers(
							context.getSession(),
							projectRecord,
							region);
				}

				/*
				//Write the Area to the Parents Hierarchy Table if it was entered
				if (locationRecord.get(LocationPaths._Root_Location._Area) != null)
				{
					String locationId = locationRecord.get(LocationPaths._Root_Location._Id)
						.toString();
					String areaId = locationRecord.get(LocationPaths._Root_Location._Area)
						.toString();
					AdaptationTable locationAreaRelationshipTable = projectRecord.getContainer()
						.getTable(
							LocationPaths._Root_LocationData_LocationAreaRelationship
								.getPathInSchema());
				
					// Query the Location/Area table for this Location and Area
					RequestResult requestResult = locationAreaRelationshipTable.createRequestResult(
						LocationPaths._Root_LocationData_LocationAreaRelationship._Location.format()
							+ "='" + locationId + "' and "
							+ LocationPaths._Root_LocationData_LocationAreaRelationship._Area
								.format()
							+ "='" + areaId + "'");
				
					if (requestResult.isEmpty())
					{
						Map<Path, Object> pathValueMap = new HashMap<Path, Object>();
				
						pathValueMap.put(
							LocationPaths._Root_LocationData_LocationAreaRelationship._Location,
							locationId);
						pathValueMap.put(
							LocationPaths._Root_LocationData_LocationAreaRelationship._Area,
							areaId);
				
						CreateRecordProcedure.execute(
							locationAreaRelationshipTable,
							pathValueMap,
							context.getSession(),
							true,
							false);
					}
				}*/
			}
		}

		super.handleWorkItemCompletion(context);

		String allocatedUser = "";

		allocatedUser = HtzLocationUtilities.getAllocatedUserForOfferedRole(
			projectRecord,
			HtzLocationUtilities.getOfferedToRoleForWorkflowTask(workflowTask));

		if (allocatedUser.equals(""))
		{
			allocatedUser = context
				.getVariableString(HtzWorkflowConstants.DATA_CONTEXT_APPROVAL_ROLES);
		}

		try
		{
			HtzLocationUtilities.updateLocationProjectUserTaskLog(
				projectRecord,
				context.getSession(),
				context
					.getVariableString(HtzWorkflowConstants.DATA_CONTEXT_PROJECT_USER_TASK_LOG_ID),
				allocatedUser);
		}
		catch (OperationException ex)
		{
			throw new RuntimeException(ex);
		}

		HtzLocationUtilities.updateLocationProjectUserTaskLog(
			projectRecord,
			context.getSession(),
			context.getVariableString(HtzWorkflowConstants.DATA_CONTEXT_PROJECT_USER_TASK_LOG_ID),
			null);
	}
}
