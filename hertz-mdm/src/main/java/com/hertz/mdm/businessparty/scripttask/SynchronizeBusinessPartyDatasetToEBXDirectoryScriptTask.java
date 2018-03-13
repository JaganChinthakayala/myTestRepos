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
import com.hertz.mdm.common.constants.HtzCommonConstants;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.RequestResult;
import com.orchestranetworks.ps.procedure.CreateRecordProcedure;
import com.orchestranetworks.ps.procedure.ModifyValuesProcedure;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.workflow.ScriptTask;
import com.orchestranetworks.workflow.ScriptTaskContext;

public class SynchronizeBusinessPartyDatasetToEBXDirectoryScriptTask extends ScriptTask
{
	@Override
	public void executeScript(ScriptTaskContext context) throws OperationException
	{
		//Process all of the EBX Users and Add to the Business Party Domain if missing and not a Permission User

		AdaptationTable usersTable = RepositoryUtils.getTable(
			RepositoryUtils
				.getDataSet(HtzConstants.DIRECTORY_DATA_SPACE, HtzConstants.DIRECTORY_DATA_SET),
			DirectoryPaths._Root_Users.getPathInSchema().format());

		AdaptationTable usersRolesTable = RepositoryUtils.getTable(
			RepositoryUtils
				.getDataSet(HtzConstants.DIRECTORY_DATA_SPACE, HtzConstants.DIRECTORY_DATA_SET),
			DirectoryPaths._UsersRoles.getPathInSchema().format());

		AdaptationTable businessPartyTable = RepositoryUtils.getTable(
			RepositoryUtils.getDataSet(
				HtzConstants.BUSINESS_PARTY_DATA_SPACE,
				HtzConstants.BUSINESS_PARTY_DATA_SET),
			BusinessPartyPaths._Root_BusinessParty.getPathInSchema().format());

		RequestResult requestResultBusinessParty = null;
		RequestResult requestResultUsers = null;
		RequestResult requestResultDirectoryUsersRoles = null;

		// Query the Directory Table to get all EBX Users
		requestResultUsers = usersTable.createRequestResult(
			"osd:is-not-null(" + DirectoryPaths._Root_Users._login.format() + ")");

		boolean isPermissionsUser = false;
		Adaptation userRecord = null;
		Adaptation businessPartyRecord = null;

		//For each EBX User, query to see if is already in the Business Party Domain
		for (int i = 0; i < requestResultUsers.getSize(); i++)
		{
			isPermissionsUser = false;

			userRecord = requestResultUsers.nextAdaptation();
			businessPartyRecord = null;

			String userId = userRecord.get(DirectoryPaths._Root_Users._login).toString();

			// Query the Business Party table for this user id to determine if the User is already in the Business Party Domain
			requestResultBusinessParty = businessPartyTable.createRequestResult(
				BusinessPartyPaths._Root_BusinessParty._EbxUser.format() + "='" + userId + "' and "
					+ BusinessPartyPaths._Root_BusinessParty._IsRecordActive.format() + "=true");

			if (requestResultBusinessParty.isEmpty())
			{
				// Query the Directory UsersRoles table to determine if this is a Permissions User
				requestResultDirectoryUsersRoles = usersRolesTable.createRequestResult(
					DirectoryPaths._UsersRoles._User.format() + "='" + userId + "'");

				//Loop through the Roles to determine if this is a Permissions User
				for (int j = 0; j < requestResultDirectoryUsersRoles.getSize(); j++)
				{
					Adaptation directoryUsersRoles = requestResultDirectoryUsersRoles
						.nextAdaptation();
					if (HtzConstants.EBX_ROLE_PERMISSIONS_USER
						.equals(directoryUsersRoles.get(DirectoryPaths._UsersRoles._Role)))
					{
						isPermissionsUser = true;
					}
				}
				if (!isPermissionsUser)
				{
					businessPartyRecord = addEBXUserToBusinessParty(
						context,
						userRecord,
						businessPartyRecord);
				}
			}
			else
			{
				//Determine if this Business Party Record has an EBX User
				businessPartyRecord = requestResultBusinessParty.nextAdaptation();
				String ebxUser = businessPartyRecord
					.get(BusinessPartyPaths._Root_BusinessParty._EbxUser).toString();
				if (ebxUser == null)
				{
					//Add the EBX User to the Business Party if not already there
					addEBXUserReferenceToBusinessPartyRecord(
						context,
						userRecord,
						businessPartyRecord);
				}
			}

			if (!isPermissionsUser)
			{
				// If there is an EBX Email, add it to the Business Party if its not already there
				synchronizeBusinessPartyEMailToEBXUserEMail(
					context,
					userRecord,
					businessPartyRecord);

				//If there any of the EBX phone numbers, add them to the Business Party if not already there
				synchronizeBusinessPartyPhoneNumbersToEBXUserPhoneNumbers(
					context,
					userRecord,
					businessPartyRecord);

				//Ensure that the Business Party has all the EBX roles
				synchronizexBusinessPartyRolesToEBXRoles(context, userRecord, businessPartyRecord);
			}
		}

		// Expire all Business Party Users that are no longer defined

		Date today = new Date();

		// Query all Business Party table
		requestResultBusinessParty = businessPartyTable.createRequestResult(
			"osd:is-not-null(" + BusinessPartyPaths._Root_BusinessParty._Id.format() + ") and "
				+ BusinessPartyPaths._Root_BusinessParty._IsRecordActive.format() + "=true");

		String ebxUserLogin = null;

		for (int i = 0; i < requestResultBusinessParty.getSize(); i++)
		{
			businessPartyRecord = requestResultBusinessParty.nextAdaptation();

			if (businessPartyRecord.get(BusinessPartyPaths._Root_BusinessParty._EbxUser) != null)
			{
				ebxUserLogin = businessPartyRecord
					.getString(BusinessPartyPaths._Root_BusinessParty._EbxUser).toString();
			}

			// If the EBX User Login is null, this is not an EBX Business Party
			if (ebxUserLogin != null)
			{
				// Query the Directory Table to get the Role
				requestResultUsers = usersTable.createRequestResult(
					DirectoryPaths._Root_Users._login.format() + "='" + ebxUserLogin + "'");

				if (requestResultUsers.isEmpty())
				{
					//Expire the Business Party
					Map<Path, Object> pathValueMap = new HashMap<Path, Object>();

					pathValueMap.put(BusinessPartyPaths._Root_BusinessParty._EbxUser, null);
					pathValueMap.put(
						BusinessPartyPaths._Root_BusinessParty._EffectiveStartEndDates_EndDate,
						today);

					ModifyValuesProcedure.execute(
						businessPartyRecord,
						pathValueMap,
						context.getSession(),
						true,
						false);

					expireBusinessPartyRolesForEBXUserPhoneNumbers(
						context,
						userRecord,
						businessPartyRecord);

					expireBusinessPartyEmailAddressesForEBXUserPhoneNumbers(
						context,
						userRecord,
						businessPartyRecord);

					expireBusinessPartyPhoneNumbersForEBXUserPhoneNumbers(
						context,
						userRecord,
						businessPartyRecord);
				}
			}
		}

		if (requestResultBusinessParty != null)
		{
			requestResultBusinessParty.close();
		}

		if (requestResultUsers != null)
		{
			requestResultUsers.close();
		}

		if (requestResultDirectoryUsersRoles != null)
		{
			requestResultDirectoryUsersRoles.close();
		}

		return;
	}

