package com.hertz.mdm.vehicle.vindecode;

import java.sql.*;
import java.util.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


import com.hertz.mdm.vehicle.util.*;

public class ChromeVinDecodeParseMatch {

	private static final String CATALOG_ID = "NVD";
    private static Connection con = null;
    private static int optionPriceDeletedRowCount = 0;
    private static int optionPriceInsertedRowCount = 0;
    private static int techSpecDeletedRowCount = 0;
    private static int techSpecInsertedRowCount = 0;
    private static int vehicleColorUpdatedRowCount = 0;

	public static void main(String[] args) {  

		if(args.length != 4) {
			System.out.println("Usage: java ChromeVinDecodeParseMatch server database username password");
			System.exit(1);
		}
		
		// Create a variable for the connection string.  

		String connectionUrl =  "jdbc:sqlserver://" + args[0] + ";" +  
			"databaseName=" + args[1] + ";" +
			"user=" + args[2] + ";password=" + args[3];

		try {  
			// Establish the connection.  
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");  
			con = DriverManager.getConnection(connectionUrl);  

			merge_catalogVehicle();
			
			load_catalogVehicleOption();
			
			load_catalogVehicleCategoryRelationship();
			
			optionCode_Condition_Matching();
		}  

		// Handle any errors that may have occurred.  
		catch (Exception e) {  
			System.out.println("main():");
			e.printStackTrace();  
		}  
		finally {  
			if (con != null) try { con.close(); } catch(Exception e) {}  
		}  
	}  

	private static void merge_catalogVehicle()
	{
		int row_count = 0;
		Statement stmt = null;  
		String SQL = 
			"with row_vcode_styles as (\n" + 
			"    SELECT passthruId,\n" + 
			"           styleId,\n" + 
			"           styleName,\n" + 
			"           CASE WHEN styleName IS NULL THEN 0 ELSE LEN(styleName) END AS styleLength,\n" + 
			"           CASE WHEN styleName LIKE '% MAN%' THEN 1 ELSE 0 END AS manual_shift,\n" + 
			"           baseInvoice,\n" + 
			"           isFleetOnly\n" + 
			"      FROM [EBX_db_vhcl_vcode_styles]\n" + 
			"),\n" + 
			"selected_styles as (\n" + 
			"    select row_number() over (partition by passthruId order by manual_shift ASC, isFleetOnly DESC, baseInvoice ASC, styleLength ASC) AS rowNum,\n" + 
			"           passthruId, styleId\n" + 
			"      from row_vcode_styles\n" + 
			"),\n" +
			"vin_styles as (\r\n" + 
			"			    select v.vehicleIdentificationNumber, s.styleId, \r\n" + 
			"			           upper(ec.colorCode) as ext_color, upper(ic.colorCode) as int_color, \r\n" + 
			"			          row_number() over (partition by v.vehicleIdentificationNumber order by ec.colorCode desc, ic.colorCode desc ) as row_num\r\n" + 
			"			      from selected_styles s \r\n" + 
			"			      join [EBX_db_vhcl_vcode_vehicle_descriptions] v \r\n" + 
			"			        on s.passthruId = v.passthruId \r\n" + 
			"			       and rowNum = 1 \r\n" + 
			"			      left join [EBX_db_vhcl_vcode_exterior_colors] ec \r\n" + 
			"			        on v.passthruId = ec.passthruId \r\n" + 
			"			      left join [EBX_db_vhcl_vcode_interior_colors] ic \r\n" + 
			"			        on v.passthruId = ic.passthruId \r\n" + 
			")\n" +
			"merge [EBX_db_vhcl_vcode_catalog_vehicle] as T\n" + 
			"using (\n" + 
			"    select vehicleIdentificationNumber, styleId, ext_color, int_color,\n" + 
			"		   '" + CATALOG_ID + "' as catalog_, SYSTEM_USER as last_user_id, SYSDATETIME() as sys_date\n" + 
			"      from vin_styles\n" + 
			"     where row_num = 1\n" +
			") as S\n" + 
			"on T.vehicleIdentificationNumber = S.vehicleIdentificationNumber\n" + 
			"when NOT MATCHED BY TARGET then\n" + 
			"    insert (vehicleIdentificationNumber, style_, color__styleID, color__ext1Code, color__intCode, catalog_,\n" + 
			"            creationDate, lastUpdateDate, T_LAST_USER_ID, T_CREATOR_ID, T_CREATION_DATE)\n" + 
			"	values (S.vehicleIdentificationNumber, S.styleId, S.styleId, S.ext_color, S.int_color, S.catalog_,\n" + 
			"            S.sys_date, S.sys_date, S.last_user_id, S.last_user_id, S.sys_date)\n" + 
			"when MATCHED then \n" + 
			"    update set T.style_ = S.styleId,\n" + 
			"	           T.color__styleID = S.styleId,\n" + 
			"			   T.color__ext1Code = S.ext_color,\n" + 
			"			   T.color__intCode = S.int_color,\n" + 
			"			   T.color__condition = NULL,\n" + 
			"			   T.catalog_ = S.catalog_,\n" + 
			"			   T.lastUpdateDate = S.sys_date;";
		
		try {  
			stmt = con.createStatement();
			row_count = stmt.executeUpdate(SQL);
		}  

		catch (Exception e) {
			System.out.println("merge_catalogVehicle():");
			e.printStackTrace();  
		}  
		finally {  
			if (stmt != null) try { stmt.close(); } catch(Exception e) {}  
			System.out.println("Total " + row_count + " rows are loaded into Catalog Vehicle table");  
		}  
	}

