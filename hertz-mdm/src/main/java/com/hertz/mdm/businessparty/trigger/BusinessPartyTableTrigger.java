/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.businessparty.trigger;

import com.hertz.mdm.businessparty.path.BusinessPartyPathConfig;
import com.hertz.mdm.common.trigger.HtzSubjectTableTrigger;
import com.orchestranetworks.ps.project.path.SubjectPathConfig;

/**
 */
public class BusinessPartyTableTrigger extends HtzSubjectTableTrigger
{
	@Override
	public SubjectPathConfig getSubjectPathConfig()
	{
		return BusinessPartyPathConfig.getInstance();
	}
}