	private Adaptation addEBXUserToBusinessParty(
		ScriptTaskContext context,
		Adaptation userRecord,
		Adaptation businessPartyRecordx) throws OperationException
	{
		String login = userRecord.getString(DirectoryPaths._Root_Users._login);
		String name = userRecord.getString(DirectoryPaths._Root_Users._firstName) + " "
			+ userRecord.getString(DirectoryPaths._Root_Users._lastName);

		AdaptationTable businessPartyTable = RepositoryUtils.getTable(
			RepositoryUtils.getDataSet(
				HtzConstants.BUSINESS_PARTY_DATA_SPACE,
				HtzConstants.BUSINESS_PARTY_DATA_SET),
			BusinessPartyPaths._Root_BusinessParty.getPathInSchema().format());

		Map<Path, Object> pathValueMap = new HashMap<Path, Object>();

		pathValueMap.put(BusinessPartyPaths._Root_BusinessParty._Name, name);
		pathValueMap.put(BusinessPartyPaths._Root_BusinessParty._EbxUser, login);
		pathValueMap.put(
			BusinessPartyPaths._Root_BusinessParty._BusinessPartyType,
			BusinessPartyConstants.BUSINESS_PARTY_ROLE_TYPE_INDIVIDUAL);
		pathValueMap.put(
			BusinessPartyPaths._Root_BusinessParty._EffectiveStartEndDates_StartDate,
			new Date());

		return CreateRecordProcedure
			.execute(businessPartyTable, pathValueMap, context.getSession(), true, false);
	}

