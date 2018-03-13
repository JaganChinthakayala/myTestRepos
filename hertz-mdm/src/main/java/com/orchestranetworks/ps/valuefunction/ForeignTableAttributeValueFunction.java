/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.valuefunction;

import com.onwbp.adaptation.*;
import com.orchestranetworks.ps.util.*;
import com.orchestranetworks.schema.*;

/**
 * A value function that looks up a field's value via a foreign key.
 * This is often done via an inherited field, but inherited fields can't
 * operate across data sets.
 */
public class ForeignTableAttributeValueFunction extends AbstractForeignKeyValueFunction
{
	@Override
	public Object getActualValue(Adaptation adaptation)
	{
		return AdaptationUtil.followFK(adaptation, foreignKeyPath, attributePath);
	}

	@Override
	public void setup(ValueFunctionContext context)
	{
		if (attributePath == null)
		{
			context.addError("attributePath must be specified.");
		}
		super.setup(context);
	}

	@Override
	public boolean validateSetupForeignKeyPath(SchemaNode foreignKeyNode)
	{
		return foreignKeyNode.getFacetOnTableReference() != null;
	}
}
