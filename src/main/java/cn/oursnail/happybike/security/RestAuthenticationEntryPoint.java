package cn.oursnail.happybike.security;

import cn.oursnail.happybike.exception.HappyBikeException;
import cn.oursnail.happybike.resp.ApiResult;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author 【swg】.
 * @Date 2018/3/14 15:15
 * @DESC 统一异常处理
 * @CONTACT 317758022@qq.com
 */
@Slf4j
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        try {
            //设置跨域请求 请求结果json刷到响应里
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, HEADER");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers", "X-Requested-With, user-token, Content-Type, Accept, version, type, platform");
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            if (request.getAttribute("header-error") != null) {
                if ("400".equals(request.getAttribute("header-error") + "")) {
                    response.getWriter().write(JSON.toJSONString(ApiResult.createByErrorCodeAndMessage(400,"请升级至最新版本")));
                } else {
                    response.getWriter().write(JSON.toJSONString(ApiResult.createByErrorCodeAndMessage(401,"请先登录")));
                }
            }
            response.flushBuffer();
        } catch (Exception er) {
            log.error("Fail to send 401 response {}", er.getMessage());
            throw new HappyBikeException("返回请求结果出错");
        }
    }
}