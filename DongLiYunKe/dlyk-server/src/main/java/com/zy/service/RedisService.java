package com.zy.service;

import java.util.concurrent.TimeUnit;

public interface RedisService {

    //写入Redis
    void setValue(String key, Object value);

    //读取Redis
    Object getValue(String key);

    //移出Redis
    Boolean removeValue(String key);

    //设置key的过期时间
    Boolean expire(String key, Long timeOut, TimeUnit timeUnit);
}
