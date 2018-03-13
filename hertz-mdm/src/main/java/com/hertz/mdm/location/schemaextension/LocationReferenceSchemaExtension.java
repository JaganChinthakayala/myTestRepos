/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.schemaextension;

import com.hertz.mdm.location.path.LocationReferencePaths;
import com.orchestranetworks.ps.accessrule.AccessRulesManager;
import com.orchestranetworks.ps.accessrule.WorkflowAccessRule;
import com.orchestranetworks.schema.SchemaExtensions;
import com.orchestranetworks.schema.SchemaExtensionsContext;

/**
 */
public class LocationReferenceSchemaExtension implements SchemaExtensions
{
	@Override
	public void defineExtensions(SchemaExtensionsContext context)
	{
		AccessRulesManager manager = new AccessRulesManager(context);
		manager.setAccessRuleOnNodeAndAllDescendants(
			LocationReferencePaths._Root,
			false,
			new WorkflowAccessRule());
	}
}
