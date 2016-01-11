
package com.mhh.httputils.tab.utils.handlers;

import com.mhh.httputils.tab.klog.KLog;
import com.mhh.httputils.tab.utils.Config;
import com.mhh.httputils.tab.utils.enums.ConnectionStatus;
import com.mhh.httputils.tab.utils.listeners.RequestListener;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class RequestHandler extends Handler {

    public static final int STATUS_SUCCESS = 1;

    public static final int STATUS_EXCEPTION = 2;

    public static final int STATUS_SESSIONTIMEOUT = 3;

    private RequestListener listener;

    public RequestHandler(RequestListener listener) {
        this.listener = listener;
    }

    @Override
    public void handleMessage(Message msg) {

        switch (msg.what) {
            case STATUS_SUCCESS:
                String json = (String) msg.obj;
                if (Config.isDebug) {
                    Log.d("response====", json);
                    KLog.json(json);
                }
                listener.requestSuccess(json);
                break;
            case STATUS_EXCEPTION:
                Exception exception = (Exception) msg.obj;
                listener.requestException(ConnectionStatus.ConnectionException, exception);
                break;
            case STATUS_SESSIONTIMEOUT:
                Exception timeOut = (Exception) msg.obj;
                listener.requestException(ConnectionStatus.SessionTimeOut, timeOut);
                break;

            default:
                break;
        }
    }

}
