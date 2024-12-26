package com.zy.manager;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class RedisManager {

    @Resource
    private RedisTemplate redisTemplate;

    //读取 Redis List类型
    public Object getValue(String key){
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    //写入 Redis List类型
    public <T> Object setValue(String key, Collection<T> data){
        Object[] t = new Object[data.size()];
        data.toArray(t);
        return redisTemplate.opsForList().leftPushAll(key, data);
    }
}
