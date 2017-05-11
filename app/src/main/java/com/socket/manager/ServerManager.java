package com.socket.manager;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.socket.org.join.ws.receiver.OnWsListener;
import com.socket.org.join.ws.receiver.WSReceiver;
import com.socket.org.join.ws.serv.WebServer;
import com.socket.org.join.ws.service.WSService;
import com.socket.org.join.ws.service.WebService;
import com.socket.org.join.ws.ui.WebServActivity;
import com.socket.org.join.ws.util.CommonUtil;

import com.socket.manager.callback.StartServerListener;

import java.util.List;

/**
 * Http服务器开启工具类
 * Created by Lee on 2017/3/21 0021.
 */

public class ServerManager {


    private final  static String TAG= "ServerManager";

    private final static class Instant{
        public static final ServerManager instant = new ServerManager();
    }

    public static ServerManager getInstant(){
        return Instant.instant;
    }

    private Context context;
    private Intent webServIntent;
    private WebService webService;
    private boolean isServiceOpen = false;
    private CommonUtil mCommonUtil;
    private StartServerListener startServerListener;

    public Context getContext(){

        return context;
    }

    /**
     * Http服务器初始化，开启后台服务
     * @param context
     */
    public void init(final Context context){
        this.context = context;
        /**
         * 先判断服务是否打开，已经打开就直接回调，否则就打开
         */
//        Boolean checkServiceAlive=isServiceRunning(context,"com.socket.org.join.ws.service.WebService");
        if (isServiceOpen){
            startServerListener.onComplete(true);
        }else {
            mCommonUtil = CommonUtil.getSingleton();
            webServIntent = new Intent(context, WebService.class);     //5.0以上系统注意此方法，更改为class类名启动服务

            Intent wsServIntent = new Intent(context,WSService.class);
            context.startService(wsServIntent);

            WSReceiver.register(context, new OnWsListener() {
                @Override
                public void onServAvailable() {

                }

                @Override
                public void onServUnavailable() {

                }
            });
            startHttpServer();
        }

    }

    /**
     * 开启Http服务器，绑定服务，开启成功进行回调通知Socket
     */
    public void startHttpServer(){

                context.bindService(webServIntent, new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

                        webService = ((WebService.LocalBinder) iBinder).getService();
                        webService.setOnWebServListener(onWebServListener);
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName componentName) {
                        webService = null;

                    }
                }, Context.BIND_AUTO_CREATE);


    }

    WebServer.OnWebServListener onWebServListener = new WebServActivity() {
        @Override
        public void onStarted() {
            Log.i(TAG, "onStarted:--startServerListener--->  " + startServerListener);
            if(startServerListener != null){
                startServerListener.onComplete(true);
                isServiceOpen = true;
            }
        }

        @Override
        public void onStopped() {
            Log.i(TAG, "onStopped: ");
        }

        @Override
        public void onError(int code) {
            Log.i(TAG, "onError: ");
        }
    };

    public void setStartServerListener(StartServerListener Callback) {
        this.startServerListener = Callback;
    }


    /**
     * 判断Web服务是否已经启动,context上下文对象 ，className服务的name
     */
    public static boolean isServiceRunning(Context mContext, String className) {

        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(200);

        if (!(serviceList.size() > 0)) {
            return false;
        }

        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
}
