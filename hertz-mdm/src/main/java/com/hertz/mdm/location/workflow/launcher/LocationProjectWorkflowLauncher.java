package com.hertz.mdm.location.workflow.launcher;

import java.util.Locale;

import com.hertz.mdm._hertz.enums.Domains;
import com.hertz.mdm._hertz.workflow.launcher.HtzProjectWorkflowLauncher;
import com.hertz.mdm.location.constants.LocationProjectWorkflowConstants;
import com.hertz.mdm.location.path.LocationProjectPathConfig;
import com.orchestranetworks.ps.project.path.ProjectPathConfig;
import com.orchestranetworks.service.OperationException;

public class LocationProjectWorkflowLauncher extends HtzProjectWorkflowLauncher
{
	private static final long serialVersionUID = 1L;

	@Override
	protected String getProjectMasterWorkflowKey()
	{
		return LocationProjectWorkflowConstants.WF_KEY_CLOSE_LOCATION_PROJECT_MASTER;
	}

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
	protected void setAdditionalContextVariables() throws OperationException
	{
		super.setAdditionalContextVariables();
		/*		launcher.setInputParameter(
					RestaurantProjectWorkflowConstants.PARAM_APPROVAL_TYPE,
					workflowLauncherContext.getCurrentAdaptation()
						.getString(BkRestaurantPaths._Root_RestaurantProject._ApprovalType));
						*/
	}

	@Override
	protected String getWorkflowModelLabelForInstanceName(String workflowModelName, Locale locale)
	{
		return "Project Master";
	}

	/*	@Override
		protected String enrichWorkflowInstanceName(String workflowInstanceName, Locale locale)
		{
			String label = super.enrichWorkflowInstanceName(workflowInstanceName, locale);
			String appendedRegionLabel = " - Region: " + workflowLauncherContext.getCurrentAdaptation()
				.getString(BkRestaurantPaths._Root_RestaurantProject._Region);
			return label + appendedRegionLabel;
		} */
}
