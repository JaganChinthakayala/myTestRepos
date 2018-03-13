package com.hertz.mdm.exporter.constraint;

import java.util.List;
import java.util.Locale;

import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.instance.ValueContextForValidation;
import com.orchestranetworks.schema.Constraint;
import com.orchestranetworks.schema.ConstraintContext;
import com.orchestranetworks.schema.ConstraintOnNull;
import com.orchestranetworks.schema.InvalidSchemaException;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaNode;

public class NullWhenRelatedListHasValuesConstraint
		implements Constraint, ConstraintOnNull {
	private String listPath;
	private SchemaNode listNode;
	
	public String getListPath() {
		return listPath;
	}

	public void setListPath(String listPath) {
		this.listPath = listPath;
	}

	@Override
	public void checkNull(ValueContextForValidation context)
			throws InvalidSchemaException {
		Object listObj = context.getValue(listNode.getPathInSchema());
		if (listObj == null) {
			return;
		}
		@SuppressWarnings("rawtypes")
		List list = (List) listObj;
		if (! list.isEmpty()) {
			// Not internationalized, assuming English
			final String msg = createMessage(context.getNode(), Locale.getDefault());
			context.addError(msg);
		}
	}

	@Override
	public void checkOccurrence(Object obj, ValueContextForValidation context)
			throws InvalidSchemaException {
		// do nothing
	}

	@Override
	public void setup(ConstraintContext context) {
		if (listPath == null) {
			context.addError("listPath parameter must be defined.");
			return;
		}
		listNode = context.getSchemaNode().getNode(Path.parse(listPath));
		if (listNode == null) {
			context.addError("No node with path " + listPath + " exists.");
			return;
		}
		if (listNode.getMaxOccurs() == 1) {
			context.addError("Node at path " + listPath + " is not a list.");
		}
	}

	@Override
	public String toUserDocumentation(Locale locale, ValueContext context)
			throws InvalidSchemaException {
		return createMessage(context.getNode(), locale);
	}
	
	private String createMessage(final SchemaNode node, final Locale locale) {
		return node.getLabel(locale) + " must have a value if "
				+ listNode.getLabel(locale) + " has values";
	}
}
