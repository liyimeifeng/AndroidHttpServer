package socket.callback;

/**
 * Created by Lee on 2017/3/16 0016.
 *
 * 启动客户端成功的回调，通知Socket
 */

public interface StartClient {
    void onComplete(String msg);
}
