package com.hertz.mdm.admin.path;

import com.orchestranetworks.schema.Path;
/**
 * Generated by EBX5 5.7.1 fix B [1034:0005], at  2017/10/27 09:30:03 [EDT].
 * WARNING: Any manual changes to this class may be overwritten by generation process.
 * DO NOT MODIFY THIS CLASS.
 * 
 * This interface defines constants related to schema [Module: hertz-mdm, path: /WEB-INF/ebx/schemas/AdminDataModel.xsd].
 * 
 * Root paths in this interface: 
 * 	'/'   relativeToRoot: false
 * 
 */
public interface AdminPaths
{
	// ===============================================
	// Constants for nodes under '/'.
	// Prefix:  ''.
	// Statistics:
	//		69 path constants.
	//		14 leaf nodes.
	public static final Path _Root = Path.parse("/root");

	// Table type path
	public final class _Root_ProjectType {
		private static final Path _Root_ProjectType = _Root.add("ProjectType");
		public static Path getPathInSchema()
		{
			return _Root_ProjectType;
		}
	public static final Path _Domain = Path.parse("./domain");
	public static final Path _ProjectType = Path.parse("./projectType");
	} 

	// Table type path
	public final class _Root_CommonProjectRole {
		private static final Path _Root_CommonProjectRole = _Root.add("CommonProjectRole");
		public static Path getPathInSchema()
		{
			return _Root_CommonProjectRole;
		}
	public static final Path _ProjectType = Path.parse("./projectType");
	public static final Path _Roles = Path.parse("./roles");
	} 

	// Table type path
	public final class _Root_LocationProjectRole {
		private static final Path _Root_LocationProjectRole = _Root.add("LocationProjectRole");
		public static Path getPathInSchema()
		{
			return _Root_LocationProjectRole;
		}
	public static final Path _ProjectType = Path.parse("./projectType");
	public static final Path _Roles = Path.parse("./roles");
	} 

	// Table type path
	public final class _Root_VehicleProjectRole {
		private static final Path _Root_VehicleProjectRole = _Root.add("VehicleProjectRole");
		public static Path getPathInSchema()
		{
			return _Root_VehicleProjectRole;
		}
	public static final Path _ProjectType = Path.parse("./projectType");
	public static final Path _Roles = Path.parse("./roles");
	} 

	// Table type path
	public final class _Root_WorkflowRole {
		private static final Path _Root_WorkflowRole = _Root.add("WorkflowRole");
		public static Path getPathInSchema()
		{
			return _Root_WorkflowRole;
		}
	public static final Path _WorkflowTask = Path.parse("./workflowTask");
	public static final Path __domain = Path.parse("./_domain");
	public static final Path _Role = Path.parse("./role");
	} 

	// Table type path
	public final class _Root_WorkflowTask {
		private static final Path _Root_WorkflowTask = _Root.add("WorkflowTask");
		public static Path getPathInSchema()
		{
			return _Root_WorkflowTask;
		}
	public static final Path _Domain = Path.parse("./domain");
	public static final Path _Id = Path.parse("./id");
	public static final Path _WorkflowRoles = Path.parse("./workflowRoles");
	} 
	// ===============================================

}
