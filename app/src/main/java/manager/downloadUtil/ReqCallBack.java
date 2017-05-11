package com.socket.manager.downloadUtil;

/**
 * Created by Administrator on 2017/3/19.
 */
public interface ReqCallBack<T>{

        /**
         * 响应成功
         */
        void onReqSuccess(String result);

        /**
         * 响应失败
         */
        void onReqFailed(String errorMsg);

}
