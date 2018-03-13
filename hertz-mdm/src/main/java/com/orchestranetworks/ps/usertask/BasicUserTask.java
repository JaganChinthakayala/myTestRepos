package com.orchestranetworks.ps.usertask;

import java.util.*;

import com.onwbp.adaptation.*;
import com.onwbp.base.text.*;
import com.orchestranetworks.instance.*;
import com.orchestranetworks.ps.util.*;
import com.orchestranetworks.ps.workflow.*;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.service.*;
import com.orchestranetworks.service.directory.*;
import com.orchestranetworks.workflow.*;

public class BasicUserTask extends UserTask
{
	protected static final String FIELD_LABEL_MESSAGE_PARAM = "<fieldLabel>";
	protected static final String TABLE_LABEL_MESSAGE_PARAM = "<tableLabel>";

	private static final String SINGLE_VALUE_FIELD_REQUIRED_MESSAGE_TEMPLATE = FIELD_LABEL_MESSAGE_PARAM
		+ " must be specified.";
	private static final String MULTI_VALUE_FIELD_REQUIRED_MESSAGE_TEMPLATE = "At least one "
		+ SINGLE_VALUE_FIELD_REQUIRED_MESSAGE_TEMPLATE;
	private static final String AT_LEAST_ONE_OF_FIELDS_REQUIRED_MESSAGE_TEMPLATE = "Either "
		+ FIELD_LABEL_MESSAGE_PARAM + " must be specified.";
	private static final String ONLY_ONE_OF_FIELDS_ALLOWED_MESSAGE_TEMPLATE = "No more than one of "
		+ FIELD_LABEL_MESSAGE_PARAM + " may be specified.";

	private static final String SINGLE_VALUE_FIELD_REQUIRED_FOR_TABLE_MESSAGE_TEMPLATE = FIELD_LABEL_MESSAGE_PARAM
		+ " must be specified for " + TABLE_LABEL_MESSAGE_PARAM + ".";
	private static final String MULTI_VALUE_FIELD_REQUIRED_FOR_TABLE_MESSAGE_TEMPLATE = "At least one "
		+ SINGLE_VALUE_FIELD_REQUIRED_FOR_TABLE_MESSAGE_TEMPLATE;
	private static final String AT_LEAST_ONE_OF_FIELDS_REQUIRED_FOR_TABLE_MESSAGE_TEMPLATE = "Either "
		+ FIELD_LABEL_MESSAGE_PARAM + " must be specified for " + TABLE_LABEL_MESSAGE_PARAM + ".";
	private static final String ONLY_ONE_OF_FIELDS_ALLOWED_FOR_TABLE_MESSAGE_TEMPLATE = "No more than one of "
		+ FIELD_LABEL_MESSAGE_PARAM + " may be specified for " + TABLE_LABEL_MESSAGE_PARAM + ".";

	protected boolean defaultIncludeTableLabel = false;
	protected boolean defaultIncludeGroupLabels = false;

	@Override
	public void handleCreate(final UserTaskCreationContext context) throws OperationException
	{
		WorkflowUtilities.setUserTaskCreateDateTime(context);
		super.handleCreate(context);
	}

	protected void addUserAndRole(UserTaskCreationContext context, Role userRole)
		throws OperationException
	{
		// Assign User as Current User if there is one already established
		// Else Assign Workflow Launcher if he plays the correct role
		// Else Assign Default Role from Subclass if one exists
		UserReference userReference = WorkflowUtilities.getCurrentUserReference(
			context,
			context.getRepository());
		if (userReference == null)
		{
			userReference = context.getProcessInstance().getCreator();
		}
		addUserReferenceAndRole(context, userRole, userReference);
	}

	protected void addUserReferenceAndRole(
		UserTaskCreationContext context,
		Role userRole,
		UserReference userReference) throws OperationException
	{
		// Assign UserReference as Current User if there is one already
		// established and he is still in the correct role
		// Else Assign Default Role from Subclass if one exists
		if (userReference == null
			|| !context.getSession().getDirectory().isUserInRole(userReference, userRole)
			|| !isUserValidForAutoAllocate(context, userRole, userReference))
		{
			userReference = getDefaultUserForRole(context, userRole);
		}
		addUserAndRole(context, userRole, userReference);
	}

