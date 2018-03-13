/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.procedure;

import java.text.*;
import java.util.*;

import com.onwbp.adaptation.*;
import com.onwbp.base.text.*;
import com.orchestranetworks.instance.*;
import com.orchestranetworks.ps.constants.*;
import com.orchestranetworks.service.*;

/**
 */
public class ProcedureExecutor
{
	public static void executeProcedure(
		final Procedure procedure,
		final Session session,
		final Adaptation adaptation) throws OperationException
	{
		AdaptationHome dataSpace = adaptation.isSchemaInstance() ? adaptation.getHome()
			: adaptation.getContainer().getHome();
		executeProcedure(procedure, session, dataSpace);
	}

	public static void executeProcedure(
		final Procedure procedure,
		final Session session,
		final AdaptationHome dataSpace) throws OperationException
	{
		executeProcedure(procedure, session, dataSpace, null);
	}

	public static void executeProcedure(
		final Procedure procedure,
		final Session session,
		final AdaptationHome dataSpace,
		final String trackingInfo) throws OperationException
	{
		String oldTrackingInfo = session.getTrackingInfo();
		ProgrammaticService svc = ProgrammaticService.createForSession(session, dataSpace);
		if (trackingInfo != null)
		{
			svc.setSessionTrackingInfo(trackingInfo);
		}
		OperationException ex;
		try
		{
			ProcedureResult procResult = svc.execute(procedure);
			ex = procResult.getException();
		}
		finally
		{
			if (trackingInfo != null)
			{
				svc.setSessionTrackingInfo(oldTrackingInfo);
			}
		}
		if (ex != null)
		{
			throw ex;
		}
	}

	public static void executeProcedureInChild(
		final Procedure procedure,
		final Session session,
		final AdaptationHome dataSpace,
		final String childDataSpaceLabelPrefix,
		final String permissionsTemplateDataSpaceName) throws OperationException
	{
		executeProcedureInChild(
			procedure,
			session,
			dataSpace,
			childDataSpaceLabelPrefix,
			permissionsTemplateDataSpaceName,
			null);
	}

	public static void executeProcedureInChild(
		final Procedure procedure,
		final Session session,
		final AdaptationHome dataSpace,
		final String childDataSpaceLabelPrefix,
		final String permissionsTemplateDataSpaceName,
		final String trackingInfo) throws OperationException
	{
		AdaptationHome childDataSpace = createChildDataSpace(
			session,
			dataSpace,
			childDataSpaceLabelPrefix,
			permissionsTemplateDataSpaceName);
		try
		{
			executeProcedure(procedure, session, childDataSpace, trackingInfo);
		}
		catch (OperationException ex)
		{
			try
			{
				closeChildDataSpace(session, childDataSpace);
			}
			catch (OperationException ex2)
			{
				LoggingCategory.getKernel().error(
					"Error closing child data space "
						+ childDataSpace.getLabelOrName(session.getLocale()) + ".",
					ex2);
			}
			throw ex;
		}
		mergeChildDataSpace(session, dataSpace, childDataSpace);
	}

	private static AdaptationHome createChildDataSpace(
		final Session session,
		final AdaptationHome dataSpace,
		final String childDataSpaceLabelPrefix,
		final String permissionsTemplateDataSpaceName) throws OperationException
	{
		UserReference user = session.getUserReference();

		HomeCreationSpec homeCreationSpec = new HomeCreationSpec();
		SimpleDateFormat dateFormat = new SimpleDateFormat(
			CommonConstants.DATA_SPACE_NAME_DATE_TIME_FORMAT);
		String childDataSpaceDateTimeStr = dateFormat.format(new Date());
		homeCreationSpec.setKey(HomeKey.forBranchName(childDataSpaceDateTimeStr));
		homeCreationSpec.setOwner(user);
		homeCreationSpec.setParent(dataSpace);
		if (childDataSpaceLabelPrefix != null)
		{
			homeCreationSpec.setLabel(UserMessage.createInfo(childDataSpaceLabelPrefix
				+ childDataSpaceDateTimeStr));
		}

		Repository repo = dataSpace.getRepository();
		if (permissionsTemplateDataSpaceName != null)
		{
			AdaptationHome templateDataSpace = repo.lookupHome(HomeKey.forBranchName(permissionsTemplateDataSpaceName));
			if (templateDataSpace == null)
			{
				LoggingCategory.getKernel().error(
					"Permissions template data space " + permissionsTemplateDataSpaceName
						+ " not found.");
			}
			else
			{
				homeCreationSpec.setHomeToCopyPermissionsFrom(templateDataSpace);
			}
		}
		return repo.createHome(homeCreationSpec, session);
	}

	private static void closeChildDataSpace(Session session, AdaptationHome childDataSpace)
		throws OperationException
	{
		childDataSpace.getRepository().closeHome(childDataSpace, session);
	}

	private static void mergeChildDataSpace(
		final Session session,
		final AdaptationHome dataSpace,
		final AdaptationHome childDataSpace) throws OperationException
	{
		Procedure mergeProcedure = new Procedure()
		{
			@Override
			public void execute(ProcedureContext pContext) throws Exception
			{
				// We could allow passing in of a Merge Spec if we need more fine-grained capabilities.
				// By default merges everything from all data sets
				pContext.doMergeToParent(childDataSpace);
			}
		};
		executeProcedure(mergeProcedure, session, dataSpace);
	}

	private ProcedureExecutor()
	{
	}
}
