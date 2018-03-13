package com.hertz.mdm.admin.scripttask;

public class PublishUtil
{
	/*
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
	/*
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
					.equals(VcmGlobalContentCatalogPaths._Root_ContentContainer.getPathInSchema()))
				{
					if (contentAdaptationSet != null && !contentAdaptationSet.isEmpty())
					{
						//	Sort the adaptations in hierarchical order (Franchise, Series, Season) 
						//	so that the parent endeavor message is sent first followed by the child endeavor message
						contentAdaptationSet = sortEndeavorsBeforePublish(
							contentAdaptationSet,
							true);
						for (Iterator<Adaptation> iter = contentAdaptationSet.iterator(); iter
							.hasNext();)
						{
							Adaptation adaptation = iter.next();
							ContentContainer contentContainer = constructJsonForContentContainer(
								adaptation,
								VcmGccConstants.CREATE);
							String jsonContent = new GsonBuilder().setPrettyPrinting()
								.create()
								.toJson(contentContainer);
							String routingKey = "gcc." + contentContainer.getOp() + "."
								+ contentContainer.getType();
							try
							{
								// send the JSON message to RabbitMQ
								sendMessage(jsonContent, routingKey);
							}
							catch (Exception e)
							{
								e.printStackTrace();
								collectItems(
									unsent,
									VcmGccConstants.CREATE,
									contentContainer.getVmid(),
									iter,
									null,
									true);
								errorSendingItem = adaptation;
								exception = e;
								errorJson = jsonContent;
								break;
							}
						}
					}
				}
				else if (contentTable.getTablePath()
					.equals(VcmGlobalContentCatalogPaths._Root_ContentItem.getPathInSchema()))
				{
					contentAdaptationSet = sortEndeavorsBeforePublish(contentAdaptationSet, false);
					if (contentAdaptationSet != null && !contentAdaptationSet.isEmpty())
					{
						for (Iterator<Adaptation> iter = contentAdaptationSet.iterator(); iter
							.hasNext();)
						{
							if (exception != null)
							{
								collectItems(
									unsent,
									VcmGccConstants.CREATE,
									null,
									iter,
									null,
									false);
								break;
							}
							Adaptation adaptation = iter.next();
							ContentItem contentItem = constructJsonForContentItem(
								adaptation,
								VcmGccConstants.CREATE);
							String jsonContent = new GsonBuilder().setPrettyPrinting()
								.create()
								.toJson(contentItem);
							String routingKey = "gcc." + contentItem.getOp() + "."
								+ contentItem.getType();
							try
							{
								// send the JSON message to RabbitMQ
								sendMessage(jsonContent, routingKey);
							}
							catch (Exception e)
							{
								e.printStackTrace();
								collectItems(
									unsent,
									VcmGccConstants.CREATE,
									contentItem.getVmid(),
									iter,
									null,
									false);
								errorSendingItem = adaptation;
								exception = e;
								errorJson = jsonContent;
								break;
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
					.equals(VcmGlobalContentCatalogPaths._Root_ContentContainer.getPathInSchema()))
				{
					if (contentAdaptationSet != null && !contentAdaptationSet.isEmpty())
					{
						//	Sort the adaptations in hierarchical order (Franchise, Series, Season) 
						//	so that the parent endeavor message is sent first followed by the child endeavor message
						contentAdaptationSet = sortEndeavorsBeforePublish(
							contentAdaptationSet,
							true);
						for (Iterator<Adaptation> iter = contentAdaptationSet.iterator(); iter
							.hasNext();)
						{
							if (exception != null)
							{
								collectItems(
									unsent,
									VcmGccConstants.UPDATE,
									null,
									iter,
									null,
									false);
								break;
							}
							Adaptation adaptation = iter.next();
							ContentContainer contentContainer = constructJsonForContentContainer(
								adaptation,
								VcmGccConstants.UPDATE);
							String jsonContent = new GsonBuilder().setPrettyPrinting()
								.create()
								.toJson(contentContainer);
							String routingKey = "gcc." + contentContainer.getOp() + "."
								+ contentContainer.getType();
							try
							{
								// send the JSON message to RabbitMQ
								sendMessage(jsonContent, routingKey);
							}
							catch (Exception e)
							{
								e.printStackTrace();
								collectItems(
									unsent,
									VcmGccConstants.UPDATE,
									contentContainer.getVmid(),
									iter,
									null,
									false);
								errorSendingItem = adaptation;
								exception = e;
								errorJson = jsonContent;
								break;
							}
						}
					}
				}
				else if (contentTable.getTablePath()
					.equals(VcmGlobalContentCatalogPaths._Root_ContentItem.getPathInSchema()))
				{
					if (contentAdaptationSet != null && !contentAdaptationSet.isEmpty())
					{
						contentAdaptationSet = sortEndeavorsBeforePublish(
							contentAdaptationSet,
							false);
						for (Iterator<Adaptation> iter = contentAdaptationSet.iterator(); iter
							.hasNext();)
						{
							if (exception != null)
							{
								collectItems(
									unsent,
									VcmGccConstants.UPDATE,
									null,
									iter,
									null,
									false);
								break;
							}
							Adaptation adaptation = iter.next();
							ContentItem contentItem = constructJsonForContentItem(
								adaptation,
								VcmGccConstants.UPDATE);
							String jsonContent = new GsonBuilder().setPrettyPrinting()
								.create()
								.toJson(contentItem);
							String routingKey = "gcc." + contentItem.getOp() + "."
								+ contentItem.getType();
							try
							{
								// send the JSON message to RabbitMQ
								sendMessage(jsonContent, routingKey);
							}
							catch (Exception e)
							{
								e.printStackTrace();
								collectItems(
									unsent,
									VcmGccConstants.UPDATE,
									contentItem.getVmid(),
									iter,
									null,
									false);
								errorSendingItem = adaptation;
								exception = e;
								errorJson = jsonContent;
								break;
							}
						}
					}
				}
			}
		}
		if (deleteDeltaAdaptationMap != null && !deleteDeltaAdaptationMap.isEmpty())
		{
			for (Iterator<Map.Entry<String, String>> iter = deleteDeltaAdaptationMap.entrySet()
				.iterator(); iter.hasNext();)
			{
				Map.Entry<String, String> entry = iter.next();
				RemoveContent removeContent = constructJsonForContentRemoval(
					VcmGccConstants.REMOVE,
					entry.getKey(),
					entry.getValue());
				String jsonContent = new GsonBuilder().setPrettyPrinting()
					.create()
					.toJson(removeContent);
				String routingKey = "gcc." + removeContent.getOp() + "." + removeContent.getType();
				try
				{
					// send the JSON message to RabbitMQ
					sendMessage(jsonContent, routingKey);
				}
				catch (Exception e)
				{
					e.printStackTrace();
					collectItems(unsent, VcmGccConstants.REMOVE, entry.getKey(), null, iter, false);
					exception = e;
					errorJson = jsonContent;
					break;
				}
	
			}
		}
	
		if (exception != null)
		{
			EmailNotificationWorkflowLauncher.sendEmailNotification(
				session,
				GccWorkflowConstants.WF_KEY_SEND_EMAIL_NOTIFICATION,
				errorJson,
				unsent,
				errorSendingItem != null ? errorSendingItem.getHome() : null,
				errorSendingItem);
			throw OperationException.createError(
				"Please contact Tech Admin.  System Exception has Occurred:  ",
				exception);
		}
	}
	
	public static void collectItems(
		Map<String, List<String>> items,
		String changeType,
		String errorVmid,
		Iterator<Adaptation> iter,
		Iterator<Map.Entry<String, String>> removeIter,
		boolean container)
	{
		List<String> ids = items.get(changeType);
		if (ids == null)
		{
			ids = new ArrayList<>();
			items.put(changeType, ids);
		}
		if (errorVmid != null)
			ids.add(errorVmid);
		Path path = container ? VcmGlobalContentCatalogPaths._Root_ContentContainer._VmId
			: VcmGlobalContentCatalogPaths._Root_ContentItem._VmId;
		if (iter != null)
		{
			while (iter.hasNext())
			{
				Adaptation next = iter.next();
				ids.add(next.getString(path));
			}
		}
		if (removeIter != null)
		{
			while (removeIter.hasNext())
			{
				Map.Entry<String, String> next = removeIter.next();
				ids.add(next.getKey());
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
	/*
	public static ContentContainer constructJsonForContentContainer(
		Adaptation adaptation,
		String opType)
	{
	
		ContentContainer contentContainer = new ContentContainer();
		contentContainer.set$schema(VcmGccConstants.SCHEMA_DEFINITION);
		contentContainer.setOp(opType);
		contentContainer.setType(
			StringUtils.uncapitalize(
				adaptation.getString(
					VcmGlobalContentCatalogPaths._Root_ContentContainer._ContentContainerType)));
		contentContainer.setVmid(
			adaptation.getString(VcmGlobalContentCatalogPaths._Root_ContentContainer._VmId));
		List<Adaptation> titleAdaptations = AdaptationUtil.getLinkedRecordList(
			adaptation,
			VcmGlobalContentCatalogPaths._Root_ContentContainer._Titles);
		List<Title> titles = new ArrayList<Title>();
		for (Adaptation titleAdaptation : titleAdaptations)
		{
			Title title = new Title();
			title.setTitle_type(
				(String) AdaptationUtil.followFK(
					titleAdaptation,
					VcmGlobalContentCatalogPaths._Root_Title._TitleType,
					VcmCommonReferencePaths._Root_TitleType._Base_Name));
			if (titleAdaptation.getString(VcmGlobalContentCatalogPaths._Root_Title._Title) != null)
			{
				title.setTitle_value(
					titleAdaptation.getString(VcmGlobalContentCatalogPaths._Root_Title._Title)
						.trim()
						.replaceAll("\n\r", ""));
			}
	
			//construct LanguageDescriptor
			String langCode = (String) AdaptationUtil.followFK(
				titleAdaptation,
				VcmGlobalContentCatalogPaths._Root_Title._Language_Language,
				VcmCommonReferencePaths._Root_Language._Code2);
			String scriptCode = (String) AdaptationUtil.followFK(
				titleAdaptation,
				VcmGlobalContentCatalogPaths._Root_Title._Language_Script,
				VcmCommonReferencePaths._Root_Script._Code);
			String countryCode = (String) AdaptationUtil.followFK(
				titleAdaptation,
				VcmGlobalContentCatalogPaths._Root_Title._Language_Country,
				VcmCommonReferencePaths._Root_Country._Code2);
			String languageDescriptor = langCode + "-" + scriptCode + "-" + countryCode;
			title.setLang(languageDescriptor);
			titles.add(title);
		}
		contentContainer.setTitles(titles);
		if (adaptation.getString(
			VcmGlobalContentCatalogPaths._Root_ContentContainer._Base_Description) != null)
		{
			contentContainer.setDescription(
				adaptation
					.getString(
						VcmGlobalContentCatalogPaths._Root_ContentContainer._Base_Description)
					.trim()
					.replaceAll("\n\r", ""));
		}
		contentContainer.setProvenance(
			(String) AdaptationUtil.followFK(
				adaptation,
				VcmGlobalContentCatalogPaths._Root_ContentContainer._Provenance,
				VcmCommonReferencePaths._Root_Provenance._Base_Name));
	
		contentContainer.setCountry_of_origin(
			(String) AdaptationUtil.followFK(
				adaptation,
				VcmGlobalContentCatalogPaths._Root_ContentContainer._CountryOfOrigin,
				VcmCommonReferencePaths._Root_Country._Code2));
	
		List<Adaptation> genreAdaptations = AdaptationUtil
			.followFKList(adaptation, VcmGlobalContentCatalogPaths._Root_ContentContainer._Genre);
		List<Genre> genres = new ArrayList<Genre>();
		for (Adaptation genreAdaptation : genreAdaptations)
		{
			Genre genre = new Genre();
			genre.setAuthority(
				(String) AdaptationUtil.followFK(
					genreAdaptation,
					VcmCommonReferencePaths._Root_Genre._GenreAuthority,
					VcmCommonReferencePaths._Root_GenreDetails_GenreAuthority._Base_Name));
			genre.setGenre(
				genreAdaptation.getString(VcmCommonReferencePaths._Root_Genre._Base_Name));
			genre.setType(
				(String) AdaptationUtil.followFK(
					genreAdaptation,
					VcmCommonReferencePaths._Root_Genre._GenreType,
					VcmCommonReferencePaths._Root_GenreDetails_GenreType._Base_Name));
			genres.add(genre);
		}
		contentContainer.setGenres(genres);
	
		// set Incident Number
		String incidentNumber = adaptation
			.getString(VcmGlobalContentCatalogPaths._Root_ContentContainer._IncidentNumber);
		if (incidentNumber != null)
		{
			contentContainer.setIncident_number(incidentNumber);
		}
	
		List<Adaptation> ratingAdaptations = AdaptationUtil.getLinkedRecordList(
			adaptation,
			VcmGlobalContentCatalogPaths._Root_ContentContainer._Ratings);
		List<ContentRating> contentRatings = new ArrayList<ContentRating>();
		for (Adaptation ratingAdaptation : ratingAdaptations)
		{
			ContentRating contentRating = new ContentRating();
			contentRating.setAuthority(
				(String) AdaptationUtil.followFK(
					AdaptationUtil.followFK(
						ratingAdaptation,
						VcmGlobalContentCatalogPaths._Root_ContentRating._Rating),
					VcmCommonReferencePaths._Root_Rating._RatingAuthority,
					VcmCommonReferencePaths._Root_RatingDetails_RatingAuthority._Base_Name));
			contentRating.setRating(
				(String) AdaptationUtil.followFK(
					ratingAdaptation,
					VcmGlobalContentCatalogPaths._Root_ContentRating._Rating,
					VcmCommonReferencePaths._Root_Rating._Base_Name));
			//Rating Descriptors is 0-* in the modeland in the CUR Event Schema
			List<Adaptation> ratingDescriptors = AdaptationUtil.followFKList(
				ratingAdaptation,
				VcmGlobalContentCatalogPaths._Root_ContentRating._RatingContentDescriptor);
			if (ratingDescriptors != null && !ratingDescriptors.isEmpty())
			{
				//Need to allow for multiple Descriptors here
				contentRating.setDescriptors(new ArrayList<String>());
				for (Adaptation ratingDescriptor : ratingDescriptors)
				{
					contentRating.getDescriptors().add(
						ratingDescriptor.getString(
							VcmCommonReferencePaths._Root_RatingDetails_RatingContentDescriptor._Base_Name));
				}
			}
			if (ratingAdaptation
				.getString(VcmGlobalContentCatalogPaths._Root_ContentRating._Advisory) != null)
			{
				contentRating.setAdvisory(
					ratingAdaptation
						.getString(VcmGlobalContentCatalogPaths._Root_ContentRating._Advisory)
						.trim()
						.replaceAll("\n\r", ""));
			}
			contentRatings.add(contentRating);
		}
		contentContainer.setRatings(contentRatings);
	
		//construct ext_attributes
		if (adaptation
			.getString(VcmGlobalContentCatalogPaths._Root_ContentContainer._ContentContainerType)
			.equalsIgnoreCase(EndeavorTypes.FRANCHISE))
		{
			contentContainer.setExt_attributes(null);
		}
		else if (adaptation
			.getString(VcmGlobalContentCatalogPaths._Root_ContentContainer._ContentContainerType)
			.equalsIgnoreCase(EndeavorTypes.SERIES))
		{
			Supplemental seriesSupplemental = new Supplemental();
			Adaptation seriesSupplementalAdaptation = AdaptationUtil.followFK(
				adaptation,
				VcmGlobalContentCatalogPaths._Root_ContentContainer._SupplementalInstance_Series);
			if (seriesSupplementalAdaptation != null)
			{
				Boolean isMiniSeries = (Boolean) seriesSupplementalAdaptation.get(
					VcmGlobalContentCatalogPaths._Root_ContainerDetails_ContainerSupplemental_SeriesSupplemental._IsMiniSeries);
				seriesSupplemental.setMini_series(isMiniSeries);
				String langCode = (String) AdaptationUtil.followFK(
					seriesSupplementalAdaptation,
					VcmGlobalContentCatalogPaths._Root_ContainerDetails_ContainerSupplemental_SeriesSupplemental._ReferenceLanguage_Language,
					VcmCommonReferencePaths._Root_Language._Code2);
				String scriptCode = (String) AdaptationUtil.followFK(
					seriesSupplementalAdaptation,
					VcmGlobalContentCatalogPaths._Root_ContainerDetails_ContainerSupplemental_SeriesSupplemental._ReferenceLanguage_Script,
					VcmCommonReferencePaths._Root_Script._Code);
				String countryCode = (String) AdaptationUtil.followFK(
					seriesSupplementalAdaptation,
					VcmGlobalContentCatalogPaths._Root_ContainerDetails_ContainerSupplemental_SeriesSupplemental._ReferenceLanguage_Country,
					VcmCommonReferencePaths._Root_Country._Code2);
				String languageDescriptor = langCode + "-" + scriptCode + "-" + countryCode;
				seriesSupplemental.setRef_lang(languageDescriptor);
				String typicalLength = resolveTime(
					seriesSupplementalAdaptation.getDate(
						VcmGlobalContentCatalogPaths._Root_ContainerDetails_ContainerSupplemental_SeriesSupplemental._TypicalLength));
				seriesSupplemental.setTypical_length(typicalLength);
				seriesSupplemental.setSchedule_status(
					seriesSupplementalAdaptation.getString(
						VcmGlobalContentCatalogPaths._Root_ContainerDetails_ContainerSupplemental_SeriesSupplemental._ScheduleStatus));
				contentContainer.setExt_attributes(seriesSupplemental);
			}
		}
		else if (adaptation
			.getString(VcmGlobalContentCatalogPaths._Root_ContentContainer._ContentContainerType)
			.equalsIgnoreCase(EndeavorTypes.SEASON))
		{
			Supplemental seasonSupplemental = new Supplemental();
			Adaptation seasonSupplementalAdaptation = AdaptationUtil.followFK(
				adaptation,
				VcmGlobalContentCatalogPaths._Root_ContentContainer._SupplementalInstance_Season);
			if (seasonSupplementalAdaptation != null)
			{
				if (seasonSupplementalAdaptation.get(
					VcmGlobalContentCatalogPaths._Root_ContainerDetails_ContainerSupplemental_SeasonSupplemental._Number) != null)
				{
					seasonSupplemental.setNumber(
						seasonSupplementalAdaptation
							.get(
								VcmGlobalContentCatalogPaths._Root_ContainerDetails_ContainerSupplemental_SeasonSupplemental._Number)
							.toString());
				}
				if (seasonSupplementalAdaptation.get(
					VcmGlobalContentCatalogPaths._Root_ContainerDetails_ContainerSupplemental_SeasonSupplemental._Sequence) != null)
				{
					seasonSupplemental.setSequence(
						seasonSupplementalAdaptation
							.get(
								VcmGlobalContentCatalogPaths._Root_ContainerDetails_ContainerSupplemental_SeasonSupplemental._Sequence)
							.toString());
				}
				contentContainer.setExt_attributes(seasonSupplemental);
			}
		}
	
		List<Adaptation> targetContainerAdaptations = AdaptationUtil.getLinkedRecordList(
			adaptation,
			VcmGlobalContentCatalogPaths._Root_ContentContainer._TargetContainers);
		List<ContainerRelatedContainer> relatedContainers = new ArrayList<ContainerRelatedContainer>();
		for (Adaptation targerContainerAdaptation : targetContainerAdaptations)
		{
			ContainerRelatedContainer relatedContainer = new ContainerRelatedContainer();
			relatedContainer.setPredicate(
				targerContainerAdaptation.getString(
					VcmGlobalContentCatalogPaths._Root_ContainerDetails_ContainerRelatedContainer._Predicate));
			relatedContainer.setTarget_vmid(
				(String) AdaptationUtil.followFK(
					targerContainerAdaptation,
					VcmGlobalContentCatalogPaths._Root_ContainerDetails_ContainerRelatedContainer._TargetContentContainer,
					VcmGlobalContentCatalogPaths._Root_ContentContainer._VmId));
			relatedContainers.add(relatedContainer);
		}
		contentContainer.setContainer_relations(relatedContainers);
	
		List<Adaptation> relatedChannelAdaptations = AdaptationUtil.getLinkedRecordList(
			adaptation,
			VcmGlobalContentCatalogPaths._Root_ContentContainer._Channels);
		List<ContentChannel> contentChannels = new ArrayList<ContentChannel>();
		for (Adaptation relatedChannelAdaptation : relatedChannelAdaptations)
		{
			ContentChannel contentChannel = new ContentChannel();
			contentChannel.setPredicate(
				relatedChannelAdaptation
					.getString(VcmGlobalContentCatalogPaths._Root_ContentChannel._Predicate));
			contentChannel.setTarget_vmid(
				(String) AdaptationUtil.followFK(
					relatedChannelAdaptation,
					VcmGlobalContentCatalogPaths._Root_ContentChannel._TargetChannel,
					VcmCommonReferencePaths._Root_Channel._VmId));
			contentChannels.add(contentChannel);
		}
		contentContainer.setChannel_relations(contentChannels);
	
		//	Handle Third Party Company Relationships
		List<Adaptation> thirdPartyCompanyRelationshipsAdaptations = AdaptationUtil
			.getLinkedRecordList(
				adaptation,
				VcmGlobalContentCatalogPaths._Root_ContentContainer._ThirdPartyCompanyRelationships);
		List<ThirdPartyCompanyRelationship> thirdPartyCompanyRelationships = new ArrayList<ThirdPartyCompanyRelationship>();
		for (Adaptation thirdPartyCompanyRelationshipsAdaptation : thirdPartyCompanyRelationshipsAdaptations)
		{
			ThirdPartyCompanyRelationship thirdPartyCompanyRelationship = new ThirdPartyCompanyRelationship();
			thirdPartyCompanyRelationship.setThird_party_role(
				thirdPartyCompanyRelationshipsAdaptation.getString(
					VcmGlobalContentCatalogPaths._Root_ThirdPartyCompanyRelationship._ThirdPartyRole));
			thirdPartyCompanyRelationship.setThird_party_company(
				(String) AdaptationUtil.followFK(
					thirdPartyCompanyRelationshipsAdaptation,
					VcmGlobalContentCatalogPaths._Root_ThirdPartyCompanyRelationship._ThirdPartyCompany,
					VcmCommonReferencePaths._Root_ThirdPartyCompany._Base_Name));
			thirdPartyCompanyRelationships.add(thirdPartyCompanyRelationship);
		}
		contentContainer.setThird_party_company_relationships(thirdPartyCompanyRelationships);
	
		//	Handle Third Party Identifiers
		List<Adaptation> thirdPartyIdentifierAdaptations = AdaptationUtil.getLinkedRecordList(
			adaptation,
			VcmGlobalContentCatalogPaths._Root_ContentContainer._ThirdPartyIdentifiers);
		List<ThirdPartyIdentifier> thirdPartyIdentifiers = new ArrayList<ThirdPartyIdentifier>();
		for (Adaptation thirdPartyIdentifierAdaptation : thirdPartyIdentifierAdaptations)
		{
			ThirdPartyIdentifier thirdPartyIdentifier = new ThirdPartyIdentifier();
			thirdPartyIdentifier.setIdentification_authority(
				(String) AdaptationUtil.followFK(
					thirdPartyIdentifierAdaptation,
					VcmGlobalContentCatalogPaths._Root_ThirdPartyIdentifier._IdentificationAuthority,
					VcmCommonReferencePaths._Root_IdentificationAuthority._Base_Name));
			thirdPartyIdentifier.setIdentifier(
				thirdPartyIdentifierAdaptation.getString(
					VcmGlobalContentCatalogPaths._Root_ThirdPartyIdentifier._Identifier));
			thirdPartyIdentifiers.add(thirdPartyIdentifier);
		}
		contentContainer.setThird_party_identifiers(thirdPartyIdentifiers);
	
		// Reference Channel
		contentContainer.setRef_channel(
			(String) AdaptationUtil.followFK(
				adaptation,
				VcmGlobalContentCatalogPaths._Root_ContentContainer._ReferenceChannel,
				VcmCommonReferencePaths._Root_Channel._Base_Name));
	
		return contentContainer;
	}
	
	/**
	 * 
	 * @param dataSet
	 * @param referenceDataSet
	 * @param adaptation
	 * @param opType
	 * @return
	 */
	/*
	public static ContentItem constructJsonForContentItem(Adaptation adaptation, String opType)
	{
	
		ContentItem contentItem = new ContentItem();
		contentItem.set$schema(VcmGccConstants.SCHEMA_DEFINITION);
		contentItem.setOp(opType);
		String endeavorType = adaptation
			.getString(VcmGlobalContentCatalogPaths._Root_ContentItem._ContentItemType);
		contentItem.setType(StringUtils.uncapitalize(endeavorType));
		contentItem
			.setVmid(adaptation.getString(VcmGlobalContentCatalogPaths._Root_ContentItem._VmId));
		List<Adaptation> titleAdaptations = AdaptationUtil.getLinkedRecordList(
			adaptation,
			VcmGlobalContentCatalogPaths._Root_ContentItem._Titles);
		List<Title> titles = new ArrayList<Title>();
		for (Adaptation titleAdaptation : titleAdaptations)
		{
			Title title = new Title();
			title.setTitle_type(
				(String) AdaptationUtil.followFK(
					titleAdaptation,
					VcmGlobalContentCatalogPaths._Root_Title._TitleType,
					VcmCommonReferencePaths._Root_TitleType._Base_Name));
			if (titleAdaptation.getString(VcmGlobalContentCatalogPaths._Root_Title._Title) != null)
			{
				title.setTitle_value(
					titleAdaptation.getString(VcmGlobalContentCatalogPaths._Root_Title._Title)
						.trim()
						.replaceAll("\n\r", ""));
			}
	
			//construct Title LanguageDescriptor
			String langCode = (String) AdaptationUtil.followFK(
				titleAdaptation,
				VcmGlobalContentCatalogPaths._Root_Title._Language_Language,
				VcmCommonReferencePaths._Root_Language._Code2);
			String scriptCode = (String) AdaptationUtil.followFK(
				titleAdaptation,
				VcmGlobalContentCatalogPaths._Root_Title._Language_Script,
				VcmCommonReferencePaths._Root_Script._Code);
			String countryCode = (String) AdaptationUtil.followFK(
				titleAdaptation,
				VcmGlobalContentCatalogPaths._Root_Title._Language_Country,
				VcmCommonReferencePaths._Root_Country._Code2);
			String languageDescriptor = langCode + "-" + scriptCode + "-" + countryCode;
			title.setLang(languageDescriptor);
			titles.add(title);
		}
		contentItem.setTitles(titles);
	
		//construct ContentItem LanguageDescriptor
		String langCode = (String) AdaptationUtil.followFK(
			adaptation,
			VcmGlobalContentCatalogPaths._Root_ContentItem._ReferenceLanguage_Language,
			VcmCommonReferencePaths._Root_Language._Code2);
		String scriptCode = (String) AdaptationUtil.followFK(
			adaptation,
			VcmGlobalContentCatalogPaths._Root_ContentItem._ReferenceLanguage_Script,
			VcmCommonReferencePaths._Root_Script._Code);
		String countryCode = (String) AdaptationUtil.followFK(
			adaptation,
			VcmGlobalContentCatalogPaths._Root_ContentItem._ReferenceLanguage_Country,
			VcmCommonReferencePaths._Root_Country._Code2);
		String languageDescriptor = langCode + "-" + scriptCode + "-" + countryCode;
		contentItem.setRef_lang(languageDescriptor);
	
		if (adaptation
			.getString(VcmGlobalContentCatalogPaths._Root_ContentItem._Base_Description) != null)
		{
			contentItem.setDescription(
				adaptation
					.getString(VcmGlobalContentCatalogPaths._Root_ContentItem._Base_Description)
					.trim()
					.replaceAll("\n\r", ""));
		}
		contentItem.setProvenance(
			(String) AdaptationUtil.followFK(
				adaptation,
				VcmGlobalContentCatalogPaths._Root_ContentItem._Provenance,
				VcmCommonReferencePaths._Root_Provenance._Base_Name));
	
		String typicalLength = resolveTime(
			adaptation.getDate(VcmGlobalContentCatalogPaths._Root_ContentItem._TypicalLength));
		contentItem.setTypical_length(typicalLength);
	
		contentItem.setCountry_of_origin(
			(String) AdaptationUtil.followFK(
				adaptation,
				VcmGlobalContentCatalogPaths._Root_ContentItem._CountryOfOrigin,
				VcmCommonReferencePaths._Root_Country._Code2));
	
		List<Adaptation> genreAdaptations = AdaptationUtil
			.followFKList(adaptation, VcmGlobalContentCatalogPaths._Root_ContentItem._Genre);
		List<Genre> genres = new ArrayList<Genre>();
		for (Adaptation genreAdaptation : genreAdaptations)
		{
			Genre genre = new Genre();
			genre.setAuthority(
				(String) AdaptationUtil.followFK(
					genreAdaptation,
					VcmCommonReferencePaths._Root_Genre._GenreAuthority,
					VcmCommonReferencePaths._Root_GenreDetails_GenreAuthority._Base_Name));
			genre.setGenre(
				genreAdaptation.getString(VcmCommonReferencePaths._Root_Genre._Base_Name));
			genre.setType(
				(String) AdaptationUtil.followFK(
					genreAdaptation,
					VcmCommonReferencePaths._Root_Genre._GenreType,
					VcmCommonReferencePaths._Root_GenreDetails_GenreType._Base_Name));
			genres.add(genre);
		}
		contentItem.setGenres(genres);
	
		// set Incident Number
		String incidentNumber = adaptation
			.getString(VcmGlobalContentCatalogPaths._Root_ContentItem._IncidentNumber);
		if (incidentNumber != null)
		{
			contentItem.setIncident_number(incidentNumber);
		}
	
		List<Adaptation> ratingAdaptations = AdaptationUtil.getLinkedRecordList(
			adaptation,
			VcmGlobalContentCatalogPaths._Root_ContentItem._Ratings);
		List<ContentRating> contentRatings = new ArrayList<ContentRating>();
		for (Adaptation ratingAdaptation : ratingAdaptations)
		{
			ContentRating contentRating = new ContentRating();
			contentRating.setAuthority(
				(String) AdaptationUtil.followFK(
					AdaptationUtil.followFK(
						ratingAdaptation,
						VcmGlobalContentCatalogPaths._Root_ContentRating._Rating),
					VcmCommonReferencePaths._Root_Rating._RatingAuthority,
					VcmCommonReferencePaths._Root_RatingDetails_RatingAuthority._Base_Name));
			contentRating.setRating(
				(String) AdaptationUtil.followFK(
					ratingAdaptation,
					VcmGlobalContentCatalogPaths._Root_ContentRating._Rating,
					VcmCommonReferencePaths._Root_Rating._Base_Name));
			//Rating Descriptors is 0-* in the modeland in the CUR Event Schema
			List<Adaptation> ratingDescriptors = AdaptationUtil.followFKList(
				ratingAdaptation,
				VcmGlobalContentCatalogPaths._Root_ContentRating._RatingContentDescriptor);
			if (ratingDescriptors != null && !ratingDescriptors.isEmpty())
			{
				//Need to allow for multiple Descriptors here
				contentRating.setDescriptors(new ArrayList<String>());
				for (Adaptation ratingDescriptor : ratingDescriptors)
				{
					contentRating.getDescriptors().add(
						ratingDescriptor.getString(
							VcmCommonReferencePaths._Root_RatingDetails_RatingContentDescriptor._Base_Name));
				}
			}
			if (ratingAdaptation
				.getString(VcmGlobalContentCatalogPaths._Root_ContentRating._Advisory) != null)
			{
				contentRating.setAdvisory(
					ratingAdaptation
						.getString(VcmGlobalContentCatalogPaths._Root_ContentRating._Advisory)
						.trim()
						.replaceAll("\n\r", ""));
			}
			contentRatings.add(contentRating);
		}
		contentItem.setRatings(contentRatings);
	
		//construct ext_attributes
		if (endeavorType.equalsIgnoreCase(EndeavorTypes.EPISODE))
		{
			Supplemental episodeSupplemental = new Supplemental();
			Adaptation episodeSupplementalAdaptation = AdaptationUtil.followFK(
				adaptation,
				VcmGlobalContentCatalogPaths._Root_ContentItem._SupplementalInstance_Episode);
			if (episodeSupplementalAdaptation != null)
			{
				episodeSupplemental.setProduction_number(
					episodeSupplementalAdaptation.getString(
						VcmGlobalContentCatalogPaths._Root_ItemDetails_ItemSupplemental_EpisodeSupplemental._ProductionNumber));
				contentItem.setExt_attributes(episodeSupplemental);
			}
		}
		else if (endeavorType.equalsIgnoreCase(EndeavorTypes.MOVIE))
		{
			contentItem.setExt_attributes(null);
	
		}
		else if (endeavorType.equalsIgnoreCase(EndeavorTypes.SPECIAL))
		{
			contentItem.setExt_attributes(null);
		}
		else if (endeavorType.equalsIgnoreCase(EndeavorTypes.VERSION))
		{
			Supplemental versionSupplemental = new Supplemental();
			List<ProcessSpec> processSpecList = new ArrayList<ProcessSpec>();
			Adaptation versionSupplementalAdaptation = AdaptationUtil.followFK(
				adaptation,
				VcmGlobalContentCatalogPaths._Root_ContentItem._SupplementalInstance_Version);
			List<Adaptation> processSpecAdaptations = AdaptationUtil.getLinkedRecordList(
				versionSupplementalAdaptation,
				VcmGlobalContentCatalogPaths._Root_ItemDetails_ItemSupplemental_VersionSupplemental._ProcessSpecs);
			for (Adaptation processSpecAdaptation : processSpecAdaptations)
			{
				ProcessSpec processSpec = new ProcessSpec();
				processSpec.setMedia_process(
					(String) AdaptationUtil.followFK(
						processSpecAdaptation,
						VcmGlobalContentCatalogPaths._Root_ItemDetails_ProcessSpec._MediaProcess,
						VcmCommonReferencePaths._Root_MediaProcess._Base_Name));
	
				List<Adaptation> processSpecPropertyValuesAdaptations = AdaptationUtil
					.getLinkedRecordList(
						processSpecAdaptation,
						VcmGlobalContentCatalogPaths._Root_ItemDetails_ProcessSpec._ProcessSpecPropertyValues);
				List<ProcessSpecPropertyValue> processSpecPropertyValueList = new ArrayList<ProcessSpecPropertyValue>();
				for (Adaptation processSpecPropertyValuesAdaptation : processSpecPropertyValuesAdaptations)
				{
					String propertyValue = processSpecPropertyValuesAdaptation.getString(
						VcmGlobalContentCatalogPaths._Root_ItemDetails_ProcessSpecPropertyValue._PropertyValue);
					if (propertyValue == null)
						continue;
					ProcessSpecPropertyValue processSpecPropertyValue = new ProcessSpecPropertyValue();
					Adaptation objectProperty = AdaptationUtil.followFK(
						processSpecPropertyValuesAdaptation,
						VcmGlobalContentCatalogPaths._Root_ItemDetails_ProcessSpecPropertyValue._ObjectProperty);
					processSpecPropertyValue.setObject_property(
						objectProperty
							.getString(VcmCommonReferencePaths._Root_ObjectProperty._Base_Name));
					//for reference, find the table record and return its 'field' value
					if (ObjectPropertyTypes.REFERENCE.equals(
						objectProperty
							.getString(VcmCommonReferencePaths._Root_ObjectProperty._ObjectType)))
					{
						propertyValue = processSpecPropertyValuesAdaptation.getString(
							VcmGlobalContentCatalogPaths._Root_ItemDetails_ProcessSpecPropertyValue._PropertyValueReference);
						String tableName = objectProperty.getString(
							VcmCommonReferencePaths._Root_ObjectProperty._ReferenceTable);
						String fieldName = objectProperty.getString(
							VcmCommonReferencePaths._Root_ObjectProperty._ReferenceField);
						AdaptationTable table = ObjectPropertyTableConstraintEnumeration.getTable(
							objectProperty.getContainerTable().getContainerAdaptation(),
							tableName);
						Adaptation record = table
							.lookupAdaptationByPrimaryKey(PrimaryKey.parseString(propertyValue));
						propertyValue = String.valueOf(record.get(Path.SELF.add(fieldName)));
					}
					processSpecPropertyValue.setProperty_value(propertyValue);
					processSpecPropertyValueList.add(processSpecPropertyValue);
				}
				processSpec.setProperty_values(processSpecPropertyValueList);
				processSpecList.add(processSpec);
			}
			versionSupplemental.setProcess_specs(processSpecList);
			versionSupplemental.setOriginal_endeavor_vmid(
				versionSupplementalAdaptation.getString(
					VcmGlobalContentCatalogPaths._Root_ItemDetails_ItemSupplemental_VersionSupplemental.__originalEndeavorVmid));
			contentItem.setExt_attributes(versionSupplemental);
		}
	
		//	Container Relations for Main-contentItem types Episode, Movie and Special
		if (EndeavorTypes.MAIN_CONTENT.contains(endeavorType))
		{
			List<Adaptation> containerAdaptations = AdaptationUtil.getLinkedRecordList(
				adaptation,
				VcmGlobalContentCatalogPaths._Root_ContentItem._Containers);
			List<ContainerContentItem> relatedContainers = new ArrayList<ContainerContentItem>();
			for (Adaptation containerAdaptation : containerAdaptations)
			{
				ContainerContentItem relatedContainer = new ContainerContentItem();
				relatedContainer.setPredicate(
					containerAdaptation.getString(
						VcmGlobalContentCatalogPaths._Root_ContainerDetails_ContainerContentItem._Predicate));
				relatedContainer.setTarget_vmid(
					(String) AdaptationUtil.followFK(
						containerAdaptation,
						VcmGlobalContentCatalogPaths._Root_ContainerDetails_ContainerContentItem._ContentContainer,
						VcmGlobalContentCatalogPaths._Root_ContentContainer._VmId));
				relatedContainers.add(relatedContainer);
			}
			contentItem.setContainer_relations(relatedContainers);
		}
		else
		{
			//	Item Relations for Sub-contentItem types Clip, Module and Version
			List<Adaptation> contentItemRelatedContentItemAdaptations = AdaptationUtil
				.getLinkedRecordList(
					adaptation,
					VcmGlobalContentCatalogPaths._Root_ContentItem._TargetContentItems);
			List<ContentItemRelatedContentItem> relatedItems = new ArrayList<ContentItemRelatedContentItem>();
			for (Adaptation contentItemRelatedContentItemAdaptation : contentItemRelatedContentItemAdaptations)
			{
				ContentItemRelatedContentItem relatedContentItem = new ContentItemRelatedContentItem();
				relatedContentItem.setPredicate(
					contentItemRelatedContentItemAdaptation.getString(
						VcmGlobalContentCatalogPaths._Root_ItemDetails_ContentItemRelatedContentItem._Predicate));
				relatedContentItem.setTarget_vmid(
					(String) AdaptationUtil.followFK(
						contentItemRelatedContentItemAdaptation,
						VcmGlobalContentCatalogPaths._Root_ItemDetails_ContentItemRelatedContentItem._TargetContentItem,
						VcmGlobalContentCatalogPaths._Root_ContentItem._VmId));
				relatedItems.add(relatedContentItem);
			}
			contentItem.setItem_relations(relatedItems);
		}
	
		List<Adaptation> relatedChannelAdaptations = AdaptationUtil.getLinkedRecordList(
			adaptation,
			VcmGlobalContentCatalogPaths._Root_ContentItem._Channels);
		List<ContentChannel> contentChannels = new ArrayList<ContentChannel>();
		for (Adaptation relatedChannelAdaptation : relatedChannelAdaptations)
		{
			ContentChannel contentChannel = new ContentChannel();
			contentChannel.setPredicate(
				relatedChannelAdaptation
					.getString(VcmGlobalContentCatalogPaths._Root_ContentChannel._Predicate));
			contentChannel.setTarget_vmid(
				(String) AdaptationUtil.followFK(
					relatedChannelAdaptation,
					VcmGlobalContentCatalogPaths._Root_ContentChannel._TargetChannel,
					VcmCommonReferencePaths._Root_Channel._VmId));
			contentChannels.add(contentChannel);
		}
		contentItem.setChannel_relations(contentChannels);
	
		//	Handle Third Party Company Relationships
		List<Adaptation> thirdPartyCompanyRelationshipsAdaptations = AdaptationUtil
			.getLinkedRecordList(
				adaptation,
				VcmGlobalContentCatalogPaths._Root_ContentItem._ThirdPartyCompanyRelationships);
		List<ThirdPartyCompanyRelationship> thirdPartyCompanyRelationships = new ArrayList<ThirdPartyCompanyRelationship>();
		for (Adaptation thirdPartyCompanyRelationshipsAdaptation : thirdPartyCompanyRelationshipsAdaptations)
		{
			ThirdPartyCompanyRelationship thirdPartyCompanyRelationship = new ThirdPartyCompanyRelationship();
			thirdPartyCompanyRelationship.setThird_party_role(
				thirdPartyCompanyRelationshipsAdaptation.getString(
					VcmGlobalContentCatalogPaths._Root_ThirdPartyCompanyRelationship._ThirdPartyRole));
			thirdPartyCompanyRelationship.setThird_party_company(
				(String) AdaptationUtil.followFK(
					thirdPartyCompanyRelationshipsAdaptation,
					VcmGlobalContentCatalogPaths._Root_ThirdPartyCompanyRelationship._ThirdPartyCompany,
					VcmCommonReferencePaths._Root_ThirdPartyCompany._Base_Name));
			thirdPartyCompanyRelationships.add(thirdPartyCompanyRelationship);
		}
		contentItem.setThird_party_company_relationships(thirdPartyCompanyRelationships);
	
		//	Handle Third Party Identifiers
		List<Adaptation> thirdPartyIdentifierAdaptations = AdaptationUtil.getLinkedRecordList(
			adaptation,
			VcmGlobalContentCatalogPaths._Root_ContentItem._ThirdPartyIdentifiers);
		List<ThirdPartyIdentifier> thirdPartyIdentifiers = new ArrayList<ThirdPartyIdentifier>();
		for (Adaptation thirdPartyIdentifierAdaptation : thirdPartyIdentifierAdaptations)
		{
			ThirdPartyIdentifier thirdPartyIdentifier = new ThirdPartyIdentifier();
			thirdPartyIdentifier.setIdentification_authority(
				(String) AdaptationUtil.followFK(
					thirdPartyIdentifierAdaptation,
					VcmGlobalContentCatalogPaths._Root_ThirdPartyIdentifier._IdentificationAuthority,
					VcmCommonReferencePaths._Root_IdentificationAuthority._Base_Name));
			thirdPartyIdentifier.setIdentifier(
				thirdPartyIdentifierAdaptation.getString(
					VcmGlobalContentCatalogPaths._Root_ThirdPartyIdentifier._Identifier));
			thirdPartyIdentifiers.add(thirdPartyIdentifier);
		}
		contentItem.setThird_party_identifiers(thirdPartyIdentifiers);
	
		// Reference Channel
		contentItem.setRef_channel(
			(String) AdaptationUtil.followFK(
				adaptation,
				VcmGlobalContentCatalogPaths._Root_ContentItem._ReferenceChannel,
				VcmCommonReferencePaths._Root_Channel._Base_Name));
	
		return contentItem;
	}
	/**
	 * 
	 * @param opType
	 * @param vmid
	 * @param endeavorType
	 * @return
	 */
	/*
	private static RemoveContent constructJsonForContentRemoval(
		String opType,
		String vmid,
		String endeavorType)
	{
		RemoveContent removeContent = new RemoveContent();
		removeContent.set$schema(VcmGccConstants.SCHEMA_DEFINITION);
		removeContent.setOp(opType);
		removeContent.setType(StringUtils.uncapitalize(endeavorType));
		removeContent.setVmid(vmid);
		return removeContent;
	}
	
	/**
	 * 
	 * @param message
	 * @throws Exception
	 */
	/*
	public static void sendMessage(String message, String routingKey) throws Exception
	{
		Properties props;
		String propertiesFile = System
			.getProperty(VcmGccConstants.VIACOM_MDM_PROPERTY_FILE_SYSTEM_PROPERTY);
		if (propertiesFile != null)
		{
			PropertyFileHelper propertyHelper = new PropertyFileHelper(propertiesFile);
			props = propertyHelper.getProperties();
			String rabbitMQHost = props.getProperty(VcmGccConstants.RABBIT_MQ_HOST_PROPERTY);
			if (rabbitMQHost == null || "".equals(rabbitMQHost))
			{
				throw new IOException(
					"Value must be specified for property "
						+ VcmGccConstants.RABBIT_MQ_HOST_PROPERTY + ".");
			}
			String rabbitMQUsername = props
				.getProperty(VcmGccConstants.RABBIT_MQ_USERNAME_PROPERTY);
			if (rabbitMQUsername == null || "".equals(rabbitMQUsername))
			{
				throw new IOException(
					"Value must be specified for property "
						+ VcmGccConstants.RABBIT_MQ_USERNAME_PROPERTY + ".");
			}
			String rabbitMQPassword = props
				.getProperty(VcmGccConstants.RABBIT_MQ_PASSWORD_PROPERTY);
			if (rabbitMQPassword == null || "".equals(rabbitMQPassword))
			{
				throw new IOException(
					"Value must be specified for property "
						+ VcmGccConstants.RABBIT_MQ_PASSWORD_PROPERTY + ".");
			}
			String rabbitMQPort = props.getProperty(VcmGccConstants.RABBIT_MQ_PORT_PROPERTY);
			if (rabbitMQPort == null || "".equals(rabbitMQPort))
			{
				throw new IOException(
					"Value must be specified for property "
						+ VcmGccConstants.RABBIT_MQ_PORT_PROPERTY + ".");
			}
			String rabbitMQVirtualHost = props
				.getProperty(VcmGccConstants.RABBIT_MQ_VIRTUALHOST_PROPERTY);
			if (rabbitMQVirtualHost == null || "".equals(rabbitMQVirtualHost))
			{
				throw new IOException(
					"Value must be specified for property "
						+ VcmGccConstants.RABBIT_MQ_VIRTUALHOST_PROPERTY + ".");
			}
			String rabbitMQExchangeName = props
				.getProperty(VcmGccConstants.RABBIT_MQ_EXCHANGE_NAME_PROPERTY);
			if (rabbitMQExchangeName == null || "".equals(rabbitMQExchangeName))
			{
				throw new IOException(
					"Value must be specified for property "
						+ VcmGccConstants.RABBIT_MQ_EXCHANGE_NAME_PROPERTY + ".");
			}
			String rabbitMQExchangeType = props
				.getProperty(VcmGccConstants.RABBIT_MQ_EXCHANGE_TYPE_PROPERTY);
			if (rabbitMQExchangeType == null || "".equals(rabbitMQExchangeType))
			{
				throw new IOException(
					"Value must be specified for property "
						+ VcmGccConstants.RABBIT_MQ_EXCHANGE_TYPE_PROPERTY + ".");
			}
			String rabbitMQConnectionTimeout = props
				.getProperty(VcmGccConstants.RABBIT_MQ_CONNECTION_TIMEOUT_PROPERTY);
			if (rabbitMQConnectionTimeout == null || "".equals(rabbitMQConnectionTimeout))
			{
				throw new IOException(
					"Value must be specified for property "
						+ VcmGccConstants.RABBIT_MQ_CONNECTION_TIMEOUT_PROPERTY + ".");
			}
			LoggingCategory.getKernel().info(
				" [PublishUtil] Connection Parameters: Host -  '" + rabbitMQHost + " Port: "
					+ rabbitMQPort + " Username: " + rabbitMQUsername + " VirtualHost: "
					+ rabbitMQVirtualHost);
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
	
		}
	}
	
	/**
	 * 
	 * @param time
	 * @return
	 */

