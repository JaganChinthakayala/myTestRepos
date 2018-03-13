package com.orchestranetworks.ps.usertask;

import java.util.*;

import com.orchestranetworks.instance.*;
import com.orchestranetworks.ps.workflow.*;
import com.orchestranetworks.service.*;
import com.orchestranetworks.workflow.*;

public class GeneralMaintenanceUserTask extends BasicUserTask
{
	private String roleName = null;
	private boolean performDatasetValidation = false;

	public String getRoleName()
	{
		return roleName;
	}

	public void setRoleName(String roleName)
	{
		this.roleName = roleName;
	}

	public boolean isPerformDatasetValidation()
	{
		return performDatasetValidation;
	}

	public void setPerformDatasetValidation(boolean performDatasetValidation)
	{
		this.performDatasetValidation = performDatasetValidation;
	}

	@Override
	public void handleCreate(final UserTaskCreationContext context) throws OperationException
	{
		WorkflowUtilities.setUserTaskCreateDateTime(context);
		List<Role> roles = getRolesForUserTask(context, context.getRepository());
		addUsersAndRoles(context, roles);
	}

	/**
	 * By default just returns the role for the specified role name
	 * but can be overridden for different behavior.
	 * 
	 * @param context the data context
	 * @param repo the repository
	 * @return the roles
	 * @throws OperationException if an exception occurs
	 */
	protected List<Role> getRolesForUserTask(DataContext context, Repository repo)
		throws OperationException
	{
		ArrayList<Role> roles = new ArrayList<Role>();
		Role role = getRoleForUserTask(context, repo);
		if (role != null)
		{
			roles.add(role);
		}
		return roles;
	}

	protected Role getRoleForUserTask(DataContext context, Repository repo)
		throws OperationException
	{
		return Role.forSpecificRole(getRoleName());
	}

	protected void addUsersAndRoles(final UserTaskCreationContext context, final List<Role> roles)
		throws OperationException
	{
		for (Role role : roles)
		{
			addUserAndRole(context, role);
		}
	}

	@Override
	public void checkBeforeWorkItemCompletion(UserTaskBeforeWorkItemCompletionContext context)
	{
		if (performDatasetValidation && context.isAcceptAction())
		{
			// For UNIT TESTING ONLY: If your Debug Configuration is set to
			// ignore completion criteria, then this check will be skipped
			// setup a duplicate Debug config with the following argument:
			// -DignoreCompletionCriteria=true
			if (!isCompletionCriteriaIgnored())
				performValidationOnWorkingDataset(context);
		}
		super.checkBeforeWorkItemCompletion(context);
	}

	@Override
	public void handleWorkItemCompletion(final UserTaskWorkItemCompletionContext context)
		throws OperationException
	{
		// set the currentUserId and currentUserLabel variables in the Data
		// Context
		setCurrentUserIdAndLabel(context);
		super.handleWorkItemCompletion(context);

	}
}
