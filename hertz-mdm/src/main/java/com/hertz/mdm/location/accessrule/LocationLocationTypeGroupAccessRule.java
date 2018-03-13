package com.hertz.mdm.location.accessrule;

import com.hertz.mdm.location.constants.LocationConstants;
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

public class LocationLocationTypeGroupAccessRule implements AccessRule
{
	private Boolean multipleSelectionsAllowed;
	private Boolean isOnAirportAttribute;

	public LocationLocationTypeGroupAccessRule()
	{
		this(null, null);
	}
	public LocationLocationTypeGroupAccessRule(
		Boolean inputMulitpleSelectionsAllowed,
		Boolean isOnAirportAttribute)
	{
		this.multipleSelectionsAllowed = inputMulitpleSelectionsAllowed;
		this.isOnAirportAttribute = isOnAirportAttribute;

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

		Adaptation locationLocationTypeGroupRecord = adaptation;

		Boolean areMultipleSelectionsAllowed = locationLocationTypeGroupRecord.get_boolean(
			LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship.__areMultipleSelectionsAllowed);

		String parentAirportType = locationLocationTypeGroupRecord.getString(
			LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship._LocationTypeGroupParent);

		//Handle Rules associated with Airport Group
		if (isOnAirportAttribute != null && parentAirportType != null
			&& parentAirportType.equals(LocationConstants.LOCATION_TYPE_GROUP_PARENT_AIRPORT))
		{
			if (LocationConstants.LOCATION_TYPE_GROUP_ON_AIRPORT.equals(
				locationLocationTypeGroupRecord.getString(
					LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship._LocationTypeGroup)))
			{
				if (!isOnAirportAttribute)
				{
					return AccessPermission.getHidden();
				}
				if (areMultipleSelectionsAllowed && multipleSelectionsAllowed)
				{
					return AccessPermission.getReadWrite();
				}
				else if (areMultipleSelectionsAllowed)
				{
					return AccessPermission.getHidden();
				}
				else if (multipleSelectionsAllowed)
				{
					return AccessPermission.getHidden();
				}
				else
					return AccessPermission.getReadWrite();
			}
			if (LocationConstants.LOCATION_TYPE_GROUP_NON_AIRPORT.equals(
				locationLocationTypeGroupRecord.getString(
					LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship._LocationTypeGroup)))
			{
				if (isOnAirportAttribute)
				{
					return AccessPermission.getHidden();
				}
				if (areMultipleSelectionsAllowed && multipleSelectionsAllowed)
				{
					return AccessPermission.getReadWrite();
				}
				else if (areMultipleSelectionsAllowed)
				{
					return AccessPermission.getHidden();
				}
				else if (multipleSelectionsAllowed)
				{
					return AccessPermission.getHidden();
				}
				else
					return AccessPermission.getReadWrite();
			}
		}

		//Hide Airport related attributes if this is not an Aiport Group
		if (isOnAirportAttribute == null && parentAirportType == null)
		{
			//Handle rules NOT associated with Airline Group
			if (areMultipleSelectionsAllowed && multipleSelectionsAllowed)
			{
				return AccessPermission.getReadWrite();
			}
			else if (areMultipleSelectionsAllowed)
			{
				return AccessPermission.getHidden();
			}
			else if (multipleSelectionsAllowed)
			{
				return AccessPermission.getHidden();
			}
			else
				return AccessPermission.getReadWrite();
		}
		return AccessPermission.getHidden();
	}

	protected boolean isUserAlwaysReadWrite(Session session)
	{
		// This method will be called back into for the permissions user being applied
		// when used in conjunction with a WorkflowAccessRule, so for more efficiency, return R/W for those.
		return session.isUserInRole(Role.forSpecificRole(CommonConstants.ROLE_TECH_ADMIN))
			|| WorkflowUtilities.isPermissionsUser(session);
	}
}
