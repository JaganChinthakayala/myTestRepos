//package com.hertz.mdm.admin.scripttask;

/*
import java.util.*;

import com.onwbp.adaptation.*;
import com.orchestranetworks.instance.*;
import com.orchestranetworks.ps.util.*;
import com.orchestranetworks.ps.workflow.*;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.service.*;
import com.orchestranetworks.service.comparison.*;
import com.orchestranetworks.workflow.*;

*/
//public class GetChildDataSpaceInitialSnapshotDeltaScriptTask extends ScriptTask
//{

/*
private StringBuilder deltaResults = null;
private static final String CONTENT_CONTAINER_FK = "contentContainer";
private static final String CONTENT_ITEM_FK = "contentItem";
private static final String SOURCE_CONTENT_CONTAINER_FK = "sourceContentContainer";
private static final String SOURCE_CONTENT_ITEM_FK = "sourceContentItem";
private static final String CONTENT_VMID_PATH = "vmId";
/*
	@Override
	public void executeScript(ScriptTaskContext context) throws OperationException
	{
		// Find the difference between the working dataspace and its initial snapshot
		String dataSpaceId = context.getVariableString(WorkflowConstants.PARAM_WORKING_DATA_SPACE);
		Repository repository = context.getRepository();
		AdaptationHome workingDataSpace = repository.lookupHome(HomeKey.forBranchName(dataSpaceId));
		if (workingDataSpace == null)
		{
			throw OperationException.createError("DataSpace " + dataSpaceId + " has not been found");
		}
		deltaResults = computeDelta(workingDataSpace.getParent(), workingDataSpace, context);
		context.setVariableString(GccWorkflowConstants.PARAM_DELTA_RESULTS, deltaResults.toString());
	}

	/**
	 * 
	 * @param initialSnapshot
	 * @param workingDataSpace
	 * @param context
	 */
