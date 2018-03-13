/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.deepcopy;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.ps.deepcopy.DeepCopyConfig;
import com.orchestranetworks.ps.deepcopy.DeepCopyRule;

/**
 */
public class DuplicateSelectedLocationSubjectDeepCopyRule implements DeepCopyRule
{
	@Override
	public boolean executeCopy(Adaptation originalRecord, DeepCopyConfig config)
	{
		// Don't copy the GL Account Company Codes
		// (so in fact the deep copy isn't deep copying anything - but easier to follow the pattern we
		// established and if we need to, we can modify it later)
		//return !LocationPaths._Root_LocationProject.getPathInSchema()
		//	.equals(originalRecord.getSchemaNode().getPathInSchema());
		return true;
	}
}
