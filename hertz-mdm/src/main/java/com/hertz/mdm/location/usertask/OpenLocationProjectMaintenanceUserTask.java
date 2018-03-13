/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.usertask;

import java.math.BigDecimal;

import com.hertz.mdm._hertz.constants.HtzConstants;
import com.hertz.mdm._hertz.constants.HtzWorkflowConstants;
import com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.repository.RepositoryUtils;
import com.hertz.mdm.businessparty.constants.BusinessPartyConstants;
import com.hertz.mdm.businessparty.path.BusinessPartyPaths;
import com.hertz.mdm.common.util.HtzCommonUtilities;
import com.hertz.mdm.location.constants.LocationConstants;
import com.hertz.mdm.location.enums.ContactPirmarySecondary;
import com.hertz.mdm.location.enums.EMailPirmarySecondary;
import com.hertz.mdm.location.enums.PhonePirmarySecondary;
import com.hertz.mdm.location.path.LocationPaths;
import com.hertz.mdm.location.util.HtzLocationUtilities;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.RequestResult;
import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.workflow.UserTaskBeforeWorkItemCompletionContext;
/**
 */
public class OpenLocationProjectMaintenanceUserTask extends LocationProjectMaintenanceUserTask
{
	static boolean isAreaSet = false;
	static boolean isRegionalVPInContacts = false;
	static boolean isRegionalFinanceInContacts = false;

	@Override
	public void checkBeforeWorkItemCompletion(UserTaskBeforeWorkItemCompletionContext context)
	{
		if (isCompletionCriteriaIgnored())
		{
			return;
		}

		if (context.isAcceptAction())
		{
			RequestResult requestResult = null;

			Adaptation projectRecord = getProjectRecordFromUserTaskBeforeWorkItemCompletionContext(
				context);

			if (projectRecord == null)
			{
				return;
			}

			Adaptation locationRecord = AdaptationUtil
				.followFK(projectRecord, LocationPaths._Root_LocationProject._Location);

			if (locationRecord == null)
			{
				return;
			}

			//Location Project Record Exit Criteria
			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._OpenLocation_ProjectedOpenDate,
				true,
				true);

			if (projectRecord.get(LocationPaths._Root_LocationProject._ProjectOriginator) == null)
			{
				String currentUserName = context
					.getVariableString(HtzWorkflowConstants.DATA_CONTEXT_PARM_CURRENT_USER_ID);

				AdaptationTable businessPartyTable = RepositoryUtils.getTable(
					RepositoryUtils.getDataSet(
						HtzConstants.BUSINESS_PARTY_DATA_SPACE,
						HtzConstants.BUSINESS_PARTY_DATA_SET),
					BusinessPartyPaths._Root_BusinessParty.getPathInSchema().format());

				// Query the Business Party Table to get this User's User Id
				requestResult = businessPartyTable.createRequestResult(
					BusinessPartyPaths._Root_BusinessParty._EbxUser.format() + "='"
						+ currentUserName + "' and"
						+ BusinessPartyPaths._Root_BusinessParty._IsRecordActive.format()
						+ "=true");

				if (requestResult.isEmpty())
				{
					context.reportMessage(
						UserMessage
							.createError("A Project Originator could not be set for this User"));
				}
			}

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._Construction_IsConstructionRequired,
				true,
				true);

			if (projectRecord.get_boolean(
				LocationPaths._Root_LocationProject._Construction_IsConstructionRequired))
			{
				checkFieldRequired(
					context,
					projectRecord,
					LocationPaths._Root_LocationProject._Construction_EstimatedConstructionStartDate,
					true,
					true);

				checkFieldRequired(
					context,
					projectRecord,
					LocationPaths._Root_LocationProject._Construction_EstimatedPermanentPowerOnDate,
					true,
					true);

				checkFieldRequired(
					context,
					projectRecord,
					LocationPaths._Root_LocationProject._Construction_EstimatedPodConstructionFinishedDate,
					true,
					true);

				checkFieldRequired(
					context,
					projectRecord,
					LocationPaths._Root_LocationProject._Construction_HasDesks,
					true,
					true);

				checkFieldRequired(
					context,
					projectRecord,
					LocationPaths._Root_LocationProject._Construction_IsSiteCoLocated,
					true,
					true);

				checkFieldRequired(
					context,
					projectRecord,
					LocationPaths._Root_LocationProject._Construction_RequiresCarpetInstall,
					true,
					true);

				checkFieldRequired(
					context,
					projectRecord,
					LocationPaths._Root_LocationProject._Construction_RequiresOtherConstruction,
					true,
					true);

				checkFieldRequired(
					context,
					projectRecord,
					LocationPaths._Root_LocationProject._Construction_RequiresPainting,
					true,
					true);

				checkFieldRequired(
					context,
					projectRecord,
					LocationPaths._Root_LocationProject._Construction_RequiresTileLaid,
					true,
					true);
			}

