-- FOS VIN Logic
select case
        when left(VEH_MANUF_CODE,1) = 'U'
        then 
         case substring(ltrim(rtrim(VEH_VIN_ID_NMBR)),2,1) 
          when '1'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'A')
          when '9'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'R')
          when '4'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, '7')
          else 
                 ltrim(rtrim(VEH_VIN_ID_NMBR))
          end 

        when left(VEH_MANUF_CODE,1) = 'J'
        then 
         case substring(ltrim(rtrim(VEH_VIN_ID_NMBR)),2,1)
          when '1'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'J')
          when '3'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'C')
          else 
                 ltrim(rtrim(VEH_VIN_ID_NMBR))
          end

        when left(VEH_MANUF_CODE,1)='3'
        then 
         case substring(ltrim(rtrim(VEH_VIN_ID_NMBR)),2,1)
          when '1'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'A')
          when '2'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'B')
          when '3'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'C')
          when '4'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'D')
          else 
                 ltrim(rtrim(VEH_VIN_ID_NMBR))
          end

        when left(VEH_MANUF_CODE,1)='4'
        then 
         case substring(ltrim(rtrim(VEH_VIN_ID_NMBR)),2,1)
          when '1'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'A')
          when '2'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'B')
          when '3'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'C')
          when '4'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'D')
          else 
                 ltrim(rtrim(VEH_VIN_ID_NMBR))
          end

        when left(VEH_MANUF_CODE,1)='7'
        then 
         case substring(ltrim(rtrim(VEH_VIN_ID_NMBR)),2,1)
          when '1'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'A')
          when '2'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'B')
          when '3'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'C')
          when '4'
                then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'D')
          else 
                ltrim(rtrim(VEH_VIN_ID_NMBR))
          end

        when left(VEH_MANUF_CODE,1)='1'
        then 
         case substring(ltrim(rtrim(VEH_VIN_ID_NMBR)),2,1)
          when '6'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'F')
          when '9'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'Z')
          when '3'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'L')
          when '4'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'M')
          else 
                 ltrim(rtrim(VEH_VIN_ID_NMBR))
          end

        when left(VEH_MANUF_CODE,1)='F'
        then 
         case substring(ltrim(rtrim(VEH_VIN_ID_NMBR)),2,1)
          when '6'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'F')
          when '9'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'Z')
          when '3'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'L')
          when '4'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'M')
          else
                 ltrim(rtrim(VEH_VIN_ID_NMBR))
         end

        when left(VEH_MANUF_CODE,2)='ML'
        then 
         case substring(ltrim(rtrim(VEH_VIN_ID_NMBR)),2,1)
          when '6'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'F')
          when '9'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'Z')
          when '3'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'L')
          when '4'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'M')
          else
                 ltrim(rtrim(VEH_VIN_ID_NMBR))
         end

        when left(VEH_MANUF_CODE,1)='C'
        then 
         case substring(ltrim(rtrim(VEH_VIN_ID_NMBR)),2,1)
          when '3'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'L')
          when '7'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'G')
          else 
                 ltrim(rtrim(VEH_VIN_ID_NMBR))
          end

        when left(VEH_MANUF_CODE,1)='D'
        then 
         case substring(ltrim(rtrim(VEH_VIN_ID_NMBR)),2,1)
          when '3'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'C')
          when '7'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'G')
          else 
                 ltrim(rtrim(VEH_VIN_ID_NMBR))
         end

        when left(VEH_MANUF_CODE,1)='B'
        then 
         case substring(ltrim(rtrim(VEH_VIN_ID_NMBR)),2,1)
          when '3'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'C')
          when '7'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'G')
          else 
                       ltrim(rtrim(VEH_VIN_ID_NMBR))
         end

        when left(VEH_MANUF_CODE,1)='2'
        then 
         case substring(ltrim(rtrim(VEH_VIN_ID_NMBR)),2,1)
          when '3'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'C')
          when '7'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'G')
          else 
                 ltrim(rtrim(VEH_VIN_ID_NMBR))
         end

        when left(VEH_MANUF_CODE,1)='G'
        then 
         case substring(ltrim(rtrim(VEH_VIN_ID_NMBR)),2,1)
          when '3'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'C')
          when '7'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'G')
          else 
                 ltrim(rtrim(VEH_VIN_ID_NMBR))
         end

        when left(VEH_MANUF_CODE,1)='H'
        then 
         case substring(ltrim(rtrim(VEH_VIN_ID_NMBR)),2,1)
          when '8'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'H')
          else 
                 ltrim(rtrim(VEH_VIN_ID_NMBR))
         end

        when left(VEH_MANUF_CODE,1)='X'
        then 
         case substring(ltrim(rtrim(VEH_VIN_ID_NMBR)),2,1)
          when '4'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'M')
          when '5'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'N')
          else 
                 ltrim(rtrim(VEH_VIN_ID_NMBR))
         end

        when left(VEH_MANUF_CODE,1)='A'
        then 
         case substring(ltrim(rtrim(VEH_VIN_ID_NMBR)),2,1)
          when '1'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'A')
          else 
                 ltrim(rtrim(VEH_VIN_ID_NMBR))
         end

        when left(VEH_MANUF_CODE,1)='Y'
        then 
         case substring(ltrim(rtrim(VEH_VIN_ID_NMBR)),2,1)
          when '5'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'N')
          when '7'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'X')
          else 
                 ltrim(rtrim(VEH_VIN_ID_NMBR))
         end

        when left(VEH_MANUF_CODE,1)='L'
        then 
         case substring(ltrim(rtrim(VEH_VIN_ID_NMBR)),2,1)
          when '4'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'M')
          when '8'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'Y')
          else
                 ltrim(rtrim(VEH_VIN_ID_NMBR))
         end

        when left(VEH_MANUF_CODE,1)='K'
        then 
         case substring(ltrim(rtrim(VEH_VIN_ID_NMBR)),2,1)
          when '1'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'A')
          else 
                 ltrim(rtrim(VEH_VIN_ID_NMBR))
         end

        when left(VEH_MANUF_CODE,1)='N'
        then 
         case substring(ltrim(rtrim(VEH_VIN_ID_NMBR)),2,1)
          when '5'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'N')
          else 
                 ltrim(rtrim(VEH_VIN_ID_NMBR))
         end

        when left(VEH_MANUF_CODE,1)='S'
        then 
         case substring(ltrim(rtrim(VEH_VIN_ID_NMBR)),2,1)
          when '6'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'F')
          when '2'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'S')
          else 
                 ltrim(rtrim(VEH_VIN_ID_NMBR))
         end

        when left(VEH_MANUF_CODE,1)='5'
        then 
         case substring(ltrim(rtrim(VEH_VIN_ID_NMBR)),2,1)
          when '2'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'S')
          when '3'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'L')
          else 
                 ltrim(rtrim(VEH_VIN_ID_NMBR))
         end

        when left(VEH_MANUF_CODE,1)='T'
        then 
         case substring(ltrim(rtrim(VEH_VIN_ID_NMBR)),2,1)
          when '5'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'N')
          when '3'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'T')
          when '8'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'Y')
          else 
                 ltrim(rtrim(VEH_VIN_ID_NMBR))
         end

        when left(VEH_MANUF_CODE,1)='W'
        then 
         case substring(ltrim(rtrim(VEH_VIN_ID_NMBR)),2,1)
          when '5'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'V')
          when '2'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'B')
          else 
                 ltrim(rtrim(VEH_VIN_ID_NMBR))
         end

        when left(VEH_MANUF_CODE,1)='V'
        then 
         case substring(ltrim(rtrim(VEH_VIN_ID_NMBR)),2,1)
          when '5'
                 then stuff(ltrim(rtrim(VEH_VIN_ID_NMBR)), 2, 1, 'V')
          else 
                 ltrim(rtrim(VEH_VIN_ID_NMBR))
         end
        else 
            ltrim(rtrim(VEH_VIN_ID_NMBR))
        end as Real_VIN      
  from poveh1 v
 where VEH_COUNTRY_CODE in ('US', 'CN', 'PR')
   and VEH_MODEL_YEAR >= 2007
   and VEH_RECORD_STATUS <> 'D'
   and not (VEH_VIN_ID_NMBR = '' or VEH_VIN_ID_NMBR is null)
   and not (VEH_HERTZ_UNIT_NMBR = '' or VEH_HERTZ_UNIT_NMBR is null)
   
   
   
