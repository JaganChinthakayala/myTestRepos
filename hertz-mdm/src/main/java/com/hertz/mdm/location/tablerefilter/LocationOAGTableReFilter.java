package com.hertz.mdm.location.tablerefilter;

import java.util.Locale;

import com.hertz.mdm.common.path.CommonReferencePaths;
import com.hertz.mdm.location.path.LocationPaths;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.schema.InvalidSchemaException;
import com.orchestranetworks.schema.TableRefFilter;
import com.orchestranetworks.schema.TableRefFilterContext;

public class LocationOAGTableReFilter implements TableRefFilter
{
	@Override
	public boolean accept(Adaptation record, ValueContext context)
	{
		Adaptation oagRecord = record;
		Adaptation locationRecord = AdaptationUtil.getRecordForValueContext(context);

		if ((oagRecord.get_boolean(CommonReferencePaths._Root_Airport._IsAirportOperations)
			&& locationRecord.get_boolean(LocationPaths._Root_Location._IsAirportOperations))
			|| (!oagRecord.get_boolean(CommonReferencePaths._Root_Airport._IsAirportOperations)
				&& !locationRecord.get_boolean(LocationPaths._Root_Location._IsAirportOperations)))
		{
			return true;
		}
		return false;
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
