package com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.repository;

import java.util.ArrayList;
import java.util.List;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationHome;
import com.onwbp.adaptation.AdaptationName;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.PrimaryKey;
import com.onwbp.adaptation.XPathExpressionHelper;
import com.orchestranetworks.instance.HomeKey;
import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.schema.Path;

/**
 * The Class RepositoryUtils.
 *
 * @author ATI
 * @since 1.0.0
 */
public final class RepositoryUtils
{
	/**
	 * Gets the data set.
	 *
	 * @param pDataSpace the data space
	 * @param pDataSetName the data set name
	 * @return the data set
	 * @since 1.0.0
	 */
	public static Adaptation getDataSet(final AdaptationHome pDataSpace, final String pDataSetName)
	{
		if (pDataSpace == null || pDataSetName == null)
		{
			return null;
		}
		return pDataSpace.findAdaptationOrNull(AdaptationName.forName(pDataSetName));
	}

	/**
	 * Gets the data set.
	 *
	 * @param pDataSpaceName the data space name
	 * @param pDataSetName the data set name
	 * @return the data set
	 * @since 1.0.0
	 */
	public static Adaptation getDataSet(final String pDataSpaceName, final String pDataSetName)
	{
		AdaptationHome dataSpace = RepositoryUtils.getDataSpace(pDataSpaceName);
		return RepositoryUtils.getDataSet(dataSpace, pDataSetName);
	}

	/**
	 * Gets the "parent" data set from the parent data space (ie. initial snapshot of the child data
	 * space).<br>
	 * <br>
	 * Careful, it is not the parent with the data set inheritance. For inherited data set, check
	 * {@link Adaptation#getParent() dataSet.getParent()}.
	 *
	 * @param pDataSet the data set
	 * @return the parent data set, null if not found or if pTable parameter is null.
	 * @since 1.2.0
	 */
	public static Adaptation getDataSetFromParentDataSpace(final Adaptation pDataSet)
	{
		if (pDataSet == null || pDataSet.isTableOccurrence())
		{
			return null;
		}

		AdaptationName dataSetName = pDataSet.getAdaptationName();

		AdaptationHome dataSpace = pDataSet.getHome();
		AdaptationHome parentDataSpace = dataSpace.getParent();
		if (parentDataSpace == null)
		{
			return null;
		}

		return parentDataSpace.findAdaptationOrNull(dataSetName);
	}

	/**
	 * Gets the data space.
	 *
	 * @param pRepository the repository
	 * @param pDataSpaceName the data space name
	 * @return the data space
	 * @since 1.0.0
	 */
	public static AdaptationHome getDataSpace(
		final Repository pRepository,
		final String pDataSpaceName)
	{
		if (pDataSpaceName == null || pRepository == null)
		{
			return null;
		}
		return pRepository.lookupHome(HomeKey.forBranchName(pDataSpaceName));
	}

	/**
	 * Gets the data space.
	 *
	 * @param pDataSpaceName the data space name
	 * @return the data space
	 * @since 1.0.0
	 */
	public static AdaptationHome getDataSpace(final String pDataSpaceName)
	{
		Repository repository = Repository.getDefault();
		return RepositoryUtils.getDataSpace(repository, pDataSpaceName);
	}

	/**
	 * Gets the record.
	 *
	 * @param pDataSet the data set
	 * @param pTablePath the table path
	 * @param pRecordPK the record pk
	 * @return the record
	 * @since 1.0.0
	 */
	public static Adaptation getRecord(
		final Adaptation pDataSet,
		final String pTablePath,
		final PrimaryKey pRecordPK)
	{
		AdaptationTable table = RepositoryUtils.getTable(pDataSet, pTablePath);
		return RepositoryUtils.getRecord(table, pRecordPK);
	}

	/**
	 * Gets the record.
	 *
	 * @param pDataSet the data set
	 * @param pTablePath the table path
	 * @param pRecordXPath the record x path
	 * @return the record
	 * @since 1.0.0
	 */
	public static Adaptation getRecord(
		final Adaptation pDataSet,
		final String pTablePath,
		final String pRecordXPath)
	{
		AdaptationTable table = RepositoryUtils.getTable(pDataSet, pTablePath);
		return RepositoryUtils.getRecord(table, pRecordXPath);
	}

	/**
	 * Gets the record.
	 *
	 * @param pDataSpace the data space
	 * @param pDataSetName the data set name
	 * @param pTablePath the table path
	 * @param pRecordPK the record pk
	 * @return the record
	 * @since 1.0.0
	 */
	public static Adaptation getRecord(
		final AdaptationHome pDataSpace,
		final String pDataSetName,
		final String pTablePath,
		final PrimaryKey pRecordPK)
	{
		AdaptationTable table = RepositoryUtils.getTable(pDataSpace, pDataSetName, pTablePath);
		return RepositoryUtils.getRecord(table, pRecordPK);
	}

