package com.hertz.mdm.location.tablerefilter;

import com.hertz.mdm._hertz.tablerefilter.HtzProjectTeamMemberUserTableRefFilter;
import com.hertz.mdm.location.path.LocationProjectPathConfig;

public class LocationProjectTeamMemberUserTableRefFilter
	extends
	HtzProjectTeamMemberUserTableRefFilter
{
	@Override
	public LocationProjectPathConfig getProjectPathConfig()
	{
		return LocationProjectPathConfig.getInstance();
	}
}
