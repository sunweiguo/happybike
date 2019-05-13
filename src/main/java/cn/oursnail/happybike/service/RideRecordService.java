package cn.oursnail.happybike.service;

import cn.oursnail.happybike.entity.RideRecord;

import java.util.List;

/**
 * @Author 【swg】.
 * @Date 2018/3/15 14:24
 * @DESC
 * @CONTACT 317758022@qq.com
 */
public interface RideRecordService {
    List<RideRecord> listRideRecord(Long userId, Long lastId);
}
