/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.trigger;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.hertz.mdm.common.trigger.HtzSubjectTableTrigger;
import com.hertz.mdm.location.constants.LocationConstants;
import com.hertz.mdm.location.enums.LocationUpdateTypes;
import com.hertz.mdm.location.path.LocationPathConfig;
import com.hertz.mdm.location.path.LocationPaths;
import com.hertz.mdm.location.path.LocationReferencePaths;
import com.hertz.mdm.location.util.HtzLocationUtilities;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.RequestResult;
import com.orchestranetworks.ps.procedure.CreateRecordProcedure;
import com.orchestranetworks.ps.procedure.ModifyValuesProcedure;
import com.orchestranetworks.ps.project.path.SubjectPathConfig;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.trigger.AfterCreateOccurrenceContext;
import com.orchestranetworks.schema.trigger.AfterModifyOccurrenceContext;
import com.orchestranetworks.schema.trigger.NewTransientOccurrenceContext;
import com.orchestranetworks.schema.trigger.ValueChanges;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.ProcedureContext;

/**
 */
public class LocationTableTrigger extends HtzSubjectTableTrigger
{
	private static final int DAY_OF_WEEK_SUNDAY = 0;
	private static final int DAY_OF_WEEK_SATURDAY = 6;

	@Override
	public SubjectPathConfig getSubjectPathConfig()
	{
		return LocationPathConfig.getInstance();
	}

	@Override
	public void handleNewContext(NewTransientOccurrenceContext context)
	{
		super.handleNewContext(context);
	}

	@Override
	public void handleAfterCreate(AfterCreateOccurrenceContext context) throws OperationException
	{
		super.handleAfterCreate(context);

		createServiceHours(context.getAdaptationOccurrence(), context.getProcedureContext());
		setRefuelingFee(context.getAdaptationOccurrence(), context.getProcedureContext());

		HtzLocationUtilities.writeToUpdateNotificationTable(
			context.getAdaptationOccurrence(),
			LocationUpdateTypes.LOCATION_CREATE,
			context.getAdaptationOccurrence().toString(),
			context.getProcedureContext());
	}

	@Override
	public void handleAfterModify(AfterModifyOccurrenceContext context) throws OperationException
	{
		boolean isLocatoinRecordUModifyRequired = false;
		HashMap<Path, Object> pathValueMap = new HashMap<Path, Object>();

		Adaptation locationRecord = context.getAdaptationOccurrence();

		if (locationRecord
			.get(LocationPaths._Root_Location._LocationInformation_LocationMinimumAge) == null)
		{
			pathValueMap.put(
				LocationPaths._Root_Location._LocationInformation_LocationMinimumAge,
				locationRecord.getString(
					LocationPaths._Root_Location._LocationInformation_CountryMinimumAge));

			isLocatoinRecordUModifyRequired = true;
		}

		if (locationRecord
			.get(LocationPaths._Root_Location._LocationInformation_CountryMaximumAge) == null)
		{
			pathValueMap.put(
				LocationPaths._Root_Location._LocationInformation_LocationMaximumAge,
				locationRecord.getString(
					LocationPaths._Root_Location._LocationInformation_CountryMaximumAge));

			isLocatoinRecordUModifyRequired = true;
		}

		ValueChanges changes = context.getChanges();
		if (changes.getChange(LocationPaths._Root_Location._LocationInformation_Oag) != null
			|| changes.getChange(LocationPaths._Root_Location._RentalCarBrand) != null
			|| changes.getChange(LocationPaths._Root_Location._OperationsOwnershipType) != null)
		{
			HtzLocationUtilities.alignIntelligentLocationIdsWithLocationTypes(
				locationRecord,
				context.getProcedureContext(),
				null);

			Adaptation intelligentLocationIdRecord = HtzLocationUtilities
				.locationPrimaryIntelligentId(locationRecord);

			//Update the Location Record with the Primary Intelligent Location Id
			if (intelligentLocationIdRecord != null)
			{
				pathValueMap.put(
					LocationPaths._Root_Location._IntelligentLocationId,
					intelligentLocationIdRecord.getString(
						LocationPaths._Root_LocationData_LocationIntelligentLocationIdRelationship.__intelligentLocationId));
			}
		}

		if (isLocatoinRecordUModifyRequired)
		{
			ModifyValuesProcedure
				.execute(context.getProcedureContext(), locationRecord, pathValueMap, true, false);
		}

		super.handleAfterModify(context);

		HtzLocationUtilities.writeToUpdateNotificationTable(
			context.getAdaptationOccurrence(),
			LocationUpdateTypes.LOCATION_UPDATE,
			context.getAdaptationOccurrence().toString(),
			context.getProcedureContext());
	}