	/**
	 * Determine whether a user is valid to be automatically allocated to. It is assumed that the user is
	 * in the role passed in. By default, this method returns <code>true</code> but can be overridden
	 * if you want to customize whether a user is valid in a particular context.
	 *
	 * @param context the context
	 * @param role the role
	 * @param user the user, which is in the role specified
	 * @return whether the user is valid to be automatically allocated to
	 * @throws OperationException if an error occurs
	 */
	protected boolean isUserValidForAutoAllocate(
		UserTaskCreationContext context,
		Role role,
		UserReference user) throws OperationException
	{
		return true;
	}

	// Override this method if you want to define a Default User
	protected UserReference getDefaultUserForRole(UserTaskCreationContext context, Role role)
		throws OperationException
	{
		return null;
	}

	protected void addUserAndRole(
		UserTaskCreationContext context,
		Role userRole,
		UserReference userReference) throws OperationException
	{

		CreationWorkItemSpec spec = null;
		if (userReference != null
			&& context.getSession().getDirectory().isUserInRole(userReference, userRole))
		{
			spec = CreationWorkItemSpec.forAllocationWithPossibleReallocation(
				userReference,
				userRole);
			spec.setNotificationMail(context.getAllocatedToNotificationMail());
		}
		else
		{
			spec = CreationWorkItemSpec.forOffer(userRole);
			spec.setNotificationMail(context.getOfferedToNotificationMail());
		}

		spec.setProfileMailCC(this.getCCUserReference(context, userRole));

		context.createWorkItem(spec);
	}

	@Override
	public void handleWorkItemCompletion(final UserTaskWorkItemCompletionContext context)
		throws OperationException
	{

		if (context.getCompletedWorkItem().isAccepted())
		{
			// set recordNameValue variable in the Data Context
			Adaptation record = WorkflowUtilities.getRecord(context, context.getRepository());
			if (record != null)
			{
				String recordLabel = getRecordLabel(record, context.getSession());
				context.setVariableString(WorkflowConstants.PARAM_RECORD_NAME_VALUE, recordLabel);
			}
		}
		super.handleWorkItemCompletion(context);
	}

	protected String getRecordLabel(Adaptation record, Session session)
	{
		return record.getLabel(session.getLocale());
	}

	public void setCurrentUserIdAndLabel(final UserTaskWorkItemCompletionContext context)
		throws OperationException
	{
		// set the currentUserId variable in the Data Context
		String currentUserId = context.getCompletedWorkItem().getUserReference().getUserId();
		context.setVariableString(WorkflowConstants.PARAM_CURRENT_USER_ID, currentUserId);
		Session session = context.getSession();
		DirectoryHandler dirHandler = session.getDirectory();
		String currentUserLabel = dirHandler.displayUser(
			UserReference.forUser(currentUserId),
			session.getLocale());
		context.setVariableString(WorkflowConstants.PARAM_CURRENT_USER_LABEL, currentUserLabel);
	}

	protected boolean isCompletionCriteriaIgnored()
	{
		return WorkflowUtilities.isCompletionCriteriaIgnored();
	}

	// Method available to be called from the checkBeforeWorkItemCompletion
	// Method if the User Task requires Validation on the Working DataSpace
	// before Accept
	protected void performValidationOnWorkingDataset(UserTaskBeforeWorkItemCompletionContext context)
	{
		performValidationOnDataset(
			context,
			context.getVariableString(WorkflowConstants.PARAM_WORKING_DATA_SPACE),
			context.getVariableString(WorkflowConstants.PARAM_DATA_SET),
			"*** Validation Errors listed below must be corrected: ***");
	}

