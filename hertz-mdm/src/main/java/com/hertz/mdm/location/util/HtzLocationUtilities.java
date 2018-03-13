package com.hertz.mdm.location.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.AddressException;

import com.hertz.mdm._hertz.constants.HtzConstants;
import com.hertz.mdm._hertz.constants.HtzWorkflowConstants;
import com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.repository.RepositoryUtils;
import com.hertz.mdm._hertz.util.HtzUtilities;
import com.hertz.mdm.admin.path.AdminPaths;
import com.hertz.mdm.businessparty.constants.BusinessPartyConstants;
import com.hertz.mdm.businessparty.path.BusinessPartyPaths;
import com.hertz.mdm.businessparty.path.BusinessPartyReferencePaths;
import com.hertz.mdm.common.path.CommonReferencePaths;
import com.hertz.mdm.common.util.HtzCommonUtilities;
import com.hertz.mdm.location.constants.LocationConstants;
import com.hertz.mdm.location.constants.LocationProjectWorkflowConstants;
import com.hertz.mdm.location.enums.LocationStatuses;
import com.hertz.mdm.location.enums.RentalTypes;
import com.hertz.mdm.location.path.LocationPaths;
import com.hertz.mdm.location.path.LocationReferencePaths;
import com.hertz.mdm.location.workflow.LocationProjectWorkflowUtilities;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationHome;
import com.onwbp.adaptation.AdaptationName;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.RequestResult;
import com.orchestranetworks.instance.HomeKey;
import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.ps.procedure.CreateRecordProcedure;
import com.orchestranetworks.ps.procedure.DeleteRecordProcedure;
import com.orchestranetworks.ps.procedure.ModifyValuesProcedure;
import com.orchestranetworks.ps.project.workflow.launcher.SubjectWorkflowLauncher;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.ps.workflow.WorkflowLauncherContext;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.ProcedureContext;
import com.orchestranetworks.service.Session;
import com.orchestranetworks.workflow.DataContext;
import com.orchestranetworks.workflow.ScriptTaskContext;

public class HtzLocationUtilities extends HtzCommonUtilities
{
	public static AdaptationHome getLocationDataSpace(Repository repo)
	{
		return repo.lookupHome(HomeKey.forBranchName(LocationConstants.LOCATION_DATA_SPACE));
	}

	public static Adaptation getLocationReferenceDataSet(Repository repo)
	{
		return getLocationReferenceDataSet(getLocationDataSpace(repo));
	}

