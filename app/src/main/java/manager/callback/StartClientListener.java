package com.socket.manager.callback;

/**
 * Created by Lee on 2017/3/16 0016.
 *
 * 启动客户端成功的接口回调，通知Socket
 */

public interface StartClientListener {
    void onComplete(boolean result);
}
