package com.orchestranetworks.ps.ranges;

import java.util.*;

import com.onwbp.adaptation.*;
import com.orchestranetworks.instance.*;
import com.orchestranetworks.ps.procedure.*;
import com.orchestranetworks.ps.util.*;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.schema.trigger.*;
import com.orchestranetworks.service.*;

/** 
 * Used in conjunction with <code>RangesConstraint</code>, this trigger will fix adjacent ranges in the range set.
 * If creating a new range, it will default the min bound to the maximum max bound in the range set.
 * When modifying a range, it will 'fix' the adjacent ranges accordingly.
 * See <a href="https://docs.google.com/document/d/1I-zS_AJIoPGgEdt-BC6g-7HNEj34Y9QD16mw_R-P7bw/edit#">Range Support</a>
 * for more information.
 */
public class FixRangeTrigger extends TableTrigger
{
	private static final String DELETED_RANGE = "FixRange_DeletedRange";

	private RangeConfig config = new RangeConfig();

	public Path getCommonValuePath()
	{
		return config.getCommonValuePath();
	}

	public void setCommonValuePath(Path commonValuePath)
	{
		config.setCommonValuePath(commonValuePath);
	}

	public String getCommonValuePathsString()
	{
		return config.getCommonValuePathsString();
	}

	public void setCommonValuePathsString(String commonValuePathsString)
	{
		config.setCommonValuePathsString(commonValuePathsString);
	}

	public Path getMinValuePath()
	{
		return config.getMinValuePath();
	}

	public void setMinValuePath(Path minValuePath)
	{
		config.setMinValuePath(minValuePath);
	}

	public Path getMaxValuePath()
	{
		return config.getMaxValuePath();
	}

	public void setMaxValuePath(Path maxValuePath)
	{
		config.setMaxValuePath(maxValuePath);
	}

	public boolean isMinInclusive()
	{
		return config.isMinInclusive();
	}

	public void setMinInclusive(boolean minInclusive)
	{
		config.setMinInclusive(minInclusive);
	}

	public boolean isMaxInclusive()
	{
		return config.isMaxInclusive();
	}

	public void setMaxInclusive(boolean maxInclusive)
	{
		config.setMaxInclusive(maxInclusive);
	}

	public boolean isCheckOverlaps()
	{
		return config.isCheckOverlaps();
	}

	public void setCheckOverlaps(boolean checkOverlaps)
	{
		config.setCheckOverlaps(checkOverlaps);
	}

	public boolean isCheckGaps()
	{
		return config.isCheckGaps();
	}

	public void setCheckGaps(boolean checkGaps)
	{
		config.setCheckGaps(checkGaps);
	}

	public boolean isUsePadding()
	{
		return config.isUsePadding();
	}

	public void setUsePadding(boolean usePadding)
	{
		config.setUsePadding(usePadding);
	}

	public String getPadding()
	{
		return config.getPadding();
	}

	public void setPadding(String padding)
	{
		config.setPadding(padding);
	}

	@Override
	public void setup(TriggerSetupContext context)
	{
		config.setup(context);
	}

