package com.mhh.httputils.tab.utils.listeners;

/**
 * @author 孟辉辉
 * 网络请求监听
 */
public interface RequestListener {
	
	/**
	 * 网络请求成功时回调
	 * @param result 网络请求的结果信息，如：json
	 */
	public abstract void requestSuccess(String result);
	/**
	 * 网络请求失败时会调用
	 * @param responseStatus 失败状态。如：ConnectionStatus.SessionTimeOut（Session超时），ConnectionStatus.ConnectionException（连接异常）
	 * @param e 具体的异常对象
	 */
	public abstract void requestException(int responseStatus, Exception e);
}
