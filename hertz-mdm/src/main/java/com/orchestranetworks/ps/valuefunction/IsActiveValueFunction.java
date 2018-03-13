package com.orchestranetworks.ps.valuefunction;

import java.util.*;

import com.onwbp.adaptation.*;
import com.orchestranetworks.ps.ranges.*;
import com.orchestranetworks.ps.util.*;
import com.orchestranetworks.schema.*;

/**
 * This valueFunction currently assumes inclusive on the end points is Active.
 * If startDate and endDate are null, we assume active.
 * If startDate is not null and today is before startDate, then not active.
 * If endDate is not null and today is after endDate, then not active. 
 */
public class IsActiveValueFunction implements ValueFunction
{
	private Path startDatePath;
	private Path endDatePath;
	private boolean date;

	public Path getStartDatePath()
	{
		return startDatePath;
	}

	public void setStartDatePath(Path startDatePath)
	{
		this.startDatePath = startDatePath;
	}

	public Path getEndDatePath()
	{
		return endDatePath;
	}

	public void setEndDatePath(Path endDatePath)
	{
		this.endDatePath = endDatePath;
	}

	@Override
	public Object getValue(Adaptation context)
	{
		return isActive(context, startDatePath, endDatePath, date);
	}

	public static Boolean isActive(
		Adaptation record,
		Path startDatePath,
		Path endDatePath,
		boolean date)
	{
		Date currentDate = new Date();
		Date startDate = (Date) record.get(startDatePath);
		Date endDate = (Date) record.get(endDatePath);
		if (date)
		{
			endDate = (Date) RangeUtils.padMax(endDate, RangeUtils.ONE_DAY_IN_MILLIS);
		}

		if (startDate != null && currentDate.before(startDate))
		{
			return Boolean.FALSE;
		}
		if (endDate != null && currentDate.after(endDate))
		{
			return Boolean.FALSE;
		}

		return Boolean.TRUE;
	}

	@Override
	public void setup(ValueFunctionContext context)
	{
		setup(context, startDatePath, "startDatePath");
		SchemaTypeName type = setup(context, endDatePath, "endDatePath");
		date = SchemaTypeName.XS_DATE.equals(type);
	}

	private SchemaTypeName setup(ValueFunctionContext context, Path datePath, String datePathName)
	{
		SchemaNode dateNode = PathUtils.setupFieldNode(context, datePath, datePathName, false);
		if (dateNode == null)
			return null; //errors already reported
		SchemaTypeName type = dateNode.getXsTypeName();
		if (!(SchemaTypeName.XS_DATE.equals(type) || SchemaTypeName.XS_DATETIME.equals(type)))
		{
			context.addError("Node " + datePath.format() + " must be of type Date or DateTime.");
		}
		return type;
	}
}
