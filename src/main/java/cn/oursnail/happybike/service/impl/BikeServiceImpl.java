package cn.oursnail.happybike.service.impl;

import cn.oursnail.happybike.dao.UserMapper;
import cn.oursnail.happybike.dao.mapper.BikeMapper;
import cn.oursnail.happybike.dao.mapper.RideFeeMapper;
import cn.oursnail.happybike.dao.mapper.RideRecordMapper;
import cn.oursnail.happybike.dao.mapper.WalletMapper;
import cn.oursnail.happybike.entity.*;
import cn.oursnail.happybike.exception.HappyBikeException;
import cn.oursnail.happybike.resp.ApiResult;
import cn.oursnail.happybike.service.BikeService;
import cn.oursnail.happybike.utils.DateUtil;
import cn.oursnail.happybike.utils.RandomNumberCode;
import cn.oursnail.happybike.vo.BikeLocation;
import cn.oursnail.happybike.vo.UserElement;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author 【swg】.
 * @Date 2018/3/15 13:44
 * @DESC
 * @CONTACT 317758022@qq.com
 */
@Service
@Slf4j
public class BikeServiceImpl implements BikeService{
    private static final Byte NOT_VERYFY = 1;//未认证
    private static final Object BIKE_UNLOCK = 2; //单车解锁
    private static final Object BIKE_LOCK = 1;//单车锁定
    private static final Byte RIDE_END = 2;//骑行结束

    @Autowired
    private BikeMapper bikeMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RideRecordMapper rideRecordMapper;
    @Autowired
    private WalletMapper walletMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private RideFeeMapper rideFeeMapper;

    @Override
    @Transactional
    public ApiResult unLockBike(UserElement ue, Long bikeNo) {
        try{
            //检查用户是否认证
            User user = userMapper.selectByPrimaryKey(ue.getUserId());
            if(user.getVerifyFlag() == NOT_VERYFY){
                return ApiResult.createByErrorMessage("用户尚未认证");
            }
            //检查用户是否有正在进行的骑行记录
            RideRecord record = rideRecordMapper.selectRecordNotClosed(ue.getUserId());
            if (record != null) {
                return ApiResult.createByErrorMessage("存在未关闭骑行订单");
            }
            //检查用户余额是否大于一元
            Wallet wallet = walletMapper.selectByUserId(ue.getUserId());
            if (wallet.getRemainSum().compareTo(new BigDecimal(1)) < 0) {
                return ApiResult.createByErrorMessage("余额不足");
            }

            //推送单车 进行解锁 没有channelId 会报异常
            /*JSONObject notification = new JSONObject();
            notification.put("unlock", "unlock");
            BaiduPushUtil.pushMsgToSingleDevice(currentUser,"{\"title\":\"TEST\",\"description\":\"Hello Baidu push!\"}");*/

            //修改Monogodb状态
            Query query = Query.query(Criteria.where("bike_no").is(bikeNo));
            Update update = Update.update("status", BIKE_UNLOCK);
            mongoTemplate.updateFirst(query, update, "bike-position");

            //创立骑行订单 记录骑行时间
            RideRecord rideRecord = new RideRecord();
            rideRecord.setBikeNo(bikeNo);
            String recordNo = new Date().toString() + System.currentTimeMillis() + RandomNumberCode.randomNo();
            rideRecord.setRecordNo(recordNo);
            rideRecord.setStartTime(new Date());
            rideRecord.setUserid(ue.getUserId());
            rideRecordMapper.insertSelective(rideRecord);
        }catch (Exception e){
            log.error("开锁出现错误",e);
            throw new HappyBikeException("开锁出现错误");
        }
        return ApiResult.createBySuccessMessage("开锁成功");
    }

