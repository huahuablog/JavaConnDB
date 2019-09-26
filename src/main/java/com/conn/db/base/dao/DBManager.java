package com.conn.db.base.dao;

import com.conn.db.base.entity.Constants;
import com.java.util.IOUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;


public class DBManager {
	private static Logger log=LoggerFactory.getLogger(DBManager.class);
	private static String userName=null;
	private static String passWord=null;
	private static String jdbcUrl=null;
	
	private static HikariConfig getConfig(String schema,String type)  {
		if(type.equals("Oracle")) {
			//oracle config
		}else {//default config
			userName=IOUtil.getPropValue("test.dataSource.user", Constants.DBCONFIG);
			passWord=IOUtil.getPropValue("test.dataSource.password", Constants.DBCONFIG);
			jdbcUrl=IOUtil.getPropValue("test.jdbcUrl", Constants.DBCONFIG);
		}
		HikariConfig config=new HikariConfig();
		config.setJdbcUrl(jdbcUrl);
		config.setUsername(userName);
		config.setPassword(passWord);
		config.addDataSourceProperty("prepStmtCacheSize", IOUtil.getPropValue("test.dataSource.prepStmtCacheSize", Constants.DBCONFIG)); //连接池大小默认25，官方推荐250-500
		config.addDataSourceProperty("prepStmtCacheSqlLimit", IOUtil.getPropValue("test.dataSource.prepStmtCacheSqlLimit", Constants.DBCONFIG)); //单条语句最大长度默认256，官方推荐2048
        config.addDataSourceProperty("cachePrepStmts", "true"); //是否自定义配置，为true时下面两个参数才生效
        //config.addDataSourceProperty("useServerPrepStmts", "true"); //新版本MySQL支持服务器端准备，开启能够得到显著性能提升
        //config.addDataSourceProperty("useLocalSessionState", "true");
        //config.addDataSourceProperty("useLocalTransactionState", "true");
        //config.addDataSourceProperty("rewriteBatchedStatements", "true");
        //config.addDataSourceProperty("cacheResultSetMetadata", "true");
        //config.addDataSourceProperty("cacheServerConfiguration", "true");
        //config.addDataSourceProperty("elideSetAutoCommits", "true");
        //config.addDataSourceProperty("maintainTimeStats", "false");
        config.setPoolName("Pool-"+schema);
        config.setLeakDetectionThreshold(5000);
        config.setIdleTimeout(30000);
        config.setConnectionTimeout(50000);
        config.setMaxLifetime(120000);
        return config;		
	}	
	private static Connection getConnection(String schema,String type,String path) throws  SQLException {
		HikariDataSource ds=null;
		if(Constants.Data_Source_Map.containsKey(schema)) {//exist
			ds=Constants.Data_Source_Map.get(schema);
		}else {
			if(!IOUtil.hasKey2Prop(schema+".jdbcUrl", path)) {
				//not exist
				log.error("key:{} is not exist in Properties",schema);
				return null;
			}
			HikariConfig config=getConfig(schema, type);
			ds=new HikariDataSource(config);
			Constants.Data_Source_Map.put(schema, ds);
		}
		return ds.getConnection();
	}

	public static Connection getConnection(String schema,String type) throws SQLException {
		if(type.equals("Oracle")) {
			return null;
		}else {
			//System.out.println(DBManager.class.getClassLoader().getResource(Constants.getConfig()));
			return getConnection(schema, "Mysql", Constants.getDBConfig());
		}
	}

}
