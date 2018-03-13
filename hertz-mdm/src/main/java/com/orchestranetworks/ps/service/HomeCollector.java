/*
 * Copyright (c) Orchestra Networks 2000-2006. All rights reserved.
 */
package com.orchestranetworks.ps.service;

import java.util.*;

import com.onwbp.adaptation.*;
import com.orchestranetworks.instance.*;
import com.orchestranetworks.service.*;

public class HomeCollector
{
	private final boolean includeBranch;
	private final boolean includeVersion;

	private List<AdaptationHome> homes = new ArrayList<AdaptationHome>();

	public HomeCollector(final boolean includeBranch, final boolean includeVersion)
	{
		super();
		this.includeBranch = includeBranch;
		this.includeVersion = includeVersion;
	}

	public static List<AdaptationHome> getAllHomes(
		Repository aRepository,
		boolean includeBranch,
		boolean includeVersion,
		Session aSession)
	{
		HomeCollector collector = new HomeCollector(includeBranch, includeVersion);
		collector.collectHomes(aRepository.getReferenceBranch(), aSession);
		// Collections.sort(collector.homes, ComparatorToDefine);
		return collector.homes;
	}

	private void collectHomes(AdaptationHome aHome, Session aSession)
	{
		if (aHome == null)
			return;

		if (aHome.isOpenBranch())
		{
			if (this.includeBranch)
				this.homes.add(aHome);
			if (this.includeVersion && !aHome.isBranchReference())
				this.homes.add(aHome.getParent());
		}

		if (aHome.isOpenVersion() && !aHome.isInitialVersion())
		{
			if (this.includeVersion)
				this.homes.add(aHome);
		}

		if (this.includeBranch)
			for (@SuppressWarnings("unchecked")
			Iterator<AdaptationHome> it = aHome.getBranchChildren().iterator(); it.hasNext();)
			{
				AdaptationHome child = it.next();
				this.collectHomes(child, aSession);
			}

		if (this.includeVersion)
			for (@SuppressWarnings("unchecked")
			Iterator<AdaptationHome> it = aHome.getVersionChildren().iterator(); it.hasNext();)
			{
				AdaptationHome child = it.next();
				this.collectHomes(child, aSession);
			}
	}
}
