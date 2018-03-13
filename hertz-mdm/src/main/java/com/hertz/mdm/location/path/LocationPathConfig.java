/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.path;

import com.hertz.mdm.common.tablereffilter.HtzSubjectPathConfig;
import com.hertz.mdm.location.enums.LocationProjectTypes;
import com.orchestranetworks.schema.Path;

public class LocationPathConfig implements HtzSubjectPathConfig
{
	private static LocationPathConfig instance = null;

	public static synchronized LocationPathConfig getInstance()
	{
		if (instance == null)
		{
			instance = new LocationPathConfig();
		}
		return instance;
	}

	@Override
	public Path getProjectSubjectTablePath()
	{
		return LocationPaths._Root_Location.getPathInSchema();
	}

	@Override
	public Path getProjectSubjectSubjectFieldPath()
	{
		return LocationPaths._Root_Location.getPathInSchema();
	}

	@Override
	public Path getProjectSubjectProjectFieldPath()
	{
		return LocationPaths._Root_LocationProject.getPathInSchema();
	}

	@Override
	public String getNewSubjectProjectType()
	{
		return LocationProjectTypes.OPEN_LOCATION;
	}

	@Override
	public Path getSubjectTablePath()
	{
		return LocationPaths._Root_Location.getPathInSchema();
	}

	@Override
	public Path getSubjectCurrentProjectTypeFieldPath()
	{
		return LocationPaths._Root_Location._CurrentProjectType;
	}

	@Override
	public Path getSubjectStatusFieldPath()
	{
		return LocationPaths._Root_LocationProject._ProjectStatus;
	}

	@Override
	public Path getSubjectNameFieldPath()
	{
		return LocationPaths._Root_Location._Name;
	}

	@Override
	public Path getSubjectProjectSubjectsFieldPath()
	{
		return LocationPaths._Root_LocationProject.getPathInSchema();
	}

	@Override
	public Path getSubjectProjectsFieldPath()
	{
		return LocationPaths._Root_Location._LocationInformation_LocationProjects_LocationProjects;
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

	private LocationPathConfig()
	{
	}
}
