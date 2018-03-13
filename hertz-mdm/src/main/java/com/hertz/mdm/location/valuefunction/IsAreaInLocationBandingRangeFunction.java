package com.hertz.mdm.location.valuefunction;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.ValueFunction;
import com.orchestranetworks.schema.ValueFunctionContext;

public class IsAreaInLocationBandingRangeFunction implements ValueFunction

{
	@Override
	public Object getValue(Adaptation adaptation)
	{
		/*		Adaptation areaRecord = adaptation;
		
				if (HtzLocationUtilities
					.isAreaAssignedToAnotherLocation(context, areaRecord, "LocationDataSet"))
				{
					return false;
				}
		
				String locationId = (String) context.getValue(LOCATION_ID_PATH);
		
				AdaptationTable locationTable = context.getAdaptationInstance()
					.getTable(LocationPaths._Root_Location.getPathInSchema());
		
				// Query the Location table to get the location of this project
				RequestResult requestResult = locationTable.createRequestResult(
					LocationPaths._Root_Location._Id.format() + "='" + locationId + "'");
		
				if (requestResult.isEmpty())
				{
					requestResult.close();
					return false;
				}
		
				Adaptation locationRecord = requestResult.nextAdaptation();
		
				String areaId = areaRecord.getString(LocationPaths._Root_Area._Base_Id);
		
				//Area must be between the lowest and highest band range
				if (areaId
					.compareTo(locationRecord.getString(LocationPaths._Root_Location._BandingLowerEnd)) < 0
					|| areaId.compareTo(
						locationRecord.getString(LocationPaths._Root_Location._BandingLowerEnd)) > 0)
				{
					return false;
				}
		
				requestResult.close(); */
		return true;
	}

	@Override
	public void setup(ValueFunctionContext aContext)
	{
		// TODO Auto-generated method stub
	}
}
