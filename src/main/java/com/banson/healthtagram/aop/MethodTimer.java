package com.banson.healthtagram.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Aspect
@Component
public class MethodTimer {

    @Pointcut("@annotation(com.banson.healthtagram.aop.annotation.Timer)")  //aop가 적용될 곳
    private void timer(){};

    @Around("timer()")  //aop가 적용될 범위
    public Object ExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();

        stopWatch.start();
        Object proceed = joinPoint.proceed();   //중요 로직을 실행시키는 코드
        stopWatch.stop();

        long totalTime = stopWatch.getTotalTimeMillis();

        //메소드의 이름을 가져옴
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String method = signature.getMethod().getName();

        log.info("method: {}, time: {}", method, totalTime);

        return proceed;
    }
}
