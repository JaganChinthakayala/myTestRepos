package com.hertz.mdm._hertz.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;

import com.hertz.mdm._hertz.constants.HtzConstants;
import com.hertz.mdm._hertz.constants.HtzWorkflowConstants;
import com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.repository.RepositoryUtils;
import com.hertz.mdm._hertz.path.DirectoryPaths;
import com.hertz.mdm.businessparty.path.BusinessPartyPaths;
import com.hertz.mdm.common.constants.HtzCommonConstants;
import com.hertz.mdm.common.path.CommonReferencePaths;
import com.hertz.mdm.location.enums.LocationProjectTypes;
import com.hertz.mdm.location.path.LocationPaths;
import com.hertz.mdm.vehicle.constants.VehicleWorkflowConstants;
import com.hertz.mdm.vehicle.util.HtzVehicleUtilities;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.RequestResult;
import com.orchestranetworks.ps.admin.PropertyFileHelper;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.workflow.ScriptTaskContext;

public class HtzUtilities
{
	private static Properties properties = null;

	public static Properties getProperties() throws OperationException
	{
		if (properties == null)
		{
			String hertzPropertyFile = System.getProperty("ebx.home") + "/"
				+ HtzConstants.EBX_PROPERTIES_FILE_PROPERTY;

			try
			{
				// Get properties file from property file
				PropertyFileHelper propHelper = new PropertyFileHelper(hertzPropertyFile);
				properties = propHelper.getProperties();

				// merge configuration and file properties
				Properties merged = new Properties();
				merged.putAll(propHelper.getProperties());
				merged.putAll(System.getProperties());

				properties = merged;
			}
			catch (Exception e)
			{
				throw new RuntimeException("Could not open " + hertzPropertyFile + " file");
			}
		}

		return properties;
	}

	public static void sendEmail(
		ScriptTaskContext context,
		String fromEmailAddress,
		ArrayList<String> emailDistributionList,
		String emailSubject,
		String emailBody) throws OperationException, AddressException
	{
		InternetAddress emailSender = new InternetAddress(HtzConstants.HERTZ_FROM_EMAIL_ADDRESS);

		if (emailDistributionList == null || emailDistributionList.isEmpty())
		{
			return;
		}

		boolean isRunningInLocalEnvironment = false;

		Properties hertzProperties = HtzUtilities.getProperties();

		String environment = hertzProperties.getProperty("environment", "local");

		if (HtzConstants.EBX_PROPERTY_ENV_PRODUCTION.equals(environment))
		{
		}
		else if (HtzConstants.EBX_PROPERTY_ENV_UAT.equals(environment))
		{
		}
		else if (HtzConstants.EBX_PROPERTY_ENV_QA.equals(environment))
		{
		}
		else if (HtzConstants.EBX_PROPERTY_ENV_DEVELOPMENT.equals(environment))
		{
		}
		else if (HtzConstants.EBX_PROPERTY_ENV_DEMO.equals(environment))
		{
		}
		else if (HtzConstants.EBX_PROPERTY_ENV_LOCAL.equals(environment))
		{
			isRunningInLocalEnvironment = true;
		}

		try
		{
			Properties mailProps = new Properties();

			mailProps.setProperty("mail.smtp.host", hertzProperties.getProperty("host", ""));
			mailProps.setProperty("mail.smtp.port", hertzProperties.getProperty("port", ""));

			Session javaMailSession = Session.getDefaultInstance(mailProps);

			MimeMessage message = new MimeMessage(javaMailSession);
			message.setFrom(new InternetAddress(fromEmailAddress));
			message.setSender(emailSender);
			for (int j = 0; j < emailDistributionList.size(); j++)
			{
				message.addRecipient(
					Message.RecipientType.TO,
					new InternetAddress(emailDistributionList.get(j)));
			}
			message.setSubject(emailSubject);
			message.setText(emailBody);

			writeToFileInFileSystem(
				"\n\nEmail Message = " + message + "  From:" + message.getFrom() + "  Subject:"
					+ message.getSubject() + "  Sender:" + message.getSender() + "  Recipient List:"
					+ emailDistributionList.toString() + " Message:" + emailBody);

			if (!isRunningInLocalEnvironment) // if running locally or smtp host or port is missing, don't send e-mail
			{
				if (StringUtils.isEmpty(mailProps.getProperty("mail.smtp.host"))
					|| StringUtils.isEmpty(mailProps.getProperty("mail.smtp.port")))
					System.out.println("SMTP server info missing - skipping e-mail send");
				else
					Transport.send(message);
			}
		}
		catch (Exception e)
		{
			//Throw exception causing the rest of the emails from being sent.
			throw new RuntimeException(
				"Error sending notification email to '" + emailDistributionList + "'.",
				e);
		}
	}

