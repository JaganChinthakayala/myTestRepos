/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.trigger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hertz.mdm._hertz.constants.HtzConstants;
import com.hertz.mdm._hertz.constants.HtzWorkflowConstants;
import com.hertz.mdm.common.trigger.HtzProjectTableTrigger;
import com.hertz.mdm.location.deepcopy.DuplicateSelectedLocationSubjectDeepCopyDataModifier;
import com.hertz.mdm.location.deepcopy.DuplicateSelectedLocationSubjectDeepCopyRule;
import com.hertz.mdm.location.deepcopy.LocationDeepCopyConfigFactory;
import com.hertz.mdm.location.enums.LocationProjectTypes;
import com.hertz.mdm.location.path.LocationPaths;
import com.hertz.mdm.location.path.LocationProjectPathConfig;
import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.ps.deepcopy.DeepCopyService;
import com.orchestranetworks.ps.procedure.ModifyValuesProcedure;
import com.orchestranetworks.ps.project.path.ProjectPathConfig;
import com.orchestranetworks.ps.project.workflow.ProjectWorkflowConstants;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.ps.workflow.WorkflowConstants;
import com.orchestranetworks.ps.workflow.WorkflowUtilities;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.trigger.AfterCreateOccurrenceContext;
import com.orchestranetworks.schema.trigger.BeforeCreateOccurrenceContext;
import com.orchestranetworks.schema.trigger.BeforeModifyOccurrenceContext;
import com.orchestranetworks.schema.trigger.NewTransientOccurrenceContext;
import com.orchestranetworks.schema.trigger.ValueChanges;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.ProcedureContext;
import com.orchestranetworks.service.Session;
import com.orchestranetworks.service.ValueContextForUpdate;

/**
 */
public class LocationProjectTableTrigger extends HtzProjectTableTrigger
{
	@Override
	public ProjectPathConfig getProjectPathConfig()
	{
		return LocationProjectPathConfig.getInstance();
	}

	@Override
	protected Adaptation createSubject(AfterCreateOccurrenceContext context, String projectType)
		throws OperationException
	{
		String subjectRecordXPath = WorkflowUtilities.getSessionInteractionParameter(
			context.getSession(),
			WorkflowConstants.SESSION_PARAM_RECORD);
		Adaptation subjectRecord;
		// If we're not duplicating an existing subject, then create the new subject giving it its defaults
		if (subjectRecordXPath == null)
		{
			subjectRecord = super.createSubject(context, projectType);
		}
		// Else we're duplicating an existing one so utilize the deep copy to copy it
		else
		{
			Adaptation oldSubjectRecord = AdaptationUtil.getRecord(
				subjectRecordXPath,
				context.getTable().getContainerAdaptation(),
				true,
				true);
			DeepCopyService service = new DeepCopyService(new LocationDeepCopyConfigFactory());
			ProcedureContext pContext = context.getProcedureContext();

			// Get what the trigger would have created the subject name to be and pass that into the deep copier
			// because we want to copy everything else, except default the name to the project name.
			// The newSubjectPathValueMap handles trimming the name also.
			Map<Path, Object> newSubjectPathValueMap = getNewSubjectPathValueMap(
				context,
				projectType);
			Path subjectNameFieldPath = getProjectPathConfig().getSubjectPathConfig(projectType)
				.getSubjectNameFieldPath();
			String subjectName = (String) newSubjectPathValueMap.get(subjectNameFieldPath);

			subjectRecord = service.deepCopy(
				pContext,
				oldSubjectRecord,
				null,
				new DuplicateSelectedLocationSubjectDeepCopyDataModifier(subjectName),
				new DuplicateSelectedLocationSubjectDeepCopyRule(),
				null);
		}
		return subjectRecord;
	}

