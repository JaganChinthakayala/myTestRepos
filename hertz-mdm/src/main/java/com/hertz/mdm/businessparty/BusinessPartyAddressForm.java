package com.hertz.mdm.businessparty;

import com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.googlemaps.GoogleMapsAddressFieldMapping;
import com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.googlemaps.GoogleMapsAddressFieldMapping.ForeignKeyMapping;
import com.hertz.mdm.businessparty.path.BusinessPartyPaths;
import com.hertz.mdm.common.path.CommonReferencePaths;
import com.hertz.mdm.common.referencedata.ReferenceData;
import com.hertz.mdm.location.path.LocationPaths;
import com.onwbp.adaptation.AdaptationTable;

public final class BusinessPartyAddressForm
{
	public class TrackingInfo
	{
		public static final String WITHIN_GOOGLE_MAPS = "withinGoogleMaps";
	}

	public static GoogleMapsAddressFieldMapping getLocationGoogleMapsMapping()
	{
		GoogleMapsAddressFieldMapping mapping = new GoogleMapsAddressFieldMapping();

		mapping.setNumberPath(
			BusinessPartyPaths._Root_BusinessPartyData_Address._Address_StreetNumber);
		mapping.setStreetPath(
			BusinessPartyPaths._Root_BusinessPartyData_Address._Address_AddressLine1);
		mapping.setCountryPath(BusinessPartyPaths._Root_BusinessPartyData_Address._Address_Country);
		mapping
			.setZipcodePath(BusinessPartyPaths._Root_BusinessPartyData_Address._Address_PostalCode);
		mapping.setCityPath(BusinessPartyPaths._Root_BusinessPartyData_Address._Address_City);

		AdaptationTable countriesTable = ReferenceData.getCountriesTable();
		ForeignKeyMapping countryFKMapping = mapping.new ForeignKeyMapping(
			countriesTable,
			CommonReferencePaths._Root_Country_ISO31661._ISO31661_alpha2,
			CommonReferencePaths._Root_Country_ISO31661._ShortName,
			CommonReferencePaths._Root_Country_ISO31661._ISO31661_alpha2);
		mapping.setCountryFKMapping(countryFKMapping);

		mapping.setLatitudePath(
			BusinessPartyPaths._Root_BusinessPartyData_Address._GeographicPoint_Latitude);
		mapping.setLongitudePath(
			BusinessPartyPaths._Root_BusinessPartyData_Address._GeographicPoint_Longitude);

		mapping.setAddressStatusPath(LocationPaths._Root_LocationData_Address._Address_Status);

		return mapping;
	}

	private BusinessPartyAddressForm()
	{
	}
}
