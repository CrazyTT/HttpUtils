package com.mhh.httputils.tab.utils.listeners;

public interface FileDownLoadListener {

	public void downLoad(int progress);
	public void prepare(int fileSize);
	public void downCompletion(int fileSize);
}
