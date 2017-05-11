package com.socket.org.join.ws;

import java.io.IOException;



import com.socket.org.join.ws.serv.TempCacheFilter;
import com.socket.org.join.ws.util.CopyUtil;

import android.app.Application;
import android.content.Intent;

/**
 * @brief 应用全局
 * @author join
 */
public class WSApplication extends Application {

    private static WSApplication self;

    private Intent wsServIntent;

    @Override
    public void onCreate() {
        super.onCreate();

        self = this;
//        Log.i(TAG, "onCreate: ===========》启动后台服务");
//        wsServIntent = new Intent(WSService.ACTION);
//
//        initAppDir();
//        initJangod();
//        initAppFilter();
//
//        if (!Config.DEV_MODE) {
//            /* 全局异常崩溃处理 */
//            new CrashHandler(this);
//        }
//
//        PreferActivity.restoreAll();
//        serverUtil = new ServerUtil();
////        WSApplication.getInstance().startWsService();
//            serverUtil.initObject();
//        serverUtil.startHttpServer();
    }

    public static WSApplication getInstance() {
        return self;
    }

    /**
     * @brief 开启全局服务
     */
    public void startWsService() {
        startService(wsServIntent);
    }

    /**
     * @brief 停止全局服务
     */
    public void stopWsService() {
        stopService(wsServIntent);
    }

    /**
     * @brief 初始化应用目录
     */
    private void initAppDir() {
        CopyUtil mCopyUtil = new CopyUtil(getApplicationContext());
        // mCopyUtil.deleteFile(new File(Config.SERV_ROOT_DIR)); // 清理服务文件目录
        try {
            // 重新复制到SDCard，仅当文件不存在时
            mCopyUtil.assetsCopy("ws", Constants.Config.SERV_ROOT_DIR, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @brief 初始化Jangod，添加自定义内容
     */
    private void initJangod() {
        /* custom tags */
//        TagLibrary.addTag(new ResStrTag());
//        TagLibrary.addTag(new ResColorTag());
//        TagLibrary.addTag(new UUIDTag());
        /* custom filters */
    }

    /**
     * @brief 初始化应用过滤器
     */
    private void initAppFilter() {
        /* TempCacheFilter */
        TempCacheFilter.addCacheTemps("403.html", "404.html", "503.html");
        /* GzipFilter */
    }

}