	private void addEBXUserReferenceToBusinessPartyRecord(
		ScriptTaskContext context,
		Adaptation userRecord,
		Adaptation businessPartyRecord) throws OperationException
	{
		String login = userRecord.getString(DirectoryPaths._Root_Users._login);

		Map<Path, Object> pathValueMap = new HashMap<Path, Object>();

		pathValueMap.put(BusinessPartyPaths._Root_BusinessParty._EbxUser, login);

		ModifyValuesProcedure
			.execute(businessPartyRecord, pathValueMap, context.getSession(), true, false);

		return;
	}

	private void synchronizexBusinessPartyRolesToEBXRoles(
		ScriptTaskContext context,
		Adaptation userRecord,
		Adaptation businessPartyRecord) throws OperationException
	{
		AdaptationTable usersRolesTable = RepositoryUtils.getTable(
			RepositoryUtils
				.getDataSet(HtzConstants.DIRECTORY_DATA_SPACE, HtzConstants.DIRECTORY_DATA_SET),
			DirectoryPaths._UsersRoles.getPathInSchema().format());

		AdaptationTable businessPartyRolesTable = RepositoryUtils.getTable(
			RepositoryUtils.getDataSet(
				HtzConstants.BUSINESS_PARTY_DATA_SPACE,
				HtzConstants.BUSINESS_PARTY_DATA_SET),
			BusinessPartyPaths._Root_BusinessPartyData_BusinessPartyRole.getPathInSchema()
				.format());

		RequestResult requestResultBusinessPartyRoles = null;
		RequestResult requestResultDirectoryUsersRoles = null;

		String userId = userRecord.get(DirectoryPaths._Root_Users._login).toString();
		String businessPartyId = businessPartyRecord.get(BusinessPartyPaths._Root_BusinessParty._Id)
			.toString();

		// Query the Directory UsersRoles table to determine if this is a Permissions User
		requestResultDirectoryUsersRoles = usersRolesTable
			.createRequestResult(DirectoryPaths._UsersRoles._User.format() + "='" + userId + "'");

		for (int i = 0; i < requestResultDirectoryUsersRoles.getSize(); i++)
		{
			Adaptation directoryUsersRolesRecord = requestResultDirectoryUsersRoles
				.nextAdaptation();

			String directoryRole = directoryUsersRolesRecord
				.getString(DirectoryPaths._UsersRoles._Role);

			requestResultBusinessPartyRoles = businessPartyRolesTable.createRequestResult(
				BusinessPartyPaths._Root_BusinessPartyData_BusinessPartyRole._BusinessParty.format()
					+ "='" + businessPartyId + "' and "
					+ BusinessPartyPaths._Root_BusinessPartyData_BusinessPartyRole._BusinessPartyType
						.format()
					+ "='" + BusinessPartyConstants.BUSINESS_PARTY_ROLE_TYPE_INDIVIDUAL + "' and "
					+ BusinessPartyPaths._Root_BusinessPartyData_BusinessPartyRole._BusinessPartyRoleType
						.format()
					+ "='" + directoryRole + "' and "
					+ BusinessPartyPaths._Root_BusinessPartyData_BusinessPartyRole._IsRecordActive
						.format()
					+ "=true");

			if (requestResultBusinessPartyRoles.isEmpty())
			{
				Map<Path, Object> pathValueMap = new HashMap<Path, Object>();

				pathValueMap.put(
					BusinessPartyPaths._Root_BusinessPartyData_BusinessPartyRole._BusinessParty,
					businessPartyId);
				pathValueMap.put(
					BusinessPartyPaths._Root_BusinessPartyData_BusinessPartyRole._BusinessPartyRoleType,
					directoryRole);
				pathValueMap.put(
					BusinessPartyPaths._Root_BusinessParty._EffectiveStartEndDates_StartDate,
					new Date());

				CreateRecordProcedure.execute(
					businessPartyRolesTable,
					pathValueMap,
					context.getSession(),
					true,
					false);
			}
		}

		if (requestResultBusinessPartyRoles != null)
		{
			requestResultBusinessPartyRoles.close();
		}

		if (requestResultDirectoryUsersRoles != null)
		{
			requestResultDirectoryUsersRoles.close();
		}

		return;
	}

