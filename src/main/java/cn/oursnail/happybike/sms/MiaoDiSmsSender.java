package cn.oursnail.happybike.sms;

import cn.oursnail.happybike.constants.Constants;
import cn.oursnail.happybike.utils.HttpUtil;
import cn.oursnail.happybike.utils.MD5Util;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author 【swg】.
 * @Date 2018/3/15 10:16
 * @DESC
 * @CONTACT 317758022@qq.com
 */
@Service
@Slf4j
public class MiaoDiSmsSender implements SmsSender{
    private static String operation = "/industrySMS/sendSMS";

    /**
     *@Author JackWang [www.coder520.com]
     *@Date 2017/8/5 16:23
     *@Description  秒滴发送短信
     */
    @Override
    public  void sendSms(String phone,String tplId,String params){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String timestamp = sdf.format(new Date());
            String sig = MD5Util.getMD5(Constants.MDSMS_ACCOUNT_SID +Constants.MDSMS_AUTH_TOKEN +timestamp);
            String url = Constants.MDSMS_REST_URL +operation;
            Map<String,String> param = new HashMap<>();
            param.put("accountSid",Constants.MDSMS_ACCOUNT_SID);
            param.put("to",phone);
            param.put("templateid",tplId);
            param.put("param",params);
            param.put("timestamp",timestamp);
            param.put("sig",sig);
            param.put("respDataType","json");
            String result = HttpUtil.post(url,param);
            JSONObject jsonObject = JSON.parseObject(result);
            if(!jsonObject.getString("respCode").equals("00000")){
                log.error("fail to send sms to "+phone+":"+params+":"+result);
            }
        } catch (Exception e) {
            log.error("fail to send sms to "+phone+":"+params);
        }
    }
}