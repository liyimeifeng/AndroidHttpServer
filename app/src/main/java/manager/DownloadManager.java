package com.socket.manager;

import android.content.Context;
import android.util.Log;

import com.socket.manager.callback.DownloadResultListener;
import com.socket.manager.callback.StartDownloadListener;
import com.socket.manager.downloadUtil.ReqCallBack;
import com.socket.manager.downloadUtil.RequestManager;
import com.socket.org.join.ws.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Http服务下载工具类
 *
 * Created by Lee on 2017/3/21 0021.
 */

public class DownloadManager {


    private final static String TAG = "DownloadManager";
    private Context context;
    private DownloadResultListener downloadResultListener;
    private StartDownloadListener startDownloadListener;
    private List<String> sourFileList;

    public DownloadManager(Context context){
        this.context = context;
    }


    /**准备下载
     *
     * @param targetFileDir  目标文件目录
     * @param serverIpAdd    Http服务器Ip
     * @return
     */
    public DownloadManager startDownload(final String targetFileDir,final String serverIpAdd){
            new Thread(new Runnable() {
            @Override
            public void run() {
                RequestManager requestManager = RequestManager.getInstance(context);
                startDownloadListener.startDownload(true);
                if (sourFileList != null){
                    for (String file : sourFileList){
                        requestManager.downLoadFile( file ,targetFileDir ,serverIpAdd, new ReqCallBack<String>() {
                            @Override
                            public void onReqFailed(String errorMsg) {
                                downloadResultListener.onComplete(false,errorMsg);
                            }
                            @Override
                            public void onReqSuccess(String result) {
                                /**
                                 * 下载成功返回文件绝对路径
                                 */
                                downloadResultListener.onComplete(true,result );
                            }
                        });
                    }
                }
            }
        }).start();
        return DownloadManager.this;
    }

    /**
     * 下载结果监听器
     * @param Callback
     */
    public void setDownloadResultListener(DownloadResultListener Callback) {
        this.downloadResultListener = Callback;
    }

    /**
     * 开始下载监听器
     * @param callback
     * @return
     */
    public DownloadManager setStartDownloadListener(StartDownloadListener callback){
        this.startDownloadListener = callback;
        return this;
    }

    /**
     * 设置要下载的源文件列表，传入多个源文件源
     * @param sourceFileList
     * @return
     */
    public DownloadManager setSourceFileList(List<String>  sourceFileList){
        this.sourFileList = sourceFileList;
        return this;
    }


}
