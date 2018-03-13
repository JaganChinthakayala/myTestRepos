package com.hertz.mdm._hertz.workflow.launcher;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.ps.project.workflow.launcher.SubjectWorkflowLauncher;
import com.orchestranetworks.ps.workflow.WorkflowLauncherContext;
import com.orchestranetworks.service.OperationException;

public abstract class HtzSubjectWorkflowLauncher extends SubjectWorkflowLauncher
{
	private static final long serialVersionUID = 1L;
	private boolean checkCurrentProjectType = true;

	protected abstract String getDomain();

	@Override
	public void execute(WorkflowLauncherContext context) throws OperationException
	{
		super.execute(context);
	}

	public void execute(WorkflowLauncherContext context, boolean checkCurrentProjectType)
		throws OperationException
	{
		this.checkCurrentProjectType = checkCurrentProjectType;
		super.execute(context);
	}

	@Override
	protected UserMessage checkCurrentProjectType(Adaptation subjectRecord)
	{
		if (checkCurrentProjectType)
		{
			String currentProjectType = subjectRecord
				.getString(getSubjectPathConfig().getSubjectCurrentProjectTypeFieldPath());
			return currentProjectType == null ? null
				: UserMessage.createError(
					"A " + currentProjectType
						+ " workflow is already in process for the selected record.");
		}
		return null;
	}

	@Override
	protected void initRedirectionPolicy()
	{
		workItemRedirectPolicy = WorkItemRedirectPolicyEnum.MASTER_WORKFLOW;
	}
}
