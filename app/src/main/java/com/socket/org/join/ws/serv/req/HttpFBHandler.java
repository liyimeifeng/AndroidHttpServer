package com.socket.org.join.ws.serv.req;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import com.socket.manager.callback.StartClientListener;
import com.socket.org.join.ws.serv.req.objs.FileRow;
import com.socket.org.join.ws.serv.support.Progress;
import com.socket.org.join.ws.serv.view.ViewFactory;
import com.socket.org.join.ws.util.CommonUtil;

import com.socket.org.join.ws.Constants;

/**
 * @brief 目录浏览页面请求处理
 * @author join
 */
public class HttpFBHandler implements HttpRequestHandler {

    private CommonUtil mCommonUtil = CommonUtil.getSingleton();
    private ViewFactory mViewFactory = ViewFactory.getSingleton();
    private final static String TAG = "HttpFBHandler";

    private String webRoot;

    public HttpFBHandler(final String webRoot) {
        this.webRoot = webRoot;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context)
            throws HttpException, IOException {
        //解析请求Uri处理中文乱码的情况
        String encodeUri = URLDecoder.decode(request.getRequestLine().getUri(), Constants.Config.ENCODING);
        String Method  = request.getRequestLine().getMethod();
        String Uri = request.getRequestLine().getUri();
        Log.i(TAG, "encodeUri-------> " +encodeUri + "--request----->" + request + "--headers---" + request.getAllHeaders() + "---Method-->" + Method + "--Uri---->" + Uri);

        String rangeHeader = "";
        Header[] head1 = request.getAllHeaders();
        for (Header ha:head1){
            Log.e(TAG, "requestheaders: ======>" + ha.getName() + " =======" + ha.getValue());
        }

        File file;
        if (encodeUri.equals("/")) {//uri只有一个正斜杠的情况下打开主目录
            file = new File(this.webRoot);
        } else if (!encodeUri.startsWith(Constants.Config.SERV_ROOT_DIR) && !encodeUri.startsWith(this.webRoot)) {
            response.setStatusCode(HttpStatus.SC_FORBIDDEN);
            response.setEntity(resp403(request));
            return;
        } else {
            file = new File(encodeUri);
        }

        HttpEntity entity;
        String contentType = "text/html;charset=" + Constants.Config.ENCODING;
        if (!file.exists()) { // 不存在
            response.setStatusCode(HttpStatus.SC_NOT_FOUND);
            entity = resp404(request);
        } else if (file.canRead()) { // 可读
            response.setStatusCode(HttpStatus.SC_OK);
            if (file.isDirectory()) {
                entity = respView(request, file);
            } else {
                entity = respFile(request, file);
                contentType = entity.getContentType().getValue();
            }
        } else { // 不可读
            response.setStatusCode(HttpStatus.SC_FORBIDDEN);
            entity = resp403(request);
        }

        long fileSize = entity.getContentLength();
        long start = 0;
        if (null != rangeHeader && rangeHeader.startsWith("bytes=")) {
            String[] values = rangeHeader.split("=")[1].split("-");
            start = Integer.parseInt(values[0]);
        }
        response.setHeader("Content-Type", contentType);
        response.setHeader("RANGE",rangeHeader);
        response.setHeader("Accept-Ranges","bytes");
        response.setHeader("Content-Range", "bytes " + start + "-" + (fileSize - 1) + "/" + fileSize);
        response.setEntity(entity);

        Progress.clear();
    }


    private HttpEntity respFile(HttpRequest request, File file) throws IOException {
        return mViewFactory.renderFile(request, file);
    }

    private HttpEntity resp403(HttpRequest request) throws IOException {
        return mViewFactory.renderTemp(request, "403.html");
    }

    private HttpEntity resp404(HttpRequest request) throws IOException {
        return mViewFactory.renderTemp(request, "404.html");
    }

    private HttpEntity respView(HttpRequest request, File dir) throws IOException {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("dirpath", dir.getPath()); // 目录路径
        data.put("hasParent", !isSamePath(dir.getPath(), this.webRoot)); // 是否有上级目录
        data.put("fileRows", buildFileRows(dir)); // 文件行信息集合
        return mViewFactory.renderTemp(request, "view.html", data);
    }

    private boolean isSamePath(String a, String b) {
        String left = a.substring(b.length(), a.length()); // a以b开头
        if (left.length() >= 2) {
            return false;
        }
        if (left.length() == 1 && !left.equals("/")) {
            return false;
        }
        return true;
    }

    private List<FileRow> buildFileRows(File dir) {
        File[] files = dir.listFiles(); // 目录列表
        if (files != null) {
            sort(files); // 排序
            ArrayList<FileRow> fileRows = new ArrayList<FileRow>();
            for (File file : files) {
                fileRows.add(buildFileRow(file));
            }
            return fileRows;
        }
        return null;
    }

    private SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd ahh:mm");

    private FileRow buildFileRow(File f) {
        boolean isDir = f.isDirectory();
        String clazz, name, link, size;
        if (isDir) {
            clazz = "icon dir";
            name = f.getName() + "/";
            link = f.getPath() + "/";
            size = "";
        } else {
            clazz = "icon file";
            name = f.getName();
            link = f.getPath();
            size = mCommonUtil.readableFileSize(f.length());
        }
        FileRow row = new FileRow(clazz, name, link, size);
        row.time = sdf.format(new Date(f.lastModified()));
        if (f.canRead()) {
            row.can_browse = true;
            if (Constants.Config.ALLOW_DOWNLOAD) {
                row.can_download = true;
            }
            if (f.canWrite() && !hasWsDir(f)) {
                if (Constants.Config.ALLOW_DELETE) {
                    row.can_delete = true;
                }
                if (Constants.Config.ALLOW_UPLOAD && isDir) {
                    row.can_upload = true;
                }
            }
        }
        return row;
    }

    private boolean hasWsDir(File f) {
//        return HttpDelHandler.hasWsDir(f);
        String path = f.isDirectory() ? f.getAbsolutePath() + "/" : f.getAbsolutePath();
        return path.indexOf(Constants.APP_DIR_NAME) != -1;
    }

    /** 排序：文件夹、文件，再各安字符顺序 */
    private void sort(File[] files) {
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                if (f1.isDirectory() && !f2.isDirectory()) {
                    return -1;
                } else if (!f1.isDirectory() && f2.isDirectory()) {
                    return 1;
                } else {
                    return f1.toString().compareToIgnoreCase(f2.toString());
                }
            }
        });
    }

}
