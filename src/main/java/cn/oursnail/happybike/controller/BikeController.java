package cn.oursnail.happybike.controller;

import cn.oursnail.happybike.entity.Bike;
import cn.oursnail.happybike.entity.RideRecord;
import cn.oursnail.happybike.resp.ApiResult;
import cn.oursnail.happybike.service.BikeService;
import cn.oursnail.happybike.service.RideRecordService;
import cn.oursnail.happybike.service.impl.BikeGeoService;
import cn.oursnail.happybike.vo.BikeLocation;
import cn.oursnail.happybike.vo.Point;
import cn.oursnail.happybike.vo.RideContrail;
import cn.oursnail.happybike.vo.UserElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author 【swg】.
 * @Date 2018/3/15 13:12
 * @DESC
 * @CONTACT 317758022@qq.com
 */
@RestController
@RequestMapping("/bike/")
public class BikeController extends BaseController{
    @Autowired
    private BikeGeoService bikeGeoService;
    @Autowired
    private BikeService bikeService;
    @Autowired
    private RideRecordService rideRecordService;

    @RequestMapping("/findAroundBike")
    public ApiResult findAroundBike(@RequestBody Point point ){
        List<BikeLocation> bikeList = bikeGeoService.geoNear("bike-position",null,point,10,500);
        if(bikeList.size() == 0){
            return ApiResult.createBySuccessMessage("附近500米没有单车");
        }else {
            return ApiResult.createBySuccessMessageAndData("找到单车",bikeList);
        }
    }

    @RequestMapping("/unLockBike")
    public ApiResult unLockBike(@RequestBody Bike bike){
        return bikeService.unLockBike(getCurrentUser(),bike.getNumber());
    }

    @RequestMapping("/lockBike")
    public ApiResult lockBike(@RequestBody BikeLocation bikeLocation){
        return bikeService.lockBike(bikeLocation);
    }

    @RequestMapping("/reportLocation")
    public ApiResult reportLocation(@RequestBody BikeLocation bikeLocation){
        return bikeService.reportLocation(bikeLocation);
    }

    @RequestMapping("/list/{id}")
    public ApiResult<List<RideRecord>> listRideRecord(@PathVariable("id") Long lastId){
        UserElement ue = getCurrentUser();
        List<RideRecord> list = rideRecordService.listRideRecord(ue.getUserId(),lastId);
        if(list.size() == 0){
            return ApiResult.createBySuccessMessage("没有骑行记录");
        }else {
            return ApiResult.createBySuccessMessageAndData("返回骑行记录列表成功",list);
        }
    }

    @RequestMapping("/contrail/{recordNo}")
    public ApiResult<RideContrail> rideContrail(@PathVariable("recordNo") String recordNo){
        RideContrail contrail = bikeGeoService.rideContrail("ride_contrail",recordNo);
        return ApiResult.createBySuccessMessageAndData("查询路径轨迹成功",contrail);
    }
}
