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

//import com.bk.mdm.common.constants.*;
//import com.bk.mdm.finance.enums.*;
//import com.bk.mdm.finance.path.*;
//import com.onwbp.adaptation.*;
//import com.orchestranetworks.ps.constants.*;
//import com.orchestranetworks.ps.workflow.*;
//import com.orchestranetworks.schema.*;
//import com.orchestranetworks.service.*;

public class ServiceHoursSeasonalDatesAccessRule implements AccessRule
{
	private Boolean isThisAccessRuleForSeasonalFields;

	public ServiceHoursSeasonalDatesAccessRule()
	{
		this(null);
	}

	public ServiceHoursSeasonalDatesAccessRule(Boolean isThisAccessRuleForSeasonalFields)
	{
		this.isThisAccessRuleForSeasonalFields = isThisAccessRuleForSeasonalFields;
	}

	@Override
	public AccessPermission getPermission(Adaptation adaptation, Session session, SchemaNode node)
	{
		//		if (adaptation.isSchemaInstance() || adaptation.isHistory()
		//			|| isUserAlwaysReadWrite(session))
		//		{
		//			return AccessPermission.getReadWrite();
		//	}

		if (adaptation.isSchemaInstance())
		{
			return AccessPermission.getReadWrite();
		}

		Adaptation locationServiceDaysRecord = adaptation;

		Boolean isSeasonal = locationServiceDaysRecord
			.get_boolean(LocationPaths._Root_LocationData_ServiceDays._IsSeasonal);

		if (isThisAccessRuleForSeasonalFields)
		{
			if (isSeasonal)
			{
				return AccessPermission.getReadWrite();
			}
			else
			{
				return AccessPermission.getHidden();
			}
		}
		else
		{
			if (isSeasonal)
			{
				return AccessPermission.getHidden();
			}
			else
			{
				return AccessPermission.getReadWrite();
			}
		}
	}

	protected boolean isUserAlwaysReadWrite(Session session)
	{
		// This method will be called back into for the permissions user being applied
		// when used in conjunction with a WorkflowAccessRule, so for more efficiency, return R/W for those.
		return session.isUserInRole(Role.forSpecificRole(CommonConstants.ROLE_TECH_ADMIN))
			|| WorkflowUtilities.isPermissionsUser(session);
	}
}
