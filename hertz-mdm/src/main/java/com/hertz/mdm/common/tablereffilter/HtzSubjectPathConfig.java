/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.common.tablereffilter;

import com.orchestranetworks.ps.project.path.*;
import com.orchestranetworks.schema.*;

public interface HtzSubjectPathConfig extends SubjectPathConfig
{
	Path getProjectSubjectBrandFieldPath();

	Path getSubjectBrandFieldPath();

	Path getProjectSubjectRegionFieldPath();

	Path getSubjectRegionFieldPath();
}
