package com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.repository;

import java.util.ArrayList;
import java.util.List;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationHome;
import com.onwbp.adaptation.AdaptationName;
import com.onwbp.adaptation.AdaptationTable;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.service.comparison.DifferenceBetweenOccurrences;
import com.orchestranetworks.service.comparison.DifferenceBetweenTables;
import com.orchestranetworks.service.comparison.DifferenceHelper;
import com.orchestranetworks.service.comparison.ExtraOccurrenceOnLeft;
import com.orchestranetworks.service.comparison.ExtraOccurrenceOnRight;

/**
 * The Class ComparisonUtils provides methods to check and get creations, updates and deletions
 * between tables (same table of the same data model).<br>
 * <br>
 * Especially used to compare with the parent data space (ie initial snapshot of the child data
 * space).
 *
 * @author ATI
 * @since 1.0.0
 */
public final class ComparisonUtils
{
	/**
	 * Compare the table with the same table in the parent data space. Gets the created, updated and
	 * deleted records.
	 *
	 * @param pTable the table
	 * @return the comparison result
	 * @throws IllegalArgumentException if the tables is not defined or if it does not have a
	 *             "parent" table.
	 * @since 1.2.0
	 */
	public static ComparisonResult compareTables(final AdaptationTable pTable)
		throws IllegalArgumentException
	{
		AdaptationTable parentTable = RepositoryUtils.getTableFromParentDataSpace(pTable);
		return ComparisonUtils.compareTables(parentTable, pTable, true, true, true);
	}

	/**
	 * Compare the two tables. Gets the created, updated and deleted records from the right table
	 * compared to the left table.
	 *
	 * @param pLeftTable the left table
	 * @param pRightTable the right table
	 * @return the comparison result
	 * @throws IllegalArgumentException if one of the specified tables is not defined or if they do
	 *             not have the same primary key definition.
	 * @since 1.0.0
	 */
	public static ComparisonResult compareTables(
		final AdaptationTable pLeftTable,
		final AdaptationTable pRightTable) throws IllegalArgumentException
	{
		return ComparisonUtils.compareTables(pLeftTable, pRightTable, true, true, true);
	}

	/**
	 * Compare table with the parent in the parent data space.
	 *
	 * @param pDataSpace the data space
	 * @param pDataSetName the data set name
	 * @param pTablePath the table path
	 * @return the comparison result
	 * @since 1.0.0
	 * @deprecated Use {@link #compareTables(AdaptationTable) compareTables(AdaptationTable)}
	 *             instead.
	 */
	@Deprecated
	public static ComparisonResult compareTableWithParent(
		final AdaptationHome pDataSpace,
		final AdaptationName pDataSetName,
		final Path pTablePath)
	{
		return ComparisonUtils
			.compareTableWithParent(pDataSpace, pDataSetName, pTablePath, true, true, true);
	}

	/**
	 * Gets the creations compared to the parent of the data space passed as argument.
	 *
	 * @param pDataSpace the data space
	 * @param pDataSetName the data set name
	 * @param pTablePath the table path
	 * @return the created records
	 * @since 1.0.0
	 * @deprecated Use {@link #getCreations(AdaptationTable) getCreations(AdaptationTable)} instead.
	 */
	@Deprecated
	public static List<Adaptation> getCreations(
		final AdaptationHome pDataSpace,
		final AdaptationName pDataSetName,
		final Path pTablePath)
	{
		ComparisonResult result = ComparisonUtils
			.compareTableWithParent(pDataSpace, pDataSetName, pTablePath, true, false, false);
		return result.getCreations();
	}

	/**
	 * Gets the created records in the table compared to the table of the parent data space.
	 *
	 * @param pTable the table
	 * @return the list of created records
	 * @since 1.2.0
	 */
	public static List<Adaptation> getCreations(final AdaptationTable pTable)
	{
		AdaptationTable parentTable = RepositoryUtils.getTableFromParentDataSpace(pTable);
		return ComparisonUtils.getCreations(parentTable, pTable);
	}

