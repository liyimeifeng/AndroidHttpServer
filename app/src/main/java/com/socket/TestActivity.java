package com.socket;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.socket.manager.ClientManager;
import com.socket.manager.DownloadManager;
import com.socket.manager.ServerManager;
import com.socket.manager.callback.DownloadResultListener;
import com.socket.manager.callback.StartClientListener;
import com.socket.manager.callback.StartDownloadListener;
import com.socket.manager.callback.StartServerListener;
import com.socket.org.join.ws.receiver.WSReceiver;

import java.util.Arrays;

public class TestActivity extends Activity {

    private Button startServer,startClient,doDownload;
    private final static String TAG = " TestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        startServer = (Button)findViewById(R.id.startserver);
        startClient = (Button)findViewById(R.id.startClient);
        doDownload = (Button)findViewById(R.id.startDownload);
        final Context context = TestActivity.this;


        startServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 打开Http服务器，进行初始化
                 */
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        ServerManager manager = ServerManager.getInstant();
                        manager.init(TestActivity.this);
                        manager.setStartServerListener(new StartServerListener() {
                            @Override
                            public void onComplete(boolean result) {
                                Log.i(TAG, "Http服务器是否打开 ------->" + result);
                            }
                        });
                        Looper.loop();
                    }
                }).start();

            }
        });

        startClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 开启Http客户端
                 */
                ClientManager clientManager =new ClientManager(TestActivity.this);
                clientManager
                        .setStartClientListern(new StartClientListener() {
                    @Override
                    public void onComplete(boolean result) {
                        Log.i(TAG, "Http客户端是否打开: ----->" + result);
                        /**
                         * 客户端打开成功，成功返回true
                         */
                    }
                })
                        .startClient();
            }
        });

        doDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 执行下载，先判断是否开始下载，过一段时间判断是否下载完成
                 * @parmas sourceFilePath  源文件路径，eg：/storage/emulated/0/
                 * @parmas  ip              Http服务器ip
                 * @params  targerFileDir  存储下载的文件路径，eg：/storage/emulated/0//
                 *
                 */
                DownloadManager manager =  new DownloadManager(context);

                manager.setStartDownloadListener(new StartDownloadListener() {
                            @Override
                            public void startDownload(boolean result) {
                                /**
                                 * 开始执行下载，如开始返回true
                                 */
                                Log.i(TAG, "startDownload: ------>" + result);
                                Log.i(TAG, "当前所在线程" + Thread.currentThread().getName() );

                            }
                        })
                        /**
                         * 设置要下载的源文件路径，接受List类型
                         */
                        .setSourceFileList(Arrays.asList("/storage/emulated/0/测试/视频.mp4"))

//                       .setSourceFileList(Arrays.asList("/storage/emulated/0/client.rar", "/storage/emulated/0/fade.mp3", "/storage/emulated/0/Download/movie.mp4"))
                        .startDownload("/storage/emulated/0/WWWWWWW/","10.10.30.235")      //需要目标文件目录以及Http服务器Ip
                        .setDownloadResultListener(new DownloadResultListener() {
                            @Override
                            public void onComplete(boolean result, String msg) {
                                /**
                                 *查看下载结果， 成功result为true，返回文件所在目录
                                 */
                                Log.i(TAG, "onComplete: " + result + "====>" + msg);
                            }
                        });
            }
        });

    }
}
