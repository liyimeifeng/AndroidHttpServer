package socket.callback;

import android.util.Log;

import org.apache.http.protocol.HttpRequestHandler;
import org.join.ws.serv.req.HttpDownHandler;
import org.join.ws.serv.req.HttpFBHandler;
import org.join.ws.ui.WSActivity;


import socket.info.StartInfo;

import static android.content.ContentValues.TAG;

/**
 * Created by Lee on 2017/3/17 0017.
 */

public class SocketTest {


    public void test(){
        StartInfo info = new StartInfo();
       info.setOpen(true);
//
        WSActivity wsActivity = new WSActivity();
        wsActivity.setStartServerCallback(new StartServer() {
            @Override
            public void complete(String info) {
//                Log.i(TAG, "complete: ====》" + wsActivity.getMsg());
                Log.i(TAG, "complete: ====》" +info);

            }
        });


        HttpDownHandler downHandler = new HttpDownHandler();

        downHandler.setDownloadSuccessCallback(new DownloadSuccessful() {
            @Override
            public void complete(String msg) {

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
            public void complete(String msg) {

            }
        });
    }





}