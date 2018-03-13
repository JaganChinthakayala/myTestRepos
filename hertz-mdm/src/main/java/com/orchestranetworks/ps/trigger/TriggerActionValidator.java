/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.trigger;

import com.onwbp.base.text.*;
import com.orchestranetworks.instance.*;
import com.orchestranetworks.schema.trigger.*;
import com.orchestranetworks.service.*;

/**
 */
public interface TriggerActionValidator
{
	enum TriggerAction {
		CREATE("create"), MODIFY("modify"), DELETE("delete");

		private final String label;

		private TriggerAction(String label)
		{
			this.label = label;
		}

		@Override
		public String toString()
		{
			return label;
		}
	}

	/**
	 * Validate the given action by a trigger and throw an exception if it should not be allowed
	 * 
	 * @param session the session
	 * @param valueContext the value context of the record
	 * @param valueChanges the changes occurring, if this is a modify. Otherwise this is <code>null</code>.
	 * @param action the action: either <code>CREATE</code>, <code>MODIFY</code>, or <code>DELETE</code>
	 * @return the error message, or <code>null</code> if there is no error
	 */
	UserMessage validateTriggerAction(
		Session session,
		ValueContext valueContext,
		ValueChanges valueChanges,
		TriggerAction action) throws OperationException;
}