	public static ArrayList<String> buildEMailDistributionList(
		ScriptTaskContext context,
		Adaptation inputAdaptation,
		String domain,
		String projectType,
		String projectTypeEvent)
	{
		ArrayList<String> emailRecipientsEMailAddressList = new ArrayList<String>();

		RequestResult requestResult = null;
		RequestResult requestResult2 = null;

		if (LocationProjectTypes.OPEN_LOCATION.equals(projectType)
			|| LocationProjectTypes.CLOSE_LOCATION.equals(projectType))
		{
			Adaptation projectRecord = inputAdaptation;
			Adaptation locationRecord = AdaptationUtil
				.followFK(projectRecord, LocationPaths._Root_LocationProject._Location);
			//Get the email addresses of all of the members in the ProjectTeam table
			AdaptationTable locationProjectTeamTable = locationRecord.getContainer().getTable(
				LocationPaths._Root_ProjectData_LocationProjectTeamMember.getPathInSchema());

			AdaptationTable businessPartyTable = RepositoryUtils.getTable(
				RepositoryUtils.getDataSet(
					HtzConstants.BUSINESS_PARTY_DATA_SPACE,
					HtzConstants.BUSINESS_PARTY_DATA_SET),
				BusinessPartyPaths._Root_BusinessParty.getPathInSchema().format());

			String projectId = projectRecord.get(LocationPaths._Root_LocationProject._Id)
				.toString();

			// Query the Location Project Team table for all rows related to this project
			requestResult = locationProjectTeamTable.createRequestResult(
				LocationPaths._Root_ProjectData_LocationProjectTeamMember._Project.format() + "='"
					+ projectId + "'");

			for (int i = 0; i < requestResult.getSize(); i++)
			{
				Adaptation projectTeamRecord = requestResult.nextAdaptation();

				String mdmUserId = projectTeamRecord
					.getString(LocationPaths._Root_ProjectData_LocationProjectTeamMember._User);

				if (mdmUserId != null)
				{
					// Query the Business Party to get the Id of this user 
					requestResult2 = businessPartyTable.createRequestResult(
						BusinessPartyPaths._Root_BusinessParty._EbxUser.format() + "='" + mdmUserId
							+ "' and"
							+ BusinessPartyPaths._Root_BusinessParty._IsRecordActive.format()
							+ "=true");

					if (!requestResult2.isEmpty())
					{
						Adaptation bussinessPartyRecord = requestResult2.nextAdaptation();

						String emailAddress = getPrimaryEMailAddressforBusinessParty(
							bussinessPartyRecord);

						if (!emailAddress.equals("")
							&& !emailRecipientsEMailAddressList.contains(emailAddress))
						{
							emailRecipientsEMailAddressList.add(emailAddress);
						}
					}
				}
			}
		}

		//Get the email addresses of all of the members in the EMail Notification table For this Process and Process Event
		AdaptationTable emailNotificationBusinessPartyRelationshipTable = RepositoryUtils.getTable(
			RepositoryUtils.getDataSet(
				HtzConstants.COMMON_REFERENCE_DATA_SPACE,
				HtzConstants.COMMON_REFERENCE_DATA_SET),
			CommonReferencePaths._Root_EMailNotificationBusinessPartyRelationship.getPathInSchema()
				.format());

		Adaptation emailNotificationRecord = getEMailNotificationRecordForProcessAndProcessEvent(
			domain,
			projectType,
			projectTypeEvent);

		if (emailNotificationRecord != null)
		{
			requestResult = emailNotificationBusinessPartyRelationshipTable
				.createRequestResult(
					CommonReferencePaths._Root_EMailNotificationBusinessPartyRelationship._EmailNotification
						.format()
						+ "='"
						+ emailNotificationRecord
							.get(CommonReferencePaths._Root_EmailNotification._Id).toString()
						+ "'");

			for (int i = 0; i < requestResult.getSize(); i++)
			{
				Adaptation emailNotificationBusinessPartyRelationshipRecord = requestResult
					.nextAdaptation();

				Adaptation businessPartyRecord = AdaptationUtil.followFK(
					emailNotificationBusinessPartyRelationshipRecord,
					CommonReferencePaths._Root_EMailNotificationBusinessPartyRelationship._BusinessParty);

				String emailAddress = getPrimaryEMailAddressforBusinessParty(businessPartyRecord);

				if (!emailAddress.equals("")
					&& !emailRecipientsEMailAddressList.contains(emailAddress))
				{
					emailRecipientsEMailAddressList.add(emailAddress);
				}
			}
		}

		if (requestResult != null)
		{
			requestResult.close();
		}

		if (requestResult2 != null)
		{
			requestResult2.close();
		}

		return emailRecipientsEMailAddressList;
	}

