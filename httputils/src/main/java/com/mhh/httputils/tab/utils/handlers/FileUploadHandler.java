package com.mhh.httputils.tab.utils.handlers;


import com.mhh.httputils.tab.utils.listeners.FileUploadListener;

import android.os.Handler;
import android.os.Message;

public class FileUploadHandler extends Handler{

	public static final int PREPARE = 1;
	public static final int PROGRESS = 2;
	public static final int FINISH_UPLOAD = 3;
	private FileUploadListener listener;
	public FileUploadHandler(FileUploadListener listener){
		this.listener = listener;
	}
	
	@Override
	public void handleMessage(Message msg) {

		switch (msg.what) {
		case PREPARE:
			listener.prepare(msg.arg1);
			break;
		case PROGRESS:
			listener.progressUpload(msg.arg1);
			break;
		case FINISH_UPLOAD:
			if (msg.arg1 == 1) {
				listener.uploadCompletion(true);
			}else {				
				listener.uploadCompletion(false);
			}
			break;

		default:
			break;
		}
	}
}
