/*
 * Copyright Orchestra Networks 2000-2016. All rights reserved.
 */
package com.orchestranetworks.ps.ranges;

import java.util.List;
import java.util.Locale;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.base.misc.ObjectUtils;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.ps.constraint.BaseConstraintOnTableWithRecordLevelCheck;
import com.orchestranetworks.ps.ranges.RangeUtils.RangeInfo;
import com.orchestranetworks.ps.util.AdaptationUtil;
import com.orchestranetworks.ps.util.CollectionUtils;
import com.orchestranetworks.schema.ConstraintContextOnTable;
import com.orchestranetworks.schema.InvalidSchemaException;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaNode;

/**
 * A generic constraint to check a collection of records that represents a collection of ranges.
 * Can be used to check continuity (that there are no gaps between ranges) or non-overlap (no two records'
 * ranges overlap or both.  Specify a common path (a way to identify the other records in the 'collection'),
 * the path to the min boundary of range, the path to the max boundary of the range, whether the min is inclusive
 * (defaults to true), whether the max is inclusive (defaults to false).  The min/max of range should be
 * of the same type and should be comparable.
 * See <a href="https://docs.google.com/document/d/1I-zS_AJIoPGgEdt-BC6g-7HNEj34Y9QD16mw_R-P7bw/edit#">Range Support</a>
 * for more information. 
 */
public class RangesConstraint extends BaseConstraintOnTableWithRecordLevelCheck
{
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
	public void setup(ConstraintContextOnTable context)
	{
		config.setup(context);
	}

	@Override
	public String toUserDocumentation(Locale locale, ValueContext context)
		throws InvalidSchemaException
	{
		SchemaNode node = context.getNode();
		Path minValuePath = getMinValuePath();
		Path maxValuePath = getMaxValuePath();
		boolean checkGaps = isCheckGaps();
		boolean checkOverlaps = isCheckOverlaps();
		return createErrorMessage(
			locale,
			node.getNode(minValuePath),
			node.getNode(maxValuePath),
			checkOverlaps,
			checkGaps);
	}

	private static String createErrorMessage(
		Locale locale,
		SchemaNode minNode,
		SchemaNode maxNode,
		RangeInfo<Adaptation> info)
	{
		StringBuilder bldr = new StringBuilder();
		if (RangeUtils.State.Overlap.equals(info.getState()))
		{
			bldr.append(AdaptationUtil.GetLabel.evaluate(info.getObject(), locale))
				.append(" has its ")
				.append(maxNode.getLabel(locale))
				.append(" greater than the ")
				.append(minNode.getLabel(locale))
				.append(" of ")
				.append(AdaptationUtil.GetLabel.evaluate(info.getOtherObject(), locale))
				.append(", causing an overlap.");
		}
		else
		{
			bldr.append(AdaptationUtil.GetLabel.evaluate(info.getObject(), locale))
				.append(" has its ")
				.append(maxNode.getLabel(locale))
				.append(" less than the ")
				.append(minNode.getLabel(locale))
				.append(" of ")
				.append(AdaptationUtil.GetLabel.evaluate(info.getOtherObject(), locale))
				.append(", causing a gap.");
		}
		return bldr.toString();
	}

	private static String createErrorMessage(
		Locale locale,
		SchemaNode minNode,
		SchemaNode maxNode,
		boolean checkOverlap,
		boolean checkGap)
	{
		StringBuilder bldr = new StringBuilder();
		bldr.append(minNode.getLabel(locale));
		bldr.append(" and ");
		bldr.append(maxNode.getLabel(locale));
		bldr.append(" cannot ");
		if (checkOverlap)
		{
			bldr.append("overlap");
			if (checkGap)
				bldr.append(" or ");
		}
		if (checkGap)
		{
			bldr.append("leave gaps");
		}
		bldr.append(" between records.");
		return bldr.toString();
	}

	@SuppressWarnings({ "rawtypes" })
	@Override
	protected String checkValueContext(ValueContext recordContext)
	{
		Path minValuePath = getMinValuePath();
		Path maxValuePath = getMaxValuePath();
		boolean checkGaps = isCheckGaps();
		boolean checkOverlaps = isCheckOverlaps();
		if (!checkGaps && !checkOverlaps)
			return null; //nothing to do
		List<Path> commonValuePaths = config.commonValuePaths;
		if (CollectionUtils.isEmpty(commonValuePaths) || commonValuePaths.contains(null)
			|| minValuePath == null || maxValuePath == null)
		{
			return null;
		}
		List<Object> commonValues = AdaptationUtil.getPathValues(recordContext, commonValuePaths);
		//gap check only works with [)/(] ranges -- we assume minInclusive != maxInclusive
		Comparable minValue = (Comparable) recordContext.getValue(minValuePath);
		Comparable maxValue = (Comparable) recordContext.getValue(maxValuePath);
		if (commonValues.contains(null) || (minValue == null && maxValue == null))
		{
			return null;
		}
		Adaptation existingRecord = AdaptationUtil.getRecordForValueContext(recordContext);
		// if this record is being modified, as opposed to being checked, let this all ride
		if (existingRecord != null)
		{
			Object prevMin = existingRecord.get(minValuePath);
			Object prevMax = existingRecord.get(maxValuePath);
			if (!ObjectUtils.equals(prevMin, minValue) || !ObjectUtils.equals(prevMax, maxValue))
			{
				return null;
			}
		}
		List<Adaptation> ranges = AdaptationUtil
			.fetchRecordsMatching(recordContext, commonValuePaths, commonValues);
		RangeInfo<Adaptation> rangeInfo = RangeUtils.checkRanges(
			ranges,
			minValuePath,
			maxValuePath,
			existingRecord,
			minValue,
			maxValue,
			checkOverlaps,
			checkGaps,
			config.paddingNum);
		if (rangeInfo != null)
		{
			SchemaNode tableNode = recordContext.getAdaptationTable()
				.getTableNode()
				.getTableOccurrenceRootNode();
			SchemaNode minNode = tableNode.getNode(minValuePath);
			SchemaNode maxNode = tableNode.getNode(maxValuePath);
			return createErrorMessage(Locale.getDefault(), minNode, maxNode, rangeInfo);
		}
		return null;
	}

}