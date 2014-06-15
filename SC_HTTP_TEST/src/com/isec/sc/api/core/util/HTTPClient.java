package com.isec.sc.api.core.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HTTPClient {
	private String url = null;
	private String method = "GET";
	private String param = null;
	private String inputContent = null;
	private String outputContent = null;
	private boolean redirect = true;
	private HashMap<String, String> inputHeader = new HashMap<String, String>();
	private HashMap<String, String> outputHeader = new HashMap<String, String>();
	private HttpURLConnection connection = null;
	private boolean debug = false; 
	
	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getInputContent() {
		return inputContent;
	}

	public void setInputContent(String inputContent) {
		this.inputContent = inputContent;
	}

	public String getOutputContent() {
		return outputContent;
	}

	public void setOutputContent(String outputContent) {
		this.outputContent = outputContent;
	}

	public HttpURLConnection getConnection() {
		return connection;
	}

	public void setConnection(HttpURLConnection connection) {
		this.connection = connection;
	}

	public Map<String, String> getInputHeader() {
		return inputHeader;
	}

	public void setHeader(String name, String value) {
		inputHeader.put(name, value);
	}

	public String getHeader(String name) {
		return outputHeader.get(name);
	}

	public HTTPClient(String url) {
		this.url = url;
	}
	
	public void invoke() {
		if (url == null) return;
		
		try {
			if (debug) {
				System.out.println("URL:" + url);
				System.out.println("Param:" + param);
				System.out.println("Method:" + method);
				System.out.println("inputContent:" + inputContent);
			}
			
			URL invokeURL = new URL(url + (param != null ? "?" + param : "")); 
			connection = (HttpURLConnection)invokeURL.openConnection();
			
			// method
			connection.setRequestMethod(method);

			// header
			Set<String> keys = inputHeader.keySet();
			for (String key : keys)
				connection.setRequestProperty(key, inputHeader.get(key));
			
			// cache
			connection.setUseCaches(false);
			
			// redirect
			connection.setInstanceFollowRedirects(redirect);
			
			// content in post method
			if (method.equals("POST") && inputContent != null) {
				connection.setRequestProperty("Content-Length", Integer.toString(inputContent.length()));
				connection.setDoInput(true);
				connection.setDoOutput(true);

				DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
				//wr.writeBytes(inputContent);
				wr.writeUTF(inputContent);
				wr.flush();
				wr.close();
			}
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
			StringBuilder builder = new StringBuilder();
			String buf = null;
			
			while ((buf = reader.readLine()) != null)
				builder.append(buf);

			outputContent = builder.toString();
			
			if (debug)
				System.out.println("OutputContent: " + outputContent);
			
			for (int n = 1; ; n++) { 
				String field = connection.getHeaderFieldKey(n);
				if (field == null) break;
				outputHeader.put(field, connection.getHeaderField(field));
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isRedirect() {
		return redirect;
	}

	public void setRedirect(boolean redirect) {
		this.redirect = redirect;
	}

	public void disconnect() {
		connection.disconnect();
	}
}
