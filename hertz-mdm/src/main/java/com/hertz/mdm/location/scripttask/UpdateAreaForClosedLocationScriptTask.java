/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.scripttask;

import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.workflow.ScriptTask;
import com.orchestranetworks.workflow.ScriptTaskContext;

public class UpdateAreaForClosedLocationScriptTask extends ScriptTask
{
	@Override
	public void executeScript(ScriptTaskContext context) throws OperationException
	{
		/*		Adaptation projectRecord = LocationProjectWorkflowUtilities
					.getLocationProjectRecord(context, context.getRepository());
		
				if (projectRecord == null)
				{
					return;
				}
		
				Adaptation locationRecord = AdaptationUtil
					.followFK(projectRecord, LocationPaths._Root_LocationProject._Location);
		
				if (locationRecord == null)
				{
					return;
				}
		
				AdaptationTable locationAreaRelationshipTable = projectRecord.getContainer()
					.getTable(LocationPaths._Root_LocationData_LocationAreaRelationship.getPathInSchema());
		
				String locationId = locationRecord.get(LocationPaths._Root_Location._Id).toString();
		
				// Query the Location/Area Relationship table for this Location
				RequestResult requestResult = locationAreaRelationshipTable.createRequestResult(
					LocationPaths._Root_LocationData_LocationAreaRelationship._Location.format() + "='"
						+ locationId + "' and"
						+ LocationPaths._Root_LocationData_LocationAreaRelationship._IsRecordActive.format()
						+ "=true");
				;
		
				for (int i = 0; i < requestResult.getSize(); i++)
				{
					Adaptation locationAreaRelationshipRecord = requestResult.nextAdaptation();
					HashMap<Path, Object> pathValueMap = new HashMap<Path, Object>();
					pathValueMap.put(
						LocationPaths._Root_LocationData_LocationAreaRelationship._EffectiveStartEndDates_EndDate,
						new Date());
					ModifyValuesProcedure.execute(
						locationAreaRelationshipRecord,
						pathValueMap,
						context.getSession(),
						true,
						false);
				}
			}*/
	}
}
