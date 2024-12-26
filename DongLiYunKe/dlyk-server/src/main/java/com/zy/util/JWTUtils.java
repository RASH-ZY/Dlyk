package com.zy.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.zy.model.TUser;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * jwt工具类
 *
 */
public class JWTUtils {

    /**
     * 自定义密钥
     */
    public static final String SECRET = "dY8300olWQ3345;1d<3w48";

    /**
     * 生成JWT （token）
     *
     */
    public static String createJWT(String userJSON) {
        //组装头数据
        Map<String, Object> header = new HashMap<>();
        header.put("alg", "HS256"); //token的加密算法
        header.put("typ", "JWT"); //token的类型，只能是JWT

        return JWT.create()
                //头部
                .withHeader(header)

                //负载
                .withClaim("user", userJSON) //自定义数据作为负载

                //签名
                .sign(Algorithm.HMAC256(SECRET)); //指定加密算法和密钥
    }

    /**
     * 校验JWT
     *
     * @param jwt 要校验的jwt的字符串
     */
    public static Boolean verifyJWT(String jwt) {
        try {
            // 创建校验器对象，需要指定对应的算法+密钥
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(SECRET)).build();

            //校验JWT，如果没有抛出异常，说明校验通过，否则校验不通过
            //DecodedJWT decodedJWT = jwtVerifier.verify(jwt); //返回一个解码后的JWT
            jwtVerifier.verify(jwt);

            return true;
        } catch (Exception e) {
            //非法token(过期、篡改...)
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 解析JWT的数据
     *
     */
    public static void parseJWT(String jwt) {
        try {
            // 使用秘钥创建一个校验器对象
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(SECRET)).build();

            //校验JWT，得到一个解码后的jwt对象
            DecodedJWT decodedJWT = jwtVerifier.verify(jwt);

            //通过解码后的jwt对象，就可以获取里面的负载数据
            Claim nickClaim = decodedJWT.getClaim("nick");
            Claim ageClaim = decodedJWT.getClaim("age");
            Claim phoneClaim = decodedJWT.getClaim("phone");
            Claim birthDayClaim = decodedJWT.getClaim("birthDay");


            String nick = nickClaim.asString();
            int age = ageClaim.asInt();
            String phone = phoneClaim.asString();
            Date birthDay = birthDayClaim.asDate();
            
            System.out.println(nick + " -- " + age + " -- " + phone + " -- " + birthDay);
        } catch (TokenExpiredException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * 解析JWT的数据，返回user对象数据
     * user对象数据转为JSON字符串，再转为Java对象
     */
    public static TUser parseUserFromJWT(String jwt) {
        try {
            // 使用秘钥创建一个校验器对象
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(SECRET)).build();

            //校验JWT，得到一个解码后的jwt对象
            DecodedJWT decodedJWT = jwtVerifier.verify(jwt);

            //通过解码后的jwt对象，就可以获取里面的负载数据
            Claim userClaim = decodedJWT.getClaim("user");

            String userJSON = userClaim.asString();

            return JSONUtils.toBean(userJSON, TUser.class);
        } catch (TokenExpiredException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
