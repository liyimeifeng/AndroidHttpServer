package org.join.ws.ui;

import org.join.web.serv.R;
import org.join.ws.Constants.Config;
import org.join.ws.WSApplication;
import org.join.ws.receiver.OnWsListener;
import org.join.ws.receiver.WSReceiver;
import org.join.ws.serv.WebServer;
import org.join.ws.util.CommonUtil;
import org.join.zxing.CaptureActivity;
import org.join.zxing.Contents;
import org.join.zxing.Intents;
import org.join.zxing.encode.QRCodeEncoder;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.result.ParsedResultType;

import java.io.File;
import java.net.URL;

import socket.callback.StartClient;
import socket.callback.StartServer;
import socket.downloadUtil.FileDownloader;
import socket.downloadUtil.FileUtil;
import socket.downloadUtil.ReqCallBack;
import socket.downloadUtil.RequestManager;


/**
 * @brief 
 * @details If you want a totally web server, <a href="https://code.google.com/p/i-jetty/">i-jetty</a> may be your choice.
 * @author join
 */
@SuppressWarnings("deprecation")
public class WSActivity extends WebServActivity implements OnClickListener, OnWsListener {

    static final String TAG = "WSActivity";
    static final boolean DEBUG = false || Config.DEV_MODE;

    private CommonUtil mCommonUtil;

    private ToggleButton serverButton;
    private Button clientButton;
    private TextView urlText;
    private ImageView qrCodeView;
    private LinearLayout contentLayout;

    private String ipAddr;

    private boolean needResumeServer = false;

    private static final int W_START = 0x0101;
    private static final int W_STOP = 0x0102;
    private static final int W_ERROR = 0x0103;

    private static final int DLG_SERV_USELESS = 0x0201;
    private static final int DLG_PORT_IN_USE = 0x0202;
    private static final int DLG_TEMP_NOT_FOUND = 0x0203;
    private static final int DLG_SCAN_RESULT = 0x0204;

