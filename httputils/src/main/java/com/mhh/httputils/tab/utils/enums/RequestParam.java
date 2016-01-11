package com.mhh.httputils.tab.utils.enums;

/**
 * @author �ϻԻ�
 * ���������������
 */
public class RequestParam {
	/**
	 * �����������
	 */
	private final String requestKey;
	/**
	 * �������ֵ
	 */
	private final String requestValue;

	public RequestParam(String requestKey, String requestValue){
		this.requestKey = requestKey;
		this.requestValue = requestValue;
	}
	
	public RequestParam(String requestKey, int requestValue){
		this.requestKey = requestKey;
		this.requestValue = requestValue+"";
	}
	
	public RequestParam(String requestKey, float requestValue){
		this.requestKey = requestKey;
		this.requestValue = requestValue+"";
	}
	
	public RequestParam(String requestKey, double requestValue){
		this.requestKey = requestKey;
		this.requestValue = requestValue+"";
	}
	
	public RequestParam(String requestKey, boolean requestValue){
		this.requestKey = requestKey;
		this.requestValue = requestValue+"";
	}
	
	public RequestParam(String requestKey, Object requestValue){
		this.requestKey = requestKey;
		this.requestValue = requestValue.toString();
	}

	/**
	 * @return �����������
	 */
	public String getRequestKey() {
		return requestKey;
	}

	/**
	 * @return  �������ֵ
	 */ 
	public String getRequestValue() {
		return requestValue;
	}
}
