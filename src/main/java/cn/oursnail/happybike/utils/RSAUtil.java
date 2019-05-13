package cn.oursnail.happybike.utils;

import javax.crypto.Cipher;
import java.io.InputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @Author 【swg】.
 * @Date 2018/3/14 10:41
 * @DESC
 * @CONTACT 317758022@qq.com
 */
public class RSAUtil {
    public static final String KEY_ALGORITHM = "RSA";

    /**
     * 私钥字符串
     */
    private static String PRIVATE_KEY ="";
    /**
     * 公钥字符串
     */
    private static String PUBLIC_KEY ="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC4WFRY/OIB8pVr440aR/+LPvJY1oBkT+FxqJ8lOv0SQMpXxZlaGxrTTOkcAnvLvbwmeagHBCluVOoA+lht98wsMNBq2H2g8sgcHJEM4y0oHr73nui/rV7ci2wDIMSJU1tkh+xV8jIkXMvNEda3Fd7ivOGMSQ5+YB5c/oqMo9GwmwIDAQAB";

    /**
     * 从enc_pri文件读取密钥字符串
     * @throws Exception
     */
    public static void convert() throws Exception {
        byte[] data = null;

        try {
            InputStream is = RSAUtil.class.getResourceAsStream("/enc_pri");
            int length = is.available();
            data = new byte[length];
            is.read(data);
        } catch (Exception e) {
        }

        String dataStr = new String(data);
        try {
            PRIVATE_KEY = dataStr;
        } catch (Exception e) {
        }

        if (PRIVATE_KEY == null) {
            throw new Exception("Fail to retrieve key");
        }
    }

    /**
     * 私钥解密
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] data) throws Exception {
        convert();
        byte[] keyBytes = Base64Util.decode(PRIVATE_KEY);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        return cipher.doFinal(data);
    }

    /**
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, String key) throws Exception {
        byte[] keyBytes = Base64Util.decode(key);
        X509EncodedKeySpec pkcs8KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicKey = keyFactory.generatePublic(pkcs8KeySpec);

        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    public static void main(String[] args) throws Exception {
        //生成一对RSA的公钥和私钥
//        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
//        keyPairGen.initialize(1024);
//        KeyPair keyPair = keyPairGen.generateKeyPair();
//        PrivateKey privateKey = keyPair.getPrivate();
//        PublicKey publicKey = keyPair.getPublic();
//        System.out.println("私钥："+Base64.encode(privateKey.getEncoded()));
//        System.out.println("公钥："+Base64.encode(publicKey.getEncoded()));

        String data = "我在南邮玩耍呢！！！";
        //用公钥加密
        byte[] enResult = encryptByPublicKey(data.getBytes("UTF-8"),PUBLIC_KEY);
        System.out.println(enResult);//[B@2ff4f00f
        //用私钥解密
        byte[] deResult = decryptByPrivateKey(enResult);
        System.out.println(new String(deResult,"UTF-8"));//我在南邮玩耍呢！！！
    }
}
