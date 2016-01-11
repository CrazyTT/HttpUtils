package com.mhh.httputils.tab.utils.bean;

import java.io.Serializable;

public class RulePair implements Serializable{
	private static final long serialVersionUID = 1L;

	private final String whereName;
	private final Object whereValue;
	
	public RulePair(String whereName, Object whereValue){
		this.whereName = whereName;
		this.whereValue = whereValue;
	}

	public String getWhereName() {
		return whereName;
	}

	public Object getWhereValue() {
		return whereValue;
	}
		
}
