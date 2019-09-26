package com.conn.db.base.entity;

import com.zaxxer.hikari.HikariDataSource;

import java.util.HashMap;
import java.util.Map;

public  class Constants {
	public final static String ENCODE="UTF-8";
	public final static String DBCONFIG="globalConfig/db.properties";
	public static Map<String, HikariDataSource> Data_Source_Map=new HashMap<String, HikariDataSource>();
	public final static Integer SQL_EXECUTE_WARNING_TIME=500; 
	public final static String PAGE_ROWNUMBER_NAME="RN";
	public final static String CONFIG="globalConfig/config.properties";

	public static String getDBConfig(){
		return Constants.DBCONFIG;
	}

}
