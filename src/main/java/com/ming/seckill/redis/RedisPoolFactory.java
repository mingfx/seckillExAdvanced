package com.ming.seckill.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

//@Service
//public class RedisPoolFactory {
//    @Autowired
//    RedisConfig redisConfig;
//
//    @Bean
//    public JedisPool JedisPoolFactory(){
//        JedisPoolConfig poolConfig = new JedisPoolConfig();
//        poolConfig.setMaxIdle(redisConfig.getPoolMaxIdle());
//        poolConfig.setMaxTotal(redisConfig.getPoolMaxTotal());
//        //我们设置的是秒
//        poolConfig.setMaxWaitMillis(redisConfig.getPoolMaxWait()*1000);
//        //timeout单位，跟进去最后是给jdk的socket用的，也是毫秒;database redis哪个库
//        JedisPool jedisPool = new JedisPool(poolConfig,redisConfig.getHost(),
//                redisConfig.getPort(),redisConfig.getTimeout()*1000,redisConfig.getPassword(),0);
//        return jedisPool;
//    }
//}

@Service
public class RedisPoolFactory {

    @Autowired
    RedisConfig redisConfig;

    @Bean
    public JedisPool JedisPoolFactory() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(redisConfig.getPoolMaxIdle());
        poolConfig.setMaxTotal(redisConfig.getPoolMaxTotal());
        poolConfig.setMaxWaitMillis(redisConfig.getPoolMaxWait() * 1000);
        JedisPool jp = new JedisPool(poolConfig, redisConfig.getHost(), redisConfig.getPort(),
                redisConfig.getTimeout()*1000, redisConfig.getPassword(), 0);
        return jp;
    }

}
