package com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.repository;

import java.util.ArrayList;
import java.util.List;

import com.onwbp.adaptation.Adaptation;

/**
 * The Class ComparisonResult.
 *
 * @author ATI
 * @since 1.0.0
 */
public final class ComparisonResult
{
	private List<Adaptation> creations;
	private List<Adaptation> updates;
	private List<Adaptation> deletions;

	/**
	 * Instantiate a new Comparison Result.
	 *
	 * @since 1.0.0
	 */
	public ComparisonResult()
	{
	}

	/**
	 * Instantiates a new comparison result.
	 *
	 * @param pCreations the list of created records
	 * @param pUpdates the list of updated records
	 * @since 1.0.0
	 */
	public ComparisonResult(final List<Adaptation> pCreations, final List<Adaptation> pUpdates)
	{
		this.creations = pCreations;
		this.updates = pUpdates;
	}

	/**
	 * Instantiates a new comparison result.
	 *
	 * @param pCreations the list of created records
	 * @param pUpdates the list of updated records
	 * @param pDeletions the list of deleted record
	 * @since 1.4.0
	 */
	public ComparisonResult(
		final List<Adaptation> pCreations,
		final List<Adaptation> pUpdates,
		final List<Adaptation> pDeletions)
	{
		this.creations = pCreations;
		this.updates = pUpdates;
		this.deletions = pDeletions;
	}

	/**
	 * Gets the list of created records.
	 *
	 * @return the list of created records
	 * @since 1.0.0
	 */
	public List<Adaptation> getCreations()
	{
		if (this.creations == null)
		{
			return new ArrayList<Adaptation>();
		}
		return this.creations;
	}

	/**
	 * Gets the list of deleted records.
	 *
	 * @return the list of deleted records
	 * @since 1.4.0
	 */
	public List<Adaptation> getDeletions()
	{
		if (this.deletions == null)
		{
			return new ArrayList<Adaptation>();
		}
		return this.deletions;
	}

	/**
	 * Gets the list of updated records.
	 *
	 * @return the list of updated records
	 * @since 1.0.0
	 */
	public List<Adaptation> getUpdates()
	{
		if (this.updates == null)
		{
			return new ArrayList<Adaptation>();
		}
		return this.updates;
	}

	/**
	 * Sets the list of created records.
	 *
	 * @param pCreations the list of created records
	 * @since 1.0.0
	 */
	public void setCreations(final List<Adaptation> pCreations)
	{
		this.creations = pCreations;
	}

	/**
	 * Sets the list of deleted records.
	 *
	 * @param pDeletions the list of deleted records
	 * @since 1.4.0
	 */
	public void setDeletions(final List<Adaptation> pDeletions)
	{
		this.deletions = pDeletions;
	}

	/**
	 * Sets the list of updated records.
	 *
	 * @param pUpdates the list of updated records
	 * @since 1.0.0
	 */
	public void setUpdates(final List<Adaptation> pUpdates)
	{
		this.updates = pUpdates;
	}
}
