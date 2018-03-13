package com.orchestranetworks.ps.accessrule;

import java.util.*;

import com.onwbp.adaptation.*;
import com.orchestranetworks.instance.*;
import com.orchestranetworks.ps.constants.*;
import com.orchestranetworks.ps.util.*;
import com.orchestranetworks.ps.workflow.*;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.service.*;

/**
 * Looks up user access when in the context of a workflow.
 * Tech-admin users always have read-write access.  If not in a workflow, other users will have read-only access.
 * When in the context of a workflow, permission is determined by getting tracking information from the session,
 * getting a list of users from this tracking and returning the most restrictive access rule.
 * 
 */
public class WorkflowAccessRule implements AccessRule
{
	public static final String SEGMENT_WORKFLOW_PERMISSIONS_USERS = "workflowPermissionsUsers";

	protected Set<String> nonWorkflowWritableRoles;

	private TrackingInfoHelper trackingInfoHelper;
	private PermissionsUserManager permissionsUserManager;

	public WorkflowAccessRule()
	{
		this(
			new FirstSegmentTrackingInfoHelper(SEGMENT_WORKFLOW_PERMISSIONS_USERS),
			new HashSet<String>());
	}

	public WorkflowAccessRule(TrackingInfoHelper trackingInfoHelper)
	{
		this(trackingInfoHelper, new HashSet<String>());
	}

	public WorkflowAccessRule(Set<String> nonWorkflowWritableRoles)
	{
		this(
			new FirstSegmentTrackingInfoHelper(SEGMENT_WORKFLOW_PERMISSIONS_USERS),
			nonWorkflowWritableRoles);
	}

	public WorkflowAccessRule(
		TrackingInfoHelper trackingInfoHelper,
		Set<String> nonWorkflowWritableRoles)
	{
		this(
			trackingInfoHelper,
			nonWorkflowWritableRoles,
			DefaultPermissionsUserManager.getInstance());
	}

	public WorkflowAccessRule(
		TrackingInfoHelper trackingInfoHelper,
		Set<String> nonWorkflowWritableRoles,
		PermissionsUserManager permissionsUserManager)
	{
		this.trackingInfoHelper = trackingInfoHelper;
		this.nonWorkflowWritableRoles = nonWorkflowWritableRoles;
		this.permissionsUserManager = permissionsUserManager;
	}

	@Override
	public AccessPermission getPermission(Adaptation aAdaptation, Session aSession, SchemaNode aNode)
	{
		/*
		 * When we call for the permissions for the Permissions User later in this method,
		 * it will end up calling back into this rule. So for all Permissions Users, skip all this.
		 */
		if (aAdaptation.isHistory() || isPermissionsUser(aSession))
		{
			return AccessPermission.getReadWrite();
		}

		/*
		 * Test whether the session has an interaction with the workflow engine or not.
		 * If there is no interaction, then we are not running within a workflow.
		 */
		if (aSession.getInteraction(true) == null)
		{
			return getNonWorkflowPermission(aAdaptation, aSession, aNode);
		}

		final String trackingInfo = convertToUserId(aSession.getTrackingInfo());
		if (trackingInfo == null)
		{
			return AccessPermission.getReadWrite();
		}
		String trackingInfoSeg;
		synchronized (trackingInfoHelper)
		{
			trackingInfoHelper.initTrackingInfo(trackingInfo);
			trackingInfoSeg = trackingInfoHelper.getTrackingInfoSegment(SEGMENT_WORKFLOW_PERMISSIONS_USERS);
		}
		Repository repository = aAdaptation.getHome().getRepository();
		AccessPermission accessPermission = AccessPermission.getReadWrite();
		final String[] users = trackingInfoSeg.split(";");
		for (String user : users)
		{
			UserReference userReference = UserReference.forUser(user);
			SessionPermissions permissions;
			if (permissionsUserManager == null)
			{
			permissions = repository.createSessionPermissionsForUser(userReference);
			}
			else
			{
				permissions = permissionsUserManager.getSessionPermissions(
					repository,
					userReference);
			}
			if (permissions == null)
			{
				continue;
			}
			AccessPermission userAccessPermission = permissions.getNodeAccessPermission(
				aNode,
				aAdaptation);
			accessPermission = accessPermission.min(userAccessPermission);
		}

		return accessPermission;
	}

	/**
	 * Return whether the current user is a special "permissions user", so that the permissions
	 * check can be ignored.
	 */
	protected boolean isPermissionsUser(Session session)
	{
		return WorkflowUtilities.isPermissionsUser(session);
	}

	protected AccessPermission getRestrictedPermission()
	{
		return AccessPermission.getReadOnly();
	}

	/**
	 * Determines whether the given data space is writable outside of the workflow,
	 * assuming the user has permission to write outside of workflow.
	 * By default, it returns <code>true</code>, but can be overridden to say, for example, only master data space is writable.
	 * 
	 * @param dataSpace the data space
	 * @param userReference the user
	 * @return whether data space is writable
	 */
	protected boolean isDataSpaceWritableOutsideWorkflow(
		AdaptationHome dataSpace,
		UserReference userReference)
	{
		return true;
	}

	/**
	 * Get the permission to use when not in a workflow
	 * 
	 * @param adaptation the adaptation
	 * @param session the session
	 * @param node the schema node
	 * @return the permission
	 */
	protected AccessPermission getNonWorkflowPermission(
		Adaptation adaptation,
		Session session,
		SchemaNode node)
	{
		// Tech admin can always update
		if (session.isUserInRole(CommonConstants.TECH_ADMIN))
		{
			return AccessPermission.getReadWrite();
		}

		// If not Tech Admin, only can update if data space is writable outside workflow
		if (isDataSpaceWritableOutsideWorkflow(adaptation.getHome(), session.getUserReference()))
		{
			// Only those in the roles specified can update
			for (String roleName : nonWorkflowWritableRoles)
			{
				Role role = Role.forSpecificRole(roleName);

				if (session.getDirectory().isSpecificRoleDefined(role)
					&& session.isUserInRole(role))
				{
					return AccessPermission.getReadWrite();
				}
			}
		}
		return getRestrictedPermission();
	}

	protected String convertToUserId(String trackingInfo)
	{
		return trackingInfo == null ? null : trackingInfo.replaceAll(" ", "_");
	}
}