	private static void createServiceHours(Adaptation locationRecord, ProcedureContext pContext)
		throws OperationException
	{
		AdaptationTable serviceDaysTable = locationRecord.getContainer()
			.getTable(LocationPaths._Root_LocationData_ServiceDays.getPathInSchema());

		AdaptationTable serviceHoursTable = locationRecord.getContainer()
			.getTable(LocationPaths._Root_LocationData_ServiceHours.getPathInSchema());

		String locationId = locationRecord.get(LocationPaths._Root_Location._Id).toString();

		// Create Service Days and Hours for 7 days
		Calendar cal = Calendar.getInstance();

		//Week Day Start/End Times
		cal.set(Calendar.HOUR_OF_DAY, 7);
		cal.set(Calendar.MINUTE, 30);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		Date startTime = cal.getTime();

		cal.set(Calendar.HOUR_OF_DAY, 18);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		Date endTime = cal.getTime();

		//Saturday Start/End Times
		cal.set(Calendar.HOUR_OF_DAY, 9);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		Date saturdayStartTime = cal.getTime();

		cal.set(Calendar.HOUR_OF_DAY, 13);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		Date saturdayEndTime = cal.getTime();

		Adaptation serviceDayRecord = null;

		//Day of Week starts with Sunday
		for (int dayOfWeek = 0; dayOfWeek <= 6; dayOfWeek++)
		{
			Map<Path, Object> pathValueMapDay = new HashMap<Path, Object>();
			Map<Path, Object> pathValueMapTime = new HashMap<Path, Object>();

			//Set up the days
			pathValueMapDay.put(LocationPaths._Root_LocationData_ServiceDays._Location, locationId);
			pathValueMapDay.put(
				LocationPaths._Root_LocationData_ServiceDays._DayOfWeek,
				String.valueOf(dayOfWeek + 1));
			pathValueMapDay.put(
				LocationPaths._Root_LocationData_ServiceDays._ServiceHoursType,
				LocationConstants.LOCATION_SERVICE_DAYS_HOURS_OF_OPERATION);
			pathValueMapDay.put(
				LocationPaths._Root_LocationData_ServiceDays._EffectiveStartEndDates_StartDate,
				new Date());
			pathValueMapDay.put(
				LocationPaths._Root_LocationData_ServiceDays._EffectiveStartEndDates_StartDate,
				new Date());

			//Set up the Open/Close times for the day
			pathValueMapTime.put(
				LocationPaths._Root_LocationData_ServiceHours._ServiceDays,
				String.valueOf(dayOfWeek + 1));
			pathValueMapTime.put(
				LocationPaths._Root_LocationData_ServiceHours._EffectiveStartEndDates_StartDate,
				new Date());
			if (dayOfWeek == DAY_OF_WEEK_SUNDAY)
			{
				pathValueMapDay
					.put(LocationPaths._Root_LocationData_ServiceDays._IsClosedOnThisDay, true);
			}
			else if (dayOfWeek == DAY_OF_WEEK_SATURDAY)
			{
				pathValueMapTime.put(
					LocationPaths._Root_LocationData_ServiceHours._TimeRange_StartTime,
					saturdayStartTime);
				pathValueMapTime.put(
					LocationPaths._Root_LocationData_ServiceHours._TimeRange_EndTime,
					saturdayEndTime);
			}
			else
			{
				pathValueMapTime.put(
					LocationPaths._Root_LocationData_ServiceHours._TimeRange_StartTime,
					startTime);
				pathValueMapTime
					.put(LocationPaths._Root_LocationData_ServiceHours._TimeRange_EndTime, endTime);
			}

			if (dayOfWeek != 0)
			{
				serviceDayRecord = CreateRecordProcedure
					.execute(pContext, serviceDaysTable, pathValueMapDay, true, false);

				pathValueMapTime.put(
					LocationPaths._Root_LocationData_ServiceHours._ServiceDays,
					serviceDayRecord.get(LocationPaths._Root_LocationData_ServiceDays._Id)
						.toString());

				CreateRecordProcedure
					.execute(pContext, serviceHoursTable, pathValueMapTime, true, false);
			}
			else
			{
				serviceDayRecord = CreateRecordProcedure
					.execute(pContext, serviceDaysTable, pathValueMapDay, true, false);
			}
		}
	}

	private static void setRefuelingFee(Adaptation locationRecord, ProcedureContext pContext)
		throws OperationException
	{
		Adaptation locationReferenceDataSet = HtzLocationUtilities
			.getLocationReferenceDataSet(locationRecord.getHome());

		HashMap<Path, Object> pathValueMapRefuel = new HashMap<Path, Object>();

		AdaptationTable refuelingFeeTable = locationReferenceDataSet
			.getTable(LocationReferencePaths._Root_RefuelingFee.getPathInSchema());

		RequestResult requestResult = refuelingFeeTable.createRequestResult(
			"osd:is-null("
				+ LocationReferencePaths._Root_RefuelingFee._EffectiveStartEndDates_EndDate.format()
				+ ")");

		if (!requestResult.isEmpty())
		{
			pathValueMapRefuel.put(
				LocationPaths._Root_Location._Fees_RefuelingFee,
				requestResult.nextAdaptation()
					.get(LocationReferencePaths._Root_RefuelingFee._RefuelingFeeAmount));
		}
		ModifyValuesProcedure.execute(pContext, locationRecord, pathValueMapRefuel, true, false);
	}
}
