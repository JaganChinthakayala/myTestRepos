/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.usertask;

import com.hertz.mdm.location.constants.LocationConstants;
import com.hertz.mdm.location.path.LocationPaths;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.RequestResult;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.workflow.UserTaskBeforeWorkItemCompletionContext;
/**
 */
public class OpenLocationEnterProductsAndServicesUserTask
	extends
	OpenLocationProjectMaintenanceUserTask
{
	@Override
	public void checkBeforeWorkItemCompletion(UserTaskBeforeWorkItemCompletionContext context)
	{
		if (isCompletionCriteriaIgnored())
		{
			return;
		}

		RequestResult requestResult = null;
		RequestResult requestResult2 = null;

		if (context.isAcceptAction())
		{
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

			checkFieldRequired(
				context,
				locationRecord,
				LocationPaths._Root_Location._ProductsAndServices_SsensEMail,
				true,
				true);

			checkFieldRequired(
				context,
				locationRecord,
				LocationPaths._Root_Location._ProductsAndServices_IsGoldAnytimeEnabled,
				true,
				true);

			checkFieldRequired(
				context,
				locationRecord,
				LocationPaths._Root_Location._ProductsAndServices_IsPreRent72Hours,
				true,
				true);

			checkFieldRequired(
				context,
				locationRecord,
				LocationPaths._Root_Location._ProductsAndServices_HasDeliveryService,
				true,
				true);

			checkFieldRequired(
				context,
				locationRecord,
				LocationPaths._Root_Location._ProductsAndServices_GoldService,
				true,
				true);

			checkFieldRequired(
				context,
				locationRecord,
				LocationPaths._Root_Location._ProductsAndServices_ExpressService,
				true,
				true);

			checkFieldRequired(
				context,
				locationRecord,
				LocationPaths._Root_Location._ProductsAndServices_MaximumReimbursementForCab,
				true,
				true);

			checkFieldRequired(
				context,
				locationRecord,
				LocationPaths._Root_Location._ProductsAndServices_AirportLocations_IsFixedBasedOperatorFBO,
				true,
				true);

			checkFieldRequired(
				context,
				locationRecord,
				LocationPaths._Root_Location._ProductsAndServices_AirportLocations_AreWalkInsPermitted,
				true,
				true);

			checkFieldRequired(
				context,
				locationRecord,
				LocationPaths._Root_Location._ProductsAndServices_AirportLocations_PrivateFlightsOnly,
				true,
				true);

			checkFieldRequired(
				context,
				locationRecord,
				LocationPaths._Root_Location._ProductsAndServices_Hotels_HotelGuestsOnly,
				true,
				true);

			/*
			checkFieldRequired(
				context,
				locationRecord,
				LocationPaths._Root_Location._ProductsAndServices_LocationDistances_DistanceToBusStation,
				true,
				true);
			
			checkFieldRequired(
				context,
				locationRecord,
				LocationPaths._Root_Location._ProductsAndServices_LocationDistances_BusStationCabFare,
				true,
				true);
			
			checkFieldRequired(
				context,
				locationRecord,
				LocationPaths._Root_Location._ProductsAndServices_LocationDistances_MaximumReimbursementForBusFare,
				true,
				true);
			
			checkFieldRequired(
				context,
				locationRecord,
				LocationPaths._Root_Location._ProductsAndServices_LocationDistances_DistanceToRailroadStation,
				true,
				true);
			
			checkFieldRequired(
				context,
				locationRecord,
				LocationPaths._Root_Location._ProductsAndServices_LocationDistances_RailroadStationCabFare,
				true,
				true);
			
			checkFieldRequired(
				context,
				locationRecord,
				LocationPaths._Root_Location._ProductsAndServices_LocationDistances_BusStationCabFare,
				true,
				true);
			
			checkFieldRequired(
				context,
				locationRecord,
				LocationPaths._Root_Location._ProductsAndServices_LocationDistances_MaximumReimbursementForRailroad,
				true,
				true);
				*/

			checkFieldRequired(
				context,
				locationRecord,
				LocationPaths._Root_Location._ProductsAndServices_Directions_ClosestCommercialAirport,
				true,
				true);

			checkFieldRequired(
				context,
				locationRecord,
				LocationPaths._Root_Location._ProductsAndServices_Directions_GeneralDirectionFromAirport,
				true,
				true);

			String locationId = locationRecord.get(LocationPaths._Root_Location._Id).toString();
			/*
						//A location must have at least one Payment Type
						AdaptationTable locationPaymentTypeRelationsipTable = locationRecord.getContainer()
							.getTable(
								LocationPaths._Root_LocationData_LocationPaymentTypeRelationship
									.getPathInSchema());
			
						// Query the Location/PaymentTypes Table for this location
						requestResult = locationPaymentTypeRelationsipTable.createRequestResult(
							LocationPaths._Root_LocationData_LocationPaymentTypeRelationship._Location.format()
								+ "='" + locationId + "' and "
								+ LocationPaths._Root_LocationData_Address._IsRecordActive.format() + "=true");
			
						if (requestResult.isEmpty())
						{
							context.reportMessage(
								UserMessage.createError(
									"At least one " + getFieldLabel(
										locationRecord,
										LocationPaths._Root_Location._PaymentTypes,
										context.getSession(),
										true) + " must be specified for the Location"));
						}
						*/

			if (LocationConstants.OPERATION_OWNERSHIP_TYPE_CORPORATE.equals(
				locationRecord.getString(LocationPaths._Root_Location._OperationsOwnershipType)))
			{
				// This location is CORPORATE, therefore all of the Ancillaries Required by Corporate need to be present
				//	Adaptation ownershipLocationType = AdaptationUtil.followFK(
				//		locationRecord,
				//		LocationPaths._Root_Location._OperationsOwnershipType);

				//	AdaptationTable ancillaryTable = ownershipLocationType.getContainer()
				//	.getTable(LocationReferencePaths._Root_Ancillary.getPathInSchema());
				/*
								AdaptationTable ancillaryTable = RepositoryUtils.getTable(
									RepositoryUtils.getDataSet(
										HtzConstants.LOCATION_DATA_SPACE,
										HtzConstants.LOCATION_REFERENCE_DATA_SET),
									LocationReferencePaths._Root_Ancillary.getPathInSchema().format());
				
								// Query the Ancillary table for a row of that are RequiredByCorporate
								requestResult = ancillaryTable.createRequestResult(
									LocationReferencePaths._Root_Ancillary._IsRequiredByCorporate.format()
										+ "=true and "
										+ LocationReferencePaths._Root_Ancillary._IsRecordActive.format()
										+ "=true");
				
								if (!requestResult.isEmpty())
								{
									for (int i = 0; i < requestResult.getSize(); i++)
									{
										Adaptation ancillaryRecord = requestResult.nextAdaptation();
				
										AdaptationTable locationAcillaryRelationshopTable = locationRecord
											.getContainer().getTable(
												LocationPaths._Root_LocationData_LocationAncillaryRelationship
													.getPathInSchema());
				
										// Query the Location Ancillaries 
										requestResult2 = locationAcillaryRelationshopTable.createRequestResult(
											LocationPaths._Root_LocationData_LocationAncillaryRelationship._Location
												.format() + "= '" + locationId
												+ "' and "
												+ LocationPaths._Root_LocationData_LocationAncillaryRelationship._Ancillary
													.format()
												+ "='" + ancillaryRecord.getString(
													LocationReferencePaths._Root_Ancillary._Base_Id)
												+ "'");
				
										if (requestResult2.isEmpty())
										{
											context.reportMessage(
												UserMessage.createError(
													"Ancillary '"
														+ ancillaryRecord.getString(
															LocationReferencePaths._Root_Ancillary._Base_Id)
														+ "' must be specified for a Corporate Location."));
										}
									}
								} */
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
		super.checkBeforeWorkItemCompletion(context);
	}
}