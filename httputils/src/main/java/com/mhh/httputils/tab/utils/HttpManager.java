package com.mhh.httputils.tab.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Message;

import com.mhh.httputils.tab.utils.enums.RequestParam;
import com.mhh.httputils.tab.utils.enums.RequestType;
import com.mhh.httputils.tab.utils.handlers.FileDownHandler;
import com.mhh.httputils.tab.utils.handlers.FileUploadHandler;
import com.mhh.httputils.tab.utils.handlers.RequestHandler;
import com.mhh.httputils.tab.utils.listeners.RequestListener;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * @author �ϻԻ� �������������
 */
@SuppressLint("HandlerLeak")
public class HttpManager {

    private ExecutorService pool = Executors.newFixedThreadPool(1);
    private HttpURLConnection urlConnection = null;
    public static HttpManager httpManager = null;

    public static HttpManager getInstance() {
        if (httpManager == null) {
            httpManager = new HttpManager();
        }
        return httpManager;
    }

    /**
     * ��ʼ���������߳�
     *
     * @param �Ƿ���POST����ʽ
     * @param url             �����URL
     * @param params          �������
     * @param requestListener �������
     */
    public void startRequest(int requestType, String url,
                             List<RequestParam> params, RequestListener requestListener) {
        pool.execute(getRequestThread(requestType, url, params, new RequestHandler(requestListener)));
    }

    public void startLoadFile(Context context, String url, String dir, FileDownHandler handler) {
        pool.execute(getFileDownThread(context, url, dir, handler));
    }

    public void startLoadFile(Context context, String url, String dir, String fileName, FileDownHandler handler) {
        pool.execute(getFileDownThread(context, url, dir, fileName, handler));
    }

    public void startUploadFile(String uploadUrl, String uploadFile, String goalName, FileUploadHandler handler) {
        pool.execute(getUploadFileThread(uploadUrl, uploadFile, goalName, handler));
    }

    /**
     * �������
     *
     * @param params ���в���
     * @return ������Ϻ������
     * @throws Exception �쳣��Ϣ
     */
    private String getParamsString(List<RequestParam> params) throws Exception {
        // Ҫ�ϴ��Ĳ���
        String paramString = "";
        if (params == null) {
            params = new ArrayList<RequestParam>();
        }
        for (int i = 0; i < params.size(); i++) {
            if (i != params.size() - 1) {
                paramString += params.get(i).getRequestKey()
                        + "="
                        + URLEncoder.encode(params.get(i).getRequestValue(),
                        "UTF-8") + "&";
            } else {
                paramString += params.get(i).getRequestKey()
                        + "="
                        + URLEncoder.encode(params.get(i).getRequestValue(),
                        "UTF-8");
            }
        }
        return paramString;
    }

