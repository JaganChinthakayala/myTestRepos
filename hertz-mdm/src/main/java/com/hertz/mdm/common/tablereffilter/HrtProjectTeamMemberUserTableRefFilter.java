/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.common.tablereffilter;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.ps.project.tablereffilter.ProjectTeamMemberUserTableRefFilter;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.service.Role;
import com.orchestranetworks.service.UserReference;

/**
 */
public abstract class HrtProjectTeamMemberUserTableRefFilter
	extends
	ProjectTeamMemberUserTableRefFilter
{
	private static final String MESSAGE = "User must be in the specified role for the project's brand and region.";

	@Override
	protected boolean isUserInRole(ValueContext valueContext, UserReference user, Role role)
	{
		HtzProjectPathConfig pathConfig = (HtzProjectPathConfig) getProjectPathConfig();
		// This assumes the project field is always at same level as user field, but that's a pretty safe assumption
		Adaptation projectRecord = AdaptationUtil.followFK(
			valueContext,
			Path.PARENT.add(pathConfig.getProjectTeamMemberProjectFieldPath()));
		if (projectRecord == null)
		{
			return true;
		}
		Path brandPath = pathConfig.getProjectBrandFieldPath();
		Path regionPath = pathConfig.getProjectRegionFieldPath();
		// Not everything is brand-enabled yet so for those, just check if in role
		if (brandPath == null || regionPath == null)
		{
			return super.isUserInRole(valueContext, user, role);
		}
		String brand = projectRecord.getString(brandPath);
		if (brand == null)
		{
			return true;
		}
		String region = projectRecord.getString(regionPath);
		if (region == null)
		{
			return true;
		}
		/*
				Role compoundRole = RbiBrandRegionCompoundDirectoryUtils.formatCompoundRole(
					valueContext.getHome().getRepository(),
					role.getRoleName(),
					brand,
					region);
				if (super.isUserInRole(valueContext, user, compoundRole))
				{
					return true;
				}
				if (BkReferenceConstants.REGION_GLOBAL.equals(region))
				{
					return false;
				}
				compoundRole = RbiBrandRegionCompoundDirectoryUtils.formatCompoundRole(
					valueContext.getHome().getRepository(),
					role.getRoleName(),
					brand,
					BkReferenceConstants.REGION_GLOBAL);
				return super.isUserInRole(valueContext, user, compoundRole);
				*/
		return true;
	}

	@Override
	protected String createMessage()
	{
		// Not everything is brand-enabled yet so only return the specialized message for those that are
		Path brandPath = ((HtzProjectPathConfig) getProjectPathConfig()).getProjectBrandFieldPath();
		return brandPath == null ? super.createMessage() : MESSAGE;
	}
}
