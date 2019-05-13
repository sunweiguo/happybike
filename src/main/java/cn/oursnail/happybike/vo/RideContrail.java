package cn.oursnail.happybike.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author 【swg】.
 * @Date 2018/3/15 14:28
 * @DESC
 * @CONTACT 317758022@qq.com
 */
@Data
public class RideContrail {
    private String rideRecordNo;

    private Long bikeNo;

    private List<Point> contrail;
}
