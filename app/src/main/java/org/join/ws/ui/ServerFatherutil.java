//package org.join.ws.ui;
//
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.content.ServiceConnection;
//import android.os.Bundle;
//import android.os.IBinder;
//
//import org.join.ws.WSApplication;
//import org.join.ws.serv.WebServer;
//import org.join.ws.service.WebService;
//
//import static android.content.Context.BIND_AUTO_CREATE;
//
///**
// * Created by Lee on 2017/3/21 0021.
// */
//
//public class ServerFatherUtil implements WebServer.OnWebServListener {
//
//
//    protected Intent webServIntent;
//    protected WebService webService;
//    private boolean isBound = false;
//
//    private ServiceConnection servConnection = new ServiceConnection() {
//
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            webService = ((WebService.LocalBinder) service).getService();
//            webService.setOnWebServListener(ServerFatherUtil.this);
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            webService = null;
//        }
//    };
//
//    protected void onCreate() {
//        webServIntent = new Intent(ServerFatherUtil.this, WebService.class);
//    }
//
//    @Override
//    public void onError(int code) {
//
//    }
//
//    @Override
//    public void onStarted() {
//
//    }
//
//    @Override
//    public void onStopped() {
//
//    }
//
//    protected boolean isBound() {
//        return this.isBound;
//    }
//
//    protected void doBindService() {
//        // Restore configs of port and root here.
//        PreferActivity.restore(PreferActivity.KEY_SERV_PORT, PreferActivity.KEY_SERV_ROOT);
//        WSApplication.getInstance().bindService(webServIntent, servConnection, BIND_AUTO_CREATE);
//        isBound = true;
//    }
//
//    protected void doUnbindService() {
//        if (isBound) {
//            WSApplication.getInstance().unbindService(servConnection);
//            isBound = false;
//        }
//    }
//
//    protected void onDestroy() {
//        doUnbindService();
//    }
//
//}
