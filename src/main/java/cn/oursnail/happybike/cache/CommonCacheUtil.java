package cn.oursnail.happybike.cache;

import cn.oursnail.happybike.exception.HappyBikeException;
import cn.oursnail.happybike.vo.UserElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.util.Map;

/**
 * @Author 【swg】.
 * @Date 2018/3/14 13:41
 * @DESC
 * @CONTACT 317758022@qq.com
 */
@Component
@Slf4j
public class CommonCacheUtil {
    private static final String TOKEN_PREFIX = "token.";

    private static final String USER_PREFIX = "user.";

    @Autowired
    private JedisPoolWrapper jedisPoolWrapper;

    /**
     * 缓存 可以value 永久
     * @param key
     * @param value
     */
    public void cache(String key, String value) {
        try {
            JedisPool pool = jedisPoolWrapper.getJedisPool();
            if (pool != null) {
                try (Jedis Jedis = pool.getResource()) {
                    Jedis.select(0);
                    Jedis.set(key, value);
                }
            }
        } catch (Exception e) {
            log.error("redis存值失败", e);
            throw new HappyBikeException("redis报错");
        }
    }

    /**
     * 获取缓存key
     * @param key
     * @return
     */
    public String getCacheValue(String key) {
        String value = null;
        try {
            JedisPool pool = jedisPoolWrapper.getJedisPool();
            if (pool != null) {
                try (Jedis Jedis = pool.getResource()) {
                    Jedis.select(0);
                    value = Jedis.get(key);
                }
            }
        } catch (Exception e) {
            log.error("redis获取指失败", e);
            throw new HappyBikeException("redis报错");
        }
        return value;
    }

    /**
     * 设置key value 以及过期时间
     * @param key
     * @param value
     * @param expiry
     * @return
     */
    public long cacheNxExpire(String key, String value, int expiry) {
        long result = 0;
        try {
            JedisPool pool = jedisPoolWrapper.getJedisPool();
            if (pool != null) {
                try (Jedis jedis = pool.getResource()) {
                    jedis.select(0);
                    result = jedis.setnx(key, value);
                    jedis.expire(key, expiry);
                }
            }
        } catch (Exception e) {
            log.error("redis塞值和设置缓存时间失败", e);
            throw new HappyBikeException("redis报错");
        }

        return result;
    }

    /**
     * 删除缓存key
     * @param key
     */
    public void delKey(String key) {
        JedisPool pool = jedisPoolWrapper.getJedisPool();
        if (pool != null) {

            try (Jedis jedis = pool.getResource()) {
                jedis.select(0);
                try {
                    jedis.del(key);
                } catch (Exception e) {
                    log.error("从redis中删除失败", e);
                    throw new HappyBikeException("redis报错");
                }
            }
        }
    }

    /**
     * 登录时设置token
     * @param ue
     */
    public void putTokenWhenLogin(UserElement ue) {
        JedisPool pool = jedisPoolWrapper.getJedisPool();
        if (pool != null) {

            try (Jedis jedis = pool.getResource()) {
                jedis.select(0);
                /*redis事务*/
                Transaction trans = jedis.multi();
                try {
                    trans.del(TOKEN_PREFIX + ue.getToken());
                    /*token为value，value为用户信息*/
                    trans.hmset(TOKEN_PREFIX + ue.getToken(), ue.toMap());
                    /*设置过时时间*/
                    trans.expire(TOKEN_PREFIX + ue.getToken(), 2592000);
                    /*userid为key,token为value*/
                    trans.sadd(USER_PREFIX + ue.getUserId(), ue.getToken());
                    trans.exec();
                } catch (Exception e) {
                    trans.discard();
                    log.error("登陆时缓存token到redis失败", e);
                }
            }
        }
    }

    public UserElement getUserByToken(String token) {
        UserElement ue = null;
        JedisPool pool = jedisPoolWrapper.getJedisPool();
        if (pool != null) {

            try (Jedis jedis = pool.getResource()) {
                jedis.select(0);
                try {
                    Map<String, String> map = jedis.hgetAll(TOKEN_PREFIX + token);
                    if (!CollectionUtils.isEmpty(map)) {
                        ue = UserElement.fromMap(map);
                    } else {
                        log.warn("fail to find cache element for token");
                        throw new HappyBikeException("根据token获取用户信息失败");
                    }
                } catch (Exception e) {
                    log.error("Fail to get user by token in redis", e);
                    throw e;
                }
            }
        }
        return ue;
    }

    public int cacheForVerificationCode(String key, String value, String type, int timeout, String ip) {
        try {
            JedisPool pool = jedisPoolWrapper.getJedisPool();
            if (pool != null) {
                try (Jedis jedis = pool.getResource()) {
                    jedis.select(0);
                    //对ip进行判断，是否是发送过的，并且判断发送次数
                    String ipKey = "ip." + ip;
                    if(ip==null){
                        return 3;
                    }else{
                        String ipSendCount = jedis.get(ipKey);
                        try {
                            if (ipSendCount != null && Integer.parseInt(ipSendCount) >= 3) {
                                return 3;
                            }
                        } catch (NumberFormatException e) {
                            log.error("Fail to process ip send count", e);
                            return 3;
                        }
                    }
                    //将key和value塞进缓存中，如果缓存中不存在则塞入成功返回1，否则返回0
                    long succ = jedis.setnx(key, value);
                    //返回0说明此时缓存中仍然存在这个值，说明验证码还没过期就又发送了一遍，此时我们是不给他发短信的
                    if (succ == 0) {
                        return 1;
                    }
                    //根据手机号码进行判断
                    String sendCount = jedis.get(key+"."+type);
                    //如果不为空并且超出了次数，也不发短信
                    try {
                        if (sendCount != null && Integer.parseInt(sendCount) >= 3) {
                            jedis.del(key);
                            return 2;
                        }
                    } catch (NumberFormatException e) {
                        log.error("Fail to process send count", e);
                        jedis.del(key);
                        return 2;
                    }
                    //走到这一步说明没有什么恶意请求
                    try {
                        //设置当前这个验证码的过期时间
                        jedis.expire(key, timeout);
                        //对验证手机号码的value增1
                        long val = jedis.incr(key + "." + type);
                        if (val == 1) {
                            //设置验证手机号码的key的过期时间为一天，过期后就自动删除，即第二天又可以发送3条短信了
                            jedis.expire(key + "." + type, 86400);
                        }
                        //对验证ip的value增1
                        jedis.incr(ipKey);
                        if (val == 1) {
                            //设值验证ip的key的过期时间为一天，过期后就自动删除，即第二天又可以发送3条短信了
                            jedis.expire(ipKey, 86400);
                        }
                    } catch (Exception e) {
                        log.error("Fail to cache data into redis", e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Fail to cache for expiry", e);
            throw new HappyBikeException("Fail to cache for expiry");
        }
        //一切正常返回0
        return 0;
    }
}