	private void synchronizeBusinessPartyEMailToEBXUserEMail(
		ScriptTaskContext context,
		Adaptation userRecord,
		Adaptation businessPartyRecord) throws OperationException
	{
		String email = userRecord.getString(DirectoryPaths._Root_Users._email);

		if (email == null)
		{
			return;
		}

		String businessPartyId = businessPartyRecord.get(BusinessPartyPaths._Root_BusinessParty._Id)
			.toString();

		AdaptationTable businessPartyEmailTable = RepositoryUtils.getTable(
			RepositoryUtils.getDataSet(
				HtzConstants.BUSINESS_PARTY_DATA_SPACE,
				HtzConstants.BUSINESS_PARTY_DATA_SET),
			BusinessPartyPaths._Root_BusinessPartyData_Email.getPathInSchema().format());

		RequestResult requestResultBusinessPartyEmail = null;

		// Query the Business Party table for this user id to determine if the User is already in the Business Party Domain
		requestResultBusinessPartyEmail = businessPartyEmailTable.createRequestResult(
			BusinessPartyPaths._Root_BusinessPartyData_Email._BusinessParty.format() + "='"
				+ businessPartyId + "' and "
				+ BusinessPartyPaths._Root_BusinessPartyData_Email._EMailAddress.format() + "='"
				+ email + "' and "
				+ BusinessPartyPaths._Root_BusinessPartyData_Email._EMailType.format() + "='"
				+ HtzCommonConstants.COMMON_EMAIL_TYPE_BUSINESS + "' and "
				+ BusinessPartyPaths._Root_BusinessParty._IsRecordActive.format() + "=true");

		if (requestResultBusinessPartyEmail.isEmpty())
		{
			//Create the Business Party EMail Address
			Map<Path, Object> pathValueMap = new HashMap<Path, Object>();

			pathValueMap.put(
				BusinessPartyPaths._Root_BusinessPartyData_Email._BusinessParty,
				businessPartyId);
			pathValueMap.put(BusinessPartyPaths._Root_BusinessPartyData_Email._EMailAddress, email);
			pathValueMap.put(
				BusinessPartyPaths._Root_BusinessPartyData_Email._EMailType,
				HtzCommonConstants.COMMON_EMAIL_TYPE_BUSINESS);
			pathValueMap.put(
				BusinessPartyPaths._Root_BusinessParty._EffectiveStartEndDates_StartDate,
				new Date());

			CreateRecordProcedure
				.execute(businessPartyEmailTable, pathValueMap, context.getSession(), true, false);
		}

		if (requestResultBusinessPartyEmail != null)
		{
			requestResultBusinessPartyEmail.close();
		}

		return;
	}

