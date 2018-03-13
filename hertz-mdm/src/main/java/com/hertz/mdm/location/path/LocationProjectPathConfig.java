/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.path;

import com.hertz.mdm._hertz.path.HtzProjectPathConfig;
import com.hertz.mdm.admin.path.AdminPaths;
import com.hertz.mdm.location.constants.LocationConstants;
import com.hertz.mdm.location.enums.LocationProjectTypes;
import com.orchestranetworks.ps.project.path.SubjectPathConfig;
import com.orchestranetworks.schema.Path;

public class LocationProjectPathConfig extends HtzProjectPathConfig
{
	private static final Path[] LOCATION_PROJECT_PATHS_FOR_PROJECT_ROLE_PK = {
			LocationPaths._Root_LocationProject._ProjectType };
	private static LocationProjectPathConfig instance = null;

	public static synchronized LocationProjectPathConfig getInstance()
	{
		if (instance == null)
		{
			instance = new LocationProjectPathConfig();
		}
		return instance;
	}

	@Override
	public SubjectPathConfig getSubjectPathConfig(String projectType)
	{
		if (LocationProjectTypes.OPEN_LOCATION.equals(projectType)
			|| LocationProjectTypes.CLOSE_LOCATION.equals(projectType)
			|| LocationProjectTypes.MOVE_LOCATION.equals(projectType))
		{
			return LocationPathConfig.getInstance();
		}
		throw new IllegalArgumentException("ProjectType " + projectType + " not supported");
	}

	@Override
	public Path getProjectTablePath()
	{
		return LocationPaths._Root_LocationProject.getPathInSchema();
	}

	@Override
	public Path getProjectNameFieldPath()
	{
		return LocationPaths._Root_LocationProject._Name;
	}

	@Override
	public Path getProjectProjectTypeFieldPath()
	{
		return LocationPaths._Root_LocationProject._ProjectType;
	}

	@Override
	public Path getProjectBrandFieldPath()
	{
		//No Brand for Location
		return null;
	}

	@Override
	public Path getProjectRegionFieldPath()
	{
		//No Region for Location
		return null;
	}

	@Override
	public Path getProjectTeamMembersFieldPath()
	{
		return LocationPaths._Root_LocationProject._ProjectTeamMembers;
	}

	@Override
	public Path getProjectRoleTablePath()
	{
		return AdminPaths._Root_LocationProjectRole.getPathInSchema();
	}

	@Override
	public Path getProjectRoleRolesFieldPath()
	{
		return AdminPaths._Root_LocationProjectRole._Roles;
	}

	@Override
	public Path[] getProjectPathsForProjectRolePK()
	{
		return LOCATION_PROJECT_PATHS_FOR_PROJECT_ROLE_PK;
	}

	@Override
	public Path getProjectStatusFieldPath()
	{
		return LocationPaths._Root_LocationProject._ProjectStatus;
	}

	@Override
	public Path getProjectCancelReasonFieldPath()
	{
		return LocationPaths._Root_LocationProject._CancelReason;
	}

	@Override
	public Path getProjectProjectSubjectsFieldPath(String projectType)
	{
		/*
		if (ProductProjectTypes.isMenuItemProject(projectType))
		{
			return BkProductPaths._Root_ProductProject._ProjectMenuItemAssocation;
		}
		if (ProductProjectTypes.isOfferOrOtherItemProject(projectType))
		{
			return BkProductPaths._Root_ProductProject._ProjectOfferOrOtherItemAssocation;
		} */
		return null;
	}

	@Override
	public Path getProjectSubjectFieldPath(String projectType)
	{
		if (LocationProjectTypes.OPEN_LOCATION.equals(projectType))
		{
			return LocationPaths._Root_LocationProject._Location;
		}
		else
		{
			return null;
		}
		//	return ProductProjectTypes.COMMODITY_EXCHANGE.equals(projectType)
		//? BkProductPaths._Root_ProductProject._OriginalCommodity : null;
	}

	@Override
	public Path getProjectIsResumableFieldPath()
	{
		return null;
		//return BkProductPaths._Root_ProductProject.__isResumable;
	}

	@Override
	public Path getProjectTeamMemberTablePath()
	{
		return LocationPaths._Root_ProjectData_LocationProjectTeamMember.getPathInSchema();
	}

	@Override
	public Path getProjectTeamMemberProjectFieldPath()
	{
		return LocationPaths._Root_ProjectData_LocationProjectTeamMember._Project;
	}

	@Override
	public Path getProjectTeamMemberProjectRoleFieldPath()
	{
		return LocationPaths._Root_ProjectData_LocationProjectTeamMember._ProjectRole;
	}

	@Override
	public Path getProjectTeamMemberCalculatedProjectRoleNameFieldPath()
	{
		return LocationPaths._Root_ProjectData_LocationProjectTeamMember.__projectRoleName;
	}

