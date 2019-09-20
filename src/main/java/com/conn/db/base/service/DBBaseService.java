package com.conn.db.base.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import com.conn.db.base.dao.DBBaseDao;
import com.conn.db.base.dao.DBManager;
import com.conn.db.base.entity.Constants;
import com.conn.db.base.entity.PageEntity;
import com.conn.db.base.entity.ProcedureParamBean;

public class DBBaseService <T extends DBBaseDao>{
	protected Logger log;
	protected T baseDao;
	protected String schema;
	@SuppressWarnings("unchecked")
	public DBBaseService(Logger log) {
		this.log=log;
		baseDao=(T) new DBBaseDao(log);
	}
	public DBBaseService(Logger log,Class<T> clazz) {
		this.log=log;
		try {
			baseDao=clazz.newInstance();
			baseDao.setLog(log);
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.log.error(e.getMessage());
		}
	}
	
	private Connection getConnection2Mysql(String schema) throws SQLException {
		return DBManager.getConnection(schema,"Mysql");
	}
	
	private void closeConnection(Connection conn) {
		if(conn!=null) {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.error(e.getMessage());
			}
		}
	}
/********************开放的数据库服务方法*********************/
/**
 * query by page for Oracle
 * @throws Exception 
 */
	protected <S>List<S> query(String sql,String schema,Class<S> t,PageEntity page, Object...objs) throws Exception {
		Connection conn=getConnection2Mysql(schema);
		int rowCount=getRowCount(conn, sql, objs);
		int pageCount=(int) Math.ceil(rowCount*1d/page.getPageSize());
		page.setTotalCount(rowCount);
		page.setTotalPage(pageCount);
		StringBuffer sbuffer=new StringBuffer("SELECT *FROM (SELECT A.*,ROWNUM ");
		sbuffer.append(Constants.PAGE_ROWNUMBER_NAME);
		sbuffer.append(" FORM(");
		sbuffer.append(sql);
		sbuffer.append(") A WHERE ROWNUM <=");
		sbuffer.append(page.getCurrentPage()*page.getPageSize());
		sbuffer.append(" ) WHERE ");
		sbuffer.append(Constants.PAGE_ROWNUMBER_NAME);
		sbuffer.append(" > ");
		sbuffer.append((page.getCurrentPage()-1)*page.getPageSize());
		if(null!=conn) {
			long before=System.currentTimeMillis();
			try {
				return baseDao.executeQuery2ListGen(conn, sql, t, objs);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.error("query Sql to Result Error:{}\nSQL:{},\nSchema:{},\nParams:{},\n",e.getMessage(),sql,schema,objs);
				throw e;
			}finally {
				long total=System.currentTimeMillis()-before;
				if(total>Constants.SQL_EXECUTE_WARNING_TIME) {
					log.warn("query Sql to Result execute Overtime:{} milliseconds,\nSQL:{},\nSchema:{},\nParams:{},\n",total,sql,schema,objs);
				}
				if(null!=conn) {
					closeConnection(conn);
				}
			}
		}
		return null;
	}
/**
 * query for ResultSet
 * @param Schema --DBName
 * @param sql --query sql
 * @param objs --params of sql
 * @return ResultSet 
 * @throws Exception
 * */	
	protected ResultSet query(String schema,String sql,Object...objs) throws Exception {
		long before=System.currentTimeMillis();
		Connection conn=getConnection2Mysql(schema);
		try {
			if(null!=conn) {			
				ResultSet rs=baseDao.executeQuery2CachedRowSet(conn, sql, objs);
				return rs;
			}else {
				return null;
			}
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("query Sql to Result Error:{}\nSQL:{},\nSchema:{},\nParams:{},\n",e.getMessage(),sql,schema,objs);
			throw e;
		}finally {
			long total=System.currentTimeMillis()-before;
			if(total>Constants.SQL_EXECUTE_WARNING_TIME) {
				log.warn("query Sql to Result execute Overtime:{} milliseconds,\nSQL:{},\nSchema:{},\nParams:{},\n",total,sql,schema,objs);
			}
			if(null!=conn) {
			closeConnection(conn);
			}
		}
	}
	
