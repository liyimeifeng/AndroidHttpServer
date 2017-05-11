package com.socket.manager.callback;

/**
 * Created by Lee on 2017/3/16 0016.
 *
 * 下载成功的接口回调,通知Socket
 *
 */

public interface DownloadResultListener {
    void onComplete(boolean result,String msg);
}
