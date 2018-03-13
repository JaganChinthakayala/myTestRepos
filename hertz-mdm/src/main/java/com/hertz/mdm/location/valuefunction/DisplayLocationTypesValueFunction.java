package com.hertz.mdm.location.valuefunction;

import java.util.ArrayList;

import com.hertz.mdm.location.path.LocationPaths;
import com.hertz.mdm.location.path.LocationReferencePaths;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.schema.ValueFunction;
import com.orchestranetworks.schema.ValueFunctionContext;

public class DisplayLocationTypesValueFunction implements ValueFunction
{
	@Override
	public Object getValue(Adaptation adaptation)
	{
		Adaptation locationLocationTypeGroupRelationshipRecord = adaptation;
		//Adaptation locationTypeGroupRelationshipRecord = adaptation;
		Adaptation locationTypeGroupLocationTypeRelationship = null;
		Adaptation locationTypeRecord = null;
		String locationTypeName = "";

		//If this Group does not allow multiple selections, return the Location Type
		if (!locationLocationTypeGroupRelationshipRecord.get_boolean(
			LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship.__areMultipleSelectionsAllowed))
		{
			locationTypeGroupLocationTypeRelationship = AdaptationUtil.followFK(
				locationLocationTypeGroupRelationshipRecord,
				LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship._LocationTypeGroupType);
			if (locationTypeGroupLocationTypeRelationship == null)
			{
				return locationTypeName;
			}
			locationTypeRecord = AdaptationUtil.followFK(
				locationTypeGroupLocationTypeRelationship,
				LocationReferencePaths._Root_LocationTypes_LocationTypeGroupLocationTypeRelationship._LocationType);
			if (locationTypeRecord == null)
			{
				return locationTypeName;
			}
			return locationTypeRecord
				.getString(LocationReferencePaths._Root_LocationTypes_LocationType._Base_Name);
		}

		String locationTypeNames = "";
		String delimiter = "";

		//Create an ArrayList of the Location Types
		ArrayList<Adaptation> locationTypeGroupLocationTypeRelationshipRecords = (ArrayList<Adaptation>) AdaptationUtil
			.followFKList(
				locationLocationTypeGroupRelationshipRecord,
				LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship._LocationTypeGroupTypes);

		//Iterate through each Parent node and get the Parent node Hierarchy Membership and concatenate it the hierarchyMembersip string
		for (Adaptation locationTypeGroupLocationTypeRelationshipRecord : locationTypeGroupLocationTypeRelationshipRecords)
		{
			locationTypeRecord = AdaptationUtil.followFK(
				locationTypeGroupLocationTypeRelationshipRecord,
				LocationReferencePaths._Root_LocationTypes_LocationTypeGroupLocationTypeRelationship._LocationType);
			if (locationTypeRecord != null)
			{
				locationTypeNames = locationTypeNames + delimiter + locationTypeRecord
					.getString(LocationReferencePaths._Root_LocationTypes_LocationType._Base_Name);
				delimiter = ", ";
			}
		}
		return locationTypeNames;
	}

	@Override
	public void setup(ValueFunctionContext aContext)
	{
		// TODO Auto-generated method stub
	}
}
