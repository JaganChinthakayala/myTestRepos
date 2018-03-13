package com.hertz.mdm._hertz.scripttask;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hertz.mdm._hertz.constants.HtzConstants;
import com.hertz.mdm._hertz.constants.HtzWorkflowConstants;
import com.hertz.mdm.location.path.LocationPaths;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationHome;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.PrimaryKey;
import com.orchestranetworks.instance.HomeKey;
import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.ps.workflow.WorkflowConstants;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.service.LoggingCategory;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.comparison.DifferenceBetweenHomes;
import com.orchestranetworks.service.comparison.DifferenceBetweenInstances;
import com.orchestranetworks.service.comparison.DifferenceBetweenOccurrences;
import com.orchestranetworks.service.comparison.DifferenceBetweenTables;
import com.orchestranetworks.service.comparison.DifferenceHelper;
import com.orchestranetworks.service.comparison.ExtraOccurrenceOnRight;
import com.orchestranetworks.workflow.ScriptTask;
import com.orchestranetworks.workflow.ScriptTaskContext;

public class GetChildDataSpaceInitialSnapshotDeltaScriptTask extends ScriptTask
{

	private StringBuilder deltaResults = null;

	@Override
	public void executeScript(ScriptTaskContext context) throws OperationException
	{
		// Find the difference between the working dataspace and its initial snapshot
		String dataSpaceId = context.getVariableString(WorkflowConstants.PARAM_WORKING_DATA_SPACE);
		Repository repository = context.getRepository();
		AdaptationHome workingDataSpace = repository.lookupHome(HomeKey.forBranchName(dataSpaceId));
		if (workingDataSpace == null)
		{
			throw OperationException
				.createError("DataSpace " + dataSpaceId + " has not been found");
		}
		deltaResults = computeDelta(workingDataSpace.getParent(), workingDataSpace, context);
		context.setVariableString(
			HtzWorkflowConstants.DATA_CONTEXT_PARM_DELTA_RESULT,
			deltaResults.toString());
	}

	/**
	 * 
	 * @param initialSnapshot
	 * @param workingDataSpace
	 * @param context
	 */
	private StringBuilder computeDelta(
		final AdaptationHome initialSnapshot,
		final AdaptationHome workingDataSpace,
		ScriptTaskContext context)
	{
		deltaResults = new StringBuilder();
		StringBuilder createDeltaResults = new StringBuilder();
		StringBuilder updateDeltaResults = new StringBuilder();
		// Table paths to be handles separately
		final List<Path> parentTablePaths = new ArrayList<Path>();
		parentTablePaths.add(LocationPaths._Root_Location.getPathInSchema());
		final List<Path> relatedTablePaths = new ArrayList<Path>();

		//TODO - These are the related location tables
		relatedTablePaths.add(LocationPaths._Root_LocationData_Address.getPathInSchema());
		relatedTablePaths.add(LocationPaths._Root_LocationData_EMailAddress.getPathInSchema());
		relatedTablePaths.add(LocationPaths._Root_LocationData_Phone.getPathInSchema());

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
						if (parentTablePaths.contains(differenceBetweenTable.getPathOnLeft())
							|| relatedTablePaths.contains(differenceBetweenTable.getPathOnLeft()))
						{
							@SuppressWarnings("unchecked")
							final List<DifferenceBetweenOccurrences> deltaOccurrences = differenceBetweenTable
								.getDeltaOccurrences();
							for (final Iterator<DifferenceBetweenOccurrences> iteratorOnOccurences = deltaOccurrences
								.iterator(); iteratorOnOccurences.hasNext();)
							{
								final DifferenceBetweenOccurrences differenceBetweenOccurrence = iteratorOnOccurences
									.next();

								updateDeltaResults = buildDeltaResultsString(
									updateDeltaResults,
									differenceBetweenOccurrence.getOccurrenceOnRight(),
									HtzConstants.UPDATE_OCCURRENCES_SEPARATOR,
									parentTablePaths,
									relatedTablePaths);
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
										operationType = HtzConstants.CREATE_OCCURRENCES_SEPARATOR;
										createDeltaResults = buildDeltaResultsString(
											createDeltaResults,
											extraOccurence,
											operationType,
											parentTablePaths,
											relatedTablePaths);
									}
									else
									{
										operationType = HtzConstants.UPDATE_OCCURRENCES_SEPARATOR;
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
										HtzConstants.UPDATE_OCCURRENCES_SEPARATOR,
										parentTablePaths,
										relatedTablePaths);
								}
							}
						}
					}
				}
			}
		}
		deltaResults = createDeltaResults.append(updateDeltaResults);
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
	private StringBuilder buildDeltaResultsString(
		StringBuilder deltaResults,
		Adaptation deltaAdaptation,
		String operationTypeSeparator,
		List<Path> parentTablePaths,
		List<Path> relatedTablePaths)
	{

		Adaptation locationAdaptation = null;
		if (parentTablePaths.contains(deltaAdaptation.getContainerTable().getTablePath()))
		{
			//Creates T[/root/ContentContainer]
			deltaResults.append(HtzConstants.TABLE_ADAPTATION_STARTS_WITH);
			deltaResults.append(deltaAdaptation.getContainerTable().getTablePath().format());
			deltaResults.append(HtzConstants.TABLE_ADAPTATION_ENDS_WITH);

			//Creates :U{5}
			deltaResults.append(HtzConstants.TABLE_ADAPTATION_SEPARATOR);
			deltaResults.append(operationTypeSeparator);
			deltaResults.append(deltaAdaptation.getOccurrencePrimaryKey().format());
			deltaResults.append("}");
		}
		else if (relatedTablePaths.contains(deltaAdaptation.getContainerTable().getTablePath()))
		{
			if (deltaAdaptation.getContainerTable()
				.getTablePath()
				.equals(LocationPaths._Root_LocationData_Address.getPathInSchema()))
			{
				locationAdaptation = AdaptationUtil
					.followFK(deltaAdaptation, LocationPaths._Root_LocationData_Address._Location);
			}
			else if (deltaAdaptation.getContainerTable().getTablePath().equals(
				LocationPaths._Root_LocationData_EMailAddress.getPathInSchema()))
			{
				locationAdaptation = AdaptationUtil.followFK(
					deltaAdaptation,
					LocationPaths._Root_LocationData_EMailAddress._Location);
			}
			else if (deltaAdaptation.getContainerTable().getTablePath().equals(

				LocationPaths._Root_LocationData_Phone.getPathInSchema()))
			{
				locationAdaptation = AdaptationUtil
					.followFK(deltaAdaptation, LocationPaths._Root_LocationData_Phone._Location);
			}
			deltaResults.append(HtzConstants.TABLE_ADAPTATION_STARTS_WITH);
			deltaResults.append(locationAdaptation.getContainerTable().getTablePath().format());
			deltaResults.append(HtzConstants.TABLE_ADAPTATION_ENDS_WITH);
			deltaResults.append(HtzConstants.TABLE_ADAPTATION_SEPARATOR);
			deltaResults.append(operationTypeSeparator);
			deltaResults.append(locationAdaptation.getOccurrencePrimaryKey().format());
			deltaResults.append("}");

		}
		return deltaResults;
	}
}
