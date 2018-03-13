/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.accessrule;

import java.text.*;
import java.util.*;

import com.onwbp.adaptation.*;
import com.orchestranetworks.instance.*;
import com.orchestranetworks.interactions.*;
import com.orchestranetworks.ps.util.*;
import com.orchestranetworks.ps.workflow.*;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.service.*;

/**
 * If in the context of a session, there is a record context (for example, if a record is selected
 * and a service or action is acting upon that record, such as duplicate), and if the record specified
 * by this access rule matches that context record, the permission is read-write; otherwise it is read-only.
 * The record specified by this access rule is either the selected adaptation, or one found by following the
 * foreign key specified by the constructor taking a 'recordFieldPath'.
 */
public class RecordMatchesAccessRule implements AccessRule
{
	private final Path recordFieldPath;
	private static final String ERRMSG = "Error looking up record {0} {1} in table {2}";

	public RecordMatchesAccessRule()
	{
		this(null);
	}

	public RecordMatchesAccessRule(Path recordFieldPath)
	{
		this.recordFieldPath = recordFieldPath;
	}

	@Override
	public AccessPermission getPermission(Adaptation adaptation, Session session, SchemaNode node)
	{
		if (adaptation.isSchemaInstance() || adaptation.isHistory())
		{
			return AccessPermission.getReadWrite();
		}
		if (adaptation.getHome().isVersion())
		{
			return AccessPermission.getReadOnly();
		}
		SessionInteraction interaction = session.getInteraction(true);
		if (interaction != null)
		{
			List<Adaptation> sessionRecordsToMatch;
			try
			{
				sessionRecordsToMatch = getRecordsToMatchFromSession(
					session,
					adaptation.getContainer().getHome().getRepository());
			}
			catch (OperationException ex)
			{
				LoggingCategory.getKernel().error("Error looking up workflow session records.");
				return AccessPermission.getReadOnly();
			}
			if (sessionRecordsToMatch != null && !sessionRecordsToMatch.isEmpty())
			{
				Adaptation recordToMatch;
				try
				{
					recordToMatch = getRecordToMatchFromAdaptation(adaptation);
				}
				catch (OperationException ex)
				{
					LoggingCategory.getKernel().error(
						MessageFormat.format(
							ERRMSG,
							(recordFieldPath == null ? "" : " for field "
								+ recordFieldPath.format() + " in record "),
							adaptation.getOccurrencePrimaryKey().format(),
							adaptation.getContainerTable().getTablePath().format()));
					return AccessPermission.getReadOnly();
				}
				for (Adaptation sessionRecordToMatch : sessionRecordsToMatch)
				{
					if (sessionRecordToMatch.equals(recordToMatch))
					{
						return AccessPermission.getReadWrite();
					}
				}
				return AccessPermission.getReadOnly();
			}
		}
		return AccessPermission.getReadWrite();
	}

	protected List<Adaptation> getRecordsToMatchFromSession(Session session, Repository repo)
		throws OperationException
	{
		List<Adaptation> result = new ArrayList<Adaptation>();
		Adaptation record = WorkflowUtilities.getRecordFromSessionInteraction(
			session.getInteraction(true),
			repo);
		if (record != null)
		{
			result.add(record);
		}
		return result;
	}

	protected Adaptation getRecordToMatchFromAdaptation(Adaptation adaptation)
		throws OperationException
	{
		return recordFieldPath == null ? adaptation : AdaptationUtil.followFK(
			adaptation,
			recordFieldPath);
	}
}
