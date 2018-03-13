/*
 * Copyright Orchestra Networks 2000-2016. All rights reserved.
 */
package com.hertz.mdm.common.referencedata;

import com.hertz.mdm._hertz.orchestra_toolbox.googlemapsinterface.repository.RepositoryUtils;
import com.hertz.mdm.common.path.CommonReferencePaths;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;

public final class ReferenceData
{

	public static AdaptationTable getCountriesTable()
	{
		return RepositoryUtils.getTable(
			RepositoryUtils.getDataSet("CommonReferenceMasterDataSpace", "CommonReferenceDataSet"),
			CommonReferencePaths._Root_Country_ISO31661.getPathInSchema().format());
	}

	public static AdaptationTable getStatesProvincesTable()
	{
		return RepositoryUtils.getTable(
			RepositoryUtils.getDataSet("CommonReferenceMasterDataSpace", "CommonReferenceDataSet"),
			CommonReferencePaths._Root_Subdivision_ISO31662.getPathInSchema().format());
	}

	public static Adaptation getCountryFromCountryName(final String pCountryName)
	{
		if (pCountryName == null || pCountryName.trim().isEmpty())
		{
			return null;
		}

		AdaptationTable countriesTable = ReferenceData.getCountriesTable();
		String predicate = CommonReferencePaths._Root_Country_ISO31661._ShortName.format() + "='"
			+ pCountryName.trim() + "'";
		return countriesTable.lookupFirstRecordMatchingPredicate(predicate);
	}

	/*	public static AdaptationTable getCurrenciesTable()
		{
			return RepositoryUtils.getTable(
				FastTrack.DomainDataSets.RDM_CURRENCIES.getDataSet(),
				CurrencyPaths._Currency.getPathInSchema().format());
		}
	
		public static AdaptationTable getLanguagesTable()
		{
			return RepositoryUtils.getTable(
				FastTrack.DomainDataSets.RDM_LANGUAGES.getDataSet(),
				LanguagePaths._Language.getPathInSchema().format());
		} */

	private ReferenceData()
	{
	}
}
