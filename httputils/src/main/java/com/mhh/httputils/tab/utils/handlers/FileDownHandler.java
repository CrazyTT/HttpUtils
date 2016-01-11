package com.mhh.httputils.tab.utils.handlers;


import com.mhh.httputils.tab.utils.listeners.FileDownLoadListener;

import android.os.Handler;
import android.os.Message;

public class FileDownHandler extends Handler{

	public static final int FILE_SIZE = 1;
	public static final int PROGRESS = 2;
	public static final int COMPLETE = 3;
	private FileDownLoadListener listener;
	public FileDownHandler(FileDownLoadListener listener){
		this.listener = listener;
	}
	
	@Override
	public void handleMessage(Message msg) {

		switch (msg.what) {
		case FILE_SIZE:
			listener.prepare(msg.arg1);
			break;
		case PROGRESS:
			listener.downLoad(msg.arg1);
			break;
		case COMPLETE:
			listener.downCompletion(msg.arg1);
			break;

		default:
			break;
		}
	}
}
