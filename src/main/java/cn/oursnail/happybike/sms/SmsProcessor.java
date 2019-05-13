package cn.oursnail.happybike.sms;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Destination;

/**
 * @Author 【swg】.
 * @Date 2018/3/15 10:15
 * @DESC
 * @CONTACT 317758022@qq.com
 */
@Component
public class SmsProcessor {
    @Autowired
    private JmsMessagingTemplate jmsTemplate;

    @Autowired
    private SmsSender smsSender;

    public void sendSmsToQueue(Destination destination, final String message){
        jmsTemplate.convertAndSend(destination, message);
    }

    @JmsListener(destination="sms.queue")
    public void doSendSmsMessage(String text){
        JSONObject jsonObject = JSON.parseObject(text);
        smsSender.sendSms(jsonObject.getString("mobile"),jsonObject.getString("tplId"),jsonObject.getString("vercode"));
    }
}