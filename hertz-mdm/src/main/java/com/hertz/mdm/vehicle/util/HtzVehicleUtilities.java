package com.hertz.mdm.vehicle.util;

import java.util.ArrayList;
import java.util.Date;

import javax.mail.internet.AddressException;

import com.hertz.mdm._hertz.constants.HtzConstants;
import com.hertz.mdm._hertz.constants.HtzWorkflowConstants;
import com.hertz.mdm._hertz.util.HtzUtilities;
import com.hertz.mdm.common.path.CommonReferencePaths;
import com.hertz.mdm.common.util.HtzCommonUtilities;
import com.hertz.mdm.vehicle.constants.HtzVehicleConstants;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationHome;
import com.onwbp.adaptation.AdaptationName;
import com.orchestranetworks.instance.HomeKey;
import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.workflow.ScriptTaskContext;

public class HtzVehicleUtilities extends HtzCommonUtilities
{
	public static AdaptationHome getVehicleDataSpace(Repository repo)
	{
		return repo.lookupHome(HomeKey.forBranchName(HtzVehicleConstants.VEHICLE_DATA_SPACE));
	}

	public static Adaptation getVehicleReferenceDataSet(Repository repo)
	{
		return getVehicleReferenceDataSet(getVehicleDataSpace(repo));
	}

	public static Adaptation getVehicleReferenceDataSet(AdaptationHome vehicleDataSpace)
	{
		return vehicleDataSpace.findAdaptationOrNull(
			AdaptationName.forName(HtzVehicleConstants.VEHICLE_REFERENCE_DATA_SET));
	}

	public static void sendVehicleEmailNotification(
		ScriptTaskContext context,
		String domain,
		String projectType,
		String projectTypeEvent) throws OperationException, AddressException
	{
		sendVehicleEmailNotification(context, domain, projectType, projectTypeEvent, null, false);
	}

	public static void sendVehicleEmailNotification(
		ScriptTaskContext context,
		String domain,
		String projectType,
		String projectTypeEvent,
		ArrayList<String> emailDistributionList) throws OperationException, AddressException
	{
		sendVehicleEmailNotification(
			context,
			domain,
			projectType,
			projectTypeEvent,
			emailDistributionList,
			false);
	}

	public static void sendVehicleEmailNotification(
		ScriptTaskContext context,
		String domain,
		String projectType,
		String projectTypeEvent,
		ArrayList<String> emailDistributionList,
		boolean isInboxNotification) throws OperationException, AddressException
	{
		String fromEmailAddress = HtzConstants.HERTZ_FROM_EMAIL_ADDRESS;

		if (emailDistributionList.isEmpty())
		{
			emailDistributionList = null;
		}

		HtzUtilities.sendEmail(
			context,
			fromEmailAddress,
			emailDistributionList,
			buildEmailSubject(projectType),
			(isInboxNotification
				? buildEMailBody(context, HtzConstants.COMMON_DOMAIN, "All", projectTypeEvent)
				: buildEMailBody(context, domain, projectType, projectTypeEvent)));
	}

	protected static String buildEMailBody(
		ScriptTaskContext context,
		String domain,
		String projectType,
		String projectTypeEvent)
	{
		Adaptation emailNotificationRecord = HtzUtilities
			.getEMailNotificationRecordForProcessAndProcessEvent(
				domain,
				projectType,
				projectTypeEvent);

		if (emailNotificationRecord == null)
		{
			return "";
		}

		StringBuffer buffer = new StringBuffer();

		String fromValue = "";
		String toValue = "";

		String message = emailNotificationRecord
			.getString(CommonReferencePaths._Root_EmailNotification._Message);

		message = message
			.replaceAll(
				HtzWorkflowConstants.WF_MESSAGE_VARIABLE_START_DELIMITER
					+ HtzWorkflowConstants.WF_MESSAGE_VARIABLE_TODAYSDATE
					+ HtzWorkflowConstants.WF_MESSAGE_VARIABLE_END_DELIMITER,
				new Date().toString());

		toValue = projectType;

		if (toValue == null)
		{
			toValue = "";
		}

		fromValue = HtzWorkflowConstants.WF_MESSAGE_VARIABLE_START_DELIMITER
			+ HtzWorkflowConstants.WF_MESSAGE_VARIABLE_PROJECT_TYPE
			+ HtzWorkflowConstants.WF_MESSAGE_VARIABLE_END_DELIMITER;

		message = message.replaceAll(fromValue, toValue);

		toValue = domain;

		if (toValue == null)
		{
			toValue = "";
		}

		fromValue = HtzWorkflowConstants.WF_MESSAGE_VARIABLE_START_DELIMITER
			+ HtzWorkflowConstants.WF_MESSAGE_VARIABLE_DOMAIN
			+ HtzWorkflowConstants.WF_MESSAGE_VARIABLE_END_DELIMITER;

		message = message.replaceAll(fromValue, toValue);

		toValue = context.getVariableString(HtzWorkflowConstants.DATA_CONTEXT_PARM_XPATH_TO_TABLE);

		if (toValue == null)
		{
			toValue = "";
		}

		fromValue = HtzWorkflowConstants.WF_MESSAGE_VARIABLE_START_DELIMITER
			+ HtzWorkflowConstants.WF_MESSAGE_VARIABLE_TABLE
			+ HtzWorkflowConstants.WF_MESSAGE_VARIABLE_END_DELIMITER;

		message = message.replaceAll(fromValue, toValue);

		toValue = context.getVariableString(HtzWorkflowConstants.PARAM_CURRENT_USER_LABEL);

		if (toValue == null)
		{
			toValue = "";
		}

		fromValue = HtzWorkflowConstants.WF_MESSAGE_VARIABLE_START_DELIMITER
			+ HtzWorkflowConstants.WF_MESSAGE_VARIABLE_USER
			+ HtzWorkflowConstants.WF_MESSAGE_VARIABLE_END_DELIMITER;

		message = message.replaceAll(fromValue, toValue);

		toValue = context
			.getVariableString(HtzWorkflowConstants.DATA_CONTEXT_PARM_CURENT_APPROVER_ID);

		if (toValue == null)
		{
			toValue = "";
		}

		fromValue = HtzWorkflowConstants.WF_MESSAGE_VARIABLE_START_DELIMITER
			+ HtzWorkflowConstants.WF_MESSAGE_VARIABLE_APPROVER
			+ HtzWorkflowConstants.WF_MESSAGE_VARIABLE_END_DELIMITER;

		message = message.replaceAll(fromValue, toValue);

		toValue = context.getVariableString(HtzWorkflowConstants.PARAM_RECORD_NAME_VALUE);

		if (toValue == null)
		{
			toValue = "";
		}

		fromValue = HtzWorkflowConstants.WF_MESSAGE_VARIABLE_START_DELIMITER
			+ HtzWorkflowConstants.WF_MESSAGE_VARIABLE_RECORD_NAME
			+ HtzWorkflowConstants.WF_MESSAGE_VARIABLE_END_DELIMITER;

		message = message.replaceAll(fromValue, toValue);

		buffer.append(message);

		return buffer.toString();
	}

	protected static String buildEmailSubject(String projectType)
	{
		StringBuffer buf = new StringBuffer();
		buf.append(projectType);
		return buf.toString();
	}
}
