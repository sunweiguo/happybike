package cn.oursnail.happybike.security;

import cn.oursnail.happybike.vo.UserElement;
import lombok.Data;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @Author 【swg】.
 * @Date 2018/3/14 15:58
 * @DESC
 * @CONTACT 317758022@qq.com
 */
@Data
public class RestAuthenticationToken extends AbstractAuthenticationToken{

    public RestAuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
    }

    private UserElement ue;

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}
