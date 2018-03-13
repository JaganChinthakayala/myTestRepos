/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.trigger;

import java.util.*;

import org.apache.commons.lang.*;

import com.onwbp.adaptation.*;
import com.onwbp.base.text.*;
import com.orchestranetworks.instance.*;
import com.orchestranetworks.ps.procedure.*;
import com.orchestranetworks.ps.trigger.TriggerActionValidator.TriggerAction;
import com.orchestranetworks.ps.util.*;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.schema.info.*;
import com.orchestranetworks.schema.trigger.*;
import com.orchestranetworks.service.*;

/**
 * This trigger enables the developer to configure whether to enable cascade delete and if enabled,
 * which related records should be deleted.  Subclasses can also control availability of modify/create/delete
 * actions for a table/occurrence.
 * Typically, in order to enable cascading delete, you would use a BaseTableTrigger and set the enableCascadeDelete
 * parameter to true.  With no other parameters configured, this will result in the behavior that all records
 * associated via an association link or selection node will be deleted.  Optionally, you can specify association/
 * selection node paths to skip.  Furthermore, you can specify foreign key fields to delete in the off chance that
 * deleting a record in this table should result in the deletion of those fk-related records.
 */
public class BaseTableTrigger extends TableTrigger
{
	public static final String CASCADE_DELETE_SESSION_ATTRIBUTE_PREFIX = "cascadeDelete_";
	public static final String IGNORE_VALIDATE_ACTION_SESSION_ATTRIBUTE = "ignoreValidateAction";
	private static final String ORIG_IGNORE_VALIDATE_ACTION_SESSION_ATTRIBUTE_PREFIX = "origIgnoreValidateAction_";

	protected static final String INVOKING_CASCADE_DELETE_SESSION_ATTRIBUTE = "invokingCascadeDelete";

	protected boolean enableCascadeDelete;
	protected String foreignKeysToCascadeDelete;
	protected String selectionNodesToNotCascadeDelete;
	protected String associationsToNotCascadeDelete;
	protected String associationsOverLinkTableToCascadeDelete;

	private Set<Path> foreignKeyPathsToProcess;
	private Set<Path> selectionNodePathsToProcess;
	private Set<Path> associationPathsToProcess;

	@Override
	public void handleBeforeCreate(BeforeCreateOccurrenceContext context) throws OperationException
	{
		// Enable all Privileges so that any hidden fields assigned programmatically can be written
		context.setAllPrivileges();
		super.handleBeforeCreate(context);

		validateTriggerAction(
			context.getSession(),
			context.getOccurrenceContext(),
			null,
			TriggerAction.CREATE);
	}

	@Override
	public void handleBeforeModify(BeforeModifyOccurrenceContext context) throws OperationException
	{
		// Enable all Privileges so that any hidden fields assigned programmatically can be written
		context.setAllPrivileges();
		super.handleBeforeModify(context);

		validateTriggerAction(
			context.getSession(),
			context.getOccurrenceContext(),
			context.getChanges(),
			TriggerAction.MODIFY);
	}

	@Override
	public void handleBeforeDelete(BeforeDeleteOccurrenceContext context) throws OperationException
	{
		Session session = context.getSession();
		validateTriggerAction(session, context.getOccurrenceContext(), null, TriggerAction.DELETE);

		// This is for backwards compatibility but allowDelete is deprecated
		UserMessage deleteErrorMsg = allowDelete(context);
		if (deleteErrorMsg != null)
		{
			throw OperationException.createError(deleteErrorMsg);
		}

		if (shouldCascadeDelete(context))
		{
			// AfterDeleteOccurrenceContext lets you get the ValueContext of the deleted record,
			// but you can't follow an association or selection line on a ValueContext.
			// Therefore we need to save the adaptation in the session before deleting so we can access it after.
			String tablePathStr = context.getTable().getTablePath().format();
			session.setAttribute(
				CASCADE_DELETE_SESSION_ATTRIBUTE_PREFIX + tablePathStr,
				context.getAdaptationOccurrence());
			session.setAttribute(ORIG_IGNORE_VALIDATE_ACTION_SESSION_ATTRIBUTE_PREFIX
				+ tablePathStr, session.getAttribute(IGNORE_VALIDATE_ACTION_SESSION_ATTRIBUTE));
			session.setAttribute(
				IGNORE_VALIDATE_ACTION_SESSION_ATTRIBUTE,
				new TriggerAction[] { TriggerAction.DELETE });
		}
	}

	protected void validateTriggerAction(
		Session session,
		ValueContext valueContext,
		ValueChanges valueChanges,
		TriggerAction triggerAction) throws OperationException
	{
		TriggerAction[] ignoreActions = (TriggerAction[]) session.getAttribute(IGNORE_VALIDATE_ACTION_SESSION_ATTRIBUTE);
		if (ignoreActions == null || !ArrayUtils.contains(ignoreActions, triggerAction))
		{
			TriggerActionValidator triggerActionValidator = createTriggerActionValidator(triggerAction);
			if (triggerActionValidator != null)
			{
				UserMessage msg = triggerActionValidator.validateTriggerAction(
					session,
					valueContext,
					valueChanges,
					triggerAction);
				if (msg != null)
				{
					throw OperationException.createError(msg);
				}
			}
		}
	}

