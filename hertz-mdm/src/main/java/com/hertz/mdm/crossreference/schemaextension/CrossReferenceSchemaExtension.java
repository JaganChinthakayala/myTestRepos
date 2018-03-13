/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.crossreference.schemaextension;

import com.hertz.mdm._hertz.constants.HtzConstants;
import com.hertz.mdm.crossreference.accessrule.CrossReferenceDomainTableAccessRule;
import com.hertz.mdm.crossreference.path.CrossReferencePaths;
import com.orchestranetworks.ps.accessrule.AccessRulesManager;
import com.orchestranetworks.ps.accessrule.WorkflowAccessRule;
import com.orchestranetworks.schema.SchemaExtensions;
import com.orchestranetworks.schema.SchemaExtensionsContext;

/**
 */
public class CrossReferenceSchemaExtension implements SchemaExtensions
{
	@Override
	public void defineExtensions(SchemaExtensionsContext context)
	{
		AccessRulesManager manager = new AccessRulesManager(context);

		manager.setAccessRuleOnNodeAndAllDescendants(
			CrossReferencePaths._Root,
			false,
			new WorkflowAccessRule());

		//Hides Hides Common Reference Table if the Business Party Domain is not selected
		manager.setAccessRuleOnNodeAndAllDescendants(
			CrossReferencePaths._Root_MDMToTargetSystemCrossReference.getPathInSchema().add(
				CrossReferencePaths._Root_MDMToTargetSystemCrossReference._CommonReferenceTable),
			true,
			new CrossReferenceDomainTableAccessRule(HtzConstants.COMMON_DOMAIN));

		//Hides Hides Business Party Reference Table if the Business Party Domain is not selected
		manager.setAccessRuleOnNodeAndAllDescendants(
			CrossReferencePaths._Root_MDMToTargetSystemCrossReference.getPathInSchema().add(
				CrossReferencePaths._Root_MDMToTargetSystemCrossReference._BusinessPartyReferenceTable),
			true,
			new CrossReferenceDomainTableAccessRule(HtzConstants.BUSINESS_PARTY_DOMAIN));

		//Hides Hides Location Reference Table if the Business Party Domain is not selected
		manager.setAccessRuleOnNodeAndAllDescendants(
			CrossReferencePaths._Root_MDMToTargetSystemCrossReference.getPathInSchema().add(
				CrossReferencePaths._Root_MDMToTargetSystemCrossReference._LocationReferenceTable),
			true,
			new CrossReferenceDomainTableAccessRule(HtzConstants.LOCATION_DOMAIN));

		//Hides Hides Vehicle Reference Table if the Business Party Domain is not selected
		manager.setAccessRuleOnNodeAndAllDescendants(
			CrossReferencePaths._Root_MDMToTargetSystemCrossReference.getPathInSchema().add(
				CrossReferencePaths._Root_MDMToTargetSystemCrossReference._VehicleReferenceTable),
			true,
			new CrossReferenceDomainTableAccessRule(HtzConstants.VEHICLE_DOMAIN));
	}
}
