package com.socket.org.join.ws.service;

import java.util.Timer;
import java.util.TimerTask;


import com.socket.org.join.ws.serv.WebServer;
import com.socket.org.join.ws.ui.WebServActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.socket.org.join.ws.Constants;

/**
 * @brief Web Service后台
 * @author join
 */
public class WebService extends Service implements WebServer.OnWebServListener {

    static final String TAG = "WebService";
    static final boolean DEBUG = false || Constants.Config.DEV_MODE;

    /** 错误时自动恢复的次数。如果仍旧异常，则继续传递。 */
    private static final int RESUME_COUNT = 3;
    /** 错误时重置次数的时间间隔。 */
    private static final int RESET_INTERVAL = 3000;
    private int errCount = 0;
    private Timer mTimer = new Timer(true);
    private TimerTask resetTask;

    private WebServer webServer;
    private WebServer.OnWebServListener mListener;

    private boolean isRunning = false;

    private NotificationManager mNM;

//    private int NOTI_SERV_RUNNING = R.string.noti_serv_running;

    private LocalBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public WebService getService() {
            return WebService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (DEBUG)
            Log.d(TAG,
                    String.format("create server: port=%d, root=%s", Constants.Config.PORT, Constants.Config.WEBROOT));
        webServer = new WebServer(Constants.Config.PORT, Constants.Config.WEBROOT);
        webServer.setOnWebServListener(this);
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        openWebServer();
        return mBinder;
    }

    private void openWebServer() {
        if (webServer != null) {
            webServer.setDaemon(true);
            webServer.start();
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        closeWebServer();
        return super.onUnbind(intent);
    }

    private void closeWebServer() {
        if (webServer != null) {
            webServer.close();
            webServer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStarted() {
        if (DEBUG)
            Log.d(TAG, "onStarted");
//        showNotification(NOTI_SERV_RUNNING, R.mipmap.ic_launcher);
        if (mListener != null) {
            mListener.onStarted();
        }
        isRunning = true;
    }

    @Override
    public void onStopped() {
        if (DEBUG)
            Log.d(TAG, "onStopped");
//        mNM.cancel(NOTI_SERV_RUNNING);
        if (mListener != null) {
            mListener.onStopped();
        }
        isRunning = false;
    }

    @Override
    public void onError(int code) {
        if (DEBUG)
            Log.d(TAG, "onError");
        if (code != WebServer.ERR_UNEXPECT) {
            if (mListener != null) {
                mListener.onError(code);
            }
            return;
        }
        errCount++;
        restartResetTask(RESET_INTERVAL);
        if (errCount <= RESUME_COUNT) {
            if (DEBUG)
                Log.d(TAG, "Retry times: " + errCount);
            openWebServer();
        } else {
            if (mListener != null) {
                mListener.onError(code);
            }
            errCount = 0;
            cancelResetTask();
        }
    }

    private void cancelResetTask() {
        if (resetTask != null) {
            resetTask.cancel();
            resetTask = null;
        }
    }

    private void restartResetTask(long delay) {
        cancelResetTask();
        resetTask = new TimerTask() {
            @Override
            public void run() {
                errCount = 0;
                resetTask = null;
                if (DEBUG)
                    Log.d(TAG, "ResetTask executed.");
            }
        };
        mTimer.schedule(resetTask, delay);
    }

    @SuppressWarnings("deprecation")
    private void showNotification(int resId, int iconId) {
        CharSequence text = getText(resId);
//
//        Notification notification = new Notification(iconId, text, System.currentTimeMillis());
//
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this,
//                WebServActivity.class), 0);
//
//        /**
//         *
//         * closed by hyy
//         */
////        notification.setLatestEventInfo(this, getText(R.string.app_name), text, contentIntent);
//        notification.flags = Notification.FLAG_ONGOING_EVENT;
//
//        mNM.notify(resId, notification);
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setOnWebServListener(WebServer.OnWebServListener mListener) {
        this.mListener = mListener;
    }

}
