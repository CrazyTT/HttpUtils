package com.mhh.httputils.tab.utils.listeners;

import android.graphics.Bitmap;

/**
 * @author �ϻԻ�
 * �����ȡͼƬ�Ļص�����
 */
public interface ImageLoadListener {

	/**
	 * @param bitmap Ŀ��ͼƬ��bitmap
	 */
	public void callback(Bitmap bitmap);
	
	public void exceptionBack(Exception e);
}