			if (!isAreaSet)
			{
				if (projectRecord
					.get(LocationPaths._Root_LocationProject._OpenLocation_RequiresNewArea) == null)
				{
					checkFieldRequired(
						context,
						projectRecord,
						LocationPaths._Root_LocationProject._OpenLocation_RequiresNewArea,
						true,
						true);
				}
				else if (projectRecord
					.get_boolean(LocationPaths._Root_LocationProject._OpenLocation_RequiresNewArea)
					&& locationRecord
						.getString(LocationPaths._Root_Location._LocationInformation_Area) != null)

				{
					context.reportMessage(
						UserMessage.createError(
							"An Area cannot be specified on Location if the Location Project indicates a New Area is required"));
				}
				else if (!projectRecord
					.get_boolean(LocationPaths._Root_LocationProject._OpenLocation_RequiresNewArea)
					&& locationRecord
						.getString(LocationPaths._Root_Location._LocationInformation_Area) == null)
				{
					context.reportMessage(
						UserMessage.createError(
							"An Area must be specified on Location if the Location Project indicates a New Area is not required"));
				}
			}

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._OpenLocation_AcceptsReturns,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._OpenLocation_DoesRentals,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._OpenLocation_Fees_RefuelingPurchaseOptionAmount,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._EnvironmentalAffairs_VehiclesWashedOnsite,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._EnvironmentalAffairs_VehicleOilChangesOnSite,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._EnvironmentalAffairs_VehiclesRefueledOnSite,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._LE_UsesLECounterSystem,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._LE_OffersLEServices,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._LE_OffersPickupServiceCabReimbursement,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._LE_OffersInsuranceReplacementRentals,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._LE_DoesCorporateFreeSell,
				true,
				true);

			checkFieldRequired(
				context,
				projectRecord,
				LocationPaths._Root_LocationProject._LE_HonorsPromosCoupons,
				true,
				true);

			//Required for a Canadian Location and Only for a Canadian Location
			if (HtzConstants.COUNTRY_CODE_CANADA.equals(
				locationRecord
					.get(LocationPaths._Root_Location._LocationInformation__locationCountry)))
			{
				checkFieldRequired(
					context,
					projectRecord,
					LocationPaths._Root_LocationProject._OpenLocation_Fees_AcSurchargeFee,
					true,
					true);
			}
			else if (projectRecord
				.get(LocationPaths._Root_LocationProject._OpenLocation_Fees_AcSurchargeFee) != null)
			{
				context.reportMessage(
					UserMessage.createError(
						"'" + getFieldLabel(
							projectRecord,
							LocationPaths._Root_LocationProject._OpenLocation_Fees_AcSurchargeFee,
							context.getSession(),
							true) + "' is only valid for a Canadian Location."));
			}

			//Location Record Exit Criteria
			if (locationRecord
				.get(LocationPaths._Root_Location._LocationInformation_Region) == null)
			{
				checkFieldRequired(
					context,
					locationRecord,
					LocationPaths._Root_Location._LocationInformation_Region,
					true,
					true);
			}
			else if (locationRecord
				.get(LocationPaths._Root_Location._LocationInformation_Area) != null)
			{
				if (!HtzLocationUtilities.isRegionInTheLocationHierarchy(locationRecord))
				{
					context.reportMessage(
						UserMessage.createError("The Region is not in the Location Hierarchy."));
				}
			}

			String locationId = locationRecord.get(LocationPaths._Root_Location._Id).toString();

			Adaptation locationContactRecord = null;

			AdaptationTable contactTable = locationRecord.getContainer()
				.getTable(LocationPaths._Root_LocationData_Contact.getPathInSchema());

			//The selected Region must have a BusinessParty who has the Role of Regional VP and Regional Finance
			if (locationRecord
				.get(LocationPaths._Root_Location._LocationInformation_Region) != null)
			{
				AdaptationTable locationHierarchyBusinessPartyRelationshipTable = locationRecord
					.getContainer().getTable(
						LocationPaths._Root_HierarchyDefinitionData_LocationHierarchyBusinessPartyRelationship
							.getPathInSchema());

				String regionId = locationRecord
					.get(LocationPaths._Root_Location._LocationInformation_Region).toString();

				// Query the LocationHierarchy/Business Party Relationship table for the location Region
				requestResult = locationHierarchyBusinessPartyRelationshipTable.createRequestResult(
					LocationPaths._Root_HierarchyDefinitionData_LocationHierarchyBusinessPartyRelationship._LocationHierarchy
						.format() + "='" + regionId + "'");

				if (requestResult.isEmpty())
				{
					context.reportMessage(
						UserMessage.createError(
							"The Location Region has no Business Parties associated with it.'"));
				}
				else
				{
					AdaptationTable businessPartyRoleTable = RepositoryUtils.getTable(
						RepositoryUtils.getDataSet(
							HtzConstants.BUSINESS_PARTY_DATA_SPACE,
							HtzConstants.BUSINESS_PARTY_DATA_SET),
						BusinessPartyPaths._Root_BusinessPartyData_BusinessPartyRole
							.getPathInSchema().format());

					String regionalVPId = null;
					String regionalFinanceId = null;

					for (int i = 0; i < requestResult.getSize(); i++)
					{
						Adaptation locationHierarchyBusinessPartyRelationshipRecord = requestResult
							.nextAdaptation();

						if (locationHierarchyBusinessPartyRelationshipRecord.get(
							LocationPaths._Root_HierarchyDefinitionData_LocationHierarchyBusinessPartyRelationship._BusinessParty) == null)
						{
							continue;
						}

						String businessPartyId = locationHierarchyBusinessPartyRelationshipRecord
							.get(
								LocationPaths._Root_HierarchyDefinitionData_LocationHierarchyBusinessPartyRelationship._BusinessParty)
							.toString();

						// Query the Business Party Roles Table table to see if this Business Party has the input role
						RequestResult requestResultBusinessPartyRoles = businessPartyRoleTable
							.createRequestResult(
								BusinessPartyPaths._Root_BusinessPartyData_BusinessPartyRole._BusinessParty
									.format() + "='" + businessPartyId
									+ "' and "
									+ BusinessPartyPaths._Root_BusinessPartyData_BusinessPartyRole._BusinessPartyRoleType
										.format()
									+ "='" + LocationConstants.ROLE_REGIONAL_VICE_PRESIDENT
									+ "' and "
									+ BusinessPartyPaths._Root_BusinessPartyData_BusinessPartyRole._RoleCategory
										.format()
									+ "='" + BusinessPartyConstants.BUSINESS_PARTY_ROLE_CATEGORY_EBX
									+ "'");

						if (!requestResultBusinessPartyRoles.isEmpty())
						{
							regionalVPId = businessPartyId;
						}

						// Query the Business Party Roles Table table to see if this Business Party has the input role
						requestResultBusinessPartyRoles = businessPartyRoleTable
							.createRequestResult(
								BusinessPartyPaths._Root_BusinessPartyData_BusinessPartyRole._BusinessParty
									.format() + "='" + businessPartyId
									+ "' and "
									+ BusinessPartyPaths._Root_BusinessPartyData_BusinessPartyRole._BusinessPartyRoleType
										.format()
									+ "='" + LocationConstants.ROLE_REGIONAL_FINANCE + "' and "
									+ BusinessPartyPaths._Root_BusinessPartyData_BusinessPartyRole._RoleCategory
										.format()
									+ "='" + BusinessPartyConstants.BUSINESS_PARTY_ROLE_CATEGORY_EBX
									+ "'");

						if (!requestResultBusinessPartyRoles.isEmpty())
						{
							regionalFinanceId = businessPartyId;
						}
					}

					if (regionalVPId != null)
					{
						//Determine if the Regional VP has been added to the Location Contacts
						//Query the Contact table for the Regional VP Contact Type
						RequestResult contactRequestResult = contactTable.createRequestResult(
							LocationPaths._Root_LocationData_Contact._Location.format() + "='"
								+ locationId + "' and"
								+ LocationPaths._Root_LocationData_Contact._ContactType.format()
								+ "='" + LocationConstants.CONTACT_TYPE_REGIONAL_VICE_PRESIDENT
								+ "' and "
								+ LocationPaths._Root_LocationData_Contact._IsRecordActive.format()
								+ "=true");

						if (!contactRequestResult.isEmpty())
						{
							isRegionalVPInContacts = true;

							locationContactRecord = contactRequestResult.nextAdaptation();

							if (!regionalVPId.equals(
								locationContactRecord.getString(
									LocationPaths._Root_LocationData_Contact._BusinessParty)))
							{
								context.reportMessage(
									UserMessage.createError(
										"The Regional VP entered in the Location Contacts does not match the Regional VP on the Region selected for the Location."));
							}
						}
						else
						{
							isRegionalVPInContacts = false;
						}
					}
					else
					{
						context.reportMessage(
							UserMessage.createError(
								"A Regional VP has not been found for the Location Region or the individual does not have that role in MDM."));
					}

					if (regionalFinanceId != null)
					{
						//Determine if the Regional Finance has been added to the Location Contacts
						//Query the Contact table for the Regional VP Contact Type
						RequestResult contactRequestResult = contactTable.createRequestResult(
							LocationPaths._Root_LocationData_Contact._Location.format() + "='"
								+ locationId + "' and"
								+ LocationPaths._Root_LocationData_Contact._ContactType.format()
								+ "='" + LocationConstants.CONTACT_TYPE_REGIONAL_FINANCE + "' and "
								+ LocationPaths._Root_LocationData_Contact._IsRecordActive.format()
								+ "=true");

						if (!contactRequestResult.isEmpty())
						{
							isRegionalFinanceInContacts = true;

							locationContactRecord = contactRequestResult.nextAdaptation();

							if (!regionalVPId.equals(
								locationContactRecord.getString(
									LocationPaths._Root_LocationData_Contact._BusinessParty)))
							{
								context.reportMessage(
									UserMessage.createError(
										"The Regional Finance entered in the Location Contacts does not match the Regional VP on the Region selected for the Location."));
							}
						}
						else
						{
							isRegionalFinanceInContacts = false;
						}
					}
					else
					{
						context.reportMessage(
							UserMessage.createError(
								"A Regional Finance has not been found for the Location Region or the individual does not have that role in MDM."));
					}
				}
			}

			//The Location must have a Branch Manager Associated with it

			//Query the Contact table for the Branch Manager Contact Type
			RequestResult contactRequestResult = contactTable.createRequestResult(
				LocationPaths._Root_LocationData_Contact._Location.format() + "='" + locationId
					+ "' and" + LocationPaths._Root_LocationData_Contact._ContactType.format()
					+ "='" + LocationConstants.CONTACT_TYPE_REGIONAL_BRANCH_MANAGER + "' and "
					+ LocationPaths._Root_LocationData_Contact._IsRecordActive.format() + "=true");

			if (contactRequestResult.isEmpty())
			{
				context.reportMessage(
					UserMessage
						.createError("A Branch Manager has not been found for the Location."));
			}

			checkFieldRequired(
				context,
				locationRecord,
				LocationPaths._Root_Location._RentalCarBrand,
				true,
				true);

			checkFieldRequired(
				context,
				locationRecord,
				LocationPaths._Root_Location._OperationsOwnershipType,
				true,
				true);

			if (locationRecord.getString(
				LocationPaths._Root_Location._LocationInformation__locationStateProvince) != null)
			{
				checkFieldRequired(
					context,
					locationRecord,
					LocationPaths._Root_Location._LocationInformation_StateProvinceTimeZone,
					true,
					true);
			}
			else
			{
				checkFieldRequired(
					context,
					locationRecord,
					LocationPaths._Root_Location._LocationInformation_CountryTimeZone,
					true,
					true);
			}

			checkFieldRequired(
				context,
				locationRecord,
				LocationPaths._Root_Location._LocationInformation_MeasurementSystem,
				true,
				true);

			checkFieldRequired(
				context,
				locationRecord,
				LocationPaths._Root_Location._LocationInformation_Language,
				true,
				true);

			checkFieldRequired(
				context,
				locationRecord,
				LocationPaths._Root_Location._ServiceRadius,
				true,
				true);

			checkFieldRequired(
				context,
				locationRecord,
				LocationPaths._Root_Location._ServiceRadiusUom,
				true,
				true);

			if (locationRecord.get(LocationPaths._Root_Location._IsStaffed) == null)
			{
				checkFieldRequired(
					context,
					locationRecord,
					LocationPaths._Root_Location._IsStaffed,
					true,
					true);
			}
			else if (projectRecord
				.get_boolean(LocationPaths._Root_LocationProject._IsLocationServedBy)
				&& locationRecord.get_boolean(LocationPaths._Root_Location._IsStaffed))
			{
				context.reportMessage(
					UserMessage.createError("A ServedBy Location cannot be Staffed"));
			}
			else if (!projectRecord
				.get_boolean(LocationPaths._Root_LocationProject._IsLocationServedBy)
				&& !locationRecord.get_boolean(LocationPaths._Root_Location._IsStaffed))
			{
				context.reportMessage(UserMessage.createError("This Location must be Staffed"));
			}

			checkFieldRequired(
				context,
				locationRecord,
				LocationPaths._Root_Location._IsAutomated,
				true,
				true);

			checkFieldRequired(
				context,
				locationRecord,
				LocationPaths._Root_Location._IsCarSalesLocation,
				true,
				true);

			checkFieldRequired(
				context,
				locationRecord,
				LocationPaths._Root_Location._IsInPlant,
				true,
				true);

			checkFieldRequired(
				context,
				locationRecord,
				LocationPaths._Root_Location._IsDropShip,
				true,
				true);

			checkFieldRequired(
				context,
				locationRecord,
				LocationPaths._Root_Location._IsUsedForReservationCenter,
				true,
				true);

			checkFieldRequired(
				context,
				locationRecord,
				LocationPaths._Root_Location._VehicleInformation_AfterHoursReservations_AfterHoursReservation,
				true,
				true);

			AdaptationTable locationAddressTable = locationRecord.getContainer()
				.getTable(LocationPaths._Root_LocationData_Address.getPathInSchema());

			// Query the LocationAddress table for a row of type "BUSINESS"
			requestResult = locationAddressTable.createRequestResult(
				LocationPaths._Root_LocationData_Address._Location.format() + "='" + locationId
					+ "' and " + LocationPaths._Root_LocationData_Address._Type.format() + "='"
					+ LocationConstants.LOCATION_ADDRESS_TYPE_BUSINESS + "' and "
					+ LocationPaths._Root_LocationData_Address._PrimarySecondary.format() + " = '"
					+ ContactPirmarySecondary.PRIMARY + "' and "
					+ LocationPaths._Root_LocationData_Address._IsRecordActive.format() + "=true");

			if (requestResult.isEmpty())
			{
				context.reportMessage(
					UserMessage.createError(
						"A Location '" + ContactPirmarySecondary.PRIMARY + "' Address of type '"
							+ LocationConstants.LOCATION_ADDRESS_TYPE_BUSINESS
							+ "' must be specified for the Location."));
			}
			else if (requestResult.getSize() > 1)
			{
				context.reportMessage(
					UserMessage.createError(
						"A Location can have only one Address of type '"
							+ ContactPirmarySecondary.PRIMARY + "'."));
			}
			else
			{
				Adaptation locaationAddressRecord = requestResult.nextAdaptation();
				checkFieldRequired(
					context,
					locaationAddressRecord,
					LocationPaths._Root_LocationData_Address._Address_Country,
					true,
					true);
				checkFieldRequired(
					context,
					locaationAddressRecord,
					LocationPaths._Root_LocationData_Address._Address_City,
					true,
					true);
				if (HtzCommonUtilities.countryHasStatesOrProvinces(
					locaationAddressRecord,
					LocationPaths._Root_LocationData_Address._Address_Country))
				{
					checkFieldRequired(
						context,
						locaationAddressRecord,
						LocationPaths._Root_LocationData_Address._Address_StateProvince,
						true,
						true);
				}
				checkFieldRequired(
					context,
					locaationAddressRecord,
					LocationPaths._Root_LocationData_Address._Address_PostalCode,
					true,
					true);

			}

			// Each Location must have a PRIMARY Contact 
			AdaptationTable locationContactTable = locationRecord.getContainer()
				.getTable(LocationPaths._Root_LocationData_Contact.getPathInSchema());

			// Query the LocationContact table for a row of type "PRIMARY"
			requestResult = locationContactTable.createRequestResult(
				LocationPaths._Root_LocationData_Contact._Location.format() + "='" + locationId
					+ "' and " + LocationPaths._Root_LocationData_Contact._PrimarySecondary.format()
					+ "='" + ContactPirmarySecondary.PRIMARY + "' and "
					+ LocationPaths._Root_LocationData_Contact._IsRecordActive.format() + "=true");

			if (requestResult.isEmpty())
			{
				context.reportMessage(
					UserMessage.createError(
						"A Location Contact of type '" + ContactPirmarySecondary.PRIMARY
							+ "' must be specified for the Location."));
			}
			else if (requestResult.getSize() > 1)
			{
				context.reportMessage(
					UserMessage.createError(
						"A Location can have only one Contact of type '"
							+ ContactPirmarySecondary.PRIMARY + "'."));
			}

			// Query the LocationContact table for a row of type "SECONDARY"
			requestResult = locationContactTable.createRequestResult(
				LocationPaths._Root_LocationData_Contact._Location.format() + "='" + locationId
					+ "' and " + LocationPaths._Root_LocationData_Contact._PrimarySecondary.format()
					+ "='" + ContactPirmarySecondary.SECONDARY + "'");

			if (requestResult.isEmpty())
			{
				context.reportMessage(
					UserMessage.createError(
						"A Location Contact of type '" + ContactPirmarySecondary.SECONDARY
							+ "' must be specified for the Location."));
			}
			else if (requestResult.getSize() > 1)
			{
				context.reportMessage(
					UserMessage.createError(
						"A Location can have only one Contact of type '"
							+ ContactPirmarySecondary.SECONDARY + "'."));
			}

			// Each Location must have a PRIMARY Phone Number 
			AdaptationTable locationPhoneTable = locationRecord.getContainer()
				.getTable(LocationPaths._Root_LocationData_Phone.getPathInSchema());

			// Query the LocationPhone table for a row of type "PRIMARY"
			requestResult = locationPhoneTable.createRequestResult(
				LocationPaths._Root_LocationData_Phone._Location.format() + "='" + locationId
					+ "' and " + LocationPaths._Root_LocationData_Phone._PrimarySecondary.format()
					+ "='" + PhonePirmarySecondary.PRIMARY + "' and"
					+ LocationPaths._Root_LocationData_Phone._IsRecordActive.format() + "=true");

			if (requestResult.isEmpty())
			{
				/* Requirement Relaxed on 9/25/17
				context.reportMessage(
					UserMessage.createError(
						"A Location Phone of type '" + PhonePirmarySecondary.PRIMARY
							+ "' must be specified for the Location."));
							*/
			}
			else if (requestResult.getSize() > 1)
			{
				context.reportMessage(
					UserMessage.createError(
						"A Location can have only one Phone of type '"
							+ ContactPirmarySecondary.PRIMARY + "'."));
			}
			else
			{
				Adaptation locationPrimaryPhone = requestResult.nextAdaptation();
				checkFieldRequired(
					context,
					locationPrimaryPhone,
					LocationPaths._Root_LocationData_Phone._PhoneNumber_AreaCode,
					true,
					true);
				checkFieldRequired(
					context,
					locationPrimaryPhone,
					LocationPaths._Root_LocationData_Phone._PhoneNumber_LocalNumber,
					true,
					true);
			}

			// Query the LocationPhone table for a row of type "SECONDARY"
			requestResult = locationPhoneTable.createRequestResult(
				LocationPaths._Root_LocationData_Phone._Location.format() + "='" + locationId
					+ "' and " + LocationPaths._Root_LocationData_Phone._PrimarySecondary.format()
					+ "='" + PhonePirmarySecondary.SECONDARY + "' and "
					+ LocationPaths._Root_LocationData_Phone._IsRecordActive.format() + "=true");

			if (requestResult.isEmpty())
			{
				/* Requirement Relaxed on 9/25/17
				context.reportMessage(
					UserMessage.createError(
						"A Location Phone of type '" + PhonePirmarySecondary.SECONDARY
							+ "' must be specified for the Location."));
							*/
			}
			else if (requestResult.getSize() > 1)
			{
				context.reportMessage(
					UserMessage.createError(
						"A Location can have only one Phone of type '"
							+ ContactPirmarySecondary.SECONDARY + "'."));
			}
			else
			{
				Adaptation locationSecondaryPhone = requestResult.nextAdaptation();
				checkFieldRequired(
					context,
					locationSecondaryPhone,
					LocationPaths._Root_LocationData_Phone._PhoneNumber_AreaCode,
					true,
					true);
				checkFieldRequired(
					context,
					locationSecondaryPhone,
					LocationPaths._Root_LocationData_Phone._PhoneNumber_LocalNumber,
					true,
					true);
			}

			// Each Location must have a Primary EMail Address 
			AdaptationTable locationEMailAddressTable = locationRecord.getContainer()
				.getTable(LocationPaths._Root_LocationData_EMailAddress.getPathInSchema());

			// Query the LocationEMail table for a row of type "PRIMARY"
			requestResult = locationEMailAddressTable.createRequestResult(
				LocationPaths._Root_LocationData_EMailAddress._Location.format() + "='" + locationId
					+ "' and "
					+ LocationPaths._Root_LocationData_EMailAddress._PrimarySecondary.format()
					+ "='" + EMailPirmarySecondary.PRIMARY + "' and"
					+ LocationPaths._Root_LocationData_EMailAddress._IsRecordActive.format()
					+ "=true");

			if (requestResult.isEmpty())
			{
				context.reportMessage(
					UserMessage.createError(
						"A Location EMail of type '" + EMailPirmarySecondary.PRIMARY
							+ "' must be specified for the Location."));
			}
			else if (requestResult.getSize() > 1)
			{
				context.reportMessage(
					UserMessage.createError(
						"A Location can have only one EMail of type '"
							+ ContactPirmarySecondary.PRIMARY + "'."));
			}

			// At least Currency must be selected for the Location 
			AdaptationTable locationCurrencyRelationshipTable = locationRecord.getContainer()
				.getTable(
					LocationPaths._Root_LocationData_LocationCurrencyRelationship
						.getPathInSchema());

			// Query the Currency Relationship for any currency assigned to this Location
			requestResult = locationCurrencyRelationshipTable.createRequestResult(
				LocationPaths._Root_LocationData_LocationCurrencyRelationship._Location.format()
					+ "='" + locationId + "'");

			if (requestResult.isEmpty())
			{
				context.reportMessage(
					UserMessage
						.createError("At least one Currency must be specified for the Locaton."));
			}

			else
			{
				// Query the Currency Relationship for the Primary currency assigned to this Location
				requestResult = locationCurrencyRelationshipTable.createRequestResult(
					LocationPaths._Root_LocationData_LocationCurrencyRelationship._Location.format()
						+ "='" + locationId + "' and "
						+ LocationPaths._Root_LocationData_LocationCurrencyRelationship._IsPrimaryCurrency
							.format()
						+ " = true");

				if (requestResult.isEmpty())
				{
					context.reportMessage(
						UserMessage.createError(
							"At least one Primary Currency must be specified for the Locaton."));
				}
			}

			// At least one Website must be selected for the Location 
			AdaptationTable locationWebsiteRelationshipTable = locationRecord.getContainer()
				.getTable(
					LocationPaths._Root_LocationData_LocationWebsiteRelationship.getPathInSchema());

			// Query the Location/Website Relationship for any website assigned to this Location
			requestResult = locationWebsiteRelationshipTable.createRequestResult(
				LocationPaths._Root_LocationData_LocationCurrencyRelationship._Location.format()
					+ "='" + locationId + "' and "
					+ LocationPaths._Root_LocationData_LocationCurrencyRelationship._IsRecordActive
						.format()
					+ "=true");

			if (requestResult.isEmpty())
			{
				context.reportMessage(
					UserMessage
						.createError("At least one Website must be specified for the Locaton."));
			}

			//Check to be sure if this is a ServedBy or Satellite Location, 
			//that the Region, Area, and Reservation Area are equal to the values of the Parent Location
			Adaptation relatedLocationRecord = null;
			Adaptation locationLocationRelationshipRecord = null;
			if (locationRecord.get_boolean(LocationPaths._Root_Location._IsServedBy))
			{
				locationLocationRelationshipRecord = HtzLocationUtilities
					.getLocationToLocationRelationshipRecordForType(
						locationRecord,
						LocationConstants.LOCATION_RELATIONSHIP_TYPE_SERVEDBY);
			}
			else if (locationRecord.get_boolean(LocationPaths._Root_Location._IsSatellite))
			{
				locationLocationRelationshipRecord = HtzLocationUtilities
					.getLocationToLocationRelationshipRecordForType(
						locationRecord,
						LocationConstants.LOCATION_RELATIONSHIP_TYPE_SATELLITE);
			}

			if (locationLocationRelationshipRecord != null)
			{
				relatedLocationRecord = AdaptationUtil.followFK(
					locationLocationRelationshipRecord,
					LocationPaths._Root_LocationData_LocationLocationRelationship._ParentLocation);
			}

			if (relatedLocationRecord != null)
			{
				if (relatedLocationRecord
					.get(LocationPaths._Root_Location._LocationInformation_Region) == null)
				{
					context.reportMessage(
						UserMessage.createError(
							"The Region of the ServedBy or Satellite Related Location has no value."));
				}
				else if (!relatedLocationRecord
					.get(LocationPaths._Root_Location._LocationInformation_Region).equals(
						locationRecord
							.get(LocationPaths._Root_Location._LocationInformation_Region)))
				{
					context.reportMessage(
						UserMessage.createError(
							"The Region for the ServedBy or Satellite Location must be the same as the Related Location."));
				}

				if (relatedLocationRecord
					.get(LocationPaths._Root_Location._LocationInformation_Area) == null)
				{
					context.reportMessage(
						UserMessage.createError(
							"The Area of the ServedBy or Satellite Related Location has no value."));
				}
				else if (!relatedLocationRecord
					.get(LocationPaths._Root_Location._LocationInformation_Area).equals(
						locationRecord.get(LocationPaths._Root_Location._LocationInformation_Area)))
				{
					context.reportMessage(
						UserMessage.createError(
							"The Area for a ServedBy or Satellite Location must be the same as the Related Location."));
				}

				if (relatedLocationRecord
					.get(LocationPaths._Root_Location._LocationInformation_ReservationArea) == null)
				{
					context.reportMessage(
						UserMessage.createError(
							"The Reservation Area of the ServedBy or Satellite Related Location has no value."));
				}
				else if (!relatedLocationRecord
					.get(LocationPaths._Root_Location._LocationInformation_ReservationArea).equals(
						locationRecord.get(
							LocationPaths._Root_Location._LocationInformation_ReservationArea)))
				{
					context.reportMessage(
						UserMessage.createError(
							"The Reservation Area for a ServedBy or Satellite Location must be the same as the Related Location."));
				}
			}

			if (locationLocationRelationshipRecord != null)
			{
				//Ensure if there are Location Relationships to this Location that a Satellite or Served by is within distance limits
				String measurementSystem = locationRecord
					.getString(LocationPaths._Root_Location._LocationInformation_MeasurementSystem);

				String unitOfMeasure = locationRecord
					.getString(LocationPaths._Root_Location._ServiceRadiusUom);

				if (unitOfMeasure == null)
				{
					unitOfMeasure = "miles";
				}

				if (measurementSystem == null
					|| HtzConstants.MEASUREMENT_TYPE_US.equals(measurementSystem))
				{
					if (((BigDecimal) locationLocationRelationshipRecord.get(
						LocationPaths._Root_LocationData_LocationLocationRelationship._Distance))
							.doubleValue() > LocationConstants.LOCATION_LOCATION_RELATIONSHIP_US_DISTANCE_LIMIT)
					{
						context.reportMessage(
							UserMessage.createError(
								"The distance between the ServedBy or Satellite Location and its parent must be less than "
									+ LocationConstants.LOCATION_LOCATION_RELATIONSHIP_US_DISTANCE_LIMIT
									+ " " + unitOfMeasure + " between them."));
					}
				}
				else
				{
					if (((BigDecimal) locationLocationRelationshipRecord.get(
						LocationPaths._Root_LocationData_LocationLocationRelationship._Distance))
							.doubleValue() > LocationConstants.LOCATION_LOCATION_RELATIONSHIP_METRIC_DISTANCE_LIMIT)
					{
						context.reportMessage(
							UserMessage.createError(
								"The distance between the ServedBy or Satellite Location and its parent must be less than "
									+ LocationConstants.LOCATION_LOCATION_RELATIONSHIP_METRIC_DISTANCE_LIMIT
									+ " " + unitOfMeasure + " between them."));
					}
				}
			}

			if (requestResult != null)
			{
				requestResult.close();
			}
		}
		super.checkBeforeWorkItemCompletion(context);
	}
}
