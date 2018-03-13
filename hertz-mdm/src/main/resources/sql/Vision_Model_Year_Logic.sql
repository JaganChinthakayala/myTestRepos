-- Vision Model Year Logic

select E010_SER_MDL_YR, E010_MDL_YR,
       case when (E010_SER_MDL_YR = 0 AND E010_MDL_YR = 0 ) then
         (CASE 
           WHEN RIGHT(LEFT(E010_SERIAL_NBR, 10), 1) = 'A' THEN 
             CASE 
               WHEN YEAR(E010_DELV_DT) < 2010 - 1 THEN '1980' 
               ELSE '2010' 
             END 
           WHEN RIGHT(LEFT(E010_SERIAL_NBR, 10), 1) = 'B' THEN 
             CASE 
               WHEN YEAR(E010_DELV_DT) < 2011 - 1 THEN '1981' 
               ELSE '2011' 
             END 
           WHEN RIGHT(LEFT(E010_SERIAL_NBR, 10), 1) = 'C' THEN 
             CASE 
               WHEN YEAR(E010_DELV_DT) < 2012 - 1 THEN '1982' 
               ELSE '2012' 
             END 
           WHEN RIGHT(LEFT(E010_SERIAL_NBR, 10), 1) = 'D' THEN 
             CASE 
               WHEN YEAR(E010_DELV_DT) < 2013 - 1 THEN '1983' 
               ELSE '2013' 
             END 
           WHEN RIGHT(LEFT(E010_SERIAL_NBR, 10), 1) = 'E' THEN 
             CASE 
               WHEN YEAR(E010_DELV_DT) < 2014 - 1 THEN '1984' 
               ELSE '2014' 
             END 
           WHEN RIGHT(LEFT(E010_SERIAL_NBR, 10), 1) = 'F' THEN 
             CASE 
               WHEN YEAR(E010_DELV_DT) < 2015 - 1 THEN '1985' 
               ELSE '2015' 
             END 
           WHEN RIGHT(LEFT(E010_SERIAL_NBR, 10), 1) = 'G' THEN 
             CASE 
               WHEN YEAR(E010_DELV_DT) < 2016 - 1 THEN '1986' 
               ELSE '2016' 
             END 
           WHEN RIGHT(LEFT(E010_SERIAL_NBR, 10), 1) = '1' THEN 
             CASE 
               WHEN YEAR(E010_DELV_DT) < 2001 - 1 THEN '1971' 
               ELSE '2001' 
             END 
           WHEN RIGHT(LEFT(E010_SERIAL_NBR, 10), 1) = '2' THEN 
             CASE 
               WHEN YEAR(E010_DELV_DT) < 2002 - 1 THEN '1972' 
               ELSE '2002' 
             END 
           WHEN RIGHT(LEFT(E010_SERIAL_NBR, 10), 1) = '3' THEN 
             CASE 
               WHEN YEAR(E010_DELV_DT) < 2003 - 1 THEN '1973' 
               ELSE '2003' 
             END 
           WHEN RIGHT(LEFT(E010_SERIAL_NBR, 10), 1) = '4' THEN 
             CASE 
               WHEN YEAR(E010_DELV_DT) < 2004 - 1 THEN '1974' 
               ELSE '2004' 
             END 
           WHEN RIGHT(LEFT(E010_SERIAL_NBR, 10), 1) = '5' THEN 
             CASE 
               WHEN YEAR(E010_DELV_DT) < 2005 - 1 THEN '1975' 
               ELSE '2005' 
             END 
           WHEN RIGHT(LEFT(E010_SERIAL_NBR, 10), 1) = '6' THEN 
             CASE 
               WHEN YEAR(E010_DELV_DT) < 2006 - 1 THEN '1976' 
               ELSE '2006' 
             END 
           WHEN RIGHT(LEFT(E010_SERIAL_NBR, 10), 1) = '7' THEN 
             CASE 
               WHEN YEAR(E010_DELV_DT) < 2007 - 1 THEN '1977' 
               ELSE '2007' 
             END 
           WHEN RIGHT(LEFT(E010_SERIAL_NBR, 10), 1) = '8' THEN 
             CASE 
               WHEN YEAR(E010_DELV_DT) < 2008 - 1 THEN '1978' 
               ELSE '2008' 
             END 
           WHEN RIGHT(LEFT(E010_SERIAL_NBR, 10), 1) = '9'THEN 
             CASE 
               WHEN YEAR(E010_DELV_DT) < 2009 - 1 THEN '1979' 
               ELSE '2009' 
             END 
           WHEN RIGHT(LEFT(E010_SERIAL_NBR, 10), 1) = 'H' THEN '1987' 
           WHEN RIGHT(LEFT(E010_SERIAL_NBR, 10), 1) = 'J' THEN '1988' 
           WHEN RIGHT(LEFT(E010_SERIAL_NBR, 10), 1) = 'K' THEN '1989' 
           WHEN RIGHT(LEFT(E010_SERIAL_NBR, 10), 1) = 'L' THEN '1990' 
           WHEN RIGHT(LEFT(E010_SERIAL_NBR, 10), 1) = 'M' THEN '1991' 
           WHEN RIGHT(LEFT(E010_SERIAL_NBR, 10), 1) = 'N' THEN '1992' 
           WHEN RIGHT(LEFT(E010_SERIAL_NBR, 10), 1) = 'P' THEN '1993' 
           WHEN RIGHT(LEFT(E010_SERIAL_NBR, 10), 1) = 'R' THEN '1994' 
           WHEN RIGHT(LEFT(E010_SERIAL_NBR, 10), 1) = 'S' THEN '1995' 
           WHEN RIGHT(LEFT(E010_SERIAL_NBR, 10), 1) = 'T' THEN '1996' 
           WHEN RIGHT(LEFT(E010_SERIAL_NBR, 10), 1) = 'V' THEN '1997' 
           WHEN RIGHT(LEFT(E010_SERIAL_NBR, 10), 1) = 'W' THEN '1998' 
           WHEN RIGHT(LEFT(E010_SERIAL_NBR, 10), 1) = 'X' THEN '1999' 
           WHEN RIGHT(LEFT(E010_SERIAL_NBR, 10), 1) = 'Y' THEN '2000' 
           WHEN RIGHT(LEFT(E010_SERIAL_NBR, 10), 1) = '0' THEN '1970' 
         END)
       when (E010_SER_MDL_YR = 0 and E010_MDL_YR <> 0 ) then
         ( case 
             when E010_MDL_YR > 18 then '19'||Cast(E010_MDL_YR as char(4)) 
             when E010_MDL_YR <= 18 
                  and E010_MDL_YR > 9 then '20'||Cast(E010_MDL_YR as char(4)) 
             when E010_MDL_YR > 0 
                  and E010_MDL_YR < 10 then '200'||Cast(E010_MDL_YR as char(4)) 
          end )
       else (
           case 
             when E010_SER_MDL_YR > 18 then '19'||Cast(E010_SER_MDL_YR as char(4)) 
             when E010_SER_MDL_YR <= 18 
                  and E010_SER_MDL_YR > 9 then '20'||Cast(E010_SER_MDL_YR as char(4)) 
             when E010_SER_MDL_YR > 0 
                  and E010_SER_MDL_YR < 10 then '200'||Cast(E010_SER_MDL_YR as char(4)) 
           end
       )
       end
       as NEW_MDL_YR
  FROM   VIVE.VIVEE010_VEHICLE
