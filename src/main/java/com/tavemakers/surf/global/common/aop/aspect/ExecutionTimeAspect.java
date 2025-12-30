package com.tavemakers.surf.global.common.aop.aspect;

import com.tavemakers.surf.global.common.aop.annotations.ExecutionTimeLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ExecutionTimeAspect {

    @Around("@annotation(com.tavemakers.surf.global.common.aop.annotations.ExecutionTimeLog)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        ExecutionTimeLog annotation = signature.getMethod().getAnnotation(ExecutionTimeLog.class);

        String jobName = annotation.jobName();
        long startTime = System.currentTimeMillis();

        try {
            return joinPoint.proceed();
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            log.info("[{}] 실행 완료 (소요시간: {}ms)", jobName, executionTime);
        }
    }
}