	public static ArrayList<Adaptation> ebxUsersForRole(String role)
	{
		AdaptationTable businessPartyRoleTable = RepositoryUtils.getTable(
			RepositoryUtils.getDataSet(
				HtzConstants.BUSINESS_PARTY_DATA_SPACE,
				HtzConstants.BUSINESS_PARTY_DATA_SET),
			BusinessPartyPaths._Root_BusinessPartyData_BusinessPartyRole.getPathInSchema()
				.format());

		// Query the Business Party Table for the BP record with this UserId
		RequestResult requestResult = businessPartyRoleTable.createRequestResult(
			BusinessPartyPaths._Root_BusinessPartyData_BusinessPartyRole._BusinessPartyRoleType
				.format() + "='" + role + "' and "
				+ BusinessPartyPaths._Root_BusinessPartyData_BusinessPartyRole._IsRecordActive
					.format()
				+ "=true");

		ArrayList<Adaptation> ebxUsers = new ArrayList<Adaptation>();

		for (int i = 0; i < requestResult.getSize(); i++)
		{
			Adaptation businessPartyRoleRecord = requestResult.nextAdaptation();

			Adaptation userRecord = AdaptationUtil.followFK(
				businessPartyRoleRecord,
				BusinessPartyPaths._Root_BusinessPartyData_BusinessPartyRole._BusinessParty);

			ebxUsers.add(userRecord);
		}

		return ebxUsers;
	}

	public static Adaptation getBusinessPartyRecordForUserId(String businessPartyId)
	{
		Adaptation businessPartyRecord = null;

		AdaptationTable businessPartyTable = RepositoryUtils.getTable(
			RepositoryUtils.getDataSet(
				HtzConstants.BUSINESS_PARTY_DATA_SPACE,
				HtzConstants.BUSINESS_PARTY_DATA_SET),
			BusinessPartyPaths._Root_BusinessParty.getPathInSchema().format());

		// Query the Business Party Table for the BP record with this UserId
		RequestResult requestResult = businessPartyTable.createRequestResult(
			BusinessPartyPaths._Root_BusinessParty._EbxUserLogin.format() + "='" + businessPartyId
				+ "' and "
				+ BusinessPartyPaths._Root_BusinessPartyData_Email._IsRecordActive.format()
				+ "=true");

		if (!requestResult.isEmpty())
		{
			businessPartyRecord = requestResult.nextAdaptation();
		}

		if (requestResult != null)
		{
			requestResult.close();
		}
		return businessPartyRecord;
	}

	public static String getPrimaryEMailAddressforUserId(String businessPartyId)
		throws OperationException
	{
		Adaptation businessPartyRecord = getBusinessPartyRecordForUserId(businessPartyId);

		if (businessPartyRecord == null)
		{
			return "";
		}

		return getPrimaryEMailAddressforBusinessParty(businessPartyRecord);
	}

