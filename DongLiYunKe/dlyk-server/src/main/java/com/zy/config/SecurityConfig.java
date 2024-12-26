package com.zy.config;

import com.zy.config.filter.TokenVerifyFilter;
import com.zy.config.handler.MyAccessDeniedHandler;
import com.zy.config.handler.MyAuthenticationFailureHandler;
import com.zy.config.handler.MyAuthenticationSuccessHandler;
import com.zy.config.handler.MyLogoutSuccessHandler;
import com.zy.constant.Constants;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@EnableMethodSecurity //开启方法级别的权限检查
@Configuration
public class SecurityConfig {

    @Resource
    private MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;

    @Resource
    private MyAuthenticationFailureHandler myAuthenticationFailureHandler;

    @Resource
    private MyLogoutSuccessHandler myLogoutSuccessHandler;

    @Resource
    private MyAccessDeniedHandler myAccessDeniedHandler;

    @Resource
    private TokenVerifyFilter tokenVerifyFilter;

    @Bean
    public PasswordEncoder passwordEncoder(){
        // BCryptPasswordEncoder 是一种强大的单向哈希算法，常用于加密用户的密码。
        // 它不仅对密码进行哈希处理，还会添加盐值（salt），使得即使两个用户的密码相同，它们的哈希值也会不同。
        // 这增加了破解密码的难度
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, CorsConfigurationSource configurationSource) throws Exception {
        // 禁用跨站请求伪造（关闭跨域请求保护）
        return httpSecurity
                //配置表单登录的相关设置
                .formLogin((formLogin) -> {
                    formLogin.loginProcessingUrl(Constants.LOGIN_URI) // 指定登录处理地址，不需要写controller
                            .usernameParameter("loginAct") //指定登录表单中用户名的参数名称
                            .passwordParameter("loginPwd") //指定登录表单中密码的参数名称
                            .successHandler(myAuthenticationSuccessHandler) //登录成功的处理逻辑
                            .failureHandler(myAuthenticationFailureHandler); //登录失败的处理逻辑
                })

                //授权http请求，authorizeHttpRequests：定义哪些 URL 需要进行权限检查
                .authorizeHttpRequests((authorize) -> {
                    //允许所有用户（包括未认证的用户）访问登录页面，即对所有用户开放登录请求
                    authorize.requestMatchers(Constants.LOGIN_URI).permitAll()
                             // 所有其他请求都需要经过身份验证才能访问
                             .anyRequest() // 所有其他请求
                             .authenticated(); // 都需要经过身份验证才能访问
                })

                .csrf(AbstractHttpConfigurer::disable) //禁止跨域请求保护

                // 配置跨域资源共享（CORS），允许前端应用从不同的域名访问后端 API
                .cors((cors) -> {
                    // 使用自定义的 CorsConfigurationSource 来配置 CORS 规则
                    cors.configurationSource(configurationSource);
                })

                //session管理策略
                .sessionManagement((session) -> {
                    //禁用session
                    //每次请求都携带令牌，不需要服务器端存储会话信息
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })

                // 添加自定义过滤器，在 LogoutFilter 之前添加了一个自定义的 TokenVerifyFilter
                // 用于验证请求中的令牌（如 JWT），确保只有合法的用户可以访问受保护的资源
                .addFilterBefore(tokenVerifyFilter, LogoutFilter.class)

                //配置退出登录的相关设置
                .logout((logout) -> {
                    //退出时将请求提交到该地址，由框架处理，无需编写Controller
                    logout.logoutUrl("/api/logout")
                            .logoutSuccessHandler(myLogoutSuccessHandler); // 指定注销成功后的处理逻辑
                })

                //无权限时触发该异常处理
                .exceptionHandling((exceptionHandling) -> {
                    exceptionHandling.accessDeniedHandler(myAccessDeniedHandler);
                })

                .build();
    }

    //处理跨域...
    @Bean
    public CorsConfigurationSource configurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*")); //允许任何来源http://localhost:8081
        configuration.setAllowedMethods(Arrays.asList("*")); //允许任何请求get post put delete
        configuration.setAllowedHeaders(Arrays.asList("*")); //允许任何请求头

        // 将 CORS 配置应用到所有 URL 路径
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
