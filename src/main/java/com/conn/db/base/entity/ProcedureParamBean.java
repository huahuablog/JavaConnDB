package com.conn.db.base.entity;

public class ProcedureParamBean {
	//Types.INTEGER,Types.FLOAT,Types.BIGINT,Types.DOUBLE,Types.VARCHAR,Types.TIMESTAMP	
	private int paraType;
	//input param，default is true
	private boolean inParam=true;
	//input param to value
	private Object paramValue;
	
	
	//set/get mathod
	public int getParaType() {
		return paraType;
	}
	public void setParaType(int paraType) {
		this.paraType = paraType;
	}
	public boolean isInParam() {
		return inParam;
	}
	public void setInParam(boolean inParam) {
		this.inParam = inParam;
	}
	public Object getParamValue() {
		return paramValue;
	}
	public void setParamValue(Object paramValue) {
		this.paramValue = paramValue;
	}
}
