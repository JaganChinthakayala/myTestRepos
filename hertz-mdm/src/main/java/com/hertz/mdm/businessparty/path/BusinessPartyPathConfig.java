/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.businessparty.path;

import com.hertz.mdm.common.tablereffilter.HtzSubjectPathConfig;
import com.orchestranetworks.schema.Path;

public class BusinessPartyPathConfig implements HtzSubjectPathConfig
{
	private static BusinessPartyPathConfig instance = null;

	public static synchronized BusinessPartyPathConfig getInstance()
	{
		if (instance == null)
		{
			instance = new BusinessPartyPathConfig();
		}
		return instance;
	}

	@Override
	public Path getProjectSubjectTablePath()
	{
		return BusinessPartyPaths._Root_BusinessParty.getPathInSchema();
	}

	@Override
	public Path getProjectSubjectSubjectFieldPath()
	{
		return null;
	}

	@Override
	public Path getProjectSubjectProjectFieldPath()
	{
		return null;
	}

	@Override
	public String getNewSubjectProjectType()
	{
		return null;
	}

	@Override
	public Path getSubjectTablePath()
	{
		return BusinessPartyPaths._Root_BusinessParty.getPathInSchema();
	}

	@Override
	public Path getSubjectCurrentProjectTypeFieldPath()
	{
		return null;
	}

	@Override
	public Path getSubjectStatusFieldPath()
	{
		return null;
	}

	@Override
	public Path getSubjectNameFieldPath()
	{
		return BusinessPartyPaths._Root_BusinessParty._Name;
	}

	@Override
	public Path getSubjectProjectSubjectsFieldPath()
	{
		return null;
	}

	@Override
	public Path getSubjectProjectsFieldPath()
	{
		return null;
	}

	@Override
	public Path getProjectSubjectBrandFieldPath()
	{
		return null;
	}

	@Override
	public Path getSubjectBrandFieldPath()
	{
		return null;
	}

	@Override
	public Path getProjectSubjectRegionFieldPath()
	{
		return null;
	}

	@Override
	public Path getSubjectRegionFieldPath()
	{
		return null;
	}

	private BusinessPartyPathConfig()
	{
	}
}
