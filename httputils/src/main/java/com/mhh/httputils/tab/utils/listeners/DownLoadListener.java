package com.mhh.httputils.tab.utils.listeners;

/**
 * @author 孟辉辉
 * 网络请求监听
 */
public interface DownLoadListener {
	
	/**
	 * 下载成功成功时回调
	 * @param dir 文件下载的路径
	 * @param fileName 文件名字
	 */
	public abstract void downLoadSuccess(String dir, String fileName);
	/**
	 * 下载失败时会调用
	 * @param e 具体的异常对象
	 */
	public abstract void downLoadException(Exception e);
}
