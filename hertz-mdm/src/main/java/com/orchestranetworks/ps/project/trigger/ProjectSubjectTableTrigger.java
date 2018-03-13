/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.project.trigger;

import com.onwbp.adaptation.*;
import com.onwbp.base.text.*;
import com.orchestranetworks.instance.*;
import com.orchestranetworks.ps.deepcopy.*;
import com.orchestranetworks.ps.project.path.*;
import com.orchestranetworks.ps.project.util.*;
import com.orchestranetworks.ps.trigger.*;
import com.orchestranetworks.ps.trigger.TriggerActionValidator.TriggerAction;
import com.orchestranetworks.ps.util.*;
import com.orchestranetworks.schema.trigger.*;
import com.orchestranetworks.service.*;

/**
 */
public abstract class ProjectSubjectTableTrigger extends BaseTableTrigger
	implements ProjectPathCapable, SubjectPathCapable
{
	private static final String DELETED_PROJECT_SUBJECT_PROJECT_TYPE = "deletedProjectSubjectProjectType";
	private static final String DELETED_PROJECT_SUBJECT_SUBJECT_PK = "deletedProjectSubjectSubjectPK";

	protected abstract String getMasterDataSpaceName();

	//
	// This is a stopgap measure to prevent someone from adding a subject to a non-new subject project,
	// changing the subject, then removing it from the project. That would result in the changes being merged
	// to the master even though they were never approved.
	//
	// Long-term strategy should be to back out the changes they made when they remove it, but that can require some
	// extensive changes to be implemented so this is the default behavior. If that is done, then extend this method
	// to not return a <code>ProjectSubjectTriggerActionValidator</code>.
	//
	@Override
	protected TriggerActionValidator createTriggerActionValidator(TriggerAction triggerAction)
	{
		return new ProjectSubjectTriggerActionValidator();
	}

	/**
	 * Return whether to check that the project's type is a New Subject type.
	 * By default always checks if you're in a workflow, but can be overridden to customize the behavior.
	 * 
	 * @param session the session
	 * @return whether to check if it's a new subject type
	 */
	protected boolean checkIfNewSubjectProjectType(Session session)
	{
		return session.getInteraction(true) != null;
	}

	/**
	 * Override this in order to specify to cascade delete when it's not a New Menu/Other Item
	 * project and when the item hasn't already been deleted. (This will occur when a Detach
	 * happens on the association.)
	 */
	// TODO: Can't do this because it will lead to error in EBX when it goes to delete
	//       the menu item in a Delete context. Waiting to hear back from engineering on
	//       how we can accomplish different behavior between Detach & Delete.
	//       For now, a tech admin will just have to delete the unattached menu item.
	//       If we end up doing this, will also need to set enableCascadeDelete = true and
	//       foreignKeysToDelete = /menuItem or /offerOrOtherItem
	//	@Override
	//	protected boolean shouldCascadeDelete(TableTriggerExecutionContext context)
	//	{
	//		if (super.shouldCascadeDelete(context))
	//		{
	//			String projectType = (String) context.getSession().getAttribute(
	//				DELETED_PROJECT_SUBJECT_PROJECT_TYPE);
	//			if (projectPathConfig.isNewSubjectProjectType(projectType))
	//			{
	//				Adaptation subjectRecord = getSubjectRecordFromSessionAttribute(context);
	//				if (subjectRecord != null)
	//				{
	//					return true;
	//				}
	//			}
	//		}
	//		return false;
	//	}

	@Override
	public void handleAfterCreate(AfterCreateOccurrenceContext context) throws OperationException
	{
		Session session = context.getSession();
		if (session.getInteraction(true) != null)
		{
			SubjectPathConfig subjectPathConfig = getSubjectPathConfig();

			Adaptation projectSubjectRecord = context.getAdaptationOccurrence();

			Adaptation projectRecord = AdaptationUtil.followFK(
				projectSubjectRecord,
				subjectPathConfig.getProjectSubjectProjectFieldPath());
			Adaptation subjectRecord = AdaptationUtil.followFK(
				projectSubjectRecord,
				subjectPathConfig.getProjectSubjectSubjectFieldPath());
			// TODO: This is a workaround for the fact that EBX allows link table records to be created even when
			//       their fk filter is violated. Once that is addressed in EBX, this can be removed.
			//       It needs to be done in afterCreate since we don't have a record to validate until then.
			ValidationReport validationReport = projectSubjectRecord.getValidationReport();
			if (validationReport.hasItemsOfSeverityOrMore(Severity.ERROR))
			{
				throw OperationException.createError("Association to "
					+ projectRecord.getContainerTable()
						.getTableNode()
						.getLabel(session.getLocale())
					+ " can't be created. "
					+ subjectRecord.getContainerTable()
						.getTableNode()
						.getLabel(session.getLocale()) + " may violate a constraint.");
			}
			modifySubjectRecordAfterCreate(
				projectRecord,
				subjectRecord,
				context.getProcedureContext(),
				session);
		}
	}

	protected void modifySubjectRecordAfterCreate(
		Adaptation projectRecord,
		Adaptation subjectRecord,
		ProcedureContext pContext,
		Session session) throws OperationException
	{
		ProjectPathConfig projectPathConfig = getProjectPathConfig();
		String projectType = projectRecord.getString(projectPathConfig.getProjectProjectTypeFieldPath());

		ProjectUtil.setCurrentProjectType(
			projectType,
			subjectRecord,
			pContext,
			session,
			projectPathConfig,
			getSubjectPathConfig());
	}

	@Override
	public void handleBeforeDelete(BeforeDeleteOccurrenceContext context) throws OperationException
	{
		Session session = context.getSession();
		if (session.getInteraction(true) != null)
		{
			ProjectPathConfig projectPathConfig = getProjectPathConfig();
			SubjectPathConfig subjectPathConfig = getSubjectPathConfig();

			Adaptation projectSubjectRecord = context.getAdaptationOccurrence();
			String subjectPK = projectSubjectRecord.getString(subjectPathConfig.getProjectSubjectSubjectFieldPath());
			session.setAttribute(DELETED_PROJECT_SUBJECT_SUBJECT_PK, subjectPK);
			String projectType = (String) AdaptationUtil.followFK(
				projectSubjectRecord,
				subjectPathConfig.getProjectSubjectProjectFieldPath(),
				projectPathConfig.getProjectProjectTypeFieldPath());
			session.setAttribute(DELETED_PROJECT_SUBJECT_PROJECT_TYPE, projectType);
		}
		super.handleBeforeDelete(context);
	}

	@Override
	public void handleAfterDelete(AfterDeleteOccurrenceContext context) throws OperationException
	{
		Session session = context.getSession();
		try
		{
			if (session.getInteraction(true) != null)
			{
				ProjectPathConfig projectPathConfig = getProjectPathConfig();
				Adaptation subjectRecord = getSubjectRecordFromSessionAttribute(context);
				if (subjectRecord != null)
				{
					ProjectUtil.setCurrentProjectType(
						null,
						subjectRecord,
						context.getProcedureContext(),
						session,
						projectPathConfig,
						getSubjectPathConfig());
				}
			}

			super.handleAfterDelete(context);
		}
		finally
		{
			session.setAttribute(DELETED_PROJECT_SUBJECT_PROJECT_TYPE, null);
			session.setAttribute(DELETED_PROJECT_SUBJECT_SUBJECT_PK, null);
		}
	}

	private Adaptation getSubjectRecordFromSessionAttribute(TableTriggerExecutionContext context)
	{
		Session session = context.getSession();
		String subjectPK = (String) session.getAttribute(DELETED_PROJECT_SUBJECT_SUBJECT_PK);

		AdaptationTable subjectTable = context.getTable()
			.getContainerAdaptation()
			.getTable(getSubjectPathConfig().getSubjectTablePath());
		return subjectTable.lookupAdaptationByPrimaryKey(PrimaryKey.parseString(subjectPK));
	}

	private class ProjectSubjectTriggerActionValidator implements TriggerActionValidator
	{
		@Override
		public UserMessage validateTriggerAction(
			Session session,
			ValueContext valueContext,
			ValueChanges valueChanges,
			TriggerAction action) throws OperationException
		{
			ProjectPathConfig projectPathConfig = getProjectPathConfig();
			SubjectPathConfig subjectPathConfig = getSubjectPathConfig();

			Adaptation projectRecord = AdaptationUtil.followFK(
				valueContext,
				subjectPathConfig.getProjectSubjectProjectFieldPath());
			if (projectRecord != null)
			{
				if (action == TriggerAction.CREATE)
				{
					// If not deep copying, check the currentProjectType
					Boolean deepCopy = (Boolean) session.getAttribute(DeepCopyService.DEEP_COPY_SESSION_ATTRIBUTE);
					if (!Boolean.TRUE.equals(deepCopy))
					{
						Adaptation subjectRecord = AdaptationUtil.followFK(
							valueContext,
							subjectPathConfig.getProjectSubjectSubjectFieldPath());
						// TODO: This is a workaround for the fact that EBX allows link table records to be created even when
						//       their fk filter is violated. Once that is addressed in EBX, this can be removed.
						String currentProjectType = ProjectUtil.getCurrentProjectType(
							subjectRecord,
							getMasterDataSpaceName(),
							subjectPathConfig);
						if (currentProjectType != null)
						{
							return UserMessage.createError("Association to "
								+ projectRecord.getContainerTable()
									.getTableNode()
									.getLabel(session.getLocale())
								+ " can't be created. "
								+ subjectRecord.getContainerTable()
									.getTableNode()
									.getLabel(session.getLocale())
								+ " is associated with another project.");
						}
					}
				}
				else if (action == TriggerAction.DELETE)
				{
					if (checkIfNewSubjectProjectType(session))
					{
						String projectType = projectRecord.getString(projectPathConfig.getProjectProjectTypeFieldPath());
						if (!projectPathConfig.isNewSubjectProjectType(projectType))
						{
							return UserMessage.createError("Record cannot be removed from a "
								+ projectType
								+ " project once it has been added. You must cancel the project and relaunch it.");
						}
					}
				}
			}
			return null;
		}
	}
}