	private static void load_catalogVehicleOption()
	{
		int row_count = 0;
		Statement stmt1 = null;  
		Statement stmt2 = null;  
		String SQL = null;
		
		try {
			// Delete the existing options before loading 
			SQL = 
				"delete from [EBX_db_vhcl_vcode_catalog_vehicle_options]\n" + 
				" where vehicleIdentificationNumber_ in (\n" + 
				"       select distinct v.vehicleIdentificationNumber\n" + 
				"         from [EBX_db_vhcl_vcode_options] o\n" + 
				"         join [EBX_db_vhcl_vcode_vehicle_descriptions] v\n" + 
				"          on o.passthruId = v.passthruId\n" + 
				"       )";
			stmt1 = con.createStatement();
			row_count = stmt1.executeUpdate(SQL);

			SQL = 
				"with raw_options as (\n" + 
				"    select passthruId, descriptions,\n" + 
				"         case\n" + 
				"		 when chromeCode is null or chromeCode = '' then\n" + 
				"		     case when oemCode is null or oemCode = '' then\n" + 
				"	             altOptionCode\n" + 
				"			 else\n" + 
				"			     oemCode\n" + 
				"			 end\n" + 
				"		 else \n" + 
				"		    chromeCode\n" + 
				"		 end as optionCode\n" + 
				"   from [EBX_db_vhcl_vcode_options]\n" + 
				")\n" + 
				"insert into [EBX_db_vhcl_vcode_catalog_vehicle_options]\n" + 
				"    ([vehicleIdentificationNumber_], [optionCode], [descriptions], [catalog_],\n" + 
				"	 [T_LAST_USER_ID], [T_CREATOR_ID], [T_CREATION_DATE]\n" + 
				"	)\n" + 
				"select v.vehicleIdentificationNumber, o.optionCode, o.descriptions, '" + CATALOG_ID + "',\n" + 
				"       SYSTEM_USER, SYSTEM_USER, SYSDATETIME()\n" + 
				"  from raw_options o\n" + 
				"  join [dbo].[EBX_db_vhcl_vcode_vehicle_descriptions] v\n" + 
				"    on o.passthruId = v.passthruId\n" + 
				"   and o.optionCode is not null";
			
			stmt2 = con.createStatement();
			row_count = stmt2.executeUpdate(SQL);

		}  

		catch (Exception e) {  
			System.out.println("load_catalogVehicleOption():");
			e.printStackTrace();  
		}  
		finally {  
			if (stmt1 != null) try { stmt1.close(); } catch(Exception e) {}  
			if (stmt2 != null) try { stmt2.close(); } catch(Exception e) {}  
			System.out.println("Total " + row_count + " rows are loaded into Catalog Vehicle Option table");  
		}  
	}

