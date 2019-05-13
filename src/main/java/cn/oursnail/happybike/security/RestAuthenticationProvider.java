package cn.oursnail.happybike.security;

import cn.oursnail.happybike.exception.BadCredentialException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

/**
 * @Author 【swg】.
 * @Date 2018/3/14 15:13
 * @DESC provider中验证权限
 * @CONTACT 317758022@qq.com
 */
public class RestAuthenticationProvider implements AuthenticationProvider {

    /**
     * 用户授权
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException{
        if (authentication instanceof PreAuthenticatedAuthenticationToken) {
            PreAuthenticatedAuthenticationToken preAuth = (PreAuthenticatedAuthenticationToken) authentication;
            RestAuthenticationToken sysAuth = (RestAuthenticationToken) preAuth.getPrincipal();
            if (sysAuth.getAuthorities() != null && sysAuth.getAuthorities().size() > 0) {
                GrantedAuthority gauth = sysAuth.getAuthorities().iterator().next();
                if ("BIKE_CLIENT".equals(gauth.getAuthority())) {
                    return sysAuth;
                }else if ("ROLE_SOMEONE".equals(gauth.getAuthority())) {
                    return sysAuth;
                }
            }
        }else if (authentication instanceof RestAuthenticationToken) {
            RestAuthenticationToken sysAuth = (RestAuthenticationToken) authentication;
            if (sysAuth.getAuthorities() != null && sysAuth.getAuthorities().size() > 0) {
                GrantedAuthority gauth = sysAuth.getAuthorities().iterator().next();
                if ("BIKE_CLIENT".equals(gauth.getAuthority())) {
                    return sysAuth;
                }else if ("ROLE_SOMEONE".equals(gauth.getAuthority())) {
                    return sysAuth;
                }
            }
        }
        throw new BadCredentialException("unknown.error");
    }

    /**
     * filter有正确返回时才会返回true，只有返回true才会进一步验证上面一个验证权限的方法
     * 验证支持的类型
     * @param authentication
     * @return
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return PreAuthenticatedAuthenticationToken.class.isAssignableFrom(authentication)||RestAuthenticationToken.class.isAssignableFrom(authentication);
    }
}