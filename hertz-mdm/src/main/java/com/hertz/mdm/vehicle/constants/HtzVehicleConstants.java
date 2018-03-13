package com.hertz.mdm.vehicle.constants;

public class HtzVehicleConstants
{
	public static final String VEHICLE_DATA_SPACE = "VehicleMasterDataSpace";
	public static final String VEHICLE_DATA_SET = "VehicleDataSet";
	public static final String VEHICLE_REFERENCE_DATA_SET = "VehicleReferenceDataSet";

	public static final String ADMIN_DATA_SPACE = "AdminDataSpace";
	public static final String ADMIN_DATA_SET = "AdminDataSet";

	public static final String PERMISSIONS_USER_UPDATE_MASTER_DATA = "UpdateMasterData";

	public static final String DIRECTORY_DATA_SPACE = "ebx-directory";
	public static final String DIRECTORY_DATA_SET = "ebx-directory";

	public static final String VEHICLE_STYLE_SELECTION_METHOD_BY_MODEL = "By Make/Model";
	public static final String VEHICLE_STYLE_SELECTION_METHOD_BY_STYLE_NAMES = "By Style Names";
	public static final String VEHICLE_STYLE_SELECTION_METHOD_BY_SPECIFIC_STYLES = "By Specific Styles";

	public static Boolean isInProgess(String projectType)
	{
		return null;
		//return projectType.equals(VEHICLE_PROJECT_PROJECT_STATUS);
	}
}
