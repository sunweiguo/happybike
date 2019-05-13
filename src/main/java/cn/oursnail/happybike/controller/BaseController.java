package cn.oursnail.happybike.controller;

import cn.oursnail.happybike.cache.CommonCacheUtil;
import cn.oursnail.happybike.constants.Constants;
import cn.oursnail.happybike.exception.HappyBikeException;
import cn.oursnail.happybike.vo.UserElement;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author 【swg】.
 * @Date 2018/3/14 14:39
 * @DESC
 * @CONTACT 317758022@qq.com
 */
@Slf4j
public class BaseController {

    @Autowired
    private CommonCacheUtil commonCacheUtil;

    protected UserElement getCurrentUser(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String token = request.getHeader(Constants.REQUEST_TOKEN_KEY);
        if(StringUtils.isNotBlank(token)){
            UserElement ue = commonCacheUtil.getUserByToken(token);
            return ue;
        }else {
            log.error("【获取token失败】");
            throw new HappyBikeException("获取token失败");
        }
    }

    protected String getIpFromRequest(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getRemoteAddr();
        }
        return ip.equals("0:0:0:0:0:0:0:1")?"127.0.0.1":ip;
    }
}
