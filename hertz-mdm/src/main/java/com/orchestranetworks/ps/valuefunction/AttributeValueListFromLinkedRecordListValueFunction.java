/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.valuefunction;

import com.onwbp.adaptation.*;
import com.orchestranetworks.ps.util.*;
import com.orchestranetworks.schema.*;

/**
 * A value function that returns an ArrayList from a linked record list.
 * -- if an attributePath is specified, then will return a List of the Attribute Values from the Linked Records 
 * -- otherwise will return the Linked Records as a List of Foreign Key values 
 */
public class AttributeValueListFromLinkedRecordListValueFunction
	extends
	AbstractForeignKeyValueFunction
{
	@Override
	public Object getActualValue(Adaptation adaptation)
	{
		if (attributePath != null)
		{
			return AdaptationUtil.getLinkedRecordList(adaptation, foreignKeyPath, attributePath);
		}
		else
		{
			return AdaptationUtil.getLinkedRecordKeyList(adaptation, foreignKeyPath);

		}
	}
	@Override
	public boolean validateSetupForeignKeyPath(SchemaNode foreignKeyNode)
	{
		return (foreignKeyNode.isAssociationNode() || foreignKeyNode.isSelectNode());
	}
}
