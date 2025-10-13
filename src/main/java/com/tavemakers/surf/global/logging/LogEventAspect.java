package com.tavemakers.surf.global.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class LogEventAspect {

    private final LogEventEmitter emitter;

    public LogEventAspect(LogEventEmitter emitter) { this.emitter = emitter; }

    @Around("@annotation(com.example.logging.LogEvent)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Method m = ((MethodSignature) pjp.getSignature()).getMethod();
        LogEvent ann = m.getAnnotation(LogEvent.class);
        String event = ann.value();
        String msg = ann.message().isBlank() ? null : ann.message();

        Map<String, Object> props = collectProps(pjp.getArgs());

        try {
            Object ret = pjp.proceed();
            // 필요하면 ret에서 추가 정보 추출해 props.put(...)
            emitter.emit(event, props);
            return ret;
        } catch (Exception ex) {
            Map<String, Object> fail = new HashMap<>(props);
            fail.put("error_code", ex.getClass().getSimpleName());
            fail.put("error_msg", ex.getMessage());
            emitter.emitError(event, fail, msg != null ? msg : "AOP captured exception");
            throw ex;
        }
    }

    private Map<String, Object> collectProps(Object[] args) {
        Map<String, Object> map = new HashMap<>();
        for (Object arg : args) {
            if (arg instanceof LogPropsProvider p) {
                try { map.putAll(p.buildProps()); } catch (Exception ignored) {}
            }
        }
        return map;
    }
}