	// Method available to be called from the checkBeforeWorkItemCompletion
	// Method if the User Task requires Validation on a Dataset before Accept
	protected void performValidationOnDataset(
		UserTaskBeforeWorkItemCompletionContext context,
		String dataSpaceId,
		String dataSetId,
		String errorMessageText)
	{
		// Perform Validation on Dataset
		Repository repo = context.getRepository();
		AdaptationHome home = repo.lookupHome(HomeKey.forBranchName(dataSpaceId));
		if (home == null)
		{
			context.reportMessage(UserMessage.createError("DataSpace " + dataSpaceId
				+ " has not been found"));
			return;
		}
		Adaptation dataset = home.findAdaptationOrNull(AdaptationName.forName(dataSetId));
		if (dataset == null)
		{
			context.reportMessage(UserMessage.createError("DataSet " + dataSetId
				+ " has not been found"));
			return;
		}

		ValidationReport validationReport = dataset.getValidationReport();
		if (validationReport.hasItemsOfSeverityOrMore(Severity.ERROR))
		{
			context.reportMessage(UserMessage.createError(errorMessageText));
			ValidationReportItemIterator iter = validationReport.getItemsOfSeverityOrMore(Severity.ERROR);
			//Only Display the first 100 error messages
			int maxErrorsToDisplay = 100;
			int count = 0;
			while (iter.hasNext())
			{
				ValidationReportItem item = iter.nextItem();
				count++;
				ValidationReportItemSubjectForAdaptation subject = item.getSubjectForAdaptation();
				if (count <= maxErrorsToDisplay)
				{
					if (subject == null)
					{
						context.reportMessage(item.getMessage());
					}
					else
					{
						context.reportMessage(AdaptationUtil.createUserMessage(
							subject.getAdaptation(),
							item.getMessage().formatMessage(context.getSession().getLocale()),
							Severity.ERROR));
					}
				}
			}
			if (count > maxErrorsToDisplay)
			{
				context.reportMessage(UserMessage.createError(".../... "
					+ String.valueOf(count - maxErrorsToDisplay) + " more errors exist."));
			}
		}
	}

	protected UserMessage checkFieldRequired(
		UserTaskBeforeWorkItemCompletionContext context,
		Adaptation adaptation,
		Path fieldPath)
	{
		return checkFieldRequired(
			context,
			adaptation,
			fieldPath,
			defaultIncludeTableLabel,
			defaultIncludeGroupLabels);
	}

	protected UserMessage checkFieldRequired(
		UserTaskBeforeWorkItemCompletionContext context,
		Adaptation adaptation,
		Path fieldPath,
		boolean includeTableLabel,
		boolean includeGroupLabels)
	{
		UserMessage msg = null;
		if (isFieldMissing(adaptation, fieldPath))
		{
			msg = getFieldRequiredMessage(
				adaptation,
				fieldPath,
				context.getSession(),
				includeTableLabel,
				includeGroupLabels);
			context.reportMessage(msg);
		}
		return msg;
	}

	protected UserMessage checkAtLeastOneOfFieldsRequired(
		UserTaskBeforeWorkItemCompletionContext context,
		Adaptation adaptation,
		Path[] fieldPaths)
	{
		return checkAtLeastOneOfFieldsRequired(
			context,
			new Adaptation[] { adaptation },
			fieldPaths,
			defaultIncludeTableLabel,
			defaultIncludeGroupLabels);
	}

	protected UserMessage checkAtLeastOneOfFieldsRequired(
		UserTaskBeforeWorkItemCompletionContext context,
		Adaptation[] adaptations,
		Path[] fieldPaths)
	{
		return checkAtLeastOneOfFieldsRequired(
			context,
			adaptations,
			fieldPaths,
			defaultIncludeTableLabel,
			defaultIncludeGroupLabels);
	}

	protected UserMessage checkAtLeastOneOfFieldsRequired(
		UserTaskBeforeWorkItemCompletionContext context,
		Adaptation[] adaptations,
		Path[] fieldPaths,
		boolean includeTableLabel,
		boolean includeGroupLabels)
	{
		UserMessage msg = null;
		if (isOneOfFieldsMissing(adaptations, fieldPaths))
		{
			msg = getAtLeastOneOfFieldsRequiredMessage(
				adaptations,
				fieldPaths,
				context.getSession(),
				includeTableLabel,
				includeGroupLabels);
			context.reportMessage(msg);
		}
		return msg;
	}

