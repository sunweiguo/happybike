package cn.oursnail.happybike.constants;

/**
 * @Author 【swg】.
 * @Date 2018/3/14 11:01
 * @DESC
 * @CONTACT 317758022@qq.com
 */
public enum  ResponseEnum {
    //成功
    SUCCESS(0,"SUCCESS"),
    //失败
    ERROR(1,"ERROR"),
    //需要登陆
    NEED_LOGIN(10,"NEED_LOGIN"),
    //参数错误
    ILLEGAL_ARGUMENT(2,"ILLEGAL_ARGUMENT");

    private final int code;
    private final String desc;

    ResponseEnum(int code,String desc){
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
