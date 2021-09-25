package android.rsa.androidlearn.utils;
/**
 * Created by zhangbin on 2015/11/14.
 */
public class Msg {
    public static final int SEND_SIGN = 1;
    public static final int RECEIVE_SIGN = 0;
    private String msg;
    private int type;

    public Msg(String msg, int type) {
        this.msg = msg;
        this.type = type;
    }
    public String getMsg(){
        return msg;
    }
    public int getType() {
        return type;
    }
}