	protected UserMessage checkOnlyOneOfFieldsAllowed(
		UserTaskBeforeWorkItemCompletionContext context,
		Adaptation adaptation,
		Path[] fieldPaths)
	{
		return checkOnlyOneOfFieldsAllowed(
			context,
			new Adaptation[] { adaptation },
			fieldPaths,
			defaultIncludeTableLabel,
			defaultIncludeGroupLabels);
	}

	protected UserMessage checkOnlyOneOfFieldsAllowed(
		UserTaskBeforeWorkItemCompletionContext context,
		Adaptation[] adaptations,
		Path[] fieldPaths)
	{
		return checkOnlyOneOfFieldsAllowed(
			context,
			adaptations,
			fieldPaths,
			defaultIncludeTableLabel,
			defaultIncludeGroupLabels);
	}

	protected UserMessage checkOnlyOneOfFieldsAllowed(
		UserTaskBeforeWorkItemCompletionContext context,
		Adaptation[] adaptations,
		Path[] fieldPaths,
		boolean includeTableLabel,
		boolean includeGroupLabels)
	{
		UserMessage msg = null;
		if (isMoreThanOneOfFieldsSet(adaptations, fieldPaths))
		{
			msg = getOnlyOneOfFieldsAllowedMessage(
				adaptations,
				fieldPaths,
				context.getSession(),
				includeTableLabel,
				includeGroupLabels);
			context.reportMessage(msg);
		}
		return msg;
	}

	protected boolean isFieldMissing(Adaptation adaptation, Path fieldPath)
	{
		if (adaptation == null)
		{
			return false;
		}
		SchemaNode fieldNode = adaptation.getSchemaNode().getNode(fieldPath);
		if (fieldNode.isSelectNode() || fieldNode.isAssociationNode())
		{
			return AdaptationUtil.isLinkedRecordListEmpty(adaptation, fieldPath);
		}
		Object value = adaptation.get(fieldPath);
		if (value == null)
		{
			return true;
		}
		if (fieldNode.getMaxOccurs() > 1)
		{
			@SuppressWarnings("unchecked")
			List<Object> listValue = (List<Object>) value;
			return listValue.isEmpty();
		}
		// For Strings, check if there is a non-blank value
		if (value instanceof String)
		{
			return ((String) value).trim().isEmpty();
		}

		return false;
	}

	protected boolean isOneOfFieldsMissing(Adaptation adaptation, Path[] fieldPaths)
	{
		return isOneOfFieldsMissing(new Adaptation[] { adaptation }, fieldPaths);
	}

	protected boolean isOneOfFieldsMissing(Adaptation[] adaptations, Path[] fieldPaths)
	{
		boolean sameAdaptation = adaptations.length == 1;
		boolean valueFound = false;
		for (int i = 0; i < fieldPaths.length && !valueFound; i++)
		{
			Path fieldPath = fieldPaths[i];
			Adaptation adaptation = sameAdaptation ? adaptations[0] : adaptations[i];
			valueFound = !isFieldMissing(adaptation, fieldPath);
		}
		return !valueFound;
	}

	protected boolean isMoreThanOneOfFieldsSet(Adaptation adaptation, Path[] fieldPaths)
	{
		return isMoreThanOneOfFieldsSet(new Adaptation[] { adaptation }, fieldPaths);
	}

	protected boolean isMoreThanOneOfFieldsSet(Adaptation[] adaptations, Path[] fieldPaths)
	{
		boolean sameAdaptation = adaptations.length == 1;
		int valueCount = 0;
		for (int i = 0; i < fieldPaths.length && valueCount < 2; i++)
		{
			Path fieldPath = fieldPaths[i];
			Adaptation adaptation = sameAdaptation ? adaptations[0] : adaptations[i];
			if (!isFieldMissing(adaptation, fieldPath))
			{
				valueCount++;
			}
		}
		return valueCount > 1;
	}

