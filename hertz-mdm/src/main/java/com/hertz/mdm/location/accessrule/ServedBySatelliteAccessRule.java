package com.hertz.mdm.location.accessrule;

import com.hertz.mdm.location.path.LocationPaths;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.ps.constants.CommonConstants;
import com.orchestranetworks.ps.workflow.WorkflowUtilities;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.service.AccessPermission;
import com.orchestranetworks.service.AccessRule;
import com.orchestranetworks.service.Role;
import com.orchestranetworks.service.Session;

public class ServedBySatelliteAccessRule implements AccessRule
{
	private Boolean isCountryAttribute;

	public ServedBySatelliteAccessRule()
	{
		this(null);
	}
	public ServedBySatelliteAccessRule(Boolean isCountryAttribute)
	{
		this.isCountryAttribute = isCountryAttribute;
	}
	@Override
	public AccessPermission getPermission(Adaptation adaptation, Session session, SchemaNode node)
	{

		if (adaptation.isSchemaInstance() || adaptation.isHistory()
			|| isUserAlwaysReadWrite(session))
		{
			return AccessPermission.getReadWrite();
		}

		Adaptation locationRecord = adaptation;

		if (locationRecord.get_boolean(LocationPaths._Root_Location._IsServedBy)
			|| locationRecord.get_boolean(LocationPaths._Root_Location._IsSatellite))
		{
			return AccessPermission.getReadOnly();
		}
		return AccessPermission.getReadWrite();
	}

	protected boolean isUserAlwaysReadWrite(Session session)
	{
		// This method will be called back into for the permissions user being applied
		// when used in conjunction with a WorkflowAccessRule, so for more efficiency, return R/W for those.
		return session.isUserInRole(Role.forSpecificRole(CommonConstants.ROLE_TECH_ADMIN))
			|| WorkflowUtilities.isPermissionsUser(session);
	}
}
