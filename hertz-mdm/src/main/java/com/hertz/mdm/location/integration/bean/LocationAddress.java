package com.hertz.mdm.location.integration.bean;

public class LocationAddress
{
	protected String id;
	protected String location;
	protected String type;
	protected String addressLine1;
	protected String country;
	protected String stateProvince;
	protected String city;
	protected String postalCode;

	public String getId()
	{
		return id;
	}
	public void setId(String id)
	{
		this.id = id;
	}
	public String getLocation()
	{
		return location;
	}
	public void setLocation(String location)
	{
		this.location = location;
	}
	public String getType()
	{
		return type;
	}
	public void setType(String type)
	{
		this.type = type;
	}
	public String getAddressLine1()
	{
		return addressLine1;
	}
	public void setAddressLine1(String addressLine1)
	{
		this.addressLine1 = addressLine1;
	}
	public String getCountry()
	{
		return country;
	}
	public void setCountry(String country)
	{
		this.country = country;
	}
	public String getStateProvince()
	{
		return stateProvince;
	}
	public void setStateProvince(String stateProvince)
	{
		this.stateProvince = stateProvince;
	}
	public String getCity()
	{
		return city;
	}
	public void setCity(String city)
	{
		this.city = city;
	}
	public String getPostalCode()
	{
		return postalCode;
	}
	public void setPostalCode(String postalCode)
	{
		this.postalCode = postalCode;
	}
}
