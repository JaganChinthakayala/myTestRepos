package com.hertz.mdm.location.tablerefilter;

import java.util.Locale;

import com.hertz.mdm.location.constants.LocationConstants;
import com.hertz.mdm.location.util.HtzLocationUtilities;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.schema.InvalidSchemaException;
import com.orchestranetworks.schema.TableRefFilter;
import com.orchestranetworks.schema.TableRefFilterContext;

public class ReservationAreaPossibleParentsTableReFilter implements TableRefFilter
{
	@Override
	public boolean accept(Adaptation record, ValueContext context)
	{
		return HtzLocationUtilities
			.areaPossibleParents(record, LocationConstants.OPERATION_AREA_TYPE_CMS);
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
