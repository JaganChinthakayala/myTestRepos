/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.admin.devartifacts;

import java.util.*;

import com.orchestranetworks.instance.*;
import com.orchestranetworks.service.*;

/**
 * A factory for <code>DevArtifactConfig</code>s.
 */
public interface DevArtifactsConfigFactory
{
	/**
	 * Create the configuration
	 * 
	 * @param repo the repository
	 * @param session the user's session
	 * @param paramMap the map containing the http parameters.
	 *                 See {@link javax.servlet.ServletRequest#getParameterMap()}.
	 * @return the configuration
	 */
	DevArtifactsConfig createConfig(Repository repo, Session session, Map<String, String[]> paramMap)
		throws OperationException;
}
