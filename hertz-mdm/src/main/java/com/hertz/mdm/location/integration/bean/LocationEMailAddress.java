package com.hertz.mdm.location.integration.bean;

public class LocationEMailAddress
{
	protected String id;
	protected String location;
	protected String eMailAddressType;
	protected String eMailAddress;

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
	public String geteMailAddressType()
	{
		return eMailAddressType;
	}
	public void seteMailAddressType(String eMailAddressType)
	{
		this.eMailAddressType = eMailAddressType;
	}
	public String geteMailAddress()
	{
		return eMailAddress;
	}
	public void seteMailAddress(String eMailAddress)
	{
		this.eMailAddress = eMailAddress;
	}

}