	protected UserMessage getFieldRequiredMessage(
		Adaptation adaptation,
		Path fieldPath,
		Session session)
	{
		return getFieldRequiredMessage(
			adaptation,
			fieldPath,
			session,
			defaultIncludeTableLabel,
			defaultIncludeGroupLabels);
	}

	protected UserMessage getFieldRequiredMessage(
		Adaptation adaptation,
		Path fieldPath,
		Session session,
		boolean includeTableLabel,
		boolean includeGroupLabels)
	{
		SchemaNode fieldNode = adaptation.getSchemaNode().getNode(fieldPath);
		String templateWithoutTable;
		String templateWithTable;
		if (fieldNode.getMaxOccurs() > 1 || fieldNode.isSelectNode()
			|| fieldNode.isAssociationNode())
		{
			templateWithoutTable = getMultiValueFieldRequiredMessageTemplate(fieldPath);
			templateWithTable = getMultiValueFieldRequiredForTableMessageTemplate(fieldPath);
		}
		else
		{
			templateWithoutTable = getSingleValueFieldRequiredMessageTemplate(fieldPath);
			templateWithTable = getSingleValueFieldRequiredForTableMessageTemplate(fieldPath);
		}
		return getTokensReplacedMessage(
			new Adaptation[] { adaptation },
			new Path[] { fieldPath },
			session,
			includeTableLabel,
			includeGroupLabels,
			templateWithoutTable,
			templateWithTable);
	}

	protected UserMessage getAtLeastOneOfFieldsRequiredMessage(
		Adaptation adaptation,
		Path[] fieldPaths,
		Session session)
	{
		return getAtLeastOneOfFieldsRequiredMessage(
			new Adaptation[] { adaptation },
			fieldPaths,
			session,
			defaultIncludeTableLabel,
			defaultIncludeGroupLabels);
	}

	protected UserMessage getAtLeastOneOfFieldsRequiredMessage(
		Adaptation[] adaptations,
		Path[] fieldPaths,
		Session session)
	{
		return getAtLeastOneOfFieldsRequiredMessage(
			adaptations,
			fieldPaths,
			session,
			defaultIncludeTableLabel,
			defaultIncludeGroupLabels);
	}

	protected UserMessage getAtLeastOneOfFieldsRequiredMessage(
		Adaptation[] adaptations,
		Path[] fieldPaths,
		Session session,
		boolean includeTableLabel,
		boolean includeGroupLabels)
	{
		return getTokensReplacedMessage(
			adaptations,
			fieldPaths,
			session,
			includeTableLabel,
			includeGroupLabels,
			getAtLeastOneOfFieldsRequiredMessageTemplate(fieldPaths),
			getAtLeastOneOfFieldsRequiredForTableMessageTemplate(fieldPaths));
	}

	protected UserMessage getOnlyOneOfFieldsAllowedMessage(
		Adaptation adaptation,
		Path[] fieldPaths,
		Session session)
	{
		return getOnlyOneOfFieldsAllowedMessage(
			new Adaptation[] { adaptation },
			fieldPaths,
			session,
			defaultIncludeTableLabel,
			defaultIncludeGroupLabels);
	}

	protected UserMessage getOnlyOneOfFieldsAllowedMessage(
		Adaptation[] adaptations,
		Path[] fieldPaths,
		Session session)
	{
		return getOnlyOneOfFieldsAllowedMessage(
			adaptations,
			fieldPaths,
			session,
			defaultIncludeTableLabel,
			defaultIncludeGroupLabels);
	}

	protected UserMessage getOnlyOneOfFieldsAllowedMessage(
		Adaptation[] adaptations,
		Path[] fieldPaths,
		Session session,
		boolean includeTableLabel,
		boolean includeGroupLabels)
	{
		return getTokensReplacedMessage(
			adaptations,
			fieldPaths,
			session,
			includeTableLabel,
			includeGroupLabels,
			getOnlyOneOfFieldsAllowedMessageTemplate(fieldPaths),
			getOnlyOneOfFieldsAllowedForTableMessageTemplate(fieldPaths));
	}

