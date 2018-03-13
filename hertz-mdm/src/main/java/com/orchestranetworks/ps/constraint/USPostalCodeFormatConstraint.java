/*
 * Copyright Orchestra Networks 2000-2016. All rights reserved.
 */
package com.orchestranetworks.ps.constraint;

import java.util.*;

import com.orchestranetworks.instance.*;
import com.orchestranetworks.schema.*;

/**
 */
public class USPostalCodeFormatConstraint implements Constraint
{
	private static final String message = "US Postal Code must be in one of the following formats: 99999 or 99999-9999.";
	private Path countryPath;
	private Path usaCountryPrimaryKey;

	public Path getCountryPath()
	{
		return this.countryPath;
	}

	public void setCountryPath(Path countryPath)
	{
		this.countryPath = countryPath;
	}

	public Path getUsaCountryPrimaryKey()
	{
		return usaCountryPrimaryKey;
	}

	public void setUsaCountryPrimaryKey(Path usaCountryPrimaryKey)
	{
		this.usaCountryPrimaryKey = usaCountryPrimaryKey;
	}

	@Override
	public void checkOccurrence(Object value, ValueContextForValidation context)
		throws InvalidSchemaException
	{
		if (getCountryPath() == null)
		{
			return;
		}

		String country = (String) context.getValue(Path.PARENT.add(getCountryPath()));

		if (country != null && !country.equals(usaCountryPrimaryKey))
		{
			return;
		}

		String postalCode = (String) context.getValue();
		String regex = "^[0-9]{5}(?:-[0-9]{4})?$";

		if (!postalCode.matches(regex))
		{
			context.addError(message);
		}

	}
	@Override
	public void setup(ConstraintContext context)
	{
		//Do Nothing
	}

	@Override
	public String toUserDocumentation(Locale locale, ValueContext context)
		throws InvalidSchemaException
	{
		return message;
	}

}
