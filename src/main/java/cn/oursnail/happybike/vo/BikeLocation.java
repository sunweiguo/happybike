package cn.oursnail.happybike.vo;

import lombok.Data;

/**
 * @Author 【swg】.
 * @Date 2018/3/15 11:39
 * @DESC
 * @CONTACT 317758022@qq.com
 */
@Data
public class BikeLocation {
    private String id;

    private Long bikeNumber;

    private int status;

    private Double[] coordinates;

    private Double distance;
}
