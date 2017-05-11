package com.socket.manager;

import android.content.Context;

import com.socket.manager.callback.StartClientListener;

/**
 * Http客户端开启工具类
 *
 * Created by HYY on 2017/3/21.
 */

public class ClientManager {

    private Context context;
    private StartClientListener startClientListener;

    public ClientManager (Context context){
        this.context = context;
    }

    public void  startClient(){
        /***
         * 开启客户端
         *
         */
        startClientListener.onComplete(true);
//        return this;
    }

    public ClientManager setStartClientListern(StartClientListener callback){
        this.startClientListener =callback;
        return ClientManager.this;
    }

}
