package com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.googlemaps;

import java.util.Locale;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.SchemaNode;

/**
 * Bean to set the values of each address component. <br>
 * Address components:
 * <ul>
 * <li>number</li>
 * <li>street</li>
 * <li>zipcode</li>
 * <li>city</li>
 * <li>region</li>
 * <li>country</li>
 * <li>lattitude</li>
 * <li>longitude</li>
 * </ul>
 *
 * @author ATI
 * @since 1.0.0
 */
public final class GoogleMapsAddressValue
{
	/**
	 * Creates the address value.
	 *
	 * @param pRecord the record
	 * @param pMapping the mapping
	 * @param pLocale the locale
	 * @return the google maps address value
	 * @since 1.0.0
	 */
	public static GoogleMapsAddressValue createAddressValue(
		final Adaptation pRecord,
		final GoogleMapsAddressFieldMapping pMapping,
		final Locale pLocale)
	{
		GoogleMapsAddressValue addressValue = new GoogleMapsAddressValue();

		if (pRecord == null || pMapping == null)
		{
			return addressValue;
		}

		if (pMapping.getNumberPath() != null)
		{
			Object numberObject = pRecord.get(pMapping.getNumberPath());
			if (numberObject != null)
			{
				addressValue.setNumber(numberObject.toString());
			}
		}
		if (pMapping.getStreetPath() != null)
		{
			Object streetObject = pRecord.get(pMapping.getStreetPath());
			if (streetObject != null && pMapping.getStreetFKMapping() == null)
			{
				addressValue.setStreet(streetObject.toString());
			}
			else if (streetObject != null)
			{
				SchemaNode streetNode = pRecord.getSchemaNode().getNode(pMapping.getStreetPath());
				String street = streetNode
					.displayOccurrence(streetObject, true, pRecord.createValueContext(), pLocale);
				addressValue.setStreet(street);
			}
		}
		if (pMapping.getZipcodePath() != null)
		{
			Object zipCodeObject = pRecord.get(pMapping.getZipcodePath());
			if (zipCodeObject != null && pMapping.getZipcodeFKMapping() == null)
			{
				addressValue.setZipcode(zipCodeObject.toString());
			}
			else if (zipCodeObject != null)
			{
				SchemaNode zipcodeNode = pRecord.getSchemaNode().getNode(pMapping.getZipcodePath());
				String zipcode = zipcodeNode
					.displayOccurrence(zipCodeObject, true, pRecord.createValueContext(), pLocale);
				addressValue.setZipcode(zipcode);
			}
		}
		if (pMapping.getCityPath() != null)
		{
			Object cityObject = pRecord.get(pMapping.getCityPath());
			if (cityObject != null && pMapping.getCityFKMapping() == null)
			{
				addressValue.setCity(cityObject.toString());
			}
			else if (cityObject != null)
			{
				SchemaNode cityNode = pRecord.getSchemaNode().getNode(pMapping.getCityPath());
				String city = cityNode
					.displayOccurrence(cityObject, true, pRecord.createValueContext(), pLocale);
				addressValue.setCity(city);
			}
		}
		if (pMapping.getRegionPath() != null)
		{
			Object regionObject = pRecord.get(pMapping.getRegionPath());
			if (regionObject != null && pMapping.getRegionFKMapping() == null)
			{
				addressValue.setRegion(regionObject.toString());
			}
			else if (regionObject != null)
			{
				SchemaNode regionNode = pRecord.getSchemaNode().getNode(pMapping.getRegionPath());
				String region = regionNode
					.displayOccurrence(regionObject, true, pRecord.createValueContext(), pLocale);
				addressValue.setRegion(region);
			}
		}
		if (pMapping.getCountryPath() != null)
		{
			Object countryObject = pRecord.get(pMapping.getCountryPath());
			if (countryObject != null && pMapping.getCountryFKMapping() == null)
			{
				addressValue.setCountry(countryObject.toString());
			}
			else if (countryObject != null)
			{
				SchemaNode countryNode = pRecord.getSchemaNode().getNode(pMapping.getCountryPath());
				String country = countryNode
					.displayOccurrence(countryObject, true, pRecord.createValueContext(), pLocale);
				addressValue.setCountry(country);
			}
		}
		if (pMapping.getLatitudePath() != null)
		{
			Object latitudeObject = pRecord.get(pMapping.getLatitudePath());
			if (latitudeObject != null)
			{
				addressValue.setLatitude(latitudeObject.toString());
			}
		}
		if (pMapping.getLongitudePath() != null)
		{
			Object longitudeObject = pRecord.get(pMapping.getLongitudePath());
			if (longitudeObject != null)
			{
				addressValue.setLongitude(longitudeObject.toString());
			}
		}

		return addressValue;
	}

	private String number;
	private String street;
	private String zipcode;
	private String city;
	private String region;
	private String country;
	private String latitude;
	private String longitude;

	/**
	 * Instantiates a new google maps address value.
	 *
	 * @since 1.0.0
	 */
	public GoogleMapsAddressValue()
	{
	}

