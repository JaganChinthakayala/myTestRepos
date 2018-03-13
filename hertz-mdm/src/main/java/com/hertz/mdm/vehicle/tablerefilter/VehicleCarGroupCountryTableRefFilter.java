/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.vehicle.tablerefilter;

import java.util.*;

import com.hertz.mdm._hertz.path.HtzProjectPathConfig;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.ps.project.tablereffilter.ProjectTeamMemberUserTableRefFilter;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.service.Role;
import com.orchestranetworks.service.UserReference;

/**
 */
public class VehicleCarGroupCountryTableRefFilter
implements TableRefFilter
{
 @Override
 public boolean accept(Adaptation record, ValueContext context)
 { return true;

}

@Override
public void setup(TableRefFilterContext context) {
	// TODO Auto-generated method stub
	
}

@Override
public String toUserDocumentation(Locale userLocale, ValueContext aContext) throws InvalidSchemaException {
	// TODO Auto-generated method stub
	return null;
}

}
