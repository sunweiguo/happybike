## 1、创建
只能说大中华的墙太狠了，spring官网有的时候都访问不了，无论是刷新页面等待还是翻墙，登陆https://start.spring.io/可以创建,也可以像我之前spring中springboot入门中创建。

## 2、springBoot整合springMVC

引入依赖：


```
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

创建controller，直接返回string，这里用@RestController，那么自动返回json数据，不需要@ResponseBody了。


```
@RestController
@RequestMapping("user")
public class UserController {

    @RequestMapping("/test")
    public String test(){
        return "hello bike";
    }
}
```

在application.yml文件配置端口(默认生成的是application.properties，改后缀)


```
server:
  port: 8888
```

启动主函数即可。在浏览器输入localhost:8888/user/test即可看到hello bike.

## 3、整合mybatis

引入依赖：


```
<!--MYSQL-->
<dependency>
	<groupId>mysql</groupId>
	<artifactId>mysql-connector-java</artifactId>
</dependency>
<!--mybatis-->
<dependency>
	<groupId>org.mybatis.spring.boot</groupId>
	<artifactId>mybatis-spring-boot-starter</artifactId>
	<version>1.2.0</version>
</dependency>
<!--fastJson-->
<dependency>
	<groupId>com.alibaba</groupId>
	<artifactId>fastjson</artifactId>
	<version>1.2.12</version>
</dependency>
<!--druid-->
<dependency>
	<groupId>com.alibaba</groupId>
	<artifactId>druid</artifactId>
	<version>1.0.18</version>
</dependency>
<!--mybatis generator-->
<dependency>
	<groupId>org.mybatis.generator</groupId>
	<artifactId>mybatis-generator-core</artifactId>
	<version>1.3.2</version>
</dependency>
```

并且加入插件


```
<build>
		<resources>
			<resource>
				<directory>src/main/java</directory>
				<includes>
					<include>**/*.xml</include>
				</includes>
			</resource>
		</resources>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>

			<plugin>
				<groupId>org.mybatis.generator</groupId>
				<artifactId>mybatis-generator-maven-plugin</artifactId>
				<version>1.3.0</version>
				<configuration>
					<configurationFile>src/main/resources/generatorConfig.xml</configurationFile>
					<verbose>true</verbose>
					<overwrite>true</overwrite>
				</configuration>
			</plugin>
		</plugins>
	</build>

```

另外在application.yml中要配置数据源和Mybatis扫描地：


```
#startup
server:
    port: 8888
#Spring
spring:
    application:
        name: bike01
#profile
    profiles:
        active: dev
#datasource
    datasource:
        # druid
        type: com.alibaba.druid.pool.DruidDataSource
        driver-class-name: com.mysql.jdbc.Driver
        filters: stat
        maxActive: 20
        initialSize: 1
        maxWait: 60000
        minIdle: 1
        timeBetweenEvictionRunsMillis: 60000
        minEvictableIdleTimeMillis: 300000
        validationQuery: select 'x'
        testWhileIdle: true
        testOnBorrow: false
        testOnReturn: false
        poolPreparedStatements: true
        maxOpenPreparedStatements: 20

#mybatis
mybatis:
    mapper-locations: classpath:com/oursnail/**/**.xml
    type-aliases-package: classpath:com.oursnail.**.entity
```

其中的profile就是配置数据源选择哪一个，一个是测试环境，一个是本地环境。这里再建立一个本地环境的配置文件：application-dev.yml：


```
spring:
  datasource:
     name: dev
     url: jdbc:mysql://localhost:3306/bike
     username: root
     password: root
```

## 4、用户表：


```
-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nickname` varchar(20) DEFAULT NULL COMMENT '昵称',
  `mobile` varchar(20) NOT NULL COMMENT '手机号码',
  `head_img` varchar(100) DEFAULT NULL COMMENT '头像',
  `verify_flag` tinyint(2) NOT NULL DEFAULT '1' COMMENT '是否实名认证 1： 否 2：已认证',
  `enable_flag` tinyint(2) NOT NULL DEFAULT '1' COMMENT '是否有效有用 1：有效  2：无效',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', 'wang', '18980840843', null, '2', '1');
```


## 5、mybatis自动生成工具

配置文件和依赖算是全部完成了，下面就用generator工具生成dao和entity（略）

## 6、service和controller

再继续完成相应的service和controller层，其中controller层为：


```
@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/test")
    public User test(){
        User u = userService.getUserById(1L);
        return u;
    }
}
```

## 7、出错:找不到mapper依赖

启动发现报错，无法找到mapper依赖，查看编译文件，都是编译进去的，这种情况从来没有发生过，后来发现mapper接口上需要增加注解：


```
@Mapper
```

## 8、启动

直接启动main函数，输入url:http://localhost:8888/user/test
正确结果是返回一条用户信息的json字符串显示在页面上：


```
{"id":1,"nickname":"wang","mobile":"18980840843","headImg":null,"verifyFlag":2,"enableFlag":1}
```

只要出现了这个字符串，那么就算是springBoot整合ssm成功了！


## 9、打开事务

在主函数上添加注解：

> @EnableTransactionManagement