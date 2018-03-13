/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.admin.cleanworkflows;

import java.util.*;

import com.onwbp.adaptation.*;
import com.orchestranetworks.workflow.*;

/**
 * Configuration for performing <code>CleanWorkflowsService</code>.
 * Specify the data space, the child data spaces to skip and the publications.
 * @see CleanWorkflowsService
 */
public class CleanWorkflowsConfig
{
	private AdaptationHome masterDataSpace;
	private List<AdaptationHome> childDataSpacesToSkip;
	private List<PublishedProcess> workflowPublications;

	public AdaptationHome getMasterDataSpace()
	{
		return this.masterDataSpace;
	}

	public void setMasterDataSpace(AdaptationHome masterDataSpace)
	{
		this.masterDataSpace = masterDataSpace;
	}

	public List<AdaptationHome> getChildDataSpacesToSkip()
	{
		return this.childDataSpacesToSkip;
	}

	public void setChildDataSpacesToSkip(List<AdaptationHome> childDataSpacesToSkip)
	{
		this.childDataSpacesToSkip = childDataSpacesToSkip;
	}

	public List<PublishedProcess> getWorkflowPublications()
	{
		return this.workflowPublications;
	}

	public void setWorkflowPublications(List<PublishedProcess> workflowPublications)
	{
		this.workflowPublications = workflowPublications;
	}
}