	/**
	 * Instantiates a new google maps address value.
	 *
	 * @param number the number
	 * @param street the street
	 * @param zipcode the zipcode
	 * @param city the city
	 * @param region the region
	 * @param country the country
	 * @param latitude the latitude
	 * @param longitude the longitude
	 * @since 1.0.0
	 */
	public GoogleMapsAddressValue(
		final String number,
		final String street,
		final String zipcode,
		final String city,
		final String region,
		final String country,
		final String latitude,
		final String longitude)
	{
		this.number = number;
		this.street = street;
		this.zipcode = zipcode;
		this.city = city;
		this.region = region;
		this.country = country;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	/**
	 * Gets the city.
	 *
	 * @return the city
	 * @since 1.0.0
	 */
	public String getCity()
	{
		if (this.city == null)
		{
			return "";
		}
		return this.city;
	}

	/**
	 * Gets the concatenated address of format "number, street, zipcode, city, region, country".
	 *
	 * @return the concatenated address
	 * @since 1.0.0
	 */
	public String getConcatenatedAddress()
	{
		boolean before = false;
		String address = "";

		String addressNumber = this.getNumber();
		if (addressNumber != null && !"".equals(addressNumber))
		{
			if (before)
			{
				address += ", ";
			}
			address += addressNumber;
			before = true;
		}

		String addressStreet = this.getStreet();
		if (addressStreet != null && !"".equals(addressStreet))
		{
			if (before)
			{
				address += ", ";
			}
			address += addressStreet;
			before = true;
		}

		String addressZipcode = this.getZipcode();
		if (addressZipcode != null && !"".equals(addressZipcode))
		{
			if (before)
			{
				address += ", ";
			}
			address += addressZipcode;
			before = true;
		}

		String addressCity = this.getCity();
		if (addressCity != null && !"".equals(addressCity))
		{
			if (before)
			{
				address += ", ";
			}
			address += addressCity;
			before = true;
		}

		String addressCountry = this.getCountry();
		if (addressCountry != null && !"".equals(addressCountry))
		{
			if (before)
			{
				address += ", ";
			}
			address += addressCountry;
			before = true;
		}

		return address;
	}

	/**
	 * Gets the country.
	 *
	 * @return the country
	 * @since 1.0.0
	 */
	public String getCountry()
	{
		if (this.country == null)
		{
			return "";
		}
		return this.country;
	}

	/**
	 * Gets the latitude.
	 *
	 * @return the latitude
	 * @since 1.0.0
	 */
	public String getLatitude()
	{
		return this.latitude;
	}

	/**
	 * Gets the longitude.
	 *
	 * @return the longitude
	 * @since 1.0.0
	 */
	public String getLongitude()
	{
		return this.longitude;
	}

	/**
	 * Gets the number.
	 *
	 * @return the number
	 * @since 1.0.0
	 */
	public String getNumber()
	{
		if (this.number == null)
		{
			return "";
		}
		return this.number;
	}

	/**
	 * Gets the region.
	 *
	 * @return the region
	 * @since 1.0.0
	 */
	public String getRegion()
	{
		if (this.region == null)
		{
			return "";
		}
		return this.region;
	}

	/**
	 * Gets the street.
	 *
	 * @return the street
	 * @since 1.0.0
	 */
	public String getStreet()
	{
		if (this.street == null)
		{
			return "";
		}
		return this.street;
	}

	/**
	 * Gets the zipcode.
	 *
	 * @return the zipcode
	 * @since 1.0.0
	 */
	public String getZipcode()
	{
		if (this.zipcode == null)
		{
			return "";
		}
		return this.zipcode;
	}

	/**
	 * Sets the city.
	 *
	 * @param city the new city
	 * @since 1.0.0
	 */
	public void setCity(final String city)
	{
		this.city = city;
	}

	/**
	 * Sets the country.
	 *
	 * @param country the new country
	 * @since 1.0.0
	 */
	public void setCountry(final String country)
	{
		this.country = country;
	}

	/**
	 * Sets the latitude.
	 *
	 * @param latitude the new latitude
	 * @since 1.0.0
	 */
	public void setLatitude(final String latitude)
	{
		this.latitude = latitude;
	}

	/**
	 * Sets the longitude.
	 *
	 * @param longitude the new longitude
	 * @since 1.0.0
	 */
	public void setLongitude(final String longitude)
	{
		this.longitude = longitude;
	}

	/**
	 * Sets the number.
	 *
	 * @param number the new number
	 * @since 1.0.0
	 */
	public void setNumber(final String number)
	{
		this.number = number;
	}

	/**
	 * Sets the region.
	 *
	 * @param region the new region
	 * @since 1.0.0
	 */
	public void setRegion(final String region)
	{
		this.region = region;
	}

	/**
	 * Sets the street.
	 *
	 * @param street the new street
	 * @since 1.0.0
	 */
	public void setStreet(final String street)
	{
		this.street = street;
	}

	/**
	 * Sets the zipcode.
	 *
	 * @param zipcode the new zipcode
	 * @since 1.0.0
	 */
	public void setZipcode(final String zipcode)
	{
		this.zipcode = zipcode;
	}
}
