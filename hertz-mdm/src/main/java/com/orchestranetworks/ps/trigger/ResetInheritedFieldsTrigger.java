package com.orchestranetworks.ps.trigger;

import java.util.*;

import com.onwbp.adaptation.*;
import com.orchestranetworks.ps.util.*;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.schema.trigger.*;
import com.orchestranetworks.service.*;

/** 
 * Simple trigger is configured with paths to inherited fields that we would wish to reset to inherited
 * whenever the record is modified.  If no paths are supplied, all inherited fields of the table will be reset.
 */
public class ResetInheritedFieldsTrigger extends TableTrigger
{
	protected String pathsToResetString;
	private List<Path> pathsToReset;

	public String getPathsToResetString()
	{
		return pathsToResetString;
	}

	public void setPathsToResetString(String pathsToResetString)
	{
		this.pathsToResetString = pathsToResetString;
	}

	@Override
	public void setup(TriggerSetupContext context)
	{
		if (pathsToReset == null)
		{
			SchemaNode node = context.getSchemaNode().getTableNode().getTableOccurrenceRootNode();
			if (pathsToResetString != null)
			{
				pathsToReset = PathUtils.convertStringToPathList(pathsToResetString, null);
				//check each path represents an inherited field
				for (Path path : pathsToReset)
				{
					SchemaNode fieldNode = node.getNode(path);
					if (fieldNode == null || fieldNode.getInheritanceProperties() == null)
						context.addError("Path " + path.format() + " is not an inherited field");
				}
			}
			else
			{
				//if no paths are specified, assume user wants all inherited fields reset
				List<Path> paths = new ArrayList<Path>();
				collectInheritedFields(node, paths);
				this.pathsToReset = paths;
			}
		}
	}

	private static void collectInheritedFields(SchemaNode node, List<Path> paths)
	{
		if (node.getInheritanceProperties() != null)
		{
			paths.add(node.getPathInAdaptation());
			return;
		}
		SchemaNode[] children = node.getNodeChildren();
		for (SchemaNode childNode : children)
		{
			collectInheritedFields(childNode, paths);
		}
	}

	@Override
	public void handleBeforeModify(BeforeModifyOccurrenceContext aContext)
		throws OperationException
	{
		super.handleBeforeModify(aContext);
		ValueContextForUpdate context = aContext.getOccurrenceContextForUpdate();
		for (Path path : pathsToReset)
		{
			context.setValue(AdaptationValue.INHERIT_VALUE, path);
		}
	}

	@Override
	public void handleBeforeCreate(BeforeCreateOccurrenceContext aContext)
		throws OperationException
	{
		super.handleBeforeCreate(aContext);
		ValueContextForUpdate context = aContext.getOccurrenceContextForUpdate();
		for (Path path : pathsToReset)
		{
			context.setValue(AdaptationValue.INHERIT_VALUE, path);
		}
	}
}
