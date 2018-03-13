package com.hertz.mdm.location.tablerefilter;

import java.util.Locale;

import com.hertz.mdm.location.enums.LocationStatuses;
import com.hertz.mdm.location.path.LocationPaths;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.RequestResult;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.schema.InvalidSchemaException;
import com.orchestranetworks.schema.TableRefFilter;
import com.orchestranetworks.schema.TableRefFilterContext;

public class LocationSubLocationTableReFilter implements TableRefFilter
{
	@Override
	public boolean accept(Adaptation record, ValueContext context)
	{
		Adaptation locationRecord = record;

		String subLocationId = locationRecord.get(LocationPaths._Root_Location._Id).toString();

		AdaptationTable locationLocationRelationshipTable = locationRecord.getContainer().getTable(
			LocationPaths._Root_LocationData_LocationLocationRelationship.getPathInSchema());

		RequestResult requestResult = locationLocationRelationshipTable.createRequestResult(
			LocationPaths._Root_LocationData_LocationLocationRelationship._SubLocation.format()
				+ "='" + subLocationId + "' and "
				+ LocationPaths._Root_LocationData_LocationLocationRelationship._IsRecordActive
					.format()
				+ "=true'");

		if (!requestResult.isEmpty())
		{
			return true;
		}

		if (LocationStatuses.CLOSED
			.equals(locationRecord.getString(LocationPaths._Root_Location._Status)))
		{
			return false;
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
