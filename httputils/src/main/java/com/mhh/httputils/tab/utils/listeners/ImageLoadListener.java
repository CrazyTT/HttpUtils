package com.mhh.httputils.tab.utils.listeners;

import android.graphics.Bitmap;

/**
 * @author 孟辉辉
 * 网络读取图片的回调监听
 */
public interface ImageLoadListener {

	/**
	 * @param bitmap 目标图片的bitmap
	 */
	public void callback(Bitmap bitmap);
	
	public void exceptionBack(Exception e);
}
