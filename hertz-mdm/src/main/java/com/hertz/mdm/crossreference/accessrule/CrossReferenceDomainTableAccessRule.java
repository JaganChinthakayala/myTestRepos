package com.hertz.mdm.crossreference.accessrule;

import com.hertz.mdm.crossreference.path.CrossReferencePaths;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.service.AccessPermission;
import com.orchestranetworks.service.AccessRule;
import com.orchestranetworks.service.Session;

public class CrossReferenceDomainTableAccessRule implements AccessRule
{
	private String inputDomain;

	public CrossReferenceDomainTableAccessRule()
	{
		this(null);
	}
	public CrossReferenceDomainTableAccessRule(String inputDomain)
	{
		this.inputDomain = inputDomain;
	}

	@Override
	public AccessPermission getPermission(Adaptation adaptation, Session session, SchemaNode node)
	{
		if (adaptation.isSchemaInstance() || adaptation.isHistory()
			|| isUserAlwaysReadWrite(session))
		{
			return AccessPermission.getReadWrite();
		}

		if (adaptation.isSchemaInstance())
		{
			return AccessPermission.getReadWrite();
		}

		Adaptation crossReferenceEBXToSourceSystemRecord = adaptation;

		String domain = crossReferenceEBXToSourceSystemRecord
			.getString(CrossReferencePaths._Root_MDMToTargetSystemCrossReference._Domain);

		if (domain == null)
		{
			return AccessPermission.getHidden();
		}

		if (domain.equals(inputDomain))
		{
			return AccessPermission.getReadWrite();
		}
		return AccessPermission.getHidden();
	}

	protected boolean isUserAlwaysReadWrite(Session session)
	{
		// This method will be called back into for the permissions user being applied
		// when used in conjunction with a WorkflowAccessRule, so for more efficiency, return R/W for those.
		//return session.isUserInRole(Role.forSpecificRole(CommonConstants.ROLE_TECH_ADMIN))
		//	|| WorkflowUtilities.isPermissionsUser(session);

		return false;
	}
}
