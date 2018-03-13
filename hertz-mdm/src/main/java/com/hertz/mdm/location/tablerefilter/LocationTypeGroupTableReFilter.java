package com.hertz.mdm.location.tablerefilter;

import java.util.Locale;

import com.hertz.mdm.location.path.LocationPaths;
import com.hertz.mdm.location.path.LocationReferencePaths;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.RequestResult;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.schema.InvalidSchemaException;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.TableRefFilter;
import com.orchestranetworks.schema.TableRefFilterContext;

public class LocationTypeGroupTableReFilter implements TableRefFilter
{
	@Override
	public boolean accept(Adaptation record, ValueContext context)
	{
		Path LOCATION_ID_PATH = Path.PARENT
			.add(LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship._Location);

		Adaptation locationTypeGroupRecord = record;

		Adaptation locationTypeGroupRecordNoMultiples = AdaptationUtil.followFK(
			locationTypeGroupRecord,
			LocationReferencePaths._Root_LocationTypes_LocationTypeGroup._NoMultipleLocationTypeGroup);

		String locationTypeGroupId = locationTypeGroupRecord
			.get(
				LocationReferencePaths._Root_LocationTypes_LocationTypeGroup._LocationTypeGroupType)
			.toString();

		AdaptationTable locationTypeGroupTable = locationTypeGroupRecord.getContainer().getTable(
			LocationReferencePaths._Root_LocationTypes_LocationTypeGroup.getPathInSchema());

		String locationTypeGroupType = locationTypeGroupRecord
			.get(
				LocationReferencePaths._Root_LocationTypes_LocationTypeGroup._LocationTypeGroupType)
			.toString();

		// Query the LocationTypeGroup to determine if this LocationTypeGroup is used as a Parent
		RequestResult requestResult = locationTypeGroupTable.createRequestResult(
			LocationReferencePaths._Root_LocationTypes_LocationTypeGroup._LocationTypeGroupParent
				.format() + "='" + locationTypeGroupType + "' and "
				+ LocationReferencePaths._Root_LocationTypes_LocationTypeGroup._IsRecordActive
					.format()
				+ "=true");

		//If it is found, this record is a Parent and cannot be selected
		if (!requestResult.isEmpty())
		{
			requestResult.close();
			return false;
		}

		Adaptation parentLocationTypeGroup = AdaptationUtil.followFK(
			locationTypeGroupRecord,
			LocationReferencePaths._Root_LocationTypes_LocationTypeGroup._LocationTypeGroupParent);

		//If this LocaitonTypeGroup has no parent, it can be selected
		if (parentLocationTypeGroup == null)
		{
			requestResult.close();
			return true;
		}

		boolean areMultipleSelectionsAllowed = parentLocationTypeGroup.get_boolean(
			LocationReferencePaths._Root_LocationTypes_LocationTypeGroup.__areMultipleSelectionsAllowed);

		//If Multiple Selections ARE allowed for this LocationGroupType and its Parents, this LocationTypeGroup can be selected
		if (areMultipleSelectionsAllowed && locationTypeGroupRecordNoMultiples == null)
		{
			requestResult.close();
			return true;
		}

		AdaptationTable locationLocationTypeGroupRelationshipTable = context.getAdaptationInstance()
			.getTable(
				LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship
					.getPathInSchema());

		String locationId = (String) context.getValue(LOCATION_ID_PATH);
		String noMultipleLocationTypeGroupId = locationTypeGroupRecordNoMultiples
			.get(
				LocationReferencePaths._Root_LocationTypes_LocationTypeGroup._LocationTypeGroupType)
			.toString();

		requestResult = locationLocationTypeGroupRelationshipTable.createRequestResult(
			LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship._Location
				.format() + "='" + locationId
				+ "' and"
				+ LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship._NoMultipleLocationTypeGroup
					.format()
				+ " = '" + noMultipleLocationTypeGroupId + "' and "
				+ LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship._LocationTypeGroup
					.format()
				+ " != '" + locationTypeGroupId + "' and "
				+ LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship._IsRecordActive
					.format()
				+ "=true");

		if (requestResult.isEmpty())
		{
			requestResult.close();
			return true;
		}
		else
		{
			requestResult.close();
			return false;
		}
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
