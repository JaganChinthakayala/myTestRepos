package com.hertz.mdm.location.workflow.launcher;

import com.hertz.mdm.location.path.LocationPathConfig;
import com.orchestranetworks.ps.project.path.SubjectPathConfig;

public class OpenLocationWorkflowLauncher extends LocationSubjectWorkflowLauncher
{
	private static final long serialVersionUID = 1L;

	@Override
	public SubjectPathConfig getSubjectPathConfig()
	{
		return LocationPathConfig.getInstance();
	}
}
