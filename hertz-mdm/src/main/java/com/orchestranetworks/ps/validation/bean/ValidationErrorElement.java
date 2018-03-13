package com.orchestranetworks.ps.validation.bean;

import com.onwbp.adaptation.*;

public class ValidationErrorElement
{
	private Adaptation record;
	private String message;

	public Adaptation getRecord()
	{
		return record;
	}

	public void setRecord(Adaptation record)
	{
		this.record = record;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}
}
