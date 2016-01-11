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
 * @author �ϻԻ�
 * �������󹤾���
 */
public class HttpUtil {

	private static HttpManager httpManager = HttpManager.getInstance();
	
	/**
	 * get����ʽ��������
	 * @param url �����URL
	 * @param requestListener ����ķ��ؼ���
	 */
	public static void executeGet(String url, RequestListener requestListener) {
		httpManager.startRequest(RequestType.GET, url, null, requestListener);
	}
	
	/**
	 * get����ʽ��������
	 * @param url �����URL
	 * @param params ����Ĳ���
	 * @param requestListener ����ķ��ؼ���
	 */
	public static void executeGet(String url, List<RequestParam> params, RequestListener requestListener) {
		httpManager.startRequest(RequestType.GET, url, params, requestListener);
	}
	
	/**
	 * post����ʽ��������
	 * @param url �����URL
	 * @param requestListener ����ķ��ؼ���
	 */
	public static void executePost(String url, RequestListener requestListener) {
		httpManager.startRequest(RequestType.POST, url, null, requestListener);
	}
	
	/**
	 * post����ʽ��������
	 * @param url �����URL
	 * @param params ����Ĳ���
	 * @param requestListener ����ķ��ؼ���
	 */
	public static void executePost(String url, List<RequestParam> params, RequestListener requestListener) {
		httpManager.startRequest(RequestType.POST, url, params, requestListener);
	}
	
	/**
	 * �����Ƿ���ü��
	 * @param context �����Ķ���
	 * @return ��ǰ�����Ƿ����, trueΪ����
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
	 * �ӷ�������ȡ���ݣ�������Ҫ��ͼƬ�ļ���
	 * @param context ������ ����
	 * @param url  Ŀ��URL
	 * @return ���ص���һ�����ص����ص��ļ�������
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
	 * �����ļ�
	 * @param context ������ ����
	 * @param url  Ŀ��URL
	 * @param dir �ļ����غ�洢��Ŀ��·��
	 * @return ���ص���һ�����ص����ص�λ��
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
	 * �����ļ�
	 * @param context ������ ����
	 * @param url  Ŀ��URL
	 * @param dir �ļ����غ�洢��Ŀ��·��
	 * @return ���ص���һ�����ص����ص�λ��
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
	 * �ϴ��ļ�
	 * @param uploadUrl �ϴ��ķ�����Url��ַ
	 * @param uploadFile ��Ҫ�ϴ���Ŀ���ļ�
	 * @param goalName �ϴ����������ļ���������
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
	 * �ϴ��ļ�
	 * @param uploadUrl �ϴ��ķ�����Url��ַ
	 * @param goalName �ϴ����������ļ���������
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
	 * ����URL��ȡ�ļ�������
	 * @param url Ŀ��URL
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
     * ����URL��ȡ����������
     * @param url Ŀ��URL
     * @return ���ص���һ������������
     * @throws IOException
     */
	public static InputStream getFileInputStream(String url) throws IOException {
        URLConnection conn = getConnection(url);
        conn.connect();
        InputStream inputStream = conn.getInputStream();
        return inputStream;
    }
    
	/**
	 *��������(ͼƬ��ȡ��ʱ���õ�)
	 * @param url Ŀ��URL
	 * @return URLConnection����
	 * @throws IOException
	 */
	public static URLConnection getConnection(String url) throws IOException {
		URL aryURI= new URL(url);
		URLConnection conn = aryURI.openConnection();
		return conn;
	}
	
	/**
	 * �����������
	 * @param requestKey ������
	 * @param requestValue ����ֵ
	 * @return ��������
	 */
	public static RequestParam createRequestParam(String requestKey, Object requestValue) {
		return new RequestParam(requestKey, requestValue);
	}
}
