/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.project.tablereffilter;

import java.util.*;

import com.onwbp.adaptation.*;
import com.orchestranetworks.instance.*;
import com.orchestranetworks.ps.project.path.*;
import com.orchestranetworks.ps.util.*;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.service.*;

/**
 * Show only roles that are associated with a project.
 * This should be placed on a foreign key to the EBX Roles table.
 */
public abstract class ProjectRoleTableRefFilter implements TableRefFilter, ProjectPathCapable
{
	private static final String MESSAGE = "Only roles that belong to the project are allowed.";

	protected abstract Adaptation lookupProjectRole(Adaptation projectRecord)
		throws OperationException;

	@Override
	public boolean accept(Adaptation adaptation, ValueContext context)
	{
		ProjectPathConfig pathConfig = getProjectPathConfig();
		String name = adaptation.getString(AdminUtil.getDirectoryRolesNamePath());

		Adaptation projectRecord = AdaptationUtil.followFK(
			context,
			Path.PARENT.add(pathConfig.getProjectTeamMemberProjectFieldPath()));
		if (projectRecord == null)
		{
			return false;
		}
		Adaptation projectRole;
		try
		{
			projectRole = lookupProjectRole(projectRecord);
		}
		catch (OperationException ex)
		{
			return false;
		}

		@SuppressWarnings("unchecked")
		List<String> roles = projectRole.getList(pathConfig.getProjectRoleRolesFieldPath());
		return roles.contains(name);
	}

	@Override
	public void setup(TableRefFilterContext context)
	{
		context.addFilterErrorMessage(MESSAGE);
	}

	@Override
	public String toUserDocumentation(Locale locale, ValueContext context)
		throws InvalidSchemaException
	{
		return MESSAGE;
	}
}