	/**
	 * Gets the record.
	 *
	 * @param pDataSpace the data space
	 * @param pDataSetName the data set name
	 * @param pTablePath the table path
	 * @param pRecordXPath the record x path
	 * @return the record
	 * @since 1.0.0
	 */
	public static Adaptation getRecord(
		final AdaptationHome pDataSpace,
		final String pDataSetName,
		final String pTablePath,
		final String pRecordXPath)
	{
		AdaptationTable table = RepositoryUtils.getTable(pDataSpace, pDataSetName, pTablePath);
		return RepositoryUtils.getRecord(table, pRecordXPath);
	}

	/**
	 * Gets the record.
	 *
	 * @param pTable the table
	 * @param pRecordPK the record pk
	 * @return the record
	 * @since 1.0.0
	 */
	public static Adaptation getRecord(final AdaptationTable pTable, final PrimaryKey pRecordPK)
	{
		if (pTable == null || pRecordPK == null)
		{
			return null;
		}
		return pTable.lookupAdaptationByPrimaryKey(pRecordPK);
	}

	/**
	 * Gets the record.
	 *
	 * @param pTable the table
	 * @param pRecordXPath the record x path
	 * @return the record
	 * @since 1.0.0
	 */
	public static Adaptation getRecord(final AdaptationTable pTable, final String pRecordXPath)
	{
		if (pTable == null || pRecordXPath == null)
		{
			return null;
		}
		String predicate = XPathExpressionHelper.getPredicateForXPath(pRecordXPath);
		return pTable.lookupFirstRecordMatchingPredicate(predicate);
	}

	/**
	 * Gets the record.
	 *
	 * @param pDataSpaceName the data space name
	 * @param pDataSetName the data set name
	 * @param pTablePath the table path
	 * @param pRecordPK the record pk
	 * @return the record
	 * @since 1.0.0
	 */
	public static Adaptation getRecord(
		final String pDataSpaceName,
		final String pDataSetName,
		final String pTablePath,
		final PrimaryKey pRecordPK)
	{
		AdaptationTable table = RepositoryUtils.getTable(pDataSpaceName, pDataSetName, pTablePath);
		return RepositoryUtils.getRecord(table, pRecordPK);
	}

	/**
	 * Gets the record.
	 *
	 * @param pDataSpaceName the data space name
	 * @param pDataSetName the data set name
	 * @param pTablePath the table path
	 * @param pRecordXPath the record x path
	 * @return the record
	 * @since 1.0.0
	 */
	public static Adaptation getRecord(
		final String pDataSpaceName,
		final String pDataSetName,
		final String pTablePath,
		final String pRecordXPath)
	{
		AdaptationTable table = RepositoryUtils.getTable(pDataSpaceName, pDataSetName, pTablePath);
		return RepositoryUtils.getRecord(table, pRecordXPath);
	}

	/**
	 * Gets the records.
	 *
	 * @param pDataSet the data set
	 * @param pTablePath the table path
	 * @return the records
	 * @since 1.0.0
	 */
	public static List<Adaptation> getRecords(final Adaptation pDataSet, final String pTablePath)
	{
		AdaptationTable table = RepositoryUtils.getTable(pDataSet, pTablePath);
		return RepositoryUtils.getRecords(table);
	}

	/**
	 * Gets the records.
	 *
	 * @param pDataSet the data set
	 * @param pTablePath the table path
	 * @param pPredicate the predicate
	 * @return the records
	 * @since 1.0.0
	 */
	public static List<Adaptation> getRecords(
		final Adaptation pDataSet,
		final String pTablePath,
		final String pPredicate)
	{
		AdaptationTable table = RepositoryUtils.getTable(pDataSet, pTablePath);
		return RepositoryUtils.getRecords(table, pPredicate);
	}

	/**
	 * Gets the records.
	 *
	 * @param pDataSpace the data space
	 * @param pDataSetName the data set name
	 * @param pTablePath the table path
	 * @return the records
	 * @since 1.0.0
	 */
	public static List<Adaptation> getRecords(
		final AdaptationHome pDataSpace,
		final String pDataSetName,
		final String pTablePath)
	{
		AdaptationTable table = RepositoryUtils.getTable(pDataSpace, pDataSetName, pTablePath);
		return RepositoryUtils.getRecords(table);
	}

	/**
	 * Gets the records.
	 *
	 * @param pDataSpace the data space
	 * @param pDataSetName the data set name
	 * @param pTablePath the table path
	 * @param pPredicate the predicate
	 * @return the records
	 * @since 1.0.0
	 */
	public static List<Adaptation> getRecords(
		final AdaptationHome pDataSpace,
		final String pDataSetName,
		final String pTablePath,
		final String pPredicate)
	{
		AdaptationTable table = RepositoryUtils.getTable(pDataSpace, pDataSetName, pTablePath);
		return RepositoryUtils.getRecords(table, pPredicate);
	}

	/**
	 * Gets the records.
	 *
	 * @param pTable the table
	 * @return the records
	 * @since 1.0.0
	 */
	public static List<Adaptation> getRecords(final AdaptationTable pTable)
	{
		return RepositoryUtils.getRecords(pTable, null);
	}

