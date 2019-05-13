package cn.oursnail.happybike.security;

import cn.oursnail.happybike.cache.CommonCacheUtil;
import cn.oursnail.happybike.cache.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * @Author 【swg】.
 * @Date 2018/3/14 15:09
 * @DESC
 * @CONTACT 317758022@qq.com
 */
@Configurable
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private Parameters parameters;

    @Autowired
    private CommonCacheUtil commonCacheUtil;

    //把manage放进filter中
    //由于在RestPreAuthenticatedProcessingFilter不能注入CommonCacheUtil，所以在这里用构造器的方式注入
    private RestPreAuthenticatedProcessingFilter getPreAuthenticatedProcessingFilter() throws Exception {
        RestPreAuthenticatedProcessingFilter filter = new RestPreAuthenticatedProcessingFilter(parameters.getNoneSecurityPath(),commonCacheUtil);
        filter.setAuthenticationManager(this.authenticationManager());
        return filter;
    }

    //把provider再放到manager中
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(new RestAuthenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers(parameters.getNoneSecurityPath().toArray(new String[parameters.getNoneSecurityPath().size()])).permitAll()
                .anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)//设置session为无状态的
                .and().httpBasic().authenticationEntryPoint(new RestAuthenticationEntryPoint())//设置全局异常处理
                .and().addFilter(getPreAuthenticatedProcessingFilter())//增加filter
        ;
    }

    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**")//忽略 OPTIONS 方法的请求
                .antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources/**", "/configuration/**", "/swagger-ui.html", "/webjars/**");
        //放过swagger
    }
}
