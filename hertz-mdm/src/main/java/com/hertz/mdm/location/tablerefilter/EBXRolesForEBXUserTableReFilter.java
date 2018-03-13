package com.hertz.mdm.location.tablerefilter;

import java.util.Locale;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.schema.InvalidSchemaException;
import com.orchestranetworks.schema.TableRefFilter;
import com.orchestranetworks.schema.TableRefFilterContext;
import com.orchestranetworks.service.directory.DirectoryPaths;

public class EBXRolesForEBXUserTableReFilter implements TableRefFilter
{
	@Override
	public boolean accept(Adaptation record, ValueContext context)
	{
		Adaptation ebxUsersRoles = record;

		if (ebxUsersRoles.get(DirectoryPaths._Directory_UsersRoles._User).equals("LOCNCBO"))
		{
			return true;
		}
		return false;
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
