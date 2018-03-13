/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.valuefunction;

import java.util.*;

import com.onwbp.adaptation.*;
import com.orchestranetworks.schema.*;

/**
 * A <code>ValueFunction</code> that returns the oldest or newest value from a list of date fields, depending on how it's configured.
 */
public class OldestOrNewestDateValueFunction implements ValueFunction
{
	private static final String SEPARATOR = ";";

	private static final String OLDEST = "oldest";
	private static final String NEWEST = "newest";

	private String dateFields;
	private String oldestOrNewest;
	// Specifies how to treat nulls: If null, ignore them. If OLDEST, a null is the oldest possible date. If NEWEST, a null is the newest possible date.
	private String nullConfig;

	private Path[] dateFieldPaths;

	@Override
	public Object getValue(Adaptation adaptation)
	{
		Date returnVal = adaptation.getDate(dateFieldPaths[0]);
		// Matching on null if the nulls indicate the type of date we're looking for
		boolean matchOnNull = oldestOrNewest.equals(nullConfig);
		// If the first date is null and we're matching on nulls, then no need to look further
		if (returnVal == null && matchOnNull)
		{
			return returnVal;
		}
		// Look through the rest of the dates
		for (int i = 1; i < dateFieldPaths.length; i++)
		{
			Date value = adaptation.getDate(dateFieldPaths[i]);
			// If the new value is a better match (i.e. older or newer depending on what we're looking for)
			if (newMatchFound(returnVal, value))
			{
				// If the new value is null and we're matching on null then just return it since no need to look further
				// (null is the oldest or newest you can get)
				if (value == null && matchOnNull)
				{
					return value;
				}
				// Otherwise, this is the new value to beat
				returnVal = value;
			}
		}
		return returnVal;
	}

	private boolean newMatchFound(Date date1, Date date2)
	{
		if (date2 == null)
		{
			// If they're both null or we're ignoring nulls then no new match was found
			if (date1 == null || nullConfig == null)
			{
				return false;
			}
			// Otherwise, a new match was found only if what we're looking for is what null represents
			return oldestOrNewest.equalsIgnoreCase(nullConfig);
		}
		// If there's a date2 but date1 is null, then it's a new match only if nulls don't represent what we're looking for
		// (which also will handle if nulls are ignored)
		if (date1 == null)
		{
			return !oldestOrNewest.equalsIgnoreCase(nullConfig);
		}
		// Both aren't null so if we're looking for oldest then we found a new match if date2 is before date1
		if (OLDEST.equalsIgnoreCase(oldestOrNewest))
		{
			return date2.before(date1);
		}
		// Otherwise we found a new match if date2 is after date1
		return date2.after(date1);
	}

	@Override
	public void setup(ValueFunctionContext context)
	{
		if (dateFields == null || dateFields.trim().length() == 0)
		{
			context.addError("dateFields must be specified.");
		}
		else
		{
			SchemaNode rootNode = context.getSchemaNode()
				.getTableNode()
				.getTableOccurrenceRootNode();
			String[] dateFieldPathStrs = dateFields.split(SEPARATOR);
			if (dateFieldPathStrs.length < 2)
			{
				context.addError("At least two paths must be specified for dateFields.");
			}
			else
			{
				dateFieldPaths = new Path[dateFieldPathStrs.length];
				for (int i = 0; i < dateFieldPathStrs.length; i++)
				{
					Path dateFieldPath = Path.parse(dateFieldPathStrs[i]);
					if (dateFieldPath == null)
					{
						context.addError("Invalid format for dateFieldPaths.");
					}
					else
					{
						SchemaNode dateFieldNode = rootNode.getNode(dateFieldPath);
						if (dateFieldNode == null)
						{
							context.addError("Field " + dateFieldPath.format() + " does not exist.");
						}
						else
						{
							SchemaTypeName nodeType = dateFieldNode.getXsTypeName();
							if (!(SchemaTypeName.XS_DATE.equals(nodeType) || SchemaTypeName.XS_DATETIME.equals(nodeType)))
							{
								context.addError("Field " + dateFieldPath.format()
									+ " is not a date or dateTime field.");
							}
							else
							{
								dateFieldPaths[i] = dateFieldPath;
							}
						}
					}
				}
			}
		}
		if (oldestOrNewest == null)
		{
			context.addError("oldestOrNewest must be specified. Valid values are \"" + OLDEST
				+ "\" or \"" + NEWEST + "\".");
		}
		else
		{
			if (!(OLDEST.equalsIgnoreCase(oldestOrNewest) || NEWEST.equalsIgnoreCase(oldestOrNewest)))
			{
				context.addError("Invalid value for oldestOrNewest. Valid values are \"" + OLDEST
					+ "\" or \"" + NEWEST + "\".");
			}
		}
		if (nullConfig != null
			&& !(OLDEST.equalsIgnoreCase(nullConfig) || NEWEST.equalsIgnoreCase(nullConfig)))
		{
			context.addError("Invalid value for nullConfig. If specified, valid values are \""
				+ OLDEST + "\" or \"" + NEWEST + "\".");
		}
	}

	public String getDateFields()
	{
		return this.dateFields;
	}

	public void setDateFields(String dateFields)
	{
		this.dateFields = dateFields;
	}

	public String getOldestOrNewest()
	{
		return this.oldestOrNewest;
	}

	public void setOldestOrNewest(String oldestOrNewest)
	{
		this.oldestOrNewest = oldestOrNewest;
	}

	public String getNullConfig()
	{
		return this.nullConfig;
	}

	public void setNullConfig(String nullConfig)
	{
		this.nullConfig = nullConfig;
	}
}
