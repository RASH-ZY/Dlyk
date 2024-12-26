package com.zy.config.filter;


import com.zy.constant.Constants;
import com.zy.model.TUser;
import com.zy.result.CodeEnum;
import com.zy.result.R;
import com.zy.service.RedisService;
import com.zy.util.JSONUtils;
import com.zy.util.JWTUtils;
import com.zy.util.ResponseUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 自定义过滤器
 */
@Component
public class TokenVerifyFilter extends OncePerRequestFilter {

    @Resource
    private RedisService redisService;

    //spring boot框架的ioc容器中已经创建好了该线程池，可以注入直接使用
    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        /**
         * 登录请求，放行
         */
        if (request.getRequestURI().equals(Constants.LOGIN_URI)) {
            //可以添加登录前的验证，如验证码

            //如果是登录请求，此时还没有生成jwt，那不需要对登录请求进行jwt验证，直接放行
            //验证jwt通过了 ，让Filter链继续执行，也就是继续执行下一个Filter
            filterChain.doFilter(request, response);
        } else {
            /**
             * 其他请求，需要验证token
             */


            /**
             * 获取token：
             *     1.从请求路径中获取token
             *     2.从请求头中获取token
             */
            String token = null;
            //导出Excel表时，EXPORT_EXCEL_URI = "/api/exportExcel"
            if (request.getRequestURI().equals(Constants.EXPORT_EXCEL_URI)) {
                //从请求路径的参数中获取token
                token = request.getParameter(Constants.TOKEN_NAME);
            } else {
                //其他请求都是从请求头中获取token
                token = request.getHeader(Constants.TOKEN_NAME);
            }

            /**
             * 情况一：token为空
             */
            if (!StringUtils.hasText(token)) {
                R result = R.FAIL(CodeEnum.TOKEN_IS_EMPTY);
                //把R对象转成json
                String resultJSON = JSONUtils.toJSON(result);
                //把R以json返回给前端
                ResponseUtils.write(response, resultJSON);
                return;
            }

            /**
             * 情况二：token被修改
             */
            if (!JWTUtils.verifyJWT(token)) {
                //token验证未通过统一结果
                R result = R.FAIL(CodeEnum.TOKEN_IS_ERROR);

                //把R对象转成json
                String resultJSON = JSONUtils.toJSON(result);

                //把R以json返回给前端
                ResponseUtils.write(response, resultJSON);

                return;
            }

            /**
             * 情况三：token已过期
             */
            //解析token
            TUser tUser = JWTUtils.parseUserFromJWT(token);
            //从redis中获取token
            String redisToken = (String) redisService.getValue(Constants.REDIS_JWT_KEY + tUser.getId());
            //redis中token为空，即token已过期
            if (!StringUtils.hasText(redisToken)) {
                //token验证未通过统一结果
                R result = R.FAIL(CodeEnum.TOKEN_IS_EXPIRED);

                //把R对象转成json
                String resultJSON = JSONUtils.toJSON(result);

                //把R以json返回给前端
                ResponseUtils.write(response, resultJSON);

                return;
            }

            /**
             * 情况四：token不匹配
             */
            //请求携带的token与redis中token不一致
            if (!token.equals(redisToken)) {
                //token验证未通过的统一结果
                R result = R.FAIL(CodeEnum.TOKEN_IS_NONE_MATCH);

                //把R对象转成json
                String resultJSON = JSONUtils.toJSON(result);

                //把R以json返回给前端
                ResponseUtils.write(response, resultJSON);
                return;
            }

            /**
             * 情况五：token验证通过
             */
            //jwt验证通过了，那么在spring security的上下文环境中要设置一下，设置当前这个人是登录过的，你后续不要再拦截他了
            //TUser中重写的getAuthorities方法，获取用户权限信息
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(tUser, tUser.getLoginPwd(), tUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            //刷新一下token, 延长token（异步处理，new一个线程去执行）
//            new Thread(() -> {
//                //刷新token
//                String rememberMe = request.getHeader("rememberMe");
//                if (Boolean.parseBoolean(rememberMe)) {
//                    redisService.expire(Constants.REDIS_JWT_KEY + tUser.getId(), Constants.EXPIRE_TIME, TimeUnit.SECONDS);
//                } else {
//                    redisService.expire(Constants.REDIS_JWT_KEY + tUser.getId(), Constants.DEFAULT_EXPIRE_TIME, TimeUnit.SECONDS);
//                }
//            }).start();

            /**
             * 验证通过后，根据用户是否勾选免登录，设置token过期时间
             */
            //异步处理（更好的方式，使用线程池去执行）
            threadPoolTaskExecutor.execute(() -> {
                //刷新token
                String rememberMe = request.getHeader("rememberMe");
                if (Boolean.parseBoolean(rememberMe)) {
                    //rememberMe为true，刷新七天
                    redisService.expire(Constants.REDIS_JWT_KEY + tUser.getId(), Constants.EXPIRE_TIME, TimeUnit.SECONDS);
                } else {
                    //rememberMe为false，刷新30分钟
                    redisService.expire(Constants.REDIS_JWT_KEY + tUser.getId(), Constants.DEFAULT_EXPIRE_TIME, TimeUnit.SECONDS);
                }
            });

            //验证jwt通过了 ，让Filter链继续执行，也就是继续执行下一个Filter
            filterChain.doFilter(request, response);
        }
    }
}
