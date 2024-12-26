package com.zy.service.impl;

import com.zy.service.RedisService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl implements RedisService {

    //注入RedisTemplate，通过RedisTemplate操作Redis
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    //写入Redis
    @Override
    public void setValue(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    //读取Redis
    @Override
    public Object getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    //移出Redis
    @Override
    public Boolean removeValue(String key) {
        return redisTemplate.delete(key);
    }

    //设置key的过期时间
    @Override
    public Boolean expire(String key, Long timeOut, TimeUnit timeUnit) {
        return redisTemplate.expire(key, timeOut, timeUnit);
    }
}
