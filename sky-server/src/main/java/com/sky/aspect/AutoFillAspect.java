package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;


/**
 * 自定义切面，实现公共字段的自动填充处理
 */
@Aspect
@Slf4j
@Component
public class AutoFillAspect {
    @Pointcut("execution(* com.sky.mapper.*.*(..)) &&@annotation(com.sky.annotation.AutoFill)")
        public void autoFillPointCut(){}

        @Before("autoFillPointCut()")
        public void autoFill(JoinPoint joinPoint){
            log.info("开始填充...");

        //获取到当前被拦截方法的数据库操作类型

            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class); //获取方法的AutoFill注解
            OperationType operationType = autoFill.value(); //获取了数据库的操作类型

        //获取当前被拦截方法的参数

            Object[] args = joinPoint.getArgs();

            if(args == null || args.length ==0 ){
                return;
            }
        //获取了要赋值的内容
            Object entity = args[0];
            LocalDateTime now = LocalDateTime.now();
            Long currentId = BaseContext.getCurrentId();

        //根据不同的操作类型，进行赋值操作

            if(operationType == OperationType.INSERT){
                try {
                    //为公共字段进行赋值
                    Method setCreatTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                    Method setCreatUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                    Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                    Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,Long.class);
                    //提供反射进行赋值
                    setCreatTime.invoke(entity,now);
                    setCreatUser.invoke(entity,currentId);
                    setUpdateTime.invoke(entity,now);
                    setUpdateUser.invoke(entity,currentId);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(operationType == OperationType.UPDATE){

                try {
                    Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                    Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                    setUpdateTime.invoke(entity,now);
                    setUpdateUser.invoke(entity,currentId);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }





        }

}
