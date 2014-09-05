package com.isec.sc.intgr.api.delegate;

import java.net.URLEncoder;
import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Component;

import com.isec.sc.intgr.api.util.HTTPClient;


@Component
public class SterlingHTTPConnector {
	private String url;
	private String user;
	private String password;
	private String param;
	
	private String api;
	private String data;
	
	private static final Logger logger = LoggerFactory.getLogger(SterlingHTTPConnector.class);
	
	public SterlingHTTPConnector(String url, String user, String password, String param) {
		
		
		this.url = url;
		this.user = user;
		this.password = password;
		this.param = param;
		
		/*
		SterlingProperties prop = new SterlingProperties();
		url = prop.getProperty("sterling.http.url");		
		user = prop.getProperty("sterling.user");
		password = prop.getProperty("sterling.password");
		param = prop.getProperty("sterling.http.param");
		*/
	}

	public String run() throws Exception{
		
		
		MessageFormat fmt = new MessageFormat(param);
		String newParam = fmt.format(new String[] { user, password, api, URLEncoder.encode(data, "UTF-8") });
		
		// newParam = URLEncoder.encode(newParam, "UTF-8");
		logger.debug("[SC API Input Param]"+newParam);
		
		HTTPClient client = new HTTPClient(url);
		client.setMethod("POST");
		client.setInputContent(newParam);
		client.setDebug(true);
		client.invoke();
		
		
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
