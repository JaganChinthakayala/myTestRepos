/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.valuefunction;

import java.util.*;

import org.apache.commons.lang.*;

import com.onwbp.adaptation.*;
import com.orchestranetworks.ps.util.*;
import com.orchestranetworks.ps.util.functional.*;
import com.orchestranetworks.schema.*;

/**
 * Given a list field and a separator, return the string representation of that list.
 * If the list items are Adaptations, use the label of the adaptation.
 */
public class ListAsStringValueFunction implements ValueFunction
{
	private String pathsString;
	private Path[] pathOfPaths;
	private Path lastPath;
	private String separator = ", ";

	public static final UnaryFunction<Object, String> toString = new UnaryFunction<Object, String>()
	{
		@Override
		public String evaluate(Object object)
		{
			if (object instanceof Adaptation)
			{
				return ((Adaptation) object).getLabel(Locale.getDefault());
			}
			return String.valueOf(object);
		}
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object getValue(Adaptation adaptation)
	{
		List list;
		if (pathOfPaths != null)
		{
			list = lastPath != null ? AdaptationUtil.evaluatePath(
				adaptation,
				pathOfPaths,
				lastPath,
				true) : AdaptationUtil.evaluatePath(adaptation, pathOfPaths);
		}
		else
		{
			list = adaptation.getList(lastPath);
		}
		if (list == null)
			return null;
		List<String> stringList = Algorithms.apply(list, toString);
		return StringUtils.join(stringList, separator);
	}

	@Override
	public void setup(ValueFunctionContext context)
	{
		if (pathsString == null)
		{
			context.addError("pathsString must be specified.");
		}
		else
		{
			List<Path> paths = PathUtils.convertStringToPathList(pathsString, null);
			if (paths.isEmpty())
				context.addError("pathsString must specify at least one path.");
			List<SchemaNode> nodes = PathUtils.validatePath(context.getSchemaNode(), paths);
			boolean hasCollection = false;
			int nodesSize = nodes.size();
			int pathsSize = paths.size();
			if (nodesSize < pathsSize)
			{
				Path unresolvedPath = paths.get(nodesSize);
				context.addError("Path element " + unresolvedPath.format() + " does not exist.");
				return;
			}
			for (int i = 0; i < paths.size(); i++)
			{
				SchemaNode node = nodes.get(i);
				if (node.getMaxOccurs() != 1)
				{
					hasCollection = true;
				}
			}
			if (!hasCollection)
				context.addError("Paths should resolve to a collection.");
			boolean lastIsRelationship = AdaptationUtil.isRelationshipNode(nodes.get(nodes.size() - 1));
			if (!lastIsRelationship)
				lastPath = paths.remove(paths.size() - 1);
			if (!paths.isEmpty())
				pathOfPaths = paths.toArray(new Path[0]);
		}
	}

	public String getSeparator()
	{
		return separator;
	}

	public void setSeparator(String separator)
	{
		this.separator = separator;
	}

	public String getPathsString()
	{
		return pathsString;
	}

	public void setPathsString(String pathsString)
	{
		this.pathsString = pathsString;
	}
}
