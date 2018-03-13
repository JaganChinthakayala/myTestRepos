package com.hertz.mdm.location.scripttask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.hertz.mdm._hertz.constants.HtzConstants;
import com.hertz.mdm._hertz.util.CallAPI;
import com.hertz.mdm._hertz.util.HtzUtilities;
import com.hertz.mdm.location.constants.LocationConstants;
import com.hertz.mdm.location.integration.bean.Location;
import com.hertz.mdm.location.integration.bean.LocationAddress;
import com.hertz.mdm.location.integration.bean.LocationEMailAddress;
import com.hertz.mdm.location.integration.bean.LocationMessage;
import com.hertz.mdm.location.integration.bean.LocationPhone;
import com.hertz.mdm.location.path.LocationPaths;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationHome;
import com.onwbp.adaptation.AdaptationName;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.RequestResult;
import com.onwbp.com.google.gson.GsonBuilder;
import com.orchestranetworks.instance.HomeKey;
import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.service.LoggingCategory;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.Session;

public class PublishUtil
{

	private MessageHeader header = new MessageHeader();
	private Object body;

	public static void publishToMuleSoft(
		Session session,
		AdaptationHome dataSpace,
		Adaptation dataSet,
		Map<AdaptationTable, Set<Adaptation>> createDeltaAdaptationMap,
		Map<AdaptationTable, Set<Adaptation>> updateDeltaAdaptationMap,
		Map<String, String> deleteDeltaAdaptationMap) throws OperationException
	{
		LoggingCategory.getKernel().info(
			"[PublishUtil] Entered Publish utility to construct JSON objects and publish objects to MulesSoft.");
		AdaptationHome referenceDataSpace = Repository.getDefault()
			.lookupHome(HomeKey.forBranchName(LocationConstants.LOCATION_DATA_SPACE));
		if (referenceDataSpace == null)
		{
			throw OperationException.createError(
				"DataSpace " + LocationConstants.LOCATION_DATA_SPACE + " has not been found");
		}
		Adaptation referenceDataSet = referenceDataSpace.findAdaptationOrNull(
			AdaptationName.forName(LocationConstants.LOCATION_REFERENCE_DATA_SET));
		if (referenceDataSet == null)
		{
			throw OperationException.createError(
				"DataSet " + LocationConstants.LOCATION_REFERENCE_DATA_SET + " has not been found");
		}

		// handle created records
		if (createDeltaAdaptationMap != null && !createDeltaAdaptationMap.isEmpty())
		{
			for (Map.Entry<AdaptationTable, Set<Adaptation>> entry : createDeltaAdaptationMap
				.entrySet())
			{
				AdaptationTable contentTable = entry.getKey();
				Set<Adaptation> contentAdaptationSet = entry.getValue();

				if (contentTable.getTablePath()
					.equals(LocationPaths._Root_Location.getPathInSchema()))
				{
					if (contentAdaptationSet != null && !contentAdaptationSet.isEmpty())
					{
						for (Iterator<Adaptation> iter = contentAdaptationSet.iterator(); iter
							.hasNext();)
						{
							Adaptation adaptation = iter.next();

							//TODO - Create a LocationMessage Java Bean
							LocationMessage locationMessageContainer = constructJsonForLocation(
								adaptation,
								HtzConstants.CREATE);

							try
							{
								// send the JSON message to End Point
								sendLocationMessage(locationMessageContainer, "Create");
							}
							catch (Exception e)
							{
								e.printStackTrace();
							}
						}
					}
				}
			}
		}

		// handle updated records
		if (updateDeltaAdaptationMap != null && !updateDeltaAdaptationMap.isEmpty())
		{
			for (Map.Entry<AdaptationTable, Set<Adaptation>> entry : updateDeltaAdaptationMap
				.entrySet())
			{
				AdaptationTable contentTable = entry.getKey();
				Set<Adaptation> contentAdaptationSet = entry.getValue();

				if (contentTable.getTablePath()
					.equals(LocationPaths._Root_Location.getPathInSchema()))
				{
					if (contentAdaptationSet != null && !contentAdaptationSet.isEmpty())
					{
						for (Iterator<Adaptation> iter = contentAdaptationSet.iterator(); iter
							.hasNext();)
						{
							Adaptation adaptation = iter.next();
							LocationMessage locationMessageContainer = constructJsonForLocation(
								adaptation,
								HtzConstants.UPDATE);

							try
							{
								// send the JSON message to End Point
								sendLocationMessage(locationMessageContainer, "Update");
							}
							catch (Exception e)
							{
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}

	public static LocationMessage constructJsonForLocation(
		Adaptation adaptation,
		String operationType)
	{
		LocationMessage locationMessageContainer = new LocationMessage();
		Location locationContainer = new Location();
		locationMessageContainer.setSchema(HtzConstants.SCHEMA_DEFINITION); //Get from Tharun
		locationMessageContainer.setOperationType(operationType);

		//Set Attributes
		locationContainer
			.setId((String) adaptation.get(LocationPaths._Root_Location._Id).toString());
		locationContainer.setName(adaptation.getString(LocationPaths._Root_Location._Name));
		locationContainer
			.setLocationNumber(adaptation.getString(LocationPaths._Root_Location._LocationNumber));

		//Set Foreign Keys
		if (adaptation
			.get(LocationPaths._Root_Location._LocationInformation__locationCountry) != null)
		{
			locationContainer.setLocationCountry(
				(String) adaptation
					.get(LocationPaths._Root_Location._LocationInformation__locationCountry)
					.toString());
		}
		if (adaptation.get(LocationPaths._Root_Location._RentalCarBrand) != null)
		{
			locationContainer.setRentalCarBrand(
				adaptation.get(LocationPaths._Root_Location._RentalCarBrand).toString());
		}

		//Set Location Addresses
		locationContainer.setAddresses(getLocationAddresses(adaptation));
		locationContainer.seteMailAddresses(getLocationEMailAddresses(adaptation));
		locationContainer.setPhones(getLocationPhones(adaptation));

		locationMessageContainer.setLocation(locationContainer);

		return locationMessageContainer;
	}

	public static ArrayList<LocationAddress> getLocationAddresses(Adaptation adaptation)
	{
		ArrayList<LocationAddress> locationAddresses = new ArrayList<LocationAddress>();

		AdaptationTable locationAddressTable = adaptation.getContainer()
			.getTable(LocationPaths._Root_LocationData_Address.getPathInSchema());

		String locationId = adaptation.get(LocationPaths._Root_Location._Id).toString();

		// Query the LocatinAddress table for Location Addresses
		RequestResult requestResult = locationAddressTable.createRequestResult(
			LocationPaths._Root_LocationData_Address._Location.format() + "='" + locationId
				+ "' and " + LocationPaths._Root_LocationData_Address._IsRecordActive.format()
				+ "=true");

		if (!requestResult.isEmpty())
		{
			for (int i = 0; i < requestResult.getSize(); i++)
			{
				Adaptation adaptationAddress = requestResult.nextAdaptation();

				LocationAddress locationAddress = new LocationAddress();

				locationAddress.setId(
					(String) adaptationAddress.get(LocationPaths._Root_LocationData_Address._Id)
						.toString());
				locationAddress.setLocation(
					(String) adaptationAddress
						.get(LocationPaths._Root_LocationData_Address._Location).toString());
				locationAddress.setType(
					adaptationAddress.getString(LocationPaths._Root_LocationData_Address._Type));
				locationAddress.setAddressLine1(
					adaptationAddress
						.getString(LocationPaths._Root_LocationData_Address._Address_AddressLine1));
				locationAddress.setCity(
					adaptationAddress
						.getString(LocationPaths._Root_LocationData_Address._Address_City));
				locationAddress.setPostalCode(
					adaptationAddress
						.getString(LocationPaths._Root_LocationData_Address._Address_PostalCode));
				locationAddress.setCountry(
					(String) adaptationAddress
						.get(LocationPaths._Root_LocationData_Address._Address_Country).toString());
				locationAddress.setStateProvince(
					(String) adaptationAddress
						.get(LocationPaths._Root_LocationData_Address._Address_StateProvince)
						.toString());

				locationAddresses.add(locationAddress);
			}
		}
		return locationAddresses;
	}

	public static ArrayList<LocationEMailAddress> getLocationEMailAddresses(Adaptation adaptation)
	{
		ArrayList<LocationEMailAddress> locationEMailAddresses = new ArrayList<LocationEMailAddress>();

		AdaptationTable locationEMailAddressTable = adaptation.getContainer()
			.getTable(LocationPaths._Root_LocationData_EMailAddress.getPathInSchema());

		String locationId = adaptation.get(LocationPaths._Root_Location._Id).toString();

		// Query the LocatinEMailAddress table for Location EMail Addresses
		RequestResult requestResult = locationEMailAddressTable.createRequestResult(
			LocationPaths._Root_LocationData_Address._Location.format() + "='" + locationId
				+ "' and " + LocationPaths._Root_LocationData_Address._IsRecordActive.format()
				+ "=true");

		if (!requestResult.isEmpty())
		{
			for (int i = 0; i < requestResult.getSize(); i++)
			{
				LocationEMailAddress locationEMailAddress = new LocationEMailAddress();

				Adaptation adaptationAddress = requestResult.nextAdaptation();

				locationEMailAddress.setId(
					(String) adaptationAddress.get(LocationPaths._Root_LocationData_Address._Id)
						.toString());
				locationEMailAddress.setLocation(
					(String) adaptationAddress
						.get(LocationPaths._Root_LocationData_EMailAddress._Location).toString());
				locationEMailAddress.seteMailAddressType(
					(String) adaptationAddress
						.get(LocationPaths._Root_LocationData_EMailAddress._EMailAddressType));
				locationEMailAddress.seteMailAddress(
					adaptationAddress
						.getString(LocationPaths._Root_LocationData_EMailAddress._EMailAddress));

				locationEMailAddresses.add(locationEMailAddress);
			}
		}
		return locationEMailAddresses;
	}

	public static ArrayList<LocationPhone> getLocationPhones(Adaptation adaptation)
	{
		ArrayList<LocationPhone> locationPhones = new ArrayList<LocationPhone>();

		AdaptationTable locationPhoneTable = adaptation.getContainer()
			.getTable(LocationPaths._Root_LocationData_Phone.getPathInSchema());

		String locationId = adaptation.get(LocationPaths._Root_Location._Id).toString();

		// Query the LocatinPhones table for Location Phones
		RequestResult requestResult = locationPhoneTable.createRequestResult(
			LocationPaths._Root_LocationData_Address._Location.format() + "='" + locationId
				+ "' and " + LocationPaths._Root_LocationData_Address._IsRecordActive.format()
				+ "=true");

		if (!requestResult.isEmpty())
		{
			for (int i = 0; i < requestResult.getSize(); i++)
			{
				Adaptation adaptationAddress = requestResult.nextAdaptation();

				LocationPhone locationPhone = new LocationPhone();

				locationPhone.setId(
					(String) adaptationAddress.get(LocationPaths._Root_LocationData_Address._Id)
						.toString());
				locationPhone.setLocation(
					(String) adaptationAddress.get(LocationPaths._Root_LocationData_Phone._Location)
						.toString());
				locationPhone.setPhoneType(
					(String) adaptationAddress
						.get(LocationPaths._Root_LocationData_Phone._PhoneType));
				locationPhone.setCountryCode(
					adaptationAddress.getString(
						LocationPaths._Root_LocationData_Phone._PhoneNumber_CountryCode));
				locationPhone.setAreaCode(
					adaptationAddress
						.getString(LocationPaths._Root_LocationData_Phone._PhoneNumber_AreaCode));
				locationPhone.setLocalNumber(
					adaptationAddress.getString(
						LocationPaths._Root_LocationData_Phone._PhoneNumber_LocalNumber));

				locationPhones.add(locationPhone);
			}
		}
		return locationPhones;
	}

	/*
	 * Add location message data to standard header/body message structure
	 */
	public static void sendLocationMessage(LocationMessage locationMsg, String routingKey)
		throws Exception
	{
		PublishUtil msgObj = new PublishUtil();

		msgObj.header.messageType = "LOCATION";
		msgObj.body = locationMsg;
		String jsonContent = new GsonBuilder().setPrettyPrinting().create().toJson(msgObj);

		sendMessage(jsonContent, routingKey);
	}

	/*
	 * Send JSON message to MuleSoft API endpoint
	 */
	public static void sendMessage(String message, String routingKey) throws Exception
	{
		Properties properties = HtzUtilities.getProperties();

		String apiURL = properties.getProperty("apiURL");

		if (StringUtils.isNotEmpty(apiURL))
		{
			// this is needed because Java 7 defaults to TLS1.0 which the MuleSoft endpoint doesn't support
			System.setProperty("https.protocols", "TLSv1.1,TLSv1.2");

			System.out.println("JSON Message = " + message);
			Map<String, String> requestProperties = new HashMap<String, String>();
			requestProperties.put("Content-Type", "application/json");
			requestProperties.put("client_id", properties.getProperty("client_id"));
			requestProperties.put("client_secret", properties.getProperty("client_secret"));

			CallAPI.post(apiURL, requestProperties, message);
		}
	}

	private class MessageHeader
	{
		public String messageType;
		public String messageId = UUID.randomUUID().toString();
		public String timestamp;

		public MessageHeader()
		{
			SimpleDateFormat dtf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss:SSSXXX");

			this.timestamp = dtf.format(new Date());
		}
	}
}
