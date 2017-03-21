package socket.downloadUtil;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import static android.content.ContentValues.TAG;

/**
 * Created by Administrator on 2017/3/19.
 */
public class FileDownloader {

    private FileUtil fileUtil=null;
    public FileDownloader(){
        this.fileUtil=new FileUtil();
    }

    /**
     * 下载指定路径的文件，并写入到指定的位置
     * @param dirName
     * @param fileName
     * @param urlStr
     * @return 返回0表示下载成功，返回1表示文件已经在指定位置存在，返回2表示下载出错
     */
    public final static String dirName = "Download/";
    private String fileName = "";

    public int downloadFile(String urlStr){
        split(urlStr);
        if(fileUtil.isExist(dirName,fileName)){
            return 1;
        }
        File file=fileUtil.IS2SD(fileUtil.getIS(urlStr), dirName, fileName);
        if(file.length()==0  || file==null){
            return 2;
        }
        return 0;
    }

    /**
     * 将Url分割出文件名，在本地创建目录以及相同文件,,如果文件已经存在则重新建一个文件（文件名加1）
     * @param url
     */

    private int m = 1;
    public void split(String url){
        String s = new String(url);
        String a[] = s.split("=");
        fileName = a[1];
        if (fileName.contains(".")){
            Log.i(TAG, "fileName ======= >" + fileName + "======" + fileUtil.isExist(dirName,fileName));
            if (fileUtil.isExist(dirName,fileName)){
                String name = fileName.substring(0,fileName.indexOf("."));
                String type = fileName.substring(fileName.indexOf("."),fileName.length());
                Log.i(TAG, "m=======>" + m);
                fileName = name + "(" + m + ")" +type;
                Log.i(TAG, "filename ==== " + fileName);
                m ++ ;
                Log.i(TAG, "mmmmm======>" + m);
            }
        }else{
            if (fileUtil.isExist(dirName,fileName)){        //在文件包含类型，如txt,rar等的情况下
                fileName = fileName + "(" + m + ")";
                m = m ++ ;
            }
        }

    }

    /**
     * 通过文件在服务器的URL地址，下载到文件内容
     * @param urlStr
     * @return
     */
    public String download(String urlStr){
        StringBuffer sb=new StringBuffer();
        String line=null;
        InputStream is=fileUtil.getIS(urlStr);
        //由于InputStream流不方便使用，包装成处理流
        BufferedReader br=new BufferedReader(new InputStreamReader(is));
        try {
            while((line=br.readLine())!=null){
                sb.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


}
