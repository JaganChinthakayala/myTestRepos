/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.constants;

import com.orchestranetworks.service.*;

/**
 * Some common constants used in other ps-library classes, such as tech-admin user role, date/time formats, etc.
 */
public class CommonConstants
{
	public static final String ROLE_TECH_ADMIN = "Tech Admin";
	public static final Role TECH_ADMIN = Role.forSpecificRole(ROLE_TECH_ADMIN);

	public static final String EBX_DATE_FORMAT = "yyyy-MM-dd";
	public static final String EBX_TIME_FORMAT = "HH:mm:ss";
	public static final String EBX_DATE_TIME_FORMAT = EBX_DATE_FORMAT + "'T'" + EBX_TIME_FORMAT;

	public static final String DATA_SPACE_NAME_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

	// For now, we only need local but we could add environments for development, etc
	public static final String ENVIRONMENT_MODE_LOCAL = "local";

	protected CommonConstants()
	{
	}
}
