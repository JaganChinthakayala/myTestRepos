/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.scripttask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.hertz.mdm._hertz.constants.HtzConstants;
import com.hertz.mdm._hertz.constants.HtzWorkflowConstants;
import com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.repository.RepositoryUtils;
import com.hertz.mdm._hertz.util.HtzUtilities;
import com.hertz.mdm.businessparty.path.BusinessPartyPaths;
import com.hertz.mdm.common.constants.HtzCommonConstants;
import com.hertz.mdm.location.constants.LocationConstants;
import com.hertz.mdm.location.path.LocationPaths;
import com.hertz.mdm.location.util.HtzLocationUtilities;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.RequestResult;
import com.orchestranetworks.ps.procedure.ModifyValuesProcedure;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.workflow.ScriptTask;
import com.orchestranetworks.workflow.ScriptTaskContext;

public class SendEscalationEMailsScriptTask extends ScriptTask
{
	@Override
	public void executeScript(ScriptTaskContext context) throws OperationException
	{
		String projectTypeEvent = "Escalation";
		Date todaysDate = new Date();

		String domain = context.getVariableString(HtzWorkflowConstants.DATA_CONTEXT_DOMAIN);
		String projectType = context
			.getVariableString(HtzWorkflowConstants.DATA_CONTEXT_PROJECT_TYPE);

		//Create an EMail Distribution List from the EMail Notification for the Type and Type Event
		ArrayList<String> eMailDistributionListFromEmailNotificationRecord = HtzUtilities
			.buildEMailDistributionList(context, null, domain, projectType, projectTypeEvent);

		//Read all of the User Task Log Records that are overdue
		AdaptationTable userTaskLogTable = RepositoryUtils.getTable(
			RepositoryUtils
				.getDataSet(HtzConstants.LOCATION_DATA_SPACE, HtzConstants.LOCATION_DATA_SET),
			LocationPaths._Root_ProjectData_ProjectUserTaskLog.getPathInSchema().format());

		RequestResult requestResult = userTaskLogTable.createRequestResult(
			LocationPaths._Root_ProjectData_ProjectUserTaskLog._IsUserTaskOverdue.format()
				+ "= true");

		ArrayList<String> distributionList = new ArrayList<String>();
		String assingedUserEmailAddress = null;
		String businessPartyId = null;

		for (int i = 0; i < requestResult.getSize(); i++)
		{
			Adaptation userTaskLogRecord = requestResult.nextAdaptation();

			//If an email has been sent in the last 24 hours, do not send another one
			Date lastEmailSent = (Date) userTaskLogRecord
				.get(LocationPaths._Root_ProjectData_ProjectUserTaskLog._LastEmailSent);

			Calendar calendar = Calendar.getInstance();

			if (lastEmailSent != null)
			{
				calendar.setTime(lastEmailSent);
				calendar.add(Calendar.DATE, +1);
			}

			if (lastEmailSent != null && calendar.getTime().after(todaysDate))
			{
				continue;
			}

			distributionList.clear();
			distributionList.addAll(eMailDistributionListFromEmailNotificationRecord);
			assingedUserEmailAddress = null;

			if (userTaskLogRecord
				.get(LocationPaths._Root_ProjectData_ProjectUserTaskLog._AllocatedUser) == null)
			{
				//A user has not taken this task yet; Email all users in the Role
				ArrayList<Adaptation> ebxUsersForRole = HtzUtilities.ebxUsersForRole(
					userTaskLogRecord.getString(
						LocationPaths._Root_ProjectData_ProjectUserTaskLog._OfferedToRole));

				for (int k = 0; k < ebxUsersForRole.size(); k++)
				{
					Adaptation ebxUser = ebxUsersForRole.get(k);

					String userEMailAddress = HtzUtilities
						.getPrimaryEMailAddressforBusinessParty(ebxUser);

					if (userEMailAddress.length() > 0)
					{
						if (!distributionList.contains(userEMailAddress))
						{
							distributionList.add(userEMailAddress);
						}
					}
				}
			}
			else
			{
				//A user has  taken this task yet; Email that user only
				Adaptation businessPartyRecord = AdaptationUtil.followFK(
					userTaskLogRecord,
					LocationPaths._Root_ProjectData_ProjectUserTaskLog._AllocatedUser);

				businessPartyId = businessPartyRecord
					.get(BusinessPartyPaths._Root_BusinessParty._Id).toString();

				AdaptationTable businessPartyEmailTable = businessPartyRecord.getContainer()
					.getTable(BusinessPartyPaths._Root_BusinessPartyData_Email.getPathInSchema());

				RequestResult requestResultEmailAddress = businessPartyEmailTable
					.createRequestResult(
						BusinessPartyPaths._Root_BusinessPartyData_Email._BusinessParty.format()
							+ "= '" + businessPartyId + "'");

				for (int j = 0; j < requestResultEmailAddress.getSize(); j++)
				{
					Adaptation businessPartyEmailRecord = requestResultEmailAddress
						.nextAdaptation();

					//If this is the Primary Business Email Address for this Individual, use it
					if (HtzCommonConstants.COMMON_EMAIL_TYPE_BUSINESS.equals(
						businessPartyEmailRecord
							.getString(BusinessPartyPaths._Root_BusinessPartyData_Email._EMailType))
						&& com.hertz.mdm.location.enums.EMailPirmarySecondary.PRIMARY.equals(
							businessPartyEmailRecord.getString(
								BusinessPartyPaths._Root_BusinessPartyData_Email._PrimarySecondary)))
					{
						assingedUserEmailAddress = businessPartyEmailRecord.getString(
							BusinessPartyPaths._Root_BusinessPartyData_Email._EMailAddress);

						break;
					}

					//Otherwise use the 1st one unless the Primary Business Email Address is found
					if (j == 0)
					{
						assingedUserEmailAddress = businessPartyEmailRecord.getString(
							BusinessPartyPaths._Root_BusinessPartyData_Email._EMailAddress);
					}
				}
			}

			if (assingedUserEmailAddress != null)
			{
				if (!distributionList.contains(assingedUserEmailAddress))
				{
					distributionList.add(assingedUserEmailAddress);
				}
			}

			if (distributionList.isEmpty())
			{
				continue;
			}

			String fromEmailAddress = HtzConstants.HERTZ_FROM_EMAIL_ADDRESS;
			String fromValue = "";
			String toValue = "";

			String eMailBody = HtzLocationUtilities
				.buildEMailBody(null, domain, projectType, projectTypeEvent);

			toValue = userTaskLogRecord
				.get(
					LocationPaths._Root_ProjectData_ProjectUserTaskLog._TaskStartEndTimestamp_StartDateTime)
				.toString();

			if (toValue == null)
			{
				toValue = "";
			}

			fromValue = HtzWorkflowConstants.WF_MESSAGE_VARIABLE_START_DELIMITER
				+ LocationConstants.WF_MESSAGE_VARIABLE_TASK_START_DATE
				+ HtzWorkflowConstants.WF_MESSAGE_VARIABLE_END_DELIMITER;

			eMailBody = eMailBody.replaceAll(fromValue, toValue);

			toValue = userTaskLogRecord
				.getString(LocationPaths._Root_ProjectData_ProjectUserTaskLog._ProjectType);

			if (toValue == null)
			{
				toValue = "";
			}

			fromValue = HtzWorkflowConstants.WF_MESSAGE_VARIABLE_START_DELIMITER
				+ LocationConstants.WF_MESSAGE_VARIABLE_OVERDUE_PROJECT_TYPE
				+ HtzWorkflowConstants.WF_MESSAGE_VARIABLE_END_DELIMITER;

			eMailBody = eMailBody.replaceAll(fromValue, toValue);

			toValue = userTaskLogRecord
				.getString(LocationPaths._Root_ProjectData_ProjectUserTaskLog._ProjectName);

			if (toValue == null)
			{
				toValue = "";
			}

			fromValue = HtzWorkflowConstants.WF_MESSAGE_VARIABLE_START_DELIMITER
				+ LocationConstants.WF_MESSAGE_VARIABLE_OVERDUE_PROJECT_NAME
				+ HtzWorkflowConstants.WF_MESSAGE_VARIABLE_END_DELIMITER;

			eMailBody = eMailBody.replaceAll(fromValue, toValue);

			toValue = userTaskLogRecord
				.getString(LocationPaths._Root_ProjectData_ProjectUserTaskLog._DataSpace);

			if (toValue == null)
			{
				toValue = "";
			}

			fromValue = HtzWorkflowConstants.WF_MESSAGE_VARIABLE_START_DELIMITER
				+ LocationConstants.WF_MESSAGE_VARIABLE_DATASPACE
				+ HtzWorkflowConstants.WF_MESSAGE_VARIABLE_END_DELIMITER;

			eMailBody = eMailBody.replaceAll(fromValue, toValue);

			toValue = userTaskLogRecord
				.getString(LocationPaths._Root_ProjectData_ProjectUserTaskLog._UserTaskName);

			if (toValue == null)
			{
				toValue = "";
			}

			fromValue = HtzWorkflowConstants.WF_MESSAGE_VARIABLE_START_DELIMITER
				+ LocationConstants.WF_MESSAGE_VARIABLE_USER_TASK_NAME
				+ HtzWorkflowConstants.WF_MESSAGE_VARIABLE_END_DELIMITER;

			eMailBody = eMailBody.replaceAll(fromValue, toValue);

			toValue = userTaskLogRecord
				.getString(LocationPaths._Root_ProjectData_ProjectUserTaskLog._OfferedToRole);

			if (toValue == null)
			{
				toValue = "";
			}

			fromValue = HtzWorkflowConstants.WF_MESSAGE_VARIABLE_START_DELIMITER
				+ LocationConstants.WF_MESSAGE_VARIABLE_OFFERED_TO_ROLE
				+ HtzWorkflowConstants.WF_MESSAGE_VARIABLE_END_DELIMITER;

			eMailBody = eMailBody.replaceAll(fromValue, toValue);

			Adaptation allocatedUserRecord = AdaptationUtil.followFK(
				userTaskLogRecord,
				LocationPaths._Root_ProjectData_ProjectUserTaskLog._AllocatedUser);

			if (allocatedUserRecord == null)
			{
				toValue = "Not Allocated";
			}
			else
			{
				toValue = allocatedUserRecord
					.getString(BusinessPartyPaths._Root_BusinessParty._Name) + "("
					+ allocatedUserRecord
						.getString(BusinessPartyPaths._Root_BusinessParty._EbxUserLogin)
					+ ")";
			}

			fromValue = HtzWorkflowConstants.WF_MESSAGE_VARIABLE_START_DELIMITER
				+ LocationConstants.WF_MESSAGE_VARIABLE_ALLOCATED_USER
				+ HtzWorkflowConstants.WF_MESSAGE_VARIABLE_END_DELIMITER;

			eMailBody = eMailBody.replaceAll(fromValue, toValue);

			try
			{
				HtzUtilities.sendEmail(
					context,
					fromEmailAddress,
					distributionList,
					"MDM User Task Escalation Notification - Immediate attention required",
					eMailBody);
			}
			catch (Exception e)
			{
				//Throw exception causing the rest of the emails from being sent.
				throw new RuntimeException("", e);
			}

			//Set the LastEmailSent Date on the Log Record
			HashMap<Path, Object> pathValueMap = new HashMap<Path, Object>();

			pathValueMap
				.put(LocationPaths._Root_ProjectData_ProjectUserTaskLog._LastEmailSent, todaysDate);

			ModifyValuesProcedure
				.execute(userTaskLogRecord, pathValueMap, context.getSession(), true, false);
		}
	}
}
