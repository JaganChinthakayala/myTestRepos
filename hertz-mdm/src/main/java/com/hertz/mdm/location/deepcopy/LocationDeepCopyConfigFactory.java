/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.deepcopy;

import java.util.HashSet;

import com.hertz.mdm.location.path.LocationPaths;
import com.onwbp.adaptation.AdaptationTable;
import com.orchestranetworks.ps.deepcopy.DeepCopyConfig;
import com.orchestranetworks.ps.deepcopy.DeepCopyConfigFactory;
import com.orchestranetworks.schema.Path;

/**
 */
public class LocationDeepCopyConfigFactory implements DeepCopyConfigFactory
{
	public DeepCopyConfig createConfig(AdaptationTable table)
	{
		Path tablePath = table.getTablePath();
		if (LocationPaths._Root_Location.getPathInSchema().equals(tablePath))
		{
			return createGeneralLedgerAccountConfig(table);
		}
		throw new IllegalArgumentException("Unsupported table path: " + tablePath.format());
	}

	protected DeepCopyConfig createGeneralLedgerAccountConfig(AdaptationTable table)
	{
		DeepCopyConfig config = new DeepCopyConfig();

		/*		HashMap<Path, DeepCopyConfig> assocMap = new HashMap<Path, DeepCopyConfig>();
				assocMap.put(
					LocationPaths._Root_Location._GeneralLedgerCompanyCodes,
					createConfig(
						dataSet.getTable(
							BkFinancePaths._Root_GeneralLedgerAccountDetails_GeneralLedgerAccountCompanyCode
								.getPathInSchema())));
				config.setAssociationConfigMap(assocMap); */

		HashSet<Path> pathsToIndicateWithCopy = new HashSet<Path>();
		pathsToIndicateWithCopy.add(LocationPaths._Root_Location._Description);
		config.setPathsToIndicateWithCopy(pathsToIndicateWithCopy);

		config.setEnforcePermission(true);

		return config;
	}

	protected DeepCopyConfig createGeneralLedgerAccountCompanyCodeConfig()
	{
		DeepCopyConfig config = new DeepCopyConfig();

		//	config.setPathToParentSelectionNodeOrAssociation(
		//		BkFinancePaths._Root_GeneralLedgerAccountDetails_GeneralLedgerAccountCompanyCode._GeneralLedgerAccount);

		return config;
	}
}
