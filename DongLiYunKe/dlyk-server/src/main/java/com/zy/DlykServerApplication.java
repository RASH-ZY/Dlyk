package com.zy;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zy.model.TClue;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServlet;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationExcludeFilter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

@MapperScan(basePackages = "com.zy.mapper") //指定SpringBoot去哪个包下找Mapper接口文件(Mapper接口也就无需使用@Mapper注解了)
@SpringBootApplication
public class DlykServerApplication implements CommandLineRunner {
    //内存
    public static final Map<String, Object> cacheMap = new HashMap<>();
//    HttpServlet

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DlykServerApplication.class, args);
        String[] beanNamesForType = context.getBeanNamesForType(Executor.class);
        for (String s : beanNamesForType) {
            System.out.println(s);
        }

        Object obj = context.getBean("applicationTaskExecutor");
        System.out.println(obj);
        System.out.println("----------------------------------");
//        System.out.println("1.0/0 = " + 1.0/0); //Infinity 正浮点数除以0 --> 正无穷
//        System.out.println("-1.0/0 = " + -1.0/0); //-Infinity 负浮点数除以0 --> 负无穷
//        System.out.println("0.0/0 = " + 0.0/0); //NaN 0.0（浮点数的0）除以0 --> 非数字
//        System.out.println("0/0 = " + 0/0); // 整数除以0 --> ArithmeticException异常
//        System.out.println("1/0 = " + 1/0); //整数除以0 --> ArithmeticException异常

        //输出1 2 3 接着报异常，数组下标越界
//        int[] arr = {1,2,3};
//        for (int i = 0; i <= arr.length; i++) {
//            System.out.println(arr[i]);
//        }

        System.out.println("----------------------------------");

    }

    @Override
    public void run(String... args) throws Exception {
        //springboot项目启动后，把redisTemplate这个Bean修改一下，修改一下key和value的序列化方式

        //设置key序列化
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        //对象映射工具，Java对象 和 json对象进行相互转化
        ObjectMapper mapper = new ObjectMapper();
        //设置可见性
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        //激活类型
        mapper.activateDefaultTyping(mapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.EVERYTHING);

        //设置value序列化
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<Object>(mapper, Object.class));

        //设置hashKey序列化
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        //设置hashValue序列化
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<Object>(mapper, Object.class));
    }
}
