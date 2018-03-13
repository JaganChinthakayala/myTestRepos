/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.trigger;

import java.util.*;

import com.onwbp.adaptation.*;
import com.onwbp.base.text.*;
import com.orchestranetworks.instance.*;
import com.orchestranetworks.interactions.*;
import com.orchestranetworks.ps.util.*;
import com.orchestranetworks.ps.workflow.*;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.schema.trigger.*;
import com.orchestranetworks.service.*;

/**
 */
public class RecordMatchesTriggerActionValidator implements TriggerActionValidator
{
	private Path recordFieldPath;
	private TriggerActionValidator nestedTriggerActionValidator;

	public RecordMatchesTriggerActionValidator()
	{
		this(null, null);
	}

	public RecordMatchesTriggerActionValidator(
		Path recordFieldPath,
		TriggerActionValidator nestedTriggerActionValidator)
	{
		this.recordFieldPath = recordFieldPath;
		this.nestedTriggerActionValidator = nestedTriggerActionValidator;
	}

	protected List<Adaptation> getRecordsToMatchFromSession(Session session, Repository repo)
		throws OperationException
	{
		ArrayList<Adaptation> records = new ArrayList<Adaptation>();
		Adaptation record = WorkflowUtilities.getRecordFromSessionInteraction(
			session.getInteraction(true),
			repo);
		if (record != null)
		{
			records.add(record);
		}
		return records;
	}

	protected Adaptation getRecordToMatchFromValueContext(ValueContext valueContext)
		throws OperationException
	{
		return recordFieldPath == null ? AdaptationUtil.getRecordForValueContext(valueContext)
			: AdaptationUtil.followFK(valueContext, recordFieldPath);
	}

	@Override
	public UserMessage validateTriggerAction(
		Session session,
		ValueContext valueContext,
		ValueChanges valueChanges,
		TriggerAction action) throws OperationException
	{
		if (nestedTriggerActionValidator != null)
		{
			UserMessage msg = nestedTriggerActionValidator.validateTriggerAction(
				session,
				valueContext,
				valueChanges,
				action);
			if (msg != null)
			{
				return msg;
			}
		}
		SessionInteraction interaction = session.getInteraction(true);
		if (interaction != null)
		{
			AdaptationTable table = valueContext.getAdaptationTable();
			List<Adaptation> sessionRecordsToMatch = getRecordsToMatchFromSession(
				session,
				valueContext.getAdaptationInstance().getHome().getRepository());
			if (sessionRecordsToMatch != null && !sessionRecordsToMatch.isEmpty())
			{
				Locale locale = session.getLocale();
				AdaptationTable sessionRecordTable = sessionRecordsToMatch.iterator()
					.next()
					.getContainerTable();
				String sessionRecordTableLabel = sessionRecordTable.getTableNode().getLabel(locale);
				// If this is a create, and the restriction is on this table itself, and the table of the record
				// being created is the same as the session record's table, then it means we're trying to create a
				// record in the table that we're not supposed to.
				if (action == TriggerAction.CREATE && recordFieldPath == null
					&& table.getTablePath().equals(sessionRecordTable.getTablePath()))
				{
					return UserMessage.createError("Not allowed to " + action + " record in table "
						+ sessionRecordTableLabel + ".");
				}
				Adaptation recordToMatch = getRecordToMatchFromValueContext(valueContext);
				if (recordToMatch == null)
				{
					return UserMessage.createError("Can't find " + sessionRecordTableLabel
						+ " record for " + table.computePrimaryKey(valueContext).format() + ".");
				}
				StringBuilder sessionRecordsBldr = new StringBuilder();
				sessionRecordsBldr.append(sessionRecordTableLabel);
				sessionRecordsBldr.append(" record(s) ");
				for (int i = 0; i < sessionRecordsToMatch.size(); i++)
				{
					Adaptation sessionRecordToMatch = sessionRecordsToMatch.get(i);
					if (sessionRecordToMatch.equals(recordToMatch))
					{
						return null;
					}
					// Get the session record to match's label, or pk if null
					String sessionRecordToMatchLabel = sessionRecordToMatch.getLabel(locale);
					if (sessionRecordToMatchLabel == null)
					{
						sessionRecordToMatchLabel = sessionRecordToMatch.getOccurrencePrimaryKey()
							.format();
					}
					sessionRecordsBldr.append(sessionRecordToMatchLabel);
					if (i < sessionRecordsToMatch.size() - 1)
					{
						sessionRecordsBldr.append(" or ");
					}
				}
				// If it's already saved, get its label. Otherwise, or if it has none, get its pk.
				Adaptation valueContextRecord = AdaptationUtil.getRecordForValueContext(valueContext);
				String valueContextLabel = null;
				if (valueContextRecord != null)
				{
					valueContextLabel = valueContextRecord.getLabel(locale);
				}
				if (valueContextLabel == null)
				{
					valueContextLabel = table.computePrimaryKey(valueContext).format();
				}
				// Get the record to match's label, or pk if null
				String recordToMatchLabel = recordToMatch.getLabel(locale);
				if (recordToMatchLabel == null)
				{
					recordToMatchLabel = recordToMatch.getOccurrencePrimaryKey().format();
				}
				return UserMessage.createError("Can't " + action + " record " + valueContextLabel
					+ ". The " + recordToMatch.getContainerTable().getTableNode().getLabel(locale)
					+ " record " + recordToMatchLabel + " doesn't match " + sessionRecordsBldr
					+ ".");
			}
		}
		return null;
	}

	public Path getRecordFieldPath()
	{
		return this.recordFieldPath;
	}

	public void setRecordFieldPath(Path recordFieldPath)
	{
		this.recordFieldPath = recordFieldPath;
	}
}
