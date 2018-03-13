package com.hertz.mdm.location.valuefunction;

import com.hertz.mdm.common.path.CommonReferencePaths;
import com.hertz.mdm.location.path.LocationPaths;
import com.hertz.mdm.location.path.LocationReferencePaths;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.schema.ValueFunction;
import com.orchestranetworks.schema.ValueFunctionContext;

public class LocationAlphaCodeValueFunction_Deprecated implements ValueFunction
{
	@Override
	public Object getValue(Adaptation adaptation)
	{
		Adaptation locationRecord = adaptation;

		String countryCode = "  ";
		String stateProvinceCode = "  ";
		String cityCode = "   ";
		String locationNumber = "  ";

		Adaptation foreignRecord = AdaptationUtil.followFK(
			locationRecord,
			LocationPaths._Root_Location._LocationInformation__locationCountry);
		if (foreignRecord != null)
		{
			countryCode = foreignRecord
				.getString(CommonReferencePaths._Root_Country_ISO31661._ISO31661_alpha2);
		}

		foreignRecord = AdaptationUtil.followFK(
			locationRecord,
			LocationPaths._Root_Location._LocationInformation__locationStateProvince);
		if (foreignRecord != null)
		{
			stateProvinceCode = foreignRecord
				.getString(CommonReferencePaths._Root_Subdivision_ISO31662._ISO31662_Subdivision);
			stateProvinceCode = stateProvinceCode.substring(stateProvinceCode.indexOf("-") + 1);
		}

		foreignRecord = AdaptationUtil.followFK(
			locationRecord,
			LocationPaths._Root_Location._LocationInformation_LocationCity);
		if (foreignRecord != null)
		{
			cityCode = foreignRecord.getString(LocationReferencePaths._Root_LocationCity._Base_Id);
		}

		if (locationRecord.getString(LocationPaths._Root_Location._LocationNumber) != null)
		{
			locationNumber = locationRecord.getString(LocationPaths._Root_Location._LocationNumber);
		}

		return countryCode + stateProvinceCode + cityCode + locationNumber;
	}

	@Override
	public void setup(ValueFunctionContext aContext)
	{
		// TODO Auto-generated method stub
	}
}
