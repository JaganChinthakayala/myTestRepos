package com.orchestranetworks.ps.project.servicepermission;

import com.onwbp.adaptation.*;
import com.onwbp.base.text.*;
import com.orchestranetworks.ps.project.path.*;
import com.orchestranetworks.ps.project.util.*;
import com.orchestranetworks.ps.servicepermission.*;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.service.*;

public abstract class SubjectAlreadyInWorkflowServicePermission
	extends
	MasterDataSpaceOnlyServicePermission implements ProjectPathCapable
{
	protected abstract SubjectPathConfig getSubjectPathConfig(Adaptation adaptation);

	@Override
	public ActionPermission getPermission(
		SchemaNode schemaNode,
		Adaptation adaptation,
		Session session)
	{
		ActionPermission permission = super.getPermission(schemaNode, adaptation, session);
		if (!ActionPermission.getEnabled().equals(permission))
		{
			return permission;
		}
		if (adaptation.isSchemaInstance())
		{
			return ActionPermission.getEnabled();
		}

		return verifySubjectNotInWorkflow(adaptation, session);
	}

	protected ActionPermission verifySubjectNotInWorkflow(Adaptation adaptation, Session session)
	{
		SubjectPathConfig subjectPathConfig = getSubjectPathConfig(adaptation);
		Path currentProjectTypeFieldPath = subjectPathConfig.getSubjectCurrentProjectTypeFieldPath();
		if (currentProjectTypeFieldPath != null)
		{
			String currentProjectType = adaptation.getString(currentProjectTypeFieldPath);
			if (currentProjectType != null)
			{
				AdaptationTable table = adaptation.getContainerTable();
				String tableLabel = table.getTableNode().getLabel(session.getLocale());
				return ActionPermission.getHidden(UserMessage.createInfo("A " + currentProjectType
					+ " project is already in process for this " + tableLabel + "."));
			}
		}

		ProjectPathConfig projectPathConfig = getProjectPathConfig();
		// Check for in-process projects. Current Project Type doesn't necessarily cover this because there may be
		// converted in-process projects that haven't been "resumed" yet.
		Adaptation inProcessProjectRecord = ProjectUtil.getInProcessProjectForSubject(
			adaptation,
			projectPathConfig,
			subjectPathConfig);
		if (inProcessProjectRecord != null)
		{
			String subjectTableLabel = adaptation.getContainerTable()
				.getTableNode()
				.getLabel(session.getLocale());
			return ActionPermission.getHidden(UserMessage.createInfo("This service can't be invoked for a "
				+ subjectTableLabel
				+ " associated with a project with status "
				+ inProcessProjectRecord.getString(projectPathConfig.getProjectStatusFieldPath())
				+ "."));
		}
		return ActionPermission.getEnabled();
	}
}