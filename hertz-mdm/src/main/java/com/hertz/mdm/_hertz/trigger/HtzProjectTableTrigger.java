/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm._hertz.trigger;

import java.util.Map;

import com.hertz.mdm._hertz.path.HtzProjectPathConfig;
import com.hertz.mdm.common.tablereffilter.HtzSubjectPathConfig;
import com.orchestranetworks.ps.project.path.ProjectPathConfig;
import com.orchestranetworks.ps.project.trigger.ProjectTableTrigger;
import com.orchestranetworks.ps.project.trigger.ProjectTriggerActionValidator;
import com.orchestranetworks.ps.trigger.TriggerActionValidator;
import com.orchestranetworks.ps.trigger.TriggerActionValidator.TriggerAction;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.trigger.AfterCreateOccurrenceContext;
import com.orchestranetworks.schema.trigger.NewTransientOccurrenceContext;
import com.orchestranetworks.service.OperationException;

/**
 */
public abstract class HtzProjectTableTrigger extends ProjectTableTrigger
{
	protected Map<Path, Object> getNewSubjectPathValueMap(
		AfterCreateOccurrenceContext context,
		String projectType) throws OperationException
	{
		ProjectPathConfig projectPathConfig = getProjectPathConfig();
		HtzSubjectPathConfig subjectPathConfig = (HtzSubjectPathConfig) projectPathConfig
			.getSubjectPathConfig(projectType);
		Map<Path, Object> pathValueMap = super.getNewSubjectPathValueMap(context, projectType);
		Path subjectBrandPath = subjectPathConfig.getSubjectBrandFieldPath();
		Path subjectRegionPath = subjectPathConfig.getSubjectRegionFieldPath();

		return pathValueMap;
	}

	@Override
	public void handleNewContext(NewTransientOccurrenceContext context)
	{
		super.handleNewContext(context);
		HtzProjectPathConfig projectPathConfig = (HtzProjectPathConfig) getProjectPathConfig();
	}

	@Override
	protected TriggerActionValidator createTriggerActionValidator(TriggerAction triggerAction)
	{
		final HtzProjectPathConfig pathConfig = (HtzProjectPathConfig) getProjectPathConfig();
		// TODO: Perhaps the base project trigger should do this? Not sure if that would impact other customers.
		TriggerActionValidator projectValidator = new ProjectTriggerActionValidator()
		{
			@Override
			public ProjectPathConfig getProjectPathConfig()
			{
				return pathConfig;
			}
		};
		return null;
	}
}
