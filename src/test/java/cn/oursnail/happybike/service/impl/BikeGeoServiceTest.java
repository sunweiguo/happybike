package cn.oursnail.happybike.service.impl;

import cn.oursnail.happybike.vo.BikeLocation;
import cn.oursnail.happybike.vo.Point;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @Author 【swg】.
 * @Date 2018/3/15 13:10
 * @DESC
 * @CONTACT 317758022@qq.com
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class BikeGeoServiceTest {
    @Autowired
    private BikeGeoService bikeGeoService;
    @Test
    public void contextLoads() {
        List<BikeLocation> lists =  bikeGeoService.geoNearSphere("bike-position","location",
                new Point(118.768376, 32.091533),0,5000,null,null,10);
        for (BikeLocation list:lists){
            System.out.println("======"+list);
        }
    }

    @Test
    public void geoNear() {
        List<BikeLocation> lists =  bikeGeoService.geoNear("bike-position",null,new Point(118.768376, 32.091533),10,5000);
        for (BikeLocation list:lists){
            System.out.println("======"+list);
        }
    }

}