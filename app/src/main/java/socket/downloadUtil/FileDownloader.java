package socket.downloadUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

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
    public int downloadFile(String dirName,String fileName,String urlStr){
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
