package com.riskcontrol.config;


import org.apache.catalina.LifecycleException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

/**
 * Redis 配置类
 */
@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private int port;
    @Value("${spring.data.redis.password:}")
    private String password;
    @Value("${spring.data.redis.database:0}")
    private int db;

    @Value("${spring.data.redis.jedis.pool.max-active}")
    private int maxTotal;
    @Value("${spring.data.redis.jedis.pool.max-idle}")
    private int maxIdle;
    @Value("${spring.data.redis.jedis.pool.min-idle}")
    private int minIdle;
    @Value("${spring.data.redis.jedis.pool.max-wait}")
    private Duration maxWait;


    @Bean(name = "jedisPool")
    public JedisPool jedisPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxWait(maxWait);
        poolConfig.setJmxEnabled(false);

        // 无密码传null
        if (StringUtils.isEmpty(password)) {
            return new JedisPool(poolConfig, host, port, 5000, null, db);
        } else {
            return new JedisPool(poolConfig, host, port, 5000, password, db);
        }
    }

//    @Bean
//    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(factory);
//
//        // String序列化：key、hashKey
//        StringRedisSerializer strSerializer = new StringRedisSerializer();
//        // JSON序列化：value、hashValue
//        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
//
//        template.setKeySerializer(strSerializer);
//        template.setHashKeySerializer(strSerializer);
//        template.setValueSerializer(jsonSerializer);
//        template.setHashValueSerializer(jsonSerializer);
//
//        template.afterPropertiesSet();
//        return template;
//    }
}