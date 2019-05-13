package cn.oursnail.happybike.vo;

import lombok.Data;

/**
 * @Author 【swg】.
 * @Date 2018/3/14 11:25
 * @DESC
 * @CONTACT 317758022@qq.com
 */
@Data
public class LoginInfo {
    /*登陆信息*/
    private String data;
    /*RSA加密的AES密钥*/
    private String key;
}