	/**
	 * Create a validator. It can be specific to a particular action or could ignore the
	 * <code>triggerAction</code> param and handle all actions. By default, this returns <code>null</code>.
	 * 
	 * @param triggerAction the action to create it for
	 * @return a validator
	 */
	protected TriggerActionValidator createTriggerActionValidator(TriggerAction triggerAction)
	{
		return null;
	}

	@Override
	public void handleAfterDelete(AfterDeleteOccurrenceContext context) throws OperationException
	{
		if (shouldCascadeDelete(context))
		{
			Session session = context.getSession();
			String tablePathStr = context.getTable().getTablePath().format();
			String sessionAttributeName = CASCADE_DELETE_SESSION_ATTRIBUTE_PREFIX + tablePathStr;

			try
			{
				// Ideally this would be done in setup but we can't guarantee that the schema has
				// completed compilation there.
				initCascadeDelete(context.getTable().getTableNode());

				Adaptation deletedRecord = (Adaptation) session.getAttribute(sessionAttributeName);
				cascadeDelete(deletedRecord, context);
			}
			finally
			{
				try
				{
					session.setAttribute(
						IGNORE_VALIDATE_ACTION_SESSION_ATTRIBUTE,
						session.getAttribute(ORIG_IGNORE_VALIDATE_ACTION_SESSION_ATTRIBUTE_PREFIX
							+ tablePathStr));
				}
				finally
				{
					session.setAttribute(sessionAttributeName, null);
					session.setAttribute(ORIG_IGNORE_VALIDATE_ACTION_SESSION_ATTRIBUTE_PREFIX
						+ tablePathStr, null);
				}
			}
		}
	}

	/**
	 * Indicates whether a cascade delete should be performed. By default, it is performed
	 * if the user is in a workflow and if the enableCascadeDelete field is set to true,
	 * but this can be subclassed for different behavior.
	 */
	protected boolean shouldCascadeDelete(TableTriggerExecutionContext context)
	{
		Session session = context.getSession();
		return enableCascadeDelete
			&& (Boolean.TRUE.equals(session.getAttribute(INVOKING_CASCADE_DELETE_SESSION_ATTRIBUTE)) || session.getInteraction(true) != null);
	}

	/**
	 * Specify whether to allow deletions on this table. Returning an error message indicates
	 * that that error message should be displayed to the user (via an exception).
	 * Returning null indicates there is no error that should prevent the delete from
	 * occurring. Default returns null and it can be overridden to return a different value.
	 * 
	 * @return the error message to use, or null if deletes should be allowed
	 * @deprecated Implement {@link #createTriggerActionValidator(TriggerAction)} instead
	 */
	@Deprecated
	protected UserMessage allowDelete(BeforeDeleteOccurrenceContext context)
	{
		return null;
	}

	protected void cascadeDelete(Adaptation deletedRecord, AfterDeleteOccurrenceContext context)
		throws OperationException
	{
		ProcedureContext pContext = context.getProcedureContext();
		// Call the getter because subclasses can override
		for (Path fkPath : getForeignKeyPathsToProcess(deletedRecord, context))
		{
			Adaptation foreignRecord = AdaptationUtil.followFK(deletedRecord, Path.SELF.add(fkPath));
			if (foreignRecord != null)
			{
				DeleteRecordProcedure.execute(pContext, foreignRecord, true, false, true);
			}
		}

		deleteLinkedRecords(
			getSelectionNodePathsToProcess(deletedRecord, context),
			deletedRecord,
			pContext);
		deleteLinkedRecords(
			getAssociationPathsToProcess(deletedRecord, context),
			deletedRecord,
			pContext);
	}

	private void deleteLinkedRecords(
		Set<Path> paths,
		Adaptation deletedRecord,
		ProcedureContext pContext) throws OperationException
	{
		for (Path path : paths)
		{
			List<Adaptation> relatedRecords = AdaptationUtil.getLinkedRecordList(
				deletedRecord,
				Path.SELF.add(path));
			for (Adaptation relatedRecord : relatedRecords)
			{
				DeleteRecordProcedure.execute(pContext, relatedRecord, true, false, true);
			}
		}
	}

	protected Set<Path> getForeignKeyPathsToProcess(
		Adaptation deletedRecord,
		AfterDeleteOccurrenceContext context)
	{
		return foreignKeyPathsToProcess;
	}

	protected Set<Path> getSelectionNodePathsToProcess(
		Adaptation deletedRecord,
		AfterDeleteOccurrenceContext context)
	{
		return selectionNodePathsToProcess;
	}

	protected Set<Path> getAssociationPathsToProcess(
		Adaptation deletedRecord,
		AfterDeleteOccurrenceContext context)
	{
		return associationPathsToProcess;
	}

	@Override
	public void setup(TriggerSetupContext context)
	{
		// Do nothing. Ideally we'd init the cascade delete fields but we can't because can't be assured
		// that compilation has completed.
	}

