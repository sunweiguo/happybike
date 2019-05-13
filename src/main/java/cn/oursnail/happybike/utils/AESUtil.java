package cn.oursnail.happybike.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @Author 【swg】.
 * @Date 2018/3/14 10:35
 * @DESC 对要传输的数据进行AES对称加密
 * @CONTACT 317758022@qq.com
 */
public class AESUtil {
    public static final String KEY_ALGORITHM = "AES";
    public static final String KEY_ALGORITHM_MODE = "AES/CBC/PKCS5Padding";

    /**
     * AES对称加密
     * @param data
     * @param key key需要16位
     * @return
     */
    public static String encrypt(String data , String key) {
        try {
            SecretKeySpec spec = new SecretKeySpec(key.getBytes("UTF-8"),KEY_ALGORITHM);
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM_MODE);
            cipher.init(Cipher.ENCRYPT_MODE , spec,new IvParameterSpec(new byte[cipher.getBlockSize()]));
            byte[] bs = cipher.doFinal(data.getBytes("UTF-8"));
            return Base64Util.encode(bs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }


    /**
     * AES对称解密 key需要16位
     * @param data
     * @param key
     * @return
     */
    public static String decrypt(String data, String key) {
        try {
            SecretKeySpec spec = new SecretKeySpec(key.getBytes("UTF-8"), KEY_ALGORITHM);
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM_MODE);
            cipher.init(Cipher.DECRYPT_MODE , spec , new IvParameterSpec(new byte[cipher.getBlockSize()]));
            byte[] originBytes = Base64Util.decode(data);
            byte[] result = cipher.doFinal(originBytes);
            return new String(result,"UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }

    public static void main(String[] args) throws Exception {
//        String key = "1234567890qwerty";
//        String dataToEn = "hello world....哈哈哈";
//        String enResult = encrypt(dataToEn,key);
//        System.out.println("对称加密后的数据为："+enResult);
//        String deResult = decrypt(enResult,key);
//        System.out.println("解密："+deResult);


        //结合在一起进行测试
        //key，16位或16的倍数即可，这里只是测试一下
        String key = "1234567890qwerty";
//数据
        String dataToEn = "{ 'mobile':'15895967012' , 'code':'6666' ,'platform':'android'}";
//用对称加密算法对数据进行对称加密
        String enResult = encrypt(dataToEn,key);
        System.out.println("AES对明文加密后的结果："+enResult);

//用RSA对key用公钥进行非对称加密
        byte[] enkey = RSAUtil.encryptByPublicKey(key.getBytes("UTF-8"),"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC4WFRY/OIB8pVr440aR/+LPvJY1oBkT+FxqJ8lOv0SQMpXxZlaGxrTTOkcAnvLvbwmeagHBCluVOoA+lht98wsMNBq2H2g8sgcHJEM4y0oHr73nui/rV7ci2wDIMSJU1tkh+xV8jIkXMvNEda3Fd7ivOGMSQ5+YB5c/oqMo9GwmwIDAQAB");
//再用base64对加密后的key编码一下，保证传输
        String baseKey = Base64Util.encode(enkey);
        System.out.println("RSA对key加密后的结果："+baseKey);

//服务端根据公钥对应的私钥解密AES的key
        byte[] de = Base64Util.decode(baseKey);
        byte[] deKeyResult = RSAUtil.decryptByPrivateKey(de);
        System.out.println("key="+new String(deKeyResult,"UTF-8"));//key=1234567890qwerty

//根据解密后的key将用对称方式将数据再解密出来
        String deResult = decrypt(enResult,new String(deKeyResult,"UTF-8"));
        System.out.println("解密后的数据为："+deResult);//解密后的数据为：hello world....哈哈哈
    }
}
