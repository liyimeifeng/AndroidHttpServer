package socket.callback;

/**
 * Created by Lee on 2017/3/16 0016.
 *
 * http服务器打开成功后的回调
 */

public interface StartServer {
    void onComplete(String msg);
}
