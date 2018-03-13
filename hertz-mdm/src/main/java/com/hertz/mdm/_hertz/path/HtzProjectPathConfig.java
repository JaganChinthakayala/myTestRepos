/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm._hertz.path;

import com.hertz.mdm.location.util.HtzLocationUtilities;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.ps.project.path.ProjectPathConfig;
import com.orchestranetworks.schema.Path;

/**
 * Specifies project related paths
 */
public abstract class HtzProjectPathConfig implements ProjectPathConfig
{
	public abstract Path getProjectBrandFieldPath();

	public abstract Path getProjectRegionFieldPath();

	@Override
	public Adaptation getAdminDataSet(Repository repo)
	{
		return HtzLocationUtilities.getAdminDataSet(repo);
	}

	@Override
	public boolean isProjectTeamMemberDeletionAllowed()
	{
		return false;
	}
}
