## 1、如何对controller进行测试？

示例：

```
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Bike01Application.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;
    @LocalServerPort
    private int port;

    @Autowired
    private UserService userService;

    @Test
    public void testShow(){
        String result = restTemplate.getForObject("/user/test",String.class);
        System.out.println(result);
    }
}
```


## 2、spring默认使用的json是jackson，效率比较低；

jackson与fastjson的区别：当查询某条记录，这条记录某个字段为NULL时，jackson也会转化为NULL：


比如我数据库中的headImg字段为null，这里获取到的对象中仍然转化为NULL:

```
{"id":1,"nickname":"wang","mobile":"18980840843","headImg":null,"verifyFlag":2,"enableFlag":1}
```

但是fastJSon对此进行了优化，它认为为null的字段就不需要转化了，那么我们就要首先覆盖原来的jackson,再来测试结果。覆盖方式为类似于xml中的配置bean，他用java来配置一个messageConvertor的bean:


```
@Bean
public HttpMessageConverters fastJsonHttpMessageConverters() {
	FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
	HttpMessageConverter<?> converter = fastConverter;
	return new HttpMessageConverters(converter);
}
```

我们再来查询这条记录，发现为空的直接就不转化了：


```
{"verifyFlag":2,"mobile":"18980840843","nickname":"wang","id":1,"enableFlag":1}
```


## 3、整合日志logback

引入依赖：


```
<dependency>
	<groupId>ch.qos.logback</groupId>
	<artifactId>logback-core</artifactId>
	<version>1.1.6</version>
</dependency>
<dependency>
	<groupId>ch.qos.logback</groupId>
	<artifactId>logback-classic</artifactId>
	<version>1.1.6</version>
</dependency>
<dependency>
	<groupId>ch.qos.logback</groupId>
	<artifactId>logback-access</artifactId>
	<version>1.1.2</version>
</dependency>
<dependency>
	<groupId>commons-logging</groupId>
	<artifactId>commons-logging</artifactId>
	<version>1.2</version>
</dependency>
```

logback.xml:


```
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
	<property name="LOG_HOME" value="/logs/bikeLog/" />

	<appender name="Console" class="ch.qos.logback.core.ConsoleAppender">
		<encoder>
			<pattern>%d{H:mm} %-5level [%logger{16}] %msg%n</pattern>
		</encoder>
	</appender>

	<appender name="normalLog"
		class="ch.qos.logback.core.rolling.RollingFileAppender">
		<rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
			<FileNamePattern>${LOG_HOME}/web.normal.%d{yyyy-MM-dd}.log
			</FileNamePattern>
			<MaxHistory>30</MaxHistory>
		</rollingPolicy>
		<triggeringPolicy class="ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy">
			<maxFileSize>10MB</maxFileSize>
		</triggeringPolicy>
		<layout class="ch.qos.logback.classic.PatternLayout">
			<pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{16} - %msg%n
			</pattern>
		</layout>
		<filter class="ch.qos.logback.classic.filter.LevelFilter">
			<level>ERROR</level>
			<onMatch>DENY</onMatch>
			<onMismatch>ACCEPT</onMismatch>
		</filter>
	</appender>
	<appender name="errorLog"
		class="ch.qos.logback.core.rolling.RollingFileAppender">
		<rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
			<FileNamePattern>${LOG_HOME}/web.error.%d{yyyy-MM-dd}.log
			</FileNamePattern>
			<MaxHistory>30</MaxHistory>
		</rollingPolicy>
		<triggeringPolicy class="ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy">
			<maxFileSize>10MB</maxFileSize>
		</triggeringPolicy>
		<layout class="ch.qos.logback.classic.PatternLayout">
			<pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{16} - %msg%n
			</pattern>
		</layout>
		<filter class="ch.qos.logback.classic.filter.LevelFilter">
			<level>ERROR</level>
			<onMatch>ACCEPT</onMatch>
			<onMismatch>DENY</onMismatch>
		</filter>
	</appender>

	
	<logger name="com.oursnail" level="debug" >
		<appender-ref ref="normalLog" />
		<appender-ref ref="errorLog" />
	</logger>


	<root level="info">
		<appender-ref ref="Console" />
	</root>
</configuration>

```

application.yml中调用这个xml:


```
#log
logging:
    config: classpath:logback.xml
```

maven中为了让这些xml被编译：


```
<resource>
	<directory>${basedir}/src/main/resources</directory>
	<includes>
		<include>*.yml</include>
		<include>*.properties</include>
		<include>*.xml</include>
		<include>enc_pri</include>
	</includes>
</resource>
```

进行测试：


在userServiceImpl中故意写一个会抛异常的方法：重复插入id和not null却不插值：

```    
@Override
public String login() {
    User user = new User();
    user.setId(1L);
    userMapper.insertSelective(user);
    return null;
}

```

在单元测试中：


注意引入Logger,选择slf4j

```
Logger logger = LoggerFactory.getLogger(UserControllerTest.class);
@Test
public void testLogin(){
    try{
        userService.login();
    }catch (Exception e){
        logger.error("用户插入出错了",e);
    }
}
```

运行之后，控制台提示“用户插入出错”，并且发现日志中也记录下来了。整合成功！！

## 4、lombok

实体类中有很多get set 方法以及要重复使用logger，为了方便，可以直接使用lombok这个插件

引入依赖：


```
<dependency>
	<groupId>org.projectlombok</groupId>
	<artifactId>lombok</artifactId>
	<version>1.16.6</version>
</dependency>
```

干掉实体类中的get set方法，直接在类头上写注解：


```
@Data
```

针对需要使用logback的地方，在类头写：


```
@Slf4j
```

然后就可以直接调用：log.error("用户插入出错了",e);

此时运行时是没有问题的，但是编译器还不能识别这个，需要装上lombok插件，重启一下，正常！






