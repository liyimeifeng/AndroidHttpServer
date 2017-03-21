package socket.callback;

import android.util.Log;

import org.join.ws.serv.req.HttpDownHandler;
import org.join.ws.serv.req.HttpFBHandler;
import org.join.ws.ui.WSActivity;


import socket.info.StartServerInfo;

import static android.content.ContentValues.TAG;

/**
 * Created by Lee on 2017/3/17 0017.
 */

public class SocketTest {


    public void test(){
        StartServerInfo info = new StartServerInfo();
       info.setOpen(true);
//
        WSActivity wsActivity = new WSActivity();
        wsActivity.setStartServerCallback(new StartServer() {
            @Override
            public void onComplete(String info) {
//                Log.i(TAG, "complete: ====》" + wsActivity.getMsg());
                Log.i(TAG, "complete: ====》" +info);

            }
        });

        wsActivity.setStartClientCallback(new StartClient() {
            @Override
            public void onComplete(String msg) {
                Log.i(TAG, "onComplete:=====> " + msg);
            }
        });


        HttpDownHandler downHandler = new HttpDownHandler();

        downHandler.setDownloadSuccessCallback(new DownloadSuccessful() {
            @Override
            public void onComplete(String msg) {

            }
        });


        downHandler.setStartDownloadCallback(new StartDownload() {
            @Override
            public void doDownload() {

            }
        });


        HttpFBHandler httpFBHandler = new HttpFBHandler();
        httpFBHandler.setStartClientCallback(new StartClient() {
            @Override
            public void onComplete(String msg) {

            }
        });
    }





}
