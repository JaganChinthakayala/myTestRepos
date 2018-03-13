/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.scheduledtask;

import java.util.*;

import com.onwbp.adaptation.*;
import com.orchestranetworks.instance.*;
import com.orchestranetworks.scheduler.*;
import com.orchestranetworks.service.*;

/**
 * A scheduled task that marks for deletion closed data spaces older than a specified number of days
 */
public class MarkForDeletionScheduledTask extends ScheduledTask
{
	private String parentDataSpace = Repository.REFERENCE.getName();
	private int daysToKeep;
	private boolean deleteHistory;

	@Override
	public void execute(ScheduledExecutionContext context)
		throws OperationException, ScheduledTaskInterruption
	{
		Repository repo = context.getRepository();
		AdaptationHome dataSpace = repo.lookupHome(HomeKey.forBranchName(parentDataSpace));
		if (dataSpace == null)
		{
			LoggingCategory.getKernel().warn(
				"Parent data space " + parentDataSpace + " does not exist.");
		}
		else
		{
			// Find the date at the specified number of days prior to the current date
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, daysToKeep * -1);
			// Delete all children that were closed earlier than that date
			markChildrenForDeletion(context.getSession(), dataSpace, cal.getTime());
		}
	}

	@Override
	public void validate(ValueContextForValidation context)
	{
		if (parentDataSpace == null || parentDataSpace.trim().length() == 0)
		{
			context.addError("Must specify parent data space. Use \"Reference\" for all data spaces.");
		}
		else
		{
			Repository repo = context.getHome().getRepository();
			AdaptationHome dataSpace = repo.lookupHome(HomeKey.forBranchName(parentDataSpace));
			// Make this a warning because it could be they just haven't created it yet
			if (dataSpace == null)
			{
				context.addWarning("Parent data space \"" + parentDataSpace + "\" does not exist.");
			}
		}
		if (daysToKeep < 0)
		{
			context.addError("daysToKeep parameter must be greater than or equal to 0.");
		}
	}

	private void markChildrenForDeletion(
		Session session,
		AdaptationHome dataSpace,
		Date earlierThanDate) throws OperationException
	{
		@SuppressWarnings("unchecked")
		List<AdaptationHome> childVersions = dataSpace.getVersionChildren();
		for (AdaptationHome childVersion : childVersions)
		{
			// Skip if it's a technical snapshot
			if (childVersion.isTechnicalVersion())
			{
				continue;
			}
			// If it's open then delete its closed child data spaces
			if (childVersion.isOpen())
			{
				@SuppressWarnings("unchecked")
				List<AdaptationHome> childDataSpaces = childVersion.getBranchChildren();
				for (AdaptationHome childDataSpace : childDataSpaces)
				{
					// Skip if it's a technical data space
					if (childDataSpace.isTechnicalBranch())
					{
						continue;
					}
					// If it's open, call this method on its children
					if (childDataSpace.isOpen())
					{
						markChildrenForDeletion(session, childDataSpace, earlierThanDate);
					}
					else
					{
						deleteHome(childDataSpace, earlierThanDate, session);
					}
				}
			}
			else
			{
				deleteHome(childVersion, earlierThanDate, session);
			}
		}
	}
	private void deleteHome(AdaptationHome home, Date earlierThanDate, Session session)
		throws OperationException
	{
		// Only delete if the date it was closed is before the specified date
		Date closeDate = home.getTerminationDate();
		if (closeDate != null && closeDate.before(earlierThanDate))
		{
			Repository repo = home.getRepository();
			repo.deleteHome(home, session);
			if (deleteHistory)
			{
				repo.getPurgeDelegate().markHomeForHistoryPurge(home, session);
			}
		}
	}

	public String getParentDataSpace()
	{
		return this.parentDataSpace;
	}

	public void setParentDataSpace(String parentDataSpace)
	{
		this.parentDataSpace = parentDataSpace;
	}

	public int getDaysToKeep()
	{
		return this.daysToKeep;
	}

	public void setDaysToKeep(int daysToKeep)
	{
		this.daysToKeep = daysToKeep;
	}

	public boolean isDeleteHistory()
	{
		return this.deleteHistory;
	}

	public void setDeleteHistory(boolean deleteHistory)
	{
		this.deleteHistory = deleteHistory;
	}
}
