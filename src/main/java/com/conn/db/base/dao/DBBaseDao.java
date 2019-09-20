package com.conn.db.base.dao;

import java.lang.reflect.Field;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;

import org.slf4j.Logger;

import com.conn.db.base.annotation.DBColumnAnnotation;
import com.conn.db.base.entity.ProcedureParamBean;
import com.java.util.StringUtil;


public class DBBaseDao {
	protected Logger log;
	//by new Object and use constructor set log
	public DBBaseDao(Logger log) {
		this.log=log;
	}
	//by newInstance() and use setLog() mathod
	public void setLog(Logger log) {
		this.log=log;
	}
	public Logger getLog() {
		return log;
	}
/**********************public method to all use**************************/	
/*************query
 * @throws Exception *************/
	
	public CachedRowSet executeQuery2CachedRowSet(Connection conn,String sql,Object ...objs) throws Exception   {
		//PreparedStatement pst=null;
		CachedRowSet crs=null;
		ResultSet rs=null;
		try(PreparedStatement pst=conn.prepareStatement(sql);) {
			crs=RowSetProvider.newFactory().createCachedRowSet();
			this.setPreparedStatement(pst, objs);
			rs=pst.executeQuery();
			crs.populate(rs);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("sql:{}"+e.getMessage(), sql);
			
			throw e;
		}
		return crs;
	}
	