/*
private StringBuilder computeDelta(
	final AdaptationHome initialSnapshot,
	final AdaptationHome workingDataSpace,
	ScriptTaskContext context)
{
	deltaResults = new StringBuilder();
	StringBuilder createDeltaResults = new StringBuilder();
	StringBuilder updateDeltaResults = new StringBuilder();
	StringBuilder deleteDeltaResults = new StringBuilder();
	// Table paths to be handles separately
	final List<Path> parentTablePaths = new ArrayList<Path>();
	parentTablePaths.add(VcmGlobalContentCatalogPaths._Root_ContentContainer.getPathInSchema());
	parentTablePaths.add(VcmGlobalContentCatalogPaths._Root_ContentItem.getPathInSchema());
	final List<Path> relatedTablePaths = new ArrayList<Path>();
	relatedTablePaths.add(
		VcmGlobalContentCatalogPaths._Root_ContainerDetails_ContainerRelatedContainer
			.getPathInSchema());
	relatedTablePaths.add(
		VcmGlobalContentCatalogPaths._Root_ContainerDetails_ContainerContentItem
			.getPathInSchema());
	relatedTablePaths.add(
		VcmGlobalContentCatalogPaths._Root_ItemDetails_ContentItemRelatedContentItem
			.getPathInSchema());
	final List<Path> ignoreTablePaths = new ArrayList<Path>();
	ignoreTablePaths
		.add(VcmGlobalContentCatalogPaths._Root_ItemDetails_ProcessSpec.getPathInSchema());
	ignoreTablePaths.add(
		VcmGlobalContentCatalogPaths._Root_ItemDetails_ProcessSpecPropertyValue
			.getPathInSchema());
	final DifferenceBetweenHomes differenceBetweenDataSpaces = DifferenceHelper
		.compareHomes(initialSnapshot, workingDataSpace, false);
	if (!differenceBetweenDataSpaces.isEmpty())
	{
		@SuppressWarnings("unchecked")
		final List<DifferenceBetweenInstances> differenceBetweenInstances = differenceBetweenDataSpaces
			.getDeltaInstances();
		for (final Iterator<DifferenceBetweenInstances> iterator = differenceBetweenInstances
			.iterator(); iterator.hasNext();)
		{
			final DifferenceBetweenInstances differenceBetweenInstance = iterator.next();
			if (!differenceBetweenInstances.isEmpty())
			{
				@SuppressWarnings("unchecked")
				final List<DifferenceBetweenTables> deltaTables = differenceBetweenInstance
					.getDeltaTables();
				for (final Iterator<DifferenceBetweenTables> iteratorOnTables = deltaTables
					.iterator(); iteratorOnTables.hasNext();)
				{
					final DifferenceBetweenTables differenceBetweenTable = iteratorOnTables
						.next();
					if (!ignoreTablePaths.contains(differenceBetweenTable.getPathOnLeft()))
					{
						@SuppressWarnings("unchecked")
						final List<DifferenceBetweenOccurrences> deltaOccurrences = differenceBetweenTable
							.getDeltaOccurrences();
						for (final Iterator<DifferenceBetweenOccurrences> iteratorOnOccurences = deltaOccurrences
							.iterator(); iteratorOnOccurences.hasNext();)
						{
							final DifferenceBetweenOccurrences differenceBetweenOccurrence = iteratorOnOccurences
								.next();
							// Update messages not to be sent if the changes are only in the Matching metadata fields
							if (!isMatchingMetaDataFieldsChange(
								differenceBetweenOccurrence,
								parentTablePaths))
							{
								updateDeltaResults = buildDeltaResultsString(
									updateDeltaResults,
									differenceBetweenOccurrence.getOccurrenceOnRight(),
									VcmGccConstants.UPDATE_OCCURRENCES_SEPARATOR,
									parentTablePaths,
									relatedTablePaths);
							}
						}

						@SuppressWarnings("unchecked")
						final List<ExtraOccurrenceOnRight> extraOccurrencesOnRight = differenceBetweenTable
							.getExtraOccurrencesOnRight();
						for (final Iterator<ExtraOccurrenceOnRight> iteratorOnOccurences = extraOccurrencesOnRight
							.iterator(); iteratorOnOccurences.hasNext();)
						{
							final ExtraOccurrenceOnRight extraOccurrenceOnRight = iteratorOnOccurences
								.next();
							// We have to check if this record hasn't been
							// created in the parent DataSpace by another
							// process
							// If it s the case, we'll do an update
							// Otherwise, a creation

							// Get the record
							final Adaptation extraOccurence = extraOccurrenceOnRight
								.getExtraAdaptationOnRight();
							final PrimaryKey occurencePk = extraOccurence
								.getOccurrencePrimaryKey();
							final AdaptationHome parentDataSpace = workingDataSpace
								.getParentBranch();
							final Adaptation container = parentDataSpace.findAdaptationOrNull(
								extraOccurence.getContainer().getAdaptationName());
							final AdaptationTable table = container
								.getTable(extraOccurence.getContainerTable().getTablePath());
							final Adaptation occurenceInParentDataSpace = table
								.lookupAdaptationByPrimaryKey(occurencePk);
							if (occurenceInParentDataSpace == null)
							{
								String operationType = null;
								if (parentTablePaths.contains(
									extraOccurence.getContainerTable().getTablePath()))
								{
									operationType = VcmGccConstants.CREATE_OCCURRENCES_SEPARATOR;
									createDeltaResults = buildDeltaResultsString(
										createDeltaResults,
										extraOccurence,
										operationType,
										parentTablePaths,
										relatedTablePaths);
								}
								else
								{
									operationType = VcmGccConstants.UPDATE_OCCURRENCES_SEPARATOR;
									updateDeltaResults = buildDeltaResultsString(
										updateDeltaResults,
										extraOccurence,
										operationType,
										parentTablePaths,
										relatedTablePaths);
								}
							}
							else
							{
								updateDeltaResults = buildDeltaResultsString(
									updateDeltaResults,
									extraOccurence,
									VcmGccConstants.UPDATE_OCCURRENCES_SEPARATOR,
									parentTablePaths,
									relatedTablePaths);
							}
						}

						// Deleted records
						@SuppressWarnings("unchecked")
						final List<ExtraOccurrenceOnLeft> extraOccurrencesOnLeft = differenceBetweenTable
							.getExtraOccurrencesOnLeft();
						for (final Iterator<ExtraOccurrenceOnLeft> iteratorOnOccurences = extraOccurrencesOnLeft
							.iterator(); iteratorOnOccurences.hasNext();)
						{
							final ExtraOccurrenceOnLeft extraOccurrenceOnLeft = iteratorOnOccurences
								.next();
							// Need to confirm the behavior for Deletes
							String operationType = null;
							if (parentTablePaths.contains(
								extraOccurrenceOnLeft.getExtraAdaptationOnLeft()
									.getContainerTable()
									.getTablePath()))
							{
								operationType = VcmGccConstants.DELETE_OCCURRENCES_SEPARATOR;
								deleteDeltaResults = buildDeltaResultsString(
									deleteDeltaResults,
									extraOccurrenceOnLeft.getExtraAdaptationOnLeft(),
									operationType,
									parentTablePaths,
									relatedTablePaths);
							}
							else
							{
								operationType = VcmGccConstants.UPDATE_OCCURRENCES_SEPARATOR;
								updateDeltaResults = buildDeltaResultsString(
									updateDeltaResults,
									extraOccurrenceOnLeft.getExtraAdaptationOnLeft(),
									operationType,
									parentTablePaths,
									relatedTablePaths);
							}
						}
					}
				}
			}
		}
	}
	deltaResults = createDeltaResults.append(updateDeltaResults).append(deleteDeltaResults);
	LoggingCategory.getKernel()
		.info("Delta results formed in ScriptTask is: " + deltaResults.toString());
	return deltaResults;
}
/**
 * 
 * @param deltaAdaptation
 * @param parentTablePaths
 * @param relatedTablePaths
 * @return
 */