	// TODO: Can only show one table in the string currently
	private UserMessage getTokensReplacedMessage(
		Adaptation[] adaptations,
		Path[] fieldPaths,
		Session session,
		boolean includeTableLabel,
		boolean includeGroupLabels,
		String templateWithoutTable,
		String templateWithTable)
	{
		boolean sameAdaptation = adaptations.length == 1;
		StringBuilder bldr = new StringBuilder();
		for (int i = 0; i < fieldPaths.length; i++)
		{
			bldr.append("'");
			Adaptation adaptation = sameAdaptation ? adaptations[0] : adaptations[i];
			bldr.append(getFieldLabel(adaptation, fieldPaths[i], session, includeGroupLabels));
			bldr.append("'");
			if (i < fieldPaths.length - 1)
			{
				bldr.append(" or ");
			}
		}
		String template = includeTableLabel ? templateWithTable : templateWithoutTable;
		String msg = template.replaceAll(FIELD_LABEL_MESSAGE_PARAM, bldr.toString());
		if (includeTableLabel)
		{
			msg = msg.replaceAll(TABLE_LABEL_MESSAGE_PARAM, getTableString(adaptations[0], session));
		}
		return UserMessage.createError(msg);
	}

	protected String getTableString(Adaptation adaptation, Session session)
	{
		return getTableLabel(adaptation, session) + " "
			+ adaptation.getLabelOrName(session.getLocale());
	}

	protected String getSingleValueFieldRequiredMessageTemplate(Path fieldPath)
	{
		return SINGLE_VALUE_FIELD_REQUIRED_MESSAGE_TEMPLATE;
	}

	protected String getMultiValueFieldRequiredMessageTemplate(Path fieldPath)
	{
		return MULTI_VALUE_FIELD_REQUIRED_MESSAGE_TEMPLATE;
	}

	protected String getAtLeastOneOfFieldsRequiredMessageTemplate(Path[] fieldPaths)
	{
		return AT_LEAST_ONE_OF_FIELDS_REQUIRED_MESSAGE_TEMPLATE;
	}

	protected String getOnlyOneOfFieldsAllowedMessageTemplate(Path[] fieldPaths)
	{
		return ONLY_ONE_OF_FIELDS_ALLOWED_MESSAGE_TEMPLATE;
	}

	protected String getSingleValueFieldRequiredForTableMessageTemplate(Path fieldPath)
	{
		return SINGLE_VALUE_FIELD_REQUIRED_FOR_TABLE_MESSAGE_TEMPLATE;
	}

	protected String getMultiValueFieldRequiredForTableMessageTemplate(Path fieldPath)
	{
		return MULTI_VALUE_FIELD_REQUIRED_FOR_TABLE_MESSAGE_TEMPLATE;
	}

	protected String getAtLeastOneOfFieldsRequiredForTableMessageTemplate(Path[] fieldPaths)
	{
		return AT_LEAST_ONE_OF_FIELDS_REQUIRED_FOR_TABLE_MESSAGE_TEMPLATE;
	}

	protected String getOnlyOneOfFieldsAllowedForTableMessageTemplate(Path[] fieldPaths)
	{
		return ONLY_ONE_OF_FIELDS_ALLOWED_FOR_TABLE_MESSAGE_TEMPLATE;
	}

	protected String getFieldLabel(
		Adaptation adaptation,
		Path fieldPath,
		Session session,
		boolean includeGroupLabels)
	{
		return AdaptationUtil.getFieldLabel(adaptation, fieldPath, session, includeGroupLabels);
	}

	protected String getTableLabel(Adaptation adaptation, Session session)
	{
		return adaptation.getContainerTable().getTableNode().getLabel(session.getLocale());
	}

	/**
	 * Defined by User Tasks that require to CC a user email to a notification.
	 * 
	 * If nobody is to be CC'd to a notification, then return null;
	 */
	protected UserReference getCCUserReference(UserTaskCreationContext context, Role userRole)
	{
		return null;
	}
}
