package socket.info; /**
 * Created by Lee on 2017/3/16 0016.
 */

/**
 * 启动http服务端
 *
 * parmas address ：IP地址
 * parmas flag ：标位置，0表示上屏，1表示下屏
 * parmas open ：是否要打开http服务端，true表示打开，false表示不打开
 */

public class StartInfo {
    String address;
    String flag;
    boolean open;

    public StartInfo() {

    }

    public StartInfo(boolean open) {
        this.open = open;
    }

    public StartInfo(String address, String flag, boolean open) {
        this.address = address;
        this.flag = flag;
        this.open = open;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
}

