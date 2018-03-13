/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.common.schemaextension;

import com.hertz.mdm.common.path.CommonReferencePaths;
import com.orchestranetworks.ps.accessrule.AccessRulesManager;
import com.orchestranetworks.ps.accessrule.WorkflowAccessRule;
import com.orchestranetworks.schema.SchemaExtensions;
import com.orchestranetworks.schema.SchemaExtensionsContext;

/**
 */
public class CommonReferenceSchemaExtension implements SchemaExtensions
{
	@Override
	public void defineExtensions(SchemaExtensionsContext context)
	{
		AccessRulesManager manager = new AccessRulesManager(context);
		manager.setAccessRuleOnNodeAndAllDescendants(
			CommonReferencePaths._Root,
			false,
			new WorkflowAccessRule());
	}
}
