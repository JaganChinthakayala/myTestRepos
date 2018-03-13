/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.common.util;

import com.hertz.mdm._hertz.constants.HtzConstants;
import com.hertz.mdm.common.path.CommonReferencePaths;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationHome;
import com.onwbp.adaptation.AdaptationName;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.RequestResult;
import com.orchestranetworks.instance.HomeKey;
import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.schema.Path;

public class HtzCommonUtilities
{
	public static AdaptationHome getAdminDataSpace(Repository repo)
	{
		return repo.lookupHome(HomeKey.forBranchName(HtzConstants.ADMIN_DATA_SPACE));
	}

	public static Adaptation getAdminDataSet(Repository repo)
	{
		return getAdminDataSet(getAdminDataSpace(repo));
	}

	public static Adaptation getAdminDataSet(AdaptationHome adminDataSpace)
	{
		return adminDataSpace
			.findAdaptationOrNull(AdaptationName.forName(HtzConstants.ADMIN_DATA_SET));
	}

	/**
	 * Return whether a given data space is a "master" data space. For Hertz, masters exist one level below the root Reference data space.
	 * 
	 * @param dataSpace the data space
	 * @return whether it is a master data space
	 */
	public static boolean isMasterDataSpace(AdaptationHome dataSpace)
	{
		AdaptationHome parentDataSpace = dataSpace.getParentBranch();
		return parentDataSpace != null && parentDataSpace.isBranchReference();
	}

	protected HtzCommonUtilities()
	{
		// do nothing
	}

	public static boolean countryHasStatesOrProvinces(Adaptation record, Path countryPath)
	{
		Adaptation countryRecord = AdaptationUtil.followFK(record, countryPath);

		if (countryRecord == null)
		{
			return false;
		}

		AdaptationTable subDivisionTable = countryRecord.getContainer()
			.getTable(CommonReferencePaths._Root_Subdivision_ISO31662.getPathInSchema());

		String locationId = countryRecord
			.get(CommonReferencePaths._Root_Country_ISO31661._ISO31661_integer).toString();

		// Query the SubDivision Table looking for a record with a reference to this country
		RequestResult requestResult = subDivisionTable.createRequestResult(
			CommonReferencePaths._Root_Subdivision_ISO31662._ISO31661_Integer.format() + "='"
				+ locationId + "' and "
				+ CommonReferencePaths._Root_Subdivision_ISO31662._IsRecordActive.format()
				+ "=true");

		if (requestResult.isEmpty())
		{
			requestResult.close();
			return false;
		}
		requestResult.close();
		return true;
	}
}