	private void synchronizeBusinessPartyPhoneNumbersToEBXUserPhoneNumbers(
		ScriptTaskContext context,
		Adaptation userRecord,
		Adaptation businessPartyRecord) throws OperationException
	{
		String officePhoneNumber = userRecord
			.getString(DirectoryPaths._Root_Users._officePhoneNumber);
		String mobilePhoneNumber = userRecord
			.getString(DirectoryPaths._Root_Users._mobilePhoneNumber);
		String faxPhoneNumber = userRecord.getString(DirectoryPaths._Root_Users._faxPhoneNumber);

		AdaptationTable businessPartyPhoneTable = RepositoryUtils.getTable(
			RepositoryUtils.getDataSet(
				HtzConstants.BUSINESS_PARTY_DATA_SPACE,
				HtzConstants.BUSINESS_PARTY_DATA_SET),
			BusinessPartyPaths._Root_BusinessPartyData_Phone.getPathInSchema().format());

		String businessPartyId = businessPartyRecord.get(BusinessPartyPaths._Root_BusinessParty._Id)
			.toString();

		RequestResult requestResultOfficePhone = null;
		RequestResult requestResultMobilePhone = null;
		RequestResult requestResultFaxPhone = null;

		if (officePhoneNumber != null)
		{
			// Query the Business Party table for this user id to determine if the User is already in the Business Party Domain
			requestResultOfficePhone = businessPartyPhoneTable.createRequestResult(
				BusinessPartyPaths._Root_BusinessPartyData_Phone._BusinessParty.format() + "='"
					+ businessPartyId + "' and "
					+ BusinessPartyPaths._Root_BusinessPartyData_Phone._PhoneNumber.format() + "='"
					+ officePhoneNumber + "' and "
					+ BusinessPartyPaths._Root_BusinessPartyData_Phone._PhoneType.format() + "='"
					+ HtzCommonConstants.COMMON_PHONE_TYPE_BUSINESS + "' and "
					+ BusinessPartyPaths._Root_BusinessParty._IsRecordActive.format() + "=true");

			if (requestResultOfficePhone.isEmpty())
			{
				Map<Path, Object> pathValueMap = new HashMap<Path, Object>();

				pathValueMap.put(
					BusinessPartyPaths._Root_BusinessPartyData_Phone._BusinessParty,
					businessPartyId);
				pathValueMap.put(
					BusinessPartyPaths._Root_BusinessPartyData_Phone._PhoneNumber,
					officePhoneNumber);
				pathValueMap.put(
					BusinessPartyPaths._Root_BusinessPartyData_Phone._PhoneType,
					HtzCommonConstants.COMMON_PHONE_TYPE_BUSINESS);
				pathValueMap.put(
					BusinessPartyPaths._Root_BusinessParty._EffectiveStartEndDates_StartDate,
					new Date());

				CreateRecordProcedure.execute(
					businessPartyPhoneTable,
					pathValueMap,
					context.getSession(),
					true,
					false);
			}
		}

		if (mobilePhoneNumber != null)
		{
			// Query the Business Party table for this user id to determine if the User is already in the Business Party Domain
			requestResultMobilePhone = businessPartyPhoneTable.createRequestResult(
				BusinessPartyPaths._Root_BusinessPartyData_Phone._BusinessParty.format() + "='"
					+ businessPartyId + "' and "
					+ BusinessPartyPaths._Root_BusinessPartyData_Phone._PhoneNumber.format() + "='"
					+ mobilePhoneNumber + "' and "
					+ BusinessPartyPaths._Root_BusinessPartyData_Phone._PhoneType.format() + "='"
					+ HtzCommonConstants.COMMON_PHONE_TYPE_MOBILE + "' and "
					+ BusinessPartyPaths._Root_BusinessParty._IsRecordActive.format() + "=true");

			if (requestResultMobilePhone.isEmpty())
			{
				Map<Path, Object> pathValueMap = new HashMap<Path, Object>();

				pathValueMap.put(
					BusinessPartyPaths._Root_BusinessPartyData_Phone._BusinessParty,
					businessPartyId);
				pathValueMap.put(
					BusinessPartyPaths._Root_BusinessPartyData_Phone._PhoneNumber,
					mobilePhoneNumber);
				pathValueMap.put(
					BusinessPartyPaths._Root_BusinessPartyData_Phone._PhoneType,
					HtzCommonConstants.COMMON_PHONE_TYPE_MOBILE);
				pathValueMap.put(
					BusinessPartyPaths._Root_BusinessParty._EffectiveStartEndDates_StartDate,
					new Date());

				CreateRecordProcedure.execute(
					businessPartyPhoneTable,
					pathValueMap,
					context.getSession(),
					true,
					false);
			}
		}

		if (faxPhoneNumber != null)
		{
			// Query the Business Party table for this user id to determine if the User is already in the Business Party Domain
			requestResultFaxPhone = businessPartyPhoneTable.createRequestResult(
				BusinessPartyPaths._Root_BusinessPartyData_Phone._BusinessParty.format() + "='"
					+ businessPartyId + "' and "
					+ BusinessPartyPaths._Root_BusinessPartyData_Phone._PhoneNumber.format() + "='"
					+ faxPhoneNumber + "' and "
					+ BusinessPartyPaths._Root_BusinessPartyData_Phone._PhoneType.format() + "='"
					+ HtzCommonConstants.COMMON_PHONE_TYPE_FAX + "' and "
					+ BusinessPartyPaths._Root_BusinessParty._IsRecordActive.format() + "=true");

			if (requestResultFaxPhone.isEmpty())
			{
				Map<Path, Object> pathValueMap = new HashMap<Path, Object>();

				pathValueMap.put(
					BusinessPartyPaths._Root_BusinessPartyData_Phone._BusinessParty,
					businessPartyId);
				pathValueMap.put(
					BusinessPartyPaths._Root_BusinessPartyData_Phone._PhoneNumber,
					faxPhoneNumber);
				pathValueMap.put(
					BusinessPartyPaths._Root_BusinessPartyData_Phone._PhoneType,
					HtzCommonConstants.COMMON_PHONE_TYPE_FAX);
				pathValueMap.put(
					BusinessPartyPaths._Root_BusinessParty._EffectiveStartEndDates_StartDate,
					new Date());

				CreateRecordProcedure.execute(
					businessPartyPhoneTable,
					pathValueMap,
					context.getSession(),
					true,
					false);
			}
		}

		if (requestResultOfficePhone != null)
		{
			requestResultFaxPhone.close();
		}

		if (requestResultMobilePhone != null)
		{
			requestResultFaxPhone.close();
		}

		if (requestResultFaxPhone != null)
		{
			requestResultFaxPhone.close();
		}

		return;
	}

