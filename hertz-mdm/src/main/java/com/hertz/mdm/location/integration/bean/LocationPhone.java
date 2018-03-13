package com.hertz.mdm.location.integration.bean;

public class LocationPhone
{
	protected String id;
	protected String location;
	protected String phoneType;
	protected String countryCode;
	protected String areaCode;
	protected String localNumber;

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
	public String getPhoneType()
	{
		return phoneType;
	}
	public void setPhoneType(String phoneType)
	{
		this.phoneType = phoneType;
	}
	public String getCountryCode()
	{
		return countryCode;
	}
	public void setCountryCode(String countryCode)
	{
		this.countryCode = countryCode;
	}
	public String getAreaCode()
	{
		return areaCode;
	}
	public void setAreaCode(String areaCode)
	{
		this.areaCode = areaCode;
	}
	public String getLocalNumber()
	{
		return localNumber;
	}
	public void setLocalNumber(String localNumber)
	{
		this.localNumber = localNumber;
	}
}