	/**
	 * Gets the created records from the right table compared to the left table.
	 *
	 * @param pLeftTable the left table
	 * @param pRightTable the right table
	 * @return the list of created records
	 * @throws IllegalArgumentException if one of the specified tables is not defined or if they do
	 *             not have the same primary key definition.
	 * @since 1.0.0
	 */
	public static List<Adaptation> getCreations(
		final AdaptationTable pLeftTable,
		final AdaptationTable pRightTable) throws IllegalArgumentException
	{
		ComparisonResult result = ComparisonUtils
			.compareTables(pLeftTable, pRightTable, true, false, false);
		return result.getCreations();
	}

	/**
	 * Gets the deleted records in the table compared to the table of the parent data space.
	 *
	 * @param pTable the table
	 * @return the list of deleted records
	 * @since 1.4.0
	 */
	public static List<Adaptation> getDeletions(final AdaptationTable pTable)
	{
		AdaptationTable parentTable = RepositoryUtils.getTableFromParentDataSpace(pTable);
		return ComparisonUtils.getDeletions(parentTable, pTable);
	}

	/**
	 * Gets the deleted records from the right table compared to the left table.
	 *
	 * @param pLeftTable the left table
	 * @param pRightTable the right table
	 * @return the list of deleted records
	 * @throws IllegalArgumentException if one of the specified tables is not defined or if they do
	 *             not have the same primary key definition.
	 * @since 1.4.0
	 */
	public static List<Adaptation> getDeletions(
		final AdaptationTable pLeftTable,
		final AdaptationTable pRightTable) throws IllegalArgumentException
	{
		ComparisonResult result = ComparisonUtils
			.compareTables(pLeftTable, pRightTable, false, false, true);
		return result.getDeletions();
	}

	/**
	 * Gets the updates compared to the parent of the data space passed as argument.
	 *
	 * @param pDataSpace the data space
	 * @param pDataSetName the data set name
	 * @param pTablePath the table path
	 * @return the list of updated records
	 * @since 1.0.0
	 * @deprecated Use {@link #getUpdates(AdaptationTable) getUpdates(AdaptationTable)} instead.
	 */
	@Deprecated
	public static List<Adaptation> getUpdates(
		final AdaptationHome pDataSpace,
		final AdaptationName pDataSetName,
		final Path pTablePath)
	{
		ComparisonResult result = ComparisonUtils
			.compareTableWithParent(pDataSpace, pDataSetName, pTablePath, false, true, false);
		return result.getUpdates();
	}

	/**
	 * Gets the updated records in the table compared to the table of the parent data space.
	 *
	 * @param pTable the table
	 * @return the list of updated records
	 * @since 1.2.0
	 */
	public static List<Adaptation> getUpdates(final AdaptationTable pTable)
	{
		AdaptationTable parentTable = RepositoryUtils.getTableFromParentDataSpace(pTable);
		return ComparisonUtils.getUpdates(parentTable, pTable);
	}

	/**
	 * Gets the updated records from the right table compared to the left table.
	 *
	 * @param pLeftTable the left table
	 * @param pRightTable the right table
	 * @return the list of updated records
	 * @throws IllegalArgumentException if one of the specified tables is not defined or if they do
	 *             not have the same primary key definition.
	 * @since 1.0.0
	 */
	public static List<Adaptation> getUpdates(
		final AdaptationTable pLeftTable,
		final AdaptationTable pRightTable) throws IllegalArgumentException
	{
		ComparisonResult result = ComparisonUtils
			.compareTables(pLeftTable, pRightTable, false, true, false);
		return result.getUpdates();
	}

	/**
	 * Checks whether the table has created records compared to its parent data space.
	 *
	 * @param pTable the table
	 * @return true, if there are created records.
	 * @since 1.2.0
	 */
	public static boolean hasCreations(final AdaptationTable pTable)
	{
		AdaptationTable parentTable = RepositoryUtils.getTableFromParentDataSpace(pTable);
		return ComparisonUtils.hasCreations(parentTable, pTable);
	}

