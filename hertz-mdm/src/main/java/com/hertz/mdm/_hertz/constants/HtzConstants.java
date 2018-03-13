package com.hertz.mdm._hertz.constants;

import com.orchestranetworks.ps.project.constants.CommonProjectConstants;

public class HtzConstants extends CommonProjectConstants
{
	//EBX Directory
	public static final String DIRECTORY_DATA_SPACE = "ebx-directory";
	public static final String DIRECTORY_DATA_SET = "ebx-directory";

	//Hertz Environment Path to Hertz.Properties File
	public static final String EBX_PROPERTIES_FILE_LOCAL = "../hertz-mdm/properties/hertz.properties";
	public static final String EBX_PROPERTIES_FILE_DEFAULT = "/usr/share/ebx/ebx_home/hertz.properties";
	public static final String EBX_PROPERTIES_FILE_PROPERTY = "hertz.properties";

	//Hertz EBX Environments
	public static final String EBX_PROPERTY_ENV_LOCAL = "Xlocal";
	public static final String EBX_PROPERTY_ENV_DEVELOPMENT = "development";
	public static final String EBX_PROPERTY_ENV_QA = "qa";
	public static final String EBX_PROPERTY_ENV_UAT = "uat";
	public static final String EBX_PROPERTY_ENV_DEMO = "demo";
	public static final String EBX_PROPERTY_ENV_PRODUCTION = "production";

	//Admin Domain
	public static final String ADMIN_DATA_SPACE = "AdminDataSpace";
	public static final String ADMIN_DATA_SET = "AdminDataSet";

	//Common Domain
	public static final String COMMON_REFERENCE_DATA_SPACE = "CommonReferenceMasterDataSpace";
	public static final String COMMON_REFERENCE_DATA_SET = "CommonReferenceDataSet";

	//Location Domain
	public static final String LOCATION_DATA_SPACE = "LocationMasterDataSpace";
	public static final String LOCATION_DATA_SET = "LocationDataSet";
	public static final String LOCATION_REFERENCE_DATA_SET = "LocationReferenceDataSet";

	//Vehicle Domain
	public static final String VEHICLE_DATA_SPACE = "VehicleMasterDataSpace";
	public static final String VEHICLE_DATA_SET = "VehicleDataSet";
	public static final String VEHICLE_REFERENCE_DATA_SET = "VehicleReferenceDataSet";

	//Business Party Domain
	public static final String BUSINESS_PARTY_DATA_SPACE = "BusinessPartyMasterDataSpace";
	public static final String BUSINESS_PARTY_DATA_SET = "BusinessPartyDataSet";
	public static final String BUSINESS_PARTY_REFERENCE_DATA_SET = "BusinessPartyReferenceDataSet";

	//HERTZ EBX Domains
	public static final String LOCATION_DOMAIN = "LOCN";
	public static final String VEHICLE_DOMAIN = "VHCL";
	public static final String COMMON_DOMAIN = "COMM";
	public static final String BUSINESS_PARTY_DOMAIN = "BPTY";
	public static final String ADMIN_DOMAIN = "ADMIN";

	//TODO - Need to replace with whatever connection is required for MS
	public static final String HERTZ_MDM_PROPERTY_FILE_SYSTEM_PROPERTY = "";
	public static final String RABBIT_MQ_HOST_PROPERTY = "";

	// CUR Operation Types
	public static final String CREATE = "create";
	public static final String UPDATE = "update";
	public static final String REMOVE = "remove";

	//Schema Definition
	public final static String SCHEMA_DEFINITION = "http://schemas.viacom.com/what/is/the/path#";

	//MQ Message Integration Separators
	public static final String TABLE_ADAPTATION_SEPARATOR = ":";
	public static final String ADAPTATION_SEPARATOR = "#";
	public static final String TABLE_ADAPTATION_STARTS_WITH = "T[";
	public static final String TABLE_ADAPTATION_ENDS_WITH = "]";
	public static final String CREATE_OCCURRENCES_SEPARATOR = "C{";
	public static final String UPDATE_OCCURRENCES_SEPARATOR = "U{";
	public static final String DELETE_OCCURRENCES_SEPARATOR = "D{";

	//Standard Roles
	public static final String ROLE_TEST_USER = "Test User";
	public static final String ROLE_GLOBAL = "Global";
	public static final String ROLE_SERVICE_USER = "Service User";
	public static final String ROLE_INTERNAL_USER_SUFFIX = "Internal";
	public static final String ROLE_DATA_GOVERNANCE_SUFFIX = "Data Governance";
	public static final String ROLE_SUPER_USER_SUFFIX = "Super User";
	public static final String ROLE_SUPER_APPROVER_SUFFIX = "Super Approver";
	public static final String ROLE_REFERENCE_DATA_ENTRY_SUFFIX = "Reference Data Entry";
	public static final String ROLE_REFERENCE_DATA_APPROVER_SUFFIX = "Reference Data Approver";
	public static final String ROLE_EXTERNAL = "External";

	//Common Countries
	public static final String COUNTRY_ALPHA2_US = "US";
	public static final String COUNTRY_CODE_US = "840";
	public static final String COUNTRY_ALPHA2_CA = "CA";
	public static final String COUNTRY_CODE_CANADA = "124";

	//Common States/Provinces
	public static final String STATE_PROVINCE_CODE_CALIFORNIA = "US-CA";

	//Measurement Types
	public static final String MEASUREMENT_TYPE_US = "US";
	public static final String MEASUREMENT_TYPE_METRIC = "METRIC";

	//Languages
	public static final String LANGUAGE_ENGLISH = "eng";
	public static final String LANGUAGE_FRENCH = "fre";

	//EBX Date/Time Formats
	public static final String EBX_DATE_FORMAT = "yyyy-MM-dd";
	public static final String EBX_TIME_FORMAT = "HH:mm:ss";
	public static final String EBX_DATE_TIME_FORMAT = EBX_DATE_FORMAT + "'T'" + EBX_TIME_FORMAT;

	//EBX Roles
	public static final String EBX_ROLE_PERMISSIONS_USER = "Permissions User";
	public static final String ROLE_TECH_ADMIN = "Tech Admin";

	//Permissions User
	public static final String PERMISSIONS_USER_UPDATE_MASTER_DATA = "UpdateMasterData";

	//Hertz From Email Address
	public static final String HERTZ_FROM_EMAIL_ADDRESS = "MasterData-Notification-DoNotReply@hertz.com";

	//
}
