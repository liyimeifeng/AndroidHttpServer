package socket.callback;

/**
 * Created by Lee on 2017/3/16 0016.
 *
 * 下载成功的回调,通知Socket
 */

public interface DownloadSuccessful {
    void complete(String msg);
}
