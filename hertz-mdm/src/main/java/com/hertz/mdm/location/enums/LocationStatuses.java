/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.enums;

/**
 */
public class LocationStatuses extends Object
{
	public static final String OPEN = "Open";
	public static final String CLOSED = "Closed";
	public static final String IN_PROGRESS = "In-Progress";

	public static boolean isActive(String locationStatus)
	{
		return (LocationStatuses.OPEN.equals(locationStatus));
	}
}
