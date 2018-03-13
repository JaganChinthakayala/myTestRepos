/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.project.trigger;

import java.util.*;

import com.onwbp.adaptation.*;
import com.orchestranetworks.instance.*;
import com.orchestranetworks.ps.project.path.*;
import com.orchestranetworks.ps.trigger.*;
import com.orchestranetworks.ps.util.*;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.service.*;

/**
 */
public abstract class SubjectRecordMatchesTriggerActionValidator
	extends
	RecordMatchesTriggerActionValidator implements ProjectPathCapable
{
	protected SubjectRecordMatchesTriggerActionValidator()
	{
		super();
	}

	protected SubjectRecordMatchesTriggerActionValidator(Path subjectRecordFieldPath)
	{
		super(subjectRecordFieldPath, null);
	}

	protected SubjectRecordMatchesTriggerActionValidator(
		Path subjectRecordFieldPath,
		TriggerActionValidator nestedTriggerActionValidator)
	{
		super(subjectRecordFieldPath, nestedTriggerActionValidator);
	}

	@Override
	protected List<Adaptation> getRecordsToMatchFromSession(Session session, Repository repo)
		throws OperationException
	{
		List<Adaptation> projectRecords = super.getRecordsToMatchFromSession(session, repo);
		List<Adaptation> subjectRecords = new ArrayList<Adaptation>();
		if (projectRecords.isEmpty())
		{
			return subjectRecords;
		}
		Adaptation projectRecord = projectRecords.iterator().next();
		ProjectPathConfig projectPathConfig = getProjectPathConfig();
		String projectType = projectRecord.getString(projectPathConfig.getProjectProjectTypeFieldPath());
		Path projectSubjectsFieldPath = projectPathConfig.getProjectProjectSubjectsFieldPath(projectType);
		if (projectSubjectsFieldPath == null)
		{
			Adaptation subjectRecord = AdaptationUtil.followFK(
				projectRecord,
				projectPathConfig.getProjectSubjectFieldPath(projectType));
			if (subjectRecord != null)
			{
				subjectRecords.add(subjectRecord);
			}
		}
		else
		{
			subjectRecords = AdaptationUtil.getLinkedRecordList(
				projectRecord,
				projectSubjectsFieldPath);
		}
		return subjectRecords;
	}
}
