/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.vehicle.path;

import com.hertz.mdm.common.tablereffilter.HtzSubjectPathConfig;
import com.orchestranetworks.schema.Path;

public class VehiclePathConfig implements HtzSubjectPathConfig
{
	private static VehiclePathConfig instance = null;

	public static synchronized VehiclePathConfig getInstance()
	{
		if (instance == null)
		{
			instance = new VehiclePathConfig();
		}
		return instance;
	}

	@Override
	public Path getProjectSubjectTablePath()
	{
		return VehiclePaths._Root_Vehicle.getPathInSchema();
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
		return VehiclePaths._Root_Vehicle.getPathInSchema();
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
		return null;
		//return VehiclePaths._Root_Vehicle._Name;
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

	private VehiclePathConfig()
	{
	}
}
