package com.orchestranetworks.ps.ranges;

import java.util.*;

import com.orchestranetworks.ps.util.*;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.schema.info.*;

public class RangeConfig
{
	private String commonValuePathsString;
	protected List<Path> commonValuePaths;
	private Path minValuePath;
	private Path maxValuePath;
	private boolean minInclusive = true;
	private boolean maxInclusive = false;
	private boolean checkOverlaps = true;
	protected boolean checkGaps = false;
	private boolean usePadding = false;
	private String padding;
	protected float paddingNum = 0;

	public Path getCommonValuePath()
	{
		return CollectionUtils.getFirstOrNull(commonValuePaths);
	}

	public void setCommonValuePath(Path commonValuePath)
	{
		this.commonValuePaths = Collections.singletonList(commonValuePath);
	}

	public String getCommonValuePathsString()
	{
		return commonValuePathsString;
	}

	public void setCommonValuePathsString(String commonValuePathsString)
	{
		this.commonValuePathsString = commonValuePathsString;
	}

	public Path getMinValuePath()
	{
		return minValuePath;
	}

	public void setMinValuePath(Path minValuePath)
	{
		this.minValuePath = minValuePath;
	}

	public Path getMaxValuePath()
	{
		return maxValuePath;
	}

	public void setMaxValuePath(Path maxValuePath)
	{
		this.maxValuePath = maxValuePath;
	}

	public boolean isMinInclusive()
	{
		return minInclusive;
	}

	public void setMinInclusive(boolean minInclusive)
	{
		this.minInclusive = minInclusive;
	}

	public boolean isMaxInclusive()
	{
		return maxInclusive;
	}

	public void setMaxInclusive(boolean maxInclusive)
	{
		this.maxInclusive = maxInclusive;
	}

	public boolean isCheckOverlaps()
	{
		return checkOverlaps;
	}

	public void setCheckOverlaps(boolean checkOverlaps)
	{
		this.checkOverlaps = checkOverlaps;
	}

	public boolean isCheckGaps()
	{
		return checkGaps;
	}

	public void setCheckGaps(boolean checkGaps)
	{
		this.checkGaps = checkGaps;
	}

	public boolean isUsePadding()
	{
		return usePadding;
	}

	public void setUsePadding(boolean usePadding)
	{
		this.usePadding = usePadding;
	}

	public String getPadding()
	{
		return padding;
	}

	public void setPadding(String padding)
	{
		this.padding = padding;
	}

	public void setup(SchemaNodeContext context)
	{
		SchemaNode parentNode = context.getSchemaNode();
		if (commonValuePathsString != null)
		{
			commonValuePaths = PathUtils.convertStringToPathList(commonValuePathsString, null);
		}
		for (Path path : commonValuePaths)
		{
			setupNode(context, parentNode, path, "commonValue");
		}
		SchemaNode minNode = setupNode(context, parentNode, minValuePath, "minValue");
		SchemaNode maxNode = setupNode(context, parentNode, maxValuePath, "maxValue");
		SchemaTypeName minType = minNode.getXsTypeName();
		SchemaTypeName maxType = maxNode.getXsTypeName();
		if (minType == null || maxType == null || !minType.equals(maxType))
		{
			context.addError("Nodes " + minValuePath.format() + " and " + maxValuePath.format()
				+ " must be matching comparable types");
		}
		else if (usePadding)
		{
			if (padding == null)
			{
				//determine the default padding based on the datatype
				if (SchemaTypeName.XS_DATE.equals(minType))
					paddingNum = RangeUtils.ONE_DAY_IN_MILLIS;
				else if (SchemaTypeName.XS_DATETIME.equals(minType))
					paddingNum = RangeUtils.ONE_SECOND_IN_MILLIS;
				else if (SchemaTypeName.XS_INT.equals(minType)
					|| SchemaTypeName.XS_INTEGER.equals(minType))
					paddingNum = RangeUtils.ONE;
				else if (SchemaTypeName.XS_DECIMAL.equals(minType))
				{
					SchemaFacetFractionDigits scaleFacet = maxNode.getFacetFractionDigits();
					if (scaleFacet != null)
					{
						int scale = scaleFacet.getFractionDigits();
						paddingNum = (float) 1 / (10 ^ scale);
					}
					else
					{
						paddingNum = RangeUtils.ONE_HUNDREDTH;
					}
				}
				else
				{
					context.addError("Range support with padding is limited to date, dateTime, and numeric ranges");
				}
			}
			else
			{
				try
				{
					paddingNum = Float.parseFloat(padding);
				}
				catch (NumberFormatException e)
				{
					context.addError("Padding should be a numeric value");
				}
			}
		}
		if (minInclusive == maxInclusive)
		{
			if (minInclusive && !usePadding)
				context.addError("When constraining a series of ranges, best practice is to us [) or (] so edges can match");
		}
	}

	private static SchemaNode setupNode(
		SchemaNodeContext context,
		SchemaNode parentNode,
		Path path,
		String paramName)
	{
		if (path == null)
		{
			context.addError(paramName + " must be specified.");
		}
		else
		{
			SchemaNode node = parentNode.getNode(path);
			if (node == null)
			{
				context.addError("Node " + path.format() + " not found.");
			}
			return node;
		}
		return null;
	}

}
