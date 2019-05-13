### 1、为什么要用mongodb？

假设一片微博，有很多评论和标签，那么典型的做法就是再搞一张评论表和一张标签表，显示是比较麻烦的。

对于mongodb，数据模型可能是：


```
 _id: POST_ID
   title: TITLE_OF_POST, 
   description: POST_DESCRIPTION,
   author: POST_BY,
   tags: [TAG1, TAG2, TAG3],
   likes: TOTAL_LIKES, 
   comments: [	
      {
         user:'COMMENT_BY',
         message: TEXT,
         dateCreated: DATE_TIME,
      },
      {
         user:'COMMENT_BY',
         message: TEXT,
         dateCreated: DATE_TIME,
      }
   ]
```

那么本来的三张表在这里只需要一个文档就能解决。

### 2、创建集合和删除集合


```
use tutorial   //新建了一个叫做tutorial的数据库
```


```
db.createCollection('author')//数据库里添加一个集合(collection)
```


```
show databases//展示所有的数据库
show collections//展示这个数据库下面的所有集合
```


```
db.author.drop()//删除集合
```

### 3、插入

模拟一个电影的信息：


```
电影名字
导演
主演(可能多个)
类型标签(可能多个)
上映日期
喜欢人数
不喜欢人数
用户评论(可能多个)
```


显然我们需要先创建一个叫电影的集合：


```
db.createCollection('movie')
```

插入数据：


```
db.movie.insert(
 {
   title: 'Forrest Gump', 
   directed_by: 'Robert Zemeckis',
   stars: ['Tom Hanks', 'Robin Wright', 'Gary Sinise'],
   tags: ['drama', 'romance'],
   debut: new Date(1994,7,6,0,0),
   likes: 864367,
   dislikes: 30127,
   comments: [	
      {
         user:'user1',
         message: 'My first comment',
         dateCreated: new Date(2013,11,10,2,35),
         like: 0 
      },
      {
         user:'user2',
         message: 'My first comment too!',
         dateCreated: new Date(2013,11,11,6,20),
         like: 0 
      }
   ]
}
)
```

我们也可以同时输入多个数据：


```
db.movie.insert([
 {
   title: 'Fight Club', 
   directed_by: 'David Fincher',
   stars: ['Brad Pitt', 'Edward Norton', 'Helena Bonham Carter'],
   tags: 'drama',
   debut: new Date(1999,10,15,0,0),
   likes: 224360,
   dislikes: 40127,
   comments: [	
      {
         user:'user3',
         message: 'My first comment',
         dateCreated: new Date(2008,09,13,2,35),
         like: 0 
      },
      {
         user:'user2',
         message: 'My first comment too!',
         dateCreated: new Date(2003,10,11,6,20),
         like: 14 
      },
      {
         user:'user7',
         message: 'Good Movie!',
         dateCreated: new Date(2009,10,11,6,20),
         like: 2
      }
   ]
},
{
   title: 'Seven', 
   directed_by: 'David Fincher',
   stars: ['Morgan Freeman', 'Brad Pitt',  'Kevin Spacey'],
   tags: ['drama','mystery','thiller'],
   debut: new Date(1995,9,22,0,0),
   likes: 134370,
   dislikes: 1037,
   comments: [	
      {
         user:'user3',
         message: 'Love Kevin Spacey',
         dateCreated: new Date(2002,09,13,2,35),
         like: 0 
      },
      {
         user:'user2',
         message: 'Good works!',
         dateCreated: new Date(2013,10,21,6,20),
         like: 14 
      },
      {
         user:'user7',
         message: 'Good Movie!',
         dateCreated: new Date(2009,10,11,6,20),
         like: 2
      }
   ]
}
])
```

查询所有：
```
db.movie.find().pretty()
```

这里find()里面是空的，说明我们不做限制和筛选，类似于SQL没有WHERE语句一样。而pretty()输出的是经格式美化后的数据。


### 4、查询

找出大卫芬奇(David Fincher)导演的所有电影：


```
db.movie.find({'directed_by':'David Fincher'}).pretty()
```

找出大卫芬奇导演的, 摩根弗里曼主演的电影：


```
db.movie.find({'directed_by':'David Fincher', 'stars':'Morgan Freeman'}).pretty()
```

找出罗宾怀特或摩根弗里曼主演的电影：


```
db.movie.find(
{
  $or: 
     [  {'stars':'Robin Wright'}, 
        {'stars':'Morgan Freeman'}
     ]
}).pretty()
```

还可以设置一个范围的搜索，比如找出50万人以上赞的电影：


```
db.movie.find({'likes':{$gt:500000}}).pretty()
```

在这些查询里，key的单引号都是可选的：


```
db.movie.find({likes:{$gt:500000}}).pretty()
```


```
db.movie.find({likes:{$lt:200000}}).pretty()

//$let:小于或等于；$get:大于或等于；$ne:不等于
```

对于包含多个值的key，同样可以用find来查询：


```
db.movie.find({'tags':'romance'})
```

如果你确切地知道返回的结果只有一个，也可以用findOne:


```
db.movie.findOne({'title':'Forrest Gump'})

//如果有多个结果，则会按磁盘存储顺序返回第一个。请注意，findOne()自带pretty模式，所以不能再加pretty()，将报错。
```

