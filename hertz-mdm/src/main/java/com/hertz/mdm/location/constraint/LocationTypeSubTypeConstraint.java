/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.constraint;

import java.util.Locale;

import com.hertz.mdm.location.constants.LocationConstants;
import com.hertz.mdm.location.path.LocationPaths;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.RequestResult;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.ps.constraint.BaseConstraintOnTableWithRecordLevelCheck;
import com.orchestranetworks.schema.ConstraintContextOnTable;
import com.orchestranetworks.schema.InvalidSchemaException;

/**
 * A constraint Location to be sure there is either Airport or Non-Airport Internal Operation and it matches the OnAirport 
 */
public class LocationTypeSubTypeConstraint extends BaseConstraintOnTableWithRecordLevelCheck
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
		if (recordContext.getValue(LocationPaths._Root_Location._IsOnAirport) == null)
		{
			return null;
		}

		String locationId = recordContext.getValue(LocationPaths._Root_Location._Id).toString();

		boolean isOnAirport = (boolean) recordContext
			.getValue(LocationPaths._Root_Location._IsOnAirport);

		AdaptationTable locationLocationTypeGroupRelationshipTable = recordContext
			.getAdaptationInstance().getTable(
				LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship
					.getPathInSchema());

		if (isOnAirport)
		{
			RequestResult requestResult = locationLocationTypeGroupRelationshipTable
				.createRequestResult(
					LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship._Location
						.format() + "='" + locationId + "' and "
						+ LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship.__subFunctionType
							.format()
						+ "='" + LocationConstants.LOCATION_SUBTYPE_AIRPORT + "'");

			if (requestResult.isEmpty())
			{
				return "There is no Airport(AP) SubType record for an On-Airport Location.";
			}

			requestResult = locationLocationTypeGroupRelationshipTable.createRequestResult(
				LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship._Location
					.format() + "='" + locationId + "' and "
					+ LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship.__subFunctionType
						.format()
					+ "='" + LocationConstants.LOCATION_SUBTYPE_LOCAL + "'");

			if (!requestResult.isEmpty())
			{
				return "An On-Airport Location cannot have a Local Code (LC) SubType record.";
			}
		}
		else if (!isOnAirport)
		{
			RequestResult requestResult = locationLocationTypeGroupRelationshipTable
				.createRequestResult(
					LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship._Location
						.format() + "='" + locationId + "' and "
						+ LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship.__subFunctionType
							.format()
						+ "='" + LocationConstants.LOCATION_SUBTYPE_LOCAL + "'");

			if (requestResult.isEmpty())
			{
				return "An Off-Airport Location must have a Local Code(LC) SubType record.";
			}

			requestResult = locationLocationTypeGroupRelationshipTable.createRequestResult(
				LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship._Location
					.format() + "='" + locationId + "' and "
					+ LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship.__subFunctionType
						.format()
					+ "='" + LocationConstants.LOCATION_SUBTYPE_AIRPORT + "'");

			if (!requestResult.isEmpty())
			{
				return "An Off-Airport Location cannot have an Airport(AP) SubType record";
			}
		}
		return null;
	}
}