/**
 *query for list<T>
 *@param schema -DBname
 *@param sql -query sql
 *@param clazz -Class<T>
 *@param object -objs
 *@return list<T>
 * @throws Exception 
 */
	protected <T>List<T> query(String schema,String sql,Class<T> clazz,Object...objs) throws Exception{
		Connection conn=getConnection2Mysql(schema);
		Long before=System.currentTimeMillis();
		try {
			return baseDao.executeQuery2ListGen(conn, sql, clazz, objs);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("query SQL error:{},\nSQL:{},\nschema:{},\nclass:{},\nParams:{}",e.getMessage(),sql,schema,clazz,objs);
			throw e;
		}finally {
			long total=System.currentTimeMillis()-before;
			if(total>Constants.SQL_EXECUTE_WARNING_TIME) {
				log.warn("query sql overTime:{},\nSQL:{},\nschema:{},\nclass:{},\nParams:{}",total,sql,schema,clazz,objs);
			}
			if(null!=conn) {
				closeConnection(conn);
			}
		}
	}
	
/**
 *query for list<Object>
 *@param schema-DBName
 *@param sql-query SQl
 *@param flag-true or false
 *@param Objs-sql params
 *@return List<list<Object>>
 * @throws SQLException 
 */
	protected List<List<Object>> query(String schema,String sql,boolean flag,Object...objs) throws SQLException{
		long before=System.currentTimeMillis();
		Connection conn=getConnection2Mysql(schema);
		try {
			return baseDao.executeQuery2ListObj(conn, sql, objs);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}finally {
			long total=System.currentTimeMillis()-before;
			if(total>Constants.SQL_EXECUTE_WARNING_TIME) {
				log.warn("query sql overTime:{},\nSQL:{},\nschema:{},\nParams:{}",total,sql,schema,objs);	
			}
			if(null!=conn) {
				closeConnection(conn);
			}
		}

		
		
	}
/**
 *insert to single sql 
 *@param schema -DBName
 *@param sql -insert sql
 *@param objs -single params
 *@return boolean -is insert success
 * @throws Exception 
 */	
	protected boolean insert(String schema,String sql,Object...objs) throws Exception {
		boolean res = false;
		List<Object[]> params=new ArrayList<>();
		params.add(objs);
		Connection conn=getConnection2Mysql(schema);
		try {
			res=baseDao.executeInsert(conn, sql, params);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("insert single sql error:{},\nSQL:{},\nParams:{},\nschema{}",e.getMessage(),sql,params,schema);
			throw e;
		}finally {
			if(null!=conn) {
				closeConnection(conn);
			}
		}
		return res;
	}
	
/**
 *insert SQL to List<Object[]> params
 *@param schema -DBName
 *@param sql -insert sql
 *@param params -list<Object[]>params
 *@return boolean -is insert success
 * @throws Exception 
 */
	protected boolean insert(String schema,String sql,List<Object[]> params) throws Exception {
		boolean res=false;
		Connection conn=getConnection2Mysql(schema);
		try {
			res=baseDao.executeInsert(conn, sql, params);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("insert List<Object[]> sql error:{},\nSQL:{},\nParams:{},\nschema:{}",e.getMessage(),sql,params,schema);
			throw e;
		}finally {
			if(null!=conn) {
				closeConnection(conn);
			}
		}
		return res;
	}
/**
 * 
 *insert SQL to List<Object[]> params by timeout controller
 *@param schema -DBName
 *@param sql -insert sql
 *@param params -List<Object[]> param
 *@param timeout -execute insert sql time limit
 *@return boolen -is insert success
 *@throws Exception 
 */	
	protected boolean insert(String schema,String sql,List<Object[]> params,int timeout) throws Exception {
		boolean res=false;
		Connection conn=getConnection2Mysql(schema);
		Long before=System.currentTimeMillis();
		try {
			res=baseDao.executeInsert(conn, sql, params, timeout);					
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("insert List<Object[]> sql error:{},\nSQL:{},\nParams:{},\nschema:{}",e.getMessage(),sql,params,schema);
			throw e;
		}finally{
			long total=System.currentTimeMillis()-before;
			if(total>Constants.SQL_EXECUTE_WARNING_TIME) {
				log.warn("update sql execute overTime:{}milliseconds,\nsql:{},\nschema:{},\nobjs:{},\ntimeOut:{}",total,sql,schema,params,timeout);	
			}
			if(null!=conn) {
				closeConnection(conn);
			}	
		}
		return res;
	}
	
