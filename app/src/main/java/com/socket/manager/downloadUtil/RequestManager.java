package com.socket.manager.downloadUtil;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.socket.manager.callback.StartDownloadListener;
import com.socket.org.join.ws.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/3/19.
 */
public class RequestManager {
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");//mdiatype 这个需要和服务端保持一致
    private static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");//mdiatype 这个需要和服务端保持一致
    private static final String TAG = RequestManager.class.getSimpleName();
    private static final String BASE_URL = "http://xxx.com/openapi";//请求接口根地址
    private static volatile RequestManager mInstance;//单利引用
    public static final int TYPE_GET = 0;//get请求
    public static final int TYPE_POST_JSON = 1;//post请求参数为json
    public static final int TYPE_POST_FORM = 2;//post请求参数为表单
    private OkHttpClient mOkHttpClient;//okHttpClient 实例
    private Handler okHttpHandler;//全局处理子线程和M主线程通信

    /**
     * 初始化RequestManager
     */
    public RequestManager(Context context) {
        //初始化OkHttpClient
        mOkHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)//设置超时时间
                .readTimeout(10, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(10, TimeUnit.SECONDS)//设置写入超时时间
                .build();
        //初始化Handler
        okHttpHandler = new Handler(context.getMainLooper());
    }

    /**
     * 获取单例引用
     *
     * @return
     */
    public static RequestManager getInstance(Context context) {
        RequestManager inst = mInstance;
        if (inst == null) {
            synchronized (RequestManager.class) {
                inst = mInstance;
                if (inst == null) {
                    inst = new RequestManager(context.getApplicationContext());
                    mInstance = inst;
                }
            }
        }
        return inst;
    }


    /**
     * 将Url分割出要下载的文件名字，从等号处分割
     * @param url
     */
    public void split(String url){
        String s = new String(url);
        String a[] = s.split("=");
        fileName = a[1];
//        final File file = new File(destFileDir, fileName);
//        if (fileName.contains(".")){
//            Log.i(TAG, "fileName ======= >" + fileName + "======" + file.exists());
//            if (file.exists()){
//                String name = fileName.substring(0,fileName.indexOf("."));
//                String type = fileName.substring(fileName.indexOf("."),fileName.length());
//            }
//        }else{
//            Log.i(TAG, "文件不包含点号");
//            if (file.exists()){
//            }
//        }

    }

    public static String destFileDir= Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "Download/";
    String fileName = "";
    String sourFileDir = "";

    /**
     * 将源文件路径分割出目录和文件名
     * @param sourceFilePath
     */
    public void getFileNameAndDir(String sourceFilePath){
        int i = sourceFilePath.lastIndexOf("/");
        sourFileDir = sourceFilePath.substring(0,i+1);
        fileName = sourceFilePath.substring(i+1);
    }

    /**
     * 判断传入的目标文件目录是否存在，没有则新建
     * @param path
     */
    public void isDirExist(String path){
        File dir = new File(path);
        if (!dir.exists()){
            dir.mkdirs();
        }
    }


    /**下载文件
     * Url有两种写法，http://10.10.30.235:9999/storage/emulated/0/fade.mp3 ，采用此方式，不认Referer
     *              http://10.10.30.235:9999/dodownload?fname=fade.mp3 ，采用此方式，要认Referer后传入的目录（目前已经不可用）
     *
     * @param sourFilePath
     * @param targetFileDir
     * @param serverIP
     * @param callBack
     * @param <T>
     */
    public <T> void downLoadFile(String sourFilePath ,final String targetFileDir,String serverIP , final ReqCallBack<T> callBack) {
        getFileNameAndDir(sourFilePath);
        isDirExist(targetFileDir);
        String fileUrl = "http://" + serverIP+ ":"+ Constants.Config.PORT+ sourFilePath;
        final File file = new File(targetFileDir, fileName);

        if (file.exists()) {
            Log.i(TAG, "文件已存在的情况下，继续下载覆盖");
//            successCallBack((T) file, callBack);
//            return;
        }
        final Request request = new Request.Builder()
                // TODO: 2017/4/6  下面这句暂时注释，不添加Referer，避免中文路径下载失败的情况
//                .addHeader("Referer", "http://" + serverIP+ ":"+ Constants.Config.PORT + "/"  + sourFileDir)   //这句话目前正在测试阶段，待确定，暂定为使用目录
                .url(fileUrl).build();
        final Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, e.toString());
                failedCallBack("下载失败,请检查网络连接是否正常，服务器是否打开", callBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    long total = response.body().contentLength();
                    if (total == -1){
                        failedCallBack("下载失败，该文件不存在", callBack);
                        return;
                    }
                    Log.e(TAG, "文件大小total------>" + total);
                    long current = 0;
                    is = response.body().byteStream();
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        current += len;
                        fos.write(buf, 0, len);
                        Log.e(TAG, "current------>" + current);
                    }
                    fos.flush();
                    successCallBack(file.getAbsolutePath(), callBack);
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                    failedCallBack("下载失败,请检查网络连接是否正常，服务器是否打开", callBack);
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.toString());
                    }
                }
            }
        });
    }


    /**
     * 统一同意处理成功信息
     * @param result
     * @param callBack
     * @param <T>
     */
    private <T> void successCallBack(final String result, final ReqCallBack<T> callBack) {
        if (callBack != null) {
            callBack.onReqSuccess(result);
        }
//        okHttpHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                if (callBack != null) {
//                    callBack.onReqSuccess("");
//                }
//            }
//        });
    }

    /**
     * 统一处理失败信息
     * @param errorMsg
     * @param callBack
     * @param <T>
     */
    private <T> void failedCallBack(final String errorMsg, final ReqCallBack<T> callBack) {
        if (callBack != null) {
            callBack.onReqFailed(errorMsg);
        }

    }
}
