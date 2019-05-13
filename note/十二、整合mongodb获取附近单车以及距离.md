1、引入依赖：


```
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

2、yml文件配置：


```
接在spring下：

  #springdata
  data:
    # mongoDB    #mongodb note:mongo3.x will not use host and port,only use uri
    mongodb:
      uri: mongodb://localhost:27017/bike
```

3、实体类


```
单车的位置集合对应的实体类：

@Data
public class BikeLocation {
    private String id;

    private Long bikeNumber;

    private int status;

    private Double[] coordinates;

    private Double distance;
}

前台传来的用户位置实体类：

@Data
public class Point {
    public Point() {
    }
    public Point(Double[] loc){
        this.longitude = loc[0];
        this.latitude = loc[1];
    }
    public Point(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    private double longitude;

    private double latitude;
}
```

4、给MongoDB一些初始数据：


```
形如：

{
    "_id" : ObjectId("59fa81910499f31ad0a5e75d"),
    "bike_no" : 28000000,
    "location" : {
        "type" : "Point",
        "coordinates" : [ 
            118.768376, 
            32.091533
        ]
    },
    "status" : 1
}
```

注意要增加location字段的索引。


```
{
    "location" : "2dsphere"
}
```

根据传来的坐标以及范围进行查询：


```
db.getCollection('bike-position').find({location:{$nearSphere:{$geometry:{type:"Point",coordinates:[118.768376,32.091533]},$maxDistance:5000}},status:1})
```

5、BikeGeoService 程序化上面的查询

无距离的查询

```

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * @param collection 指定查询的集合
     * @param locationField 查询的字段
     * @param center 上传上来的坐标
     * @param minDistance 最小的距离
     * @param maxDistance 最大的距离
     * @param query 查询的条件
     * @param fields 限制查询出来的字段
     * @param limit 限制查询出来的个数
     * @Description 查找某经坐标点附近某范围内坐标点 由近到远 无距离
     */
    public List<BikeLocation> geoNearSphere(String collection,
                                            String locationField,
                                            Point center,
                                            long minDistance,
                                            long maxDistance,
                                            DBObject query,
                                            DBObject fields,
                                            int limit) throws BikeException {
        try {
            //先new出来用来存放查询条件
            if (query == null) {
                query = new BasicDBObject();
            }
            //locationField就是location，与上面的查询时一致的
            query.put(locationField,
                    new BasicDBObject("$nearSphere",
                            new BasicDBObject("$geometry",
                                    new BasicDBObject("type", "Point")
                                            .append("coordinates", new double[]{center.getLongitude(), center.getLatitude()}))
                                    .append("$minDistance", minDistance)
                                    .append("$maxDistance", maxDistance)
                    ));
            query.put("status", 1);
            //查询结果的集合
            List<DBObject> objList = mongoTemplate.getCollection(collection).find(query, fields).limit(limit).toArray();
            //用List存放到对应的对象中
            List<BikeLocation> result = new ArrayList<>();
            for (DBObject obj : objList) {
                BikeLocation location = new BikeLocation();
                //Integer转到Long
                location.setBikeNumber(((Integer) obj.get("bike_no")).longValue());
                location.setStatus((Integer) obj.get("status"));
                BasicDBList coordinates = (BasicDBList) ((BasicDBObject) obj.get("location")).get("coordinates");
                Double[] temp = new Double[2];
                coordinates.toArray(temp);
                location.setCoordinates(temp);
                result.add(location);
            }
            return result;
        } catch (Exception e) {
            log.error("fail to find around bike", e);
            throw new BikeException("查找附近单车失败");
        }
    }
```

可以用单元测试对这个service进行测试一下：


```
	@Test
	public void contextLoads() throws BikeException {
		List<BikeLocation> lists =  bikeGeoService.geoNearSphere("bike-position","location",
				new Point(118.768376, 32.091533),0,5000,null,null,10);
		for (BikeLocation list:lists){
			System.out.println("======"+list);
		}
	}
```

结果：


```
======BikeLocation(id=null, bikeNumber=28000003, status=1, coordinates=[118.768376, 32.091533], distance=null)
======BikeLocation(id=null, bikeNumber=28000001, status=1, coordinates=[118.772041, 32.093812], distance=null)
```

有距离的查询：


```
    /**
     * @param collection 指定查询的集合
     * @param query 查询的条件
     * @param point 用户上传的坐标
     * @param limit 限制查询出来的个数
     * @param maxDistance 最大的距离
     * @Description 查找某经坐标点附近某范围内坐标点 由近到远 有距离
     */
    public List<BikeLocation> geoNear(String collection, DBObject query, Point point, int limit, long maxDistance) throws BikeException {
        try {
            if (query == null) {
                query = new BasicDBObject();
            }
            List<DBObject> pipeLine = new ArrayList<>();
            BasicDBObject aggregate = new BasicDBObject("$geoNear",
                    new BasicDBObject("near", new BasicDBObject("type", "Point").append("coordinates", new double[]{point.getLongitude(), point.getLatitude()}))
                            .append("distanceField", "distance")
                            .append("query", new BasicDBObject())
                            .append("num", limit)
                            .append("maxDistance", maxDistance)
                            .append("spherical", true)
                            .append("query", new BasicDBObject("status", 1))
            );
            pipeLine.add(aggregate);
            Cursor cursor = mongoTemplate.getCollection(collection).aggregate(pipeLine, AggregationOptions.builder().build());
            List<BikeLocation> result = new ArrayList<>();
            while (cursor.hasNext()) {
                DBObject obj = cursor.next();
                BikeLocation location = new BikeLocation();
                location.setBikeNumber(((Integer) obj.get("bike_no")).longValue());
                BasicDBList coordinates = (BasicDBList) ((BasicDBObject) obj.get("location")).get("coordinates");
                Double[] temp = new Double[2];
                coordinates.toArray(temp);
                location.setCoordinates(temp);
                location.setDistance((Double) obj.get("distance"));
                result.add(location);
            }

            return result;
        } catch (Exception e) {
            log.error("fail to find around bike", e);
            throw new BikeException("查找附近单车失败");
        }
    }
```

测试


```
@Test
public void geoNear() throws BikeException {
	List<BikeLocation> lists =  bikeGeoService.geoNear("bike-position",null,new Point(118.768376, 32.091533),10,5000);
	for (BikeLocation list:lists){
		System.out.println("======"+list);
	}
}
```

结果：


```
======BikeLocation(id=null, bikeNumber=28000003, status=0, coordinates=[118.768376, 32.091533], distance=0.0)
======BikeLocation(id=null, bikeNumber=28000001, status=0, coordinates=[118.772041, 32.093812], distance=428.7518840364588)
======BikeLocation(id=null, bikeNumber=28000000, status=0, coordinates=[118.776591, 32.087816], distance=878.3346245371454)
```

6、service层没有问题，下面就是controller了：


```
@RequestMapping("/findAroundBike")
public ApiResult findAroundBike(@RequestBody Point point ){
    ApiResult<List<BikeLocation>> resp = new ApiResult<>();
    try {
        List<BikeLocation> bikeList = bikeGeoService.geoNear("bike-position",null,point,10,500);
        resp.setMessage("查询附近单车成功");
        resp.setData(bikeList);
    }catch (BikeException e){
        resp.setCode(e.getStatusCode());
        resp.setMessage(e.getMessage());
    }catch (Exception e){
        log.error("Fail to update bike info", e);
        resp.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
        resp.setMessage("内部错误");
    }
    return resp;
}
```

对于这个方法可以不需要授权就能查询，也可以经过登陆授权才能查询。这里假设这个方法是需要授权的：


```
"key":"user-token","value":"c8c22cd86fa1e9c517efb63c4bc20414"
"key":"version","value":"1.0"

请求体是Point,即经纬度坐标：

{
	"longitude":"118.768376",
	"latitude":"32.091533"
}

请求的方法为：localhost:8888/bike/findAroundBike
```

这里选择500米之内，有两辆车，从进到远排序，一辆是他自己，所以distance是0：


```
{
    "code": 200,
    "data": [
        {
            "bikeNumber": 28000003,
            "coordinates": [
                118.768376,
                32.091533
            ],
            "distance": 0,
            "status": 0
        },
        {
            "bikeNumber": 28000001,
            "coordinates": [
                118.772041,
                32.093812
            ],
            "distance": 428.7518840364588,
            "status": 0
        }
    ],
    "message": "查询附近单车成功"
}
```














