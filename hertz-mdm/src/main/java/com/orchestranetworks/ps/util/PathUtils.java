package com.orchestranetworks.ps.util;

import java.util.*;

import com.orchestranetworks.schema.*;
import com.orchestranetworks.schema.info.*;

/**
 * Utilities to convert strings to paths, find nodes, etc. 
 */
public class PathUtils
{
	public static final String DEFAULT_SEPARATOR = ";";

	public static Set<Path> convertStringToPathSet(String pathStr, String separator)
	{
		Set<Path> paths = new HashSet<Path>();
		addPaths(paths, pathStr, separator);
		return paths;
	}

	public static List<Path> convertStringToPathList(String pathStr, String separator)
	{
		List<Path> paths = new ArrayList<Path>();
		addPaths(paths, pathStr, separator);
		return paths;
	}

	private static void addPaths(Collection<Path> paths, String pathStr, String separator)
	{
		if (separator == null)
			separator = DEFAULT_SEPARATOR;
		if (pathStr != null)
		{
			String[] arr = pathStr.split(separator);
			for (String str : arr)
			{
				paths.add(Path.parse(str.trim()));
			}
		}
	}

	/**
	 * Validate that the path list provided represents a legitimate path expression
	 * and return the resolved schema nodes for the paths.
	 * @param root
	 * @param paths
	 * @return resolved schema nodes
	 */
	public static List<SchemaNode> validatePath(SchemaNode root, List<Path> paths)
	{
		SchemaNode currNode = root.getTableNode().getTableOccurrenceRootNode();
		List<SchemaNode> nodes = new ArrayList<SchemaNode>();
		for (Path path : paths)
		{
			currNode = currNode != null ? currNode.getNode(path) : currNode;
			if (currNode == null)
				return nodes;
			nodes.add(currNode);
			SchemaNode tableNode = AdaptationUtil.getTableNodeForRelated(currNode);
			if (tableNode != null)
			{
				currNode = tableNode.getTableOccurrenceRootNode();
			}
		}
		return nodes;
	}

	/**
	 * Many component setup methods need to take a path and get the related field, and
	 * report errors when the path is missing or if the path is not a field, etc.
	 * @param context SchemaNodeContext e.g. from most setup methods
	 * @param path path representing a field from the SchemaNodeContext's table node
	 * @param pathName the name for the error message
	 * @param addDependency -- for constraint context, optionally add field as a dependency
	 * @return SchemaNode the node corresponding to the field
	 */
	public static SchemaNode setupFieldNode(
		SchemaNodeContext context,
		Path path,
		String pathName,
		boolean addDependency)
	{
		if (path == null)
		{
			context.addError(pathName + " must be specified.");
		}
		else
		{
			SchemaNode fieldNode = context.getSchemaNode().getTableNode().getTableOccurrenceRootNode().getNode(path);
			if (fieldNode == null)
			{
				if (pathName == null)
					context.addError(path.format() + " does not exist.");
				else
					context.addError(pathName + " " + path.format() + " does not exist.");
			}
			if (addDependency && context instanceof DependenciesDefinitionContext)
			{
				addDependency((DependenciesDefinitionContext) context, fieldNode);
			}
			return fieldNode;
		}
		return null;
	}

	/**
	 * Add a fieldNode as a dependency.  If the field is an association field, add dependency to the association table.
	 * @param context
	 * @param fieldNode
	 */
	public static void addDependency(DependenciesDefinitionContext context, SchemaNode fieldNode)
	{
		if (fieldNode.isAssociationNode())
		{
			AssociationLink link = fieldNode.getAssociationLink();
			if (link.isLinkTable())
			{
				AssociationLinkByLinkTable alink = (AssociationLinkByLinkTable) link;
				Path tablePath = alink.getLinkTablePath();
				SchemaNode tableNode = fieldNode.getNode(tablePath);
				context.addDependencyToInsertDeleteAndModify(tableNode);
			}
			else if (link.isTableRefInverse())
			{
				AssociationLinkByTableRefInverse alink = (AssociationLinkByTableRefInverse) link;
				Path tablePath = alink.getFieldToSourcePath();
				SchemaNode tableNode = fieldNode.getNode(tablePath).getTableNode();
				context.addDependencyToInsertDeleteAndModify(tableNode);
			}
		}
		else
		{
			context.addDependencyToInsertDeleteAndModify(fieldNode);
		}
	}
}
