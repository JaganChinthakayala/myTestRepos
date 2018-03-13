USE [MDM_DEV]
GO

--/****** Object:  View [dbo].[vwLocation]    Script Date: 10/18/2017 9:26:39 PM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE VIEW [dbo].[vwLocation]
AS

SELECT        
'en' AS Language,

L.id AS location_id,
L.intelligentLocationId AS intelligent_location_id,
L.operationsOwnershipType_ AS operations_ownership_type,
LR_OwnershipType.base_description AS operations_ownership_type_description,

L.T_LAST_USER_ID AS last_user_id,
L.T_CREATOR_ID AS creator_id,
L.T_CREATION_DATE AS creation_date,
L.T_LAST_WRITE AS last_write,

L.locationNumber AS location_number,
L.name AS location_name,
L.description AS location_description,
L.openCloseDate_startDate AS open_date,
L.openCloseDate_endDate AS close_date,

L.LocationInformation_countryTimeZone__country AS country_timezone_country,
L.LocationInformation_countryTimeZone__timezone AS country_timezone_timezone,
L.LocationInformation_stateProvinceTimeZone__stateProvince AS state_province_timezone_state_province,
L.LocationInformation_stateProvinceTimeZone__timezone AS state_province_timezone_timeZone,
C_Timezone.base_description AS timezone_description,
L.LocationInformation_oag_ AS oagCode,
L.LocationInformation_locationCity_ AS location_city,
L.rentalCarBrand_ AS rental_car_brand,
L.LocationInformation_region_ AS region,
L.LocationInformation_reservationArea_ AS reservation_area,
L.isAutomated AS is_automated,
L.isCarSalesLocation AS is_car_sales_location,
L.isFleetLocation AS is_fleet_location,
L.isInPlant AS is_in_plant,
L.isDropShip AS is_drop_ship,
L.isUsedForReservationCenter AS is_used_for_reservation_center,
L.serviceRadius AS service_radius,
L.serviceRadiusUom_ AS service_radius_uom,
L.Fees_refuelingFee AS refueling_fee,
L.LocationInformation_SourceSystem_sourceSystemName_ AS source_system_name,
L.LocationInformation_SourceSystem_sourceSystemRecordId AS source_system_record_id,

L.ProductsAndServices_ContributionManagementSystem_cmsLocationGroup AS cms_location_group,
L.ProductsAndServices_advancedReservationDate AS advanced_reservation_date,
L.ProductsAndServices_ssensEMail AS sssens_email,
L.ProductsAndServices_isGoldAnytimeEnabled AS is_gold_anytime_enabled,
L.ProductsAndServices_isPreRent72Hours AS is_prerent_72_hours,
L.ProductsAndServices_hasDeliveryService AS has_delivery_service,
L.ProductsAndServices_goldService AS gold_service,
L.ProductsAndServices_expressService AS express_service,
L.ProductsAndServices_maximumReimbursementForCab AS maximum_reinbursement_for_cab,
L.ProductsAndServices_AirportLocations_isFixedBasedOperatorFBO AS is_fixed_base_operator_fbo,
L.ProductsAndServices_AirportLocations_areWalkInsPermitted AS are_walkins_permitted,
L.ProductsAndServices_AirportLocations_privateFlightsOnly AS private_flights_only,
L.ProductsAndServices_Hotels_hotelGuestsOnly AS hotel_guests_only,
L.ProductsAndServices_Hotels_specialInstructions AS special_instructions,

L.VehicleInformation_AfterHoursReturns_whereShoudCustomerParkVehicle AS where_should_customer_park_vehicle,
L.VehicleInformation_AfterHoursReturns_whereisDropBoxLocated AS where_is_drop_box_located,
L.VehicleInformation_AfterHoursReturns_dropOffInstructions AS drop_off_instructions,

L.ProductsAndServices_LocationDistances_distanceToBusStation AS distance_to_bus_station,
L.ProductsAndServices_LocationDistances_busStationCabFare AS bus_station_cab_fare,
L.ProductsAndServices_LocationDistances_maximumReimbursementForBusFare AS maximum_reimbursement_for_bus_fare,
L.ProductsAndServices_LocationDistances_distanceToRailroadStation AS distance_to_railroad_station,
L.ProductsAndServices_LocationDistances_railroadStationCabFare AS railroad_station_cab_fare,
L.ProductsAndServices_LocationDistances_maximumReimbursementForRailroad AS maximum_reimbursement_for_railroad,
L.ProductsAndServices_LocationDistances_distanceToPier AS distance_to_pier,
L.ProductsAndServices_LocationDistances_pierCabFare AS pier_cab_fare,
L.ProductsAndServices_LocationDistances_maximumReimbursementForPier AS maximum_reimbursement_for_pier,
L.LocationInformation_AreaBanding_areaBanding AS area_banding,
L.ProductsAndServices_Directions_closestCommercialAirport_ AS closest_commercial_airport,
L.ProductsAndServices_Directions_generalDirectionFromAirport AS general_direction_from_airport,
L.LocationInformation_measurementSystem_ AS measurement_system,
L.LocationInformation_area_ AS area,
L.airport_ AS location_airport,
L.ProductsAndServices_ContributionManagementSystem_cmsPool AS cms_pool,
L.ProductsAndServices_Yield_vawPool AS yield_vaw_pool,
L.ProductsAndServices_Yield_fleetFrom_ AS yield_fleet_from,
L.VehicleInformation_AfterHoursReservations_afterHoursReservation AS after_hours_reservations,
L.status,
L.currentProjectType AS project_type,

C_Brand.base_id AS brand_id,
C_Brand.base_name AS brand_name,
C_Brand.base_description AS brand_description,
C_Brand.isHertzBrand AS brand_is_hertz_brand,
C_Brand.effectiveStartEndDates_startDate AS brand_effective_start_date,
C_Brand.effectiveStartEndDates_endDate AS brand_effective_end_date,

L_TGR.locationTypeGroup_ AS location_type_group,
L_TGR.effectiveStartEndDates_startDate AS location_type_group_effective_start_date,
L_TGR.effectiveStartEndDates_endDate AS location_type_group_effective_end_date,

C_Airport.iataFAA AS airport,
C_Airport.Name AS airport_name,
CASE WHEN L.airport_ IS NOT NULL THEN L_Addr.Address_city ELSE NULL END AS airport_city,
CASE WHEN L.airport_ IS NOT NULL THEN L_Addr.GeographicPoint_longitude ELSE NULL END AS airport_longitude,
CASE WHEN L.airport_ IS NOT NULL THEN L_Addr.GeographicPoint_latitude ELSE NULL END AS airport_latitude,
CASE WHEN L.airport_ IS NOT NULL THEN L.LocationInformation_stateProvinceTimeZone__timezone ELSE NULL END AS airport_timezone,
C_Airport.effectiveStartEndDates_startDate AS airport_effective_start_date,
C_Airport.effectiveStartEndDates_endDate AS airport_effective_end_date,
C_Country.ISO3166_1_integer AS country,
C_Country.ISO3166_1_alpha_2 AS country_code_alpha_2,
C_Country.ISO3166_1_alpha_3 AS country_code_alpha_3,
C_Country.shortName AS country_short_name,
C_Country.effectiveStartEndDates_startDate AS country_effective_start_date,
C_Country.effectiveStartEndDates_endDate AS country_effective_end_date,

C_Subdivision.ISO3166_2_Subdivision AS state_province,
SUBSTRING(C_Subdivision.ISO3166_2_Subdivision, CHARINDEX('-', C_Subdivision.ISO3166_2_Subdivision) + 1,
LEN(C_Subdivision.ISO3166_2_Subdivision) - CHARINDEX('-', C_Subdivision.ISO3166_2_Subdivision)) AS state_province_code,
C_Subdivision.ISO3166_2_SubdivisionName AS state_province_name,
C_Subdivision.effectiveStartEndDates_startDate AS state_province_effective_start_date,
C_Subdivision.effectiveStartEndDates_endDate AS state_province_effective_end_date,

L_Addr.id AS address_id,
L_Addr.type_ AS address_type,
L_Addr.Address_streetNumber AS address_street_number,
L_Addr.Address_addressLine1 AS address_line_1,
L_Addr.Address_addressLine2 AS address_line_2,
L_Addr.Address_addressLine3 AS address_line_3,
L_Addr.Address_city,
L_Addr.Address_stateProvince_ AS address_state_province,
L_Addr.Address_postalCode AS address_postal_code,
L_Addr.primarySecondary AS address_primary_secondary,
L_Addr.GeographicPoint_latitude AS address_latitude,
L_Addr.GeographicPoint_longitude AS address_longitude,
L_Addr.effectiveStartEndDates_startDate AS address_effective_start_date,
L_Addr.effectiveStartEndDates_endDate AS address_effective_end_date,

L_Email.id AS email_id,
L_Email.eMailAddress AS email_address,
L_Email.eMailAddressType_ AS email_address_type,
C_EmailAddressType.base_description AS email_address_type_description,
L_Email.effectiveStartEndDates_startDate AS email_effective_start_date,
L_Email.effectiveStartEndDates_endDate AS email_effective_end_date,

C_Website.base_name AS web_site,
L_WSR.location_ AS web_site_relationship_location,
L_WSR.website_ AS web_site_relationship_web_site,
L_WSR.effectiveStartEndDates_startDate AS web_site_relationship_effective_start_date,
L_WSR.effectiveStartEndDates_endDate AS web_site_relationship_effective_end_date,
C_Website.base_id AS web_site_id,
C_Website.effectiveStartEndDates_startDate AS web_site_effective_start_date,
C_Website.effectiveStartEndDates_endDate AS web_site_effective_end_date,

L_SvcDays.id AS service_days_id,
L_SvcHours.id AS service_hours_id,
C_DOW.dayOfTheWeek AS day_of_the_week,
L_SvcDays.serviceHoursType_ AS service_hours_type,
LR_ServiceHoursType.base_description AS service_hours_type_description,
L_SvcDays.dayOfWeekId_ AS day_of_the_week_id,
L_SvcDays.isOpen24Hours AS service_days_is_open_24_hours,
L_SvcDays.isClosedOnThisDay AS service_days_is_closed_on_this_day,
L_SvcHours.id AS service_hours_times,
L_SvcHours.TimeRange_startTime AS service_hours_start_time,
L_SvcHours.TimeRange_endTime AS service_hours_end_time,
L_SvcDays.DateRange_startDate AS service_days_date_range_startdate,
L_SvcDays.DateRange_endDate AS service_days_date_range_enddate,
L_SvcDays.SeasonalDateRange_startMonth AS service_days_seasonal_start_month,
L_SvcDays.SeasonalDateRange_startDay AS service_days_seasonal_start_day,
L_SvcDays.SeasonalDateRange_endMonth AS service_seasonal_end_month,
L_SvcDays.SeasonalDateRange_endDay AS service_seasonal_end_day,
L_SvcHours.effectiveStartEndDates_startDate AS service_hours_effective_start_date,
L_SvcHours.effectiveStartEndDates_endDate AS service_hours_effective_end_date,

L_SvcDays.isSeasonal AS is_seasonal,
L_SvcDays.effectiveStartEndDates_startDate AS service_days_effective_start_date,
L_SvcDays.effectiveStartEndDates_endDate AS service_days_effective_end_date,
C_DOW.effectiveStartEndDates_startDate AS day_of_the_week_effective_start_date,
C_DOW.effectiveStartEndDates_endDate AS day_of_the_week_effective_end_date,

L_Phone.id AS phone_id,
L_Phone.phoneType_ AS phone_type,
C_PhoneType.base_description AS phone_type_description,
L_Phone.PhoneNumber_countryCode AS phone_country_code,
L_Phone.PhoneNumber_areaCode AS phone_area_code,
L_Phone.PhoneNumber_localNumber AS phone_local_number,
L_Phone.effectiveStartEndDates_startDate AS phone_effective_start_date,
L_Phone.effectiveStartEndDates_endDate AS phone_effective_end_date,

C_NoteType.base_name AS note_type,
C_Note.base_name AS note_name,
C_Note.base_description AS note_description,
C_Note.effectiveStartEndDates_startDate AS note_effective_start_date,
C_Note.effectiveStartEndDates_endDate AS note_effective_end_date,
L_NR.effectiveStartEndDates_startDate AS note_relationship_effective_start_date,
L_NR.effectiveStartEndDates_endDate AS note_relationship_effective_end_date,
C_NoteType.effectiveStartEndDates_startDate AS note_type_effective_start_date,
C_NoteType.effectiveStartEndDates_endDate AS note_type_effective_end_Date,

L_AR.location_ AS ancillary_relationship_location,
L_AR.ancillary_ AS ancillary_relationship,
L_AR.effectiveStartEndDates_startDate AS ancillary_relationship_effective_start_date,
L_AR.effectiveStartEndDates_endDate AS ancillary_relationship_effective_end_date,
LR_Ancillary.base_id AS ancillary_id,
LR_Ancillary.base_name AS ancillary_name,
LR_Ancillary.price AS ancillary_list_price,
LR_Ancillary.base_description AS ancillary_description,
LR_Ancillary.type AS ancillary_type,
LR_Ancillary.effectiveStartEndDates_startDate AS ancillary_effective_start_date,
LR_Ancillary.effectiveStartEndDates_endDate AS ancillary_effective_end_date,

LR_AncillaryGroupRelationship.ancillaryGroup_ AS ancillary_group_id,
LR_AncillaryGroupRelationship.ancillary_ AS ancillary_group_name,
LR_AncillaryGroupRelationship.ancillaryGroup_ AS group_ancillary_id,
LR_AncillaryGroupRelationship.effectiveStartEndDates_startDate AS ancillary_group_start_date,
LR_AncillaryGroupRelationship.effectiveStartEndDates_endDate AS ancillary_group_end_date,

C_OAG.extendedOAGCode AS oag_extended_oag_code, SUBSTRING(C_OAG.extendedOAGCode, 1, 3) AS oag_iata_code,
SUBSTRING(C_OAG.extendedOAGCode, 4, 1) AS oag_vicinity,
SUBSTRING(C_OAG.extendedOAGCode, 5, 2) AS oag_location_code,
C_OAG.effectiveStartEndDates_startDate AS oag_effective_start_date,
C_OAG.effectiveStartEndDates_endDate AS oag_effective_end_date,

L_LocationCurrency.currency_ AS currency_name,
L_LocationCurrency.isPrimaryCurrency AS is_primary_currency,
C_Currency.base_description AS currency_description,
L_LocationCurrency.effectiveStartEndDates_startDate AS currency_start_date,
L_LocationCurrency.effectiveStartEndDates_endDate AS currency_end_date,

L_LocationPaymentTypes.paymentType_ AS payment_type,
C_PaymentType.base_id AS payment_type_id,
C_PaymentType.base_description AS payment_type_description,
L_LocationPaymentTypes.effectiveStartEndDates_startDate AS payment_type_start_date,
L_LocationPaymentTypes.effectiveStartEndDates_endDate AS payment_type_end_date,

LR_LocationRelationshipType.base_name AS location_relationship_type_name,
LR_LocationRelationshipType.base_id AS location_relationship_type_id,
LR_LocationRelationshipType.base_description AS location_relationship_type_description,
L_LocationLocation.subLocation_ AS child_location_id,
L_LocationLocation.parentLocation_ AS parent_location_id,
L_LocationLocation.effectiveStartEndDates_startDate AS location_relationship_start_date,
L_LocationLocation.effectiveStartEndDates_endDate AS location_relationship_end_date,

LR_Indicator.base_id AS location_indicator_id,
L_LocationIndicatorRelationship.indicator_ AS location_indicator_name,
LR_Indicator.base_description AS location_indicator_description,
LR_IndicatorCategory.base_name AS location_indicator_category_name,
LR_IndicatorCategory.base_description AS location_indicator_category_description,
L_LocationIndicatorRelationship.effectiveStartEndDates_startDate AS location_indicator_start_date,
L_LocationIndicatorRelationship.effectiveStartEndDates_endDate AS location_indicator_end_date,

L_LocationCarClassRelationship.carClass__carClass AS car_class_name,
VR_CarClass.carClass AS car_class_id,
VR_CarClass.description AS car_class_description,
L_LocationCarClassRelationship.effectiveStartEndDates_startDate AS car_class_start_date,
L_LocationCarClassRelationship.effectiveStartEndDates_endDate AS car_class_end_date

FROM            

dbo.EBX_db_locn_location AS L LEFT OUTER JOIN
dbo.EBX_db_comm_comref_rental_car_brand AS C_Brand ON L.rentalCarBrand_ = C_Brand.base_id LEFT OUTER JOIN
dbo.EBX_db_comm_comref_airport AS C_Airport ON L.airport_ = C_Airport.iataFAA LEFT OUTER JOIN
dbo.EBX_HG_LocationLocationTypeGroupRelationship AS HG_LocationTypeGroupRelationship ON HG_LocationTypeGroupRelationship.location_ = L.id LEFT OUTER JOIN
dbo.EBX_db_locn_location_address AS L_Addr ON L.id = L_Addr.location_ AND L_Addr.type_ = 'BUSINESS' AND L_Addr.primarySecondary = 'Primary' LEFT OUTER JOIN
dbo.EBX_db_comm_comref_country_iso3166_1 AS C_Country ON L_Addr.Address_country_ = C_Country.ISO3166_1_integer LEFT OUTER JOIN
dbo.EBX_db_comm_comref_subdivision_iso3166_2 AS C_Subdivision ON L_Addr.Address_stateProvince_ = C_Subdivision.ISO3166_2_Subdivision LEFT OUTER JOIN
dbo.EBX_db_locn_location_email_address AS L_Email ON L.id = L_Email.location_ LEFT OUTER JOIN
dbo.EBX_db_comm_comref_email_address_type AS C_EmailAddressType ON C_EmailAddressType.base_id = L_Email.eMailAddressType_ LEFT OUTER JOIN
dbo.EBX_db_locn_location_website_relationship AS L_WSR ON L.id = L_WSR.location_ LEFT OUTER JOIN
dbo.EBX_db_comm_comref_website AS C_Website ON L_WSR.website_ = C_Website.base_id LEFT OUTER JOIN

dbo.EBX_db_locn_service_days AS L_SvcDays ON L.id = L_SvcDays.location_ LEFT OUTER JOIN
dbo.EBX_db_comm_comref_days_of_week AS C_DOW ON L_SvcDays.dayOfWeek_ = C_DOW.id LEFT OUTER JOIN
dbo.EBX_db_locn_service_hours AS L_SvcHours ON L_SvcDays.id = L_SvcHours.serviceDays_ LEFT OUTER JOIN
dbo.EBX_db_locn_locref_service_hours_type AS LR_ServiceHoursType ON LR_ServiceHoursType.base_id = L_SvcDays.serviceHoursType_ LEFT OUTER JOIN

dbo.EBX_db_locn_location_phone AS L_Phone ON L.id = L_Phone.location_ LEFT OUTER JOIN
dbo.EBX_db_comm_comref_phone_type AS C_PhoneType ON C_PhoneType.base_id = L_Phone.phoneType_ LEFT OUTER JOIN

dbo.EBX_db_locn_location_ancillary_relationship AS L_AR ON L.id = L_AR.location_ LEFT OUTER JOIN
dbo.EBX_db_locn_locref_ancillary AS LR_Ancillary ON L_AR.ancillary_ = LR_Ancillary.base_id LEFT OUTER JOIN
dbo.EBX_db_locn_locref_ancillary AS LR_AncillaryGroup ON L_AR.ancillary_ = LR_AncillaryGroup.base_id AND LR_AncillaryGroup.isAncillariesGroup = 1 LEFT OUTER JOIN
dbo.EBX_db_locn_locref_ancillary_group_relationship AS LR_AncillaryGroupRelationship ON LR_AncillaryGroupRelationship.ancillary_ = LR_Ancillary.base_id LEFT OUTER JOIN
dbo.EBX_db_locn_location_location_type_group_relationship AS L_TGR ON L.id = L_TGR.location_ LEFT OUTER JOIN
dbo.EBX_db_locn_location_note_relationship AS L_NR ON L.id = L_NR.location_ LEFT OUTER JOIN
dbo.EBX_db_comm_comref_note AS C_Note ON L_NR.note_ = C_Note.base_id LEFT OUTER JOIN
dbo.EBX_db_comm_comref_note_type AS C_NoteType ON C_Note.noteType_ = C_NoteType.base_id LEFT OUTER JOIN
dbo.EBX_db_comm_comref_oag AS C_OAG ON L.LocationInformation_oag_ = C_OAG.extendedOAGCode LEFT OUTER JOIN
dbo.EBX_db_comm_comref_timezone AS C_Timezone ON L.LocationInformation_stateProvinceTimeZone__timezone = C_Timezone.base_id LEFT OUTER JOIN
dbo.EBX_db_locn_locref_operations_ownership_type AS LR_OwnershipType ON LR_OwnershipType.base_name = L.operationsOwnershipType_ LEFT OUTER JOIN
dbo.EBX_db_locn_location_currency_relationship AS L_LocationCurrency ON L_LocationCurrency.location_ = L.id AND L_LocationCurrency.isPrimaryCurrency = 1 LEFT OUTER JOIN
dbo.EBX_db_comm_comref_currency AS C_Currency ON C_Currency.base_id = L_LocationCurrency.currency_ LEFT OUTER JOIN
dbo.EBX_db_locn_location_payment_types_relationship AS L_LocationPaymentTypes ON L_LocationPaymentTypes.location_ = L.id LEFT OUTER JOIN
dbo.EBX_db_comm_comref_payment_type AS C_PaymentType ON L_LocationPaymentTypes.paymentType_ = C_PaymentType.base_name LEFT OUTER JOIN
dbo.EBX_db_locn_location_location_relationship AS L_LocationLocation ON L_LocationLocation.subLocation_ = L.id LEFT OUTER JOIN
dbo.EBX_db_locn_location_location_relationship AS L_LocationRelationship ON L_LocationRelationship.locationRelationshipType_ = L_LocationLocation.locationRelationshipType_ LEFT OUTER JOIN
dbo.EBX_db_locn_locref_location_relationship_type AS LR_LocationRelationshipType ON LR_LocationRelationshipType.base_id = L_LocationLocation.locationRelationshipType_ LEFT OUTER JOIN
dbo.EBX_db_locn_location_indicator_relationship AS L_LocationIndicatorRelationship ON L_LocationIndicatorRelationship.location_ = L.id LEFT OUTER JOIN
dbo.EBX_db_locn_locref_indicator AS LR_Indicator ON LR_Indicator.base_id = L_LocationIndicatorRelationship.indicator_ LEFT OUTER JOIN
dbo.EBX_db_locn_locref_indicator_category AS LR_IndicatorCategory ON LR_IndicatorCategory.base_id = LR_Indicator.indicatorCategory_ LEFT OUTER JOIN
dbo.EBX_db_locn_location_car_class_relationship AS L_LocationCarClassRelationship ON L_LocationCarClassRelationship.location_ = L.id LEFT OUTER JOIN
dbo.EBX_db_vhclref_car_class AS VR_CarClass ON VR_CarClass.carClass = L_LocationCarClassRelationship.carClass__carClass