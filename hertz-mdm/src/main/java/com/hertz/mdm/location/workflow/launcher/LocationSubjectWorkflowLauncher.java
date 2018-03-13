package com.hertz.mdm.location.workflow.launcher;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.hertz.mdm._hertz.enums.Domains;
import com.hertz.mdm._hertz.workflow.launcher.HtzSubjectWorkflowLauncher;
import com.hertz.mdm.location.constants.LocationProjectWorkflowConstants;
import com.hertz.mdm.location.enums.LocationProjectTypes;
import com.hertz.mdm.location.path.LocationProjectPathConfig;
import com.orchestranetworks.ps.project.path.ProjectPathConfig;
import com.orchestranetworks.ps.project.workflow.launcher.SubjectWorkflowLauncher;
import com.orchestranetworks.ps.workflow.WorkflowLauncherContext;
import com.orchestranetworks.service.OperationException;

public abstract class LocationSubjectWorkflowLauncher extends HtzSubjectWorkflowLauncher
{
	private static final long serialVersionUID = 1L;

	@Override
	public ProjectPathConfig getProjectPathConfig()
	{
		return LocationProjectPathConfig.getInstance();
	}

	@Override
	protected String getDomain()
	{
		return Domains.LOCATION;
	}

	@Override
	protected String getProjectMasterWorkflowKey(WorkflowLauncherContext context)
	{
		String projectType = context.getParameter(SubjectWorkflowLauncher.PARAM_PROJECT_TYPE);
		if (LocationProjectTypes.OPEN_LOCATION.equals(projectType))
		{
			return LocationProjectWorkflowConstants.WF_KEY_OPEN_LOCATION_PROJECT_MASTER;
		}
		else if (LocationProjectTypes.CLOSE_LOCATION.equals(projectType))
		{
			return LocationProjectWorkflowConstants.WF_KEY_CLOSE_LOCATION_PROJECT_MASTER;
		}
		else if (LocationProjectTypes.MOVE_LOCATION.equals(projectType))
		{
			return LocationProjectWorkflowConstants.WF_KEY_MOVE_LOCATION_PROJECT_MASTER;
		}
		return null;
	}

	public void execute(HttpServletRequest request, String projectType, String approvalType)
		throws OperationException
	{
		WorkflowLauncherContext context = new WorkflowLauncherContext(request);
		context.setParameter(SubjectWorkflowLauncher.PARAM_PROJECT_TYPE, projectType);
		execute(context);
	}

	public void execute(
		HttpServletRequest request,
		String projectType,
		String approvalType,
		boolean checkCurrentProjectType) throws OperationException
	{
		WorkflowLauncherContext context = new WorkflowLauncherContext(request);
		context.setParameter(SubjectWorkflowLauncher.PARAM_PROJECT_TYPE, projectType);
		execute(context, checkCurrentProjectType);
	}

	@Override
	protected void setAdditionalContextVariables() throws OperationException
	{
		super.setAdditionalContextVariables();
	}

	// For the restaurant master workflows, the label sounds redundant since it has the project type in it,
	// so just make it "Project Master".
	@Override
	protected String getWorkflowModelLabelForInstanceName(String workflowModelName, Locale locale)
	{
		return "Project Master";
	}
}
