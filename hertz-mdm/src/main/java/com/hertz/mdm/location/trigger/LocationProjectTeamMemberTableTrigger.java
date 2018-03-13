/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.trigger;

import com.hertz.mdm.location.path.LocationProjectPathConfig;
import com.orchestranetworks.ps.project.path.ProjectPathConfig;
import com.orchestranetworks.ps.project.trigger.ProjectTeamMemberTableTrigger;

/**
 */
public class LocationProjectTeamMemberTableTrigger extends ProjectTeamMemberTableTrigger
{
	@Override
	public ProjectPathConfig getProjectPathConfig()
	{
		return LocationProjectPathConfig.getInstance();
	}
}
