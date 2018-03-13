/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.workflow;

import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.onwbp.adaptation.*;
import com.orchestranetworks.instance.*;
import com.orchestranetworks.service.*;
import com.orchestranetworks.workflow.*;

/**
 * This is a class that encapsulates the basic information needed by the {@link WorkflowLauncher} that normally could be retrieved
 * from a {@link ServiceContext} or its {@link HttpServletRequest}, but this allows the launcher to be used from components other than services.
 */
public class WorkflowLauncherContext
{
	private Session session;
	private Repository repository;
	private ServiceContext serviceContext;

	private AdaptationHome currentDataSpace;
	private Adaptation currentAdaptation;

	private Map<String, String[]> parameterMap = new HashMap<String, String[]>();;

	public WorkflowLauncherContext(
		Session session,
		Repository repository,
		AdaptationHome currentDataSpace,
		Adaptation currentAdaptation)
	{
		this.session = session;
		this.repository = repository;
		this.currentDataSpace = currentDataSpace;
		this.currentAdaptation = currentAdaptation;
	}

	public WorkflowLauncherContext(HttpServletRequest request)
	{
		this(ServiceContext.getServiceContext(request));
		// Copy over all request params (can't use the request's map itself because it's immutable)
		Map<String, String[]> requestParamMap = request.getParameterMap();
		parameterMap.putAll(requestParamMap);
	}

	public WorkflowLauncherContext(ServiceContext serviceContext)
	{
		this.serviceContext = serviceContext;
		this.session = serviceContext.getSession();
		this.currentDataSpace = serviceContext.getCurrentHome();
		this.currentAdaptation = serviceContext.getCurrentAdaptation();
	}

	public WorkflowEngine createWorkflowEngine()
	{
		if (serviceContext == null)
		{
			return WorkflowEngine.getFromRepository(repository, session);
		}
		return WorkflowEngine.getFromServiceContext(serviceContext);
	}

	public Session getSession()
	{
		return this.session;
	}

	public ServiceContext getServiceContext()
	{
		return this.serviceContext;
	}

	public AdaptationHome getCurrentDataSpace()
	{
		return this.currentDataSpace;
	}

	public void setCurrentDataSpace(AdaptationHome currentDataSpace)
	{
		this.currentDataSpace = currentDataSpace;
	}

	public Adaptation getCurrentAdaptation()
	{
		return this.currentAdaptation;
	}

	public void setCurrentAdaptation(Adaptation currentAdaptation)
	{
		this.currentAdaptation = currentAdaptation;
	}

	/**
	 * Get the parameter map. Similar to {@link ServletRequest#getParameterMap()}.
	 * 
	 * @return the parameter map
	 */
	public Map<String, String[]> getParameterMap()
	{
		return this.parameterMap;
	}

	/**
	 * Set the parameter map
	 * 
	 * param parameterMap the parameter map
	 */
	public void setParameterMap(Map<String, String[]> parameterMap)
	{
		this.parameterMap = parameterMap;
	}

	/**
	 * Get whether a parameter name is defined.
	 * 
	 * @param parameterName the parameter name
	 * @return whether it's defined
	 */
	public boolean isParameterNameDefined(String parameterName)
	{
		return parameterMap.keySet().contains(parameterName);
	}

	/**
	 * Get the parameter names. Similar to {@link ServletRequest#getParameterNames()}.
	 * 
	 * @return the parameter names
	 */
	public Enumeration<String> getParameterNames()
	{
		return Collections.enumeration(parameterMap.keySet());
	}

	/**
	 * Get the parameter value for the given parameter name, assuming it has only one value. Similar to {@link ServletRequest#getParameter(String)}.
	 * 
	 * @return the parameter value
	 */
	public String getParameter(String name)
	{
		String[] value = getParameterValues(name);
		return value == null ? null : value[0];
	}

	/**
	 * Set the parameter value for the given parameter name, assuming it has only one value.
	 * 
	 * @param name the parameter name
	 * @param value the parameter value
	 */
	public void setParameter(String name, String value)
	{
		String[] values;
		if (value == null)
		{
			values = null;
		}
		else
		{
			values = new String[1];
			values[0] = value;
		}
		setParameterValues(name, values);
	}

	/**
	 * Get the parameter values for the given parameter name. Similar to {@link ServletRequest#getParameterValues(String)}.
	 * 
	 * @return the parameter values
	 */
	public String[] getParameterValues(String name)
	{
		return parameterMap.get(name);
	}

	/**
	 * Set the parameter values for the given parameter name.
	 * 
	 * @param name the parameter name
	 * @param values the parameter values
	 */
	public void setParameterValues(String name, String[] values)
	{
		parameterMap.put(name, values);
	}
}
