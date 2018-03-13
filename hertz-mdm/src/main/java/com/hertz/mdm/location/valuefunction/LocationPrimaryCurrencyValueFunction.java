package com.hertz.mdm.location.valuefunction;

import com.hertz.mdm.common.path.CommonReferencePaths;
import com.hertz.mdm.location.path.LocationPaths;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.RequestResult;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.schema.ValueFunction;
import com.orchestranetworks.schema.ValueFunctionContext;

public class LocationPrimaryCurrencyValueFunction implements ValueFunction
{
	@Override
	public Object getValue(Adaptation adaptation)
	{
		Adaptation locationRecord = adaptation;
		String locationRecordId = locationRecord.get(LocationPaths._Root_Location._Id).toString();
		String primaryCurrency = null;

		AdaptationTable locationCurrencyRelationshipTable = locationRecord.getContainer().getTable(
			LocationPaths._Root_LocationData_LocationCurrencyRelationship.getPathInSchema());

		// Query the Location Currency Relationship table for the Primary Currency
		RequestResult requestResult = locationCurrencyRelationshipTable.createRequestResult(
			LocationPaths._Root_LocationData_LocationCurrencyRelationship._Location.format() + "='"
				+ locationRecordId + "' and "
				+ LocationPaths._Root_LocationData_LocationCurrencyRelationship._IsPrimaryCurrency
					.format()
				+ "=true and "
				+ LocationPaths._Root_LocationData_LocationLocationRelationship._IsRecordActive
					.format()
				+ "=true");

		if (!requestResult.isEmpty())
		{
			Adaptation currencyRecord = AdaptationUtil.followFK(
				requestResult.nextAdaptation(),
				LocationPaths._Root_LocationData_LocationCurrencyRelationship._Currency);

			if (currencyRecord != null)
			{
				primaryCurrency = currencyRecord
					.getString(CommonReferencePaths._Root_Currency._Base_Name);
			}
		}

		if (requestResult != null)
		{
			requestResult.close();
		}
		return primaryCurrency;
	}

	@Override
	public void setup(ValueFunctionContext aContext)
	{
		// TODO Auto-generated method stub
	}
}
