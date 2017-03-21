package socket.info; /**
 * Created by Lee on 2017/3/16 0016.
 */

/**
 * 启动http服务端
 *
 * @param flag ：标位置，0表示上屏，1表示下屏
 * @param start ：是否要打开http服务端，true表示打开，false表示不打开
 */

public class StartServerInfo {

    String flag;
    boolean start;

    public StartServerInfo() {

    }

    public StartServerInfo(boolean start) {
        this.start = start;
    }

    public StartServerInfo(String flag, boolean start) {
        this.flag = flag;
        this.start = start;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public boolean isOpen() {
        return start;
    }

    public void setOpen(boolean open) {
        this.start = open;
    }
}