	private static void load_catalogVehicleCategoryRelationship()
	{
		int row_count = 0;
		Statement stmt1 = null;  
		Statement stmt2 = null;  
		String SQL = null;
		
		try {
			// Delete the existing options before loading 
			SQL = 
				"delete from [EBX_db_vhcl_vcode_catalog_vehicle_category_relationships]\n" + 
				" where vehicleIdentificationNumber_ in (\n" + 
				"       select v.vehicleIdentificationNumber\n" + 
				"         from [EBX_db_vhcl_vcode_generic_equipments] e\n" + 
				"         join [EBX_db_vhcl_vcode_vehicle_descriptions] v\n" + 
				"           on e.passthruId = v.passthruId\n" + 
				"	   )";
			stmt1 = con.createStatement();
			row_count = stmt1.executeUpdate(SQL);

			SQL = 
				"with vin_categories as (\n" +
				"  select v.vehicleIdentificationNumber, e.categoryId,\n" +
				"         row_number() over (partition by v.vehicleIdentificationNumber, e.categoryId" +
				"         order by v.vehicleIdentificationNumber, e.categoryId) as row_num\n" +
				"  from [dbo].[EBX_db_vhcl_vcode_generic_equipments] e\n" + 
				"  join [dbo].[EBX_db_vhcl_vcode_vehicle_descriptions] v\n" + 
				"    on e.passthruId = v.passthruId\n" +
				")\n" +
				"insert into [dbo].[EBX_db_vhcl_vcode_catalog_vehicle_category_relationships]\n" + 
				"    ([vehicleIdentificationNumber_], [category_], [catalog_],\n" + 
				"	 [T_LAST_USER_ID], [T_CREATOR_ID], [T_CREATION_DATE]\n" + 
				"	)\n" + 
				"select vehicleIdentificationNumber, categoryId, '" + CATALOG_ID + "',\n" + 
				"       SYSTEM_USER, SYSTEM_USER, SYSDATETIME()\n" + 
				"  from vin_categories\n" + 
				" where row_num = 1\n";
			
			stmt2 = con.createStatement();
			row_count = stmt2.executeUpdate(SQL);

		}  

		catch (Exception e) {  
			System.out.println("load_catalogVehicleCategoryRelationship():");
			e.printStackTrace();  
		}  
		finally {  
			if (stmt1 != null) try { stmt1.close(); } catch(Exception e) {}  
			if (stmt2 != null) try { stmt2.close(); } catch(Exception e) {}  
			System.out.println("Total " + row_count + " rows are loaded into Catalog Vehicle Category Relationship table");  
		}  
	}

	private static void optionCode_Condition_Matching()
	{
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		int vin_count = 0;
		Statement stmt = null;  
		String SQL = 
			"select vehicleIdentificationNumber, color__styleID, color__ext1Code, color__intCode\n" + 
			"  from [EBX_db_vhcl_vcode_catalog_vehicle]\n" + 
			" where color__condition is null";
		
		try {  
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			
			while( rs.next() )
			{
				String vin = rs.getString(1);
				int    style = rs.getInt(2);
				String ext_color = rs.getString(3);
				String int_color = rs.getString(4);

				vin_count++;
				
				ChromeOptionCodes vehicleOptionCodes = fetchChromeOptionCodes(vin);
				
				load_catalogVehicleOptionPriceRelationship(vin, style, vehicleOptionCodes);

				load_catalogVehicleTechSpecRelationship(vin, style, vehicleOptionCodes);
				
				// Keep this as the last step
				update_catalogVehicle_colorCondition(vin, style, ext_color, int_color, vehicleOptionCodes);

				if (vin_count % 500 == 0) {
					System.out.println(df.format(new Date()) + ": " + vin_count + " VIN numbers processed");
					System.out.println( "    " + optionPriceInsertedRowCount + " rows inserted to Option Price Relationship table");
					System.out.println( "    " + techSpecInsertedRowCount + " rows inserted to Tech Spec Relationship table");
					System.out.println( "    " + vehicleColorUpdatedRowCount + " rows updated with color info to Catelog Vehicle table");
				}
			}
		}
		catch (Exception e) {  
			System.out.println("optionCode_Condition_Matching():");
			e.printStackTrace();  
		}  
		finally {  
			if (stmt != null) try { stmt.close(); } catch(Exception e) {}  
		}  
		System.out.println("Total " + optionPriceDeletedRowCount + " rows are deleted from Catalog Vehicle Option Price Relationship table");  
		System.out.println("Total " + optionPriceInsertedRowCount + " rows are inserted into Catalog Vehicle Option Price Relationship table");  

		System.out.println("Total " + techSpecDeletedRowCount + " rows are deleted from Catalog Vehicle Tech Spec Relationship table");  
		System.out.println("Total " + techSpecInsertedRowCount + " rows are inserted into Catalog Vehicle Tech Spec Relationship table");

		System.out.println("Total " + vehicleColorUpdatedRowCount + " rows are updated in Catalog Vehicle for for Vehicle Colors");
	}

