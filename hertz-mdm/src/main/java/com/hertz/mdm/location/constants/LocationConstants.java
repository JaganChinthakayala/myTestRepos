package com.hertz.mdm.location.constants;

import java.util.ArrayList;
import java.util.Arrays;

import com.hertz.mdm._hertz.enums.Domains;
import com.hertz.mdm.location.enums.LocationProjectStatuses;

public class LocationConstants
{
	public static final String LOCATION_DATA_SPACE = "LocationMasterDataSpace";
	public static final String LOCATION_DATA_SET = "LocationDataSet";
	public static final String LOCATION_REFERENCE_DATA_SET = "LocationReferenceDataSet";

	//Location To Location Relationship Types
	public static final String LOCATION_RELATIONSHIP_TYPE_SERVEDBY = "SERVEDBY";
	public static final String LOCATION_RELATIONSHIP_TYPE_SATELLITE = "SATELLITE";

	//Airport Types
	public static final String LOCATION_TYPE_GROUP_ON_AIRPORT = "ONAIRPORT";
	public static final String LOCATION_TYPE_GROUP_NON_AIRPORT = "NONAIRPORT";
	public static final String LOCATION_TYPE_GROUP_PARENT_AIRPORT = "AIRPORT";

	//Internal Operation Types
	public static final String AIRPORT_OPERATIONS = "AIRPORT_OPERATIONS";
	public static final String LOCAL_OPERATIONS = "LOCAL_OPERATIONS";
	public static final String REMARKETING_OPERATIONS = "REMARKETING_OPERATIONS";

	//LocationType SubTypes
	public static final String LOCATION_SUBTYPE_AIRPORT = "AP";
	public static final String LOCATION_SUBTYPE_LOCAL = "LC";
	public static final String LOCATION_SUBTYPE_RAIL = "RA";

	//Non-Airport Types
	public static final String LOCATION_NONAIRPORT_RAIL_STATION = "Rail Station";

	//Maximum Distances for Satellite/ServedBy Relationships
	public static final double LOCATION_LOCATION_RELATIONSHIP_US_DISTANCE_LIMIT = 100;
	public static final double LOCATION_LOCATION_RELATIONSHIP_METRIC_DISTANCE_LIMIT = 220;

	public static final String HIERARCHY_NODE_TYPE_HIERARCHY = "HIERARCHY";

	public static final String LOCATION_ADDRESS_TYPE_BUSINESS = "BUSINESS";

	public static final String LOCATION_SERVICE_DAYS_HOURS_OF_OPERATION = "HOURS_OF_OPERATION";

	//Area Types
	public static final String OPERATION_AREA_TYPE_AREA = "AREA";
	public static final String OPERATION_AREA_TYPE_CMS = "CMS_AREA";
	public static final String OPERATION_AREA_TYPE_FLEET = "FLEET_AREA";
	public static final String OPERATION_AREA_TYPE_RESERVATION = "RESERVATION_AREA";

	//Operation Ownership Types
	public static final String OPERATION_OWNERSHIP_TYPE_CORPORATE = "CORPORATE";
	public static final String OPERATION_OWNERSHIP_TYPE_AGENCY = "AGENCY";
	public static final String OPERATION_OWNERSHIP_TYPE_LICENSEE_FRANCHISEE = "LICENSEE_FRANCHISEE";

	//Location Approval Roles
	public static final String APPROVAL_ROLE_REGIONAL_VICE_PRESIDENT = "RegionalVPApproval";
	public static final String APPROVAL_ROLE_REGIONAL_VICE_FINANCE = "RegionalFinanceApproval";
	public static final String APPROVAL_ROLE_CONCESSIONS = "ConcessionsApproval";
	public static final String APPROVAL_ROLE_FACILITIES_CONSTRUCTION = "FacilitiesConstructionApproval";
	public static final String APPROVAL_ROLE_REAL_ESTATE = "RealEstateApproval";
	public static final String APPROVAL_ROLE_ENVIRONMENTAL_AFFAIRS = "EnvironmentalAffairsApproval";
	public static final String APPROVAL_ROLE_CAR_SALES = "CarSalesApproval";

