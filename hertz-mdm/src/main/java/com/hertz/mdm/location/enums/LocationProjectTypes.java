/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.enums;

/**
 */
public class LocationProjectTypes extends Object
{
	public static final String OPEN_LOCATION = "Open Location";
	public static final String CLOSE_LOCATION = "Close Location";
	public static final String MOVE_LOCATION = "Move Location";
	public static final String MAINTAIN_LOCATION = "Maintain Location";

	public static boolean isNewLocationProjectType(String projectType)
	{
		return OPEN_LOCATION.equals(projectType);
	}
}
