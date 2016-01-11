package com.mhh.httputils.tab.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mhh.httputils.tab.utils.listeners.ImageLoadListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

/**
 * @author 孟辉辉
 * 图片加载工具类
 */
public class ImageDownLoadUtil {

	private ExecutorService pool = Executors.newFixedThreadPool(1);
	private static ImageDownLoadUtil imageUtil;
	public ImageDownLoadUtil(){
		
	}
	
	/**
	 * @return ImageDownLoadUtil对象
	 */
	public static ImageDownLoadUtil getInstance(){
		if (imageUtil == null) {
			imageUtil = new  ImageDownLoadUtil();
		}
		return imageUtil;
	}
	
	/**
	 * 根据URL获取对应的图片资源
	 * @param context 上下文对象
	 * @param url 图片URL
	 * @param imageView 图片加载的目标控件
	 */
	public void loadImageBitmap(Context context, String url,ImageView imageView) {
		ImageHandler handler = new ImageHandler(imageView);
		pool.execute(new ImageBitmapThread(context, url, handler));
	}
	
	/**
	 * 根据URL获取对应的图片资源
	 * @param context 上下文对象
	 * @param url 图片URL
	 * @param imageView 图片加载的目标控件
	 * @param 图片加载情况监听
	 */
	public void loadImageBitmap(Context context, String url,ImageView imageView, ImageLoadListener loadListener) {
		ImageHandler handler = new ImageHandler(imageView, loadListener);
		pool.execute(new ImageBitmapThread(context, url, handler));
	}
	
	/**
	 * 根据URL获取对应的图片资源
	 * @param context 上下文对象
	 * @param url 图片URL
	 * @param imageView 图片加载的目标控件
	 * @param scale 图片的缩放比例
	 */
	public void loadImageBitmap(Context context, String url,ImageView imageView, float scale) {
		ImageHandler handler = new ImageHandler(imageView, scale);
		pool.execute(new ImageBitmapThread(context, url, handler));
	}
	
	/**
	 * 根据URL获取对应的图片资源
	 * @param context 上下文对象
	 * @param url 图片URL
	 * @param imageView 图片加载的目标控件
	 * @param scale 图片的缩放比例
	 * @param 图片加载情况监听
	 */
	public void loadImageBitmap(Context context, String url,ImageView imageView, float scale, ImageLoadListener loadListener) {
		ImageHandler handler = new ImageHandler(imageView, scale, loadListener);
		pool.execute(new ImageBitmapThread(context, url, handler));
	}
	
	/**
	 * 根据URL获取对应的图片资源
	 * @param context 上下文对象
	 * @param url 图片URL
	 * @param imageView 图片加载的目标控件
	 * @param width 图片缩放的目标宽度
	 * @param height 图片缩放的目标高度
	 */
	public void loadImageBitmap(Context context, String url,ImageView imageView, float width, float height) {
		ImageHandler handler = new ImageHandler(imageView, width, height);
		pool.execute(new ImageBitmapThread(context, url, handler));
	}
	
	/**
	 * 根据URL获取对应的图片资源
	 * @param context 上下文对象
	 * @param url 图片URL
	 * @param imageView 图片加载的目标控件
	 * @param width 图片缩放的目标宽度
	 * @param height 图片缩放的目标高度
	 * @param 图片加载情况监听
	 */
	public void loadImageBitmap(Context context, String url,ImageView imageView, float width, float height, ImageLoadListener loadListener) {
		ImageHandler handler = new ImageHandler(imageView, width, height, loadListener);
		pool.execute(new ImageBitmapThread(context, url, handler));
	}
	
	/**
	 * 根据URL获取对应的图片资源，可以在回调监听里面获取对应的bitmap
	 * @param context 上下文对象
	 * @param url 图片URL
	 * @param loadListener 回调监听对象
	 */
	public void loadImageBitmap(Context context, String url,ImageLoadListener loadListener) {
		ImageHandler handler = new ImageHandler(loadListener);
		pool.execute(new ImageBitmapThread(context, url, handler));
	}
	
	/**
	 * 图片读取线程
	 *
	 */
	private class ImageBitmapThread extends Thread {
		private Context context;
		private String url;
		private Handler handler;

		public ImageBitmapThread(Context context, String url,
				Handler handler) {
			this.context = context;
			this.url = url;
			this.handler = handler;
		}

		@Override
		public void run() {
			Message msg = handler.obtainMessage();
			try {
				Bitmap bitmap = getBitmap(context,
						url);
				msg.what = SUCCESS;
				msg.obj = bitmap;
				
			} catch (IOException e) {
				msg.what = NETWORK_ANOMALY;
				msg.obj = e;
				
			} catch (Throwable e) {
				msg.what = ERROR;
				msg.obj = e;
				
			} finally {
				msg.sendToTarget();
			}

		}
	}

	
	/**
	 * 
	 * 获取本地图片的Bitmap
	 * @param context 上下文对象
	 * @param imageName 图片名称
	 * @return 返回目标图片的bitmap
	 * @throws IOException
	 */
	private Bitmap getLocalImageBitmap(Context context, String imageName) throws IOException {
		
		Bitmap bitmap = null;
		InputStream input = null;
		try {
			input = LocalFileUtil.getLocalIS(context, imageName);
			bitmap = BitmapFactory.decodeStream(input);

		} catch (IOException e) {
			throw e;
		} finally {
			if (input != null) {
				input.close();
				input = null;
			}
		}
		return bitmap;
	}
	
