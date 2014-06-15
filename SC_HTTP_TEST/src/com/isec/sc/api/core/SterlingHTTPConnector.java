package com.isec.sc.api.core;

import java.text.MessageFormat;

import com.isec.sc.api.core.util.HTTPClient;

public class SterlingHTTPConnector {
	private String url;
	private String user;
	private String password;
	private String param;
	
	private String api;
	private String data;
	
	public SterlingHTTPConnector() {
		SterlingProperties prop = new SterlingProperties();
		url = prop.getProperty("sterling.http.url");		
		user = prop.getProperty("sterling.user");
		password = prop.getProperty("sterling.password");
		param = prop.getProperty("sterling.http.param");
	}

	public String run() {
		
		System.out.println("========= Input XML Message =========");
		System.out.println(data);
		
		MessageFormat fmt = new MessageFormat(param);
		String newParam = fmt.format(new String[] { user, password, api, data });
		
		HTTPClient client = new HTTPClient(url);
		client.setMethod("POST");
		client.setInputContent(newParam);
		client.setDebug(false);
		client.invoke();
		
		System.out.println("========= Output XML Message =========");
		System.out.println(client.getOutputContent());
		
		return client.getOutputContent();
	}
	
	public String getApi() {
		return api;
	}

	public void setApi(String api) {
		this.api = api;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
