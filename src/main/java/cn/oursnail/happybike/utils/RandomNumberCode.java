package cn.oursnail.happybike.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Random;

/**
 * @Author 【swg】.
 * @Date 2018/3/15 9:45
 * @DESC
 * @CONTACT 317758022@qq.com
 */
public class RandomNumberCode {
    public static String verCode(){
        Random random =new Random();
        return StringUtils.substring(String.valueOf(random.nextInt()*-10), 2, 6);
    }
    public static String randomNo(){
        Random random =new Random();
        return String.valueOf(Math.abs(random.nextInt()*-10));
    }
}
