package com.hertz.mdm.location.valuefunction;

import com.hertz.mdm.location.path.LocationPaths;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.RequestResult;
import com.orchestranetworks.schema.ValueFunction;
import com.orchestranetworks.schema.ValueFunctionContext;

public class AreaLocationValueFunction implements ValueFunction
{
	@Override
	public Object getValue(Adaptation adaptation)
	{
		Adaptation areaRecord = adaptation;
		String areaRecordId = areaRecord.get(LocationPaths._Root_Area._Base_Id).toString();
		String locationRecordId = null;

		AdaptationTable locationTable = areaRecord.getContainer()
			.getTable(LocationPaths._Root_Location.getPathInSchema());

		// Query the LocationHierarchy table for the Revenue Area parent
		RequestResult requestResult = locationTable.createRequestResult(
			LocationPaths._Root_Location._LocationInformation_Area.format() + "='" + areaRecordId
				+ "'");

		if (!requestResult.isEmpty())
		{
			locationRecordId = requestResult.nextAdaptation()
				.get(LocationPaths._Root_Location._Id)
				.toString();
		}

		if (requestResult != null)
		{
			requestResult.close();
		}
		return locationRecordId;
	}

	@Override
	public void setup(ValueFunctionContext aContext)
	{
		// TODO Auto-generated method stub
	}
}
