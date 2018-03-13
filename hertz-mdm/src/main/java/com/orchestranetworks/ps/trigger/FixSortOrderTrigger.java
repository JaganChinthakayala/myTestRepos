package com.orchestranetworks.ps.trigger;

import java.util.*;

import org.apache.commons.lang.*;

import com.onwbp.adaptation.*;
import com.orchestranetworks.instance.*;
import com.orchestranetworks.ps.procedure.*;
import com.orchestranetworks.ps.util.*;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.schema.trigger.*;
import com.orchestranetworks.service.*;

/**
 * Configured with a semi-colon separated list of 'commonValuePaths' which is the
 * means of grouping a collection of records that can be sorted, and a 'sortOrderPath'
 * which represents an integer field on the record indicating its index in the collection,
 * this trigger will:
 * - on creation of a new record in the collection, will assign the existing collection-count
 *   to the sortOrder field
 * - on delete of an existing record, will decrement the sortOrder of subsequent records
 * - on modify of an existing record's sortOrder, will adjust the impacted records in the group
 * 
 * Optionally, can be configured to start at 0 (default is to start at 1).
 */
public class FixSortOrderTrigger extends BaseTableTrigger
{
	private String commonValuePathsString;
	private List<Path> commonValuePaths;
	private Path sortOrderPath;
	private boolean startAtOne = true;

	@Override
	public void setup(TriggerSetupContext context)
	{
		if (commonValuePathsString != null)
		{
			commonValuePaths = PathUtils.convertStringToPathList(commonValuePathsString, null);
		}
		for (Path path : commonValuePaths)
		{
			PathUtils.setupFieldNode(context, path, "commonValue", false);
		}
		SchemaNode sortOrderNode = PathUtils.setupFieldNode(
			context,
			sortOrderPath,
			"sortOrderPath",
			false);
		SchemaTypeName sortOrderType = sortOrderNode.getXsTypeName();
		if (!(SchemaTypeName.XS_INT.equals(sortOrderType) || SchemaTypeName.XS_INTEGER.equals(sortOrderType)))
		{
			context.addError("Node " + sortOrderPath.format() + " must be integer type");
		}
	}

	@Override
	public void handleAfterDelete(AfterDeleteOccurrenceContext context) throws OperationException
	{
		ValueContext recordContext = context.getOccurrenceContext();
		List<Adaptation> otherProperties = AdaptationUtil.fetchRecordsMatching(
			recordContext,
			commonValuePaths,
			null,
			sortOrderPath);
		if (otherProperties == null)
			return;
		ProcedureContext pContext = context.getProcedureContext();
		Map<Path, Object> map = new HashMap<Path, Object>(1);
		int delta = startAtOne ? 1 : 0;
		for (int i = 0; i < otherProperties.size(); i++)
		{
			Adaptation next = otherProperties.get(i);
			int order = next.get_int(sortOrderPath);
			if (order != (i + delta))
			{
				map.put(sortOrderPath, i + delta);
				ModifyValuesProcedure.execute(pContext, next, map, true, true);
			}
		}
		super.handleAfterDelete(context);
	}

	@Override
	public void handleAfterModify(AfterModifyOccurrenceContext context) throws OperationException
	{
		// if you change the index, update the other indices accordingly
		ValueChanges changes = context.getChanges();
		ValueChange indexChange = changes.getChange(sortOrderPath);
		if (indexChange != null)
		{
			int newValue = (Integer) indexChange.getValueAfter();
			ValueContext recordContext = context.getOccurrenceContext();
			Adaptation record = context.getAdaptationOccurrence();
			List<Adaptation> otherProperties = AdaptationUtil.fetchRecordsMatching(
				recordContext,
				commonValuePaths,
				null,
				sortOrderPath);
			if (otherProperties == null)
				return;
			ProcedureContext pContext = context.getProcedureContext();
			Map<Path, Object> map = new HashMap<Path, Object>(1);
			int delta = startAtOne ? 1 : 0;
			otherProperties.remove(record);
			if (newValue - delta > otherProperties.size())
				otherProperties.add(record);
			else
				otherProperties.add(newValue - delta, record);
			for (int i = 0; i < otherProperties.size(); i++)
			{
				Adaptation next = otherProperties.get(i);
				int order = ObjectUtils.equals(next, record) ? newValue
					: next.get_int(sortOrderPath);
				if (order != (i + delta))
				{
					map.put(sortOrderPath, i + delta);
					ModifyValuesProcedure.execute(pContext, next, map, true, true);
				}
			}
		}
		super.handleAfterModify(context);
	}

	@Override
	public void handleNewContext(NewTransientOccurrenceContext context)
	{
		if (!context.isDuplicatingOccurrence())
		{
			ValueContextForUpdate recordContext = context.getOccurrenceContextForUpdate();
			if (recordContext.getValue(sortOrderPath) == null) //when importing, allow the existing value to be retained
			{
				List<Adaptation> otherProperties = AdaptationUtil.fetchRecordsMatching(
					recordContext,
					commonValuePaths,
					null);
				if (otherProperties == null)
					return;
				recordContext.setValue(otherProperties.size() + (startAtOne ? 1 : 0), sortOrderPath);
			}
		}
		super.handleNewContext(context);
	}

	@Override
	public void handleAfterCreate(AfterCreateOccurrenceContext context) throws OperationException
	{
		super.handleAfterCreate(context);
		ValueContext recordContext = context.getOccurrenceContext();
		List<Adaptation> otherProperties = AdaptationUtil.fetchRecordsMatching(
			recordContext,
			commonValuePaths,
			null);
		if (otherProperties == null)
			return;
		ProcedureContext pContext = context.getProcedureContext();
		int max = otherProperties.size() - (startAtOne ? 0 : 1);
		int value = (Integer) recordContext.getValue(sortOrderPath);
		if (value == max)
			return;
		Map<Path, Object> map = new HashMap<Path, Object>(1);
		//if newValue < oldValue, will need to increment subsequent records
		//if oldValue < newValue, will need to decrement subsequent records
		if (value > max)
		{
			map.put(sortOrderPath, max);
			ModifyValuesProcedure.execute(
				pContext,
				context.getAdaptationOccurrence(),
				map,
				true,
				true);
			return;
		}
		for (Adaptation adaptation : otherProperties)
		{
			if (adaptation.equals(context.getAdaptationOccurrence()))
			{
				continue;
			}
			int order = adaptation.get_int(sortOrderPath);
			if (order >= value)
			{
				map.put(sortOrderPath, order + 1);
				ModifyValuesProcedure.execute(pContext, adaptation, map, true, true);
			}
		}
	}

	public String getCommonValuePathsString()
	{
		return commonValuePathsString;
	}

	public void setCommonValuePathsString(String commonValuePathsString)
	{
		this.commonValuePathsString = commonValuePathsString;
	}

	public Path getSortOrderPath()
	{
		return sortOrderPath;
	}

	public void setSortOrderPath(Path sortOrderPath)
	{
		this.sortOrderPath = sortOrderPath;
	}

	public boolean isStartAtOne()
	{
		return startAtOne;
	}

	public void setStartAtOne(boolean startAtOne)
	{
		this.startAtOne = startAtOne;
	}

}
