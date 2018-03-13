package com.orchestranetworks.ps.accessrule;

import com.onwbp.adaptation.*;
import com.orchestranetworks.ps.constants.*;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.service.*;

/**
 * Almost the opposite of HideOnCreationAccessRule, this rule ensures that some fields are only
 * read-write during creation and are otherwise read-only or hidden. The 'otherwise' permission is configurable
 * using an argument to the constructor.  The no-argument constructor uses read-only.
 * Note: the tech-admin role will always have read-write access.
 * @see HideOnCreationAccessRule
 */
public class ReadWriteForNewRecordOnlyAccessRule implements AccessRule
{
	protected AccessPermission notNewRecordPermission;

	public ReadWriteForNewRecordOnlyAccessRule()
	{
		this(AccessPermission.getReadOnly());
	}

	public ReadWriteForNewRecordOnlyAccessRule(AccessPermission notNewRecordPermission)
	{
		this.notNewRecordPermission = notNewRecordPermission;
	}

	@Override
	public AccessPermission getPermission(Adaptation adaptation, Session session, SchemaNode node)
	{
		// Will be data set when called for column permissions
		// or a record not yet saved
		if (adaptation.isSchemaInstance() || adaptation.isHistory())
		{
			return AccessPermission.getReadWrite();
		}
		return isUserAlwaysReadWrite(session) ? AccessPermission.getReadWrite()
			: notNewRecordPermission;
	}

	protected boolean isUserAlwaysReadWrite(Session session)
	{
		return session.isUserInRole(CommonConstants.TECH_ADMIN);
	}
}
