/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.common.trigger;

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
		/*
		if (subjectBrandPath != null)
		{
			String brand = WorkflowUtilities.getSessionInteractionParameter(
				context.getSession(),
				HtzProjectWorkflowConstants.SESSION_PARAM_BRAND);
			// Old workflows won't have brand in session interaction, so default it to BK
			if (brand == null)
			{
				brand = HtzReferenceConstants.BRAND_BURGER_KING;
			}
			pathValueMap.put(subjectBrandPath, brand);
		} */
		Path subjectRegionPath = subjectPathConfig.getSubjectRegionFieldPath();
		/*		if (subjectRegionPath != null)
				{
					String region = WorkflowUtilities.getSessionInteractionParameter(
						context.getSession(),
						HtzProjectWorkflowConstants.SESSION_PARAM_REGION);
		
					pathValueMap.put(subjectRegionPath, region);
				} */
		return pathValueMap;
	}

	@Override
	public void handleNewContext(NewTransientOccurrenceContext context)
	{
		super.handleNewContext(context);
		HtzProjectPathConfig projectPathConfig = (HtzProjectPathConfig) getProjectPathConfig();
		/*Path brandPath = projectPathConfig.getProjectBrandFieldPath();
		HtzReferenceUtilities.setDefaultBrandRegionInValueContext(
			context.getOccurrenceContextForUpdate(),
			context.getSession(),
			brandPath,
			projectPathConfig.getProjectRegionFieldPath());*/
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
		//Path brandPath = pathConfig.getProjectBrandFieldPath();
		// If brand-enabled, wrap the project validator inside a brand/region one.
		// Otherwise just return the project validator.
		/*return brandPath == null ? projectValidator
			: new BrandRegionDefaultingTriggerActionValidator(
				brandPath,
				pathConfig.getProjectRegionFieldPath(),
				projectValidator); */
		return null;
	}
}
