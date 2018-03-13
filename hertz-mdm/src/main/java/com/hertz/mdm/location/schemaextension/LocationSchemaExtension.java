/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.hertz.mdm.location.schemaextension;

import com.hertz.mdm.location.accessrule.HierarchyStructureDefinitionHierarchyNodeAccessRule;
import com.hertz.mdm.location.accessrule.HierarchyStructureDefinitionNodeAccessRule;
import com.hertz.mdm.location.accessrule.LocationLocationTypeGroupAccessRule;
import com.hertz.mdm.location.accessrule.LocationTimeZoneAccessRule;
import com.hertz.mdm.location.accessrule.ServedBySatelliteAccessRule;
import com.hertz.mdm.location.accessrule.ServiceHoursSeasonalDatesAccessRule;
import com.hertz.mdm.location.path.LocationPaths;
import com.orchestranetworks.ps.accessrule.AccessRulesManager;
import com.orchestranetworks.ps.accessrule.WorkflowAccessRule;
import com.orchestranetworks.schema.SchemaExtensions;
import com.orchestranetworks.schema.SchemaExtensionsContext;

/**
 */
public class LocationSchemaExtension implements SchemaExtensions
{
	@Override
	public void defineExtensions(SchemaExtensionsContext context)
	{
		AccessRulesManager manager = new AccessRulesManager(context);

		manager.setAccessRuleOnNodeAndAllDescendants(
			LocationPaths._Root,
			false,
			new WorkflowAccessRule());

		//Hides Parents tab for a node of type "Hierarchy"
		manager.setAccessRuleOnNodeAndAllDescendants(
			LocationPaths._Root_HierarchyDefinitionData_HierarchyStructureDefinition
				.getPathInSchema()
				.add(
					LocationPaths._Root_HierarchyDefinitionData_HierarchyStructureDefinition._Parents),
			true,
			new HierarchyStructureDefinitionHierarchyNodeAccessRule());
		//Hides Countries tab for all nodes except type "Hierarchy"
		manager.setAccessRuleOnNodeAndAllDescendants(
			LocationPaths._Root_HierarchyDefinitionData_HierarchyStructureDefinition
				.getPathInSchema()
				.add(
					LocationPaths._Root_HierarchyDefinitionData_HierarchyStructureDefinition._Countries),
			true,
			new HierarchyStructureDefinitionNodeAccessRule());
		//Hides Contact Types tab for all nodes except type "Hierarchy"
		manager.setAccessRuleOnNodeAndAllDescendants(
			LocationPaths._Root_HierarchyDefinitionData_HierarchyStructureDefinition
				.getPathInSchema()
				.add(
					LocationPaths._Root_HierarchyDefinitionData_HierarchyStructureDefinition._ContactTypes),
			true,
			new HierarchyStructureDefinitionHierarchyNodeAccessRule());

		//Hides the appropriate set of Date Ranges on LocationServiceDays based upon isSeasonal
		manager.setAccessRuleOnNodeAndAllDescendants(
			LocationPaths._Root_LocationData_ServiceDays.getPathInSchema()
				.add(LocationPaths._Root_LocationData_ServiceDays._DayOfWeek),
			true,
			new ServiceHoursSeasonalDatesAccessRule(Boolean.FALSE));
		manager.setAccessRuleOnNodeAndAllDescendants(
			LocationPaths._Root_LocationData_ServiceDays.getPathInSchema()
				.add(LocationPaths._Root_LocationData_ServiceDays._DateRange),
			true,
			new ServiceHoursSeasonalDatesAccessRule(Boolean.FALSE));
		manager.setAccessRuleOnNodeAndAllDescendants(
			LocationPaths._Root_LocationData_ServiceDays.getPathInSchema()
				.add(LocationPaths._Root_LocationData_ServiceDays._SeasonalDateRange),
			true,
			new ServiceHoursSeasonalDatesAccessRule(Boolean.TRUE));

		manager.setAccessRuleOnNodeAndAllDescendants(
			LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship.getPathInSchema()
				.add(
					LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship._LocationTypeGroupType),
			true,
			new LocationLocationTypeGroupAccessRule(Boolean.FALSE, null));
		manager.setAccessRuleOnNodeAndAllDescendants(
			LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship.getPathInSchema()
				.add(
					LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship._LocationTypeGroupTypes),
			true,
			new LocationLocationTypeGroupAccessRule(Boolean.TRUE, null));

		/*
		//Set Permissions on Airport Types
		manager.setAccessRuleOnNodeAndAllDescendants(
			LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship.getPathInSchema()
				.add(
					LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship._AirportType),
			true,
			new LocationLocationTypeGroupAccessRule(Boolean.FALSE, Boolean.TRUE));
		manager.setAccessRuleOnNodeAndAllDescendants(
			LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship.getPathInSchema()
				.add(
					LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship._AirportTypes),
			true,
			new LocationLocationTypeGroupAccessRule(Boolean.TRUE, Boolean.TRUE));
		
		//Set Permissions on Off-Airport
		manager.setAccessRuleOnNodeAndAllDescendants(
			LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship.getPathInSchema()
				.add(
					LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship._NonAirportType),
			true,
			new LocationLocationTypeGroupAccessRule(Boolean.FALSE, Boolean.FALSE));
		manager.setAccessRuleOnNodeAndAllDescendants(
			LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship.getPathInSchema()
				.add(
					LocationPaths._Root_LocationData_LocationLocationTypeGroupRelationship._NonAirportTypes),
			true,
			new LocationLocationTypeGroupAccessRule(Boolean.TRUE, Boolean.FALSE));
			*/

		//Set Permissions for Time Zone
		manager.setAccessRuleOnNodeAndAllDescendants(
			LocationPaths._Root_Location.getPathInSchema()
				.add(LocationPaths._Root_Location._LocationInformation_CountryTimeZone),
			true,
			new LocationTimeZoneAccessRule(Boolean.TRUE));
		manager.setAccessRuleOnNodeAndAllDescendants(
			LocationPaths._Root_Location.getPathInSchema()
				.add(LocationPaths._Root_Location._LocationInformation_StateProvinceTimeZone),
			true,
			new LocationTimeZoneAccessRule(Boolean.FALSE));

		//Set Permissions for ServedBy and Satellite
		manager.setAccessRuleOnNodeAndAllDescendants(
			LocationPaths._Root_Location.getPathInSchema()
				.add(LocationPaths._Root_Location._LocationInformation_Region),
			true,
			new ServedBySatelliteAccessRule());
		manager.setAccessRuleOnNodeAndAllDescendants(
			LocationPaths._Root_Location.getPathInSchema()
				.add(LocationPaths._Root_Location._LocationInformation_Area),
			true,
			new ServedBySatelliteAccessRule());
		manager.setAccessRuleOnNodeAndAllDescendants(
			LocationPaths._Root_Location.getPathInSchema()
				.add(LocationPaths._Root_Location._LocationInformation_ReservationArea),
			true,
			new ServedBySatelliteAccessRule());

	}
}
