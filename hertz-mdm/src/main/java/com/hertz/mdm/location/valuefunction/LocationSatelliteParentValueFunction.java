package com.hertz.mdm.location.valuefunction;

import com.hertz.mdm.location.constants.LocationConstants;
import com.hertz.mdm.location.path.LocationPaths;
import com.hertz.mdm.location.util.HtzLocationUtilities;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.schema.ValueFunction;
import com.orchestranetworks.schema.ValueFunctionContext;

public class LocationSatelliteParentValueFunction implements ValueFunction
{
	@Override
	public Object getValue(Adaptation adaptation)
	{
		Adaptation projectRecord = adaptation;

		Adaptation locationRecord = AdaptationUtil
			.followFK(projectRecord, LocationPaths._Root_LocationProject._Location);

		if (locationRecord == null)
		{
			return null;
		}

		Adaptation locationLocationRelationshipRecord = HtzLocationUtilities
			.getLocationToLocationRelationshipRecordForType(
				locationRecord,
				LocationConstants.LOCATION_RELATIONSHIP_TYPE_SATELLITE);

		if (locationLocationRelationshipRecord == null)
		{
			return null;
		}

		return locationLocationRelationshipRecord.getString(
			LocationPaths._Root_LocationData_LocationLocationRelationship._ParentLocation);
	}

	@Override
	public void setup(ValueFunctionContext aContext)
	{
		// TODO Auto-generated method stub
	}
}
