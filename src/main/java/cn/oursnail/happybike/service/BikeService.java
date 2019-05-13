package cn.oursnail.happybike.service;

import cn.oursnail.happybike.resp.ApiResult;
import cn.oursnail.happybike.vo.BikeLocation;
import cn.oursnail.happybike.vo.UserElement; /**
 * @Author 【swg】.
 * @Date 2018/3/15 13:44
 * @DESC
 * @CONTACT 317758022@qq.com
 */
public interface BikeService {
    ApiResult unLockBike(UserElement currentUser, Long number);

    ApiResult lockBike(BikeLocation bikeLocation);

    ApiResult reportLocation(BikeLocation bikeLocation);
}
