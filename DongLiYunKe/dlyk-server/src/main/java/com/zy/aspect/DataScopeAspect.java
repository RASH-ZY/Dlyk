package com.zy.aspect;

import com.zy.commons.DataScope;
import com.zy.constant.Constants;
import com.zy.model.TUser;
import com.zy.query.BaseQuery;
import com.zy.util.JWTUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

import static org.springframework.web.context.request.RequestContextHolder.getRequestAttributes;

@Aspect
@Component
public class DataScopeAspect {

    //切入点
    @Pointcut(value = "@annotation(com.zy.commons.DataScope)")
    private void pointCut(){

    }

    //连接点
    @Around(value = "pointCut()") //环绕增强
    public Object process(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //拿到方法的注解
        DataScope dataScope = signature.getMethod().getDeclaredAnnotation(DataScope.class);
        String tableAlias = dataScope.tableAlias();
        String tableField = dataScope.tableField();

        //从Spring容器中获取当前请求的Request对象
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        //获取token
        String token = request.getHeader(Constants.TOKEN_NAME);
        //解析token，分析时管理员还是普通用户
        TUser tUser = JWTUtils.parseUserFromJWT(token);

        //拿到用户角色
        List<String> roleList = tUser.getRoleList();

        if(!roleList.contains("admin")) {
            //不含admin角色 查询当前用户数据 拼接sql语句
            Object params = joinPoint.getArgs()[0]; //获取方法的第一个参数
            if(params instanceof BaseQuery){
                BaseQuery query = (BaseQuery) params;
                //select * from t_user tu where tu.id = 2 普通用户
                //tu.id = 2
                query.setFilterSQL(" and " + tableAlias + " . " + tableField + " = " + tUser.getId());
            }
        }
        System.out.println("目标方法执行之前");
        Object result = joinPoint.proceed(); //执行目标方法，sql查询
        System.out.println("目标方法执行之后");
        return result;
    }
}
