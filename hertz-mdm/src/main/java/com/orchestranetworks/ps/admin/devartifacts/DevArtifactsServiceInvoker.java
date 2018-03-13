/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.orchestranetworks.ps.admin.devartifacts;

import java.io.*;
import java.net.*;

/**
 * A class for invoking the Dev Artifacts Service outside the context of the EBX UI
 */
public class DevArtifactsServiceInvoker
{
	private String servletURL;
	private String login;
	private String password;
	private boolean copyEnvironment;
	private boolean replaceMode;
	private boolean skipNonExistingFiles;
	private String[] workflowModels = new String[0];

	/**
	 * Construct the invoker
	 * 
	 * @param servletURL the url of the Dev Artifacts Service servlet
	 * @param login the username to log in as
	 * @param password the password
	 */
	public DevArtifactsServiceInvoker(String servletURL, String login, String password)
	{
		this.servletURL = servletURL;
		this.login = login;
		this.password = password;
	}

	public boolean isCopyEnvironment()
	{
		return this.copyEnvironment;
	}

	public void setCopyEnvironment(boolean copyEnvironment)
	{
		this.copyEnvironment = copyEnvironment;
	}

	public boolean isReplaceMode()
	{
		return this.replaceMode;
	}

	public void setReplaceMode(boolean replaceMode)
	{
		this.replaceMode = replaceMode;
	}

	public boolean isSkipNonExistingFiles()
	{
		return this.skipNonExistingFiles;
	}

	public void setSkipNonExistingFiles(boolean skipNonExistingFiles)
	{
		this.skipNonExistingFiles = skipNonExistingFiles;
	}

	public String[] getWorkflowModels()
	{
		return this.workflowModels;
	}

	public void setWorkflowModels(String[] workflowModels)
	{
		this.workflowModels = workflowModels;
	}

	/**
	 * Execute the servlet
	 * 
	 * @return the input stream from the servlet connection
	 * @throws IOException if an exception occurred
	 */
	public InputStream execute() throws IOException
	{
		// Construct the url to the servlet
		String urlStr = servletURL + "?";
		if (copyEnvironment)
		{
			urlStr += ("&" + DevArtifactsService.PARAM_ENVIRONMENT_COPY + "=true");
		}
		if (replaceMode)
		{
			urlStr += ("&" + ImportDevArtifactsService.PARAM_REPLACE_MODE + "=true");
		}
		if (skipNonExistingFiles)
		{
			urlStr += ("&" + ImportDevArtifactsService.PARAM_SKIP_NONEXISTING_FILES + "=true");
		}

		for (String workflowModel : workflowModels)
		{
			urlStr += ("&" + ImportDevArtifactsService.PARAM_WORKFLOW_PREFIX + workflowModel + "=true");
		}
		// Open a connection to the servlet and return its input stream
		URL url = new URL(urlStr);
		URLConnection conn = url.openConnection();
		conn.setDoOutput(true);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
		writer.write(login + " " + password);
		writer.newLine();
		writer.flush();
		writer.close();
		return conn.getInputStream();
	}
}
