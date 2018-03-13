package com.hertz.mdm._hertz.path;

import com.orchestranetworks.schema.Path;
/**
 * Root paths in this interface: 
 * 	'/'   relativeToRoot: false
 * 
 */
public interface DirectoryPaths
{
	public static final Path _Root = Path.parse("/directory");

	// Table type path
	public final class _Root_Users
	{
		private static final Path _Root_Directory = _Root.add("users");
		public static Path getPathInSchema()
		{
			return _Root_Directory;
		}
		public static final Path _login = Path.parse("./login");
		public static final Path _firstName = Path.parse("./firstName");
		public static final Path _lastName = Path.parse("./lastName");
		public static final Path _email = Path.parse("./email");
		public static final Path _officePhoneNumber = Path.parse("./officePhoneNumber");
		public static final Path _mobilePhoneNumber = Path.parse("./mobilePhoneNumber");
		public static final Path _faxPhoneNumber = Path.parse("./faxNumber");
		public static final Path _comments = Path.parse("./comments");
	}

	// Table type path
	public final class _Root_Roles
	{
		private static final Path _Root_Directory = _Root.add("roles");
		public static Path getPathInSchema()
		{
			return _Root_Directory;
		}
		public static final Path _name = Path.parse("./name");

	}

	// Table type path
	public final class _UsersRoles
	{
		private static final Path _Root_Directory = _Root.add("usersRoles");
		public static Path getPathInSchema()
		{
			return _Root_Directory;
		}
		public static final Path _User = Path.parse("./user");
		public static final Path _Role = Path.parse("./role");
		//public static final Path _firstName = Path.parse("./firstName");
		//public static final Path _lastName = Path.parse("./lastName");
	}
	// ===============================================

}
