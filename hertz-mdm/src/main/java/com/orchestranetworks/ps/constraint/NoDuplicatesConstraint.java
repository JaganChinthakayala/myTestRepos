/*
 * Copyright Orchestra Networks 2000-2016. All rights reserved.
 */
package com.orchestranetworks.ps.constraint;

import java.util.*;

import com.orchestranetworks.instance.*;
import com.orchestranetworks.schema.*;

/**
 * Configure this table constraint with a fieldPath that represents a list field (can be any field with maxOccurs != 1)
 * and the constraint will ensure that the list contains no duplicates.
 * For example, if ./group/field is a path to a field on the table, setUp will ensure that the path exists, represents
 * a list field, and the checkOccurence will ensure there are no duplicates.
 */
public class NoDuplicatesConstraint extends BaseConstraintOnTableWithRecordLevelCheck
{
	private Path fieldPath;
	private SchemaNode field;

	public Path getFieldPath()
	{
		return fieldPath;
	}

	public void setFieldPath(Path fieldPath)
	{
		this.fieldPath = fieldPath;
	}

	@Override
	public String toUserDocumentation(Locale locale, ValueContext context)
		throws InvalidSchemaException
	{
		return field.getLabel(locale) + " can not contain duplicates.";
	}

	@Override
	public void setup(ConstraintContextOnTable context)
	{
		if (fieldPath == null)
			context.addError("fieldPath required for NoDuplicatesConstraint");
		field = context.getSchemaNode().getNode(fieldPath);
		if (field == null)
			context.addError("fieldPath " + fieldPath.format() + " not found.");
		if (field.getMaxOccurs() == 1)
			context.addError("fieldPath " + fieldPath.format()
				+ " does not represent a list field.");
	}

	@Override
	protected SchemaNode getNodeForRecordValidation(ValueContext recordContext)
	{
		return field;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected String checkValueContext(ValueContext recordContext)
	{
		List list = (List) recordContext.getValue(fieldPath);
		if (list == null || list.isEmpty())
			return null;
		Set set = new HashSet(list);
		if (set.size() < list.size())
			return field.getLabel(Locale.getDefault()) + " contains duplicates.";
		return null;
	}

}
