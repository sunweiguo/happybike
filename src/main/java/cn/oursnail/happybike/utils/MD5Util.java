package cn.oursnail.happybike.utils;

import org.apache.commons.codec.digest.DigestUtils;
/**
 * @Author 【swg】.
 * @Date 2017/11/9 14:52
 * @DESC
 * @CONTACT 317758022@qq.com
 */
public class MD5Util {
    public static String getMD5(String source){
        return DigestUtils.md5Hex(source);
    }
}
