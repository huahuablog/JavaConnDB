package com.java.util;

import java.io.*;
import java.net.URL;
import java.util.Properties;

public class IOUtil {
	
	public static boolean hasKey2Prop(String key,String path)  {
		BufferedReader reader=null;
		String line;
		try {
			URL url=IOUtil.class.getClassLoader().getResource(path);

			reader=new BufferedReader(new FileReader(url.getFile()));

			while((line=reader.readLine())!=null) {
				System.out.println("读取文件的行字符："+line);
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
			URL url=IOUtil.class.getClassLoader().getResource(path);
			prop.load(new FileInputStream(new File(url.getFile())));
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