	/**
	 * 根据图片的URL获取bitmap，先检查本地是否已经存在此图片，如果没有那么去下载，如果存在就读取本地。
	 * @param context 上下文对象
	 * @param url 图片的URL
	 * @return 返回当前URL图片的bitmap
	 * @throws IOException
	 */
	private Bitmap getBitmap(Context context, String url) throws Exception{
		String fileName = HttpUtil.getFileName(url);		
		Bitmap bitmap = null;
		try {
			bitmap = getLocalImageBitmap(context, fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{			
			if (bitmap == null) {
				HttpUtil.loadDataFromServer(context, url);
				bitmap = getLocalImageBitmap(context, fileName);
			}
		}
		return bitmap;
	}
	
	private final int SUCCESS = 1;
	private final int NETWORK_ANOMALY = 2;
	private final int ERROR = 3;
			
	@SuppressLint("HandlerLeak")
	class ImageHandler extends Handler{

		private ImageView imageView;
		private float goalWidth = 0;
		private float goalHeight = 0;
		private float scale = 0;
		private ImageLoadListener loadListener = null;
		public ImageHandler(ImageView imageView){
			this.imageView = imageView;
		}
		public ImageHandler(ImageView imageView, ImageLoadListener loadListener){
			this.imageView = imageView;
			this.loadListener = loadListener;
		}
		public ImageHandler(ImageView imageView, float scale){
			this.imageView = imageView;
			this.scale = scale;
		}
		public ImageHandler(ImageView imageView, float scale, ImageLoadListener loadListener){
			this.imageView = imageView;
			this.scale = scale;
			this.loadListener = loadListener;
		}
		public ImageHandler(ImageView imageView, float goalWidth, float goalHeight){
			this.imageView = imageView;
			this.goalWidth = goalWidth;
			this.goalHeight = goalHeight;
		}
		
		public ImageHandler(ImageView imageView, float goalWidth, float goalHeight, ImageLoadListener loadListener){
			this.imageView = imageView;
			this.goalWidth = goalWidth;
			this.goalHeight = goalHeight;
			this.loadListener = loadListener;
		}
		
		public ImageHandler(ImageLoadListener loadListener){
			this.loadListener = loadListener;
		}
		
		@Override
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			case SUCCESS:
				Bitmap bitmap = (Bitmap) msg.obj;
				if (loadListener != null) {
					loadListener.callback(bitmap);
				}
				if (goalWidth == 0 && goalHeight == 0 && scale == 0) {	
					bitmap = (Bitmap) msg.obj;
				}else if (scale != 0) {
					System.out.println("scale is " + scale);
					bitmap = getResizedBitmap((Bitmap) msg.obj, scale);
				}else if (goalWidth != 0 && goalHeight != 0) {
					bitmap = getResizedBitmap((Bitmap) msg.obj, goalWidth, goalHeight);
				}else {
					bitmap = (Bitmap) msg.obj;
				}
				if (imageView != null) {					
					imageView.setImageBitmap(bitmap);
				}
				break;
			case NETWORK_ANOMALY:
				if (loadListener != null) {
					loadListener.exceptionBack((IOException) msg.obj);
				}
				break;
			case ERROR:
				if (loadListener != null) {
					loadListener.exceptionBack((Exception) msg.obj);
				}
				break;

			default:
				break;
			}
		}		
	}
	
	/**
	 * @param bm 目标bitmap
	 * @param goalWidth 宽度的目标尺寸
	 * @param goalHeight 高度的目标尺寸
	 * @return 返回缩放过的Bitmap
	 */
	private Bitmap getResizedBitmap(Bitmap bm, float goalWidth, float goalHeight) {
		if (bm != null) {
			int width = bm.getWidth();
			int height = bm.getHeight();
			float widthScale =  goalWidth / width;
			float heightScale =  goalHeight / height;
			// create a matrix for the manipulation
			Matrix matrix = new Matrix();
			
			// resize the bit map
			matrix.postScale(widthScale, heightScale);
			
			// recreate the new Bitmap
			Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
					matrix, false);
			
			return resizedBitmap;
		}else {
			return null;
		}		
	}
	
	/**
	 * @param bm 目标bitmap
	 * @param scale  目标尺寸和当前尺寸的比例
	 * @return 返回缩放过的Bitmap
	 */
	private Bitmap getResizedBitmap(Bitmap bm, float scale) {
		if (bm != null) {
			int width = bm.getWidth();
			int height = bm.getHeight();
			// create a matrix for the manipulation
			Matrix matrix = new Matrix();
			
			// resize the bit map
			matrix.postScale(scale, scale);
			
			// recreate the new Bitmap
			Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
					matrix, false);
			
			return resizedBitmap;
		}else {
			return null;
		}		
	}
}