	/**
	 * Checks whether the right table has created records compared to the left table.
	 *
	 * @param pLeftTable the left table
	 * @param pRightTable the right table
	 * @return true, if there are created records.
	 * @throws IllegalArgumentException if one of the specified tables is not defined or if they do
	 *             not have the same primary key definition.
	 * @since 1.2.0
	 */
	public static boolean hasCreations(
		final AdaptationTable pLeftTable,
		final AdaptationTable pRightTable) throws IllegalArgumentException
	{
		DifferenceBetweenTables differences = ComparisonUtils
			.getDifferencesBetweenTables(pLeftTable, pRightTable);
		if (differences == null)
		{
			return false;
		}
		int nbCreations = differences.getExtraOccurrencesOnRightSize();
		if (nbCreations > 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Checks whether the table has deleted records compared to its parent data space.
	 *
	 * @param pTable the table
	 * @return true, if there are deleted records.
	 * @since 1.4.0
	 */
	public static boolean hasDeletions(final AdaptationTable pTable)
	{
		AdaptationTable parentTable = RepositoryUtils.getTableFromParentDataSpace(pTable);
		return ComparisonUtils.hasCreations(parentTable, pTable);
	}

	/**
	 * Checks whether the right table has deleted records compared to the left table.
	 *
	 * @param pLeftTable the left table
	 * @param pRightTable the right table
	 * @return true, if there are deleted records.
	 * @throws IllegalArgumentException if one of the specified tables is not defined or if they do
	 *             not have the same primary key definition.
	 * @since 1.4.0
	 */
	public static boolean hasDeletions(
		final AdaptationTable pLeftTable,
		final AdaptationTable pRightTable) throws IllegalArgumentException
	{
		DifferenceBetweenTables differences = ComparisonUtils
			.getDifferencesBetweenTables(pLeftTable, pRightTable);
		if (differences == null)
		{
			return false;
		}
		int nbDeletions = differences.getExtraOccurrencesOnLeftSize();
		if (nbDeletions > 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Checks whether the table has updated records compared to its parent data space.
	 *
	 * @param pTable the table
	 * @return true, if there are updated records.
	 * @since 1.2.0
	 */
	public static boolean hasUpdates(final AdaptationTable pTable)
	{
		AdaptationTable parentTable = RepositoryUtils.getTableFromParentDataSpace(pTable);
		return ComparisonUtils.hasUpdates(parentTable, pTable);
	}

	/**
	 * Checks whether the right table has updated records compared to the left table.
	 *
	 * @param pLeftTable the left table
	 * @param pRightTable the right table
	 * @return true, if there are updated records.
	 * @throws IllegalArgumentException if one of the specified tables is not defined or if they do
	 *             not have the same primary key definition.
	 * @since 1.2.0
	 */
	public static boolean hasUpdates(
		final AdaptationTable pLeftTable,
		final AdaptationTable pRightTable) throws IllegalArgumentException
	{
		DifferenceBetweenTables differences = ComparisonUtils
			.getDifferencesBetweenTables(pLeftTable, pRightTable);
		if (differences == null)
		{
			return false;
		}
		int nbUpdates = differences.getDeltaOccurrencesSize();
		if (nbUpdates > 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Checks whether the record has been created compared to its parent data space.
	 *
	 * @param pRecord the record
	 * @return true, if the record has been created
	 * @since 1.2.0
	 */
	public static boolean isCreated(final Adaptation pRecord)
	{
		if (pRecord == null)
		{
			return false;
		}

		AdaptationTable table = pRecord.getContainerTable();
		AdaptationTable parentTable = RepositoryUtils.getTableFromParentDataSpace(table);
		return ComparisonUtils.isCreated(pRecord, parentTable);
	}

	/**
	 * Checks whether the record has been created compared to the other table. Checks if is created.
	 *
	 * @param pRecord the record
	 * @param pOtherTable the other table
	 * @return true, if the record has been created
	 * @throws IllegalArgumentException if the record does not have the same primary key definition
	 *             as the table.
	 * @since 1.2.0
	 */
	public static boolean isCreated(final Adaptation pRecord, final AdaptationTable pOtherTable)
		throws IllegalArgumentException
	{
		if (pRecord == null || pOtherTable == null)
		{
			return false;
		}

		AdaptationTable table = pRecord.getContainerTable();

		DifferenceBetweenTables differences = ComparisonUtils
			.getDifferencesBetweenTables(pOtherTable, table);

		ExtraOccurrenceOnRight extraOccurrence = differences
			.getExtraOccurrenceOnRight(pRecord.getOccurrencePrimaryKey());
		if (extraOccurrence == null)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	/**
	 * Checks whether the record has been deleted compared to the other table.
	 *
	 * @param pRecord the record
	 * @param pOtherTable the other table
	 * @return true, if the record has been deleted
	 * @throws IllegalArgumentException if the record does not have the same primary key definition
	 *             as the table.
	 * @since 1.4.0
	 */
	public static boolean isDeleted(final Adaptation pRecord, final AdaptationTable pOtherTable)
		throws IllegalArgumentException
	{
		if (pRecord == null || pOtherTable == null)
		{
			return false;
		}

		AdaptationTable table = pRecord.getContainerTable();

		DifferenceBetweenTables differences = ComparisonUtils
			.getDifferencesBetweenTables(table, pOtherTable);

		ExtraOccurrenceOnLeft extraOccurrence = differences
			.getExtraOccurrenceOnLeft(pRecord.getOccurrencePrimaryKey());
		if (extraOccurrence == null)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	/**
	 * Checks whether the record has been created compared to its parent data space.
	 *
	 * @param pRecord the record
	 * @return true, if the record has been updated
	 * @since 1.2.0
	 */
	public static boolean isUpdated(final Adaptation pRecord)
	{
		if (pRecord == null)
		{
			return false;
		}

		AdaptationTable table = pRecord.getContainerTable();
		AdaptationTable parentTable = RepositoryUtils.getTableFromParentDataSpace(table);
		return ComparisonUtils.isUpdated(pRecord, parentTable);
	}

	/**
	 * Checks whether the record has been updated compared to the other table.
	 *
	 * @param pRecord the record
	 * @param pOtherTable the other table
	 * @return true, if the record has been updated
	 * @throws IllegalArgumentException if the record does not have the same primary key definition
	 *             as the table.
	 * @since 1.2.0
	 */
	public static boolean isUpdated(final Adaptation pRecord, final AdaptationTable pOtherTable)
		throws IllegalArgumentException
	{
		if (pRecord == null || pOtherTable == null)
		{
			return false;
		}

		AdaptationTable table = pRecord.getContainerTable();

		DifferenceBetweenTables differences = ComparisonUtils
			.getDifferencesBetweenTables(pOtherTable, table);

		DifferenceBetweenOccurrences deltaOccurrence = differences
			.getDeltaOccurrence(pRecord.getOccurrencePrimaryKey());
		if (deltaOccurrence == null)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	/**
	 * Compare tables.
	 *
	 * @param pLeftTable the left table
	 * @param pRightTable the right table
	 * @param pGetCreations true to get creations
	 * @param pGetUpdates true to get updates
	 * @param pGetDeletions true to get deletions
	 * @return the comparison result
	 * @throws IllegalArgumentException if one of the specified tables is not defined or if they do
	 *             not have the same primary key definition.
	 * @since 1.0.0
	 */
	@SuppressWarnings("unchecked")
	private static ComparisonResult compareTables(
		final AdaptationTable pLeftTable,
		final AdaptationTable pRightTable,
		final boolean pGetCreations,
		final boolean pGetUpdates,
		final boolean pGetDeletions) throws IllegalArgumentException
	{
		ComparisonResult result = new ComparisonResult();

		DifferenceBetweenTables differenceBetweenTables = ComparisonUtils
			.getDifferencesBetweenTables(pLeftTable, pRightTable);

		if (pGetCreations)
		{
			ArrayList<Adaptation> creations = new ArrayList<Adaptation>();
			List<ExtraOccurrenceOnRight> extraOccurrences = differenceBetweenTables
				.getExtraOccurrencesOnRight();
			for (ExtraOccurrenceOnRight ocurrence : extraOccurrences)
			{
				creations.add(ocurrence.getExtraOccurrence());
			}
			result.setCreations(creations);
		}

		if (pGetUpdates)
		{
			ArrayList<Adaptation> updates = new ArrayList<Adaptation>();
			List<DifferenceBetweenOccurrences> deltaOccurrences = differenceBetweenTables
				.getDeltaOccurrences();
			for (DifferenceBetweenOccurrences occurrence : deltaOccurrences)
			{
				updates.add(occurrence.getOccurrenceOnRight());
			}
			result.setUpdates(updates);
		}

		if (pGetDeletions)
		{
			ArrayList<Adaptation> deletions = new ArrayList<Adaptation>();
			List<ExtraOccurrenceOnLeft> extraOccurrences = differenceBetweenTables
				.getExtraOccurrencesOnLeft();
			for (ExtraOccurrenceOnLeft ocurrence : extraOccurrences)
			{
				deletions.add(ocurrence.getExtraOccurrence());
			}
			result.setDeletions(deletions);
		}

		return result;
	}

	/**
	 * Compare table with parent.
	 *
	 * @param pDataSpace the data space
	 * @param pDataSetName the data set name
	 * @param pTablePath the table path
	 * @param pGetCreations true to get creations
	 * @param pGetUpdates true to get updates
	 * @param pGetDeletions true to get deletions
	 * @return the comparison result
	 * @since 1.0.0
	 * @deprecated Use
	 *             {@link #compareTables(AdaptationTable, AdaptationTable, boolean, boolean, boolean)
	 *             compareTables(AdaptationTable, AdaptationTable, boolean, boolean, boolean)}
	 *             instead.
	 */
	@Deprecated
	private static ComparisonResult compareTableWithParent(
		final AdaptationHome pDataSpace,
		final AdaptationName pDataSetName,
		final Path pTablePath,
		final boolean pGetCreations,
		final boolean pGetUpdates,
		final boolean pGetDeletions)
	{
		if (pDataSpace == null || pDataSetName == null || pTablePath == null)
		{
			return null;
		}
		Adaptation dataSet = pDataSpace.findAdaptationOrNull(pDataSetName);
		if (dataSet == null)
		{
			return null;
		}

		AdaptationHome parentDataSpace = pDataSpace.getParent();
		if (parentDataSpace == null)
		{
			return null;
		}
		Adaptation parentDataSet = parentDataSpace.findAdaptationOrNull(pDataSetName);
		if (parentDataSet == null)
		{
			return null;
		}

		AdaptationTable table = dataSet.getTable(pTablePath);
		AdaptationTable parentTable = parentDataSet.getTable(pTablePath);

		if (table == null || parentTable == null)
		{
			return null;
		}

		return ComparisonUtils
			.compareTables(parentTable, table, pGetCreations, pGetUpdates, pGetDeletions);
	}

	/**
	 * Gets the differences between tables.
	 *
	 * @param pLeftTable the left table
	 * @param pRightTable the right table
	 * @return the differences between tables
	 * @throws IllegalArgumentException if one of the specified tables is not defined or if they do
	 *             not have the same primary key definition.
	 * @since 1.2.0
	 */
	private static DifferenceBetweenTables getDifferencesBetweenTables(
		final AdaptationTable pLeftTable,
		final AdaptationTable pRightTable) throws IllegalArgumentException
	{
		return DifferenceHelper.compareAdaptationTables(pLeftTable, pRightTable, true);
	}

	private ComparisonUtils()
	{
	}

}