	//Location Roles
	public static final String ROLE_AREA_MANAGER = Domains.LOCATION + " - Area Manager";
	public static final String ROLE_REGIONAL_VICE_PRESIDENT = Domains.LOCATION + " - Regional VP";
	public static final String ROLE_REGIONAL_FINANCE = Domains.LOCATION + " - Regional Finance";
	public static final String ROLE_REAL_ESTATE = Domains.LOCATION + " - Real Estate";
	public static final String ROLE_FACILITIES_CONSTRUCTION = Domains.LOCATION
		+ " - Facilities and Construction";
	public static final String ROLE_CONCESSIONS = Domains.LOCATION + " - Concessions";
	public static final String ROLE_ENVIRONMENTAL_AFFAIRS = Domains.LOCATION
		+ " - Environmental Affairs";
	public static final String[] NEW_LOCATION_APPROVER_ROLES = { ROLE_REGIONAL_VICE_PRESIDENT,
			ROLE_REGIONAL_FINANCE, ROLE_REAL_ESTATE, ROLE_FACILITIES_CONSTRUCTION };
	public static final ArrayList<String> OPEN_LOCATION_REGIONAL_APPROVER_ROLES = new ArrayList<String>(
		Arrays.asList(
			ROLE_REGIONAL_VICE_PRESIDENT,
			ROLE_REGIONAL_FINANCE,
			ROLE_REAL_ESTATE,
			ROLE_FACILITIES_CONSTRUCTION,
			ROLE_ENVIRONMENTAL_AFFAIRS));

	public static final String BUSINESS_LICENSE_REQUESTOR_SUFFIX = Domains.LOCATION
		+ " - Business License";

	public static final String PERMISSIONS_USER_UPDATE_MASTER_DATA = "UpdateMasterData";

	public static final String DIRECTORY_DATA_SPACE = "ebx-directory";
	public static final String DIRECTORY_DATA_SET = "ebx-directory";

	//Location Contact Types
	public static final String CONTACT_TYPE_REGIONAL_VICE_PRESIDENT = "REGIONAL_VP";
	public static final String CONTACT_TYPE_REGIONAL_FINANCE = "REGIONAL_FINANCE";
	public static final String CONTACT_TYPE_REGIONAL_BRANCH_MANAGER = "BRANCH_MANAGER";

	//Escalation Email Notification Variables
	public static final String WF_MESSAGE_VARIABLE_TASK_START_DATE = "TASKSTARTDATE";
	public static final String WF_MESSAGE_VARIABLE_OVERDUE_PROJECT_TYPE = "OVERDUEPROJECTTYPE";
	public static final String WF_MESSAGE_VARIABLE_OVERDUE_PROJECT_NAME = "OVERDUEPROJECTNAME";
	public static final String WF_MESSAGE_VARIABLE_DATASPACE = "DATASPACE";
	public static final String WF_MESSAGE_VARIABLE_USER_TASK_NAME = "USERTASKNAME";
	public static final String WF_MESSAGE_VARIABLE_OFFERED_TO_ROLE = "OFFEREDTOROLE";
	public static final String WF_MESSAGE_VARIABLE_ALLOCATED_USER = "ALLOCATEDUSER";

	//Location Minimum and Maximum Rental Age
	public static final String LOCATION_DEFAULT_MINIMUM_RENTAL_AGE = "25";
	public static final String LOCATION_DEFAULT_MAXIMUM_RENTAL_AGE = null;

	public static Boolean isInProgess(String projectType)
	{
		return projectType.equals(LocationProjectStatuses.LOCATION_PROJECT_STATUS_IN_PROGRESS);
	}
}
