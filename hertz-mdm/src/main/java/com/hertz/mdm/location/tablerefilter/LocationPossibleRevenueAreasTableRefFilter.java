package com.hertz.mdm.location.tablerefilter;

import java.util.Locale;

import com.hertz.mdm.location.path.LocationPaths;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.schema.InvalidSchemaException;
import com.orchestranetworks.schema.TableRefFilter;
import com.orchestranetworks.schema.TableRefFilterContext;

public class LocationPossibleRevenueAreasTableRefFilter implements TableRefFilter
{
	@Override
	public boolean accept(Adaptation record, ValueContext context)
	{
		Adaptation areaRecord = record;

		Adaptation locationRecord = AdaptationUtil.getRecordForValueContext(context);

		String areaId = areaRecord.getString(LocationPaths._Root_Area._Base_Id);

		if (areaId.length() != 4)
		{
			return false;
		}

		if (locationRecord != null)
		{
			//Area must be between the lowest and highest band range
			if (locationRecord.getString(
				LocationPaths._Root_Location._LocationInformation_AreaBanding_AreaBanding) != null)
			{
				if (areaId.compareTo(
					locationRecord.getString(
						LocationPaths._Root_Location._LocationInformation_AreaBanding_BandingLowerEnd)) < 0
					|| areaId.compareTo(
						locationRecord.getString(
							LocationPaths._Root_Location._LocationInformation_AreaBanding_BandingUpperEnd)) > 0)
				{
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public void setup(TableRefFilterContext arg0)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public String toUserDocumentation(Locale arg0, ValueContext arg1) throws InvalidSchemaException
	{
		// TODO Auto-generated method stub
		return null;
	}
}
