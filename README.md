### 单车后台系统

一个基于springboot的类似于共享单车的后台系统，包括手机号码注册、登陆、定位、骑行轨迹和消息推送等功能

技术栈:

- SpringBoot SpringMVC Mybatis(基础框架) Mysql

- springSecurity(权限验证和请求拦截)

- AES对称加密数据，RSA非对称加密公钥key(对用户信息进行加密)

- redis缓存token(token作为用户的标识，维护用户的状态，类似于session)

- redis结合ActiveMQ发送短信验证码和防止恶意短信无限发送

- 整合云存储，保存头像(七牛云对象存储为例)

- 整合mongodb获取附近单车以及距离，主要是geoHash算法(重点)

- 百度云推送的实战-通知开锁

- 锁车之后的一系列操作(订单、消费、锁车，支付没有做)

- 骑行轨迹的保存(保存在mongodb中)

- 其他：logback，lombok，fastjson， 全局异常和高复用状态类封装、
利用mysql主键自增特性实现单车连续编号

- 学习这个项目的时候，对其思路做了笔记，我这里按照自己的写法，与其略有不同，
但是基本思路都是一样的。

注意：

- 本机环境是jdk8,tomcat8,mysql5.7.13，windows下，IDE是IntelliJ IDEA,
mongodb(mongodb-win32-x86_64-2008plus-v3.2-latest),redis(redis64-3.0.501),
activeMq(apache-activemq-5.15.2)

- api测试工具为Postman，还用了mongodb客户端Robo 3T，redis客户端工具RedisDesktopManage

- 跑这个代码之前，需要准备好数据库，开启redis、mongodb、activeMQ服务。

- 需要注册自己的七牛云账号(上传头像)、秒嘀账号(发送短信验证码)、百度云推送账号

- 代码仅供参考和学习，具体的学习，访问coder520.com的进阶实战项目

## 对于springBoot的入门，我强烈推荐慕课网上廖师兄的两个视频

#### 2小时学会Spring Boot：https://www.imooc.com/learn/767

#### Spring Boot进阶之Web进阶：https://www.imooc.com/learn/810