	public static String getPrimaryEMailAddressforBusinessParty(Adaptation businessPartyRecord)
	{
		AdaptationTable businessPartyEMailTable = RepositoryUtils.getTable(
			RepositoryUtils.getDataSet(
				HtzConstants.BUSINESS_PARTY_DATA_SPACE,
				HtzConstants.BUSINESS_PARTY_DATA_SET),
			BusinessPartyPaths._Root_BusinessPartyData_Email.getPathInSchema().format());

		String emailAddress = "";

		String businessPartyId = businessPartyRecord.get(BusinessPartyPaths._Root_BusinessParty._Id)
			.toString();

		// Query the Business Party Email record with this UserId
		RequestResult requestResult = businessPartyEMailTable.createRequestResult(
			BusinessPartyPaths._Root_BusinessPartyData_Email._BusinessParty.format() + "='"
				+ businessPartyId + "' and "
				+ BusinessPartyPaths._Root_BusinessPartyData_Email._EMailType.format() + "='"
				+ HtzCommonConstants.COMMON_EMAIL_TYPE_BUSINESS + "' and "
				+ BusinessPartyPaths._Root_BusinessPartyData_Email._PrimarySecondary.format() + "='"
				+ com.hertz.mdm.location.enums.EMailPirmarySecondary.PRIMARY + "' and "
				+ BusinessPartyPaths._Root_BusinessPartyData_Email._IsRecordActive.format()
				+ "=true");

		if (!requestResult.isEmpty())
		{
			Adaptation businessPartyEMailRecord = requestResult.nextAdaptation();
			emailAddress = businessPartyEMailRecord
				.getString(BusinessPartyPaths._Root_BusinessPartyData_Email._EMailAddress);
		}

		if (requestResult != null)
		{
			requestResult.close();
		}

		return emailAddress;
	}

	public static Adaptation getEMailNotificationRecordForProcessAndProcessEvent(
		String domain,
		String projectType,
		String projectTypeEvent)
	{
		AdaptationTable emailNotificationTable = RepositoryUtils.getTable(
			RepositoryUtils.getDataSet(
				HtzConstants.COMMON_REFERENCE_DATA_SPACE,
				HtzConstants.COMMON_REFERENCE_DATA_SET),
			CommonReferencePaths._Root_EmailNotification.getPathInSchema().format());

		// Query the EMail Notification Table for this Message type		
		RequestResult requestResult = emailNotificationTable.createRequestResult(
			CommonReferencePaths._Root_EmailNotification._Domain.format() + "='" + domain + "' and "
				+ CommonReferencePaths._Root_EmailNotification.__projectTypeName.format() + "='"
				+ projectType + "' and "
				+ CommonReferencePaths._Root_EmailNotification._ProjectTypeEvent.format() + "='"
				+ projectTypeEvent + "' and "
				+ CommonReferencePaths._Root_EmailNotification._IsRecordActive.format() + "=true");

		if (!requestResult.isEmpty())
		{
			return requestResult.nextAdaptation();
		}
		return null;
	}

	public static void sendEmailInboxNotificationToDataEntry(
		ScriptTaskContext context,
		String domain,
		String projectType)
	{
		String currentUserId = context
			.getVariableString(HtzWorkflowConstants.PARAM_CURRENT_USER_ID);

		sendEmailInboxNotification(context, currentUserId, domain, projectType);
	}

