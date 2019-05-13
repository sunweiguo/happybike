package cn.oursnail.happybike.service.impl;

import cn.oursnail.happybike.cache.CommonCacheUtil;
import cn.oursnail.happybike.constants.Constants;
import cn.oursnail.happybike.dao.UserMapper;
import cn.oursnail.happybike.entity.User;
import cn.oursnail.happybike.exception.HappyBikeException;
import cn.oursnail.happybike.resp.ApiResult;
import cn.oursnail.happybike.service.IUserService;
import cn.oursnail.happybike.sms.SmsProcessor;
import cn.oursnail.happybike.utils.*;
import cn.oursnail.happybike.vo.UserElement;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.web.multipart.MultipartFile;

import javax.jms.Destination;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author 【swg】.
 * @Date 2018/3/14 9:43
 * @DESC
 * @CONTACT 317758022@qq.com
 */
@Service
@Slf4j
public class UserServiceImpl implements IUserService {
    private static final String VERIFYCODE_PREFIX = "verify.code.";
    private static final String SMS_QUEUE = "sms.queue";

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CommonCacheUtil commonCacheUtil;
    @Autowired
    private SmsProcessor smsProcessor;

    @Override
    public String login(String data, String key){
        String token = null;
        byte[] aeskey = null;
        String decryptData = null;
        //1. 开始解码
        try {
            aeskey = RSAUtil.decryptByPrivateKey(Base64Util.decode(key));
            decryptData = AESUtil.decrypt(data,new String(aeskey,"UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new HappyBikeException("数据解码失败");
        }

        if(StringUtils.isBlank(decryptData)){
            throw new HappyBikeException("解析的数据为空");
        }
        //2. fastajson解析json数据
        JSONObject jsonObject = JSON.parseObject(decryptData);
        String mobile = jsonObject.getString("mobile");
        String code = jsonObject.getString("code");
        String platform = jsonObject.getString("platform");
        if(StringUtils.isBlank(mobile) || StringUtils.isBlank(code) || StringUtils.isBlank(platform)){
            throw new HappyBikeException("数据不完整");
        }
        //3. 拿到手机号码和验证码，去redis取验证码，比较手机号码和验证码是不是匹配
        String verCode = commonCacheUtil.getCacheValue(mobile);
        User user = null;
        if(StringUtils.equals(verCode,code)){
            user = userMapper.selectByMobile(mobile);
            //4. 检查用户是否存在 存在 生成token存redis 不存在就注册 插入数据库
            if(user == null){
                user = new User();
                user.setMobile(mobile);
                user.setNickname(mobile);
                userMapper.insertSelective(user);
            }
        }else {
            throw new HappyBikeException("手机验证码不匹配");
        }
        //5. 生成token并返回
        try {
            token = generatToken(user);
        } catch (Exception e) {
            throw new HappyBikeException("token生成错误");
        }
        //6. 最后，在redis中存储key：token，value：user信息(UE对象)；还存储key：userid，value：token
        UserElement ue = new UserElement();
        ue.setUserId(user.getId());
        ue.setMobile(user.getMobile());
        ue.setToken(token);
        ue.setPlatform(platform);
        commonCacheUtil.putTokenWhenLogin(ue);
        return token;
    }

    @Override
    public ApiResult modifyNickName(User user) {
        User userNew = userMapper.selectByPrimaryKey(user.getId());
        if(userNew == null){
            throw new HappyBikeException("获取用户信息失败");
        }
        userNew.setNickname(user.getNickname());
        int rowCount = userMapper.updateByPrimaryKeySelective(userNew);
        if(rowCount > 0){
            return ApiResult.createBySuccessMessage("更新用户昵称成功");
        }else {
            throw new HappyBikeException("更新用户昵称失败");
        }
    }

    @Override
    public ApiResult sendVercode(String mobile, String ip) {
        if(StringUtils.isBlank(mobile)){
            return ApiResult.createByErrorMessage("手机号码不能为空");
        }
        String verCode = RandomNumberCode.verCode();
        int result = commonCacheUtil.cacheForVerificationCode(VERIFYCODE_PREFIX+mobile,verCode,"reg",60,ip);

        if (result == 1) {
            log.info("当前验证码未过期，请稍后重试");
            return ApiResult.createByErrorMessage("当前验证码未过期，请稍后重试");
        } else if (result == 2) {
            log.info("手机号超过当日验证码次数上限{}",mobile);
            return ApiResult.createByErrorMessage("手机号超过当日验证码次数上限");
        } else if (result == 3) {
            log.info("ip超过当日验证码次数上限 {}", ip);
            return ApiResult.createByErrorMessage("ip超过当日验证码次数上限");
        }
        log.info("Sending verify code {} for phone {}", verCode, mobile);

        //发送短信
        //验证码推送到队列，SMS_QUEUE就是目的地queue：sms.queue
        try{
            Destination destination = new ActiveMQQueue(SMS_QUEUE);
            Map<String,String> smsParam = new HashMap<>();
            smsParam.put("mobile",mobile);
            smsParam.put("tplId", Constants.MDSMS_VERCODE_TPLID);
            smsParam.put("vercode",verCode);
            String message = JSON.toJSONString(smsParam);
            smsProcessor.sendSmsToQueue(destination,message);
        }catch (Exception e){
            log.error("发送短信发生异常");
            throw new HappyBikeException("发送短信发生异常");
        }
        return ApiResult.createBySuccessMessage("发送短信成功");
    }

    @Override
    public ApiResult<String> uploadHeadImg(MultipartFile file, Long userId) throws IOException {
        User user = userMapper.selectByPrimaryKey(userId);
        // 调用七牛
        String imgUrlName = QiniuFileUploadUtil.uploadHeadImg(file);
        user.setHeadImg(imgUrlName);
        //更新用户头像URL
        int rowCount = userMapper.updateByPrimaryKeySelective(user);
        if(rowCount == 0){
            return ApiResult.createByErrorMessage("更新头像失败");
        }
        String headImg = Constants.QINIU_HEAD_IMG_BUCKET_URL+"/"+Constants.QINIU_HEAD_IMG_BUCKET_NAME+"/"+imgUrlName;
        return ApiResult.createBySuccessMessageAndData("更新头像成功",headImg);
    }

    private String generatToken(User user) {
        String source = user.getId() + ":" + user.getMobile() + System.currentTimeMillis();
        return MD5Util.getMD5(source);
    }
}
