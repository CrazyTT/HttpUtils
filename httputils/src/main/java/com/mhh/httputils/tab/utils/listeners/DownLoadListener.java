package com.mhh.httputils.tab.utils.listeners;

/**
 * @author �ϻԻ�
 * �����������
 */
public interface DownLoadListener {
	
	/**
	 * ���سɹ��ɹ�ʱ�ص�
	 * @param dir �ļ����ص�·��
	 * @param fileName �ļ�����
	 */
	public abstract void downLoadSuccess(String dir, String fileName);
	/**
	 * ����ʧ��ʱ�����
	 * @param e ������쳣����
	 */
	public abstract void downLoadException(Exception e);
}
