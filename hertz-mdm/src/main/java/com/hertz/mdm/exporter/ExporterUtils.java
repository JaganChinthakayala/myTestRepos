package com.hertz.mdm.exporter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.service.LoggingCategory;
import com.orchestranetworks.service.OperationException;

/**
 * A utility class that contains some static methods for use with the exporter
 */
public abstract class ExporterUtils {
	protected static final LoggingCategory LOG = LoggingCategory.getKernel();
	
	/**
	 * Get the field nodes to export for a given table based on which paths are specified
	 * as included or excluded. If no paths are specified for include or exclude, then
	 * include all field nodes for the table.
	 * 
	 * @param tableNode the table node
	 * @param includeFieldPaths the paths to include
	 * @param excludeFieldPaths the paths to exclude
	 * @return the list of field nodes
	 * @throws OperationException if there's an error getting the nodes
	 */
	protected static List<SchemaNode> getFieldNodes(final SchemaNode tableNode,
			final List<Path> includeFieldPaths, final List<Path> excludeFieldPaths)
			throws OperationException {
		if (LOG.isDebug()) {
			LOG.debug("ExporterUtils: getFieldNodes");
			LOG.debug("ExporterUtils: tableNode = " + tableNode.getPathInSchema().format());
		}
		// Get all of the nodes
		final List<SchemaNode> allFieldNodes = getAllFieldNodes(tableNode);
		List<SchemaNode> fieldNodes = new ArrayList<SchemaNode>();
		if (includeFieldPaths == null || includeFieldPaths.isEmpty()) {
			if (excludeFieldPaths == null || excludeFieldPaths.isEmpty()) {
				// No paths were specified at all, so return all nodes
				fieldNodes = allFieldNodes;
			} else {
				// Only exclude paths were specified so loop through all nodes and only
				// return it if it's not in the exclude list.
				for (SchemaNode fieldNode: allFieldNodes) {
					final Path fieldPath = Path.SELF.add(fieldNode.getPathInAdaptation());
					if (! excludeFieldPaths.contains(fieldPath)) {
						fieldNodes.add(fieldNode);
					}
				}
			}
		} else {
			// Include paths were specified so put all nodes in a map, then loop through the
			// include paths and pull only those from the nodes map.
			final Map<Path, SchemaNode> allFieldNodesMap = getFieldNodesAsMap(allFieldNodes);
			for (final Path includeFieldPath: includeFieldPaths) {
				final SchemaNode fieldNode = allFieldNodesMap.get(includeFieldPath);
				if (fieldNode == null) {
					throw OperationException.createError(
							"Path for included field is invalid: " + includeFieldPath.format());
				} else {
					fieldNodes.add(fieldNode);
				}
			}
		}
		return fieldNodes;
	}
	
	/**
	 * Get all table nodes for a schema node. This will recurse through all schema node
	 * children.
	 * 
	 * @param parentNode the parent schema node
	 * @return all table nodes
	 */
	protected static Set<SchemaNode> getAllTableNodes(final SchemaNode parentNode) {
		if (LOG.isDebug()) {
			LOG.debug("ExporterUtils: getAllTableNodes");
			LOG.debug("ExporterUtils: parentNode = " + parentNode.getPathInSchema().format());
		}
		final HashSet<SchemaNode> allTableNodes = new HashSet<SchemaNode>();
		// Get the immediate schema node children
		final SchemaNode[] nodeChildren = parentNode.getNodeChildren();
		// Loop through all children
		for (int i = 0; i < nodeChildren.length; i++) {
			final SchemaNode child = nodeChildren[i];
			// If the child is a table node, include it
			if (child.isTableNode()) {
				allTableNodes.add(child);
			// Else it's a schema node so recursively call this method on the child
			} else {
				allTableNodes.addAll(getAllTableNodes(child));
			}
		}
		return allTableNodes;
	}
	
	// Get all the field nodes under a schema node, recursively looking through child schema nodes.
	private static List<SchemaNode> getAllFieldNodes(final SchemaNode parentNode) {
		if (LOG.isDebug()) {
			LOG.debug("ExporterUtils: getAllFieldNodes");
			LOG.debug("ExporterUtils: parentNode = " + parentNode.getPathInSchema().format());
		}
		// Tables have a special root node that you need to get in order to get its fields
		final SchemaNode node = parentNode.isTableNode()
				? parentNode.getTableOccurrenceRootNode() : parentNode;
		// Get all the immediate child nodes
		final SchemaNode[] children = node.getNodeChildren();
		final ArrayList<SchemaNode> fieldNodes = new ArrayList<SchemaNode>();
		// Loop through all the children
		for (int i = 0; i < children.length; i++) {
			final SchemaNode child = children[i];
			// If it's a group or a table, then recursively call this method on the child
			if (child.isComplex() || child.isTableNode()) {
				fieldNodes.addAll(getAllFieldNodes(child));
			// Else it is a field node itself so include it
			} else {
				fieldNodes.add(child);
			}
		}
		return fieldNodes;
	}
	
	// Put the given nodes into a map with their paths as the keys
	private static Map<Path, SchemaNode> getFieldNodesAsMap(final List<SchemaNode> fieldNodes) {
		final HashMap<Path, SchemaNode> map = new HashMap<Path, SchemaNode>();
		for (SchemaNode fieldNode: fieldNodes) {
			final Path path = Path.SELF.add(fieldNode.getPathInAdaptation());
			map.put(path, fieldNode);
		}
		return map;
	}
}
