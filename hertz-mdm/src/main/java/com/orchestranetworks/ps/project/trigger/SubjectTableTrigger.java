/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.project.trigger;

import com.onwbp.base.text.*;
import com.orchestranetworks.instance.*;
import com.orchestranetworks.ps.deepcopy.*;
import com.orchestranetworks.ps.project.path.*;
import com.orchestranetworks.ps.project.workflow.*;
import com.orchestranetworks.ps.trigger.*;
import com.orchestranetworks.ps.trigger.TriggerActionValidator.TriggerAction;
import com.orchestranetworks.ps.workflow.*;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.schema.trigger.*;
import com.orchestranetworks.service.*;

/**
 */
public abstract class SubjectTableTrigger extends BaseTableTrigger implements SubjectPathCapable
{
	@Override
	public void handleNewContext(NewTransientOccurrenceContext context)
	{
		if (context.getSession().getInteraction(true) != null)
		{
			// Current Project Type is normally null when created, but if someone duplicates in the UI, we don't want to duplicate that value
			// since the new subject record isn't associated with a project yet. Upon associating it (via fk or link table), those triggers will
			// handle setting the Current Project Type.
			Path currentProjectTypePath = getSubjectPathConfig().getSubjectCurrentProjectTypeFieldPath();
			if (currentProjectTypePath != null)
			{
				ValueContextForUpdate vc = context.getOccurrenceContextForUpdate();
				Object currentProjectType = vc.getValue(currentProjectTypePath);
				if (currentProjectType != null)
				{
					vc.setValue(null, currentProjectTypePath);
				}
			}
		}
	}

	@Override
	protected TriggerActionValidator createTriggerActionValidator(TriggerAction triggerAction)
	{
		if (triggerAction == TriggerAction.CREATE)
		{
			return new CreateSubjectTriggerActionValidator();
		}
		if (triggerAction == TriggerAction.DELETE)
		{
			return new DeleteSubjectTriggerActionValidator();
		}
		return null;
	}

	/**
	 * Return whether to check if the session's project type is a New Subject type.
	 * By default always checks if you're in a workflow, but can be overridden to customize the behavior.
	 * 
	 * @param session the session
	 * @return whether to check the session's project type
	 */
	protected boolean checkIfNewSubjectProjectType(Session session)
	{
		return session.getInteraction(true) != null;
	}

	protected boolean isNewSubjectProjectType(String projectType)
	{
		String newSubjectProjectType = getSubjectPathConfig().getNewSubjectProjectType();
		return newSubjectProjectType != null && newSubjectProjectType.equals(projectType);
	}

	protected class CreateSubjectTriggerActionValidator implements TriggerActionValidator
	{
		@Override
		public UserMessage validateTriggerAction(
			Session session,
			ValueContext valueContext,
			ValueChanges valueChanges,
			TriggerAction action) throws OperationException
		{
			// If not deep copying, prevent creates for project types that aren't "new"
			Boolean deepCopy = (Boolean) session.getAttribute(DeepCopyService.DEEP_COPY_SESSION_ATTRIBUTE);
			if (!Boolean.TRUE.equals(deepCopy))
			{
				if (checkIfNewSubjectProjectType(session))
				{
					String projectType = WorkflowUtilities.getSessionInteractionParameter(
						session,
						ProjectWorkflowConstants.SESSION_PARAM_PROJECT_TYPE);
					if (!isNewSubjectProjectType(projectType))
					{
						return UserMessage.createError("Creation is not allowed in a "
							+ projectType + " project.");
					}
				}
			}
			return null;
		}
	}

	protected class DeleteSubjectTriggerActionValidator implements TriggerActionValidator
	{
		@Override
		public UserMessage validateTriggerAction(
			Session session,
			ValueContext valueContext,
			ValueChanges valueChanges,
			TriggerAction action) throws OperationException
		{
			if (checkIfNewSubjectProjectType(session))
			{
				String projectType = WorkflowUtilities.getSessionInteractionParameter(
					session,
					ProjectWorkflowConstants.SESSION_PARAM_PROJECT_TYPE);
				if (!isNewSubjectProjectType(projectType))
				{
					return UserMessage.createWarning("Deletion is not allowed in a " + projectType
						+ " project. Use Detach to remove it from the project.");
				}
			}
			return null;
		}
	}
}
