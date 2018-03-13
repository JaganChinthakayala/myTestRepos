/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.businessparty.scripttask;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.hertz.mdm._hertz.constants.HtzConstants;
import com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.repository.RepositoryUtils;
import com.hertz.mdm._hertz.path.DirectoryPaths;
import com.hertz.mdm.businessparty.constants.BusinessPartyConstants;
import com.hertz.mdm.businessparty.path.BusinessPartyPaths;
import com.hertz.mdm.businessparty.path.BusinessPartyReferencePaths;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.RequestResult;
import com.orchestranetworks.ps.procedure.CreateRecordProcedure;
import com.orchestranetworks.ps.procedure.ModifyValuesProcedure;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.workflow.ScriptTask;
import com.orchestranetworks.workflow.ScriptTaskContext;

public class SynchronizeBusinessPartyReferenceDatasetToEBXDirectoryScriptTask extends ScriptTask
{
	@Override
	public void executeScript(ScriptTaskContext context) throws OperationException
	{
		//Process all of the EBX Roles and Add to the Business Party Reference Domain if missing

		AdaptationTable rolesTable = RepositoryUtils.getTable(
			RepositoryUtils
				.getDataSet(HtzConstants.DIRECTORY_DATA_SPACE, HtzConstants.DIRECTORY_DATA_SET),
			DirectoryPaths._Root_Roles.getPathInSchema().format());

		AdaptationTable businessPartyRoleTypeTable = RepositoryUtils.getTable(
			RepositoryUtils.getDataSet(
				HtzConstants.BUSINESS_PARTY_DATA_SPACE,
				HtzConstants.BUSINESS_PARTY_REFERENCE_DATA_SET),
			BusinessPartyReferencePaths._Root_BusinessPartyRoleType.getPathInSchema().format());

		RequestResult requestResultBusinessPartyRole = null;
		RequestResult requestResultRoles = null;

		// Query the Directory Table to get all EBX Roles
		requestResultRoles = rolesTable.createRequestResult(
			"osd:is-not-null(" + DirectoryPaths._Root_Roles._name.format() + ")");

		//For each EBX User, query to see if is already in the Business Party Domain
		for (int i = 0; i < requestResultRoles.getSize(); i++)
		{
			Adaptation roleRecord = requestResultRoles.nextAdaptation();

			String roleId = roleRecord.get(DirectoryPaths._Root_Roles._name).toString();

			// Query the Business Party Role table for this Role to determine if the User is already in the Business Party Role table
			requestResultBusinessPartyRole = businessPartyRoleTypeTable.createRequestResult(
				BusinessPartyReferencePaths._Root_BusinessPartyRoleType._Base_Id.format() + "='"
					+ roleId + "' and "
					+ BusinessPartyPaths._Root_BusinessParty._IsRecordActive.format() + "=true");

			if (requestResultBusinessPartyRole.isEmpty())
			{
				Map<Path, Object> pathValueMap = new HashMap<Path, Object>();

				pathValueMap
					.put(BusinessPartyReferencePaths._Root_BusinessPartyRoleType._Base_Id, roleId);
				pathValueMap.put(
					BusinessPartyReferencePaths._Root_BusinessPartyRoleType._Base_Name,
					roleId);
				pathValueMap.put(
					BusinessPartyReferencePaths._Root_BusinessPartyRoleType._BusinessPartyType,
					BusinessPartyConstants.BUSINESS_PARTY_ROLE_TYPE_INDIVIDUAL);
				pathValueMap.put(
					BusinessPartyReferencePaths._Root_BusinessPartyRoleType._RoleCategory,
					BusinessPartyConstants.BUSINESS_PARTY_ROLE_CATEGORY_EBX);
				pathValueMap.put(
					BusinessPartyPaths._Root_BusinessParty._EffectiveStartEndDates_StartDate,
					new Date());

				CreateRecordProcedure.execute(
					businessPartyRoleTypeTable,
					pathValueMap,
					context.getSession(),
					true,
					false);
			}
		}

		// Expire all Business Party Roles that are no longer defined

		Date today = new Date();

		// Query the Business Party Role table for this Role to determine if the User is already in the Business Party Role table
		requestResultBusinessPartyRole = businessPartyRoleTypeTable.createRequestResult(
			"osd:is-not-null("
				+ BusinessPartyReferencePaths._Root_BusinessPartyRoleType._Base_Id.format()
				+ ") and " + BusinessPartyPaths._Root_BusinessParty._IsRecordActive.format()
				+ "=true");

		for (int i = 0; i < requestResultBusinessPartyRole.getSize(); i++)
		{
			Adaptation businessPartyRoleRecord = requestResultBusinessPartyRole.nextAdaptation();

			String businessPartyRole = businessPartyRoleRecord
				.getString(BusinessPartyReferencePaths._Root_BusinessPartyRoleType._Base_Id)
				.toString();

			// Query the Directory Table to get the Role
			requestResultRoles = rolesTable.createRequestResult(
				DirectoryPaths._Root_Roles._name.format() + "='" + businessPartyRole + "'");

			if (requestResultRoles.isEmpty())
			{
				//Expire the Role
				Map<Path, Object> pathValueMap = new HashMap<Path, Object>();

				pathValueMap.put(
					BusinessPartyPaths._Root_BusinessParty._EffectiveStartEndDates_EndDate,
					today);

				ModifyValuesProcedure.execute(
					businessPartyRoleRecord,
					pathValueMap,
					context.getSession(),
					true,
					false);
			}

		}

		if (requestResultBusinessPartyRole != null)
		{
			requestResultBusinessPartyRole.close();
		}
		if (requestResultRoles != null)
		{
			requestResultRoles.close();
		}

		return;
	}
}
