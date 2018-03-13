/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.condition;

import com.hertz.mdm._hertz.constants.HtzConstants;
import com.hertz.mdm._hertz.constants.HtzWorkflowConstants;
import com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.repository.RepositoryUtils;
import com.hertz.mdm.location.path.LocationPaths;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.RequestResult;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.workflow.Condition;
import com.orchestranetworks.workflow.ConditionContext;

public class DoesLocationHaveActiveChildrenCondition extends Condition
{
	@Override
	public boolean evaluateCondition(ConditionContext context) throws OperationException
	{
		String launchLocationId = context
			.getVariableString(HtzWorkflowConstants.DATA_CONTEXT_LAUNCH_LOCATION_ID);

		String locationRecordId = "";

		if (launchLocationId == null)
		{
			locationRecordId = context
				.getVariableString(HtzWorkflowConstants.DATA_CONTEXT_PARM_RECORD);

			int locationRecordIdEqualSign = locationRecordId.indexOf("=");

			locationRecordId = locationRecordId.substring(locationRecordIdEqualSign + 1);

			int locationRecordIdBracket = locationRecordId.indexOf("]");

			locationRecordId = locationRecordId.substring(0, locationRecordIdBracket);
		}
		else
		{
			locationRecordId = launchLocationId;
		}

		AdaptationTable locationLocationRelationshipTable = RepositoryUtils.getTable(
			RepositoryUtils
				.getDataSet(HtzConstants.LOCATION_DATA_SPACE, HtzConstants.LOCATION_DATA_SET),
			LocationPaths._Root_LocationData_LocationLocationRelationship.getPathInSchema()
				.format());

		// Query the LocationLocationRelatiohsipTable to see if there are records with this LocationId 
		RequestResult requestResult = locationLocationRelationshipTable.createRequestResult(
			LocationPaths._Root_LocationData_LocationLocationRelationship._ParentLocation.format()
				+ "='" + locationRecordId + "' and "
				+ LocationPaths._Root_LocationData_Address._IsRecordActive.format() + "=true");

		if (requestResult.isEmpty())
		{
			requestResult.close();
			return false;
		}
		else
		{
			requestResult.close();
			return true;
		}
	}
}
