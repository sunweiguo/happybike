package cn.oursnail.happybike.sms;

/**
 * @Author 【swg】.
 * @Date 2018/3/15 10:16
 * @DESC
 * @CONTACT 317758022@qq.com
 */
public interface SmsSender {
    void sendSms(String phone,String tplId,String params);
}