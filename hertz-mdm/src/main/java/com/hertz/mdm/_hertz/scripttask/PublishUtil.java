package com.hertz.mdm._hertz.scripttask;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hertz.mdm._hertz.constants.HtzConstants;
import com.hertz.mdm.location.integration.bean.Location;
import com.hertz.mdm.location.path.LocationPaths;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationHome;
import com.onwbp.adaptation.AdaptationName;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.com.google.gson.GsonBuilder;
import com.orchestranetworks.instance.HomeKey;
import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.service.LoggingCategory;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.Session;

public class PublishUtil
{

	//Common Reference Dataspace and Dataset
	public static final String REFERENCE_DATASPACE_ID = "VcmCommonDataSpace";
	public static final String REFERENCE_DATASET_ID = "VcmCommonReferenceDataSet";
	//RabbitMQ Queue name to publish the messages
	//private final static String QUEUE_NAME = "GCC_QUEUE";

	/**
	 * 
	 * @param dataSpace
	 * @param dataSet
	 * @param createDeltaAdaptationMap
	 * @param updateDeltaAdaptationMap
	 * @param deleteDeltaAdaptationMap
	 * @throws OperationException
	 */
	public static void publishToMQ(
		Session session,
		AdaptationHome dataSpace,
		Adaptation dataSet,
		Map<AdaptationTable, Set<Adaptation>> createDeltaAdaptationMap,
		Map<AdaptationTable, Set<Adaptation>> updateDeltaAdaptationMap,
		Map<String, String> deleteDeltaAdaptationMap) throws OperationException
	{
		LoggingCategory.getKernel().info(
			"[PublishUtil] Entered Publish utility to construct JSON objects and publish objects to RabbitMQ.");
		AdaptationHome referenceDataSpace = Repository.getDefault()
			.lookupHome(HomeKey.forBranchName(REFERENCE_DATASPACE_ID));
		if (referenceDataSpace == null)
		{
			throw OperationException
				.createError("DataSpace " + REFERENCE_DATASPACE_ID + " has not been found");
		}
		Adaptation referenceDataSet = referenceDataSpace
			.findAdaptationOrNull(AdaptationName.forName(REFERENCE_DATASET_ID));
		if (referenceDataSet == null)
		{
			throw OperationException
				.createError("DataSet " + REFERENCE_DATASET_ID + " has not been found");
		}

		Map<String, List<String>> unsent = new HashMap<>();
		Adaptation errorSendingItem = null;
		String errorJson = null;
		Exception exception = null;
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
						//	Sort the adaptations in hierarchical order (Franchise, Series, Season) 
						//	so that the parent endeavor message is sent first followed by the child endeavor message
						for (Iterator<Adaptation> iter = contentAdaptationSet.iterator(); iter
							.hasNext();)
						{
							Adaptation adaptation = iter.next();

							//TODO - Create a Location Java Bean
							Location contentContainer = constructJsonForLocation(
								adaptation,
								HtzConstants.CREATE);
							String jsonContent = new GsonBuilder().setPrettyPrinting()
								.create()
								.toJson(contentContainer);
							//String routingKey = "gcc." + contentContainer.getOp() + "."
							//	+ contentContainer.getType();
							try
							{
								// send the JSON message to RabbitMQ
								//sendMessage(jsonContent, routingKey);
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
							Location contentContainer = constructJsonForLocation(
								adaptation,
								HtzConstants.UPDATE);
							String jsonContent = new GsonBuilder().setPrettyPrinting()
								.create()
								.toJson(contentContainer);
							//String routingKey = "gcc." + contentContainer.getOp() + "."
							//	+ contentContainer.getType();
							try
							{
								// send the JSON message to RabbitMQ
								//sendMessage(jsonContent, routingKey);
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

	/**
	 * 
	 * @param dataSet
	 * @param referenceDataSet
	 * @param adaptation
	 * @param opType
	 * @return
	 */
	public static Location constructJsonForLocation(Adaptation adaptation, String opType)
	{
		/*
		Location contentContainer = new Location();
		contentContainer.set$schema(HtzConstants.SCHEMA_DEFINITION); //Get from Tharun
		contentContainer.setOp(opType);
		contentContainer.setType(
			StringUtils.uncapitalize(
				adaptation.getString(LocationPaths._Root_Location._ContentContainerType)));
		contentContainer.setVmid(adaptation.getString(LocationPaths._Root_Location._VmId));
		List<Adaptation> titleAdaptations = AdaptationUtil
			.getLinkedRecordList(adaptation, LocationPaths._Root_Location._Titles);
		List<Title> titles = new ArrayList<Title>();
		for (Adaptation titleAdaptation : titleAdaptations)
		{
			Title title = new Title();
			title.setTitle_type(
				(String) AdaptationUtil.followFK(
					titleAdaptation,
					LocationPaths._Root_Title._TitleType,
					CommonReferencePaths._Root_TitleType._Base_Name));
			if (titleAdaptation.getString(LocationPaths._Root_Title._Title) != null)
			{
				title.setTitle_value(
					titleAdaptation.getString(LocationPaths._Root_Title._Title)
						.trim()
						.replaceAll("\n\r", ""));
			}
		
			//construct LanguageDescriptor
			String langCode = (String) AdaptationUtil.followFK(
				titleAdaptation,
				LocationPaths._Root_Title._Language_Language,
				CommonReferencePaths._Root_Language._Code2);
			String scriptCode = (String) AdaptationUtil.followFK(
				titleAdaptation,
				LocationPaths._Root_Title._Language_Script,
				CommonReferencePaths._Root_Script._Code);
			String countryCode = (String) AdaptationUtil.followFK(
				titleAdaptation,
				LocationPaths._Root_Title._Language_Country,
				CommonReferencePaths._Root_Country._Code2);
			String languageDescriptor = langCode + "-" + scriptCode + "-" + countryCode;
			title.setLang(languageDescriptor);
			titles.add(title);
		}
		contentContainer.setTitles(titles);
		if (adaptation.getString(LocationPaths._Root_Location._Base_Description) != null)
		{
			contentContainer.setDescription(
				adaptation.getString(LocationPaths._Root_Location._Base_Description)
					.trim()
					.replaceAll("\n\r", ""));
		}
		contentContainer.setProvenance(
			(String) AdaptationUtil.followFK(
				adaptation,
				LocationPaths._Root_Location._Provenance,
				CommonReferencePaths._Root_Provenance._Base_Name));
		
		contentContainer.setCountry_of_origin(
			(String) AdaptationUtil.followFK(
				adaptation,
				LocationPaths._Root_Location._CountryOfOrigin,
				CommonReferencePaths._Root_Country._Code2));
		
		List<Adaptation> genreAdaptations = AdaptationUtil
			.followFKList(adaptation, LocationPaths._Root_Location._Genre);
		List<Genre> genres = new ArrayList<Genre>();
		for (Adaptation genreAdaptation : genreAdaptations)
		{
			Genre genre = new Genre();
			genre.setAuthority(
				(String) AdaptationUtil.followFK(
					genreAdaptation,
					CommonReferencePaths._Root_Genre._GenreAuthority,
					CommonReferencePaths._Root_GenreDetails_GenreAuthority._Base_Name));
			genre.setGenre(genreAdaptation.getString(CommonReferencePaths._Root_Genre._Base_Name));
			genre.setType(
				(String) AdaptationUtil.followFK(
					genreAdaptation,
					CommonReferencePaths._Root_Genre._GenreType,
					CommonReferencePaths._Root_GenreDetails_GenreType._Base_Name));
			genres.add(genre);
		}
		contentContainer.setGenres(genres);
		
		// set Incident Number
		String incidentNumber = adaptation.getString(LocationPaths._Root_Location._IncidentNumber);
		if (incidentNumber != null)
		{
			contentContainer.setIncident_number(incidentNumber);
		}
		
		List<Adaptation> ratingAdaptations = AdaptationUtil
			.getLinkedRecordList(adaptation, LocationPaths._Root_Location._Ratings);
		List<ContentRating> contentRatings = new ArrayList<ContentRating>();
		for (Adaptation ratingAdaptation : ratingAdaptations)
		{
			ContentRating contentRating = new ContentRating();
			contentRating.setAuthority(
				(String) AdaptationUtil.followFK(
					AdaptationUtil
						.followFK(ratingAdaptation, LocationPaths._Root_ContentRating._Rating),
					CommonReferencePaths._Root_Rating._RatingAuthority,
					CommonReferencePaths._Root_RatingDetails_RatingAuthority._Base_Name));
			contentRating.setRating(
				(String) AdaptationUtil.followFK(
					ratingAdaptation,
					LocationPaths._Root_ContentRating._Rating,
					CommonReferencePaths._Root_Rating._Base_Name));
			//Rating Descriptors is 0-* in the modeland in the CUR Event Schema
			List<Adaptation> ratingDescriptors = AdaptationUtil.followFKList(
				ratingAdaptation,
				LocationPaths._Root_ContentRating._RatingContentDescriptor);
			if (ratingDescriptors != null && !ratingDescriptors.isEmpty())
			{
				//Need to allow for multiple Descriptors here
				contentRating.setDescriptors(new ArrayList<String>());
				for (Adaptation ratingDescriptor : ratingDescriptors)
				{
					contentRating.getDescriptors().add(
						ratingDescriptor.getString(
							CommonReferencePaths._Root_RatingDetails_RatingContentDescriptor._Base_Name));
				}
			}
			if (ratingAdaptation.getString(LocationPaths._Root_ContentRating._Advisory) != null)
			{
				contentRating.setAdvisory(
					ratingAdaptation.getString(LocationPaths._Root_ContentRating._Advisory)
						.trim()
						.replaceAll("\n\r", ""));
			}
			contentRatings.add(contentRating);
		}
		contentContainer.setRatings(contentRatings);
		
		//construct ext_attributes
		if (adaptation.getString(LocationPaths._Root_Location._ContentContainerType)
			.equalsIgnoreCase(EndeavorTypes.FRANCHISE))
		{
			contentContainer.setExt_attributes(null);
		}
		else if (adaptation.getString(LocationPaths._Root_Location._ContentContainerType)
			.equalsIgnoreCase(EndeavorTypes.SERIES))
		{
			Supplemental seriesSupplemental = new Supplemental();
			Adaptation seriesSupplementalAdaptation = AdaptationUtil
				.followFK(adaptation, LocationPaths._Root_Location._SupplementalInstance_Series);
			if (seriesSupplementalAdaptation != null)
			{
				Boolean isMiniSeries = (Boolean) seriesSupplementalAdaptation.get(
					LocationPaths._Root_ContainerDetails_ContainerSupplemental_SeriesSupplemental._IsMiniSeries);
				seriesSupplemental.setMini_series(isMiniSeries);
				String langCode = (String) AdaptationUtil.followFK(
					seriesSupplementalAdaptation,
					LocationPaths._Root_ContainerDetails_ContainerSupplemental_SeriesSupplemental._ReferenceLanguage_Language,
					CommonReferencePaths._Root_Language._Code2);
				String scriptCode = (String) AdaptationUtil.followFK(
					seriesSupplementalAdaptation,
					LocationPaths._Root_ContainerDetails_ContainerSupplemental_SeriesSupplemental._ReferenceLanguage_Script,
					CommonReferencePaths._Root_Script._Code);
				String countryCode = (String) AdaptationUtil.followFK(
					seriesSupplementalAdaptation,
					LocationPaths._Root_ContainerDetails_ContainerSupplemental_SeriesSupplemental._ReferenceLanguage_Country,
					CommonReferencePaths._Root_Country._Code2);
				String languageDescriptor = langCode + "-" + scriptCode + "-" + countryCode;
				seriesSupplemental.setRef_lang(languageDescriptor);
				String typicalLength = resolveTime(
					seriesSupplementalAdaptation.getDate(
						LocationPaths._Root_ContainerDetails_ContainerSupplemental_SeriesSupplemental._TypicalLength));
				seriesSupplemental.setTypical_length(typicalLength);
				seriesSupplemental.setSchedule_status(
					seriesSupplementalAdaptation.getString(
						LocationPaths._Root_ContainerDetails_ContainerSupplemental_SeriesSupplemental._ScheduleStatus));
				contentContainer.setExt_attributes(seriesSupplemental);
			}
		}
		else if (adaptation.getString(LocationPaths._Root_Location._ContentContainerType)
			.equalsIgnoreCase(EndeavorTypes.SEASON))
		{
			Supplemental seasonSupplemental = new Supplemental();
			Adaptation seasonSupplementalAdaptation = AdaptationUtil
				.followFK(adaptation, LocationPaths._Root_Location._SupplementalInstance_Season);
			if (seasonSupplementalAdaptation != null)
			{
				if (seasonSupplementalAdaptation.get(
					LocationPaths._Root_ContainerDetails_ContainerSupplemental_SeasonSupplemental._Number) != null)
				{
					seasonSupplemental.setNumber(
						seasonSupplementalAdaptation
							.get(
								LocationPaths._Root_ContainerDetails_ContainerSupplemental_SeasonSupplemental._Number)
							.toString());
				}
				if (seasonSupplementalAdaptation.get(
					LocationPaths._Root_ContainerDetails_ContainerSupplemental_SeasonSupplemental._Sequence) != null)
				{
					seasonSupplemental.setSequence(
						seasonSupplementalAdaptation
							.get(
								LocationPaths._Root_ContainerDetails_ContainerSupplemental_SeasonSupplemental._Sequence)
							.toString());
				}
				contentContainer.setExt_attributes(seasonSupplemental);
			}
		}
		
		List<Adaptation> targetContainerAdaptations = AdaptationUtil
			.getLinkedRecordList(adaptation, LocationPaths._Root_Location._TargetContainers);
		List<ContainerRelatedContainer> relatedContainers = new ArrayList<ContainerRelatedContainer>();
		for (Adaptation targerContainerAdaptation : targetContainerAdaptations)
		{
			ContainerRelatedContainer relatedContainer = new ContainerRelatedContainer();
			relatedContainer.setPredicate(
				targerContainerAdaptation.getString(
					LocationPaths._Root_ContainerDetails_ContainerRelatedContainer._Predicate));
			relatedContainer.setTarget_vmid(
				(String) AdaptationUtil.followFK(
					targerContainerAdaptation,
					LocationPaths._Root_ContainerDetails_ContainerRelatedContainer._TargetContentContainer,
					LocationPaths._Root_Location._VmId));
			relatedContainers.add(relatedContainer);
		}
		contentContainer.setContainer_relations(relatedContainers);
		
		List<Adaptation> relatedChannelAdaptations = AdaptationUtil
			.getLinkedRecordList(adaptation, LocationPaths._Root_Location._Channels);
		List<Location> contentChannels = new ArrayList<Location>();
		for (Adaptation relatedChannelAdaptation : relatedChannelAdaptations)
		{
			Location contentChannel = new Location();
			contentChannel.setPredicate(
				relatedChannelAdaptation.getString(LocationPaths._Root_Location._Predicate));
			contentChannel.setTarget_vmid(
				(String) AdaptationUtil.followFK(
					relatedChannelAdaptation,
					LocationPaths._Root_Location._TargetChannel,
					CommonReferencePaths._Root_Channel._VmId));
			contentChannels.add(contentChannel);
		}
		contentContainer.setChannel_relations(contentChannels);
		
		//	Handle Third Party Company Relationships
		List<Adaptation> thirdPartyCompanyRelationshipsAdaptations = AdaptationUtil
			.getLinkedRecordList(
				adaptation,
				LocationPaths._Root_Location._ThirdPartyCompanyRelationships);
		List<ThirdPartyCompanyRelationship> thirdPartyCompanyRelationships = new ArrayList<ThirdPartyCompanyRelationship>();
		for (Adaptation thirdPartyCompanyRelationshipsAdaptation : thirdPartyCompanyRelationshipsAdaptations)
		{
			ThirdPartyCompanyRelationship thirdPartyCompanyRelationship = new ThirdPartyCompanyRelationship();
			thirdPartyCompanyRelationship.setThird_party_role(
				thirdPartyCompanyRelationshipsAdaptation
					.getString(LocationPaths._Root_ThirdPartyCompanyRelationship._ThirdPartyRole));
			thirdPartyCompanyRelationship.setThird_party_company(
				(String) AdaptationUtil.followFK(
					thirdPartyCompanyRelationshipsAdaptation,
					LocationPaths._Root_ThirdPartyCompanyRelationship._ThirdPartyCompany,
					CommonReferencePaths._Root_ThirdPartyCompany._Base_Name));
			thirdPartyCompanyRelationships.add(thirdPartyCompanyRelationship);
		}
		contentContainer.setThird_party_company_relationships(thirdPartyCompanyRelationships);
		
		//	Handle Third Party Identifiers
		List<Adaptation> thirdPartyIdentifierAdaptations = AdaptationUtil
			.getLinkedRecordList(adaptation, LocationPaths._Root_Location._ThirdPartyIdentifiers);
		List<ThirdPartyIdentifier> thirdPartyIdentifiers = new ArrayList<ThirdPartyIdentifier>();
		for (Adaptation thirdPartyIdentifierAdaptation : thirdPartyIdentifierAdaptations)
		{
			ThirdPartyIdentifier thirdPartyIdentifier = new ThirdPartyIdentifier();
			thirdPartyIdentifier.setIdentification_authority(
				(String) AdaptationUtil.followFK(
					thirdPartyIdentifierAdaptation,
					LocationPaths._Root_ThirdPartyIdentifier._IdentificationAuthority,
					CommonReferencePaths._Root_IdentificationAuthority._Base_Name));
			thirdPartyIdentifier.setIdentifier(
				thirdPartyIdentifierAdaptation
					.getString(LocationPaths._Root_ThirdPartyIdentifier._Identifier));
			thirdPartyIdentifiers.add(thirdPartyIdentifier);
		}
		contentContainer.setThird_party_identifiers(thirdPartyIdentifiers);
		
		// Reference Channel
		contentContainer.setRef_channel(
			(String) AdaptationUtil.followFK(
				adaptation,
				LocationPaths._Root_Location._ReferenceChannel,
				CommonReferencePaths._Root_Channel._Base_Name)); */

		//return contentContainer;
		return null;
	}

	/**
	 * 
	 * @param message
	 * @throws Exception
	 */
	public static void sendMessage(String message, String routingKey) throws Exception
	{
		/*
		//TODO - This the message that send out the message to MS
		//TODO - this will contain  all the connection info for our environment
		Properties props;
		String propertiesFile = System
			.getProperty(HtzConstants.HERTZ_MDM_PROPERTY_FILE_SYSTEM_PROPERTY);
		if (propertiesFile != null)
		{
			PropertyFileHelper propertyHelper = new PropertyFileHelper(propertiesFile);
			props = propertyHelper.getProperties();
			String rabbitMQHost = props.getProperty(HtzConstants.RABBIT_MQ_HOST_PROPERTY);
			if (rabbitMQHost == null || "".equals(rabbitMQHost))
			{
				throw new IOException(
					"Value must be specified for property " + HtzConstants.RABBIT_MQ_HOST_PROPERTY
						+ ".");
			}
			String rabbitMQUsername = props.getProperty(HtzConstants.RABBIT_MQ_HOST_PROPERTY);
			if (rabbitMQUsername == null || "".equals(rabbitMQUsername))
			{
				throw new IOException(
					"Value must be specified for property "
						+ HtzConstants.RABBIT_MQ_USERNAME_PROPERTY + ".");
			}
			String rabbitMQPassword = props.getProperty(HtzConstants.RABBIT_MQ_PASSWORD_PROPERTY);
			if (rabbitMQPassword == null || "".equals(rabbitMQPassword))
			{
				throw new IOException(
					"Value must be specified for property "
						+ HtzConstants.RABBIT_MQ_PASSWORD_PROPERTY + ".");
			}
			String rabbitMQPort = props.getProperty(HtzConstants.RABBIT_MQ_PORT_PROPERTY);
			if (rabbitMQPort == null || "".equals(rabbitMQPort))
			{
				throw new IOException(
					"Value must be specified for property " + HtzConstants.RABBIT_MQ_PORT_PROPERTY
						+ ".");
			}
			String rabbitMQVirtualHost = props
				.getProperty(HtzConstants.RABBIT_MQ_VIRTUALHOST_PROPERTY);
			if (rabbitMQVirtualHost == null || "".equals(rabbitMQVirtualHost))
			{
				throw new IOException(
					"Value must be specified for property "
						+ HtzConstants.RABBIT_MQ_VIRTUALHOST_PROPERTY + ".");
			}
			String rabbitMQExchangeName = props
				.getProperty(HtzConstants.RABBIT_MQ_EXCHANGE_NAME_PROPERTY);
			if (rabbitMQExchangeName == null || "".equals(rabbitMQExchangeName))
			{
				throw new IOException(
					"Value must be specified for property "
						+ HtzConstants.RABBIT_MQ_EXCHANGE_NAME_PROPERTY + ".");
			}
			String rabbitMQExchangeType = props
				.getProperty(HtzConstants.RABBIT_MQ_EXCHANGE_TYPE_PROPERTY);
			if (rabbitMQExchangeType == null || "".equals(rabbitMQExchangeType))
			{
				throw new IOException(
					"Value must be specified for property "
						+ HtzConstants.RABBIT_MQ_EXCHANGE_TYPE_PROPERTY + ".");
			}
			String rabbitMQConnectionTimeout = props
				.getProperty(HtzConstants.RABBIT_MQ_CONNECTION_TIMEOUT_PROPERTY);
			if (rabbitMQConnectionTimeout == null || "".equals(rabbitMQConnectionTimeout))
			{
				throw new IOException(
					"Value must be specified for property "
						+ HtzConstants.RABBIT_MQ_CONNECTION_TIMEOUT_PROPERTY + ".");
			}
			//TODO - Logs to kernel for Debugging
			LoggingCategory.getKernel().info(
				" [PublishUtil] Connection Parameters: Host -  '" + rabbitMQHost + " Port: "
					+ rabbitMQPort + " Username: " + rabbitMQUsername + " VirtualHost: "
					+ rabbitMQVirtualHost);
		
			//TODO - Replace with MS connection
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost(rabbitMQHost);
			factory.setPort(Integer.parseInt(rabbitMQPort));
			factory.setUsername(rabbitMQUsername);
			factory.setPassword(rabbitMQPassword);
			factory.setVirtualHost(rabbitMQVirtualHost);
			factory.setConnectionTimeout(Integer.parseInt(rabbitMQConnectionTimeout));
			Connection connection = factory.newConnection();
			Channel channel = connection.createChannel();
			//channel.exchangeDeclare(rabbitMQExchangeName, rabbitMQExchangeType);
			//channel.basicPublish("", rabbitMQQueueName, null, message.getBytes());
			channel.basicPublish(rabbitMQExchangeName, routingKey, null, message.getBytes());
			LoggingCategory.getKernel()
				.info(" [PublishUtil] Mesage Sent '" + routingKey + "':'" + message + "'");
		
			channel.close();
			connection.close();
		} */
	}
}
