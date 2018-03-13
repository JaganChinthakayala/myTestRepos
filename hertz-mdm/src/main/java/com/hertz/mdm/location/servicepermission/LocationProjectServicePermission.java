/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.servicepermission;

import com.hertz.mdm.location.path.LocationProjectPathConfig;
import com.orchestranetworks.ps.project.path.ProjectPathConfig;
import com.orchestranetworks.ps.project.servicepermission.ProjectServicePermission;

/**
 */
public class LocationProjectServicePermission extends ProjectServicePermission
{
	@Override
	public ProjectPathConfig getProjectPathConfig()
	{
		return LocationProjectPathConfig.getInstance();
	}
}
