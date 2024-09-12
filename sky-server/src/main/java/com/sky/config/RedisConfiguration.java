package com.sky.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 此处不是必需的，因为springboot框架会自动装配redisTemplate对象
 * 但默认的key序列化器为JdkSerializationRedisSerializer，导致我们存到Redis中的数据和原始数据有差别
 * 所以在此处我们选择序列化器为StringRedisSerializer
 */
@Configuration
@Slf4j
public class RedisConfiguration {
    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory){
        log.info("开始创建redis对象");
        RedisTemplate redisTemplate = new RedisTemplate();
        //设置一个连接器
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        //设置一个redis key的序列化器
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}