	private static ChromeOptionCodes fetchChromeOptionCodes(String vin)
	{
		int row_count = 0;
		Statement stmt = null;  
		String SQL = 
			"select optionCode\n" + 
			"  from [EBX_db_vhcl_vcode_catalog_vehicle_options]\n" + 
			" where vehicleIdentificationNumber_ = '" + vin + "'"; 
		
		List<String> rawOptionCodes = new ArrayList<>();
		
		try {  
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			
			for(row_count = 0; rs.next(); row_count++ )
			{
				String optionCode = rs.getString(1);
				rawOptionCodes.add(optionCode);
			}
		}
		catch (Exception e) {  
			System.out.println("fetchChromeOptionCodes():");
			e.printStackTrace();  
		}  
		finally {  
			if (stmt != null) try { stmt.close(); } catch(Exception e) {}  
		}
		if( row_count > 0) {
			return new ChromeOptionCodes(rawOptionCodes);
		}
		else {
			return ChromeOptionCodes.NONE;
		}
	}


	
	private static void load_catalogVehicleOptionPriceRelationship(String vin, int style, ChromeOptionCodes vehicleOptionCodes)
	{
		Statement queryStmt = null;  
		Statement insertStmt = null;  
		String querySQL = 
			"select p.sequence00, p.optionCode, p.condition\n" + 
			"  from [EBX_db_vhcl_vcode_catalog_vehicle_options] o\n" + 
			"  join [EBX_db_vhclref_prices] p\n" + 
			"    on p.optionCode = o.optionCode\n" + 
			"   and o.vehicleIdentificationNumber_ = '" + vin + "'\n" + 
			"   and p.styleID_ = " + style + "\n" + 
			" order by p.optionCode, p.sequence00";
		String insertSQL;
		
		ChromeOptionEvaluator conditionMatcher = new ChromeOptionEvaluator();
		
		optionPriceDeletedRowCount += deleteExistingOptionPrice(vin);
		
		try {  
			queryStmt = con.createStatement();
			ResultSet rs = queryStmt.executeQuery(querySQL);
			insertStmt = con.createStatement();

			String prevOptionCode = "?";
			boolean foundPrice = false;
			
			while( rs.next() )
			{
				int    priceSequence  = rs.getInt(1);
				String optionCode     = rs.getString(2);
				String priceCondition = rs.getString(3);
				
				if ( !optionCode.equals(prevOptionCode) ) {
					prevOptionCode = optionCode;
					foundPrice = false;
				}
				
				if (!foundPrice) {
					if (conditionMatcher.isMatch(priceCondition, vehicleOptionCodes)) {
						foundPrice = true;

						insertSQL = 
							"insert into EBX_db_vhcl_vcode_catalog_vehicle_option_price_relationships\n" +
							"    (vehicleIdentificationNumber_, price__styleID, price__sequence, price__optionCode, catalog_,\n" +
							"     T_LAST_USER_ID, T_CREATOR_ID, T_CREATION_DATE)\n" +
							"values('" + vin + "', " + style + ", " + priceSequence + ", '" + optionCode + "', '" + CATALOG_ID + "', \n" +
							"SYSTEM_USER, SYSTEM_USER, SYSDATETIME())";
						
						optionPriceInsertedRowCount += insertStmt.executeUpdate(insertSQL);
					}
				}
			}
		}  

		catch (Exception e) {  
			System.out.println("load_catalogVehicleOptionPriceRelationship():");
			e.printStackTrace();  
		}  
		finally {  
			if (queryStmt != null) try { queryStmt.close(); } catch(Exception e) {}  
			if (insertStmt != null) try { insertStmt.close(); } catch(Exception e) {}  
		}  
		
    }

	private static int deleteExistingOptionPrice(String vin)
	{
		int row_cnt = 0;
		Statement stmt = null;
		String SQL = 
			"delete from EBX_db_vhcl_vcode_catalog_vehicle_option_price_relationships\n" + 
			" where vehicleIdentificationNumber_ = '" + vin + "'" ;
		
		try {  
			stmt = con.createStatement();
			row_cnt = stmt.executeUpdate(SQL);
		}  

		catch (Exception e) {  
			System.out.println("deleteExistingOptionPrice():");
			e.printStackTrace();  
		}  
		finally {  
			if (stmt != null) try { stmt.close(); } catch(Exception e) {}  
		}
		return(row_cnt);
	}