	@SuppressWarnings({ "rawtypes" })
	@Override
	public void handleAfterCreate(AfterCreateOccurrenceContext context) throws OperationException
	{
		super.handleAfterCreate(context);
		if (!shouldDeal())
			return;
		ValueContext recordContext = context.getOccurrenceContext();
		dealWithRecord(
			recordContext,
			context.getAdaptationOccurrence(),
			true,
			null,
			(Comparable) recordContext.getValue(getMinValuePath()),
			true,
			null,
			(Comparable) recordContext.getValue(getMaxValuePath()),
			context.getProcedureContext());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void handleNewContext(NewTransientOccurrenceContext context)
	{
		super.handleNewContext(context);
		//want to set the min value on the new record to the max existing maxValue
		ValueContextForUpdate recordContext = context.getOccurrenceContextForUpdate();
		List<Adaptation> ranges = AdaptationUtil.fetchRecordsMatching(
			recordContext,
			config.commonValuePaths,
			null);
		if (ranges == null)
			return;
		RangeUtils.sortRanges(ranges, RangeUtils.createGetValue(null, null, getMinValuePath()));
		Adaptation maxRecord = ranges.isEmpty() ? null : (Adaptation) ranges.get(ranges.size() - 1);
		if (maxRecord != null)
		{
			Object maxValue = maxRecord.get(getMaxValuePath());
			recordContext.setValue(
				RangeUtils.padMax((Comparable) maxValue, config.paddingNum),
				getMinValuePath());
			recordContext.setValue(null, getMaxValuePath()); //in the case of a duplicate, will want the default
		}
	}

	@Override
	public void handleBeforeDelete(BeforeDeleteOccurrenceContext aContext)
		throws OperationException
	{
		super.handleBeforeDelete(aContext);
		//if checking gaps, when we delete a record, we would like to close the gap created
		if (shouldDeal() && isCheckGaps())
		{
			aContext.getSession().setAttribute(
				DELETED_RANGE + this,
				aContext.getAdaptationOccurrence());
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void handleAfterDelete(AfterDeleteOccurrenceContext aContext) throws OperationException
	{
		super.handleAfterDelete(aContext);
		boolean checkGaps = isCheckGaps();
		if (shouldDeal() && checkGaps)
		{
			Adaptation existingRecord = (Adaptation) aContext.getSession().getAttribute(
				DELETED_RANGE + this);
			if (existingRecord == null)
				return;
			aContext.getSession().setAttribute(DELETED_RANGE + this, null);
			ValueContext recordContext = aContext.getOccurrenceContext();
			List<Adaptation> ranges = AdaptationUtil.fetchRecordsMatching(
				recordContext,
				config.commonValuePaths,
				null);
			if (ranges == null)
				return;
			ranges.add(existingRecord);
			Path minValuePath = getMinValuePath();
			Path maxValuePath = getMaxValuePath();
			Comparable minValue = (Comparable) recordContext.getValue(minValuePath);
			RangeUtils.sortRanges(
				ranges,
				RangeUtils.createGetValue(existingRecord, minValue, minValuePath));
			int indexOfCurr = ranges.indexOf(existingRecord);
			Adaptation predecessor = indexOfCurr > 0 ? (Adaptation) ranges.get(indexOfCurr - 1)
				: null;
			Adaptation successor = indexOfCurr < ranges.size() - 1 ? (Adaptation) ranges.get(indexOfCurr + 1)
				: null;
			if (predecessor != null)
			{
				//we can set the maxValue of the predecessor to the deleted record's maxValue
				Map<Path, Object> pathValueMap = new HashMap<>();
				pathValueMap.put(maxValuePath, recordContext.getValue(maxValuePath));
				ModifyValuesProcedure.execute(
					aContext.getProcedureContext(),
					predecessor,
					pathValueMap);
			}
			else if (successor != null)
			{
				//we can set the minValue of the successor to the deleted record's minValue
				Map<Path, Object> pathValueMap = new HashMap<>();
				pathValueMap.put(minValuePath, recordContext.getValue(minValuePath));
				ModifyValuesProcedure.execute(
					aContext.getProcedureContext(),
					successor,
					pathValueMap);
			}
		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void dealWithRecord(
		ValueContext recordContext,
		Adaptation existingRecord,
		boolean minChanged,
		Comparable prevMin,
		Comparable minValue,
		boolean maxChanged,
		Comparable prevMax,
		Comparable maxValue,
		ProcedureContext pContext) throws OperationException
	{
		//gap check only works with [)/(] ranges -- we assume minInclusive != maxInclusive
		if (existingRecord == null)
		{
			//do nothing.  This trigger is for fixing existing sets of ranges
			return;
		}
		List<Adaptation> ranges = AdaptationUtil.fetchRecordsMatching(
			recordContext,
			config.commonValuePaths,
			null);
		if (ranges == null)
			return;
		Path minValuePath = getMinValuePath();
		Path maxValuePath = getMaxValuePath();
		boolean checkGaps = isCheckGaps();
		boolean checkOverlaps = isCheckOverlaps();
		float paddingNum = config.paddingNum;
		if (minValue == null)
			minValue = (Comparable) recordContext.getValue(minValuePath);
		RangeUtils.sortRanges(
			ranges,
			RangeUtils.createGetValue(existingRecord, minValue, minValuePath));
		int indexOfCurr = ranges.indexOf(existingRecord);
		Adaptation predecessor = indexOfCurr > 0 ? (Adaptation) ranges.get(indexOfCurr - 1) : null;
		Adaptation successor = indexOfCurr < ranges.size() - 1 ? (Adaptation) ranges.get(indexOfCurr + 1)
			: null;
		if (minChanged && predecessor != null)
		{
			boolean needFix = false;
			int minChange = compareMin(prevMin, minValue);
			Comparable pmax = (Comparable) predecessor.get(maxValuePath);
			if (prevMin == null || (minChange < 0 && checkGaps) || (minChange > 0 && checkOverlaps))
			{
				//see if predecessor needs to be fixed
				int compare = compareMinMax(RangeUtils.padMin(minValue, paddingNum), pmax);
				if (checkGaps && pmax != null && compare > 0)
					needFix = true;
				if (checkOverlaps && (pmax == null || compare < 0))
					needFix = true;
			}
			if (needFix)
			{
				Map<Path, Object> pathValueMap = new HashMap<Path, Object>();
				if (minValue == null)
				{
					pathValueMap.put(maxValuePath, RangeUtils.padMax(pmax, paddingNum));
					ModifyValuesProcedure.execute(pContext, existingRecord, pathValueMap);
				}
				else
				{
					pathValueMap.put(maxValuePath, RangeUtils.padMin(minValue, paddingNum));
					ModifyValuesProcedure.execute(pContext, predecessor, pathValueMap);
				}
			}
		}
		if (maxChanged && successor != null)
		{
			boolean needFix = false;
			int maxChange = compareMax(prevMax, maxValue);
			Comparable pmin = (Comparable) successor.get(minValuePath);
			if (prevMax == null || (maxChange > 0 && checkGaps) || (maxChange < 0 && checkOverlaps))
			{
				int compare = compareMinMax(pmin, RangeUtils.padMax(maxValue, paddingNum));
				if (checkGaps && pmin != null && compare > 0)
					needFix = true;
				if (checkOverlaps && (pmin == null || compare < 0))
					needFix = true;
			}
			if (needFix)
			{
				Map<Path, Object> pathValueMap = new HashMap<Path, Object>();
				if (maxValue == null)
				{
					pathValueMap.put(minValuePath, RangeUtils.padMin(pmin, paddingNum));
					ModifyValuesProcedure.execute(pContext, existingRecord, pathValueMap);
				}
				else
				{
					pathValueMap.put(minValuePath, RangeUtils.padMax(maxValue, paddingNum));
					ModifyValuesProcedure.execute(pContext, successor, pathValueMap);
				}
			}
		}

	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static int compareMinMax(Comparable min, Comparable max)
	{
		int result = 0;
		if (min == null)
			result = -1;
		else if (max == null)
			result = 1;
		else
			result = min.compareTo(max);
		return result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static int compareMin(Comparable aComp, Comparable bComp)
	{
		int result = 0;
		if (aComp == null)
			result = bComp == null ? 0 : -1;
		else if (bComp == null)
			result = 1;
		else
			result = aComp.compareTo(bComp);
		return result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static int compareMax(Comparable aComp, Comparable bComp)
	{
		int result = 0;
		if (aComp == null)
			result = bComp == null ? 0 : -1;
		else if (bComp == null)
			result = 1;
		else
			result = aComp.compareTo(bComp);
		return result;
	}

	private boolean shouldDeal()
	{
		Path minValuePath = getMinValuePath();
		Path maxValuePath = getMaxValuePath();
		boolean checkGaps = isCheckGaps();
		boolean checkOverlaps = isCheckOverlaps();
		if (!checkGaps && !checkOverlaps)
			return false; //nothing to do
		if (CollectionUtils.isEmpty(config.commonValuePaths)
			|| config.commonValuePaths.contains(null) || minValuePath == null
			|| maxValuePath == null)
		{
			return false;
		}
		return true;
	}

	@SuppressWarnings({ "rawtypes" })
	@Override
	public void handleAfterModify(AfterModifyOccurrenceContext aContext) throws OperationException
	{
		super.handleAfterModify(aContext);
		if (!shouldDeal())
			return;
		ValueChanges changes = aContext.getChanges();
		Comparable prevMin = null, minValue = null, prevMax = null, maxValue = null;
		Path minValuePath = getMinValuePath();
		Path maxValuePath = getMaxValuePath();
		ValueChange minChange = changes.getChange(minValuePath);
		if (minChange != null)
		{
			prevMin = (Comparable) minChange.getValueBefore();
			minValue = (Comparable) minChange.getValueAfter();
		}
		ValueChange maxChange = changes.getChange(maxValuePath);
		if (maxChange != null)
		{
			prevMax = (Comparable) maxChange.getValueBefore();
			maxValue = (Comparable) maxChange.getValueAfter();
		}
		dealWithRecord(
			aContext.getOccurrenceContext(),
			aContext.getAdaptationOccurrence(),
			minChange != null,
			prevMin,
			minValue,
			maxChange != null,
			prevMax,
			maxValue,
			aContext.getProcedureContext());
	}

}