    /**
     * HTTP GET��ʽ�����ʼ��
     *
     * @param url
     * @param params
     */
    @SuppressLint("DefaultLocale")
    public void initGet(String url, List<RequestParam> params) throws Exception {

        String paramString = getParamsString(params);
        if (paramString != null && !paramString.equals("")) {
            url = url + "?" + getParamsString(params);
        }
        // ͨ��openConnection ����
        URL urlGet = new java.net.URL(url);
        if (urlGet.getProtocol().toLowerCase().equals("https")) {
            trustAllHosts();
            urlConnection = (HttpsURLConnection) urlGet.openConnection();
            ((HttpsURLConnection) urlConnection)
                    .setHostnameVerifier(DO_NOT_VERIFY);// ������������ȷ
        } else {
            urlConnection = (HttpURLConnection) urlGet.openConnection();
        }
        // ��������������
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);
        urlConnection.setRequestMethod("GET");
        // �ر�����
        urlConnection.disconnect();
    }

    /**
     * HTTP POST��ʽ�����ʼ��
     *
     * @param url
     * @param params
     * @throws Exception
     */
    @SuppressLint("DefaultLocale")
    public void initPost(String url, List<RequestParam> params) throws Exception {

        // Ҫ�ϴ��Ĳ���
        String paramString = getParamsString(params);
        // ͨ��openConnection ����
        URL urlPost = new java.net.URL(url);
        if (urlPost.getProtocol().toLowerCase().equals("https")) {
            trustAllHosts();
            urlConnection = (HttpsURLConnection) urlPost.openConnection();
            ((HttpsURLConnection) urlConnection)
                    .setHostnameVerifier(DO_NOT_VERIFY);// ������������ȷ
        } else {
            urlConnection = (HttpURLConnection) urlPost.openConnection();
        }
        urlConnection.setConnectTimeout(20000);//�������ӳ�ʱ:20000ms
        urlConnection.setReadTimeout(20000);//���ö�ȡ��ʱ:20000ms
        // ��������������
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);

        urlConnection.setRequestMethod("POST");
        // POST��ʽ��Ҫ�رջ���
        urlConnection.setUseCaches(false);
        // ���ñ������ӵ�Content-type������Ϊapplication/x-www-form-urlencoded��
        urlConnection.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");
        // ���������ͷ
        urlConnection.setRequestProperty("Connection", "keep-alive");
        urlConnection.setRequestProperty("Charset", "UTF-8");
        // ���������ͷ
        urlConnection.setRequestProperty("Content-Length",
                String.valueOf(paramString.getBytes("UTF-8").length));
        // ���ӣ���postUrl.openConnection()���˵����ñ���Ҫ��connect֮ǰ��ɣ�
        // Ҫע�����connection.getOutputStream�������Ľ���connect��
        urlConnection.connect();
        // DataOutputStream��
        DataOutputStream out = new DataOutputStream(
                urlConnection.getOutputStream());
        // ��Ҫ�ϴ�������д������
        out.writeBytes(paramString);
        // ˢ�¡��ر�
        out.flush();
        out.close();

    }

    /**
     * ��ȡ���������߳�
     *
     * @param requestType ��������
     * @param url         �����ַ
     * @param params      �������
     * @param ����״̬����
     * @return �����������
     */
    public Thread getRequestThread(final int requestType, final String url,
                                   final List<RequestParam> params,
                                   final RequestHandler handler) {
        return new Thread() {
            public void run() {

                boolean isExceptionCatch = false;//�Ƿ��Ѿ����쳣�׳�����ֹ�ظ���handler������Ϣ
                String resultData = "";
                try {
                    if (requestType == RequestType.POST) {
                        // Post��ʽ
                        initPost(url, params);
                    } else {
                        // Get��ʽ
                        initGet(url, params);
                    }
                    if (urlConnection.getResponseCode() == 200) {
                        InputStreamReader in = new InputStreamReader(
                                urlConnection.getInputStream());
                        BufferedReader buffer = new BufferedReader(in);
                        String inputLine = null;
                        while (((inputLine = buffer.readLine()) != null)) {
                            resultData += inputLine + "\n";
                        }
                        in.close();
                        disconectHttpPost(requestType, isExceptionCatch, handler);
                        Message msg = handler.obtainMessage();
                        msg.what = RequestHandler.STATUS_SUCCESS;
                        msg.obj = resultData;
                        msg.sendToTarget();
                    } else {
                        isExceptionCatch = true;
                        disconectHttpPost(requestType, isExceptionCatch, handler);
                        Message msg = handler.obtainMessage();
                        msg.what = RequestHandler.STATUS_EXCEPTION;
                        msg.obj = new Exception("ResponseCode = " + urlConnection.getResponseCode());
                        msg.sendToTarget();
                    }

                } catch (Exception e) {
                    if (!isExceptionCatch) {
                        isExceptionCatch = true;
                        disconectHttpPost(requestType, isExceptionCatch, handler);
                        Message msg = handler.obtainMessage();
                        msg.what = RequestHandler.STATUS_EXCEPTION;
                        msg.obj = e;
                        msg.sendToTarget();
                        e.printStackTrace();
                    }
                }
//				finally {
//					try {
//						// �ر�����
//						if (requestType == RequestType.POST) {
//							urlConnection.disconnect();
//						}						
//					} catch (Exception e) {
//						e.printStackTrace();
//						if (!isExceptionCatch) {							
//							Message msg = handler.obtainMessage();
//							msg.what = RequestHandler.STATUS_EXCEPTION;
//							msg.obj = e;
//							msg.sendToTarget();						
//						}
//					}
//				}
            }
        };
    }

    private void disconectHttpPost(final int requestType, boolean isExceptionCatch, final RequestHandler handler) {
        try {
            // �ر�����
            if (requestType == RequestType.POST) {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (!isExceptionCatch) {
                Message msg = handler.obtainMessage();
                msg.what = RequestHandler.STATUS_EXCEPTION;
                msg.obj = e;
                msg.sendToTarget();
            }
        }
    }


    /**
     * ������������-�����κ�֤�鶼�������
     */
    static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            // System.out.println("URL Host: " + hostname + " vs. " +
            // session.getPeerHost());
            return true;
        }
    };

    /**
     * ������������-�����κ�֤�鶼�������
     */
    static X509TrustManager DO_NOT_TRUST_MANAGER = new X509TrustManager() {

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            // TODO Auto-generated method stub
            return new X509Certificate[]{};
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            // TODO Auto-generated method stub

        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            // TODO Auto-generated method stub

        }
    };

    /**
     * �����κ����ι���
     */
    static TrustManager[] xtmArray = new TrustManager[]{DO_NOT_TRUST_MANAGER};

    /**
     * ������������
     */
    @SuppressLint({"DefaultLocale", "TrulyRandom"})
    public void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        // Android ����X509��֤����Ϣ����
        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, xtmArray, new java.security.SecureRandom());
            HttpsURLConnection
                    .setDefaultSSLSocketFactory(sc.getSocketFactory());
            // ������������ȷ��
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Thread getFileDownThread(Context context, String url, String dir, FileDownHandler handler) {
        return new DownLoadFileThread(context, url, dir, handler);
    }

    public Thread getFileDownThread(Context context, String url, String dir, String fileName, FileDownHandler handler) {
        return new DownLoadFileThread(context, url, dir, fileName, handler);
    }

    class DownLoadFileThread extends Thread {
        private Context context;
        private String url;
        private String dir;
        private FileDownHandler handler;
        private String fileName;

        public DownLoadFileThread(Context context, String url, String dir, FileDownHandler handler) {
            this.context = context;
            this.url = url;
            this.dir = dir;
            this.handler = handler;
        }

        public DownLoadFileThread(Context context, String url, String dir, String fileName, FileDownHandler handler) {
            this.context = context;
            this.url = url;
            this.dir = dir;
            this.handler = handler;
            this.fileName = fileName;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (fileName == null || fileName.equals("")) {
                fileName = HttpUtil.getFileName(url);
            }
            URLConnection conn;
            try {
                conn = HttpUtil.getConnection(url);
                conn.connect();
                Message msg = handler.obtainMessage();
                msg.arg1 = conn.getContentLength();
                msg.what = FileDownHandler.FILE_SIZE;
                msg.sendToTarget();
                InputStream is = conn.getInputStream();
                OutputStream os = (FileOutputStream) LocalFileUtil.getLocalOS(fileName, dir);
                LocalFileUtil.writeLocalFile(context, is, os, handler);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public Thread getUploadFileThread(final String uploadUrl, final String uploadFile, final String goalName, final FileUploadHandler handler) {
        return new Thread() {

            @Override
            public void run() {
                upLoadFile(uploadUrl, uploadFile, goalName, handler);
            }
        };
    }

    public void upLoadFile(String uploadUrl, String uploadFile, String goalName, FileUploadHandler handler) {

        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        try {
            URL url = new URL(uploadUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            /* ����Input��Output����ʹ��Cache */
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
			/* ���ô��͵�method=POST */
            urlConnection.setRequestMethod("POST");
			/* setRequestProperty */
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Charset", "UTF-8");
            urlConnection.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);
			/* ����DataOutputStream */
            DataOutputStream ds = new DataOutputStream(
                    urlConnection.getOutputStream());
            ds.writeBytes(twoHyphens + boundary + end);
            ds.writeBytes("Content-Disposition: form-data; "
                    + "name=\"uploadedfile\";filename=\"" + goalName + "\"" + end);
            ds.writeBytes(end);
			/* ȡ���ļ���FileInputStream */
            FileInputStream fStream = new FileInputStream(uploadFile);
			/* ����ÿ��д��1024bytes */
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int length = -1;
            int toalUpload = 0;
			/* ���ļ���ȡ������������ */
            while ((length = fStream.read(buffer)) != -1) {
				/* ������д��DataOutputStream�� */
                ds.write(buffer, 0, length);
                toalUpload += length;
                Message msg = handler.obtainMessage();
                msg.what = FileUploadHandler.PROGRESS;
                msg.arg1 = toalUpload;
                msg.sendToTarget();
            }
            ds.writeBytes(end);
            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
			/* close streams */
            fStream.close();
            ds.flush();
			/* ȡ��Response���� */
            InputStream is = urlConnection.getInputStream();
            int ch;
            StringBuffer b = new StringBuffer();
            while ((ch = is.read()) != -1) {
                b.append((char) ch);
            }
			/* ��Response��ʾ��Dialog */
            Message msg = handler.obtainMessage();
            msg.what = FileUploadHandler.FINISH_UPLOAD;
            msg.arg1 = 1;
            msg.sendToTarget();
			/* �ر�DataOutputStream */
            ds.close();
        } catch (Exception e) {
            System.out.println("upload fial :" + e);
            Message msg = handler.obtainMessage();
            msg.what = FileUploadHandler.FINISH_UPLOAD;
            msg.arg1 = 0;
            msg.sendToTarget();
        }
    }
}
