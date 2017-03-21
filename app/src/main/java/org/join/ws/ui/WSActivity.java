package org.join.ws.ui;

import org.join.web.serv.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import socket.downloadUtil.ReqCallBack;
import socket.downloadUtil.RequestManager;


/**
 * @brief 
 * @details If you want a totally web server, <a href="https://code.google.com/p/i-jetty/">i-jetty</a> may be your choice.
 * @author join
 */
@SuppressWarnings("deprecation")
public class WSActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        Button startServer = (Button) findViewById(R.id.startServer);
        startServer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

//                CommonUtil.getSingleton();
                ServerManager serverManager = new ServerManager();
                serverManager.init(WSActivity.this);
                serverManager.startHttpServer();
            }
        });

        Button startClient = (Button) findViewById(R.id.startClient);
        startClient.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });

    }
}