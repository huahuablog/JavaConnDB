package com.java.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class IOUtil {
	
	public static boolean hasKey2Prop(String key,String path)  {
		BufferedReader reader=null;
		String line;
		try {
			reader=new BufferedReader(new FileReader(path));
			while((line=reader.readLine())!=null) {
				if(line.startsWith("#")) {
					continue;
				}
				if(line.toUpperCase().contains(key.toUpperCase())) {
					return true;
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(reader!=null) {
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	
	
	
	public static String getPropValue(String key,String path) {
		Properties prop=new Properties();
		try {
			//InputStream input=new FileInputStream(path);
			//prop.load(new InputStreamReader(input, "utf-8"));
			prop.load(new FileInputStream(new File(path)));
			return prop.getProperty(key);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			//关流
		}
		return null;
	}
}
