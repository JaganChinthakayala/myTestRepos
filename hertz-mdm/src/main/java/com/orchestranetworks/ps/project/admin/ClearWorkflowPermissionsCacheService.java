/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.project.admin;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.orchestranetworks.ps.project.actionpermissions.*;
import com.orchestranetworks.service.*;
import com.orchestranetworks.ui.*;

/**
 * A service that clears the cache of workflow permissions.
 */
@SuppressWarnings("serial")
public class ClearWorkflowPermissionsCacheService extends HttpServlet
{
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		ServiceContext sContext = ServiceContext.getServiceContext(request);
		Session session = sContext.getSession();

		// Make sure only admins can execute
		if (!session.isUserInRole(Role.ADMINISTRATOR))
		{
			throw new ServletException("User doesn't have permission to execute service.");
		}

		ProjectWorkflowPermissionsCache.getInstance().clearAllCache();

		UIComponentWriter writer = sContext.getUIComponentWriter();
		writer.addJS("alert('Service complete.');");
		writer.addJS("window.location.href='" + sContext.getURLForEndingService() + "';");
	}
}