-- Vision VIN Logic
    select 
       case
        when left(E010_MFR_CD,1) = 'U'
        then 
         case substring(ltrim(rtrim(E010_SERIAL_NBR)),2,1) 
          when '1'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'A')
          when '9'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'R')
          when '4'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, '7')
          else 
                 ltrim(rtrim(E010_SERIAL_NBR))
          end 

        when left(E010_MFR_CD,1) = 'J'
        then 
         case substring(ltrim(rtrim(E010_SERIAL_NBR)),2,1)
          when '1'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'J')
          when '3'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'C')
          else 
                 ltrim(rtrim(E010_SERIAL_NBR))
          end

        when left(E010_MFR_CD,1)='3'
        then 
         case substring(ltrim(rtrim(E010_SERIAL_NBR)),2,1)
          when '1'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'A')
          when '2'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'B')
          when '3'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'C')
          when '4'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'D')
          else 
                 ltrim(rtrim(E010_SERIAL_NBR))
          end

        when left(E010_MFR_CD,1)='4'
        then 
         case substring(ltrim(rtrim(E010_SERIAL_NBR)),2,1)
          when '1'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'A')
          when '2'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'B')
          when '3'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'C')
          when '4'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'D')
          else 
                 ltrim(rtrim(E010_SERIAL_NBR))
          end

        when left(E010_MFR_CD,1)='7'
        then 
         case substring(ltrim(rtrim(E010_SERIAL_NBR)),2,1)
          when '1'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'A')
          when '2'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'B')
          when '3'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'C')
          when '4'
                then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'D')
          else 
                ltrim(rtrim(E010_SERIAL_NBR))
          end

        when left(E010_MFR_CD,1)='1'
        then 
         case substring(ltrim(rtrim(E010_SERIAL_NBR)),2,1)
          when '6'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'F')
          when '9'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'Z')
          when '3'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'L')
          when '4'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'M')
          else 
                 ltrim(rtrim(E010_SERIAL_NBR))
          end

        when left(E010_MFR_CD,1)='F'
        then 
         case substring(ltrim(rtrim(E010_SERIAL_NBR)),2,1)
          when '6'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'F')
          when '9'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'Z')
          when '3'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'L')
          when '4'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'M')
          else
                 ltrim(rtrim(E010_SERIAL_NBR))
         end

        when left(E010_MFR_CD,2)='ML'
        then 
         case substring(ltrim(rtrim(E010_SERIAL_NBR)),2,1)
          when '6'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'F')
          when '9'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'Z')
          when '3'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'L')
          when '4'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'M')
          else
                 ltrim(rtrim(E010_SERIAL_NBR))
         end

        when left(E010_MFR_CD,1)='C'
        then 
         case substring(ltrim(rtrim(E010_SERIAL_NBR)),2,1)
          when '3'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'L')
          when '7'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'G')
          else 
                 ltrim(rtrim(E010_SERIAL_NBR))
          end

        when left(E010_MFR_CD,1)='D'
        then 
         case substring(ltrim(rtrim(E010_SERIAL_NBR)),2,1)
          when '3'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'C')
          when '7'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'G')
          else 
                 ltrim(rtrim(E010_SERIAL_NBR))
         end

        when left(E010_MFR_CD,1)='B'
        then 
         case substring(ltrim(rtrim(E010_SERIAL_NBR)),2,1)
          when '3'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'C')
          when '7'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'G')
          else 
                       ltrim(rtrim(E010_SERIAL_NBR))
         end

        when left(E010_MFR_CD,1)='2'
        then 
         case substring(ltrim(rtrim(E010_SERIAL_NBR)),2,1)
          when '3'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'C')
          when '7'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'G')
          else 
                 ltrim(rtrim(E010_SERIAL_NBR))
         end

        when left(E010_MFR_CD,1)='G'
        then 
         case substring(ltrim(rtrim(E010_SERIAL_NBR)),2,1)
          when '3'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'C')
          when '7'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'G')
          else 
                 ltrim(rtrim(E010_SERIAL_NBR))
         end

        when left(E010_MFR_CD,1)='H'
        then 
         case substring(ltrim(rtrim(E010_SERIAL_NBR)),2,1)
          when '8'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'H')
          else 
                 ltrim(rtrim(E010_SERIAL_NBR))
         end

        when left(E010_MFR_CD,1)='X'
        then 
         case substring(ltrim(rtrim(E010_SERIAL_NBR)),2,1)
          when '4'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'M')
          when '5'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'N')
          else 
                 ltrim(rtrim(E010_SERIAL_NBR))
         end

        when left(E010_MFR_CD,1)='A'
        then 
         case substring(ltrim(rtrim(E010_SERIAL_NBR)),2,1)
          when '1'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'A')
          else 
                 ltrim(rtrim(E010_SERIAL_NBR))
         end

        when left(E010_MFR_CD,1)='Y'
        then 
         case substring(ltrim(rtrim(E010_SERIAL_NBR)),2,1)
          when '5'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'N')
          when '7'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'X')
          else 
                 ltrim(rtrim(E010_SERIAL_NBR))
         end

        when left(E010_MFR_CD,1)='L'
        then 
         case substring(ltrim(rtrim(E010_SERIAL_NBR)),2,1)
          when '4'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'M')
          when '8'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'Y')
          else
                 ltrim(rtrim(E010_SERIAL_NBR))
         end

        when left(E010_MFR_CD,1)='K'
        then 
         case substring(ltrim(rtrim(E010_SERIAL_NBR)),2,1)
          when '1'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'A')
          else 
                 ltrim(rtrim(E010_SERIAL_NBR))
         end

        when left(E010_MFR_CD,1)='N'
        then 
         case substring(ltrim(rtrim(E010_SERIAL_NBR)),2,1)
          when '5'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'N')
          else 
                 ltrim(rtrim(E010_SERIAL_NBR))
         end

        when left(E010_MFR_CD,1)='S'
        then 
         case substring(ltrim(rtrim(E010_SERIAL_NBR)),2,1)
          when '6'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'F')
          when '2'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'S')
          else 
                 ltrim(rtrim(E010_SERIAL_NBR))
         end

        when left(E010_MFR_CD,1)='5'
        then 
         case substring(ltrim(rtrim(E010_SERIAL_NBR)),2,1)
          when '2'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'S')
          when '3'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'L')
          else 
                 ltrim(rtrim(E010_SERIAL_NBR))
         end

        when left(E010_MFR_CD,1)='T'
        then 
         case substring(ltrim(rtrim(E010_SERIAL_NBR)),2,1)
          when '5'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'N')
          when '3'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'T')
          when '8'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'Y')
          else 
                 ltrim(rtrim(E010_SERIAL_NBR))
         end

        when left(E010_MFR_CD,1)='W'
        then 
         case substring(ltrim(rtrim(E010_SERIAL_NBR)),2,1)
          when '5'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'V')
          when '2'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'B')
          else 
                 ltrim(rtrim(E010_SERIAL_NBR))
         end

        when left(E010_MFR_CD,1)='V'
        then 
         case substring(ltrim(rtrim(E010_SERIAL_NBR)),2,1)
          when '5'
                 then stuff(ltrim(rtrim(E010_SERIAL_NBR)), 2, 1, 'V')
          else 
                 ltrim(rtrim(E010_SERIAL_NBR))
         end
        else 
            ltrim(rtrim(E010_SERIAL_NBR))
        end as Real_VIN
  FROM GLOBAL_VISION_VIVEE010_VEHICLE v