	public static void sendEmailInboxNotificationToDataApprover(
		ScriptTaskContext context,
		String domain,
		String projectType)
	{
		String currentApproverId = context
			.getVariableString(HtzWorkflowConstants.PARAM_CURRENT_APPROVER_ID);

		//If the Approver Id is null, it means that the task is being offered to the Role, not a user so get all the users for the Role
		if (currentApproverId == null)
		{
			RequestResult requestResultBusinessParty = null;
			RequestResult requestResultDirectoryUsersRoles = null;

			ArrayList<String> emailAddressList = new ArrayList<String>();

			AdaptationTable usersRolesTable = RepositoryUtils.getTable(
				RepositoryUtils
					.getDataSet(HtzConstants.DIRECTORY_DATA_SPACE, HtzConstants.DIRECTORY_DATA_SET),
				DirectoryPaths._UsersRoles.getPathInSchema().format());

			AdaptationTable businessPartyTable = RepositoryUtils.getTable(
				RepositoryUtils.getDataSet(
					HtzConstants.BUSINESS_PARTY_DATA_SPACE,
					HtzConstants.BUSINESS_PARTY_DATA_SET),
				BusinessPartyPaths._Root_BusinessParty.getPathInSchema().format());

			// Query the Directory UsersRoles to get all the users in this role
			requestResultDirectoryUsersRoles = usersRolesTable.createRequestResult(
				DirectoryPaths._UsersRoles._Role.format() + "='"
					+ VehicleWorkflowConstants.VEHICLE_REFERENCE_DATA_APPROVER + "'");

			//Loop through the Roles to get all the users that need to get email
			for (int i = 0; i < requestResultDirectoryUsersRoles.getSize(); i++)
			{
				Adaptation directoryUsersRolesRecord = requestResultDirectoryUsersRoles
					.nextAdaptation();

				String userId = directoryUsersRolesRecord.get(DirectoryPaths._UsersRoles._User)
					.toString();

				// Query the Business Party table for this user id to determine if the User is already in the Business Party Domain
				requestResultBusinessParty = businessPartyTable.createRequestResult(
					BusinessPartyPaths._Root_BusinessParty._EbxUser.format() + "='" + userId
						+ "' and " + BusinessPartyPaths._Root_BusinessParty._IsRecordActive.format()
						+ "=true");

				Adaptation businessPartyRecord = requestResultBusinessParty.nextAdaptation();

				if (businessPartyRecord != null)
				{
					String emailAddress = getPrimaryEMailAddressforBusinessParty(
						businessPartyRecord);

					if (emailAddress != null && !emailAddress.equals(""))
					{
						if (!emailAddressList.contains(emailAddress))
						{
							emailAddressList.add(emailAddress);
						}
					}
				}
			}
			sendEmailInboxNotificationToEmailAddressList(
				context,
				emailAddressList,
				domain,
				projectType,
				true);
		}
		else
		{
			sendEmailInboxNotification(context, currentApproverId, domain, projectType);
		}
	}

	public static void sendEmailInboxNotification(
		ScriptTaskContext context,
		String ebxUser,
		String domain,
		String projectType)
	{
		String projectTypeEvent = "Inbox";

		ArrayList<String> emailRecipientsEMailAddressList = new ArrayList<String>();

		AdaptationTable businessPartyTable = RepositoryUtils.getTable(
			RepositoryUtils.getDataSet(
				HtzConstants.BUSINESS_PARTY_DATA_SPACE,
				HtzConstants.BUSINESS_PARTY_DATA_SET),
			BusinessPartyPaths._Root_BusinessParty.getPathInSchema().format());

		// Query the Business Party to get the email of this user 
		RequestResult requestResult = businessPartyTable.createRequestResult(
			BusinessPartyPaths._Root_BusinessParty._EbxUser.format() + "='" + ebxUser + "' and"
				+ BusinessPartyPaths._Root_BusinessParty._IsRecordActive.format() + "=true");

		if (!requestResult.isEmpty())
		{
			Adaptation bussinessPartyRecord = requestResult.nextAdaptation();

			String emailAddress = getPrimaryEMailAddressforBusinessParty(bussinessPartyRecord);

			if (!emailAddress.equals("") && !emailRecipientsEMailAddressList.contains(emailAddress))
			{
				emailRecipientsEMailAddressList.add(emailAddress);
			}

			try
			{
				HtzVehicleUtilities.sendVehicleEmailNotification(
					context,
					domain,
					projectType,
					projectTypeEvent,
					emailRecipientsEMailAddressList,
					true);
			}
			catch (Exception e)
			{
				//Throw exception causing the rest of the emails from being sent.
				throw new RuntimeException("", e);
			}
		}
	}