	public List<List<Object>> executeQuery2ListObj(Connection conn,String sql,Object...objs) throws SQLException{
		List<List<Object>> listObj=new ArrayList<List<Object>>();
		PreparedStatement pst=null;
		ResultSet rs=null;
		ResultSetMetaData rsmd=null;
		try {
			pst=conn.prepareStatement(sql);
			this.setPreparedStatement(pst, objs);
			rs=pst.executeQuery();
			rsmd=rs.getMetaData();
			int count=rsmd.getColumnCount();
			List<Object> list=null;
			while(rs.next()) {
				list=new ArrayList<Object>();
				for(int i=1;i<count;i++) {
					list.add(rs.getObject(i));
				}
				listObj.add(list);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			log.error(e.getMessage());
			throw e;
		}finally {
			try {
			if(rs!=null) {
				rs.close();
			}
			if(pst!=null) {
				pst.close();
			}
			}catch(SQLException e) {
				log.error(e.getMessage());
				throw e;
			}
		}
		return listObj;
	}
	
	public <T>List<T> executeQuery2ListGen(Connection conn,String sql,Class<T> clazz,Object...objs) throws Exception {
		List<T> listEntity=new ArrayList<T>();
		PreparedStatement pst=null;
		ResultSet rs=null;
		ResultSetMetaData rsmd=null;
		try {
			pst=conn.prepareStatement(sql);
			this.setPreparedStatement(pst, objs);
			rs=pst.executeQuery();
			rsmd=rs.getMetaData();
			int count=rsmd.getColumnCount();
			T t=null;
			while(rs.next()) {
				t=clazz.newInstance();
				for(int i=1;i<count;i++) {
					setObjectVal2Entity(clazz, t, rsmd.getColumnName(i), rs, i);
				}
				listEntity.add(t);
			}	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(e.getMessage());
			throw e;
		}finally {
			try {
			if(rs!=null) {
				rs.close();
			}
			if(pst!=null) {
				pst.close();
			}
			}catch(SQLException e) {
				log.error(e.getMessage());
				throw e;
			}
		} 
		
	return listEntity;
	}
	public <T>List<? extends T> executeQuery2ListGen_(Connection conn,String sql,Class<? extends T> clazz,Object...objs) throws Exception {
		List<T> listEntity=new ArrayList<T>();
		PreparedStatement pst=null;
		ResultSet rs=null;
		ResultSetMetaData rsmd=null;
		try {
			pst=conn.prepareStatement(sql);
			this.setPreparedStatement(pst, objs);
			rs=pst.executeQuery();
			rsmd=rs.getMetaData();
			int count=rsmd.getColumnCount();
			T t=null;
			while(rs.next()) {
				t=clazz.newInstance();
				for(int i=1;i<count;i++) {
					setObjectVal2Entity(clazz, t, rsmd.getColumnName(i), rs, i);
				}
				listEntity.add(t);
			}	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(e.getMessage());
			throw e;
		}finally {
			try {
				if(rs!=null) {
					rs.close();
				}
				if(pst!=null) {
					pst.close();
				}
			}catch(SQLException e) {
				log.error(e.getMessage());
				throw e;
			}
		} 
		
		return listEntity;
	}
/*************insert 
 * @throws Exception *************/	

	public boolean executeInsert(Connection conn,String sql,List<Object[]> params,int timeout) throws Exception  {
		boolean res=false;
		//PreparedStatement pst=null;
		int countNum=0;
		try( PreparedStatement pst=conn.prepareStatement(sql)) {			
			if(timeout>0) {
				pst.setQueryTimeout(timeout);
			}
			if(params==null||params.isEmpty()) {
				pst.addBatch();
				pst.executeBatch();
			}else {
				for(Object[] objArr:params) {
					this.setPreparedStatement(pst, objArr);
					pst.addBatch();
					countNum++;
					if(countNum==1000) {
						pst.executeBatch();
						countNum=0;
					}
				}
				pst.executeBatch();
			}
			res=true;
			return res;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(e.getMessage());
			throw e;
		}
	}
	
	public boolean executeInsert(Connection conn,String sql,List<Object[]> params) throws Exception {
		return executeInsert(conn, sql, params, 0);
	}
/************update
 * @throws SQLException ************/
	public boolean executeUpdate(Connection conn,String sql,int timeOut,Object...objs) throws SQLException {
		return executeSql(conn, sql, timeOut, objs);
	}
	public boolean executeUpdate(Connection conn,String sql,Object...objs) throws SQLException {
		return executeSql(conn, sql, 0, objs);
	}
	
/*****************delete
 * @throws SQLException *******************************/	
public boolean executeDelete(Connection conn,String sql,Object...objs) throws SQLException {
	return executeSql(conn, sql, 0, objs);
}



/**********************private method only this class use***********************/	
	
private boolean executeSql(Connection conn,String sql,int timeout,Object...objs) throws SQLException {
		boolean res=false;
		//PreparedStatement pst=null;
	
		try(PreparedStatement pst=conn.prepareStatement(sql)) {
			
			if(timeout>0) {
				pst.setQueryTimeout(timeout);
			}
			this.setPreparedStatement(pst, objs);
			pst.executeUpdate();
			res=true;
			return res;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(e.getMessage());
			throw e;
		}
			
	}
/*
 * call procedure 
 * @param connection -conn
 * @param String -proname
 * @param List<ProcedureParamBean> -blist
 * */
	public List<Object> callProcedure(Connection conn,String proName,List<ProcedureParamBean> blist) throws Exception{
		List<Object> outlist=new ArrayList<>();
		if(blist.isEmpty()) {
			//throw exception and finilize execute
			throw new Exception("paramter list is empty");
		}
		//1.loop Procedure params place
		StringBuffer sb=new StringBuffer("call "+proName+"(");
		int parameterIndex=1;
		for(;parameterIndex <=blist.size();parameterIndex++) {
			sb.append("?,");
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append(")");
		CallableStatement cs=conn.prepareCall(sb.toString());
		//2.loop match place params
		parameterIndex=1;
		for(ProcedureParamBean bean:blist) {
			//input param
			if(bean.isInParam()) {
				switch(bean.getParaType()) {
				case Types.INTEGER:
					cs.setInt(parameterIndex, (Integer) bean.getParamValue());
					break;
				case Types.FLOAT:
					cs.setFloat(parameterIndex, (Float)bean.getParamValue());
					break;
				case Types.DOUBLE:
					cs.setDouble(parameterIndex, (Double)bean.getParamValue());
					break;
				case Types.BIGINT:
					cs.setLong(parameterIndex, (Long)bean.getParamValue());
					break;
				case Types.CHAR:
					
				case Types.VARCHAR:
					cs.setString(parameterIndex, (String)bean.getParamValue());
					break;
				case Types.TIMESTAMP:
					java.sql.Timestamp time=new java.sql.Timestamp(((Date)bean.getParamValue()).getTime());
					cs.setTimestamp(parameterIndex, time);
					break;
				default:
					throw new Exception("Unknown Type of parameter");
				}
			}else {
			//output param
				cs.registerOutParameter(parameterIndex, bean.getParaType());
			}
			parameterIndex++;
		}
		//3.execute procedure
		cs.execute();
		//4.loop get procedure output values
		parameterIndex=1;
		for(ProcedureParamBean bean:blist) {
			if(bean.isInParam()) {
				outlist.add(null);
			}else if(cs.getObject(parameterIndex) instanceof ResultSet) {//if the out result is resultSet
				CachedRowSet css=RowSetProvider.newFactory().createCachedRowSet();
				css.populate(cs.getObject(parameterIndex, ResultSet.class));
				outlist.add(css);
			}else {
				outlist.add(cs.getObject(parameterIndex));
			}
			parameterIndex++;
		}		
		return outlist;		
	}
	
	
	private <T> void setObjectVal2Entity(Class<T> clazz,Object obj,String column,ResultSet rs,int index) throws Exception {
		String methodName=null;
		Class<?> dataType=null;
		Object data=null;
		for(Field field:clazz.getDeclaredFields()) {
			if(field.getAnnotation(DBColumnAnnotation.class).columnName().equalsIgnoreCase(column)) {
				methodName="set"+
						field.getName().substring(0, 1).toUpperCase()+
						field.getName().substring(1);
				if(field.getType().equals(String.class)) {
					data=rs.getString(index);
				}else if(field.getType().equals(Byte.class)) {
					data=rs.getByte(index);
				}else if(field.getType().equals(byte[].class)) {
					data=rs.getBytes(index);
				}else if(field.getType().equals(Integer.TYPE)||field.getType().equals(Integer.class)) {
					data=rs.getInt(index);
				}else if(field.getType().equals(Float.TYPE)||field.getType().equals(Float.class)) {
					data=rs.getFloat(index);
				}else if(field.getType().equals(Double.TYPE)||field.getType().equals(Double.class)) {
					data=rs.getDouble(index);
				}else if(field.getType().equals(java.util.Date.class)) {
					data=rs.getDate(index);
				}else {
					throw new Exception("Unsupport The Type:"+field.getType().toString()+",the DBColumn name:"+column);
				}
				dataType=field.getType();
				break;
			}
		}
		if(StringUtil.isEmpty(methodName)) {
			clazz.getMethod(methodName, dataType).invoke(obj, data);
		}
	}
	
	private void setPreparedStatement(PreparedStatement pst,Object ...objs) throws SQLException {
		if(objs==null||objs.length==0) {
			return;
		}
		int i=0;
		for(Object obj:objs) {
			if(obj instanceof byte[]) {
				pst.setBytes(i++, (byte[]) obj);
			}else if(obj instanceof Integer) {
				pst.setInt(i++, (Integer) obj);
			}else if(obj instanceof String) {
				pst.setString(i++, (String) obj);
			}else if(obj instanceof Float) {
				pst.setFloat(i++, (Float) obj);
			}else if(obj instanceof Long) {
				pst.setLong(i++, (Long) obj);
			}else if(obj instanceof Double) {
				pst.setDouble(i++, (Double) obj);
			}else if(obj instanceof Timestamp) {
				pst.setTimestamp(i++, new Timestamp(((Date)obj).getTime()));
			}
		}
	}
}
