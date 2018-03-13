package com.hertz.mdm.location.integration.bean;

import java.util.List;

public class Location
{
	protected String id;
	protected String name;
	protected String locationNumber;
	protected String locationCountry;
	protected String rentalCarBrand;
	protected List<LocationAddress> addresses;
	protected List<LocationEMailAddress> eMailAddresses;
	protected List<LocationPhone> phones;

	public String getId()
	{
		return id;
	}
	public void setId(String id)
	{
		this.id = id;
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getLocationNumber()
	{
		return locationNumber;
	}
	public void setLocationNumber(String locationNumber)
	{
		this.locationNumber = locationNumber;
	}
	public String getLocationCountry()
	{
		return locationCountry;
	}
	public void setLocationCountry(String locationCountry)
	{
		this.locationCountry = locationCountry;
	}
	public String getRentalCarBrand()
	{
		return rentalCarBrand;
	}
	public void setRentalCarBrand(String rentalCarBrand)
	{
		this.rentalCarBrand = rentalCarBrand;
	}
	public List<LocationAddress> getAddresses()
	{
		return addresses;
	}
	public void setAddresses(List<LocationAddress> addresses)
	{
		this.addresses = addresses;
	}
	public List<LocationEMailAddress> geteMailAddresses()
	{
		return eMailAddresses;
	}
	public void seteMailAddresses(List<LocationEMailAddress> eMailAddresses)
	{
		this.eMailAddresses = eMailAddresses;
	}
	public List<LocationPhone> getPhones()
	{
		return phones;
	}
	public void setPhones(List<LocationPhone> phones)
	{
		this.phones = phones;
	}

}