    @Override
    public ApiResult lockBike(BikeLocation bikeLocation) {
        try{
            //结束订单，计算骑行时间
            RideRecord record = rideRecordMapper.selectBikeRecordOnGoing(bikeLocation.getBikeNumber());
            if(record==null){
               return ApiResult.createByErrorMessage("骑行记录不存在");
            }
            Long userid = record.getUserid();
            //查询单车类型  查询计价信息
            Bike bike = bikeMapper.selectByBikeNo(bikeLocation.getBikeNumber());
            if(bike==null){
                return ApiResult.createByErrorMessage("单车不存在");
            }
            //根据单车类型查询计价信息
            RideFee fee =rideFeeMapper.selectBikeTypeFee(bike.getType());
            if(fee==null){
                return ApiResult.createByErrorMessage("计费信息异常");
            }
            int minUnit =fee.getMinUnit();//30分钟为计费的一个单位

            record.setEndTime(new Date());//骑行的结束时间
            record.setStatus(RIDE_END);//骑行结束标志
            Long min = DateUtil.getBetweenMin(new Date(),record.getStartTime());//获取骑行总时间
            int intMin = min.intValue();//转成int
            record.setRideTime(intMin);//放进数据库

            BigDecimal cost = BigDecimal.ZERO;//初始化金额

            //计算费用
            if(intMin/minUnit==0){
                //不足一个时间单位 按照一个时间单位算
                cost = fee.getFee();
            }else if(intMin%minUnit==0){
                //整除了时间单位 直接计费
                cost = fee.getFee().multiply(new BigDecimal(intMin/minUnit));
            }else if(intMin%minUnit!=0){
                //不整除 +1 补足一个时间单位
                cost = fee.getFee().multiply(new BigDecimal((intMin/minUnit)+1));
            }
            record.setRideCost(cost);
            int rowCount = rideRecordMapper.updateByPrimaryKeySelective(record);
            if(rowCount == 0){
                return ApiResult.createByErrorMessage("更新骑行记录失败");
            }
            //钱包扣费
            Wallet wallet = walletMapper.selectByUserId(userid);
            wallet.setRemainSum(wallet.getRemainSum().subtract(cost));
            rowCount = walletMapper.updateByPrimaryKeySelective(wallet);
            if(rowCount == 0){
                return ApiResult.createByErrorMessage("余额更新失败");
            }
            //修改mongoDB中单车状态为锁定,更新锁车的位置
            Query query = Query.query(Criteria.where("bike_no").is(bikeLocation.getBikeNumber()));
            Update update = Update.update("status",BIKE_LOCK)
                    .set("location.coordinates",bikeLocation.getCoordinates());
            mongoTemplate.updateFirst(query,update,"bike-position");
        }catch (Exception e){
            log.error("开锁出现错误",e);
            throw new HappyBikeException("开锁失败");
        }
        return ApiResult.createBySuccessMessage("开锁成功");
    }

    @Override
    public ApiResult reportLocation(BikeLocation bikeLocation) {
        try{
            //数据库中查询该单车尚未完结的订单
            RideRecord record = rideRecordMapper.selectBikeRecordOnGoing(bikeLocation.getBikeNumber());
            if(record==null){
                return ApiResult.createByErrorMessage("骑行记录不存在");
            }
            // 查询mongo中是否已经有骑行的坐标记录数据
            DBObject obj = mongoTemplate.getCollection("ride_contrail")
                    .findOne(new BasicDBObject("record_no",record.getRecordNo()));
            //没有新添加
            //已经存在 往list集合中添加坐标即可
            if(obj==null){
                List<BasicDBObject> list = new ArrayList();
                BasicDBObject temp = new BasicDBObject("loc",bikeLocation.getCoordinates());
                list.add(temp);
                BasicDBObject insertObj = new BasicDBObject("record_no",record.getRecordNo())
                        .append("bike_no",record.getBikeNo())
                        .append("contrail",list);
                mongoTemplate.insert(insertObj,"ride_contrail");
            }else {
                Query query = new Query( Criteria.where("record_no").is(record.getRecordNo()));
                Update update = new Update().push("contrail", new BasicDBObject("loc",bikeLocation.getCoordinates()));
                mongoTemplate.updateFirst(query,update,"ride_contrail");
            }
        }catch (Exception e){
            log.error("记录骑行记录出现错误",e);
            throw new HappyBikeException("记录骑行记录失败");
        }
        return ApiResult.createBySuccessMessage("记录骑行记录成功");
    }
}
