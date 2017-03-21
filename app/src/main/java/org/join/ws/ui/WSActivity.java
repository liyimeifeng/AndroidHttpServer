package org.join.ws.ui;

import org.join.web.serv.R;

import android.app.*;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import socket.callback.DownloadSuccessful;
import socket.downloadUtil.ReqCallBack;
import socket.downloadUtil.RequestManager;


/**
 * @brief 
 * @details If you want a totally web server, <a href="https://code.google.com/p/i-jetty/">i-jetty</a> may be your choice.
 * @author join
 */
@SuppressWarnings("deprecation")
public class WSActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        Button startServer = (Button) findViewById(R.id.startServer);
        startServer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

//                CommonUtil.getSingleton();
                ServerManager serverManager = new ServerManager();
                serverManager.init(WSActivity.this);
                serverManager.startHttpServer();
            }
        });

        Button startClient = (Button) findViewById(R.id.startClient);
        startClient.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                DownloadManager downloadManager = new DownloadManager(WSActivity.this);
                downloadManager.download();

            }
        });

    }
}