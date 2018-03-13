/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.scripttask;

import com.hertz.mdm._hertz.constants.HtzConstants;
import com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.repository.RepositoryUtils;
import com.hertz.mdm.location.path.LocationPaths;
import com.hertz.mdm.location.util.HtzLocationUtilities;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.RequestResult;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.workflow.ScriptTask;
import com.orchestranetworks.workflow.ScriptTaskContext;

public class SetPrimaryCurrencyScriptTask extends ScriptTask
{
	@Override
	public void executeScript(ScriptTaskContext context) throws OperationException
	{
		//Read all of the Location Records
		AdaptationTable locationTable = RepositoryUtils.getTable(
			RepositoryUtils
				.getDataSet(HtzConstants.LOCATION_DATA_SPACE, HtzConstants.LOCATION_DATA_SET),
			LocationPaths._Root_Location.getPathInSchema().format());

		RequestResult requestResult = locationTable.createRequestResult(
			LocationPaths._Root_Location._ResetPrimaryCurrencyCode.format() + "=true");

		for (int i = 0; i < requestResult.getSize(); i++)
		{
			Adaptation locationRecord = requestResult.nextAdaptation();

			HtzLocationUtilities.setPrimaryCurrency(locationRecord, context.getSession());
		}
	}
}