	/**
	 * Gets the records.
	 *
	 * @param pTable the table
	 * @param pPredicate the predicate
	 * @return the records
	 * @since 1.0.0
	 */
	@SuppressWarnings("unchecked")
	public static List<Adaptation> getRecords(final AdaptationTable pTable, final String pPredicate)
	{
		List<Adaptation> records = new ArrayList<Adaptation>();
		if (pTable == null)
		{
			return records;
		}
		records = pTable.selectOccurrences(pPredicate);
		return records;
	}

	/**
	 * Gets the records.
	 *
	 * @param pDataSpaceName the data space name
	 * @param pDataSetName the data set name
	 * @param pTablePath the table path
	 * @return the records
	 * @since 1.0.0
	 */
	public static List<Adaptation> getRecords(
		final String pDataSpaceName,
		final String pDataSetName,
		final String pTablePath)
	{
		AdaptationTable table = RepositoryUtils.getTable(pDataSpaceName, pDataSetName, pTablePath);
		return RepositoryUtils.getRecords(table);
	}

	/**
	 * Gets the records.
	 *
	 * @param pDataSpaceName the data space name
	 * @param pDataSetName the data set name
	 * @param pTablePath the table path
	 * @param pPredicate the predicate
	 * @return the records
	 * @since 1.0.0
	 */
	public static List<Adaptation> getRecords(
		final String pDataSpaceName,
		final String pDataSetName,
		final String pTablePath,
		final String pPredicate)
	{
		AdaptationTable table = RepositoryUtils.getTable(pDataSpaceName, pDataSetName, pTablePath);
		return RepositoryUtils.getRecords(table, pPredicate);
	}

	/**
	 * Gets the snapshot.
	 *
	 * @param pRepository the repository
	 * @param pSnapshotName the snapshot name
	 * @return the snapshot
	 * @since 1.0.0
	 */
	public static AdaptationHome getSnapshot(
		final Repository pRepository,
		final String pSnapshotName)
	{
		if (pSnapshotName == null || pRepository == null)
		{
			return null;
		}
		return pRepository.lookupHome(HomeKey.forVersionName(pSnapshotName));
	}

	/**
	 * Gets the snapshot.
	 *
	 * @param pSnapshotName the snapshot name
	 * @return the snapshot
	 * @since 1.0.0
	 */
	public static AdaptationHome getSnapshot(final String pSnapshotName)
	{
		Repository repository = Repository.getDefault();
		return RepositoryUtils.getSnapshot(repository, pSnapshotName);
	}

	/**
	 * Gets the table.
	 *
	 * @param pDataSet the data set
	 * @param pTablePath the table path
	 * @return the table
	 * @since 1.0.0
	 */
	public static AdaptationTable getTable(final Adaptation pDataSet, final String pTablePath)
	{
		if (pDataSet == null || pTablePath == null)
		{
			return null;
		}
		return pDataSet.getTable(Path.ROOT.add(pTablePath));
	}

	/**
	 * Gets the table.
	 *
	 * @param pDataSpace the data space
	 * @param pDataSetName the data set name
	 * @param pTablePath the table path
	 * @return the table
	 * @since 1.0.0
	 */
	public static AdaptationTable getTable(
		final AdaptationHome pDataSpace,
		final String pDataSetName,
		final String pTablePath)
	{
		Adaptation dataSet = RepositoryUtils.getDataSet(pDataSpace, pDataSetName);
		return RepositoryUtils.getTable(dataSet, pTablePath);
	}

	/**
	 * Gets the table.
	 *
	 * @param pDataSpaceName the data space name
	 * @param pDataSetName the data set name
	 * @param pTablePath the table path
	 * @return the table
	 * @since 1.0.0
	 */
	public static AdaptationTable getTable(
		final String pDataSpaceName,
		final String pDataSetName,
		final String pTablePath)
	{
		Adaptation dataSet = RepositoryUtils.getDataSet(pDataSpaceName, pDataSetName);
		return RepositoryUtils.getTable(dataSet, pTablePath);
	}

	/**
	 * Gets the "parent" table from the parent data space (ie. initial snapshot of the child data
	 * space).
	 *
	 * @param pTable the table
	 * @return the parent table, null if not found or if pTable parameter is null.
	 * @since 1.2.0
	 */
	public static AdaptationTable getTableFromParentDataSpace(final AdaptationTable pTable)
	{
		if (pTable == null)
		{
			return null;
		}

		Adaptation dataSet = pTable.getContainerAdaptation();
		AdaptationName dataSetName = dataSet.getAdaptationName();

		AdaptationHome dataSpace = dataSet.getHome();
		AdaptationHome parentDataSpace = dataSpace.getParent();
		if (parentDataSpace == null)
		{
			return null;
		}

		Adaptation parentDataSet = parentDataSpace.findAdaptationOrNull(dataSetName);
		if (parentDataSet == null)
		{
			return null;
		}

		return parentDataSet.getTable(pTable.getTablePath());
	}

	private RepositoryUtils()
	{
	}
}