	@Override
	public Path getProjectTeamMemberUserFieldPath()
	{
		return LocationPaths._Root_ProjectData_LocationProjectTeamMember._User;
	}

	@Override
	public Path getProjectTeamMemberCalculatedUserIdFieldPath()
	{
		return LocationPaths._Root_ProjectData_LocationProjectTeamMember.__userid;
	}

	@Override
	public String getCancelledProjectStatus(String projectType)
	{
		return null;
		//return ProductProjectStatuses.CANCELLED;
	}

	@Override
	public String getCompletedProjectStatus(String projectType)
	{
		return null;
		//return ProductProjectStatuses.LAUNCHED;
	}

	@Override
	public boolean isNewSubjectProjectType(String projectType)
	{
		return LocationProjectTypes.isNewLocationProjectType(projectType);
	}

	@Override
	public boolean isInProcessProjectStatus(String projectStatus)
	{
		return LocationConstants.isInProgess(projectStatus);
	}

	@Override
	public String getMasterDataSpaceName()
	{
		return LocationConstants.LOCATION_DATA_SPACE;
	}

	@Override
	public Path getProjectWorkflowEventTablePath()
	{
		return null;
		//return BkProductPaths._Root_ProjectDetails_ProductProjectWorkflowEvent.getPathInSchema();
	}

	@Override
	public Path getProjectWorkflowEventEventTypeFieldPath()
	{
		return null;
		//return BkProductPaths._Root_ProjectDetails_ProductProjectWorkflowEvent._EventType;
	}

	@Override
	public Path getProjectWorkflowEventProjectFieldPath()
	{
		return null;
		//return BkProductPaths._Root_ProjectDetails_ProductProjectWorkflowEvent._Project;
	}

	@Override
	public Path getProjectWorkflowEventCreateDateTimeFieldPath()
	{
		return null;
		//return BkProductPaths._Root_ProjectDetails_ProductProjectWorkflowEvent._CreateDateTime;
	}

	@Override
	public Path getProjectWorkflowEventCompleteDateTimeFieldPath()
	{
		return null;
		//return BkProductPaths._Root_ProjectDetails_ProductProjectWorkflowEvent._CompleteDateTime;
	}

	@Override
	public Path getProjectWorkflowEventWorkflowNameFieldPath()
	{
		return null;
		//return BkProductPaths._Root_ProjectDetails_ProductProjectWorkflowEvent.__workflowName;
	}

	@Override
	public Path getProjectWorkflowEventWorkflowLabelFieldPath()
	{
		return null;
		//return BkProductPaths._Root_ProjectDetails_ProductProjectWorkflowEvent._WorkflowLabel;
	}

	@Override
	public Path getProjectWorkflowEventWorkflowInstanceKeyFieldPath()
	{
		return null;
		//return BkProductPaths._Root_ProjectDetails_ProductProjectWorkflowEvent.__workflowInstanceKey;
	}

	@Override
	public Path getProjectWorkflowEventMasterWorkflowInstanceKeyFieldPath()
	{
		return null;
		//	return BkProductPaths._Root_ProjectDetails_ProductProjectWorkflowEvent.__masterWorkflowInstanceKey;
	}

	@Override
	public Path getProjectWorkflowEventWorkItemStepIdFieldPath()
	{
		return null;
		//	return BkProductPaths._Root_ProjectDetails_ProductProjectWorkflowEvent._WorkItemInformation__workItemStepId;
	}
	@Override
	public Path getProjectWorkflowEventWorkItemLabelFieldPath()
	{
		return null;
		//	return BkProductPaths._Root_ProjectDetails_ProductProjectWorkflowEvent._WorkItemInformation_WorkItemLabel;
	}

	@Override
	public Path getProjectWorkflowEventWorkItemInstanceKeyFieldPath()
	{
		return null;
		//return BkProductPaths._Root_ProjectDetails_ProductProjectWorkflowEvent._WorkItemInformation__workItemInstanceKey;
	}

	@Override
	public Path getProjectWorkflowEventWorkItemRoleFieldPath()
	{
		return null;
		//return BkProductPaths._Root_ProjectDetails_ProductProjectWorkflowEvent._WorkItemInformation_WorkItemRole;
	}

	@Override
	public Path getProjectWorkflowEventWorkItemUserFieldPath()
	{
		return null;
		//return BkProductPaths._Root_ProjectDetails_ProductProjectWorkflowEvent._WorkItemInformation_WorkItemUser;
	}

	@Override
	public Path getProjectWorkflowEventWorkItemIsAcceptedFieldPath()
	{
		return null;
		//return BkProductPaths._Root_ProjectDetails_ProductProjectWorkflowEvent._WorkItemInformation_IsAccepted;
	}

	private LocationProjectPathConfig()
	{
	}
}
