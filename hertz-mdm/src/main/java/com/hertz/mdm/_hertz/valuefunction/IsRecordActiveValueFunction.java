package com.hertz.mdm._hertz.valuefunction;

import java.util.Date;

import com.hertz.mdm.common.path.CommonReferencePaths;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.ValueFunction;
import com.orchestranetworks.schema.ValueFunctionContext;

public class IsRecordActiveValueFunction implements ValueFunction
{
	@Override
	public Object getValue(Adaptation adaptation)
	{
		Date todayDate = new Date();

		Adaptation anyRecord = adaptation;

		//This technique of using an arbitrary Path works because the path appears on all of the records the same way
		if (anyRecord
			.get(CommonReferencePaths._Root_AddressType._EffectiveStartEndDates_StartDate) == null)
		{
			return false;
		}
		else
		{
			Date startDate = (Date) anyRecord
				.get(CommonReferencePaths._Root_AddressType._EffectiveStartEndDates_StartDate);

			if (todayDate.before(startDate))
			{
				return false;
			}
		}

		if (anyRecord
			.get(CommonReferencePaths._Root_AddressType._EffectiveStartEndDates_EndDate) == null)
		{
			return true;
		}
		else
		{
			Date endDate = (Date) anyRecord
				.get(CommonReferencePaths._Root_AddressType._EffectiveStartEndDates_EndDate);

			if (!todayDate.before(endDate))
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public void setup(ValueFunctionContext aContext)
	{
		// TODO Auto-generated method stub
	}
}
