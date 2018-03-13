package com.hertz.mdm.location.integration.bean;

public class LocationMessage
{
	protected String operationType;
	protected String schema;
	protected Location location;
	public String getOperationType()

	{
		return operationType;
	}
	public void setOperationType(String operationType)
	{
		this.operationType = operationType;
	}
	public Location getLocation()
	{
		return location;
	}
	public void setLocation(Location location)
	{
		this.location = location;
	}
	public String getSchema()
	{
		return schema;
	}
	public void setSchema(String schema)
	{
		this.schema = schema;
	}
}