	public static Adaptation getLocationReferenceDataSet(AdaptationHome locationDataSpace)
	{
		return locationDataSpace.findAdaptationOrNull(
			AdaptationName.forName(LocationConstants.LOCATION_REFERENCE_DATA_SET));
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<String> determineHierarchyPaths(
		Adaptation hierarchyNodeRecord,
		ArrayList<String> hierarchyPaths,
		boolean isRecursiveCall)
	{
		if (isRecursiveCall && hierarchyPaths.isEmpty())
		{
			//return hierarchyPaths;
		}

		Adaptation hierarchyNodeParentRecord = null;

		if (hierarchyNodeRecord.get(LocationPaths._Root_LocationHierarchy._Parents) == null
			|| ((ArrayList<String>) hierarchyNodeRecord
				.get(LocationPaths._Root_LocationHierarchy._Parents)).isEmpty())
		{
			hierarchyPaths.add("|");
			hierarchyPaths
				.add(hierarchyNodeRecord.getString(LocationPaths._Root_LocationHierarchy._Name));
		}
		else
		{
			ArrayList<String> parents = (ArrayList<String>) hierarchyNodeRecord
				.get(LocationPaths._Root_LocationHierarchy._Parents);

			//Iterate through all of this locationHierarchy Records Parents
			for (String parent : parents)
			{
				{
					AdaptationTable locationHierarchyTable = hierarchyNodeRecord.getContainer()
						.getTable(LocationPaths._Root_LocationHierarchy.getPathInSchema());

					// Query the LocationHierarcy table for a row whose Primary Key is the parent
					RequestResult requestResult = locationHierarchyTable.createRequestResult(
						LocationPaths._Root_LocationHierarchy._Id.format() + "='" + parent
							+ "' and "
							+ LocationPaths._Root_LocationHierarchy._IsRecordActive.format()
							+ "=true");

					//If no record found, this Target Node does not have an Address row of type "BUSINESS" to derive the country from
					if (requestResult.isEmpty())
					{
						requestResult.close();
						return hierarchyPaths;
					}
					hierarchyNodeParentRecord = requestResult.nextAdaptation();
					determineHierarchyPaths(hierarchyNodeParentRecord, hierarchyPaths, true);
					hierarchyPaths.add(
						hierarchyNodeRecord.getString(LocationPaths._Root_LocationHierarchy._Name));
				}
			}
		}
		return hierarchyPaths;
	}

	public static boolean isLocationInAValidHierarchy(Adaptation locationRecord)
	{
		if (locationRecord == null)
		{
			return false;
		}

		Adaptation areaRecord = AdaptationUtil
			.followFK(locationRecord, LocationPaths._Root_Location._LocationInformation_Area);

		if (areaRecord == null)
		{
			return false;
		}

		String locationId = locationRecord.get(LocationPaths._Root_Location._Id).toString();

		AdaptationTable locationAddressTable = locationRecord.getContainer()
			.getTable(LocationPaths._Root_LocationData_Address.getPathInSchema());

		// Query the LocationAddress table for a row whose Type is "BUSINESS"
		RequestResult requestResult = locationAddressTable.createRequestResult(
			LocationPaths._Root_LocationData_Address._Location.format() + "='" + locationId
				+ "' and " + LocationPaths._Root_LocationData_Address._Type.format() + "='"
				+ LocationConstants.LOCATION_ADDRESS_TYPE_BUSINESS + "' and"
				+ LocationPaths._Root_LocationData_Address._IsRecordActive.format() + "=true");

		//If no record found, this Target Node does not have an Address row of type "BUSINESS" to derive the country from
		if (requestResult.isEmpty())
		{
			requestResult.close();
			return false;
		}

		Adaptation addressRecord = requestResult.nextAdaptation();

		//This is the Country of the LocationRecord; now we need to determine if the Hierarchy that the Area is in is valid for that country
		Adaptation country = (Adaptation) AdaptationUtil
			.followFK(addressRecord, LocationPaths._Root_LocationData_Address._Address_Country);

		if (country == null)
		{
			requestResult.close();
			return false;
		}

		//Get Node's Hierarchy Paths to strip off the first node which should be the Top Hierarchy Node
		@SuppressWarnings("unchecked")
		ArrayList<String> hierarchyPaths = (ArrayList<String>) areaRecord
			.get(LocationPaths._Root_Area._HierarchyPaths);

		if (hierarchyPaths.isEmpty())
		{
			requestResult.close();
			return false;
		}

		for (String hierarchyPath : hierarchyPaths)
		{
			//Isolates the Hierarchy Node from the Hierarchy Path (first node in the list)
			if (hierarchyPath.contains("|"))
			{
				String nameOfHierarchy = (String) hierarchyPath
					.subSequence(0, hierarchyPath.indexOf('|'));

				AdaptationTable locationHierachyTable = locationRecord.getContainer()
					.getTable(LocationPaths._Root_LocationHierarchy.getPathInSchema());

				// Query the LocationHierarchy table for row that has the selected Location Hierarchy Name and Node Type of "Hierarchy")
				requestResult = locationHierachyTable.createRequestResult(
					LocationPaths._Root_LocationHierarchy._Name.format() + "='" + nameOfHierarchy
						+ "' and"
						+ LocationPaths._Root_LocationHierarchy._HierarchyNodeType.format() + "='"
						+ LocationConstants.HIERARCHY_NODE_TYPE_HIERARCHY + "' and "
						+ LocationPaths._Root_LocationHierarchy._IsRecordActive.format() + "=true");

				if (!requestResult.isEmpty())
				{
					Adaptation locationHierarchyTopNode = requestResult.nextAdaptation();

					//Follow the Top Node in the Hierarchy to its Hierarchy Definition Node
					Adaptation locationStructureDefinitionNode = AdaptationUtil.followFK(
						locationHierarchyTopNode,
						LocationPaths._Root_LocationHierarchy._HierarchyDefinitionNode);

					if (locationStructureDefinitionNode != null)
					{

						//Create an ArrayList of this node's Countries
						ArrayList<Adaptation> countries = (ArrayList<Adaptation>) AdaptationUtil
							.getLinkedRecordList(
								locationStructureDefinitionNode,
								LocationPaths._Root_HierarchyDefinitionData_HierarchyStructureDefinition._Countries);

						//If the Country is in the List of Available Countries for this Hierarchy, this Area can be this Location's Parent
						requestResult.close();
						if (countries.contains(country))
						{
							if (requestResult != null)
							{
								requestResult.close();
							}
							return true;
						}
						else
						{
							if (requestResult != null)
							{
								requestResult.close();
							}
							if (requestResult != null)
							{
								requestResult.close();
							}
							return false;
						}
					}
				}
			}
		}
		if (requestResult != null)
		{
			requestResult.close();
		}
		return false;
	}

	public static boolean isRoleInLocationHierarchy(Adaptation locationHierarchy, String role)
	{
		if (locationHierarchy == null)
		{
			return false;
		}

		String locationHierarchyId = locationHierarchy
			.get(LocationPaths._Root_LocationHierarchy._Id)
			.toString();

		AdaptationTable locationHierarchyBusinessPartyRelationshipTable = locationHierarchy
			.getContainer()
			.getTable(
				LocationPaths._Root_HierarchyDefinitionData_LocationHierarchyBusinessPartyRelationship
					.getPathInSchema());

		// Query the Roles table
		RequestResult requestResult = locationHierarchyBusinessPartyRelationshipTable
			.createRequestResult(
				LocationPaths._Root_HierarchyDefinitionData_LocationHierarchyBusinessPartyRelationship._LocationHierarchy
					.format() + "='" + locationHierarchyId + "'");

		if (requestResult.isEmpty())
		{
			return false;
		}

		Adaptation locationHierarchyBusinessPartyRelationshipRecord = null;
		ArrayList<Adaptation> businessPartyRecords = new ArrayList<Adaptation>();

		AdaptationTable businessPartyRoleTable = RepositoryUtils.getTable(
			RepositoryUtils.getDataSet(
				HtzConstants.BUSINESS_PARTY_DATA_SPACE,
				HtzConstants.BUSINESS_PARTY_DATA_SET),
			BusinessPartyPaths._Root_BusinessPartyData_BusinessPartyRole.getPathInSchema()
				.format());

		for (int i = 0; i < requestResult.getSize(); i++)
		{
			locationHierarchyBusinessPartyRelationshipRecord = requestResult.nextAdaptation();

			businessPartyRecords = (ArrayList<Adaptation>) AdaptationUtil.followFKList(
				locationHierarchyBusinessPartyRelationshipRecord,
				LocationPaths._Root_HierarchyDefinitionData_LocationHierarchyBusinessPartyRelationship._BusinessParty);

			for (Adaptation businessParty : businessPartyRecords)
			{
				String businessPartyId = businessParty
					.get(BusinessPartyPaths._Root_BusinessParty._Id)
					.toString();

				// Query the Business Party Roles Table table to see if this Business Party has the input role
				RequestResult requestResultBusinessPartyRoles = businessPartyRoleTable
					.createRequestResult(
						BusinessPartyPaths._Root_BusinessPartyData_BusinessPartyRole._BusinessParty
							.format()
							+ "='" + businessPartyId + "' and "
							+ BusinessPartyPaths._Root_BusinessPartyData_BusinessPartyRole._BusinessPartyRoleType
								.format()
							+ "='" + role + "' and "
							+ BusinessPartyPaths._Root_BusinessPartyData_BusinessPartyRole._RoleCategory
								.format()
							+ "='" + BusinessPartyConstants.BUSINESS_PARTY_ROLE_CATEGORY_EBX + "'");

				if (!requestResultBusinessPartyRoles.isEmpty())
				{
					return true;
				}
			}
		}
		return false;
	}

	public static void updateProjectTeamRoleWithRegionUsers(
		final Session session,
		Adaptation projectRecord,
		String region)
		throws OperationException
	{
		//Retrieve the LocationHiearachyBusinessPartyRelationship records for this region
		AdaptationTable locationHierachyBusinessPartyRelationshipTable = projectRecord
			.getContainer()
			.getTable(
				LocationPaths._Root_HierarchyDefinitionData_LocationHierarchyBusinessPartyRelationship
					.getPathInSchema());

		AdaptationTable businessPartyRoleTable = RepositoryUtils.getTable(
			RepositoryUtils.getDataSet(
				HtzConstants.BUSINESS_PARTY_DATA_SPACE,
				HtzConstants.BUSINESS_PARTY_DATA_SET),
			BusinessPartyPaths._Root_BusinessPartyData_BusinessPartyRole.getPathInSchema()
				.format());

		RequestResult requestResult = locationHierachyBusinessPartyRelationshipTable
			.createRequestResult(
				LocationPaths._Root_HierarchyDefinitionData_LocationHierarchyBusinessPartyRelationship._LocationHierarchy
					.format() + "='" + region + "'");

		if (requestResult.isEmpty())
		{
			requestResult.close();
			return;
		}

		Adaptation role = null;
		int locationHieararchyBusinessPartyRelationshipRecords = requestResult.getSize();
		RequestResult requestResultProjectTeam = null;

		for (int i = 0; i < locationHieararchyBusinessPartyRelationshipRecords; i++)
		{
			Adaptation locationRegionBusinessPartyRelationshipRecord = requestResult
				.nextAdaptation();

			Adaptation businessPartyRecord = AdaptationUtil.followFK(
				locationRegionBusinessPartyRelationshipRecord,
				LocationPaths._Root_HierarchyDefinitionData_LocationHierarchyBusinessPartyRelationship._BusinessParty);

			String businessPartyId = businessPartyRecord
				.get(BusinessPartyPaths._Root_BusinessParty._Id)
				.toString();

			// Query the Business Party Roles Table table to see if this Business Party has the input role
			RequestResult requestResultBusinessPartyRoles = businessPartyRoleTable
				.createRequestResult(
					BusinessPartyPaths._Root_BusinessPartyData_BusinessPartyRole._BusinessParty
						.format()
						+ "='" + businessPartyId + "' and "
						+ BusinessPartyPaths._Root_BusinessPartyData_BusinessPartyRole._RoleCategory
							.format()
						+ "='" + BusinessPartyConstants.BUSINESS_PARTY_ROLE_CATEGORY_EBX + "'");

			role = null;
			for (int j = 0; j < requestResultBusinessPartyRoles.getSize(); j++)
			//Process each BusinessPartyRole record

			{
				Adaptation businessPartyRole = requestResultBusinessPartyRoles.nextAdaptation();

				if (LocationConstants.OPEN_LOCATION_REGIONAL_APPROVER_ROLES.contains(
					businessPartyRole.getString(
						BusinessPartyPaths._Root_BusinessPartyData_BusinessPartyRole._BusinessPartyRoleType)))
				{
					role = AdaptationUtil.followFK(
						businessPartyRole,
						BusinessPartyPaths._Root_BusinessPartyData_BusinessPartyRole._BusinessPartyRoleType);
					break;
				}
			}

			if (role == null)
			{
				continue;
			}

			String projectRecordId = projectRecord.get(LocationPaths._Root_LocationProject._Id)
				.toString();

			//Retrieve the ProjectTeamMemberRecord  for this Project and Role
			AdaptationTable projectTeamTable = projectRecord.getContainer().getTable(
				LocationPaths._Root_ProjectData_LocationProjectTeamMember.getPathInSchema());

			requestResultProjectTeam = projectTeamTable.createRequestResult(
				LocationPaths._Root_ProjectData_LocationProjectTeamMember._Project.format() + "='"
					+ projectRecordId + "' and "
					+ LocationPaths._Root_ProjectData_LocationProjectTeamMember._ProjectRole
						.format()
					+ "='" + role.getString(
						BusinessPartyReferencePaths._Root_BusinessPartyRoleType._Base_Id)
					+ "'");

			if (requestResultProjectTeam.isEmpty())
			{
				continue;
			}

			//Update the ProjectMemberRecord with this User
			Adaptation projectTeamMemberRecord = requestResultProjectTeam.nextAdaptation();

			final Map<Path, Object> projectTeamMembersValueMap = new HashMap<Path, Object>();

			projectTeamMembersValueMap.put(
				LocationPaths._Root_ProjectData_LocationProjectTeamMember._User,
				businessPartyRecord.get(BusinessPartyPaths._Root_BusinessParty._EbxUser));

			ModifyValuesProcedure
				.execute(projectTeamMemberRecord, projectTeamMembersValueMap, session, true, true);
		}

		if (requestResult != null)
		{
			requestResult.close();
		}
		if (requestResultProjectTeam != null)
		{
			requestResultProjectTeam.close();
		}
		return;
	}
	public static Adaptation getLocationToLocationRelationshipRecordForType(
		Adaptation locationRecord,
		String relationshipType)
	{
		AdaptationTable locationToLocationRelationshipTable = locationRecord.getContainer()
			.getTable(
				LocationPaths._Root_LocationData_LocationLocationRelationship.getPathInSchema());

		String locationId = locationRecord.get(LocationPaths._Root_Location._Id).toString();

		// Query the Location/Location Relationship table for THIS Location of type "SERVEDBY"
		RequestResult requestResult = locationToLocationRelationshipTable.createRequestResult(
			LocationPaths._Root_LocationData_LocationLocationRelationship._SubLocation.format()
				+ "='" + locationId + "' and "
				+ LocationPaths._Root_LocationData_LocationLocationRelationship._LocationRelationshipType
					.format()
				+ "='" + relationshipType + "'");

		if (requestResult.isEmpty())
		{
			requestResult.close();
			return null;
		}
		requestResult.close();
		return requestResult.nextAdaptation();
	}

	public static void sendLocationEmailNotification(
		ScriptTaskContext context,
		String domain,
		String projectType,
		String projectTypeEvent)
		throws OperationException, AddressException
	{
		Adaptation projectRecord = LocationProjectWorkflowUtilities
			.getLocationProjectRecord(context, context.getRepository());

		String fromEmailAddress = HtzConstants.HERTZ_FROM_EMAIL_ADDRESS;

		HtzUtilities.sendEmail(
			context,
			fromEmailAddress,
			HtzUtilities.buildEMailDistributionList(
				context,
				projectRecord,
				domain,
				projectType,
				projectTypeEvent),
			buildEmailSubject(projectRecord),
			buildEMailBody(projectRecord, domain, projectType, projectTypeEvent));
	}

	protected static String buildEmailSubject(Adaptation projectRecord)
	{
		if (projectRecord == null)
		{
			return "";
		}

		StringBuffer buf = new StringBuffer();
		buf.append(projectRecord.getString(LocationPaths._Root_LocationProject._Name));
		return buf.toString();
	}

	public static String buildEMailBody(
		Adaptation projectRecord,
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

		Adaptation locationRecord = null;

		StringBuffer buffer = new StringBuffer();

		String fromValue = "";
		String toValue = "";

		String message = emailNotificationRecord
			.getString(CommonReferencePaths._Root_EmailNotification._Message);

		message = message.replaceAll(
			HtzWorkflowConstants.WF_MESSAGE_VARIABLE_START_DELIMITER
				+ HtzWorkflowConstants.WF_MESSAGE_VARIABLE_TODAYSDATE
				+ HtzWorkflowConstants.WF_MESSAGE_VARIABLE_END_DELIMITER,
			new Date().toString());

		if (projectRecord != null)
		{
			toValue = projectRecord.getString(LocationPaths._Root_LocationProject._CancelReason);

			if (toValue == null)
			{
				toValue = "";
			}

			fromValue = HtzWorkflowConstants.WF_MESSAGE_VARIABLE_START_DELIMITER
				+ HtzWorkflowConstants.WF_MESSAGE_VARIABLE_CANCEL_REASON
				+ HtzWorkflowConstants.WF_MESSAGE_VARIABLE_END_DELIMITER;

			message = message.replaceAll(fromValue, toValue);

			locationRecord = AdaptationUtil
				.followFK(projectRecord, LocationPaths._Root_LocationProject._Location);
		}

		if (locationRecord != null)
		{
			toValue = locationRecord.get(LocationPaths._Root_Location._Id).toString();

			if (toValue == null)
			{
				toValue = "";
			}

			fromValue = HtzWorkflowConstants.WF_MESSAGE_VARIABLE_START_DELIMITER
				+ LocationProjectWorkflowConstants.WF_MESSAGE_VARIABLE_LOCATION_ID
				+ HtzWorkflowConstants.WF_MESSAGE_VARIABLE_END_DELIMITER;

			message = message.replaceAll(fromValue, toValue);

			toValue = locationRecord.getString(LocationPaths._Root_Location._LocationNumber);

			if (toValue == null)
			{
				toValue = "";
			}

			fromValue = HtzWorkflowConstants.WF_MESSAGE_VARIABLE_START_DELIMITER
				+ LocationProjectWorkflowConstants.WF_MESSAGE_VARIABLE_LOCATION
				+ HtzWorkflowConstants.WF_MESSAGE_VARIABLE_END_DELIMITER;

			message = message.replaceAll(fromValue, toValue);

			toValue = locationRecord.getString(LocationPaths._Root_Location._Name);

			if (toValue == null)
			{
				toValue = "";
			}

			fromValue = HtzWorkflowConstants.WF_MESSAGE_VARIABLE_START_DELIMITER
				+ LocationProjectWorkflowConstants.WF_MESSAGE_VARIABLE_LOCATION_NAME
				+ HtzWorkflowConstants.WF_MESSAGE_VARIABLE_END_DELIMITER;

			message = message.replaceAll(fromValue, toValue);

			toValue = locationRecord
				.getString(LocationPaths._Root_Location._LocationInformation_Area);

			if (toValue == null)
			{
				toValue = "";
			}

			fromValue = HtzWorkflowConstants.WF_MESSAGE_VARIABLE_START_DELIMITER
				+ LocationProjectWorkflowConstants.WF_MESSAGE_VARIABLE_AREA
				+ HtzWorkflowConstants.WF_MESSAGE_VARIABLE_END_DELIMITER;

			message = message.replaceAll(fromValue, toValue);

			fromValue = HtzWorkflowConstants.WF_MESSAGE_VARIABLE_START_DELIMITER
				+ LocationProjectWorkflowConstants.WF_MESSAGE_VARIABLE_LOCATION_ADDRESS
				+ HtzWorkflowConstants.WF_MESSAGE_VARIABLE_END_DELIMITER;

			toValue = "";

			AdaptationTable locationAddressTable = locationRecord.getContainer()
				.getTable(LocationPaths._Root_LocationData_Address.getPathInSchema());

			String locationId = locationRecord.get(LocationPaths._Root_Location._Id).toString();

			// Query the LocationHierarchy/Business Party Relationship table for the location Region
			RequestResult requestResult = locationAddressTable.createRequestResult(
				LocationPaths._Root_LocationData_Address._Location.format() + "='" + locationId
					+ "' and " + LocationPaths._Root_LocationData_Address._Type.format() + "='"
					+ LocationConstants.LOCATION_ADDRESS_TYPE_BUSINESS + "' and "
					+ LocationPaths._Root_LocationData_Address._IsRecordActive.format() + "=true");

			if (!requestResult.isEmpty())
			{
				Adaptation locationAddressRecord = requestResult.nextAdaptation();

				toValue = locationAddressRecord
					.getString(LocationPaths._Root_LocationData_Address._Address_StreetNumber)
					+ " "
					+ locationAddressRecord
						.getString(LocationPaths._Root_LocationData_Address._Address_AddressLine1)
					+ " "
					+ locationAddressRecord
						.getString(LocationPaths._Root_LocationData_Address._Address_AddressLine2)
					+ " "
					+ locationAddressRecord
						.getString(LocationPaths._Root_LocationData_Address._Address_AddressLine3)
					+ " "
					+ locationAddressRecord
						.getString(LocationPaths._Root_LocationData_Address._Address_City)
					+ ", "
					+ locationAddressRecord
						.getString(LocationPaths._Root_LocationData_Address._Address_StateProvince)
					+ " " + locationAddressRecord
						.getString(LocationPaths._Root_LocationData_Address._Address_Country);

				toValue = toValue.replaceAll("null", "");
				toValue = toValue.replaceAll("  ", " ");
			}

			message = message.replaceAll(fromValue, toValue);
		}

		buffer.append(message);

		return buffer.toString();
	}

	public static boolean isAreaAssignedToAnotherLocationInChildOrMasterDataSpace(
		Adaptation areaRecord,
		Adaptation locationRecord)
	{
		boolean isMasterDataSpace = false;

		//If the input Location has no Status or is CLOSED, no need to check this Location Record
		if (locationRecord.getString(LocationPaths._Root_Location._Status) == null
			|| LocationStatuses.CLOSED
				.equals(locationRecord.getString(LocationPaths._Root_Location._Status)))
		{
			return false;
		}

		//Using the Area Record, attempt to get the Master DataSpace if the input Area Record is in a Child DataSpace
		AdaptationHome parentDataSpace = areaRecord.getHome().getParentBranch();

		//Get the Master DataSet
		Adaptation workingLocationDataSet = parentDataSpace
			.findAdaptationOrNull(areaRecord.getContainer().getAdaptationName());

		if (workingLocationDataSet == null)
		{
			isMasterDataSpace = true;
		}

		workingLocationDataSet = areaRecord.getContainer();

		//Using the DataSpace of the input Area Record, determine if the Area is being used by another Location
		if (isAreaAssignedToAnotherLocation(
			areaRecord,
			locationRecord,
			workingLocationDataSet,
			isMasterDataSpace))
		{
			return true;
		}

		if (isMasterDataSpace)
		{
			return false;
		}

		//Get the Master DataSet
		workingLocationDataSet = parentDataSpace
			.findAdaptationOrNull(areaRecord.getContainer().getAdaptationName());

		isMasterDataSpace = true;

		return isAreaAssignedToAnotherLocation(
			areaRecord,
			locationRecord,
			workingLocationDataSet,
			isMasterDataSpace);
	}

	private static boolean isAreaAssignedToAnotherLocation(
		Adaptation inputAreaRecord,
		Adaptation inputLocationRecord,
		Adaptation inputDataSet,
		boolean isMasterDataSpace)
	{
		String inputAreaId = inputAreaRecord.get(LocationPaths._Root_Area._Base_Id).toString();
		String inputLocationId = inputLocationRecord.get(LocationPaths._Root_Location._Id)
			.toString();
		String inputLocationParentId = null;

		RequestResult requestResult = null;

		AdaptationTable areaTable = inputDataSet
			.getTable(LocationPaths._Root_Area.getPathInSchema());

		//Get the Area record
		requestResult = areaTable.createRequestResult(
			LocationPaths._Root_Area._Base_Id.format() + "='"
				+ inputAreaRecord.getString(LocationPaths._Root_Area._Base_Id) + "' and "
				+ LocationPaths._Root_Area._IsRecordActive.format() + "=true");

		if (requestResult.isEmpty())
		{
			requestResult.close();
			return false;
		}

		AdaptationTable locationTable = inputDataSet
			.getTable(LocationPaths._Root_Location.getPathInSchema());

		AdaptationTable locationLocationRelationshipTable = inputDataSet.getTable(
			LocationPaths._Root_LocationData_LocationLocationRelationship.getPathInSchema());

		//If the Input Location is in the In-Progress status, the location is in a child DataSpace and need to get the Parent Location Id
		if (!isMasterDataSpace
			&& (inputLocationRecord.get_boolean(LocationPaths._Root_Location._IsSatellite)
				|| inputLocationRecord.get_boolean(LocationPaths._Root_Location._IsServedBy)))
		{
			//Retrieve the Location/Location Relationship record to get this Location Parent
			RequestResult requestResultLocationLocationRelationship = locationLocationRelationshipTable
				.createRequestResult(
					LocationPaths._Root_LocationData_LocationLocationRelationship._SubLocation
						.format()
						+ "='" + inputLocationId + "' and ("
						+ LocationPaths._Root_LocationData_LocationLocationRelationship._LocationRelationshipType
							.format()
						+ "='" + LocationConstants.LOCATION_RELATIONSHIP_TYPE_SERVEDBY + "' or "
						+ LocationPaths._Root_LocationData_LocationLocationRelationship._LocationRelationshipType
							.format()
						+ "='" + LocationConstants.LOCATION_RELATIONSHIP_TYPE_SATELLITE + "') and "
						+ LocationPaths._Root_LocationData_LocationLocationRelationship._IsRecordActive
							.format()
						+ "=true");

			if (!requestResultLocationLocationRelationship.isEmpty())
			{
				inputLocationParentId = requestResultLocationLocationRelationship.nextAdaptation()
					.get(
						LocationPaths._Root_LocationData_LocationLocationRelationship._ParentLocation)
					.toString();
			}
		}

		//Retrieve the Location records with this Area Id and Status of either OPEN or IN-PROGRESS
		requestResult = locationTable.createRequestResult(
			LocationPaths._Root_Location._LocationInformation_Area.format() + "='" + inputAreaId
				+ "' and (" + LocationPaths._Root_Location._Status.format() + "='"
				+ LocationStatuses.OPEN + "' or " + LocationPaths._Root_Location._Status.format()
				+ "='" + LocationStatuses.IN_PROGRESS + "')");

		//Read the Location records to determine if there is a Location with this Area
		for (int i = 0; i < requestResult.getSize(); i++)
		{
			Adaptation locationRecord = requestResult.nextAdaptation();

			//If this Location record is equal to the input Location record, skip this record

			String locationId = locationRecord.get(LocationPaths._Root_Location._Id).toString();

			if (inputLocationId.equals(locationId))
			{
				continue;
			}

			//If this Location has the same Id as the input Parent Location, this is the Parent
			if (locationId.equals(inputLocationParentId))
			{
				continue;
			}

			//If Location just read is a ServedBy or Satellite Location, check to see if the input Location is its Parent
			if (locationRecord.get_boolean(LocationPaths._Root_Location._IsSatellite)
				|| locationRecord.get_boolean(LocationPaths._Root_Location._IsServedBy))
			{
				//Check if this ServedBy/Satellite location is the Child of the input Location

				//Retrieve the Location/Location Relationship records with this Location as the Child and the input Location as the Parent
				RequestResult requestResultLocationLocation = locationLocationRelationshipTable
					.createRequestResult(
						LocationPaths._Root_LocationData_LocationLocationRelationship._ParentLocation
							.format()
							+ "='" + inputLocationId + "' and "
							+ LocationPaths._Root_LocationData_LocationLocationRelationship._SubLocation
								.format()
							+ "='" + locationId + "' and "
							+ LocationPaths._Root_LocationData_LocationLocationRelationship._IsRecordActive
								.format()
							+ "=true");

				if (requestResultLocationLocation.isEmpty())
				{
					//Check to see if this Location has the same Parent Location as the Input Location
					if (inputLocationParentId != null)
					{
						RequestResult requestResultLocationLocationRelationship2 = locationLocationRelationshipTable
							.createRequestResult(
								LocationPaths._Root_LocationData_LocationLocationRelationship._ParentLocation
									.format()
									+ "='" + inputLocationParentId + "' and"
									+ LocationPaths._Root_LocationData_LocationLocationRelationship._SubLocation
										.format()
									+ "='" + locationId + "' and "
									+ LocationPaths._Root_LocationData_LocationLocationRelationship._IsRecordActive
										.format()
									+ "=true");

						if (requestResultLocationLocationRelationship2.isEmpty())
						{
							requestResult.close();
							return true;
						}
					}
				}
			}
			else
			{
				//Check if this location is the Parent of the input Location

				//If the input Location is NOT a ServedBy or Satellite, it cannot be this Location Parent
				if (!(inputLocationRecord.get_boolean(LocationPaths._Root_Location._IsSatellite)
					|| inputLocationRecord.get_boolean(LocationPaths._Root_Location._IsServedBy)))
				{
					requestResult.close();
					return true;
				}
				else
				{
					//If this is the Child DataSpace, can do the lookup for the parent
					if (!isMasterDataSpace)
					{
						//Retrieve the Location/Location Relationship records with this Location as the Child and the input Location as the Parent
						RequestResult requestResultLocationLocation = locationLocationRelationshipTable
							.createRequestResult(
								LocationPaths._Root_LocationData_LocationLocationRelationship._ParentLocation
									.format()
									+ "='" + locationId + "' and "
									+ LocationPaths._Root_LocationData_LocationLocationRelationship._SubLocation
										.format()
									+ "='" + inputLocationId + "' and "
									+ LocationPaths._Root_LocationData_LocationLocationRelationship._IsRecordActive
										.format()
									+ "=true");

						if (requestResultLocationLocation.isEmpty())
						{
							requestResult.close();
							return true;
						}
					}
				}
			}
		}
		requestResult.close();
		return false;
	}

	public static boolean isRegionInTheLocationHierarchy(Adaptation locationRecord)
	{
		Adaptation areaRecord = AdaptationUtil
			.followFK(locationRecord, LocationPaths._Root_Location._LocationInformation_Area);
		Adaptation regionRecord = AdaptationUtil
			.followFK(locationRecord, LocationPaths._Root_Location._LocationInformation_Region);

		ArrayList<Adaptation> parents = (ArrayList<Adaptation>) AdaptationUtil
			.followFKList(areaRecord, LocationPaths._Root_Area._Parents);

		for (int i = 0; i < parents.size(); i++)
		{
			if (isRegionInTheParentNode(parents.get(i), regionRecord))
			{
				return true;
			}
		}
		return false;
	}

	public static boolean isRegionInTheParentNode(Adaptation adaptation, Adaptation regionRecord)
	{
		if (adaptation == null)
		{
			return false;
		}

		if (adaptation.equals(regionRecord))
		{
			return true;
		}

		ArrayList<Adaptation> parents = (ArrayList<Adaptation>) AdaptationUtil
			.followFKList(adaptation, LocationPaths._Root_Area._Parents);

		for (int i = 0; i < parents.size(); i++)
		{
			if (isRegionInTheParentNode(parents.get(i), regionRecord))
			{
				return true;
			}
		}
		return false;
	}

	public static void createLocationProjectUserTaskLog(
		Adaptation projectRecord,
		Session session,
		DataContext context,
		String workflowTask)
		throws OperationException
	{
		String projectType = "";
		String projectName = "";
		String userTaskName = "";
		String offeredToRole = "";
		String dataSpace = "";

		if (projectRecord == null)
		{
			return;
		}

		String workingDataSpace = context
			.getVariableString(HtzWorkflowConstants.DATA_CONTEXT_PARM_WORKING_DATA_SPACE);
		String masterDataSpace = context
			.getVariableString(HtzWorkflowConstants.DATA_CONTEXT_PARM_MASTER_DATA_SPACE);

		AdaptationTable locationProjectUserTaskLogTable = null;

		if (workingDataSpace.equals(masterDataSpace))
		{
			locationProjectUserTaskLogTable = projectRecord.getContainer()
				.getTable(LocationPaths._Root_ProjectData_ProjectUserTaskLog.getPathInSchema());
		}
		else
		{
			AdaptationHome parentDataSpace = projectRecord.getHome().getParentBranch();

			final Adaptation masterLocationDataSet = parentDataSpace
				.findAdaptationOrNull(projectRecord.getContainer().getAdaptationName());

			//If the Master Location Data Set is null; then this is NOT a Child Dataspace
			if (masterLocationDataSet == null)
			{
				return;
			}

			locationProjectUserTaskLogTable = masterLocationDataSet
				.getTable(LocationPaths._Root_ProjectData_ProjectUserTaskLog.getPathInSchema());
		}

		HashMap<Path, Object> pathValueMap = new HashMap<Path, Object>();

		offeredToRole = getOfferedToRoleForWorkflowTask(workflowTask);

		projectType = context.getVariableString(HtzWorkflowConstants.DATA_CONTEXT_PROJECT_TYPE);
		userTaskName = context.getVariableString(HtzWorkflowConstants.DATA_CONTEXT_USER_TASK_NAME);
		dataSpace = context
			.getVariableString(HtzWorkflowConstants.DATA_CONTEXT_PARM_WORKING_DATA_SPACE);
		projectName = projectRecord.getString(LocationPaths._Root_LocationProject._Name);

		pathValueMap
			.put(LocationPaths._Root_ProjectData_ProjectUserTaskLog._OfferedToRole, offeredToRole);
		pathValueMap
			.put(LocationPaths._Root_ProjectData_ProjectUserTaskLog._ProjectType, projectType);
		pathValueMap
			.put(LocationPaths._Root_ProjectData_ProjectUserTaskLog._ProjectName, projectName);
		pathValueMap
			.put(LocationPaths._Root_ProjectData_ProjectUserTaskLog._UserTaskName, userTaskName);
		pathValueMap.put(LocationPaths._Root_ProjectData_ProjectUserTaskLog._DataSpace, dataSpace);
		pathValueMap.put(
			LocationPaths._Root_ProjectData_ProjectUserTaskLog._TaskStartEndTimestamp_StartDateTime,
			new Date());

		//requestResult.close();

		Adaptation locationProjectUserTaskLogRecord = CreateRecordProcedure
			.execute(locationProjectUserTaskLogTable, pathValueMap, session);

		context.setVariableString(
			HtzWorkflowConstants.DATA_CONTEXT_PROJECT_USER_TASK_LOG_ID,
			locationProjectUserTaskLogRecord
				.get(LocationPaths._Root_ProjectData_ProjectUserTaskLog._Id)
				.toString());
	}

	public static void updateLocationProjectUserTaskLog(
		Adaptation projectRecord,
		Session session,
		String locationProjectUserTaskLogRecordId,
		String allocatedUser)
		throws OperationException
	{
		if (locationProjectUserTaskLogRecordId == null)
		{
			return;
		}

		AdaptationHome parentDataSpace = projectRecord.getHome().getParentBranch();

		final Adaptation masterLocationDataSet = parentDataSpace
			.findAdaptationOrNull(projectRecord.getContainer().getAdaptationName());

		AdaptationTable locationProjectUserTaskLogTable = null;

		//If the Master Location Data Set is null; then this is NOT a Child Dataspace
		if (masterLocationDataSet == null)
		{
			locationProjectUserTaskLogTable = RepositoryUtils.getTable(
				RepositoryUtils
					.getDataSet(HtzConstants.LOCATION_DATA_SPACE, HtzConstants.LOCATION_DATA_SET),
				LocationPaths._Root_ProjectData_ProjectUserTaskLog.getPathInSchema().format());

		}
		else
		{
			locationProjectUserTaskLogTable = masterLocationDataSet
				.getTable(LocationPaths._Root_ProjectData_ProjectUserTaskLog.getPathInSchema());
		}

		if (locationProjectUserTaskLogTable == null)
		{
			return;
		}

		// Query the LocationHierarchy/Business Party Relationship table for the location Region
		RequestResult requestResult = locationProjectUserTaskLogTable.createRequestResult(
			LocationPaths._Root_ProjectData_ProjectUserTaskLog._Id.format() + "='"
				+ locationProjectUserTaskLogRecordId + "'");

		if (requestResult.isEmpty())
		{
			return;
		}

		Adaptation locationProjectUserTaskRecord = requestResult.nextAdaptation();

		HashMap<Path, Object> pathValueMap = new HashMap<Path, Object>();

		if (allocatedUser == null)
		{
			pathValueMap.put(
				LocationPaths._Root_ProjectData_ProjectUserTaskLog._TaskStartEndTimestamp_EndDateTime,
				new Date());
		}
		else
		{
			AdaptationTable businessPartyTable = RepositoryUtils.getTable(
				RepositoryUtils.getDataSet(
					HtzConstants.BUSINESS_PARTY_DATA_SPACE,
					HtzConstants.BUSINESS_PARTY_DATA_SET),
				BusinessPartyPaths._Root_BusinessParty.getPathInSchema().format());

			// Query the Business Party Table to get this User's User Id
			requestResult = businessPartyTable.createRequestResult(
				BusinessPartyPaths._Root_BusinessParty._EbxUser.format() + "='" + allocatedUser
					+ "' and" + BusinessPartyPaths._Root_BusinessParty._IsRecordActive.format()
					+ "=true");

			if (!requestResult.isEmpty())
			{
				pathValueMap.put(
					LocationPaths._Root_ProjectData_ProjectUserTaskLog._AllocatedUser,
					requestResult.nextAdaptation()
						.get(BusinessPartyPaths._Root_BusinessParty._Id)
						.toString());
			}
		}

		ModifyValuesProcedure
			.execute(locationProjectUserTaskRecord, pathValueMap, session, true, true);

		requestResult.close();

		return;
	}

	public static String getOfferedToRoleForWorkflowTask(String workflowTask)
	{
		String offeredToRole = "";

		AdaptationTable workflowTaskTable = RepositoryUtils.getTable(
			RepositoryUtils.getDataSet(HtzConstants.ADMIN_DATA_SPACE, HtzConstants.ADMIN_DATA_SET),
			AdminPaths._Root_WorkflowTask.getPathInSchema().format());

		// Query the Admin Table to get the Role for this workflowTask
		RequestResult requestResult = workflowTaskTable.createRequestResult(
			AdminPaths._Root_WorkflowTask._Id.format() + "='" + workflowTask + "'");

		if (!requestResult.isEmpty())
		{
			Adaptation workflowTaskRecord = requestResult.nextAdaptation();
			Adaptation workflowRoleRecord = AdaptationUtil.getFirstRecordFromLinkedRecordList(
				workflowTaskRecord,
				AdminPaths._Root_WorkflowTask._WorkflowRoles);
			offeredToRole = workflowRoleRecord.get(AdminPaths._Root_WorkflowRole._Role).toString();
		}
		return offeredToRole;
	}

	public static String getAllocatedUserForOfferedRole(
		Adaptation projectRecord,
		String offeredRole)
	{
		String allocatedUser = "";

		if (offeredRole == null)
		{
			return allocatedUser;
		}

		AdaptationTable projectTeamMemberTable = projectRecord.getContainer()
			.getTable(LocationPaths._Root_ProjectData_LocationProjectTeamMember.getPathInSchema());

		String projectRecordId = projectRecord.get(LocationPaths._Root_LocationProject._Id)
			.toString();

		// Query the Project Team Member table for the project role
		RequestResult requestResult = projectTeamMemberTable.createRequestResult(
			LocationPaths._Root_ProjectData_LocationProjectTeamMember._Project.format() + "='"
				+ projectRecordId + "' and "
				+ LocationPaths._Root_ProjectData_LocationProjectTeamMember._ProjectRole.format()
				+ "='" + offeredRole + "'");

		if (!requestResult.isEmpty())
		{
			Adaptation locationProjectTeamRecord = requestResult.nextAdaptation();
			Object locationProjectTeamMemberUser = locationProjectTeamRecord
				.get(LocationPaths._Root_ProjectData_LocationProjectTeamMember._User);

			Adaptation userRecord = AdaptationUtil.followFK(
				locationProjectTeamRecord,
				LocationPaths._Root_ProjectData_LocationProjectTeamMember._User);

			Path passwordPath = Path.parse("./firstName");

			String pw = userRecord.getString(passwordPath);

			if (locationProjectTeamMemberUser != null)
			{
				allocatedUser = locationProjectTeamMemberUser.toString();
			}
		}
		return allocatedUser;
	}

	public static WorkflowLauncherContext createWorkflowLauncherContext(
		Adaptation projectRecord,
		Adaptation locationRecord,
		Session session,
		Repository repo,
		String projectType,
		String projectNamePrefix)
	{
		WorkflowLauncherContext workflowLauncherContext = new WorkflowLauncherContext(
			session,
			repo,
			locationRecord.getHome(),
			locationRecord);
		workflowLauncherContext
			.setParameter(SubjectWorkflowLauncher.PARAM_PROJECT_TYPE, projectType);
		workflowLauncherContext.setParameter(
			SubjectWorkflowLauncher.PARAM_PROJECT_NAME,
			projectNamePrefix + locationRecord.getString(LocationPaths._Root_Location._Name));

		return workflowLauncherContext;
	}

	@SuppressWarnings("unchecked")
	public static boolean areaPossibleParents(Adaptation inputHierarchyRecord, String areaType)
	{
		Adaptation locationHierarchyRecord = inputHierarchyRecord;

		//A Parent of Revenue Area must be a Leaf Node (i.e. has no children)
		if (!((ArrayList<String>) locationHierarchyRecord
			.get(LocationPaths._Root_LocationHierarchy._Children)).isEmpty())
		{
			return false;
		}

		ArrayList<String> locationHierarchyPaths = ((ArrayList<String>) locationHierarchyRecord
			.get(LocationPaths._Root_LocationHierarchy._HierarchyPaths));

		ArrayList<String> hierarchyNames = new ArrayList<String>();
		String locationPath = null;
		int delimiter;

		//Strip the Hierarchy Name from each Hierarchy Path
		for (int i = 0; i < locationHierarchyPaths.size(); i++)
		{
			locationPath = locationHierarchyPaths.get(i);
			delimiter = locationPath.indexOf("|");
			if (delimiter > 0)
			{
				locationPath = locationPath.substring(0, delimiter).trim();
				hierarchyNames.add(locationPath);
			}
		}

		//Determine if this Leaf Node is in an allowable hierarchy for the Area Type
		AdaptationTable areaTypeLocationHierarchyRelationshipTable = RepositoryUtils.getTable(
			RepositoryUtils
				.getDataSet(HtzConstants.LOCATION_DATA_SPACE, HtzConstants.LOCATION_DATA_SET),
			LocationPaths._Root_AreaData_AreaTypeLocationHierarchyRelationship.getPathInSchema()
				.format());

		// Query the Area Type Location Hierarchy Relationship Table 
		RequestResult requestResult = areaTypeLocationHierarchyRelationshipTable
			.createRequestResult(
				LocationPaths._Root_AreaData_AreaTypeLocationHierarchyRelationship._AreaType
					.format() + "='" + areaType + "' and"
					+ LocationPaths._Root_AreaData_AreaTypeLocationHierarchyRelationship._IsRecordActive
						.format()
					+ "=true");

		String locationHierarchyName = "";

		for (int j = 0; j < requestResult.getSize(); j++)
		{
			Adaptation areaTypeLocationHierarchyRelationshipRecord = requestResult.nextAdaptation();

			Adaptation LocationHierarchyRecord = AdaptationUtil.followFK(
				areaTypeLocationHierarchyRelationshipRecord,
				LocationPaths._Root_AreaData_AreaTypeLocationHierarchyRelationship._LocationHierarchy);

			if (LocationHierarchyRecord == null)
			{
				continue;
			}

			locationHierarchyName = LocationHierarchyRecord
				.getString(LocationPaths._Root_LocationHierarchy._Name);

			if (hierarchyNames.contains(locationHierarchyName))
			{
				requestResult.close();
				return true;
			}
		}

		requestResult.close();
		return false;
	}

	public static ArrayList<String> areaLocations(Adaptation areaRecord, String areaType)
	{
		String areaRecordId = areaRecord.get(LocationPaths._Root_Area._Base_Id).toString();
		RequestResult requestResult = null;

		AdaptationTable locationTable = areaRecord.getContainer()
			.getTable(LocationPaths._Root_Location.getPathInSchema());

		// Query the LocationHierarchy table for the Revenue Area parent
		if (LocationConstants.OPERATION_AREA_TYPE_AREA.equals(areaType))
		{
			requestResult = locationTable.createRequestResult(
				LocationPaths._Root_Location._LocationInformation_Area.format() + "='"
					+ areaRecordId + "' and " + LocationPaths._Root_Location._Status.format() + "='"
					+ LocationStatuses.OPEN + "'");
		}
		else if (LocationConstants.OPERATION_AREA_TYPE_CMS.equals(areaType))
		{
			requestResult = locationTable.createRequestResult(
				LocationPaths._Root_Location._LocationInformation_CmsArea.format() + "='"
					+ areaRecordId + "' and " + LocationPaths._Root_Location._Status.format() + "='"
					+ LocationStatuses.OPEN + "'");
		}
		else if (LocationConstants.OPERATION_AREA_TYPE_FLEET.equals(areaType))
		{
			requestResult = locationTable.createRequestResult(
				LocationPaths._Root_Location._LocationInformation_FleetArea.format() + "='"
					+ areaRecordId + "' and " + LocationPaths._Root_Location._Status.format() + "='"
					+ LocationStatuses.OPEN + "'");
		}

		ArrayList<String> areaLocations = new ArrayList<String>();

		for (int i = 0; i < requestResult.getSize(); i++)
		{
			areaLocations
				.add(requestResult.nextAdaptation().getString(LocationPaths._Root_Location._Name));
		}

		requestResult.close();
		return areaLocations;
	}

	public static boolean isHierarchyValidForThisAreaType(String locationPath, String areaType)
	{
		ArrayList<String> hierarchyNames = new ArrayList<String>();
		int delimiter;

		if (locationPath == null)
		{
			return false;
		}

		delimiter = locationPath.indexOf("|");

		if (delimiter < 0)
		{
			return false;
		}

		locationPath = locationPath.substring(0, delimiter).trim();
		hierarchyNames.add(locationPath);

		//Determine if this Leaf Node is in an allowable hierarchy for the Area Type
		AdaptationTable areaTypeLocationHierarchyRelationshipTable = RepositoryUtils.getTable(
			RepositoryUtils
				.getDataSet(HtzConstants.LOCATION_DATA_SPACE, HtzConstants.LOCATION_DATA_SET),
			LocationPaths._Root_AreaData_AreaTypeLocationHierarchyRelationship.getPathInSchema()
				.format());

		// Query the Area Type Location Hierarchy Relationship Table 
		RequestResult requestResult = areaTypeLocationHierarchyRelationshipTable
			.createRequestResult(
				LocationPaths._Root_AreaData_AreaTypeLocationHierarchyRelationship._AreaType
					.format() + "='" + areaType + "' and"
					+ LocationPaths._Root_AreaData_AreaTypeLocationHierarchyRelationship._IsRecordActive
						.format()
					+ "=true");

		return !requestResult.isEmpty();
	}

	public static boolean isAreaInAreaBandingRange(Adaptation areaRecord, Adaptation locationRecord)
	{
		String areaId = areaRecord.getString(LocationPaths._Root_Area._Base_Id);

		if (areaId.length() != 4)
		{
			return false;
		}

		if (locationRecord != null)
		{
			//Area must be between the lowest and highest band range
			if (locationRecord.getString(
				LocationPaths._Root_Location._LocationInformation_AreaBanding_AreaBanding) != null)
			{
				if (areaId.compareTo(
					locationRecord.getString(
						LocationPaths._Root_Location._LocationInformation_AreaBanding_BandingLowerEnd)) < 0
					|| areaId.compareTo(
						locationRecord.getString(
							LocationPaths._Root_Location._LocationInformation_AreaBanding_BandingUpperEnd)) > 0)
				{
					return false;
				}
			}
		}
		return true;
	}

	public static Adaptation locationPrimaryBusinessAddress(Adaptation locationRecord)
	{
		String locationId = locationRecord.get(LocationPaths._Root_Location._Id).toString();

		AdaptationTable locationAddressTable = locationRecord.getContainer()
			.getTable(LocationPaths._Root_LocationData_Address.getPathInSchema());

		RequestResult requestResult = locationAddressTable.createRequestResult(
			LocationPaths._Root_LocationData_Address._Location.format() + "='" + locationId
				+ "' and " + LocationPaths._Root_LocationData_Address._Type.format() + "='"
				+ LocationConstants.LOCATION_ADDRESS_TYPE_BUSINESS + "' and "
				+ LocationPaths._Root_LocationData_Address._PrimarySecondary.format() + "='"
				+ com.hertz.mdm.location.enums.AddressPirmarySecondary.PRIMARY + "'");

		return requestResult.nextAdaptation();
	}

	public static Adaptation locationPrimaryIntelligentId(Adaptation locationRecord)
	{
		if (locationRecord == null)
		{
			return null;
		}

		String locationId = locationRecord.get(LocationPaths._Root_Location._Id).toString();

		AdaptationTable locationIntelligentLocationIdRelationshipTable = locationRecord
			.getContainer()
			.getTable(
				LocationPaths._Root_LocationData_LocationIntelligentLocationIdRelationship
					.getPathInSchema());

		RequestResult requestResult = locationIntelligentLocationIdRelationshipTable
			.createRequestResult(
				LocationPaths._Root_LocationData_LocationIntelligentLocationIdRelationship._Location
					.format() + "='" + locationId + "' and "
					+ LocationPaths._Root_LocationData_LocationIntelligentLocationIdRelationship._IsPrimary
						.format()
					+ "=true");

		return requestResult.nextAdaptation();
	}

	public static boolean isLocationAirportOperations(Adaptation locationRecord)
		throws OperationException
	{
		if (locationRecord == null)
		{
			return false;
		}

		String locationId = locationRecord.get(LocationPaths._Root_Location._Id).toString();

		AdaptationTable locationLocationTypeGroupRelationshipTable = locationRecord.getContainer()
			.getTable(
				LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship
					.getPathInSchema());

		RequestResult requestResult = locationLocationTypeGroupRelationshipTable
			.createRequestResult(
				LocationPaths._Root_LocationData_LocationIntelligentLocationIdRelationship._Location
					.format() + "='" + locationId + "' and "
					+ LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship._IsAirportOperations
						.format()
					+ "=true");

		return !requestResult.isEmpty();
	}

	public static boolean isLSubFunctionTypeAssociatedWithLocaton(
		Adaptation locationRecord,
		String subType)
		throws OperationException
	{
		if (locationRecord == null)
		{
			return false;
		}

		String locationId = locationRecord.get(LocationPaths._Root_Location._Id).toString();

		AdaptationTable locationLocationTypeGroupRelationshipTable = locationRecord.getContainer()
			.getTable(
				LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship
					.getPathInSchema());

		// Query the LocationGroupType table for THIS Location with the Location Sub Type equal to the input value
		RequestResult requestResult = locationLocationTypeGroupRelationshipTable
			.createRequestResult(
				LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship._Location
					.format() + "='" + locationId + "' and "
					+ LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship.__subFunctionType
						.format()
					+ "='" + subType + "'");

		return !requestResult.isEmpty();
	}

	public static boolean isLocationOnAirport(Adaptation locationRecord) throws OperationException
	{
		if (locationRecord == null)
		{
			return false;
		}

		String locationId = locationRecord.get(LocationPaths._Root_Location._Id).toString();

		AdaptationTable locationLocationTypeGroupRelationshipTable = locationRecord.getContainer()
			.getTable(
				LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship
					.getPathInSchema());

		// Query the LocationGroupType table for THIS Location with Location Type Group Rental Type = "AIRPORT"
		RequestResult requestResult = locationLocationTypeGroupRelationshipTable
			.createRequestResult(
				LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship._Location
					.format() + "='" + locationId + "' and "
					+ LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship._RentalType
						.format()
					+ "='" + RentalTypes.AIRPORT + "'");

		return !requestResult.isEmpty();
	}

	public static boolean isLocationOnRail(Adaptation locationRecord) throws OperationException
	{
		if (locationRecord == null)
		{
			return false;
		}

		String locationId = locationRecord.get(LocationPaths._Root_Location._Id).toString();

		AdaptationTable locationLocationTypeGroupRelationshipTable = locationRecord.getContainer()
			.getTable(
				LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship
					.getPathInSchema());

		// Query the LocationGroupType table for THIS Location with Location Type Group Rental Type = "AIRPORT"
		RequestResult requestResult = locationLocationTypeGroupRelationshipTable
			.createRequestResult(
				LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship._Location
					.format()
					+ "='" + locationId + "' and "
					+ LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship._LocationTypeGroup
						.format()
					+ "='" + LocationConstants.LOCATION_NONAIRPORT_RAIL_STATION + "'");

		return !requestResult.isEmpty();
	}

	public static void updateLocationRecordForIntelligentIds(
		Adaptation locationRecord,
		Session session)
		throws OperationException
	{
		//boolean isModifyRequired = false;

		if (locationRecord == null)
		{
			return;
		}

		Adaptation intelligentLocationIdRecord = HtzLocationUtilities
			.locationPrimaryIntelligentId(locationRecord);

		HashMap<Path, Object> pathValueMap = new HashMap<Path, Object>();

		//Update the Location Record with the Primary Intelligent Location Id
		if (intelligentLocationIdRecord != null)
		{
			pathValueMap.put(
				LocationPaths._Root_Location._IntelligentLocationId,
				intelligentLocationIdRecord.getString(
					LocationPaths._Root_LocationData_LocationIntelligentLocationIdRelationship.__intelligentLocationId));
		}

		pathValueMap.put(LocationPaths._Root_Location._ResetIntelligentIds, false);

		ModifyValuesProcedure.execute(locationRecord, pathValueMap, session);

		setReplicatedBooleanValues(locationRecord, session);
	}

	public static void setReplicatedBooleanValues(Adaptation locationRecord, Session session)
		throws OperationException
	{
		HashMap<Path, Object> pathValueMap = new HashMap<Path, Object>();

		//Update Is On Airport
		if (locationRecord.get(LocationPaths._Root_Location._IsOnAirport) == null
			|| !(boolean) locationRecord.get(LocationPaths._Root_Location._IsOnAirportComputed)
				.equals(locationRecord.get_boolean(LocationPaths._Root_Location._IsOnAirport)))
		{
			pathValueMap.put(
				LocationPaths._Root_Location._IsOnAirport,
				locationRecord.get_boolean(LocationPaths._Root_Location._IsOnAirportComputed));
		}

		//Update Airport Operations
		if (locationRecord.get(LocationPaths._Root_Location._IsAirportOperations) == null
			|| !(boolean) locationRecord
				.get(LocationPaths._Root_Location._IsAirportOperationsComputed)
				.equals(
					locationRecord.get_boolean(LocationPaths._Root_Location._IsAirportOperations)))
		{
			pathValueMap.put(
				LocationPaths._Root_Location._IsAirportOperations,
				locationRecord
					.get_boolean(LocationPaths._Root_Location._IsAirportOperationsComputed));
		}

		//Update Airport Operations
		if (locationRecord.get(LocationPaths._Root_Location._IsOnRail) == null
			|| !(boolean) locationRecord.get(LocationPaths._Root_Location._IsOnRailComputed)
				.equals(locationRecord.get_boolean(LocationPaths._Root_Location._IsOnRail)))
		{
			pathValueMap.put(
				LocationPaths._Root_Location._IsOnRail,
				locationRecord.get_boolean(LocationPaths._Root_Location._IsOnRailComputed));
		}

		//Update Is Served By
		if (locationRecord.get(LocationPaths._Root_Location._IsServedBy) == null
			|| !(boolean) locationRecord.get(LocationPaths._Root_Location._IsServedByComputed)
				.equals(locationRecord.get_boolean(LocationPaths._Root_Location._IsServedBy)))
		{
			pathValueMap.put(
				LocationPaths._Root_Location._IsServedBy,
				locationRecord.get_boolean(LocationPaths._Root_Location._IsServedByComputed));
		}

		//Update Is Satellite
		if (locationRecord.get(LocationPaths._Root_Location._IsSatellite) == null
			|| !(boolean) locationRecord.get(LocationPaths._Root_Location._IsSatelliteComputed)
				.equals(locationRecord.get_boolean(LocationPaths._Root_Location._IsSatellite)))
		{
			pathValueMap.put(
				LocationPaths._Root_Location._IsSatellite,
				locationRecord.get_boolean(LocationPaths._Root_Location._IsSatelliteComputed));
		}

		//Update Is Staffed by Hertz Personnel
		if (locationRecord.get(LocationPaths._Root_Location._IsStaffedByHertzPersonnel) == null
			|| !(boolean) locationRecord
				.get(LocationPaths._Root_Location._IsStaffedByHertzPersonnelComputed)
				.equals(
					locationRecord
						.get_boolean(LocationPaths._Root_Location._IsStaffedByHertzPersonnel)))
		{
			pathValueMap.put(
				LocationPaths._Root_Location._IsStaffedByHertzPersonnel,
				locationRecord
					.get_boolean(LocationPaths._Root_Location._IsStaffedByHertzPersonnelComputed));
		}

		pathValueMap.put(LocationPaths._Root_Location._ResetReplicatedBooleanValues, false);

		ModifyValuesProcedure.execute(locationRecord, pathValueMap, session);
	}

	public static void setPrimaryCurrency(Adaptation locationRecord, Session session)
		throws OperationException
	{
		boolean isPrimaryCurrencyFound = false;

		String locationId = locationRecord.get(LocationPaths._Root_Location._Id).toString();

		Adaptation primaryBusinessAddress = HtzLocationUtilities
			.locationPrimaryBusinessAddress(locationRecord);

		if (primaryBusinessAddress != null)
		{
			Adaptation isoCountryRecord = AdaptationUtil.followFK(
				primaryBusinessAddress,
				LocationPaths._Root_LocationData_Address._Address_Country);

			String currency = isoCountryRecord
				.getString(CommonReferencePaths._Root_Country_ISO31661._CurrencyAlphaCode);

			String currencyFK = isoCountryRecord
				.getString(CommonReferencePaths._Root_Country_ISO31661._IsIdependent);

			AdaptationTable locationCurrencyRelationshipTable = locationRecord.getContainer()
				.getTable(
					LocationPaths._Root_LocationData_LocationCurrencyRelationship
						.getPathInSchema());

			RequestResult requestResult = locationCurrencyRelationshipTable.createRequestResult(
				LocationPaths._Root_LocationData_LocationCurrencyRelationship._Location.format()
					+ "='" + locationId + "'");

			HashMap<Path, Object> pathValueMap = new HashMap<Path, Object>();

			for (int i = 0; i < requestResult.getSize(); i++)
			{
				Adaptation locationCurrencyRelationshipRecord = requestResult.nextAdaptation();

				if (locationCurrencyRelationshipRecord.getString(
					LocationPaths._Root_LocationData_LocationCurrencyRelationship._Currency) != null
					&& locationCurrencyRelationshipRecord
						.getString(
							LocationPaths._Root_LocationData_LocationCurrencyRelationship._Currency)
						.equals(currency))
				{
					isPrimaryCurrencyFound = true;

					pathValueMap.put(
						LocationPaths._Root_LocationData_LocationCurrencyRelationship._IsPrimaryCurrency,
						true);
				}
				else
				{
					pathValueMap.put(
						LocationPaths._Root_LocationData_LocationCurrencyRelationship._IsPrimaryCurrency,
						false);
				}
				ModifyValuesProcedure
					.execute(locationCurrencyRelationshipRecord, pathValueMap, session);
			}
			if (!isPrimaryCurrencyFound)
			{
				pathValueMap.put(
					LocationPaths._Root_LocationData_LocationCurrencyRelationship._Location,
					locationId);
				pathValueMap.put(
					LocationPaths._Root_LocationData_LocationCurrencyRelationship._Currency,
					currencyFK);
				pathValueMap.put(
					LocationPaths._Root_LocationData_LocationCurrencyRelationship._IsPrimaryCurrency,
					true);

				CreateRecordProcedure
					.execute(locationCurrencyRelationshipTable, pathValueMap, session);
			}
		}

		HashMap<Path, Object> pathValueMapLocation = new HashMap<Path, Object>();

		pathValueMapLocation.put(LocationPaths._Root_Location._ResetPrimaryCurrencyCode, false);

		ModifyValuesProcedure.execute(locationRecord, pathValueMapLocation, session);
	}

	public static Adaptation closestAirportToLatitudeLongitude(
		double locationLatitude,
		double locationLongitude,
		double locationElevation,
		String measurementSystem,
		boolean excludeCommercialAirport)
		throws OperationException
	{
		Adaptation closestAirportRecord = null;
		Long closestAirportDistance = null;
		long longDistance = (long) 0;
		double distance = 0;

		AdaptationTable commonReferenceTable = RepositoryUtils.getTable(
			RepositoryUtils.getDataSet(
				HtzConstants.COMMON_REFERENCE_DATA_SPACE,
				HtzConstants.COMMON_REFERENCE_DATA_SET),
			CommonReferencePaths._Root_Airport.getPathInSchema().format());

		RequestResult requestResult = commonReferenceTable.createRequestResult(
			CommonReferencePaths._Root_Airport._IsRecordActive.format() + "=true");

		for (int i = 0; i < requestResult.getSize(); i++)
		{
			Adaptation airportRecord = requestResult.nextAdaptation();

			if (airportRecord == null
				|| airportRecord.get(CommonReferencePaths._Root_Airport._Latitude) == null
				|| airportRecord.get(CommonReferencePaths._Root_Airport._Latitude) == null)
			{
				continue;
			}

			//If Exclude Commercial Airports is true, and there is an Open location with this OAG (IATA) code, bypass
			if (excludeCommercialAirport)
			{
				AdaptationTable loationReferenceTable = RepositoryUtils.getTable(
					RepositoryUtils.getDataSet(
						HtzConstants.LOCATION_DATA_SPACE,
						HtzConstants.LOCATION_DATA_SET),
					LocationPaths._Root_Location.getPathInSchema().format());

				RequestResult requestResultLocation = loationReferenceTable.createRequestResult(
					LocationPaths._Root_Location._IsOnAirport.format() + "=true and "
						+ "osd:is-not-null("
						+ LocationPaths._Root_Location._LocationInformation_Oag.format() + ") and "
						+ LocationPaths._Root_Location._Status.format() + "='"
						+ LocationStatuses.OPEN + "' and "
						+ LocationPaths._Root_Location._IsAirportOperations.format() + "=false");

				if (!requestResultLocation.isEmpty())
				{
					continue;
				}
			}

			Double airportLatitude = Double
				.parseDouble(airportRecord.getString(CommonReferencePaths._Root_Airport._Latitude));
			Double airportLongitude = Double.parseDouble(
				airportRecord.getString(CommonReferencePaths._Root_Airport._Longitude));
			Double airportElevation = airportRecord
				.get(CommonReferencePaths._Root_Airport._Altitude) == null ? 0
					: Double.parseDouble(
						airportRecord.getString(CommonReferencePaths._Root_Airport._Altitude));

			distance = HtzUtilities.distance(
				locationLatitude,
				airportLatitude,
				locationLongitude,
				airportLongitude,
				locationElevation,
				airportElevation,
				measurementSystem.equals(HtzConstants.MEASUREMENT_TYPE_US) ? "M" : "K");

			longDistance = (new Double(distance)).longValue();

			//If the compare results in a negative number; it is closer than the previous closest
			if (closestAirportDistance == null || longDistance < closestAirportDistance)
			{
				closestAirportRecord = airportRecord;
				closestAirportDistance = longDistance;
			}
		}
		return closestAirportRecord;
	}

	public static void alignIntelligentLocationIdsWithLocationTypes(
		Adaptation locationRecord,
		ProcedureContext procedureContext,
		ScriptTaskContext context)
		throws OperationException
	{
		if (locationRecord == null)
		{
			return;
		}

		if (procedureContext == null && context == null)
		{
			return;
		}

		String locationId = locationRecord.get(LocationPaths._Root_Location._Id).toString();
		String locationOag = null;
		String locationBrandCode = null;
		String locationOwnershipType = null;
		String locationFunctionType = null;
		String locationNumber = null;
		String originalLocationNumber = null;

		Adaptation oagRecord = AdaptationUtil
			.followFK(locationRecord, LocationPaths._Root_Location._LocationInformation_Oag);

		if (oagRecord != null)
		{
			locationOag = oagRecord.getString(CommonReferencePaths._Root_Airport._IataFAA);
		}

		boolean isOnAirport = locationRecord.get_boolean(LocationPaths._Root_Location._IsOnAirport);

		if (!isOnAirport && locationOag == null)
		{
			//HtzUtilities.writeToFileInFileSystem("*** In OFF-Airport Logic ");

			//Non-Airport, use the OAG of the closest Non-Commercial Airport
			Adaptation locationPrimaryBusinessAddressRecord = HtzLocationUtilities
				.locationPrimaryBusinessAddress(locationRecord);

			if (locationPrimaryBusinessAddressRecord != null)
			{
				String latitudeString = locationPrimaryBusinessAddressRecord
					.getString(LocationPaths._Root_LocationData_Address._GeographicPoint_Latitude);
				String longitudeString = locationPrimaryBusinessAddressRecord
					.getString(LocationPaths._Root_LocationData_Address._GeographicPoint_Longitude);

				if (latitudeString != null && longitudeString != null)
				{
					double latitude = Double.parseDouble(latitudeString);

					double longitude = Double.parseDouble(longitudeString);

					double altitude = Double.parseDouble(longitudeString);

					String measurementSystem = locationRecord.getString(
						LocationPaths._Root_Location._LocationInformation_MeasurementSystem);

					if (measurementSystem == null)
					{
						measurementSystem = HtzConstants.MEASUREMENT_TYPE_US;
					}

					Adaptation airportRecord = HtzLocationUtilities
						.closestAirportToLatitudeLongitude(
							latitude,
							longitude,
							altitude,
							measurementSystem,
							true);

					if (airportRecord != null)
					{
						locationOag = airportRecord
							.getString(CommonReferencePaths._Root_Airport._IataFAA);
					}
				}
			}
		}

		Adaptation brandRecord = AdaptationUtil
			.followFK(locationRecord, LocationPaths._Root_Location._RentalCarBrand);

		if (brandRecord != null)
		{
			locationBrandCode = brandRecord.getString(
				CommonReferencePaths._Root_RentalCarBrand._IntelligentLocationIdBrandCode);
		}

		Adaptation ownershipRecord = AdaptationUtil
			.followFK(locationRecord, LocationPaths._Root_Location._OperationsOwnershipType);

		if (ownershipRecord != null)
		{
			locationOwnershipType = ownershipRecord.getString(
				LocationReferencePaths._Root_OperationsOwnershipType._IntelligentLocationIdOwnershipCode);
		}

		locationNumber = locationRecord.getString(LocationPaths._Root_Location._LocationNumber);
		originalLocationNumber = locationNumber;

		//For Debugging Purposes
		if (locationOag == null)
		{
			locationOag = "???";
		}

		if (locationOwnershipType == null)
		{
			locationOwnershipType = "?";
		}
		if (locationBrandCode == null)
		{
			locationBrandCode = "?";
		}
		if (locationNumber == null)
		{
			locationNumber = "??";
		}

		//	HtzUtilities.writeToFileInFileSystem(
		//	"*** Values Set: locationOAG: " + locationOag + "  " + "LocationOwnershipType: " + " "
		//		+ locationOwnershipType + "  Brand: " + " " + locationBrandCode
		//		+ "  LocationNumber: " + " " + locationNumber);

		//Cannot generate Intelligent Location Id is any of the critical attributes are missing
		if (locationOag == null || locationBrandCode == null || locationOwnershipType == null
			|| locationNumber == null)
		{
			return;
		}

		AdaptationTable locationLocationTypeGroupRelationshipTable = locationRecord.getContainer()
			.getTable(
				LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship
					.getPathInSchema());

		AdaptationTable locationIntelligentLocationIdRelationshipTable = locationRecord
			.getContainer()
			.getTable(
				LocationPaths._Root_LocationData_LocationIntelligentLocationIdRelationship
					.getPathInSchema());

		//Retrieve all the Location Location Type Group Relationship Relationship Records
		RequestResult requestResultLocationType = locationLocationTypeGroupRelationshipTable
			.createRequestResult(
				LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship._Location
					.format() + "='" + locationId + "' and"
					+ LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship._IsRecordActive
						.format()
					+ "=true");

		RequestResult requestResultIntelligentLocatonId = null;

		ArrayList<String> locationIntelligentLocationIds = new ArrayList<String>();

		boolean primarySet = false;

		for (int i = 0; i < requestResultLocationType.getSize(); i++)
		{
			boolean isPrimary = false;
			locationNumber = originalLocationNumber;

			//For each Location Type Record, create the Intelligent ID and determine if it already exists for the Location
			Adaptation requestResultLocationTypeRecord = requestResultLocationType.nextAdaptation();

			locationFunctionType = requestResultLocationTypeRecord.getString(
				LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship.__subFunctionType);

			if (!primarySet)
			{
				if (RentalTypes.AIRPORT.equals(
					requestResultLocationTypeRecord.getString(
						LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship._RentalType)))
				{
					primarySet = true;
					isPrimary = true;
					//If this location is On Airport, always use 01 as the LocationNumber
					if (isOnAirport && isPrimary)
					{
						locationNumber = "01";
					}
				}
				else if (RentalTypes.NON_AIRPORT.equals(
					requestResultLocationTypeRecord.getString(
						LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship._RentalType)))
				{
					if (LocationConstants.LOCATION_SUBTYPE_LOCAL.equals(
						requestResultLocationTypeRecord.getString(
							LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship.__subFunctionType)))
					{
						primarySet = true;
						isPrimary = true;
					}
				}
			}

			if (locationFunctionType == null)
			{
				continue;
			}

			//		HtzUtilities.writeToFileInFileSystem(
			//		"*** Building ID: locationOAG: " + locationOag + "  " + "LocationOwnershipType: "
			//			+ " " + locationOwnershipType + "  Brand: " + " " + locationBrandCode
			//			+ "  LocationNumber: " + " " + locationNumber);

			String intelligentLocationId = locationOag + locationBrandCode + "-"
				+ locationOwnershipType + locationFunctionType + locationNumber;

			locationIntelligentLocationIds.add(intelligentLocationId);

			requestResultIntelligentLocatonId = locationIntelligentLocationIdRelationshipTable
				.createRequestResult(
					LocationPaths._Root_LocationData_LocationIntelligentLocationIdRelationship._Location
						.format()
						+ "='" + locationId + "' and "
						+ LocationPaths._Root_LocationData_LocationIntelligentLocationIdRelationship.__intelligentLocationId
							.format()
						+ "='" + intelligentLocationId + "' and "
						+ LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship._IsRecordActive
							.format()
						+ "=true");

			//If the Intelligent Location Id already exists, check the isPrimary on the record
			if (!requestResultIntelligentLocatonId.isEmpty())
			{
				Adaptation intelligentLocatonIdRecord = requestResultIntelligentLocatonId
					.nextAdaptation();
				if ((intelligentLocatonIdRecord.get_boolean(
					LocationPaths._Root_LocationData_LocationIntelligentLocationIdRelationship._IsPrimary)
					&& !isPrimary)
					|| (!intelligentLocatonIdRecord.get_boolean(
						LocationPaths._Root_LocationData_LocationIntelligentLocationIdRelationship._IsPrimary)
						&& isPrimary))
				//Update the isPrimary boolean
				{
					Map<Path, Object> pathValueMap = new HashMap<Path, Object>();

					pathValueMap.put(
						LocationPaths._Root_LocationData_LocationIntelligentLocationIdRelationship._IsPrimary,
						isPrimary);

					if (procedureContext != null)
					{
						ModifyValuesProcedure
							.execute(procedureContext, intelligentLocatonIdRecord, pathValueMap);
					}
					else
					{
						ModifyValuesProcedure.execute(
							intelligentLocatonIdRecord,
							pathValueMap,
							context.getSession(),
							true,
							false);
					}
				}
				continue;
			}

			//Add the Intelligent Location Id to the Location

			//	HtzUtilities
			//.writeToFileInFileSystem("*** Adding Id to locaton: ILI: " + intelligentLocationId);

			Map<Path, Object> pathValueMap = new HashMap<Path, Object>();

			pathValueMap.put(
				LocationPaths._Root_LocationData_LocationIntelligentLocationIdRelationship._Location,
				locationId);
			pathValueMap.put(
				LocationPaths._Root_LocationData_LocationIntelligentLocationIdRelationship.__intelligentLocationId,
				intelligentLocationId);
			pathValueMap.put(
				LocationPaths._Root_LocationData_LocationIntelligentLocationIdRelationship._IsPrimary,
				isPrimary);

			if (procedureContext != null)
			{
				CreateRecordProcedure.execute(
					procedureContext,
					locationIntelligentLocationIdRelationshipTable,
					pathValueMap,
					true,
					false);
			}
			else
			{
				CreateRecordProcedure.execute(
					locationIntelligentLocationIdRelationshipTable,
					pathValueMap,
					context.getSession());
			}

		}

		//Delete all of the Non-Applicable UNLs from the Location by reading each and deleting it if not in the list of valid UNLs in the table
		requestResultIntelligentLocatonId = locationIntelligentLocationIdRelationshipTable
			.createRequestResult(
				LocationPaths._Root_LocationData_LocationIntelligentLocationIdRelationship._Location
					.format() + "='" + locationId + "' and "
					+ LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship._IsRecordActive
						.format()
					+ "=true");

		for (int i = 0; i < requestResultIntelligentLocatonId.getSize(); i++)
		{
			Adaptation intelligentlLocationIdRecord = requestResultIntelligentLocatonId
				.nextAdaptation();

			if (!locationIntelligentLocationIds.contains(
				intelligentlLocationIdRecord.getString(
					LocationPaths._Root_LocationData_LocationIntelligentLocationIdRelationship.__intelligentLocationId)))
			{
				if (procedureContext != null)
				{
					DeleteRecordProcedure.execute(procedureContext, intelligentlLocationIdRecord);
				}
				else
				{
					DeleteRecordProcedure
						.execute(intelligentlLocationIdRecord, context.getSession());
				}
			}
		}

		if (requestResultLocationType != null)
		{
			requestResultLocationType.close();
		}

		if (requestResultIntelligentLocatonId != null)
		{
			requestResultIntelligentLocatonId.close();
		}
	}

	public static Adaptation getCountryMinimumMaximumAgeRow(Adaptation locationRecord)
		throws OperationException
	{

		if (locationRecord == null)
		{
			return null;
		}

		AdaptationTable countryMinimumMaximumTable = RepositoryUtils.getTable(
			RepositoryUtils.getDataSet(
				HtzConstants.LOCATION_DATA_SPACE,
				HtzConstants.LOCATION_REFERENCE_DATA_SET),
			LocationReferencePaths._Root_CountryMinimumMaximumAge.getPathInSchema().format());

		String locationCountryId = locationRecord
			.getString(LocationPaths._Root_Location._LocationInformation__locationCountry)
			.toString();

		//Retrieve the CountryMinimumMaximumAge record for the Location Country
		RequestResult requestResult = countryMinimumMaximumTable.createRequestResult(
			LocationReferencePaths._Root_CountryMinimumMaximumAge._Country.format() + "='"
				+ locationCountryId + "' and "
				+ LocationReferencePaths._Root_CountryMinimumMaximumAge._IsRecordActive.format()
				+ "=true");

		return requestResult.nextAdaptation();
	}

	public static void writeToUpdateNotificationTable(
		Adaptation locationRecord,
		String updateType,
		String tableUpdated,
		ProcedureContext context)
		throws OperationException
	{
		if (locationRecord == null)
		{
			return;
		}

		String locationId = locationRecord.get(LocationPaths._Root_Location._Id).toString();

		AdaptationTable locationUpdateNotificationTable = locationRecord.getContainer().getTable(
			LocationPaths._Root_LocationData_LocationUpdateNotification.getPathInSchema());

		Map<Path, Object> pathValueMap = new HashMap<Path, Object>();

		String tableUpdatedLocal = tableUpdated.substring(tableUpdated.indexOf("[") + 1);

		if (tableUpdatedLocal.indexOf("[") > 0)
		{
			tableUpdatedLocal = tableUpdatedLocal.substring(0, tableUpdatedLocal.indexOf("["));
		}

		pathValueMap
			.put(LocationPaths._Root_LocationData_LocationUpdateNotification._Location, locationId);
		pathValueMap.put(
			LocationPaths._Root_LocationData_LocationUpdateNotification._UpdateType,
			updateType);
		pathValueMap.put(
			LocationPaths._Root_LocationData_LocationUpdateNotification._TableUpdated,
			tableUpdatedLocal);
		pathValueMap.put(
			LocationPaths._Root_LocationData_LocationUpdateNotification._CreatedDateTime,
			new Date());

		CreateRecordProcedure.execute(context, locationUpdateNotificationTable, pathValueMap);

		return;
	}
}
