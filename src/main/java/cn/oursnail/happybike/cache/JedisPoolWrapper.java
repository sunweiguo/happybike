package cn.oursnail.happybike.cache;

import cn.oursnail.happybike.exception.HappyBikeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutionException;

/**
 * @Author 【swg】.
 * @Date 2018/3/14 13:32
 * @DESC
 * @CONTACT 317758022@qq.com
 */
@Component
@Slf4j
public class JedisPoolWrapper {
    private JedisPool jedisPool = null;

    @Autowired
    private Parameters parameters;

    @PostConstruct
    public void init(){
        try{
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(parameters.getRedisMaxTotal());
            config.setMaxIdle(parameters.getRedisMaxIdle());
            config.setMaxWaitMillis(parameters.getRedisMaxWaitMillis());

            jedisPool = new JedisPool(config,parameters.getRedisHost(),parameters.getRedisPort(),2000);
        }catch (Exception e){
            log.error("【初始化jedis连接池失败】",e);
            throw new HappyBikeException("初始化jedis连接池失败");
        }
    }

    public JedisPool getJedisPool() {
        return jedisPool;
    }
}
