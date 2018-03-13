package com.hertz.mdm.exporter.location;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.PrimaryKey;
import com.onwbp.adaptation.Request;
import com.onwbp.adaptation.RequestResult;
import com.hertz.mdm.location.path.LocationPaths;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.service.LoggingCategory;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.Session;

/**
 * An exporter for the PMF Function table that outputs all leaves of the table along with a flattened
 * representation of their complete path to the root. There is a specific format this conforms
 * to. Refer to a sample output file.
 */
public class LocationFlatTableExporter {
	protected static final LoggingCategory LOG = LoggingCategory.getKernel();
	
	private static final Path addressTablePath=Path.parse("/root/LocationData/Address");
	private static final Path locationTablePath=Path.parse("/root/Location");
	
	//private static final Path LOCATION_FK_PATH = Path.parse("./location");
	private static final Path addressTypePath=Path.parse("/type");
	private static final Path primarySecondaryPath=Path.parse("/primarySecondary");
    private static final Path addressLine1Path = Path.parse("/Address/addressLine1");
    private static final Path addressLine2Path = Path.parse("/Address/addressLine2");
	
	
	String addressdetail="";
	/**
	 * This stores all the records from the table for faster lookup.
	 * Key = the primary key of the record, Value = the record itself.
	 */
	protected Map<String, Adaptation> allRecordsAddressMap;
	protected Map<PrimaryKey, Adaptation> allRecordsLocationMap;
	protected ArrayList<Adaptation> allAddress;
	protected ArrayList<Adaptation> allLocation;
	public void exportTable(Session session,AdaptationTable table )
			throws IOException, OperationException {
		System.out.println("Inside Export Table Function");
		LOG.info("Inside Export Table Function");
		AdaptationTable addressTable=table.getContainerAdaptation().getTable(addressTablePath);
		AdaptationTable locationTable=table.getContainerAdaptation().getTable(locationTablePath);
		System.out.println("Location Table--------------"+locationTablePath);
		System.out.println("Address Table"+addressTablePath);
		LocationAllRecordsMap(locationTable);
		//AddressAllRecordsMap(addressTable);
		
		final String FILENAME="C:\\Users\\aanand\\Desktop\\Location\\export\\location_data.txt";
		BufferedWriter writer = null;
		FileWriter filewriter = null;
		filewriter = new FileWriter(FILENAME);
		writer = new BufferedWriter(filewriter);
		//String headerRow="Id|Intelligent Location Id|Location Code|Location Country|Location City Code|Address Line 1|Address Line 2|Type|Primary/Secondary\n";
		//writer.write(headerRow);
		Iterator<Adaptation> locationIterator=allLocation.iterator();
		System.out.println(allLocation.size());
		try {
			while(locationIterator.hasNext())
			{
				System.out.println(locationIterator.toString());
				System.out.println(allLocation.size());
				String rowStr="";
				rowStr= createRow(locationIterator.next(),addressTable);
				writer.write(rowStr);
			    writer.newLine();
		}		
		} 
		catch(Exception e)
		{
			LOG.info(e.getMessage());
		}finally {
			writer.close();
		}
	}
	
	protected String createRow(Adaptation locationrecord,AdaptationTable addressTable)
			throws OperationException {	
		final StringBuffer strBuff = new StringBuffer();
		
		final char separator = '|';
		int locationid=locationrecord.get_int(LocationPaths._Root_Location._Id);
		String intelligentLocationId=locationrecord.getString(LocationPaths._Root_Location._IntelligentLocationId);
		String locationCode=locationrecord.getString(LocationPaths._Root_Location._LocationCode);
		String locationCtry=locationrecord.getString(LocationPaths._Root_Location._LocationInformation__locationCountry);
		String locationCity=locationrecord.getString(LocationPaths._Root_Location._LocationInformation_LocationCity);
		strBuff.append(locationid);
		
		strBuff.append(separator);
		strBuff.append(intelligentLocationId);
		strBuff.append(separator);
		strBuff.append(locationCode);
		strBuff.append(separator);
		strBuff.append(locationCtry);
		strBuff.append(separator);
		strBuff.append(locationCity);
		strBuff.append(separator);
		System.out.println("Location Data"+strBuff.toString());
		addressdetail=createAddressRow(locationid,addressTable);
		strBuff.append(addressdetail);
		System.out.println("Appended Data"+strBuff.toString());
		return strBuff.toString();
	}
	
	protected String createAddressRow(int locationid,AdaptationTable addressTable)
			throws OperationException {
		final char separator = '|';
		final StringBuffer strBuff = new StringBuffer();
		final String predicate=LocationPaths._Root_LocationData_Address._Location.format()+" = '" + locationid + "'";
		RequestResult res = addressTable.createRequestResult(predicate);
		for (Adaptation record; (record = res.nextAdaptation()) != null;) {
			System.out.println("Adaptation Record in address row"+record.toString());
			String addressLine1 = record.getString(addressLine1Path);
			String addressLine2 = record.getString(addressLine2Path);
			String addressType=record.getString(addressTypePath);
			String primarySecondry=record.getString(primarySecondaryPath);
			strBuff.append(addressType);
			strBuff.append(separator);
			strBuff.append(primarySecondry);
			strBuff.append(separator);
			strBuff.append(addressLine1);
			strBuff.append(separator);
			strBuff.append(addressLine2);
			strBuff.append(separator);
		}
		
		return strBuff.toString();
	}
		
	private void LocationAllRecordsMap(final AdaptationTable table) {
		
		//allRecordsLocationMap = new HashMap<PrimaryKey, Adaptation>();
		// If you specify no predicate, it will request every record in the table
		// (that meets any filter you set)
		allLocation=new ArrayList<Adaptation>();
		final Request request = table.createRequest();
		final RequestResult reqRes = request.execute();
				try {
			// Loop through all the results and put each into the map
			for (Adaptation adaptation; (adaptation = reqRes.nextAdaptation()) != null;) {
				//allRecordsLocationMap.put(adaptation.getOccurrencePrimaryKey(), adaptation);
				allLocation.add(adaptation);
			}
		} finally {
			reqRes.close();
		}
		
	}
}