package com.orchestranetworks.ps.constraint;

import java.text.*;
import java.util.*;

import com.orchestranetworks.instance.*;
import com.orchestranetworks.schema.*;

/**
 * Configured with a sibling field path and a sibling field value,
 * this constraint on null will check, when the sibling field has the specified value,
 * that this field (on which the constraint is declared) requires a value.
 * If the existence of the sibling value makes this field required, set the
 * otherFieldValue to "<not-null>".
 * <p>
 * This constraint can be ignored when the the passOver Path and Value are configured 
 * within the UI.
 */
public class FieldConditionallyRequiredConstraint implements Constraint, ConstraintOnNull
{
	private static String MESSAGE = "{0} required when {1} is {2}.";
	private static String EBX_MESSAGE = "Field {0} is mandatory.";
	private static String IGNORE_CONSTRAINT_MESSAGE = " This constraint will be ignored when {0} contains the value {1}.";
	private static String NOTNULL = "<not-null>";

	private Path otherFieldPath;
	private SchemaNode otherFieldNode;
	private String otherFieldValue;

	// Note: Prefixed these data members with "passOver" so that they would be displayed 
	// under the otherField parameters in UI. Would have preferred the prefix "ignore". 
	private Path passOverConstraintFieldPath;
	private SchemaNode passOverConstraintFieldNode;
	private String passOverConstratintFieldValue;

	private boolean useEBXmessage;
	private String customMessage;

	private String message;
	private String helpMessage;

	/**
	 * @return the passOverConstraintFieldPath
	 */
	public Path getPassOverConstraintFieldPath()
	{
		return passOverConstraintFieldPath;
	}

	/**
	 * @param passOverConstraintFieldPath the passOverConstraintFieldPath to set
	 */
	public void setPassOverConstraintFieldPath(Path passOverConstraintFieldPath)
	{
		this.passOverConstraintFieldPath = passOverConstraintFieldPath;
	}

	/**
	 * @return the passOverConstratintFieldValue
	 */
	public String getPassOverConstratintFieldValue()
	{
		return passOverConstratintFieldValue;
	}

	/**
	 * @param passOverConstratintFieldValue the passOverConstratintFieldValue to set
	 */
	public void setPassOverConstratintFieldValue(String passOverConstratintFieldValue)
	{
		this.passOverConstratintFieldValue = passOverConstratintFieldValue;
	}

	public Path getOtherFieldPath()
	{
		return otherFieldPath;
	}

	public void setOtherFieldPath(Path otherFieldPath)
	{
		this.otherFieldPath = otherFieldPath;
	}

	public String getOtherFieldValue()
	{
		return otherFieldValue;
	}

	public void setOtherFieldValue(String otherFieldValue)
	{
		this.otherFieldValue = otherFieldValue;
	}

	@Override
	public void checkNull(ValueContextForValidation context) throws InvalidSchemaException
	{
		if (passOverConstraintFieldNode != null)
		{
			Object passOverConstraintValue = context.getValue(passOverConstraintFieldNode);
			if (passOverConstratintFieldValue.equals(passOverConstraintValue))
			{
				return; //Pass Over value matches criteria so ignore constraint. 
			}
		}

		Object value = context.getValue(otherFieldNode);
		if (NOTNULL.equals(otherFieldValue) && value != null)
			context.addError(message);
		else if (otherFieldValue.equals(String.valueOf(value)))
			context.addError(message);
	}

	@Override
	public void checkOccurrence(Object value, ValueContextForValidation context)
		throws InvalidSchemaException
	{
		//nothing to do
	}

	@Override
	public void setup(ConstraintContext context)
	{
		if (otherFieldPath == null)
			context.addError("Conditionally required field constraint requires a path to another field in the record");
		otherFieldNode = otherFieldPath.startsWith(Path.PARENT) ? context.getSchemaNode().getNode(
			otherFieldPath) : context.getSchemaNode()
			.getTableNode()
			.getTableOccurrenceRootNode()
			.getNode(otherFieldPath);
		if (otherFieldNode == null)
			context.addError(otherFieldPath.format() + " not found");
		if (otherFieldValue == null)
			otherFieldValue = String.valueOf(null);
		helpMessage = MessageFormat.format(
			MESSAGE,
			context.getSchemaNode().getLabel(Locale.getDefault()),
			otherFieldNode.getLabel(Locale.getDefault()),
			otherFieldValue);
		if (customMessage != null)
			message = customMessage;
		else if (useEBXmessage)
			message = MessageFormat.format(
				EBX_MESSAGE,
				"'" + context.getSchemaNode().getLabel(Locale.getDefault()) + "'");
		else
			message = helpMessage;

		this.setupPassOverConstraintWhen(context);

		context.addDependencyToInsertDeleteAndModify(otherFieldNode);
	}

	@Override
	public String toUserDocumentation(Locale userLocale, ValueContext aContext)
		throws InvalidSchemaException
	{
		return helpMessage;
	}

	/**
	 * Configure this constraint to be ignored if the passOver data members are populated.
	 * If the passOver members are configured then the associated message is also updated.
	 * 
	 * @param context
	 */
	private void setupPassOverConstraintWhen(ConstraintContext context)
	{
		if (passOverConstraintFieldPath == null)
		{
			return; //Nothing to setup
		}
		passOverConstraintFieldNode = passOverConstraintFieldPath.startsWith(Path.PARENT) ? context.getSchemaNode()
			.getNode(passOverConstraintFieldPath)
			: context.getSchemaNode()
				.getTableNode()
				.getTableOccurrenceRootNode()
				.getNode(passOverConstraintFieldPath);
		if (passOverConstraintFieldNode == null)
			context.addError(passOverConstraintFieldPath.format() + " not found");
		if (passOverConstratintFieldValue == null)
			passOverConstratintFieldValue = String.valueOf(null);

		String passOverMessage = MessageFormat.format(
			IGNORE_CONSTRAINT_MESSAGE,
			passOverConstraintFieldNode.getLabel(Locale.getDefault()),
			passOverConstratintFieldValue);

		helpMessage += passOverMessage;
	}

	public boolean isUseEBXmessage()
	{
		return useEBXmessage;
	}

	public void setUseEBXmessage(boolean useEBXmessage)
	{
		this.useEBXmessage = useEBXmessage;
	}

	public String getCustomMessage()
	{
		return customMessage;
	}

	public void setCustomMessage(String customMessage)
	{
		this.customMessage = customMessage;
	}

}
