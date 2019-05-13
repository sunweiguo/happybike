package cn.oursnail.happybike.constants;

/**
 * @Author 【swg】.
 * @Date 2018/3/14 10:59
 * @DESC
 * @CONTACT 317758022@qq.com
 */
public class Constants {
    /**自定义状态码 start**/
    public static final int RESP_STATUS_OK = 200;

    public static final int RESP_STATUS_NOAUTH = 401;

    public static final int RESP_STATUS_INTERNAL_ERROR = 500;

    public static final int RESP_STATUS_BADREQUEST = 400;
    /**自定义状态码 end**/

    public static final String REQUEST_TOKEN_KEY = "user-token";
    /**客户端版本**/
    public static final String REQUEST_VERSION_KEY = "version";
    /**客户端平台 android/ios**/
    public static final String REQUEST_PLATFORM_KEY = "platform";

    /**秒滴SMS start**/
    public static final String MDSMS_ACCOUNT_SID = "fbfdd5bd437a47d89f98c93ec1912f84";

    public static final String MDSMS_AUTH_TOKEN = "4f0d9f14f44444798a23dd5f89b52923";

    public static final String MDSMS_REST_URL = "https://api.miaodiyun.com/20150822";

    public static final String MDSMS_VERCODE_TPLID = "93696219";

    /**秒滴SMS end**/

    /***七牛keys start****/
    public static final String QINIU_ACCESS_KEY="nEtJ89BgDDjEaB8yyFWGu-IUohR0Fpv299cGAQxU";

    public static final String QINIU_SECRET_KEY="qXb6cXyPdYv8ch_xvd2gHDjqVezJ9MoLMcf0zRDc";

    public static final String QINIU_HEAD_IMG_BUCKET_NAME="njupt";

    public static final String QINIU_HEAD_IMG_BUCKET_URL="http://oyii3l15f.bkt.clouddn.com/";
    /***七牛keys end****/

    /**百度云推送 start 这里我没填，只是关注了推送的逻辑**/
    public static final String BAIDU_YUN_PUSH_API_KEY="";

    public static final String BAIDU_YUN_PUSH_SECRET_KEY="";

    public static final String CHANNEL_REST_URL = "api.push.baidu.com";
    /**百度云推送end**/

}
