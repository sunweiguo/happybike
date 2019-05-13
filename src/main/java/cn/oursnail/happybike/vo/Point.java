package cn.oursnail.happybike.vo;

import lombok.Data;

/**
 * @Author 【swg】.
 * @Date 2018/3/15 11:39
 * @DESC
 * @CONTACT 317758022@qq.com
 */
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
