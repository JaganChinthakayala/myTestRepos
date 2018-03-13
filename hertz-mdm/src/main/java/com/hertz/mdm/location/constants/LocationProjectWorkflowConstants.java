/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.constants;

import com.hertz.mdm._hertz.constants.HtzProjectWorkflowConstants;

/**
 */
public class LocationProjectWorkflowConstants extends HtzProjectWorkflowConstants
{
	public static final String PERMISSIONS_USER_LOCATION = "InitiateProductProject";

	public static final String WF_KEY_OPEN_LOCATION_PROJECT_MASTER = "LOCN_OpenLocation";
	public static final String WF_KEY_CLOSE_LOCATION_PROJECT_MASTER = "LOCN_CloseLocation";
	public static final String WF_KEY_MOVE_LOCATION_PROJECT_MASTER = "LOCN_MoveLocation";

	//Open Location Workflows
	public static final String WF_KEY_OBTAIN_BUSINESS_LICENSE = "LOCN_Execute_ObtainBusinessLicense";
	public static final String WF_KEY_HANDLE_TRASH_UTILITIES = "LOCN_ExecuteHandleTrashAndUtilities";
	public static final String WF_KEY_ENTER_TAX_DATA = "LOCN_Execute_EnterTaxData";
	public static final String WF_KEY_PROPERTY_AND_CONCESSIONS = "LOCN_Execute_PropertyAndConcessions";
	public static final String WF_KEY_COMPLETE_ITQUESTIONNAIRE_AND_FORWARD_TO_ITPMO = "LOCN_Execute_ITQuestionnaireAndForwardToITPMO";
	public static final String WF_KEY_EXECUTE_REVENUE_MANAGER = "LOCN_Execute_RevenueManager";
	public static final String WF_KEY_EXECUTE_UPDATE_VERISAE = "LOCN_Execute_UpdateVerisae";
	public static final String WF_KEY_EXECUTE_UPDATE_OFFSELL = "LOCN_Execute_UpdateOffsell";

	//Open Location Complete Notification Workflows
	public static final String WF_KEY_ADD_FIXED_ASSETS_TO_ORACLE = "LOCN_Execute_AddFixedAssetsToOracle";
	public static final String WF_KEY_BANK_RECONCILIATION = "LOCN_Execute_BankReconciliation";
	public static final String WF_KEY_UPDATE_CARS_ADMIN = "LOCN_Execute_UpdateCarsAdmin";
	public static final String WF_KEY_UPDATE_DASH = "LOCN_Execute_LoadDash";
	public static final String WF_KEY_UPDATE_HR_ORACLE = "LOCN_Execute_LoadHROracle";
	public static final String WF_KEY_TRANSLATION_DATA_LOCATION = "LOCN_Execute_Translate_Location_Data";
	public static final String WF_KEY_MFD_HELP_DESK = "LOCN_Execute_MFDHelpDesk";
	public static final String WF_KEY_HANDLE_FUEL_CARDS = "LOCN_ExecuteHandleFuelCards";
	public static final String WF_KEY_UPDATE_WEBSITES = "LOCN_Execute_Update_Websites";
	public static final String WF_KEY_UPDATE_RESERVATION_DELIVERY = "LOCN_Execute_UpdateReservationDelivery";
	public static final String WF_KEY_UPDATE_TREASURY = "LOCN_Execute_UpdateTreasury";
	public static final String WF_KEY_UPDATE_CARRENT = "LOCN_Execute_UpdateCarRent";
	public static final String WF_KEY_UPDATE_CARRENT_VAW = "LOCN_Execute_UpdateCarRentVAW";
	public static final String WF_KEY_UPDATE_FOS = "LOCN_Execute_UpdateFOS";
	public static final String WF_KEY_UPDATE_RESMAINT = "LOCN_Execute_UpdateResMaint";
	public static final String WF_KEY_SETUP_CANVASSING_AND_RMS = "LOCN_SetupCanvassingRMS";

	//Close Location Complete Notification Workflows
	public static final String WF_KEY_IT_QUESTIONNAIRE_AND_PRECLOSE_CHECKLIST = "LOCN_Execute_ITQuestionnaire_PreCloseCheckList";
	public static final String WF_KEY_UPDATE_RESDUMP = "LOCN_Execute_UpdateResDump";
	public static final String WF_KEY_RESFIX = "LOCN_Execute_ResFix";
	public static final String WF_KEY_POSTCLOSE_CHECKLIST = "LOCN_Execute_Post_Close_Checklist";
	public static final String WF_KEY_UPDATE_RENT_WORKS = "LOCN_Execute_UpdateRentworks";
	public static final String WF_KEY_UPDATE_HLES = "LOCN_Execute_UpdateHLES";
	public static final String WF_KEY_UPDATE_RMS = "LOCN_Execute_UpdateRMS";

	//Close Location Complete Notification Workflows
	public static final String WF_KEY_SEND_ESCALATION_EMAILS = "LOCN_SendEscalationEmails";

	//Move Location Complete Notification Workflows

	//Location Approval Roles
	public static final String WF_REGIONAL_VP_APPROVAL_ROLE = "RegionalVPApproval";
	public static final String WF_REGIONAL_FINANCE_APPROVAL_ROLE = "RegionFinanceApproval";
	public static final String WF_REGIONAL_REAL_ESTATE_APPROVAL_ROLE = "RealEstateApproval";
	public static final String WF_REGIONAL_CONCESSIONS_ROLE = "ConcessionsApproval";
	public static final String WF_REGIONAL_FACILITIES_CONSTRUCTION_ROLE = "FacilitiesConstructionApproval";
	public static final String WF_REGIONAL_ENVIRONMENTAL_AFFAIRS_APPROVAL_ROLE = "EnvironmentalAffairsApproval";

	//Project Message Variables
	public static final String WF_MESSAGE_VARIABLE_LOCATION_ID = "LOCATIONID";
	public static final String WF_MESSAGE_VARIABLE_LOCATION = "LOCATION";
	public static final String WF_MESSAGE_VARIABLE_LOCATION_NAME = "LOCATIONNAME";
	public static final String WF_MESSAGE_VARIABLE_LOCATION_ADDRESS = "LOCATIONADDRESS";
	public static final String WF_MESSAGE_VARIABLE_AREA = "AREA";

	private LocationProjectWorkflowConstants()
	{
		// do nothing
	}
}
