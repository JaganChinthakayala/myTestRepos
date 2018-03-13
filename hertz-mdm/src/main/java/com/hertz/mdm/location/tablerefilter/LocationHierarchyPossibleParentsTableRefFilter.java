package com.hertz.mdm.location.tablerefilter;

import java.util.Locale;

import com.hertz.mdm.location.path.LocationPaths;
import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.RequestResult;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.schema.InvalidSchemaException;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.TableRefFilter;
import com.orchestranetworks.schema.TableRefFilterContext;

public class LocationHierarchyPossibleParentsTableRefFilter implements TableRefFilter
{
	private static final Path HIERARCHY_DEFINITION_NODE_PATH = Path.PARENT
		.add(LocationPaths._Root_LocationHierarchy._HierarchyDefinitionNode);

	@Override
	public boolean accept(Adaptation record, ValueContext context)
	{
		Adaptation locationHierarchyRecord = record;

		if (context.getValue(HIERARCHY_DEFINITION_NODE_PATH) == null)
		{
			return false;
		}

		//Get the Target Node Hierarchy Definition Node Id
		String hiearchyDefinitionNodeId = (String) context.getValue(HIERARCHY_DEFINITION_NODE_PATH)
			.toString();
		//Get the Location Hierarchy Definition Node Id for this Node
		String locationHierarchyDefinitionNodeId = locationHierarchyRecord
			.getString(LocationPaths._Root_LocationHierarchy._HierarchyDefinitionNode);

		if (hiearchyDefinitionNodeId == null
			|| hiearchyDefinitionNodeId.equals(locationHierarchyDefinitionNodeId))
		{
			return false;
		}

		AdaptationTable locationStructureDefinitionParentRelationshipTable = context
			.getAdaptationInstance().getTable(
				LocationPaths._Root_HierarchyDefinitionData_LocationStructureDefinitionParentRelationship
					.getPathInSchema());

		// Query the LocationStructureDefinitionParentRelationship table for row(s) that have the selected LocaitonHierarchyNode 
		// to determine if the Target Node has this node as a Parent
		RequestResult requestResult = locationStructureDefinitionParentRelationshipTable
			.createRequestResult(
				LocationPaths._Root_HierarchyDefinitionData_LocationStructureDefinitionParentRelationship._LocationStructureDefinition
					.format() + "='" + hiearchyDefinitionNodeId + "' and "
					+ LocationPaths._Root_HierarchyDefinitionData_LocationStructureDefinitionParentRelationship._Parent
						.format()
					+ "='" + locationHierarchyDefinitionNodeId + "'");

		//If requestResult is Empty this node cannot be the Target's Parent
		if (requestResult.isEmpty())
		{
			requestResult.close();
			return false;
		}
		requestResult.close();
		return true;

	}
	@Override
	public void setup(TableRefFilterContext arg0)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public String toUserDocumentation(Locale arg0, ValueContext arg1) throws InvalidSchemaException
	{
		// TODO Auto-generated method stub
		return null;
	}
}
