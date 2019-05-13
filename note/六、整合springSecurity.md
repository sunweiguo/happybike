### 1、导入依赖：


```
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

### 2、配置拦截


```
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter{
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/**/login").permitAll()
                .anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                ;
    }
}
```

这里是先将csrf失效，本系统用不到防止表单攻击。允许login方法通过，其他的都需要授权才能通过。

我们会发现，当用postman发送 localhost:8888/user/modifyNickName 
返回信息是：
{
    "timestamp": 1509024238618,
    "status": 403,
    "error": "Forbidden",
    "message": "Access Denied",
    "path": "/user/modifyNickName"
}
我们的访问被限制了，但是login是可以得。但是我们这里修改用户昵称的方法是携带token的，是属于授权用户，我们不应该粗暴地拦截，所以需要配置过滤器，让携带token的用户通过拦截。

### 3、自定义过滤器

第一步就是来到这边，返回一个东西，这个东西会被后面的provider中的supports()方法拿到判断


```
public class RestPreAuthenticatedProcessingFilter extends AbstractPreAuthenticatedProcessingFilter {

    //获取用户信息
    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest httpServletRequest) {
        System.out.println("111111");
        return null;
    }


    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest httpServletRequest) {
        return null;
    }
}
```

第二步如果返回了什么，就到provider中判断：

如果符合supports()，那么就会执行authenticate()验证权限


```
public class RestAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        System.out.println("33333");
        return null;
    }

    //filter有正确返回时才会返回true，只有返回true才会进一步验证上面一个验证权限的方法
    @Override
    public boolean supports(Class<?> aClass) {
        System.out.println("222222");
        return false;
    }
}
```

如果不符合，直接到异常处理：


```
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        System.out.println("44444");
    }
}

```

securityConfig中配置过滤器，异常处理等：


```
.and().httpBasic().authenticationEntryPoint(new RestAuthenticationEntryPoint())
.and().addFilter(getPreAuthenticatedProcessingFilter())
```

这里第一个是直接new一下统一异常处理的类，第二个是设置filter，但是注意，filter也要设置manager，因为manager中管理着provider，才能提供权限信息：

所以先把manager放到filer中：


```
private RestPreAuthenticatedProcessingFilter getPreAuthenticatedProcessingFilter() throws Exception {
    RestPreAuthenticatedProcessingFilter filter = new RestPreAuthenticatedProcessingFilter();
    filter.setAuthenticationManager(this.authenticationManager());
    return filter;
}
```

然后把provider再放到manager中:


```
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(new RestAuthenticationProvider());
}
```

注意这里要设置一下放过option，这是跨域请求设置：


```
@Override
public void configure(WebSecurity web) throws Exception {
    web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**")//忽略 OPTIONS 方法的请求
            .antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources/**", "/configuration/**", "/swagger-ui.html", "/webjars/**");
    //放过swagger
}
```

运行：访问localhost:8888/user/modifyNickName 内容还是修改昵称，头还加上token ,这个时候结果为空白，运行的过程是：先显示11111，后显示44444.显示11111表示进入了filter，因为现在是直接return null，所以肯定没有进入provider中，直接进入了异常处理中，所以显示44444


3、因为有一些url是不需要拦截的，所以在securityConfig配置中会有这样一句话：


```
.antMatchers("/**/login").permitAll()
```

但是不利于扩展，因为不止一个这样的url，直接写死在这里不优雅，我们可以将这些url写进一个配置文件，读进去。以后修改的时候，直接在配置文件中修改即可。

首先是新建parameter.properties文件：


```
#security无需拦截的url
security.noneSecurityPath=/**/login,/**/regester,/**/sendVercode,/**/generateBike,/**/lockBike,/**/reportLocation,/**/swagger-ui.html
```

然后将其注入到程序中，即到Parameter中注入：

```
@Value("#{'${security.noneSecurityPath}'.split(',')}")
private List<String> noneSecurityPath;
```

在主函数上注解让他跟随一起启动：


```
@PropertySource(value="classpath:parameter.properties")
```

并且在主函数中也需要配置一下占位符问题：


```
@Bean
public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
	return new PropertySourcesPlaceholderConfigurer();
}
```

下面就是将Parameter注入到securityConfig中，然后写进配置：



```
@Autowired
private Parameters parameters;
```


```
.antMatchers(parameters.getNoneSecurityPath().toArray(new String[parameters.getNoneSecurityPath().size()])).permitAll()
```

### 4、验证用户及权限

首先是filter:


```
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
        this.noneSecurityList = noneSecurityList;
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

        if (version == null) {
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
                        RestAuthenticationToken authToken = new RestAuthenticationToken(Arrays.asList(authorities));\
                        //把用户信息存进去，防止provide中需要用
                        authToken.setUser(ue);
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

```

自定义的token:


```
@Data
public class RestAuthenticationToken extends AbstractAuthenticationToken {
    //authorities就是用户的角色，可以继而验证到权限
    public RestAuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
    }

    private UserElement user;

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}
```

下面就是provide处理


```
public class RestAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
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

    //filter有正确返回时才会返回true，只有返回true才会进一步验证上面一个验证权限的方法
    @Override
    public boolean supports(Class<?> authentication) {
        return PreAuthenticatedAuthenticationToken.class.isAssignableFrom(authentication)||RestAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
```

最后有一个统一的异常处理：


```
@Slf4j
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        ApiResult result = new ApiResult();
        //检查头部错误
        if (request.getAttribute("header-error") != null) {
            if ("400".equals(request.getAttribute("header-error") + "")) {
                result.setCode(408);
                result.setMessage("请升级至app最新版本");
            } else {
                result.setCode(401);
                result.setMessage("请您登录");
            }
        }
        try {
            //设置跨域请求 请求结果json刷到响应里
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, HEADER");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers", "X-Requested-With, user-token, Content-Type, Accept, version, type, platform");
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            response.getWriter().write(JSON.toJSONString(result));
            response.flushBuffer();
        } catch (Exception er) {
            log.error("Fail to send 401 response {}", er.getMessage());
        }
    }
}
```

这样权限验证就实现了。
potsman测试：localhost:8888/user/modifyNickName
当头不带user-token时：

```
{
    "code": 401,
    "message": "请您登录"
}
```
当头不带version时：


```
{
    "code": 408,
    "message": "请升级至app最新版本"
}
```

不错的话返回200码。


