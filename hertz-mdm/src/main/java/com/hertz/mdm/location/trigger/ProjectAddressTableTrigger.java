/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.trigger;

import com.hertz.mdm._hertz.constants.HtzConstants;
import com.hertz.mdm.location.constants.LocationConstants;
import com.hertz.mdm.location.path.LocationPaths;
import com.orchestranetworks.ps.trigger.BaseTableTrigger;
import com.orchestranetworks.schema.trigger.AfterCreateOccurrenceContext;
import com.orchestranetworks.schema.trigger.NewTransientOccurrenceContext;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.ValueContextForUpdate;

/**
 */
public class ProjectAddressTableTrigger extends BaseTableTrigger
{
	private static String adminUser = "admin";

	@Override
	public void handleNewContext(NewTransientOccurrenceContext context)
	{
		//If a TechAdmin is doing a load, do not automatically set values
		//if (context.getSession().isUserInRole(Role.forSpecificRole(HtzConstants.ROLE_TECH_ADMIN)))
		//{
		//return;
		//}

		//If the User "admin" is doing the load, do not automatically set values
		if (adminUser.equals(context.getSession().getUserReference().getUserId()))
		{
			return;
		}

		ValueContextForUpdate updateContext = context.getOccurrenceContextForUpdate();

		updateContext.setValue(
			LocationConstants.LOCATION_ADDRESS_TYPE_BUSINESS,
			LocationPaths._Root_ProjectData_Address._Type);

		updateContext.setValue(
			com.hertz.mdm.location.enums.AddressPirmarySecondary.PRIMARY,
			LocationPaths._Root_ProjectData_Address._PrimarySecondary);

		updateContext.setValue(
			HtzConstants.COUNTRY_CODE_US,
			LocationPaths._Root_ProjectData_Address._Address_Country);

		super.handleNewContext(context);
	}

	@Override
	public void handleAfterCreate(AfterCreateOccurrenceContext context) throws OperationException
	{
		super.handleAfterCreate(context);

	}
}
