package com.mhh.httputils.tab.utils.listeners;

/**
 * @author �ϻԻ�
 * �����������
 */
public interface RequestListener {
	
	/**
	 * ��������ɹ�ʱ�ص�
	 * @param result ��������Ľ����Ϣ���磺json
	 */
	public abstract void requestSuccess(String result);
	/**
	 * ��������ʧ��ʱ�����
	 * @param responseStatus ʧ��״̬���磺ConnectionStatus.SessionTimeOut��Session��ʱ����ConnectionStatus.ConnectionException�������쳣��
	 * @param e ������쳣����
	 */
	public abstract void requestException(int responseStatus, Exception e);
}
