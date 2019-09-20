package com.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;

import com.conn.db.base.dao.DBBaseDao;

/*
 * 测试类
 * */
public class Test  <T extends DBBaseDao>  {
	public Test(Logger log) {
		super();
		// TODO Auto-generated constructor stub
	}
	T tt2;

public  void querySql(String sql) throws SQLException {
	
}
}