/**
 *update sql
 *@param schema -DBName
 *@param sql -update sql
 *@param objs -Object[] params
 *@return boolean -is update success
 * @throws SQLException 
 * 
 */
	protected boolean update(String schema,String sql, Object...objs) throws SQLException {
		boolean res=false;
		Connection conn=getConnection2Mysql(schema);
		try {
			res=baseDao.executeUpdate(conn, sql, objs);
					
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("update sql error:{},\nschema:{},\nsql:{},\nobjs:{}",e.getMessage(),schema,sql,objs);
			throw e;
		}finally {
			if(null!=conn) {
				closeConnection(conn);
			}
		}
		return res;
	}
	
/**
 *update sql for timeout limit
 * @throws SQLException 
 *
 */
	protected boolean update(String schema,String sql,int timeOut,Object...objs) throws SQLException {
		boolean res=false;
		Connection conn=getConnection2Mysql(schema);
		long before=System.currentTimeMillis();
		try {
			res=baseDao.executeUpdate(conn, sql, timeOut, objs);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("update sql error:{},\nschema:{},\nsql:{},\nobjs:{}",e.getMessage(),schema,sql,objs);
			throw e;
		}finally {
			long total=System.currentTimeMillis()-before;
			if(total>Constants.SQL_EXECUTE_WARNING_TIME) {
				log.warn("update sql execute overTime:{}milliseconds,\nsql:{},\nschema:{},\nobjs:{},\ntimeOut:{}",total,sql,schema,objs,timeOut);
			}
			if(null!=conn) {
				closeConnection(conn);
			}
		}
		return res;
	}
	
/**
 *delete sql 
 *@param schema -DBName
 *@param sql -delete sql
 *@param objs -sql params
 *@return boolean-is delete success
 * @throws SQLException 
 */
	protected boolean delete(String schema,String sql,Object...objs) throws SQLException {
		boolean res=false;
		Connection conn=getConnection2Mysql(schema);
		try {
			res=baseDao.executeDelete(conn, sql, objs);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("delete sql error:{},\nschema:{},\nsql:{},\nobjs:{}",e.getMessage(),schema,sql,objs);			
			throw e;
		}finally {
			if(null!=conn) {
				closeConnection(conn);
			}
		}
		return res;
	}
	
/**
 *execute procedure for sql
 * @throws Exception 
 */
	protected List<Object> procedure(String schema,String prcName,List<ProcedureParamBean> blist) throws Exception{
		Connection conn=getConnection2Mysql(schema);
		try {
			return baseDao.callProcedure(conn, prcName, blist);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("execute procedure error:{},\nschema:{},\nprocedureName:{},\nListParams:{}",e.getMessage(),schema,prcName,blist);
			throw e;
		}finally {
			if(null!=conn) {
				closeConnection(conn);
			}
		}
		
	}
	
/**
 *get single sql resultset rowcount
 *@param conn -same connction for inner
 *@param sql -query sql
 *@param objs -query sql params
 *@return int -all rowCount
 *@throws SQLException
 */	
	protected int getRowCount(Connection conn,String sql,Object... objs) throws SQLException {
		int rowCount=0;
		sql="SELECT COUNT(0) FORM ("+sql+")";
		if(null!=conn) {
			try {
			rowCount=((BigDecimal)baseDao.executeQuery2ListObj(conn, sql, objs).get(0).get(0)).intValue();
			}catch(SQLException e){
				log.error("Query RowCount error:"+e.getMessage());
				throw e;
			}
		}
		return rowCount;
	}	
	
}
