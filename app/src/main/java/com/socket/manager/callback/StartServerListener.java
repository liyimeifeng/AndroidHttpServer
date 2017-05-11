package com.socket.manager.callback;

/**
 * Created by Lee on 2017/3/16 0016.
 *
 * http服务器打开成功后的接口回调
 */

public interface StartServerListener {
    void onComplete(boolean result);
}
