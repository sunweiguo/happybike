package cn.oursnail.happybike.security;

import cn.oursnail.happybike.cache.CommonCacheUtil;
import cn.oursnail.happybike.constants.Constants;
import cn.oursnail.happybike.vo.UserElement;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * @Author 【swg】.
 * @Date 2018/3/14 15:13
 * @DESC 获取用户信息(manage)
 * @CONTACT 317758022@qq.com
 */
@Slf4j
public class RestPreAuthenticatedProcessingFilter extends AbstractPreAuthenticatedProcessingFilter {

    /**
     * spring的路径匹配器
     */
    private AntPathMatcher matcher = new AntPathMatcher();

    //不需要授权的路径和redis操作，用构造器的方式得到，因为这里还不能直接注入，只能在SecurityConfig中注入，可能原因是这里相对于spring是独立的东西。
    private List<String> noneSecurityList;

    private CommonCacheUtil commonCacheUtil;

    public RestPreAuthenticatedProcessingFilter(List<String> noneSecurityPath, CommonCacheUtil commonCacheUtil) {
        this.noneSecurityList = noneSecurityPath;
        this.commonCacheUtil = commonCacheUtil;
    }

    //获取用户信息
    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        //这里是定义一个长度为1的数组存放角色，将其放到自定义的token中，用于后面provider调用
        GrantedAuthority[] authorities = new GrantedAuthority[1];
        //url是直接放过的，角色赋予ROLE_SOME
        if(isNoneSecurity(request.getRequestURI().toString()) || "OPTIONS".equals(request.getMethod())){
            GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_SOME");
            authorities[0] = authority;
            return new RestAuthenticationToken(Arrays.asList(authorities));
        }
        //检查APP版本
        String version = request.getHeader(Constants.REQUEST_VERSION_KEY);
        String token = request.getHeader(Constants.REQUEST_TOKEN_KEY);

        if (StringUtils.isBlank(version)) {
            request.setAttribute("header-error", Constants.RESP_STATUS_BADREQUEST);
        }
        //为空说明version是正常传过来的
        if(request.getAttribute("header-error") == null){
            try{
                if(!StringUtils.isBlank(token)){
                    UserElement ue = commonCacheUtil.getUserByToken(token);
                    if(ue instanceof UserElement){
                        //检查到token说明用户已经登录 授权给用户BIKE_CLIENT角色 允许访问
                        GrantedAuthority authority = new SimpleGrantedAuthority("BIKE_CLIENT");
                        authorities[0] = authority;
                        RestAuthenticationToken authToken = new RestAuthenticationToken(Arrays.asList(authorities));
                        //把用户信息存进去，防止provide中需要用
                        authToken.setUe(ue);
                        return authToken;
                    }else {
                        //token不对
                        request.setAttribute("header-error", 401);
                    }
                }else {
                    log.warn("Got no token from request header");
                    //token不存在 告诉移动端 登录
                    request.setAttribute("header-error", 401);
                }
            }catch (Exception e){
                log.error("fail to authenticate'",e);
            }
        }

        //其他的情况，不能return null。赋予ROLE_NONE角色
        if(request.getAttribute("header-error") != null){
            //请求头有错误  随便给个角色 让逻辑继续
            GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_NONE");
            authorities[0] = authority;
        }
        RestAuthenticationToken authToken = new RestAuthenticationToken(Arrays.asList(authorities));
        return authToken;
    }

    private boolean isNoneSecurity(String uri) {
        boolean result = false;
        if (this.noneSecurityList != null) {
            for (String pattern : this.noneSecurityList) {
                if (matcher.match(pattern, uri)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }


    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest httpServletRequest) {
        return null;
    }
}