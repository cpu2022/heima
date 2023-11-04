package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect //表示该类是切面类
@Component //容器启动时加入容器
@Slf4j
public class AutoFillAspect {
    //切入点表达
    //表示我要切入的点--  切入该xx包并且含有注释AutoFill
    @Pointcut(value = "execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void myPointCut(){
    }

    @Before(value = "myPointCut()") //JoinPoint joinPoint表示连接点 信息放在这
    public void autoFill(JoinPoint joinPoint){
        log.info("开始进行公共字段自动填充");

        //获得注释中的参数类型
        //1.获得增强方法对象--进行向下转型强转(因为Signature是接口)
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        //2.// 获取method上的注解 并且拿到注解上的value值，AutoFill.class为自定义注解类
        AutoFill annotation = methodSignature.getMethod().getAnnotation(AutoFill.class);
        //3.获取注解上的值
        OperationType value = annotation.value();
        log.info("value的值为:"+value);

        //获取目标的方法的参数合集
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0){
            return;
        }
        //获得第一个参数
        Object entity = args[0];
        log.info("目标的方法的参数类型为:" + entity);

        //准备赋值的数据
        //1.当前修改时间
        LocalDateTime updateNow = LocalDateTime.now();
        //2.当前修改人
        Long updateId = BaseContext.getCurrentId();

        //判断注释中参数类型如果是update执行xx如果是select执行xx(赋值)
        if(value == OperationType.UPDATE){//如果是更新
            try {
                //获得更新时间方法(通过反射)
                Method setUpdateTimes = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                //获得修改人方法(通过反射)
                Method setUpdateUsers = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //调用方法
                setUpdateTimes.invoke(entity,updateNow);
                setUpdateUsers.invoke(entity,updateId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }else if (value == OperationType.INSERT){
            try {
                //获得设置更新时间方法(通过反射)
                Method setUpdateTimes = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                //获得设置修改人方法(通过反射)
                Method setUpdateUsers = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                //获得设置创建时间方法(通过反射)
                Method setCreateTimes = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                //获得设置创建人方法(通过反射)
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);

                //调用方法
                setUpdateTimes.invoke(entity,updateNow);
                setCreateTimes.invoke(entity,updateNow);
                setCreateUser.invoke(entity,updateId);
                setUpdateUsers.invoke(entity,updateId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }


        }
    }
}