    private static final int REQ_CAPTURE = 0x0001;
    private String lastResult;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case W_START: {
                setUrlText(ipAddr);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) serverButton
                        .getLayoutParams();
                params.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                contentLayout.setVisibility(View.VISIBLE);
                break;
            }
            case W_STOP: {
                urlText.setText("");
                qrCodeView.setImageResource(0);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) serverButton
                        .getLayoutParams();
                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                contentLayout.setVisibility(View.GONE);
                break;
            }
            case W_ERROR:
                switch (msg.arg1) {
                case WebServer.ERR_PORT_IN_USE: {
                    showDialog(DLG_PORT_IN_USE);
                    break;
                }
                case WebServer.ERR_TEMP_NOT_FOUND: {
                    showDialog(DLG_TEMP_NOT_FOUND);
                    break;
                }
                case WebServer.ERR_UNEXPECT:
                default:
                    Log.e(TAG, "ERR_UNEXPECT");
                    break;
                }
                doStopClick();
                return;
            }
            serverButton.setEnabled(true);
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initObjs(savedInstanceState);
        initViews(savedInstanceState);

        WSApplication.getInstance().startWsService();
        WSReceiver.register(this, this);
    }

    private void initObjs(Bundle state) {
        mCommonUtil = CommonUtil.getSingleton();
    }

    private void initViews(Bundle state) {
        serverButton = (ToggleButton) findViewById(R.id.toggleBtn);
        serverButton.setOnClickListener(this);
        clientButton = (Button)findViewById(R.id.startClenit);
        clientButton.setOnClickListener(this);
        urlText = (TextView) findViewById(R.id.urlText);
        qrCodeView = (ImageView) findViewById(R.id.qrCodeView);
        contentLayout = (LinearLayout) findViewById(R.id.contentLayout);

        if (state != null) {
            ipAddr = state.getString("ipAddr");
            needResumeServer = state.getBoolean("needResumeServer", false);
            boolean isRunning = state.getBoolean("isRunning", false);
            if (isRunning) {
                serverButton.setChecked(true);
                setUrlText(ipAddr);
                doBindService();
            }
        }
    }


    private void setUrlText(String ipAddr) {
        String url = "http://" + ipAddr + ":" + Config.PORT + "/";
        urlText.setText(url);
        generateQRCode(url);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("ipAddr", ipAddr);
        outState.putBoolean("needResumeServer", needResumeServer);
        boolean isRunning = webService != null && webService.isRunning();
        outState.putBoolean("isRunning", isRunning);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (DEBUG)
            Log.d(TAG,
                    newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ? "ORIENTATION_LANDSCAPE"
                            : "ORIENTATION_PORTRAIT");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WSReceiver.unregister(this);
        WSApplication.getInstance().stopWsService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_scan_barcode:
            toCaptureActivity();
            break;
        case R.id.action_preferences:
            toPreferActivity();
            break;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CAPTURE) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra(Intents.Scan.RESULT);
                ParsedResultType type = ParsedResultType.values()[data.getIntExtra(
                        Intents.Scan.RESULT_TYPE, ParsedResultType.TEXT.ordinal())];
                boolean isShow = false;
                try {
                    if (type == ParsedResultType.URI) {
                        toBrowserActivity(result);
                    } else {
                        isShow = true;
                    }
                } catch (ActivityNotFoundException e) {
                    isShow = true;
                } finally {
                    lastResult = result;
                    if (isShow)
                        showDialog(DLG_SCAN_RESULT);
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.toggleBtn:
                Log.i(TAG, "========点击了启动服务端按钮");
                boolean isChecked = serverButton.isChecked();
                if (isChecked) {
                    Log.i(TAG, "isWedSerAvailable======>" + isWebServAvailable());
                    if (!isWebServAvailable()) {
                        serverButton.setChecked(false);
                        urlText.setText("");
                        showDialog(DLG_SERV_USELESS);
                        return;
                    }
                    doStartClick();
                } else {
                    doStopClick();
                }
                needResumeServer = false;
            case R.id.startClenit:
                Log.i(TAG, "=========打开客户端");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                      final int result = new FileDownloader().downloadFile("http://10.10.30.153:8080/dodownload?fname=client.rar");
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (result == 0){
//                                    Toast.makeText(WSActivity.this,"文件下载成功,请到" + FileUtil.SDPATH + FileDownloader.dirName + "文件目录下查看",Toast.LENGTH_LONG).show();
//                                }else if (result == 1){
//                                    Toast.makeText(WSActivity.this,"文件已经存在，请删除后再下载",Toast.LENGTH_LONG).show();
//                                }else {
//                                    Toast.makeText(WSActivity.this,"文件下载失败，请查看服务器是否打开、网络是否异常",Toast.LENGTH_LONG).show();
//                                }
//                            }
//                        });



                        final RequestManager manager = new RequestManager(WSActivity.this);
                        String url = "http://10.10.30.153:8080/dodownload?fname=client.rar";
                        manager.downLoadFile(url, new ReqCallBack<Object>() {
                            @Override
                            public void onReqFailed(String errorMsg) {
                                Log.i("================", "onReqFailed:====下载失败 " + errorMsg);
                            }

                            @Override
                            public void onReqSuccess(Object result) {
                                Toast.makeText(WSActivity.this,"文件下载成功,请到" + RequestManager.destFileDir + "文件目录下查看",Toast.LENGTH_LONG).show();
                            }
                        });



                    }
                }).start();

        }

    }

    private void getMsgFromSocketServer(){
        Log.i(TAG, "接收到socket服务端请求开启之后打开http服务端或者http客户端");
        onClick(serverButton);
        onClick(clientButton);
    }

    /**
     *  服务端完成启动后进行回调通知SocketServer
     *  客户端完成启动后进行回调通知SocketServer
     */

    public WSActivity() {

    }

    private StartServer startServer;
    private StartClient startClient;


    public void setStartServerCallback(StartServer Callback) {
        this.startServer = Callback;
    }

    public void setStartClientCallback(StartClient callback){
        this.startClient = callback;
    }

    private void success(){
        startServer.onComplete("HTTP服务器已打开");
        startClient.onComplete("Http下载客户端已打开");
    }

    private void doStartClick() {
    	 WifiManager wifiManager = (WifiManager)this.getSystemService(Context.WIFI_SERVICE) ;
         WifiInfo wifiInfo = wifiManager.getConnectionInfo();
         int ipAddress = wifiInfo.getIpAddress();
         final String formatedIpAddress = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        ipAddr = formatedIpAddress;
        if (ipAddr == null) {
            serverButton.setChecked(false);
            urlText.setText("");
            toast(getString(R.string.info_net_off));
            return;
        }
        serverButton.setChecked(true);
        serverButton.setEnabled(false);
        doBindService();
    }

    private void doStopClick() {
        serverButton.setChecked(false);
        serverButton.setEnabled(false);
        doUnbindService();
        ipAddr = null;
    }

    private boolean isWebServAvailable() {
        return mCommonUtil.isNetworkAvailable() && mCommonUtil.isExternalStorageMounted();
    }

    @Override
    public void onStarted() {
        mHandler.sendEmptyMessage(W_START);
    }

    @Override
    public void onStopped() {
        mHandler.sendEmptyMessage(W_STOP);
    }

    @Override
    public void onError(int code) {
        Message msg = mHandler.obtainMessage(W_ERROR);
        msg.arg1 = code;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onServAvailable() {
        if (needResumeServer) {
            doStartClick();
            needResumeServer = false;
        }
    }

    @Override
    public void onServUnavailable() {
        if (webService != null && webService.isRunning()) {
            doStopClick();
            needResumeServer = true;
        }
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    // DialogFragment needs android-support.jar in API-8.
    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        switch (id) {
        case DLG_SERV_USELESS:
            return createConfirmDialog(android.R.drawable.ic_dialog_info,
                    R.string.tit_serv_useless, R.string.msg_serv_useless, null);
        case DLG_PORT_IN_USE:
            return createConfirmDialog(android.R.drawable.ic_dialog_info, R.string.tit_port_in_use,
                    R.string.msg_port_in_use, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            toPreferActivity();
                        }
                    });
        case DLG_TEMP_NOT_FOUND:
            return createConfirmDialog(android.R.drawable.ic_dialog_info,
                    R.string.tit_temp_not_found, R.string.tit_temp_not_found,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            toPreferActivity();
                        }
                    });
        case DLG_SCAN_RESULT:
            AlertDialog dialog = createConfirmDialog(android.R.drawable.ic_dialog_info,
                    R.string.tit_scan_result, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            copy2Clipboard(lastResult);
                        }
                    });
            dialog.setMessage(lastResult);
            return dialog;
        }
        return super.onCreateDialog(id, args);
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
        switch (id) {
        case DLG_SCAN_RESULT:
            ((AlertDialog) dialog).setMessage(lastResult);
            break;
        }
        super.onPrepareDialog(id, dialog, args);
    }

    private AlertDialog createConfirmDialog(int iconId, int titleId, int messageId,
            DialogInterface.OnClickListener positiveListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (iconId > 0)
            builder.setIcon(iconId);
        if (titleId > 0)
            builder.setTitle(titleId);
        if (messageId > 0)
            builder.setMessage(messageId);
        builder.setPositiveButton(android.R.string.ok, positiveListener);
        return builder.create();
    }

    private void toPreferActivity() {
        try {
            Intent intent = new Intent(this, PreferActivity.class);
            intent.putExtra("isRunning", webService == null ? false : webService.isRunning());
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void toCaptureActivity() {
        try {
            Intent intent = new Intent(this, CaptureActivity.class);
            intent.setAction(Intents.Scan.ACTION);
            startActivityForResult(intent, REQ_CAPTURE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void toBrowserActivity(String uri) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
    }

    private void copy2Clipboard(String text) {
        ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        cm.setText(text);
    }

    private void generateQRCode(String text) {
        Intent intent = new Intent(Intents.Encode.ACTION);
        intent.putExtra(Intents.Encode.FORMAT, BarcodeFormat.QR_CODE.toString());
        intent.putExtra(Intents.Encode.TYPE, Contents.Type.TEXT);
        intent.putExtra(Intents.Encode.DATA, text);
        try {
            int dimension = getDimension();
            QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(this, intent, dimension, false);
            Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
            if (bitmap == null) {
                Log.w(TAG, "Could not encode barcode");
            } else {
                qrCodeView.setImageBitmap(bitmap);
            }
        } catch (WriterException e) {
        }
    }

    private int getDimension() {
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        int dimension = width < height ? width : height;
        dimension = dimension * 3 / 4;
        return dimension;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        String title = "请注意";
        String message = "服务器正在运行，确定要关闭它吗？";
        if (keyCode == KeyEvent.KEYCODE_BACK){
            new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .setNegativeButton(getResources().getString(android.R.string.cancel),null)
                    .show();
        }
        return false;
    }
}