	private void expireBusinessPartyRolesForEBXUserPhoneNumbers(
		ScriptTaskContext context,
		Adaptation userRecord,
		Adaptation businessPartyRecord) throws OperationException
	{
		AdaptationTable businessPartyRoleTable = RepositoryUtils.getTable(
			RepositoryUtils.getDataSet(
				HtzConstants.BUSINESS_PARTY_DATA_SPACE,
				HtzConstants.BUSINESS_PARTY_DATA_SET),
			BusinessPartyPaths._Root_BusinessPartyData_BusinessPartyRole.getPathInSchema()
				.format());

		Date today = new Date();

		String businessPartyId = businessPartyRecord.get(BusinessPartyPaths._Root_BusinessParty._Id)
			.toString();

		RequestResult requestResultRole = null;

		// Query the Business Party table for this user id to determine if the User is already in the Business Party Domain
		requestResultRole = businessPartyRoleTable.createRequestResult(
			BusinessPartyPaths._Root_BusinessPartyData_BusinessPartyRole._BusinessParty.format()
				+ "='" + businessPartyId + "' and "
				+ BusinessPartyPaths._Root_BusinessPartyData_BusinessPartyRole._IsRecordActive
					.format()
				+ "=true");

		for (int i = 0; i < requestResultRole.getSize(); i++)
		{ //Expire the Role
			Adaptation businessPartyRoleRecord = requestResultRole.nextAdaptation();

			Map<Path, Object> pathValueMap = new HashMap<Path, Object>();

			pathValueMap.put(
				BusinessPartyPaths._Root_BusinessPartyData_BusinessPartyRole._EffectiveStartEndDates_EndDate,
				today);

			ModifyValuesProcedure
				.execute(businessPartyRoleRecord, pathValueMap, context.getSession(), true, false);
		}

		if (requestResultRole != null)
		{
			requestResultRole.close();
		}

		return;
	}