	protected void initCascadeDelete(SchemaNode tableNode)
	{
		// We can't guarantee that the same table trigger instance is used in between
		// trigger transactions, so each time we have to check if it's null and initialize
		// the attributes again.
		if (foreignKeyPathsToProcess == null)
		{
			foreignKeyPathsToProcess = PathUtils.convertStringToPathSet(
				getForeignKeysToCascadeDelete(),
				null);

			Set<Path> selectionNodePathsToSkip = PathUtils.convertStringToPathSet(
				getSelectionNodesToNotCascadeDelete(),
				null);
			selectionNodePathsToProcess = getTableSelectionNodeOrAssociationPaths(
				tableNode.getTableOccurrenceRootNode(),
				selectionNodePathsToSkip,
				new HashSet<Path>(),
				false);

			Set<Path> associationPathsToSkip = PathUtils.convertStringToPathSet(
				getAssociationsToNotCascadeDelete(),
				null);
			Set<Path> associationOverLinkTablePathsToProcess = PathUtils.convertStringToPathSet(
				getAssociationsOverLinkTableToCascadeDelete(),
				null);
			associationPathsToProcess = getTableSelectionNodeOrAssociationPaths(
				tableNode.getTableOccurrenceRootNode(),
				associationPathsToSkip,
				associationOverLinkTablePathsToProcess,
				true);
		}
	}

	/**
	 * When a field is added to a model, all existing rows will have that value set to "inherited". Then when the record is saved,
	 * it will get the value of <code>null</code> (unless the user gave it a value), and it will be considered a change.
	 * This method will consider a change from "inherited" to <code>null</code> as not really being a change.
	 * This should only be used for fields that have <code>null</code> as their default value.
	 * 
	 * @param change the change
	 * @return whether it changed
	 */
	protected static boolean isValueChangedIgnoringInheritedToNull(ValueChange change)
	{
		return change != null
			&& !(AdaptationValue.INHERIT_VALUE.equals(change.getValueBefore()) && change.getValueAfter() == null);
	}

	private static Set<Path> getTableSelectionNodeOrAssociationPaths(
		SchemaNode node,
		Set<Path> pathsToSkip,
		Set<Path> pathsToProcess,
		boolean association)
	{
		Set<Path> paths = new HashSet<Path>();
		collectTableSelectionNodeOrAssociationPaths(
			paths,
			node,
			pathsToSkip,
			pathsToProcess,
			association);
		return paths;
	}

	private static void collectTableSelectionNodeOrAssociationPaths(
		Set<Path> paths,
		SchemaNode node,
		Set<Path> pathsToSkip,
		Set<Path> pathsToCascadeDelete,
		boolean association)
	{
		SchemaNode[] children = node.getNodeChildren();
		for (SchemaNode child : children)
		{
			if (child.isTerminalValue())
			{
				Path childPath = child.getPathInAdaptation();
				boolean processChild = false;
				if (association)
				{
					AssociationLink assocLink = child.getAssociationLink();
					if (assocLink != null)
					{
						if (!assocLink.isLinkTable() || pathsToCascadeDelete.contains(childPath))
						{
							processChild = true;
						}
					}
				}
				else if (child.getSelectionLink() != null)
				{
					processChild = true;
				}
				if (processChild && !pathsToSkip.contains(childPath))
				{
					paths.add(childPath);
				}
			}
			else
			{
				collectTableSelectionNodeOrAssociationPaths(
					paths,
					child,
					pathsToSkip,
					pathsToCascadeDelete,
					association);
			}
		}
	}

	public boolean isEnableCascadeDelete()
	{
		return this.enableCascadeDelete;
	}

	public void setEnableCascadeDelete(boolean enableCascadeDelete)
	{
		this.enableCascadeDelete = enableCascadeDelete;
	}

	public String getForeignKeysToCascadeDelete()
	{
		return this.foreignKeysToCascadeDelete;
	}

	public void setForeignKeysToCascadeDelete(String foreignKeysToCascadeDelete)
	{
		this.foreignKeysToCascadeDelete = foreignKeysToCascadeDelete;
	}

	public String getSelectionNodesToNotCascadeDelete()
	{
		return this.selectionNodesToNotCascadeDelete;
	}

	public void setSelectionNodesToNotCascadeDelete(String selectionNodesToNotCascadeDelete)
	{
		this.selectionNodesToNotCascadeDelete = selectionNodesToNotCascadeDelete;
	}

	public String getAssociationsToNotCascadeDelete()
	{
		return this.associationsToNotCascadeDelete;
	}

	public void setAssociationsToNotCascadeDelete(String associationsToNotCascadeDelete)
	{
		this.associationsToNotCascadeDelete = associationsToNotCascadeDelete;
	}

	public String getAssociationsOverLinkTableToCascadeDelete()
	{
		return this.associationsOverLinkTableToCascadeDelete;
	}

	public void setAssociationsOverLinkTableToCascadeDelete(
		String associationsOverLinkTableToCascadeDelete)
	{
		this.associationsOverLinkTableToCascadeDelete = associationsOverLinkTableToCascadeDelete;
	}
}
