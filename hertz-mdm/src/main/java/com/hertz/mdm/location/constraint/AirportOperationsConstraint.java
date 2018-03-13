/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.constraint;

import java.util.Locale;

import com.hertz.mdm.common.path.CommonReferencePaths;
import com.hertz.mdm.location.path.LocationPaths;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.RequestResult;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.ps.constraint.BaseConstraintOnTableWithRecordLevelCheck;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.schema.ConstraintContextOnTable;
import com.orchestranetworks.schema.InvalidSchemaException;

/**
 * A constraint Location to be sure there is either Airport or Non-Airport Internal Operation and it matches the OnAirport 
 */
public class AirportOperationsConstraint extends BaseConstraintOnTableWithRecordLevelCheck
{
	private String message;

	@Override
	public void setup(ConstraintContextOnTable context)
	{
		// do nothing
	}

	@Override
	public String toUserDocumentation(Locale locale, ValueContext context)
		throws InvalidSchemaException
	{
		return message;
	}

	@Override
	protected String checkValueContext(ValueContext recordContext)
	{
		if (recordContext.getValue(LocationPaths._Root_Location._IsOnAirport) == null
			|| recordContext.getValue(LocationPaths._Root_Location._IsAirportOperations) == null)
		{
			return null;
		}

		String locationId = recordContext.getValue(LocationPaths._Root_Location._Id).toString();
		boolean isOnAirport = (boolean) recordContext
			.getValue(LocationPaths._Root_Location._IsOnAirport);

		if (!isOnAirport)
		{
			return null;
		}

		boolean isAirportOperations = (boolean) recordContext
			.getValue(LocationPaths._Root_Location._IsAirportOperations);

		if (!isAirportOperations)
		{
			return null;
		}

		AdaptationTable locationTable = recordContext.getAdaptationInstance()
			.getTable(LocationPaths._Root_Location.getPathInSchema());

		RequestResult requestResult = locationTable.createRequestResult(
			LocationPaths._Root_Location._Id.format() + "='" + locationId + "'");

		if (requestResult.isEmpty())
		{
			return null;
		}

		Adaptation locationRecord = requestResult.nextAdaptation();

		Adaptation oagRecord = AdaptationUtil
			.followFK(locationRecord, LocationPaths._Root_Location._LocationInformation_Oag);

		if (oagRecord == null)
		{
			return null;
		}

		if (!oagRecord.get_boolean(CommonReferencePaths._Root_Airport._IsAirportOperations))
		{
			return "A Location that is an Airport Operations must have an OAG/IATA that is Airport Operations also associated with it.";
		}

		return null;
	}
}
