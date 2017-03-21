//package org.join.ws.ui;
//
//import android.app.Application;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.content.ServiceConnection;
//import android.net.wifi.WifiInfo;
//import android.net.wifi.WifiManager;
//import android.os.IBinder;
//import android.text.LoginFilter;
//import android.util.Log;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.ToggleButton;
//
//import org.join.web.serv.R;
//import org.join.ws.Constants;
//import org.join.ws.WSApplication;
//import org.join.ws.receiver.OnWsListener;
//import org.join.ws.receiver.WSReceiver;
//import org.join.ws.serv.WebServer;
//import org.join.ws.service.WSService;
//import org.join.ws.service.WebService;
//import org.join.ws.util.CommonUtil;
//
//import socket.callback.DownloadSuccessful;
//import socket.callback.StartClient;
//import socket.callback.StartServer;
//
///**
// * Created by Lee on 2017/3/21 0021.
// *
// * @param flag ：标位置，0表示上屏，1表示下屏
// * @param start ：是否要打开http服务端，true表示打开，false表示不打开
// */
//
//public class ServerUtil extends ServerFatherUtil implements OnWsListener {
//
//    static final String TAG = "ServerUtil";
//    static final boolean DEBUG = false || Constants.Config.DEV_MODE;
//
//    private CommonUtil mCommonUtil;
//
//    private String ipAddr;
//    protected Intent webServIntent;
//    private boolean needResumeServer = false;
//    private String lastResult;
//
//    private boolean start;
//    private  int flag;
//    private Context context = WSApplication.getInstance();
//    private OnWsListener mListener;
//    private  static ServerUtil Intance;
//
//
//    public ServerUtil(Context context,boolean isToStart,int flag){
//        this.start = isToStart;
//        this.flag = flag;
//        this.context = context;
//    }
//
//    public  ServerUtil(){
//
//
//    };
//
////    public static ServerUtil getInstance(){
////        return SingletonHolder.sInstance;
////    }
//
//    public static ServerUtil getInstance(){
//        if(Intance == null){
//            Intance = new ServerUtil();
//        }
//        return Intance;
//    }
//
//    /**
//     * 静态内部类
//     */
//    private static class SingletonHolder{
//        private static final ServerUtil sInstance = new ServerUtil();
//    }
//
//    public void initObject(Context context){
////        Log.i(TAG, "initObject: ==========");
//        mCommonUtil = CommonUtil.getSingleton();
////        WSApplication.getInstance().startWsService();
//
//        Intent webServIntent = new Intent(context, WebService.class);
////        context.startService(webServIntent);
//
//
//
//        Intent wsServIntent = new Intent(WSService.ACTION);
//        context.startService(wsServIntent);
//
//        WSReceiver.register(context, null);
//
//        Log.i(TAG, "context=====" + context + "======" + mListener);
////        WSReceiver.register(context,);
//        Log.i(TAG, "initObject: --------------");
//    }
//
//    public void startHttpServer(){
//        Log.i(TAG, "startServer: =========>启动服务端");
//        if (!isWebServAvailable()) {
//            return;
//        }
//        doStartClick();
//        startServer.onComplete("HTTP服务器已打开");
//        context.bindService(webServIntent, new ServiceConnection() {
//            @Override
//            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//
//            }
//
//            @Override
//            public void onServiceDisconnected(ComponentName componentName) {
//
//                startServer.onComplete("HTTP服务器已打开");
//
//            }
//        }, 0);
//    }
//
//    private ServiceConnection servConnection = new ServiceConnection() {
//
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            webService = ((WebService.LocalBinder) service).getService();
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            webService = null;
//        }
//    };
//
//    protected void onDestroy() {
//        WSReceiver.unregister(context);
//        WSApplication.getInstance().stopWsService();
//        doUnbindService();
//
//    }
//
//    private boolean isWebServAvailable() {
//        return mCommonUtil.isNetworkAvailable() && mCommonUtil.isExternalStorageMounted();
//    }
//
//    private void doStartClick() {
//        WifiManager wifiManager = (WifiManager)this.context.getSystemService(Context.WIFI_SERVICE) ;
//        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//        int ipAddress = wifiInfo.getIpAddress();
//
//        Log.i(TAG, "ipAddress: ======>" + ipAddress);
//        final String formatedIpAddress = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
//        Log.i(TAG, "formatedIpaddress------->" + formatedIpAddress);
//        ipAddr = formatedIpAddress;
//        if (ipAddr == null) {
//            return;
//        }
////      ;
//    }
//
//
//    /**
//     *  服务端完成启动后进行回调通知SocketServer
//     *  客户端完成启动后进行回调通知SocketServer
//     */
//
//
//    private StartServer startServer;
//    private StartClient startClient;
//    private DownloadSuccessful downloadSuccessful;
//
//
//    public void setStartServerCallback(StartServer Callback) {
//        this.startServer = Callback;
//    }
//
//    public void setStartClientCallback(StartClient callback){
//        this.startClient = callback;
//    }
//
//    public void setDownloadSuccessfulCallback(DownloadSuccessful callback){
//        this.downloadSuccessful = callback;
//    }
//
//    private void success(){
//        startServer.onComplete("HTTP服务器已打开");
//        startClient.onComplete("Http下载客户端已打开");
//        downloadSuccessful.onComplete("下载完成");
//    }
//
//
//    @Override
//    public void onServAvailable() {
//        if (needResumeServer) {
//            doStartClick();
//            needResumeServer = false;
//        }
//    }
//
//    @Override
//    public void onServUnavailable() {
////        if (webService != null && webService.isRunning()) {
////            doStopClick();
////            needResumeServer = true;
////        }
//    }
//
////    @Override
////    public void onStopped() {
////
////    }
////
////    @Override
////    public void onStarted() {
////
////    }
////
////    @Override
////    public void onError(int code) {
////
////    }
//}
