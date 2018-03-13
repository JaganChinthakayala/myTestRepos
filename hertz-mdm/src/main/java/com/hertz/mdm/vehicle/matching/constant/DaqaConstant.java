package com.hertz.mdm.vehicle.matching.constant;

/**
 * @author noiritabera
 *
 */
public class DaqaConstant
{
	public static final String MATCH_CONFIG_DATASPACE = "ebx-addon-daqa";
	public static final String MATCH_CONFIG_DATASET = "ebx-addon-daqa-configuration-v2";
	public static final String MATCH_CONFIG_TABLE = "/root/DataQualityConfiguration/MatchingPolicy/MatchingPolicy";
	public static final String PROCESS_POLICY_TABLE = "/root/DataQualityConfiguration/ProcessPolicyGroup/ProcessPolicy";
	public static final String PATH_ON_DQ_PROCESS = "./onDQProcess";
	public static final String PATH_ON_CREATION = "./onCreation";
	public static final String PATH_ON_MODIFICATION = "./onModification";
	public static final String PATH_LITERAL_SCORE = "./literalScore";
	public static final String PATH_WORKFLOW_CONTEXT = "./workflowContext";
	public static final String PATH_MATCHING_POLICY_TABLE = "./FKTableConfiguration";
	public static final String ENUM_LITERAL_SCORE = "Exact";
	public static final String PATH_DAQAMETADATA_STATE = "./DaqaMetaData/State";
	public static final String PATH_VEHICLE_SOURCE_SYSTEM = "./sourceSystem";

	public static String STATUS_GOLDEN = "Golden";
	public static String STATUS_MERGED = "Merged";
	public static String STATUS_UNMATCHED = "Unmatched";
	public static String STATUS_PIVOT = "Pivot";
	public static String STATUS_SUSPECT = "Suspect";
	public static String SOURCE_MDM = "MDM";
	public static String SOURCE_MANUAL = "MANUAL";
	public static String PARTY_TYPE_PERSON = "person";
	public static String PARTY_TYPE_COMPANY = "company";
}
