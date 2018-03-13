/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm._hertz.tablerefilter;

import com.orchestranetworks.ps.project.path.SubjectPathConfig;
import com.orchestranetworks.schema.Path;

public interface HtzSubjectPathConfig extends SubjectPathConfig
{
	Path getProjectSubjectBrandFieldPath();

	Path getSubjectBrandFieldPath();

	Path getProjectSubjectRegionFieldPath();

	Path getSubjectRegionFieldPath();
}
