package com.orchestranetworks.ps.constraint;

import java.util.*;

import com.onwbp.adaptation.*;
import com.orchestranetworks.instance.*;
import com.orchestranetworks.ps.util.*;
import com.orchestranetworks.ps.util.functional.*;
import com.orchestranetworks.schema.*;

/**
 * Configured with a set of paths, each representing a field node, and whether or not at least one
 * occurrence is required, this constraint will ensure that not more than one of the choices is populated.
 */
public class ChoiceConstraint extends BaseConstraintOnTableWithRecordLevelCheck
{
	private List<SchemaNode> fields;
	private String pathsAsString;
	private boolean required = true;

	public String getPathsAsString()
	{
		return pathsAsString;
	}

	public void setPathsAsString(String pathsAsString)
	{
		this.pathsAsString = pathsAsString;
	}

	public boolean isRequired()
	{
		return required;
	}

	public void setRequired(boolean required)
	{
		this.required = required;
	}

	@Override
	public void setup(ConstraintContextOnTable context)
	{
		if (fields == null && pathsAsString != null)
		{
			List<Path> pathList = PathUtils.convertStringToPathList(pathsAsString, null);
			fields = new ArrayList<SchemaNode>();
			for (Path path : pathList)
			{
				SchemaNode fieldNode = PathUtils.setupFieldNode(context, path, null, true);
				if (fieldNode != null)
					fields.add(fieldNode);
			}
		}
		if (fields == null || fields.size() < 2)
			context.addError("Choice constraint requires specifying at least two fields for the choice");
	}

	@Override
	public String toUserDocumentation(Locale locale, ValueContext context)
		throws InvalidSchemaException
	{
		return createMessage(locale, context, null);
	}

	private String createMessage(
		Locale locale,
		ValueContext context,
		List<SchemaNode> fieldsWithValues)
	{
		StringBuilder sb = new StringBuilder();
		if (required)
			sb.append("Exactly one of ");
		else
			sb.append("At most one of ");
		UnaryFunction<SchemaNode, String> toString = AdaptationUtil.GetNodeLabel.bindRight(locale);
		sb.append(CollectionUtils.joinStrings(fields, toString, ", "));
		sb.append(" can have a value");
		if (fieldsWithValues != null)
		{
			if (fieldsWithValues.isEmpty())
			{
				sb.append(" (none has a value)");
			}
			else
			{
				sb.append(" (the following have a value: ");
				sb.append(CollectionUtils.joinStrings(fieldsWithValues, toString, ", "));
				sb.append(")");
			}
		}
		return sb.toString();
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected String checkValueContext(ValueContext recordContext)
	{
		Adaptation record = AdaptationUtil.getRecordForValueContext(recordContext);
		if (record != null)
		{
			List<SchemaNode> fieldsWithValues = new ArrayList<SchemaNode>();
			for (SchemaNode schemaNode : fields)
			{
				Object value = recordContext.getValue(schemaNode);
				if (value instanceof Collection && ((Collection) value).isEmpty())
					continue;
				if (value != null)
					fieldsWithValues.add(schemaNode);
			}
			int size = fieldsWithValues.size();
			if ((required && size == 0) || size > 1)
				return createMessage(Locale.getDefault(), recordContext, fieldsWithValues);
		}
		return null;
	}

}