	public static void sendEmailInboxNotificationToEmailAddressList(
		ScriptTaskContext context,
		ArrayList<String> emailRecipientsEMailAddressList,
		String domain,
		String projectType,
		boolean isInboxNotification)
	{
		String projectTypeEvent = "Inbox";

		try
		{
			HtzVehicleUtilities.sendVehicleEmailNotification(
				context,
				domain,
				projectType,
				projectTypeEvent,
				emailRecipientsEMailAddressList,
				isInboxNotification);
		}
		catch (Exception e)
		{
			//Throw exception causing the rest of the emails from being sent.
			throw new RuntimeException("", e);
		}
	}

	public static boolean writeToFileInFileSystem(String outputText) throws OperationException
	{
		Properties hertzProperties = HtzUtilities.getProperties();

		String filePath = hertzProperties.getProperty("WriteFileSystemFilePath");

		if (filePath == null)
			return false;

		BufferedWriter bufferWriter = null;
		FileWriter fileWriter = null;

		try
		{
			String content = outputText;

			fileWriter = new FileWriter(filePath, true);
			bufferWriter = new BufferedWriter(fileWriter);
			bufferWriter.write(content);

			return true;

		}
		catch (IOException e)
		{
			throw new RuntimeException("", e);
		}
		finally
		{
			try
			{
				if (bufferWriter != null)
					bufferWriter.close();
				if (fileWriter != null)
					fileWriter.close();
			}
			catch (IOException ex)
			{
				throw new RuntimeException("", ex);
			}
		}
	}

	public static double distance(
		double lat1,
		double lat2,
		double lon1,
		double lon2,
		double alt1,
		double alt2,
		String unit)
	{
		/*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
		/*::                                                                         :*/
		/*::  This routine calculates the distance between two points (given the     :*/
		/*::  latitude/longitude of those points). It is being used to calculate     :*/
		/*::  the distance between two locations using GeoDataSource (TM) prodducts  :*/
		/*::                                                                         :*/
		/*::  Definitions:                                                           :*/
		/*::    South latitudes are negative, east longitudes are positive           :*/
		/*::                                                                         :*/
		/*::  Passed to function:                                                    :*/
		/*::    lat1, lon1 = Latitude and Longitude of point 1 (in decimal degrees)  :*/
		/*::    lat2, lon2 = Latitude and Longitude of point 2 (in decimal degrees)  :*/
		/*::    unit = the unit you desire for results                               :*/
		/*::           where: 'M' is statute miles (default)                         :*/
		/*::                  'K' is kilometers                                      :*/
		/*::                  'N' is nautical miles                                  :*/
		/*::  Worldwide cities and other features databases with latitude longitude  :*/
		/*::  are available at http://www.geodatasource.com                          :*/
		/*::                                                                         :*/
		/*::  For enquiries, please contact sales@geodatasource.com                  :*/
		/*::                                                                         :*/
		/*::  Official Web site: http://www.geodatasource.com                        :*/
		/*::                                                                         :*/
		/*::           GeoDataSource.com (C) All Rights Reserved 2017                :*/
		/*::                                                                         :*/
		/*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/

		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
			+ Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;

		double height = Math.abs(alt1 - alt2);

		dist = dist + height;

		if (unit == "K")
		{
			dist = dist * 1.609344;
		}
		else if (unit == "N")
		{
			dist = dist * 0.8684;
		}

		return (dist);
	}

	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::	This function converts decimal degrees to radians						 :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	private static double deg2rad(double deg)
	{
		return (deg * Math.PI / 180.0);
	}

	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::	This function converts radians to decimal degrees						 :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	private static double rad2deg(double rad)
	{
		return (rad * 180 / Math.PI);
	}

	/*
	public static double distance2(
		double lat1,
		double lat2,
		double lon1,
		double lon2,
		double el1,
		double el2,
		boolean inMeters)
	{
		final int R = 6371; // Radius of the earth
	
		double latDistance = Math.toRadians(lat2 - lat1);
		double lonDistance = Math.toRadians(lon2 - lon1);
		double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
			+ Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
				* Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	
		double distance = 0;
	
		if (inMeters)
		{
			// convert to meters
			distance = R * c * 1000;
		}
		else
		{
			distance = R * c;
		}
	
		double height = el1 - el2;
	
		distance = Math.pow(distance, 2) + Math.pow(height, 2);
	
		return Math.sqrt(distance);
	}
	*/
}
