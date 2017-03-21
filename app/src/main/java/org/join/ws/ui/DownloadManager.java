package org.join.ws.ui;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import socket.callback.DownloadSuccessful;
import socket.callback.StartServer;
import socket.downloadUtil.ReqCallBack;
import socket.downloadUtil.RequestManager;

/**
 * Created by Lee on 2017/3/21 0021.
 */

public class DownloadManager {


    private Context context;
    private DownloadSuccessful downloadSuccessful;

    public void DownloadManager(Context context){
        this.context = context;
    }

    public void download(){
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



                final RequestManager manager = new RequestManager(context);
                String url = "http://10.10.30.153:8080/dodownload?fname=client.rar";
                manager.downLoadFile(url, new ReqCallBack<Object>() {
                    @Override
                    public void onReqFailed(String errorMsg) {
                        Log.i("================", "onReqFailed:====下载失败 " + errorMsg);
                    }

                    @Override
                    public void onReqSuccess(Object result) {
                        Toast.makeText(context,"文件下载成功,请到" + RequestManager.destFileDir + "文件目录下查看",Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).start();
    }

    public void setStartServerCallback(DownloadSuccessful Callback) {
        this.downloadSuccessful = Callback;
    }

}