只想显示其中一部分，可以用limit()和skip()，前者指明输出的个数，后者指明从第二个结果开始数：


```
db.movie.find().limit(2).skip(1).pretty()

//则跳过第一部，从第二部开始选取两部电影。
```

### 5、局部查询

返回tags为drama的电影的名字和首映日期：


```
db.movie.find({'tags':'drama'},{'debut':1,'title':1}).pretty()

//这里find的第二个参数是用来控制输出的，1表示要返回，而0则表示不返回。默认值是0
```
### 6、更新

假设有人对《七宗罪》点了两个赞：


```
db.movie.update({title:'Seven'}, {$inc:{likes:2}})
```

如果有多部符合要求的电影。则默认只会更新第一个。如果要多个同时更新，要设置{multi:true}：


```
db.movie.update({}, {$inc:{likes:10}},{multi:true})

//所有电影的赞数都多了10
```

想在原有的值得基础上增加一个值的话，则应该用$push：


```
db.movie.update({'title':'Seven'}, {$push:{'tags':'popular'}})
```

### 7、删除

要删除标签为romance的电影

```
db.movie.remove({'tags':'romance'})
```

如果你只想删除第一个：


```
db.movie.remove({'tags':'romance'},1)
```

全部删除：


```
db.movie.remove()
```

### 8、索引和排序

比如我们要对导演这个key加索引：


```
db.movie.ensureIndex({directed_by:1})

//这里的1是升序索引，如果要降序索引，用-1
```

MongoDB支持对输出进行排序，比如按名字排序：


```
db.movie.find().sort({'title':1}).pretty()

//同样地，1是升序，-1是降序。默认是1。
```


```
db.movie.getIndexes()//返回所有索引，包括其名字
```


```
db.movie.dropIndex('index_name')//删除对应的索引
```

### 9、聚合


类似于Mysql中的group by.

先执行如下命令：


```
db.movie.update({title:'Seven'},{$set:{grade:1}})
db.movie.update({title:'Forrest Gump'},{$set:{grade:1}})
db.movie.update({title:'Fight Club'},{$set:{grade:2}})

这几条是给每部电影加一个虚拟的分级，前两部是归类是一级，后一部是二级。

这里你也可以看到MongoDB的强大之处：可以动态地后续添加各种新项目。
```


```
db.movie.aggregate([{$group:{_id:'$grade'}}])

//按照grade聚合，返回结果是聚合的依据：

{ "_id" : 2 }
{ "_id" : 1 }

如果按照导演名字聚合：

db.movie.aggregate([{$group:{_id:'$directed_by'}}])

返回结果就是聚合的依据：

{ "_id" : "David Fincher" }
{ "_id" : "Robert Zemeckis" }
```


```
db.movie.aggregate([{$group:{_id:'$directed_by',num_movie:{$sum:1}}}])//找出每个导演的电影数

输出：

{ "_id" : "David Fincher", "num_movie" : 2 }
{ "_id" : "Robert Zemeckis", "num_movie" : 1 }

$sum后面的1表示只是把电影数加起来，但我们也可以统计别的数据，比如两位导演谁的赞比较多：

 db.movie.aggregate([{$group:{_id:'$directed_by',num_likes:{$sum:'$likes'}}}])
 
 输出：
 
 { "_id" : "David Fincher", "num_likes" : 358753 }
{ "_id" : "Robert Zemeckis", "num_likes" : 864377 }

除了$sum：

统计平均的赞

db.movie.aggregate([{$group:{_id:'$directed_by',num_movie:{$avg:'$likes'}}}])

返回每个导演电影中的第一部的赞数：

db.movie.aggregate([{$group:{_id:'$directed_by',num_movie:{$first:'$likes'}}}]
```


### 10、原子性


```
db.movie.findAndModify(
			{
			query:{'title':'Forrest Gump'},
			update:{$inc:{likes:10}}
			}
		      )
```

### 11、文本搜索

假定我们要对标题进行文本搜索


```
db.movie.ensureIndex({title:'text'})
```
接着我们就可以对标题进行文本搜索了，比如，查找带有"Gump"的标题：


```
db.movie.find({$text:{$search:"Gump"}}).pretty()

假设我们要搜索的key是一个长长的文档，这种text search的方便性就显现出来了。MongoDB目前支持15种语言的文本搜索。
```

### 12、正则表达式

查找标题以b结尾的电影信息：


```
db.movie.find({title:{$regex:'.*b$'}}).pretty()

db.movie.find({title:/.*b$/}).pretty()
```

查找含有'Fight'标题的电影：


```
db.movie.find({title:/Fight/}).pretty()
```
注意以上匹配都是区分大小写的，如果你要让其不区分大小写，则可以：


```
db.movie.find({title:{$regex:'fight.*b',$options:'$i'}}).pretty()

$i是insensitive的意思。这样的话，即使是小写的fight，也能搜到了。
```

参考链接：https://github.com/StevenSLXie/Tutorials-for-Web-Developers/blob/master/MongoDB%20%E6%9E%81%E7%AE%80%E5%AE%9E%E8%B7%B5%E5%85%A5%E9%97%A8.md
