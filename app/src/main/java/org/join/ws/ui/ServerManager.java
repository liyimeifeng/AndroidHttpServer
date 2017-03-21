package org.join.ws.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import org.join.ws.receiver.OnWsListener;
import org.join.ws.receiver.WSReceiver;
import org.join.ws.serv.WebServer;
import org.join.ws.service.WSService;
import org.join.ws.service.WebService;
import org.join.ws.ui.WebServActivity;
import org.join.ws.util.CommonUtil;

import socket.callback.StartServer;

/**
 * Created by Lee on 2017/3/21 0021.
 */

public class ServerManager {
    private final  static String TAG= "ServerManager";

    private Context context;
    private Intent webServIntent;
    private WebService webService;

    private CommonUtil mCommonUtil;
    private StartServer startServer;

    public void init(Context context){

        this.context = context;

        mCommonUtil = CommonUtil.getSingleton();
        webServIntent = new Intent(context, WebService.class);

        Intent wsServIntent = new Intent(WSService.ACTION);
        context.startService(wsServIntent);

        WSReceiver.register(context, new OnWsListener() {
            @Override
            public void onServAvailable() {

            }

            @Override
            public void onServUnavailable() {

            }
        });
    }

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
            Log.i(TAG, "onStarted: ");
            if(startServer != null){
                startServer.onComplete("");
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

    public void setStartServerCallback(StartServer Callback) {
        this.startServer = Callback;
    }
}
