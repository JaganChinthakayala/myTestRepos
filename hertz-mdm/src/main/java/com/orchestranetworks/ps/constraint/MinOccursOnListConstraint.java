/*
 * Copyright Orchestra Networks 2000-2016. All rights reserved.
 */
package com.orchestranetworks.ps.constraint;

import java.util.*;

import com.orchestranetworks.instance.*;
import com.orchestranetworks.schema.*;

/**
 * This is a constraint that enforces that a list field (minOccurs > 0) must have that number of minimum values in it.
 * This is the same thing that the built-in minOccurs check does, except the built-in check doesn't prevent the save from happening.
 * (The editor for each value won't pre-validate because there's no editor when there's nothing in the list.
 * Also, check for null won't work because a list field is never null, it always returns at least an empty list.)
 */
public class MinOccursOnListConstraint extends BaseConstraintOnTableWithRecordLevelCheck
{
	private Path fieldPath;
	private SchemaNode fieldNode;

	@Override
	public void setup(ConstraintContextOnTable context)
	{
		if (fieldPath == null)
		{
			context.addError("fieldPath must be specified.");
		}
		else
		{
			fieldNode = context.getSchemaNode().getNode(fieldPath);
			if (fieldNode == null)
			{
				context.addError("Node " + fieldPath.format() + " not found.");
			}
			else
			{
				if (fieldNode.getMinOccurs() == 0)
				{
					context.addError("Node " + fieldPath.format()
						+ " must have minOccurs greater than 0.");
				}
			}
		}
	}

	@Override
	public String toUserDocumentation(Locale locale, ValueContext context)
		throws InvalidSchemaException
	{
		return createMessage(locale);
	}

	@Override
	protected SchemaNode getNodeForRecordValidation(ValueContext recordContext)
	{
		return fieldNode;
	}

	@Override
	protected String checkValueContext(ValueContext recordContext)
	{
		List<?> valueList = (List<?>) recordContext.getValue(fieldPath);
		if (valueList.size() < fieldNode.getMinOccurs())
		{
			return createMessage(Locale.getDefault());
		}
		return null;
	}

	private String createMessage(Locale locale)
	{
		StringBuilder bldr = new StringBuilder();
		bldr.append(fieldNode.getLabel(locale));
		bldr.append(" must have at least ");
		int minOccurs = fieldNode.getMinOccurs();
		bldr.append(minOccurs);
		bldr.append("  value");
		if (minOccurs > 1)
		{
			bldr.append("s");
		}
		bldr.append(".");
		return bldr.toString();
	}

	public Path getFieldPath()
	{
		return fieldPath;
	}

	public void setFieldPath(Path fieldPath)
	{
		this.fieldPath = fieldPath;
	}
}
