package cn.oursnail.happybike.cache;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author 【swg】.
 * @Date 2018/3/14 13:28
 * @DESC
 * @CONTACT 317758022@qq.com
 */
@Component
@Data
public class Parameters {
    /*****redis config start*******/
    @Value("${redis.host}")
    private String redisHost;
    @Value("${redis.port}")
    private int redisPort;
    //@Value("${redis.auth}")
    //private String redisAuth;
    @Value("${redis.max-idle}")
    private int redisMaxTotal;
    @Value("${redis.max-total}")
    private int redisMaxIdle;
    @Value("${redis.max-wait-millis}")
    private int redisMaxWaitMillis;
    /*****redis config end*******/

    @Value("#{'${security.noneSecurityPath}'.split(',')}")
    private List<String> noneSecurityPath;
}