	@Override
	public void handleNewContext(NewTransientOccurrenceContext context)
	{
		super.handleNewContext(context);
		ValueContextForUpdate vc = context.getOccurrenceContextForUpdate();
		Session session = context.getSession();
		if (session.getInteraction(true) != null)
		{
			String projectType = WorkflowUtilities.getSessionInteractionParameter(
				session,
				ProjectWorkflowConstants.SESSION_PARAM_PROJECT_TYPE);

			if (LocationProjectTypes.OPEN_LOCATION.equals(projectType))
			{
				vc.setValue(
					"Open Location" + " - " + new Date().toString(),
					LocationPaths._Root_LocationProject._Name);
				projectType = LocationProjectTypes.OPEN_LOCATION;
			}
			else if (LocationProjectTypes.CLOSE_LOCATION.equals(projectType))
			{
				vc.setValue(
					"Close Location" + " - " + new Date().toString(),
					LocationPaths._Root_LocationProject._Name);
				projectType = LocationProjectTypes.CLOSE_LOCATION;
			}
			else if (LocationProjectTypes.MOVE_LOCATION.equals(projectType))
			{
				vc.setValue(
					"Move Location" + " - " + new Date().toString(),
					LocationPaths._Root_LocationProject._Name);
				projectType = LocationProjectTypes.MOVE_LOCATION;
			}
			else if (LocationProjectTypes.MAINTAIN_LOCATION.equals(projectType))
			{
				vc.setValue(
					"Change Location" + " - " + new Date().toString(),
					LocationPaths._Root_LocationProject._Name);
				projectType = LocationProjectTypes.MAINTAIN_LOCATION;
			}
			vc.setValue(
				new Date(),
				LocationPaths._Root_LocationProject._ProjectStartEndDates_StartDate);
			vc.setValue(
				new Date(),
				LocationPaths._Root_LocationProject._EffectiveStartEndDates_StartDate);
			String domain = WorkflowUtilities.getSessionInteractionParameter(
				session,
				ProjectWorkflowConstants.SESSION_PARAM_DOMAIN);
			domain = HtzConstants.LOCATION_DOMAIN;
			vc.setValue(projectType, LocationPaths._Root_LocationProject._ProjectType);
			vc.setValue(domain, LocationPaths._Root_LocationProject._Domain);

			//Set Project Expiration Date for 1 months from today
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MONTH, +1);
			vc.setValue(
				calendar.getTime(),
				LocationPaths._Root_LocationProject._ProjectExpirationDate);
		}
	}

	@Override
	public void handleBeforeCreate(BeforeCreateOccurrenceContext context) throws OperationException
	{
		String originalRecordXPath = WorkflowUtilities.getSessionInteractionParameter(
			context.getSession(),
			ProjectWorkflowConstants.SESSION_PARAM_RECORD);

		if (originalRecordXPath != null)
		{
			ValueContextForUpdate vc = context.getOccurrenceContextForUpdate();
			Adaptation originalRecord = AdaptationUtil
				.getRecord(originalRecordXPath, vc.getAdaptationInstance(), true, false);
			if (originalRecord != null)
			{
				String originalRecordPK = originalRecord.getOccurrencePrimaryKey().format();
				vc.setValue(originalRecordPK, LocationPaths._Root_LocationProject._Location);
			}
		}

		List<String> locationProjectApprovalRoles = new ArrayList<String>();
		ValueContextForUpdate updateContext = context.getOccurrenceContextForUpdate();

		for (int i = 0; i < com.hertz.mdm.location.enums.LocationProjectApprovalRoles.LOCATION_PROJECT_APPROVERS.length; i++)
		{
			locationProjectApprovalRoles.add(
				com.hertz.mdm.location.enums.LocationProjectApprovalRoles.LOCATION_PROJECT_APPROVERS[i]);
		}

		updateContext.setValue(
			locationProjectApprovalRoles,
			LocationPaths._Root_LocationProject._ProjectApprovalRoles);

		super.handleBeforeCreate(context);
	}

	@Override
	public void handleBeforeModify(BeforeModifyOccurrenceContext context) throws OperationException
	{
		ValueChanges changes = context.getChanges();
		if (changes.getChange(
			LocationPaths._Root_LocationProject._CloseLocation_ProjectedCloseDate) != null)
		{
			context.getOccurrenceContextForUpdate().setValue(
				Boolean.TRUE,
				LocationPaths._Root_LocationProject._ProjectedCloseDateChanged);
		}
		else
		{
			context.getOccurrenceContextForUpdate().setValue(
				Boolean.FALSE,
				LocationPaths._Root_LocationProject._ProjectedCloseDateChanged);
		}
		super.handleBeforeModify(context);
	}

	@Override
	public void handleAfterCreate(AfterCreateOccurrenceContext context) throws OperationException
	{
		super.handleAfterCreate(context);

		Session session = context.getSession();

		if (session.getInteraction(true) != null)
		{
			String launchLocationId = WorkflowUtilities.getSessionInteractionParameter(
				context.getSession(),
				HtzWorkflowConstants.DATA_CONTEXT_LAUNCH_LOCATION_ID);

			if (launchLocationId != null)
			{
				Adaptation projectRecord = context.getAdaptationOccurrence();

				HashMap<Path, Object> pathValueMap = new HashMap<Path, Object>();
				pathValueMap.put(LocationPaths._Root_LocationProject._Location, launchLocationId);

				ModifyValuesProcedure.execute(
					context.getProcedureContext(),
					projectRecord,
					pathValueMap,
					true,
					false);
			}
		}
	}
}
