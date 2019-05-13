package cn.oursnail.happybike.service.impl;

import cn.oursnail.happybike.exception.HappyBikeException;
import cn.oursnail.happybike.vo.BikeLocation;
import cn.oursnail.happybike.vo.Point;
import cn.oursnail.happybike.vo.RideContrail;
import com.mongodb.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author 【swg】.
 * @Date 2018/3/15 12:56
 * @DESC
 * @CONTACT 317758022@qq.com
 */
@Service
@Slf4j
public class BikeGeoService {
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
                                            int limit){
        try {
            if(query == null)
                query = new BasicDBObject();

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

        }catch (Exception e){
            log.error("fail to find around bike", e);
            throw new HappyBikeException("查找附近单车失败");
        }
    }

    /**
     * @param collection 指定查询的集合
     * @param query 查询的条件
     * @param point 用户上传的坐标
     * @param limit 限制查询出来的个数
     * @param maxDistance 最大的距离
     * @Description 查找某经坐标点附近某范围内坐标点 由近到远 有距离
     */
    public List<BikeLocation> geoNear(String collection, DBObject query, Point point, int limit, long maxDistance) {
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
            throw new HappyBikeException("查找附近单车失败");
        }
    }

    public RideContrail rideContrail(String collectionName, String recordNo) {
        try {
            //查询集合名字是ride_contrail，骑行记录就存在里面
            DBObject obj = mongoTemplate.getCollection(collectionName).findOne(new BasicDBObject("record_no", recordNo));
            RideContrail rideContrail = new RideContrail();
            rideContrail.setRideRecordNo((String) obj.get("record_no"));
            rideContrail.setBikeNo(((Integer) obj.get("bike_no")).longValue());
            BasicDBList locList = (BasicDBList) obj.get("contrail");
            List<Point> pointList = new ArrayList<>();
            for (Object object : locList) {
                BasicDBList locObj = (BasicDBList) ((BasicDBObject) object).get("loc");
                Double[] temp = new Double[2];
                locObj.toArray(temp);
                Point point = new Point(temp);
                pointList.add(point);
            }
            rideContrail.setContrail(pointList);
            return rideContrail;
        } catch (Exception e) {
            log.error("fail to query ride contrail", e);
            throw new HappyBikeException("查询单车轨迹失败");
        }
    }
}
