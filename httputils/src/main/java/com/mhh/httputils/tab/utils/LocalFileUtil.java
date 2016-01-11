package com.mhh.httputils.tab.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.mhh.httputils.tab.utils.handlers.FileDownHandler;

import android.content.Context;
import android.os.Message;

/**
 * @author �ϻԻ�
 * �����ļ�����������
 */
public class LocalFileUtil {
	
	
	/**
	 * ɾ�������ļ�
	 * @param context �����Ķ���
	 * @param fileName Ҫɾ����Ŀ���ļ�����
	 */
	public static void deleteFile(Context context,String fileName) {
		File file=new File(context.getCacheDir().getPath() + File.separator + fileName);
		file.delete();
	}
	
	/**
	 * ɾ�������������ļ������ã�
	 * @param context �����Ķ���
	 */
	public static void deleteFiles(Context context) {
		File file= context.getCacheDir();
		if (file.exists()) {
			
			File[] files = file.listFiles();
			for (File f : files) {
				if (f.isFile()) {
					f.delete();
				}
			}
		}
	}

	/**
	 * �����ļ����ƻ�ȡ�����ļ���OutputStream����
	 * @param context �����Ķ���
	 * @param fileName �ļ����ƣ������еģ�
	 * @return �����ļ���OutputStream����
	 * @throws FileNotFoundException û���ҵ����ļ�
	 */
	public static OutputStream getLocalOS(Context context, String fileName)
			throws FileNotFoundException {
		File file = context.getCacheDir();
		return new FileOutputStream(file.getPath() + File.separator + fileName);
	}
	
	/**
	 * �����ļ����ƻ�ȡ�����ļ���InputStream����
	 * @param context �����Ķ���
	 * @param fileName �ļ����ƣ������еģ�
	 * @return �����ļ���InputStream����
	 * @throws FileNotFoundException û���ҵ����ļ�
	 */
	public static InputStream getLocalIS(Context context, String fileName)
			throws FileNotFoundException {
		File file = context.getCacheDir();
		return new FileInputStream(file.getPath() + File.separator + fileName);
	}
	
	/**
	 * �����ļ����ƻ�ȡ�����ļ���OutputStream����
	 * @param context �����Ķ���
	 * @param fileName �ļ�����
	 * @param dir ·��
	 * @return �����ļ���OutputStream����
	 * @throws FileNotFoundException û���ҵ����ļ�
	 */
	public static OutputStream getLocalOS(String fileName, String dir)
			throws FileNotFoundException {
		File file = new File(dir);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(dir, fileName);
		return new FileOutputStream(file);
	}
	
	/**
	 * �����ļ����ƻ�ȡ�����ļ���InputStream����
	 * @param context �����Ķ���
	 * @param fileName �ļ�����
	 * @param dir ·��
	 * @return �����ļ���InputStream����
	 * @throws FileNotFoundException û���ҵ����ļ�
	 */

	public static InputStream getLocalIS(String fileName, String dir)
			throws FileNotFoundException {
		File file = new File(dir, fileName);
		return new FileInputStream(file);
	}
	
	/**
	 * �ļ�д�����
	 * @param context �����Ķ���
	 * @param is ��Ҫд���InputStream
	 * @param os Ŀ��OutputStream
	 * @throws IOException IO����д�쳣
	 */
	public static void writeLocalFile(Context context,
			InputStream is, OutputStream os)
			throws IOException {
		byte buf[] = new byte[1024];
		
		int numread;
		try {
			while ((numread = is.read(buf)) != -1) {
				os.write(buf, 0, numread);
				
			}			
		} catch (IOException e) {
			throw e;
		}finally {
			if (is != null) {
				is.close();
				is = null;
			}
			if (os != null) {
				os.close();
				os = null;
			}
		}
	}
	
	/**
	 * �ļ�д�����
	 * @param context �����Ķ���
	 * @param is ��Ҫд���InputStream
	 * @param os Ŀ��OutputStream
	 * @throws IOException IO����д�쳣
	 */
	public static void writeLocalFile(Context context,
			InputStream is, OutputStream os, FileDownHandler handler)
			throws IOException {
		byte buf[] = new byte[1024];
		
		int numread;
		int total = 0;
		try {
			while ((numread = is.read(buf)) != -1) {
				os.write(buf, 0, numread);
				total += numread;
				Message msg = handler.obtainMessage();
				msg.arg1 = total;
				msg.what = FileDownHandler.PROGRESS;
				msg.sendToTarget();
			}
		} catch (IOException e) {
			throw e;
		}finally {
			if (is != null) {
				is.close();
				is = null;
			}
			if (os != null) {
				os.close();
				os = null;
			}
			Message msg = handler.obtainMessage();
			msg.arg1 = total;
			msg.what = FileDownHandler.COMPLETE;
			msg.sendToTarget();
		}
	}
	
}
