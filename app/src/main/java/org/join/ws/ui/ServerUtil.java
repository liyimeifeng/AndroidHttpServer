package org.join.ws.ui;

/**
 * Created by Lee on 2017/3/21 0021.
 *
 * @param flag ：标位置，0表示上屏，1表示下屏
 * @param start ：是否要打开http服务端，true表示打开，false表示不打开
 */



public class ServerUtil {

    private boolean start;
    private  int flag;
    public ServerUtil(boolean isToStart,int flag){
        this.start = isToStart;
        this.flag = flag;
    }




}
