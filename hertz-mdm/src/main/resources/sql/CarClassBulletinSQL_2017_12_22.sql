SELECT
	C.carClass__carClass as 'Car Class' ,
	C.sippCode_ as 'SIPP',
	G.USGROUP,
	G.RMCMS,
	G.LHCSEGMENT,
	G.USCOLLECTION,
	C.description AS 'Car Class Description',
	C.RESERVABLE,
	C.EXACT_MODEL,
	C.SIPP_DESC AS 'SIPP Description'
FROM
	(
SELECT
	b.carClass__carClass,
	-- b.carClass__country,
	b.sippCode_,
	CASE WHEN b.reservable = 'Y' THEN 'RESERVABLE' ELSE 'NON-RESERVABLE' END AS RESERVABLE,
	CASE WHEN b.exactModel = 'Y' THEN 'YES' ELSE 'NO' END AS EXACT_MODEL,
	a.description,
	sc.description + ' ' + st.description + ' ' + sd.description + ' ' + sf.description AS 'SIPP_DESC'
FROM
	dbo.EBX_db_vhclref_sipp_car_class_relationship b,
	dbo.EBX_db_vhclref_car_class a,
	dbo.EBX_db_vhclref_sipp_category sc,
	dbo.EBX_db_vhclref_sipp_type st,
	dbo.EBX_db_vhclref_sipp_fuel_air_conditioning sf,
	dbo.EBX_db_vhclref_sipp_transmission_drive sd
WHERE
	a.carClass = b.carClass__carClass AND
	a.country_ = b.carClass__country AND
	a.country_ = 840 AND
	SUBSTRING(b.sippCode_,	1, 1) = sc.categoryCode AND
	SUBSTRING(b.sippCode_, 2, 1) = st.typeCode AND
	SUBSTRING(b.sippCode_, 3, 1) = sd.transmissionDriveCode AND
	SUBSTRING(b.sippCode_, 4, 1) = sf.fuelAirConditioningCode AND
	b.effectiveStartEndDates_endDate is NULL AND
	a.effectiveStartEndDates_endDate is NULL
	 ) C,
	(
SELECT
	*
FROM
	(
SELECT
	sippCode_,
	carGroup__carGroupType,
	carGroup__carGroup
FROM
	dbo.EBX_db_vhclref_sipp_car_group_relationship
	where effectiveStartEndDates_endDate is NULL ) p pivot ( 
	max(carGroup__carGroup) for carGroup__carGroupType IN ([USGROUP],
	[RMCMS],
	[LHCSEGMENT],
	[USCOLLECTION] )) as carGroup__carGroup ) G
WHERE
	C.sippCode_ = G.sippCode_
ORDER BY
	G.USCOLLECTION,
	C.carClass__carClass
