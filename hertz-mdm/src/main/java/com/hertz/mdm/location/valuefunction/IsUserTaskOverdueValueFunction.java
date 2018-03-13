package com.hertz.mdm.location.valuefunction;

import java.util.Calendar;
import java.util.Date;

import com.hertz.mdm.location.path.LocationPaths;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.ValueFunction;
import com.orchestranetworks.schema.ValueFunctionContext;

public class IsUserTaskOverdueValueFunction implements ValueFunction
{
	@Override
	public Object getValue(Adaptation adaptation)
	{
		Adaptation locationProjectUserTaskLogRecord = adaptation;

		Date locationProjectUserTaskStartDate = (Date) locationProjectUserTaskLogRecord.get(
			LocationPaths._Root_ProjectData_ProjectUserTaskLog._TaskStartEndTimestamp_StartDateTime);

		if (locationProjectUserTaskStartDate == null)
		{
			return true;
		}

		Date compareDate = null;
		Date locationProjectUserTaskEndDate = (Date) locationProjectUserTaskLogRecord.get(
			LocationPaths._Root_ProjectData_ProjectUserTaskLog._TaskStartEndTimestamp_EndDateTime);

		if (locationProjectUserTaskEndDate != null)
		{
			compareDate = locationProjectUserTaskEndDate;
		}
		else
		{
			compareDate = new Date();
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(locationProjectUserTaskStartDate);
		calendar.add(Calendar.DATE, 1);
		locationProjectUserTaskStartDate = calendar.getTime();

		return compareDate.after(locationProjectUserTaskStartDate);
	}

	@Override
	public void setup(ValueFunctionContext aContext)
	{
		// TODO Auto-generated method stub
	}
}
