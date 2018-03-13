/*
 * Copyright Orchestra Networks 2016. All rights reserved.
 */
package com.orchestranetworks.ps.constraint;

import java.text.*;
import java.util.*;

import com.onwbp.adaptation.*;
import com.orchestranetworks.instance.*;
import com.orchestranetworks.ps.util.*;
import com.orchestranetworks.ps.util.functional.*;
import com.orchestranetworks.schema.*;

/**
 * This table constraint can be used to prevent cycles in the data.  Subclasses can override the setup method
 * to provide a getParent function, or this class can be used directly, by configuring it with a 'path expression'
 * which is a list of paths that express how to reach the 'parent' record from the record being validated.
 */
public class AcyclicConstraint extends BaseConstraintOnTableWithRecordLevelCheck
{
	protected String pathsToParentString;
	private List<Path> parentPath;
	private BinaryFunction<Adaptation, ValueContext, Adaptation> getParent;
	private String pathDisplay;
	private static final String MESSAGE = "Cycle in 'parent' path {0} detected";
	private static final String MESSAGE2 = "Cycle in 'parent' path {0} detected ({1})";

	public String getPathsToParentString()
	{
		return pathsToParentString;
	}

	public void setPathsToParentString(String pathsToParentString)
	{
		this.pathsToParentString = pathsToParentString;
	}

	public BinaryFunction<Adaptation, ValueContext, Adaptation> getGetParent()
	{
		return getParent;
	}

	public void setGetParent(BinaryFunction<Adaptation, ValueContext, Adaptation> getParent)
	{
		this.getParent = getParent;
	}

	@Override
	public void setup(ConstraintContextOnTable context)
	{
		if (getParent == null && pathsToParentString != null)
		{
			Locale locale = Locale.getDefault();
			this.parentPath = PathUtils.convertStringToPathList(pathsToParentString, null);
			SchemaNode currentNode = context.getSchemaNode();
			List<SchemaNode> nodes = PathUtils.validatePath(currentNode, parentPath);
			if (nodes.size() < parentPath.size())
			{
				context.addError("Path " + parentPath.get(nodes.size()).format() + " not found.");
			}
			else
			{
				SchemaNode lastNode = nodes.get(nodes.size() - 1);
				if (!AdaptationUtil.isRelationshipNode(lastNode))
					context.addError("Field " + lastNode.getLabel(locale)
						+ " does not specify a related record.");
			}
			final Path[] paths = parentPath.toArray(new Path[0]);
			getParent = new BinaryFunction<Adaptation, ValueContext, Adaptation>()
			{
				@Override
				public Adaptation evaluate(Adaptation record, ValueContext valueContext)
				{
					List<Adaptation> results;
					if (valueContext != null)
						results = AdaptationUtil.evaluatePath(valueContext, paths);
					else
						results = AdaptationUtil.evaluatePath(record, paths);
					return results.isEmpty() ? null : results.get(0);
				}
			};
			String parentPathString;
			if (parentPath == null || parentPath.isEmpty())
				parentPathString = "<unknown>";
			else
			{
				StringBuilder pathString = new StringBuilder();
				for (SchemaNode node : nodes)
				{
					if (pathString.length() > 0)
						pathString.append(PathUtils.DEFAULT_SEPARATOR);
					pathString.append(node.getLabel(locale));
				}
				parentPathString = pathString.toString();
			}
			pathDisplay = parentPathString;
		}
	}

	@Override
	public String toUserDocumentation(Locale locale, ValueContext context)
		throws InvalidSchemaException
	{
		return MessageFormat.format(MESSAGE, pathDisplay);
	}

	@Override
	protected String checkValueContext(ValueContext recordContext)
	{
		Adaptation record = AdaptationUtil.getRecordForValueContext(recordContext);
		if (record != null)
		{
			String cycle = AdaptationUtil.detectCycle(record, recordContext, getParent, null);
			if (cycle != null)
				return MessageFormat.format(MESSAGE2, pathDisplay, cycle);
		}
		return null;
	}

	public BinaryFunction<Adaptation, ValueContext, Adaptation> getGetParentFunction()
	{
		return getParent;
	}

}
