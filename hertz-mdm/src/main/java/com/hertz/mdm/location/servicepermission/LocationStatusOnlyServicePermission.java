package com.hertz.mdm.location.servicepermission;

import com.hertz.mdm.location.enums.LocationStatuses;
import com.hertz.mdm.location.path.LocationPathConfig;
import com.hertz.mdm.location.path.LocationPaths;
import com.hertz.mdm.location.path.LocationProjectPathConfig;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.ps.project.path.ProjectPathConfig;
import com.orchestranetworks.ps.project.path.SubjectPathConfig;
import com.orchestranetworks.ps.project.servicepermission.SubjectStatusOnlyServicePermission;
import com.orchestranetworks.service.ActionPermission;
import com.orchestranetworks.service.Session;

public class LocationStatusOnlyServicePermission extends SubjectStatusOnlyServicePermission
{
	@Override
	public ProjectPathConfig getProjectPathConfig()
	{
		return LocationProjectPathConfig.getInstance();
	}

	@Override
	protected SubjectPathConfig getSubjectPathConfig(Adaptation adaptation)
	{
		return LocationPathConfig.getInstance();
	}

	@Override
	protected ActionPermission verifySubjectNotInWorkflow(Adaptation adaptation, Session session)
	{
		ActionPermission permission = super.verifySubjectNotInWorkflow(adaptation, session);
		if (ActionPermission.getEnabled().equals(permission))
		{
			String currentLocationStatus = adaptation
				.getString(LocationPaths._Root_Location._Status);
			if (!LocationStatuses.OPEN.equals(currentLocationStatus))
			{
				return ActionPermission.getHidden(
					UserMessage.createError(
						"This Location can not be Closed due to its curret status: "
							+ currentLocationStatus));
			}
		}
		return permission;
	}
}