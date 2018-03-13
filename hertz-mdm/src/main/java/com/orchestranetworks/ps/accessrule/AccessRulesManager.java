package com.orchestranetworks.ps.accessrule;

import java.util.*;

import com.onwbp.adaptation.*;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.service.*;

/**
 * AccessRuleManager, as its name implies, manages access rules.  In essence, this class delegates to
 * SchemaExtensionsContext.  Access rules can be occurrence/record level rules or
 * node/field level rules, and can also be configured for a node and all its descendants.  Typically, an
 * implementation of SchemaExtensions will be defined and associated with a data model, and in its defineExtensions
 * definition, an AccessRulesManager will be constructed and rules will be added to it using one or more of the
 * 'setAccessRulesOn' methods. 
 */
public class AccessRulesManager implements AccessRule
{
	private final Map<Path, List<AccessRule>> rulesOnNodes = new HashMap<Path, List<AccessRule>>();
	private final Map<Path, List<AccessRule>> rulesOnOccurrences = new HashMap<Path, List<AccessRule>>();
	private final SchemaExtensionsContext context;

	public AccessRulesManager(SchemaExtensionsContext context)
	{
		this.context = context;
	}

	/**
	 * When using AccessRulesManager as an AccessRule (as opposed to a delegate to SchemaExtensionsContext)
	 * To obtain the permission for a adaptation/node within the context of a session:
	 * If the adaptation is a record, and the schema node represents that table occurrence,
	 * find the associated rules in the rulesOnOccurrences map.
	 * Otherwise, the rules are found in the rulesOnNodes map for the pNode's pathInSchema.
	 * If there are rules for the occurrence/node, then the minimum permission in the list is returned.
	 */
	@Override
	public AccessPermission getPermission(Adaptation pAdaptation, Session pSession, SchemaNode pNode)
	{
		List<AccessRule> rules = null;
		if (pAdaptation.isTableOccurrence() && pNode.equals(pAdaptation.getSchemaNode()))
		{
			rules = rulesOnOccurrences.get(pNode.getTableNode().getPathInSchema());
		}
		else
		{
			rules = rulesOnNodes.get(pNode.getPathInSchema());
		}
		AccessPermission permission = AccessPermission.getReadWrite();
		if (rules != null)
		{
			for (AccessRule aRule : rules)
			{
				permission = permission.min(aRule.getPermission(pAdaptation, pSession, pNode));
			}
		}
		return permission;
	}

	/**
	 * Delegates to the SchemaExtensionsContext and adds rule to the local map.
	 * @param pPath - the location of the node on which to set the rule
	 * @param pRule - specifies the rule to be set
	 * @see SchemaExtensionsContext#setAccessRuleOnNode(Path,AccessRule)
	 */
	public void setAccessRuleOnNode(Path pPath, AccessRule pRule)
	{
		context.setAccessRuleOnNode(pPath, this);
		addRuleToMap(pPath, pRule, rulesOnNodes);

	}

	private void addRuleToMap(Path pPath, AccessRule pRule, Map<Path, List<AccessRule>> rulesMap)
	{
		List<AccessRule> rules = rulesMap.get(pPath);
		if (rules != null)
		{
			rules.add(pRule);
		}
		else
		{
			rules = new ArrayList<AccessRule>();
			rules.add(pRule);
			rulesMap.put(pPath, rules);
		}
	}

	/**
	 * Delegates to the SchemaExtensionsContext and adds rule to the local map.
	 * @param aTablePath - location of the table node
	 * @param aRule - specifies the rule to be set
	 * @see SchemaExtensionsContext#setAccessRuleOnOccurrence(Path, AccessRule)
	 */
	public void setAccessRuleOnOccurrence(Path aTablePath, AccessRule aRule)
	{
		context.setAccessRuleOnOccurrence(aTablePath, this);
		addRuleToMap(aTablePath, aRule, rulesOnOccurrences);
	}

	/**
	 * Recursively add the specified rule to the children of the specified node.
	 * Rules will be added as 'accessRuleOnNode'.
	 * If pIncludeRoot is specified, also add the rule to the specified node.
	 * @param pPath - location of the root node
	 * @param pIncludeRoot - answers whether or not the rule applies to the root node
	 * @param pRule - specifies the rule to be set
	 * @see SchemaExtensionsContext#setAccessRuleOnNode(Path,AccessRule)
	 */
	public void setAccessRuleOnNodeAndAllDescendants(
		Path pPath,
		boolean pIncludeRoot,
		AccessRule pRule)
	{
		if (pIncludeRoot)
		{
			setAccessRuleOnNode(pPath, pRule);
		}

		SchemaNode rootNode = context.getSchemaNode().getNode(pPath);

		if (rootNode.isTableNode())
		{
			rootNode = rootNode.getTableOccurrenceRootNode();
		}
		for (SchemaNode aNode : rootNode.getNodeChildren())
		{
			setAccessRuleOnNodeAndAllDescendants(aNode.getPathInSchema(), true, pRule);
		}
	}
}
