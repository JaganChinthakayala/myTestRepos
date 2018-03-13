package com.hertz.mdm.location.valuefunction;

import java.math.BigDecimal;

import com.hertz.mdm._hertz.constants.HtzConstants;
import com.hertz.mdm._hertz.util.HtzUtilities;
import com.hertz.mdm.location.path.LocationPaths;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.RequestResult;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.schema.ValueFunction;
import com.orchestranetworks.schema.ValueFunctionContext;

public class DistanceBetweenRelatedLocationsValueFunction implements ValueFunction
{
	static BigDecimal METERSTOMILESCONVERSION = new BigDecimal(0.000621371);

	@Override
	public Object getValue(Adaptation adaptation)
	{
		double parentLatitude = 0;
		double parentLongitude = 0;
		double subLatitude = 0;
		double subLongitude = 0;

		double parentElevation = 0;
		double subElevation = 0;

		BigDecimal distance = new BigDecimal(0);
		String coordinate;

		Adaptation locationLocationRecord = adaptation;

		Adaptation parentLocationRecord = AdaptationUtil.followFK(
			locationLocationRecord,
			LocationPaths._Root_LocationData_LocationLocationRelationship._ParentLocation);

		String parentLocationId = parentLocationRecord.get(LocationPaths._Root_Location._Id)
			.toString();

		Adaptation subLocationRecord = AdaptationUtil.followFK(
			locationLocationRecord,
			LocationPaths._Root_LocationData_LocationLocationRelationship._SubLocation);

		String subLocationId = subLocationRecord.get(LocationPaths._Root_Location._Id).toString();

		AdaptationTable addressTable = locationLocationRecord.getContainer()
			.getTable(LocationPaths._Root_LocationData_Address.getPathInSchema());

		//Get Address for the Parent Location
		RequestResult requestResult = addressTable.createRequestResult(
			LocationPaths._Root_LocationData_Address._Location.format() + "='" + parentLocationId
				+ "' and" + LocationPaths._Root_LocationData_Address._Type.format() + "='"
				+ "BUSINESS" + "' and"
				+ LocationPaths._Root_LocationData_Address._PrimarySecondary.format() + "='"
				+ "Primary" + "' and "
				+ LocationPaths._Root_LocationData_Address._IsRecordActive.format() + "=true");

		if (requestResult.isEmpty())
		{
			return distance;
		}

		Adaptation parentAddressRecord = requestResult.nextAdaptation();

		//Get Address for the Sub Location
		requestResult = addressTable.createRequestResult(
			LocationPaths._Root_LocationData_Address._Location.format() + "='" + subLocationId
				+ "' and" + LocationPaths._Root_LocationData_Address._Type.format() + "='"
				+ "BUSINESS" + "' and"
				+ LocationPaths._Root_LocationData_Address._PrimarySecondary.format() + "='"
				+ "Primary" + "' and"
				+ LocationPaths._Root_LocationData_Address._IsRecordActive.format() + "=true");

		if (requestResult.isEmpty())
		{
			return distance;
		}

		Adaptation subAddressRecord = requestResult.nextAdaptation();

		if (parentAddressRecord
			.get(LocationPaths._Root_LocationData_Address._GeographicPoint_Latitude) == null
			|| parentAddressRecord
				.get(LocationPaths._Root_LocationData_Address._GeographicPoint_Longitude) == null
			|| subAddressRecord
				.get(LocationPaths._Root_LocationData_Address._GeographicPoint_Latitude) == null
			|| subAddressRecord
				.get(LocationPaths._Root_LocationData_Address._GeographicPoint_Longitude) == null)
		{
			return distance;
		}

		//Capture Parent Location Lat/Long
		coordinate = parentAddressRecord
			.get(LocationPaths._Root_LocationData_Address._GeographicPoint_Latitude).toString();

		parentLatitude = new Double(coordinate);

		coordinate = parentAddressRecord
			.get(LocationPaths._Root_LocationData_Address._GeographicPoint_Longitude).toString();

		parentLongitude = new Double(coordinate);

		//Capture Sub Location Lat/Long
		coordinate = subAddressRecord
			.get(LocationPaths._Root_LocationData_Address._GeographicPoint_Latitude).toString();

		subLatitude = new Double(coordinate);

		coordinate = subAddressRecord
			.get(LocationPaths._Root_LocationData_Address._GeographicPoint_Longitude).toString();

		subLongitude = new Double(coordinate);

		String measurementSystem = parentLocationRecord
			.getString(LocationPaths._Root_Location._LocationInformation_MeasurementSystem);

		if (measurementSystem == null)
		{
			measurementSystem = HtzConstants.MEASUREMENT_TYPE_US;
		}

		distance = new BigDecimal(
			HtzUtilities.distance(
				parentLatitude,
				subLatitude,
				parentLongitude,
				subLongitude,
				parentElevation,
				subElevation,
				measurementSystem.equals(HtzConstants.MEASUREMENT_TYPE_US) ? "M" : "K"));

		return distance.setScale(2, BigDecimal.ROUND_HALF_EVEN);
	}

	@Override
	public void setup(ValueFunctionContext aContext)
	{
		// TODO Auto-generated method stub
	}
}
