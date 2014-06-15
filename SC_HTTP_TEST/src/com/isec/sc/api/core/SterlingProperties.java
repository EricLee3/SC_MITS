package com.isec.sc.api.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class SterlingProperties extends Properties {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String propFile = "config.properties";
	
	public SterlingProperties() {
		try {
			this.load(getClass().getResourceAsStream(propFile));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
