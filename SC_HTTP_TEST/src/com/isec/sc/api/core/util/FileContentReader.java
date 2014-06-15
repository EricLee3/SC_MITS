package com.isec.sc.api.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileContentReader {
	public static String readContent(String fileName) throws FileNotFoundException {
		return readContent(new FileInputStream(new File(fileName)));
	}

	public static String readContent(InputStream inputStream) {
		BufferedReader br = null;
		br = new BufferedReader(new InputStreamReader(inputStream));
		String buf = null;
		StringBuffer stringBuf = new StringBuffer();
		try {
			while ((buf = br.readLine()) != null) {
				stringBuf.append(buf);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return stringBuf.toString();
	}
}
