/*
 * Copyright Orchestra Networks 2000-2016. All rights reserved.
 */
package com.orchestranetworks.ps.constraint;

import java.util.*;

import javax.mail.internet.*;

import com.orchestranetworks.instance.*;
import com.orchestranetworks.schema.*;

/**
 * Basic constraint to check that an email address value is well-formed.  Note: this won't actually
 * ping the email address or anything requiring internet access.
 */
public class EmailAddressConstraint implements Constraint
{
	private static final String MESSAGE = "Must be a valid email address.";

	@Override
	public void checkOccurrence(Object value, ValueContextForValidation valueContext)
		throws InvalidSchemaException
	{
		if (value != null)
		{
			String valueStr = value.toString();
			if (valueStr.length() > 0)
			{
				try
				{
					InternetAddress internetAddress = new InternetAddress(valueStr);
					internetAddress.validate();
				}
				catch (AddressException ex)
				{
					valueContext.addError(MESSAGE + " " + ex.getMessage());
				}
			}
		}
	}
	@Override
	public void setup(ConstraintContext context)
	{
		// do nothing
	}

	@Override
	public String toUserDocumentation(Locale locale, ValueContext valueContext)
		throws InvalidSchemaException
	{
		return MESSAGE;
	}
}