	private void expireBusinessPartyEmailAddressesForEBXUserPhoneNumbers(
		ScriptTaskContext context,
		Adaptation userRecord,
		Adaptation businessPartyRecord) throws OperationException
	{
		AdaptationTable businessPartyEmailTable = RepositoryUtils.getTable(
			RepositoryUtils.getDataSet(
				HtzConstants.BUSINESS_PARTY_DATA_SPACE,
				HtzConstants.BUSINESS_PARTY_DATA_SET),
			BusinessPartyPaths._Root_BusinessPartyData_Email.getPathInSchema().format());

		Date today = new Date();

		String businessPartyId = businessPartyRecord.get(BusinessPartyPaths._Root_BusinessParty._Id)
			.toString();

		RequestResult requestResultEmail = null;

		// Query the Business Party table for this user id to determine if the User is already in the Business Party Domain
		requestResultEmail = businessPartyEmailTable.createRequestResult(
			BusinessPartyPaths._Root_BusinessPartyData_Email._BusinessParty.format() + "='"
				+ businessPartyId + "' and "
				+ BusinessPartyPaths._Root_BusinessPartyData_Email._IsRecordActive.format()
				+ "=true");

		for (int i = 0; i < requestResultEmail.getSize(); i++)
		{ //Expire the Email
			Adaptation businessPartyEmailRecord = requestResultEmail.nextAdaptation();

			Map<Path, Object> pathValueMap = new HashMap<Path, Object>();

			pathValueMap.put(
				BusinessPartyPaths._Root_BusinessPartyData_Email._EffectiveStartEndDates_EndDate,
				today);

			ModifyValuesProcedure
				.execute(businessPartyEmailRecord, pathValueMap, context.getSession(), true, false);
		}

		if (requestResultEmail != null)
		{
			requestResultEmail.close();
		}

		return;
	}

	private void expireBusinessPartyPhoneNumbersForEBXUserPhoneNumbers(
		ScriptTaskContext context,
		Adaptation userRecord,
		Adaptation businessPartyRecord) throws OperationException
	{
		AdaptationTable businessPartyPhoneTable = RepositoryUtils.getTable(
			RepositoryUtils.getDataSet(
				HtzConstants.BUSINESS_PARTY_DATA_SPACE,
				HtzConstants.BUSINESS_PARTY_DATA_SET),
			BusinessPartyPaths._Root_BusinessPartyData_Phone.getPathInSchema().format());

		Date today = new Date();

		String businessPartyId = businessPartyRecord.get(BusinessPartyPaths._Root_BusinessParty._Id)
			.toString();

		RequestResult requestResultPhone = null;

		// Query the Business Party table for this user id to determine if the User is already in the Business Party Domain
		requestResultPhone = businessPartyPhoneTable.createRequestResult(
			BusinessPartyPaths._Root_BusinessPartyData_Phone._BusinessParty.format() + "='"
				+ businessPartyId + "' and "
				+ BusinessPartyPaths._Root_BusinessPartyData_Phone._IsRecordActive.format()
				+ "=true");

		for (int i = 0; i < requestResultPhone.getSize(); i++)
		{ //Expire the Phone Number

			Adaptation businessPartyPhoneRecord = requestResultPhone.nextAdaptation();

			Map<Path, Object> pathValueMap = new HashMap<Path, Object>();

			pathValueMap.put(
				BusinessPartyPaths._Root_BusinessPartyData_Phone._EffectiveStartEndDates_EndDate,
				today);

			ModifyValuesProcedure
				.execute(businessPartyPhoneRecord, pathValueMap, context.getSession(), true, false);
		}

		if (requestResultPhone != null)
		{
			requestResultPhone.close();
		}

		return;
	}
}
