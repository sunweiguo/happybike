package cn.oursnail.happybike.service.impl;

import cn.oursnail.happybike.dao.mapper.RideRecordMapper;
import cn.oursnail.happybike.entity.RideRecord;
import cn.oursnail.happybike.service.RideRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author 【swg】.
 * @Date 2018/3/15 14:24
 * @DESC
 * @CONTACT 317758022@qq.com
 */
@Service
@Slf4j
public class RideRecordServiceImpl implements RideRecordService{
    @Autowired
    private RideRecordMapper rideRecordMapper;


    @Override
    public List<RideRecord> listRideRecord(Long userId, Long lastId) {
        List<RideRecord> list = rideRecordMapper.selectRideRecordPage(userId,lastId);

        return list;
    }
}
