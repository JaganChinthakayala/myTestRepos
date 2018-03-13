package com.hertz.mdm._hertz.util;

import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import com.orchestranetworks.service.OperationException;

public class CallAPI
{

	public static void post(String urlString, Map<String, String> headers, String payload)
		throws Exception
	{
		try
		{
			URL url = new URL(urlString);
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();

			urlConn.setRequestMethod("POST");

			for (Map.Entry<String, String> header : headers.entrySet())
			{
				urlConn.setRequestProperty(header.getKey(), header.getValue().toString());
			}

			urlConn.setDoOutput(true);
			urlConn.setAllowUserInteraction(false);

			PrintStream ps = new PrintStream(urlConn.getOutputStream());
			ps.print(payload);
			ps.close();

			Integer responseCode = urlConn.getResponseCode();
			if (responseCode != 200)
				throw OperationException.createError(" Error:" + urlConn.getResponseMessage());
		}
		catch (Exception e)
		{
			throw OperationException.createError(e.toString());
		}
	}
}