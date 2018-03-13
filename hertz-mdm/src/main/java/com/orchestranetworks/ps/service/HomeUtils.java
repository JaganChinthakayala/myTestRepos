/*
 * Copyright (c) Orchestra Networks 2000-2006. All rights reserved.
 */
package com.orchestranetworks.ps.service;

import java.util.*;

import com.onwbp.adaptation.*;

public class HomeUtils
{
	public static String formatHomeLabel(AdaptationHome aHome, Locale aLocale)
	{
		StringBuffer buffer = new StringBuffer();
		int depth = HomeUtils.getHomeDepth(aHome);

		for (int i = 0; i < depth * 3; i++)
		{
			buffer.append("&nbsp;");
		}

		if (aHome.isInitialVersion())
		{
			for (int i = 0; i < 3; i++)
			{
				buffer.append("&nbsp;");
			}

			buffer.append(
				((AdaptationHome) aHome.getBranchChildren().get(0)).getLabelOrName(aLocale))
				.append("&nbsp;&lt;initial version&gt;");
		}
		else
		{
			if (aHome.isVersion())
				buffer.append("(V)");
			buffer.append(aHome.getLabelOrName(aLocale));
		}
		return buffer.toString();
	}

	public static int getHomeDepth(AdaptationHome aHome)
	{

		AdaptationHome parentBranch = aHome.getParentBranch();

		if (parentBranch == null)
			return 0;

		return getHomeDepth(parentBranch) + 1;
	}
}