	private static void load_catalogVehicleTechSpecRelationship(String vin, int style, ChromeOptionCodes vehicleOptionCodes)
	{
		Statement queryStmt = null;  
		Statement insertStmt = null;  
		String querySQL = 
			"select titleID_, sequence00, condition\n" + 
			"  from [EBX_db_vhclref_tech_specs]\n" + 
			" where styleID_ = '" + style + "'\n" + 
			" order by titleID_, sequence00";
		String insertSQL;
		
		ChromeOptionEvaluator conditionMatcher = new ChromeOptionEvaluator();
		
		techSpecDeletedRowCount += deleteExistingTechSpec(vin);
		
		try {  
			queryStmt = con.createStatement();
			ResultSet rs = queryStmt.executeQuery(querySQL);
			insertStmt = con.createStatement();

			int prevTitleId = -1;
			boolean foundTitle = false;
			
			while( rs.next() )
			{
				int titleId           = rs.getInt(1);
				int titleSequence     = rs.getInt(2);
				String titleCondition = rs.getString(3);
				
				if ( titleId != prevTitleId ) {
					prevTitleId = titleId;
					foundTitle = false;
				}
				
				if (!foundTitle) {
					if (conditionMatcher.isMatch(titleCondition, vehicleOptionCodes)) {
						foundTitle = true;

						insertSQL = 
							"insert into EBX_db_vhcl_vcode_catalog_vehicle_tech_spec_relationships\n" +
							"    (vehicleIdentificationNumber_, techSpec__styleID, techSpec__titleID, techSpec__sequence, catalog_,\n" +
							"     T_LAST_USER_ID, T_CREATOR_ID, T_CREATION_DATE)\n" +
							"values('" + vin + "', " + style + ", " + titleId + ", " + titleSequence + ", '" + CATALOG_ID + "', \n" +
							"SYSTEM_USER, SYSTEM_USER, SYSDATETIME())";
						
						techSpecInsertedRowCount += insertStmt.executeUpdate(insertSQL);
					}
				}
			}
		}  

		catch (Exception e) {  
			System.out.println("load_catalogVehicleTechSpecRelationship():");
			e.printStackTrace();  
		}  
		finally {  
			if (queryStmt != null) try { queryStmt.close(); } catch(Exception e) {}  
			if (insertStmt != null) try { insertStmt.close(); } catch(Exception e) {}  
		}  
		
    }

	private static int deleteExistingTechSpec(String vin)
	{
		int row_cnt = 0;
		Statement stmt = null;  
		String SQL = 
			"delete from EBX_db_vhcl_vcode_catalog_vehicle_tech_spec_relationships\n" + 
			" where vehicleIdentificationNumber_ = '" + vin + "'" ;
		
		try {  
			stmt = con.createStatement();
			row_cnt = stmt.executeUpdate(SQL);
		}  

		catch (Exception e) {
			System.out.println("deleteExistingTechSpec():");
			e.printStackTrace();  
		}  
		finally {  
			if (stmt != null) try { stmt.close(); } catch(Exception e) {}  
		}
		return(row_cnt);
	}

	private static void update_catalogVehicle_colorCondition(String vin, int style, String ext_color, String int_color, ChromeOptionCodes vehicleOptionCodes)
	{
		Statement queryStmt = null;  
		Statement updateStmt = null;  
		String querySQL = 
			"select ext1Code, ext2Code, intCode, condition\n" + 
			"  from [EBX_db_vhclref_colors]\n" + 
			" where styleID_ = " + style + "\n" + 
			"   and (case when ext1ManCode is null then ext1Code else ext1ManCode end = '" + ext_color + "')\n" + 
			"   and (case when intManCode is null then intCode else intManCode end = '" + int_color + "')";
		String updateSQL;
		
		ChromeOptionEvaluator conditionMatcher = new ChromeOptionEvaluator();
		
		try {  
			queryStmt = con.createStatement();
			ResultSet rs = queryStmt.executeQuery(querySQL);
			updateStmt = con.createStatement();

			while( rs.next() )
			{
				String ext1Code       = rs.getString(1);
				String ext2Code       = rs.getString(2);
				String intCode        = rs.getString(3);
				String colorCondition = rs.getString(4);
				
				if (conditionMatcher.isMatch(colorCondition, vehicleOptionCodes)) {

					updateSQL = 
							"update EBX_db_vhcl_vcode_catalog_vehicle\n" +
							"   set color__ext1Code = '" + ext1Code + "',\n" +
							"       color__ext2Code = '" + ext2Code + "',\n" +
							"       color__intCode = '" + intCode + "',\n" +
							"       color__condition = '" + colorCondition + "',\n" +
							"       T_LAST_USER_ID = SYSTEM_USER,\n" +
							"       T_CREATION_DATE = SYSDATETIME()\n" +
							" where vehicleIdentificationNumber = '" + vin + "'";
						
					vehicleColorUpdatedRowCount += updateStmt.executeUpdate(updateSQL);
					
					break;
				}
			}
		}  

		catch (Exception e) {  
			System.out.println("update_catalogVehicle_colorCondition():");
			e.printStackTrace();  
		}  
		finally {  
			if (queryStmt != null) try { queryStmt.close(); } catch(Exception e) {}  
			if (updateStmt != null) try { updateStmt.close(); } catch(Exception e) {}  
		}  
		
	}

}
