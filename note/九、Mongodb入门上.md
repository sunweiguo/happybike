### 1、因为用到mongodb，所以先安装一下，然后入一下门

### 2、下载

到官网 https://www.mongodb.com/download-center#community

点击 All Version Binaries 进入选择最近的zip下载

https://www.mongodb.org/dl/win32/x86_64-2008plus-ssl?_ga=2.26151293.1146073882.1509520277-1740852681.1509520277 


### 3、简单的安装启动


```
mongod --dbpath D:\mongodb\data\db
```
  
数据就放在D:\mongodb\data\db下，要先创建好这个文件夹。

安装成功之后会提示端口port=27017 ，输入http://localhost:27017/，看到：It looks like you are trying to access MongoDB over HTTP on the native driver port.这句话就说明已经启动成功了。

### 4、每次都要去启动会比较繁琐，做成服务

在data目录下新建文件夹：log,新建文件mongo.log
在mongodb这个目录下新建文件:mongo.config


```
dbpath=D:\mongodb\data\db

logpath=D:\mongodb\data\log\mongo.log
```


然后进入bin执行命令： 


```
mongod --config D:\mongodb\mongo.config --install --serviceName "MongoDB"
```


进入服务管理，这时应该可以看到mongodb的服务，直接启动即可。

### 5、黑窗口的操作

执行mongo命令，进入shell，可以直接操作数据库。

塞入数据：
这里是新建了一个person的集合，相当于Mysql中的table

```
db.person.insert({'name':'swg','age':12})
db.person.insert({'name':'xf','age':22})
db.person.insert({'name':'hh','age':32})
```

查询所有数据：

```
db.person.find()
```

查询某一条具体数据：


```
db.person.find({'name':'swg'})
```

更新某一条数据

```
db.person.update({'name':'swg','age':12},{'name':'swg111','age':13})
```


删除某一条数据

```
db.person.remove({'name':'hhhhhh'})
```

### 6、图形化界面：robomongo

下载: http://www.newasp.net/soft/75669.html#downloaded

优势：查看数据比较直观


稍微详细点的介绍：  https://github.com/StevenSLXie/Tutorials-for-Web-Developers/blob/master/MongoDB%20%E6%9E%81%E7%AE%80%E5%AE%9E%E8%B7%B5%E5%85%A5%E9%97%A8.md

易百的教程： http://www.yiibai.com/mongodb/




