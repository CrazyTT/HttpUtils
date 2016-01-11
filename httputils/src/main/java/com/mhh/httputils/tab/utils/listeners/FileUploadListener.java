package com.mhh.httputils.tab.utils.listeners;

public interface FileUploadListener {

	public void progressUpload(long progress);
	public void prepare(long fileSize);
	public void uploadCompletion(boolean isSuccess);
}