	/*
	private static String resolveTime(Date time)
	{
		String timeLength = null;
		if (time != null)
		{
			SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
			timeLength = df.format(time);
		}
		return timeLength;
	}
	
	private static Set<Adaptation> sortEndeavorsBeforePublish(
		Set<Adaptation> contentAdaptationSet,
		boolean containers)
	{
		Set<Adaptation> sortedAdaptationSet = new LinkedHashSet<Adaptation>();
		if (containers)
		{
			Set<Adaptation> franchiseAdaptationSet = new LinkedHashSet<Adaptation>();
			Set<Adaptation> seriesAdaptationSet = new LinkedHashSet<Adaptation>();
			Set<Adaptation> seasonAdaptationSet = new LinkedHashSet<Adaptation>();
			for (Adaptation adaptation : contentAdaptationSet)
			{
				if (EndeavorTypes.FRANCHISE.equals(
					adaptation.getString(
						VcmGlobalContentCatalogPaths._Root_ContentContainer._ContentContainerType)))
				{
					franchiseAdaptationSet.add(adaptation);
				}
				else if (EndeavorTypes.SERIES.equals(
					adaptation.getString(
						VcmGlobalContentCatalogPaths._Root_ContentContainer._ContentContainerType)))
				{
					seriesAdaptationSet.add(adaptation);
				}
				else if (EndeavorTypes.SEASON.equals(
					adaptation.getString(
						VcmGlobalContentCatalogPaths._Root_ContentContainer._ContentContainerType)))
				{
					seasonAdaptationSet.add(adaptation);
				}
			}
			sortedAdaptationSet.addAll(franchiseAdaptationSet);
			sortedAdaptationSet.addAll(seriesAdaptationSet);
			sortedAdaptationSet.addAll(seasonAdaptationSet);
		}
		else
		{
			Set<Adaptation> sortedVersionSet = new LinkedHashSet<Adaptation>();
			Set<Adaptation> nonVersionSet = new LinkedHashSet<Adaptation>();
			for (Adaptation adaptation : contentAdaptationSet)
			{
				if (!addVersion(adaptation, sortedVersionSet, contentAdaptationSet, null))
				{
					nonVersionSet.add(adaptation);
				}
			}
			sortedAdaptationSet.addAll(nonVersionSet);
			//reverse the order of the versions
			List<Adaptation> versions = new ArrayList<Adaptation>(sortedVersionSet);
			Collections.reverse(versions);
			sortedAdaptationSet.addAll(versions);
		}
		return sortedAdaptationSet;
	}
	
	private static boolean addVersion(
		Adaptation version,
		Set<Adaptation> parentFirstSet,
		Set<Adaptation> contentAdaptationSet,
		Set<Adaptation> visited)
	{
		if (!VcmGccUtilities.isVersion(version))
			return false;
		if (visited == null)
			visited = new HashSet<Adaptation>();
		if (!visited.add(version))
			return false;
		Adaptation parent = VcmGccUtilities.getParentRecordForChild(version);
		addVersion(parent, parentFirstSet, contentAdaptationSet, visited);
		if (contentAdaptationSet.contains(version))
			parentFirstSet.add(version);
		return true;
	} */
}
