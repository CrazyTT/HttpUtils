package com.mhh.httputils.tab.utils.enums;

/**
 * @author 孟辉辉
 * 网络请求参数对象
 */
public class RequestParam {
	/**
	 * 请求参数名称
	 */
	private final String requestKey;
	/**
	 * 请求参数值
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
	 * @return 请求参数名称
	 */
	public String getRequestKey() {
		return requestKey;
	}

	/**
	 * @return  请求参数值
	 */ 
	public String getRequestValue() {
		return requestValue;
	}
}
