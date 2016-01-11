package com.mhh.httputils.tab.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mhh.httputils.tab.utils.enums.RequestParam;
import com.mhh.httputils.tab.utils.enums.RequestType;
import com.mhh.httputils.tab.utils.exception.NetException;
import com.mhh.httputils.tab.utils.handlers.FileDownHandler;
import com.mhh.httputils.tab.utils.handlers.FileUploadHandler;
import com.mhh.httputils.tab.utils.listeners.FileDownLoadListener;
import com.mhh.httputils.tab.utils.listeners.FileUploadListener;
import com.mhh.httputils.tab.utils.listeners.RequestListener;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * @author 孟辉辉
 * 网络请求工具类
 */
public class HttpUtil {

	private static HttpManager httpManager = HttpManager.getInstance();
	
	/**
	 * get请求方式请求数据
	 * @param url 请求的URL
	 * @param requestListener 请求的返回监听
	 */
	public static void executeGet(String url, RequestListener requestListener) {
		httpManager.startRequest(RequestType.GET, url, null, requestListener);
	}
	
	/**
	 * get请求方式请求数据
	 * @param url 请求的URL
	 * @param params 请求的参数
	 * @param requestListener 请求的返回监听
	 */
	public static void executeGet(String url, List<RequestParam> params, RequestListener requestListener) {
		httpManager.startRequest(RequestType.GET, url, params, requestListener);
	}
	
	/**
	 * post请求方式请求数据
	 * @param url 请求的URL
	 * @param requestListener 请求的返回监听
	 */
	public static void executePost(String url, RequestListener requestListener) {
		httpManager.startRequest(RequestType.POST, url, null, requestListener);
	}
	
	/**
	 * post请求方式请求数据
	 * @param url 请求的URL
	 * @param params 请求的参数
	 * @param requestListener 请求的返回监听
	 */
	public static void executePost(String url, List<RequestParam> params, RequestListener requestListener) {
		httpManager.startRequest(RequestType.POST, url, params, requestListener);
	}
	
	/**
	 * 网络是否可用检查
	 * @param context 上下文对象
	 * @return 当前网络是否可用, true为可用
	 */
	public static boolean isNetAvailable(Context context) {
		
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connMgr == null) {
			return false;
		}
		
		return
		   (connMgr.getNetworkInfo(
				    ConnectivityManager.TYPE_WIFI) != null && 
				   connMgr.getNetworkInfo(
				    ConnectivityManager.TYPE_WIFI)
				   .isConnected())
		|| (connMgr.getNetworkInfo(
			    ConnectivityManager.TYPE_MOBILE) != null && 
		connMgr.getNetworkInfo(
					ConnectivityManager.TYPE_MOBILE)
			       .isConnected())
		|| (connMgr.getNetworkInfo(
				ConnectivityManager.TYPE_WIMAX) != null && 
		connMgr.getNetworkInfo(
					ConnectivityManager.TYPE_WIMAX)
			       .isConnected())     
		;
	}
	
	/**
	 * 从服务器读取数据（这里主要是图片文件）
	 * @param context 上下文 变量
	 * @param url  目标URL
	 * @return 返回的是一个下载到本地的文件的名字
	 * @throws IOException
	 */
	public static String loadDataFromServer(Context context, String url) throws Exception{
		
		
		if(!isNetAvailable(context)){
			
			throw new NetException();
		}
		String fileName = getFileName(url);
		InputStream is = getFileInputStream(url);
		OutputStream os = (FileOutputStream) LocalFileUtil.getLocalOS(context, fileName);		
		LocalFileUtil.writeLocalFile(context,is, os);
		return fileName;
	}
	
	/**
	 * 下载文件
	 * @param context 上下文 变量
	 * @param url  目标URL
	 * @param dir 文件下载后存储的目标路径
	 * @return 返回的是一个下载到本地的位置
	 * @throws IOException
	 */
	public static String downLoadFile(Context context, String url, String dir, FileDownLoadListener listener) throws NetException{
		
		
		if(!isNetAvailable(context)){
			
			throw new NetException();
		}
		httpManager.startLoadFile(context, url, dir, new FileDownHandler(listener));
		return dir + File.separator + getFileName(url);
	}
	
	/**
	 * 下载文件
	 * @param context 上下文 变量
	 * @param url  目标URL
	 * @param dir 文件下载后存储的目标路径
	 * @return 返回的是一个下载到本地的位置
	 * @throws IOException
	 */
	public static String downLoadFile(Context context, String url, String dir, String fileName, FileDownLoadListener listener) throws NetException{
		
		
		if(!isNetAvailable(context)){
			
			throw new NetException();
		}
		httpManager.startLoadFile(context, url, dir, fileName, new FileDownHandler(listener));
		return dir + File.separator + fileName;
	}
	
	/**
	 * 上传文件
	 * @param uploadUrl 上传的服务器Url地址
	 * @param uploadFile 需要上传的目标文件
	 * @param goalName 上传服务器后文件的新名字
	 */
	public static void uploadFile(Context context,String uploadUrl, String uploadFile, String goalName,FileUploadListener listener) throws NetException {
		if(!isNetAvailable(context)){			
			throw new NetException();
		}
		File file = new File(uploadFile);
		listener.prepare(file.length());
		httpManager.startUploadFile(uploadUrl, uploadFile, goalName, new FileUploadHandler(listener));
	}
	
	/**
	 * 上传文件
	 * @param uploadUrl 上传的服务器Url地址
	 * @param goalName 上传服务器后文件的新名字
	 */
	public static void uploadFile(Context context,String uploadUrl, String uploadFile,FileUploadListener listener) throws NetException {
		if(!isNetAvailable(context)){			
			throw new NetException();
		}
		File file = new File(uploadFile);
		listener.prepare(file.length());
		httpManager.startUploadFile(uploadUrl, uploadFile, getFileName(uploadFile), new FileUploadHandler(listener));
	}
	
	/**
	 * 根据URL获取文件的名字
	 * @param url 目标URL
	 * @return
	 */
	public static String getFileName(String url) {
		
		String fileNameRegexp = "[^/]*$";
		Pattern pattern = Pattern.compile(fileNameRegexp);
		Matcher matcher = pattern.matcher(url);
		String result = "";
		if (matcher.find()) {
			result = matcher.group();
		}
		if (result.indexOf("?") != -1) {
			String[] nameList = result.split("[?]");
			if (nameList.length > 0) {
				result = nameList[0];
			}
		}
		return result;
	}
	
    /**
     * 根据URL获取数据流对象
     * @param url 目标URL
     * @return 返回的是一个数据流对象
     * @throws IOException
     */
	public static InputStream getFileInputStream(String url) throws IOException {
        URLConnection conn = getConnection(url);
        conn.connect();
        InputStream inputStream = conn.getInputStream();
        return inputStream;
    }
    
	/**
	 *网络连接(图片读取的时候用的)
	 * @param url 目标URL
	 * @return URLConnection对象
	 * @throws IOException
	 */
	public static URLConnection getConnection(String url) throws IOException {
		URL aryURI= new URL(url);
		URLConnection conn = aryURI.openConnection();
		return conn;
	}
	
	/**
	 * 创建请求参数
	 * @param requestKey 参数名
	 * @param requestValue 参数值
	 * @return 参数对象
	 */
	public static RequestParam createRequestParam(String requestKey, Object requestValue) {
		return new RequestParam(requestKey, requestValue);
	}
}
