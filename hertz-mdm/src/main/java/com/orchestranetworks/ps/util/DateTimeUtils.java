package com.orchestranetworks.ps.util;

import java.text.*;
import java.util.*;

import com.orchestranetworks.ps.constants.*;

public class DateTimeUtils
{
	/**
	 * Compares a Date's time to that of the String passed in.
	 * <p>
	 * The String passed in should be in the format:
	 * <p>
	 *<ul><li><code>HH:mm:ss</code></li></ul>
	 *
	 * @param date	Instance of Date whose time value will be compared against the string passed in.
	 * @param time	Instance of String representing the time to use for the comparison.
	 */
	public static boolean isDateTimeEqualToTime(Date date, String time)
	{
		boolean isEqual = false;

		SimpleDateFormat parser = new SimpleDateFormat(CommonConstants.EBX_TIME_FORMAT);

		String timeToCompare = parser.format(date);

		isEqual = timeToCompare.equalsIgnoreCase(time);

		return isEqual;
	}

	/**
	 * Compares only the time setting (HH:mm:ss) of the two Dates passed in. 
	 * <p>
	 * <strong>NOTE:</strong> This method only compares the time between the two Dates. Not the actual calendar day.
	 * <p>
	 * @param fisrtDateTime Instance of Date
	 * @param secondDateTime Instance of Date
	 * 
	 * @return True if the Time of the firstDateTime is equal to the secondDateTime. 
	 */
	public static boolean isTimeEqual(Date firstDateTime, Date secondDateTime)
	{

		boolean isEqual = false;

		SimpleDateFormat parser = new SimpleDateFormat(CommonConstants.EBX_TIME_FORMAT);

		if (firstDateTime != null && secondDateTime != null)
		{
			String time1 = parser.format(firstDateTime);
			String time2 = parser.format(secondDateTime);

			isEqual = time1.equalsIgnoreCase(time2);
		}

		return isEqual;
	}

}
