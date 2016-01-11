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
 * @author 孟辉辉
 * 本地文件操作工具类
 */
public class LocalFileUtil {
	
	
	/**
	 * 删除本地文件
	 * @param context 上下文对象
	 * @param fileName 要删除的目标文件名称
	 */
	public static void deleteFile(Context context,String fileName) {
		File file=new File(context.getCacheDir().getPath() + File.separator + fileName);
		file.delete();
	}
	
	/**
	 * 删除缓存中所有文件（慎用）
	 * @param context 上下文对象
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
	 * 根据文件名称获取本地文件的OutputStream对象
	 * @param context 上下文对象
	 * @param fileName 文件名称（缓存中的）
	 * @return 本地文件的OutputStream对象
	 * @throws FileNotFoundException 没有找到此文件
	 */
	public static OutputStream getLocalOS(Context context, String fileName)
			throws FileNotFoundException {
		File file = context.getCacheDir();
		return new FileOutputStream(file.getPath() + File.separator + fileName);
	}
	
	/**
	 * 根据文件名称获取本地文件的InputStream对象
	 * @param context 上下文对象
	 * @param fileName 文件名称（缓存中的）
	 * @return 本地文件的InputStream对象
	 * @throws FileNotFoundException 没有找到此文件
	 */
	public static InputStream getLocalIS(Context context, String fileName)
			throws FileNotFoundException {
		File file = context.getCacheDir();
		return new FileInputStream(file.getPath() + File.separator + fileName);
	}
	
	/**
	 * 根据文件名称获取本地文件的OutputStream对象
	 * @param context 上下文对象
	 * @param fileName 文件名称
	 * @param dir 路径
	 * @return 本地文件的OutputStream对象
	 * @throws FileNotFoundException 没有找到此文件
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
	 * 根据文件名称获取本地文件的InputStream对象
	 * @param context 上下文对象
	 * @param fileName 文件名称
	 * @param dir 路径
	 * @return 本地文件的InputStream对象
	 * @throws FileNotFoundException 没有找到此文件
	 */

	public static InputStream getLocalIS(String fileName, String dir)
			throws FileNotFoundException {
		File file = new File(dir, fileName);
		return new FileInputStream(file);
	}
	
	/**
	 * 文件写入操作
	 * @param context 上下文对象
	 * @param is 需要写入的InputStream
	 * @param os 目标OutputStream
	 * @throws IOException IO流读写异常
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
	 * 文件写入操作
	 * @param context 上下文对象
	 * @param is 需要写入的InputStream
	 * @param os 目标OutputStream
	 * @throws IOException IO流读写异常
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
