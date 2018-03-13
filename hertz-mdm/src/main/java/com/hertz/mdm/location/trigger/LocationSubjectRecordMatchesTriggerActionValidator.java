/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.trigger;

import com.hertz.mdm.location.path.LocationProjectPathConfig;
import com.orchestranetworks.ps.project.path.ProjectPathConfig;
import com.orchestranetworks.ps.project.trigger.SubjectRecordMatchesTriggerActionValidator;
import com.orchestranetworks.ps.trigger.TriggerActionValidator;
import com.orchestranetworks.schema.Path;

/**
 */
public class LocationSubjectRecordMatchesTriggerActionValidator
	extends
	SubjectRecordMatchesTriggerActionValidator
{
	public LocationSubjectRecordMatchesTriggerActionValidator()
	{
		this(null);
	}

	public LocationSubjectRecordMatchesTriggerActionValidator(Path subjectRecordFieldPath)
	{
		this(subjectRecordFieldPath, null);
	}

	public LocationSubjectRecordMatchesTriggerActionValidator(
		Path subjectRecordFieldPath,
		TriggerActionValidator nestedTriggerActionValidator)
	{
		super(subjectRecordFieldPath, nestedTriggerActionValidator);
	}

	@Override
	public ProjectPathConfig getProjectPathConfig()
	{
		return LocationProjectPathConfig.getInstance();
	}

}
