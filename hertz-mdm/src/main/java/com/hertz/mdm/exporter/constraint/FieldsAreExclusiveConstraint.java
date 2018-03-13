package com.hertz.mdm.exporter.constraint;

import java.util.List;
import java.util.Locale;

import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.instance.ValueContextForValidation;
import com.orchestranetworks.schema.Constraint;
import com.orchestranetworks.schema.ConstraintContext;
import com.orchestranetworks.schema.InvalidSchemaException;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaNode;

public class FieldsAreExclusiveConstraint
		implements Constraint {
	private String fieldPath;
	private SchemaNode fieldNode;
	
	public String getFieldPath() {
		return fieldPath;
	}

	public void setFieldPath(String fieldPath) {
		this.fieldPath = fieldPath;
	}

	@Override
	public void checkOccurrence(Object obj, ValueContextForValidation context)
			throws InvalidSchemaException {
		final Object value = context.getValue();
		if (value != null) {
			final Object value2 = context.getValue(fieldNode.getPathInSchema());
			if (! isNullOrEmptyList(value2, fieldNode)) {
				context.addError(createMessage(context.getNode(), Locale.getDefault()));
			}
		}
	}
	
	@Override
	public void setup(ConstraintContext context) {
		if (fieldPath == null) {
			context.addError("fieldPath parameter must be defined.");
			return;
		}
		fieldNode = context.getSchemaNode().getNode(Path.parse(fieldPath));
		if (fieldNode == null) {
			context.addError("No node with path " + fieldNode + " exists.");
		}
	}

	@Override
	public String toUserDocumentation(Locale locale, ValueContext context)
			throws InvalidSchemaException {
		return createMessage(context.getNode(), locale);
	}
	
	private String createMessage(final SchemaNode thisNode, final Locale locale) {
		return "Fields " + thisNode.getLabel(locale) + " and "
				+ fieldNode.getLabel(locale)
				+ " are mutually exclusive. Only one may have a value.";
	}
	
	private static boolean isNullOrEmptyList(final Object value, final SchemaNode schemaNode) {
		if (value == null) {
			return true;
		}
		if (schemaNode.getMaxOccurs() > 1) {
			@SuppressWarnings("rawtypes")
			List list = (List) value;
			return list.isEmpty();
		}
		return false;
	}
}
