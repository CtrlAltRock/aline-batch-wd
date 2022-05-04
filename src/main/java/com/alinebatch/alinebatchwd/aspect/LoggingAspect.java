package com.alinebatch.alinebatchwd.aspect;


import ch.qos.logback.classic.spi.ThrowableProxyVO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("within(@org.springframework.stereotype.Component *)")
    public void analyzerPointcut()
    {

    }

    @Around("analyzerPointcut()")
    public Object logAnalysisSetup(ProceedingJoinPoint joinPoint) throws Throwable
    {
        log.info("Entering: {}.{}() with arguments[s] = {}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));

        try {
            Object result = joinPoint.proceed();

            return result;
        } catch (IllegalArgumentException e) {
            log.error("Hey, fix this later");

            throw e;
        }
    }


}
