package com.zy.config.handler;

//import com.zy.constant.Constants;
import com.zy.constant.Constants;
import com.zy.model.TUser;
import com.zy.result.R;
//import com.zy.service.RedisService;
import com.zy.service.RedisService;
import com.zy.util.JSONUtils;
import com.zy.util.JWTUtils;
import com.zy.util.ResponseUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Resource
    private RedisService redisService;

    /**
     * 用户登录成功/认证成功
     * @param request
     * @param response
     * @param authentication
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        //在安全框架中获取用户登录信息/认证信息
        TUser tUser = (TUser) authentication.getPrincipal();

        //1、生成jwt
        //tUser对象转成json作为负载数据放入jwt
        String userJSON = JSONUtils.toJSON(tUser);
        String jwt = JWTUtils.createJWT(userJSON);

        //2、将jwt写入Redis
        //key：Constants.REDIS_JWT_KEY + tUser.getId()
        //value：jwt
        redisService.setValue(Constants.REDIS_JWT_KEY + tUser.getId(), jwt);
        //根据key获取value，即获取token
        String a = redisService.getValue(Constants.REDIS_JWT_KEY + tUser.getId()).toString();

        //3、设置jwt过期时间，若选择了"记住我"则为7天，没有则30分钟
        String rememberMe = request.getParameter("rememberMe");
        if(Boolean.parseBoolean(rememberMe)){
            //设置token过期时间为7天
            redisService.expire(Constants.REDIS_JWT_KEY + tUser.getId(), Constants.EXPIRE_TIME, TimeUnit.SECONDS);
        } else {
            //设置token过期时间为30分钟
            redisService.expire(Constants.REDIS_JWT_KEY + tUser.getId(), Constants.DEFAULT_EXPIRE_TIME, TimeUnit.SECONDS);
        }

        //登录成功的统一结果
        R result = R.OK(jwt);

        //把R对象转成json
        String resultJSON = JSONUtils.toJSON(result);

        //把R以json返回给前端
        ResponseUtils.write(response, resultJSON);
    }
}