/*
private StringBuilder buildDeltaResultsString(
	StringBuilder deltaResults,
	Adaptation deltaAdaptation,
	String operationTypeSeparator,
	List<Path> parentTablePaths,
	List<Path> relatedTablePaths)
{

	Adaptation endeavorAdaptation = null;
	if (parentTablePaths.contains(deltaAdaptation.getContainerTable().getTablePath()))
	{
		if (operationTypeSeparator.equals(VcmGccConstants.DELETE_OCCURRENCES_SEPARATOR))
		{
			deltaResults.append(VcmGccConstants.TABLE_ADAPTATION_STARTS_WITH);
			if (deltaAdaptation.getContainerTable().getTablePath().equals(
				VcmGlobalContentCatalogPaths._Root_ContentContainer.getPathInSchema()))
			{
				deltaResults.append(
					deltaAdaptation.getString(
						VcmGlobalContentCatalogPaths._Root_ContentContainer._ContentContainerType));
			}
			else
			{
				deltaResults.append(
					deltaAdaptation.getString(
						VcmGlobalContentCatalogPaths._Root_ContentItem._ContentItemType));
			}

			deltaResults.append(VcmGccConstants.TABLE_ADAPTATION_ENDS_WITH);
			deltaResults.append(VcmGccConstants.TABLE_ADAPTATION_SEPARATOR);
			deltaResults.append(operationTypeSeparator);
			deltaResults.append(deltaAdaptation.getString(Path.parse(CONTENT_VMID_PATH)));
			deltaResults.append("}");
		}
		else
		{
			deltaResults.append(VcmGccConstants.TABLE_ADAPTATION_STARTS_WITH);
			deltaResults.append(deltaAdaptation.getContainerTable().getTablePath().format());
			deltaResults.append(VcmGccConstants.TABLE_ADAPTATION_ENDS_WITH);
			deltaResults.append(VcmGccConstants.TABLE_ADAPTATION_SEPARATOR);
			deltaResults.append(operationTypeSeparator);
			deltaResults.append(deltaAdaptation.getOccurrencePrimaryKey().format());
			deltaResults.append("}");
			//deltaResults.append(VcmGccConstants.ADAPTATION_SEPARATOR);
		}
	}
	else if (deltaAdaptation.getContainerTable().getTablePath().format().contains(
		VcmGlobalContentCatalogPaths._Root_ContainerDetails.format())
		&& !relatedTablePaths.contains(deltaAdaptation.getContainerTable().getTablePath()))
	{
		endeavorAdaptation = AdaptationUtil
			.followFK(deltaAdaptation, Path.parse(CONTENT_CONTAINER_FK));
		deltaResults.append(VcmGccConstants.TABLE_ADAPTATION_STARTS_WITH);
		deltaResults.append(endeavorAdaptation.getContainerTable().getTablePath().format());
		deltaResults.append(VcmGccConstants.TABLE_ADAPTATION_ENDS_WITH);
		deltaResults.append(VcmGccConstants.TABLE_ADAPTATION_SEPARATOR);
		deltaResults.append(operationTypeSeparator);
		deltaResults.append(endeavorAdaptation.getOccurrencePrimaryKey().format());
		deltaResults.append("}");
		//deltaResults.append(VcmGccConstants.ADAPTATION_SEPARATOR);
	}
	else if (deltaAdaptation.getContainerTable().getTablePath().format().contains(
		VcmGlobalContentCatalogPaths._Root_ItemDetails.format())
		&& !relatedTablePaths.contains(deltaAdaptation.getContainerTable().getTablePath()))
	{
		endeavorAdaptation = AdaptationUtil
			.followFK(deltaAdaptation, Path.parse(CONTENT_ITEM_FK));
		deltaResults.append(VcmGccConstants.TABLE_ADAPTATION_STARTS_WITH);
		deltaResults.append(endeavorAdaptation.getContainerTable().getTablePath().format());
		deltaResults.append(VcmGccConstants.TABLE_ADAPTATION_ENDS_WITH);
		deltaResults.append(VcmGccConstants.TABLE_ADAPTATION_SEPARATOR);
		deltaResults.append(operationTypeSeparator);
		deltaResults.append(endeavorAdaptation.getOccurrencePrimaryKey().format());
		deltaResults.append("}");
		//deltaResults.append(VcmGccConstants.ADAPTATION_SEPARATOR);
	}
	else if (relatedTablePaths.contains(deltaAdaptation.getContainerTable().getTablePath()))
	{
		if (deltaAdaptation.getContainerTable().getTablePath().equals(
			VcmGlobalContentCatalogPaths._Root_ContainerDetails_ContainerRelatedContainer
				.getPathInSchema()))
		{
			endeavorAdaptation = AdaptationUtil
				.followFK(deltaAdaptation, Path.parse(SOURCE_CONTENT_CONTAINER_FK));
		}
		else if (deltaAdaptation.getContainerTable().getTablePath().equals(
			VcmGlobalContentCatalogPaths._Root_ContainerDetails_ContainerContentItem
				.getPathInSchema()))
		{
			endeavorAdaptation = AdaptationUtil
				.followFK(deltaAdaptation, Path.parse(CONTENT_ITEM_FK));
		}
		else if (deltaAdaptation.getContainerTable().getTablePath().equals(
			VcmGlobalContentCatalogPaths._Root_ItemDetails_ContentItemRelatedContentItem
				.getPathInSchema()))
		{
			endeavorAdaptation = AdaptationUtil
				.followFK(deltaAdaptation, Path.parse(SOURCE_CONTENT_ITEM_FK));
		}
		deltaResults.append(VcmGccConstants.TABLE_ADAPTATION_STARTS_WITH);
		deltaResults.append(endeavorAdaptation.getContainerTable().getTablePath().format());
		deltaResults.append(VcmGccConstants.TABLE_ADAPTATION_ENDS_WITH);
		deltaResults.append(VcmGccConstants.TABLE_ADAPTATION_SEPARATOR);
		deltaResults.append(operationTypeSeparator);
		deltaResults.append(endeavorAdaptation.getOccurrencePrimaryKey().format());
		deltaResults.append("}");

	}
	else
	{
		endeavorAdaptation = AdaptationUtil
			.followFK(deltaAdaptation, Path.parse(CONTENT_CONTAINER_FK));
		//one of these 2 FK references. Check this logic.
		if (endeavorAdaptation == null)
		{
			endeavorAdaptation = AdaptationUtil
				.followFK(deltaAdaptation, Path.parse(CONTENT_ITEM_FK));
		}
		deltaResults.append(VcmGccConstants.TABLE_ADAPTATION_STARTS_WITH);
		deltaResults.append(endeavorAdaptation.getContainerTable().getTablePath().format());
		deltaResults.append(VcmGccConstants.TABLE_ADAPTATION_ENDS_WITH);
		deltaResults.append(VcmGccConstants.TABLE_ADAPTATION_SEPARATOR);
		deltaResults.append(operationTypeSeparator);
		deltaResults.append(endeavorAdaptation.getOccurrencePrimaryKey().format());
		deltaResults.append("}");
	}
	return deltaResults;
}

@SuppressWarnings("unchecked")
private boolean isMatchingMetaDataFieldsChange(
	DifferenceBetweenOccurrences differenceBetweenOccurrence,
	List<Path> parentTablePaths)
{
	Adaptation deltaAdaptation = differenceBetweenOccurrence.getOccurrenceOnRight();
	if (parentTablePaths.contains(deltaAdaptation.getContainerTable().getTablePath()))
	{
		List<NodeDifference> nodeDifferences = differenceBetweenOccurrence.getNodeDifferences();
		List<String> nonMetaDataPaths = new ArrayList<String>();
		for (NodeDifference nodeDifference : nodeDifferences)
		{
			if (!nodeDifference.getRightValuePath().format().contains("DaqaMetaData"))
			{
				nonMetaDataPaths.add(nodeDifference.getRightValuePath().format());
			}
		}
		return nonMetaDataPaths.isEmpty();
	}
return false;}}*/
