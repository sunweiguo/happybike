package cn.oursnail.happybike.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @Author 【swg】.
 * @Date 2018/3/14 17:19
 * @DESC
 * @CONTACT 317758022@qq.com
 */
public class BadCredentialException extends AuthenticationException {
    public BadCredentialException(String msg) {
        super(msg);
    }
}