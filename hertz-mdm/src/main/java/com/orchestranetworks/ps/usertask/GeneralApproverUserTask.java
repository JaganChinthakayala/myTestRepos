package com.orchestranetworks.ps.usertask;

import com.onwbp.base.text.*;
import com.orchestranetworks.ps.workflow.*;
import com.orchestranetworks.service.*;
import com.orchestranetworks.workflow.*;

public class GeneralApproverUserTask extends GeneralMaintenanceUserTask
{
	private String selfApproverRoleName = null;

	private boolean allocateCurrentApprover = true;

	public String getSelfApproverRoleName()
	{
		return selfApproverRoleName;
	}

	public void setSelfApproverRoleName(String selfApproverRoleName)
	{
		this.selfApproverRoleName = selfApproverRoleName;
	}

	/**
	 * @return the allocateCurrentApprover
	 */
	public boolean isAllocateCurrentApprover()
	{
		return allocateCurrentApprover;
	}

	/**
	 * @param allocateCurrentApprover the allocateCurrentApprover to set
	 */
	public void setAllocateCurrentApprover(boolean allocateCurrentApprover)
	{
		this.allocateCurrentApprover = allocateCurrentApprover;
	}

	@Override
	protected void addUserAndRole(UserTaskCreationContext context, Role userRole)
		throws OperationException
	{
		// Assign User as Current Approver if there is one already established
		UserReference userReference = null;
		if (allocateCurrentApprover)
		{
			userReference = WorkflowUtilities.getCurrentApproverReference(
				context,
				context.getRepository());
		}
		addUserReferenceAndRole(context, userRole, userReference);
	}

	@Override
	public void checkBeforeWorkItemCompletion(UserTaskBeforeWorkItemCompletionContext context)
	{
		if (context.isAcceptAction())
		{
			// Exit criteria prevent the currentUserId in the data context
			// from submitting the approval
			// -- allow for self approval if a selfApproverRole is provided and
			// the current user plays that role
			Session session = context.getSession();
			if (session.getUserReference()
				.getUserId()
				.equals(context.getVariableString(WorkflowConstants.PARAM_CURRENT_USER_ID)))
			{
				if (selfApproverRoleName == null
					|| !session.isUserInRole(Role.forSpecificRole(selfApproverRoleName)))
					context.reportMessage(UserMessage.createError("You are not allowed to approve your own changes. You must deallocate this work item so that another user can perform the approval task."));
			}
		}

		super.checkBeforeWorkItemCompletion(context);
	}

	@Override
	public void setCurrentUserIdAndLabel(final UserTaskWorkItemCompletionContext context)
		throws OperationException
	{
		// set the currentApproverId variable in the Data Context, no need to
		// set a label for the Approver
		if (allocateCurrentApprover)
		{
			String currentUserId = context.getCompletedWorkItem().getUserReference().getUserId();
			context.setVariableString(WorkflowConstants.PARAM_CURRENT_APPROVER_ID, currentUserId);
		}
	}

}
