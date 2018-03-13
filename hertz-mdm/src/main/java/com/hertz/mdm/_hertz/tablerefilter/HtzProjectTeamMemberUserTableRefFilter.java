/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm._hertz.tablerefilter;

import com.hertz.mdm._hertz.path.HtzProjectPathConfig;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.ps.project.tablereffilter.ProjectTeamMemberUserTableRefFilter;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.service.Role;
import com.orchestranetworks.service.UserReference;

/**
 */
public abstract class HtzProjectTeamMemberUserTableRefFilter
	extends
	ProjectTeamMemberUserTableRefFilter
{
	private static final String MESSAGE = "User must be in the specified role for the project's brand and region.";

	@Override
	protected boolean isUserInRole(ValueContext valueContext, UserReference user, Role role)
	{
		HtzProjectPathConfig pathConfig = (HtzProjectPathConfig) getProjectPathConfig();

		Adaptation projectRecord = AdaptationUtil.followFK(
			valueContext,
			Path.PARENT.add(pathConfig.getProjectTeamMemberProjectFieldPath()));

		if (projectRecord == null)
		{
			return true;
		}

		if (super.isUserInRole(valueContext, user, role))
		{
			return true;
		}
		return false;
	}

	@Override
	protected String createMessage()
	{
		// Not everything is brand-enabled yet so only return the specialized message for those that are
		Path brandPath = ((HtzProjectPathConfig) getProjectPathConfig()).getProjectBrandFieldPath();
		return brandPath == null ? super.createMessage() : MESSAGE;
	}
}
