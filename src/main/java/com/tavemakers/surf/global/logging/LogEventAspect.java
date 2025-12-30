package com.tavemakers.surf.global.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class LogEventAspect {

    private final LogEventEmitter emitter;

    public LogEventAspect(LogEventEmitter emitter) {
        this.emitter = emitter;
    }

    @Around("@annotation(com.tavemakers.surf.global.logging.LogEvent)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method m = signature.getMethod();

        Class<?> targetClass = (pjp.getTarget() != null) ? pjp.getTarget().getClass() : null;
        if (targetClass != null) {
            m = AopUtils.getMostSpecificMethod(m, targetClass);
        }

        LogEvent ann = m.getAnnotation(LogEvent.class);
        String event = (ann != null) ? ann.value() : null;
        String msg = (ann != null && !ann.message().isBlank()) ? ann.message() : null;

        Map<String, Object> props = collectPropsFromArgs(pjp.getArgs(), m);

        try {
            Object ret = pjp.proceed();

            enrichWithReturn(props, ret);

            // pendingProps drain + override 추출
            Map<String, Object> drained = LogEventContext.drain();
            String overrideEvent = LogEventContext.extractOverrideEvent(drained);
            String overrideMsg = LogEventContext.extractOverrideMessage(drained);

            props.putAll(drained);

            String finalEvent = (overrideEvent != null) ? overrideEvent : event;
            String finalMsg = (overrideMsg != null) ? overrideMsg : msg;

            if (finalMsg == null || finalMsg.isBlank()) {
                emitter.emit(finalEvent, props);
            } else {
                emitter.emit(finalEvent, props, finalMsg);
            }
            return ret;

        } catch (Exception ex) {
            Map<String, Object> fail = new HashMap<>(props);

            // 실패에서도 drain + override 반영
            Map<String, Object> drained = LogEventContext.drain();
            String overrideEvent = LogEventContext.extractOverrideEvent(drained);
            String overrideMsg = LogEventContext.extractOverrideMessage(drained);

            fail.putAll(drained);
            fail.put("error_code", ex.getClass().getSimpleName());
            fail.put("error_msg", ex.getMessage());

            String baseEvent = (overrideEvent != null) ? overrideEvent : event;
            String failedEvent = (baseEvent != null && baseEvent.endsWith(".failed"))
                    ? baseEvent
                    : (baseEvent != null ? baseEvent + ".failed" : "unknown.failed");

            String finalMsg = (overrideMsg != null) ? overrideMsg : msg;

            emitter.emitError(
                    failedEvent,
                    fail,
                    (finalMsg != null && !finalMsg.isBlank()) ? finalMsg : "AOP captured exception"
            );

            throw ex;
        }
    }

    private Map<String, Object> collectPropsFromArgs(Object[] args, Method method) {
        Map<String, Object> props = new HashMap<>();
        Parameter[] params = method.getParameters();

        for (int i = 0; i < params.length; i++) {
            var annotation = params[i].getAnnotation(LogParam.class);
            if (annotation != null) {
                props.put(annotation.value(), args[i]);
            }

            if (args[i] instanceof LogPropsProvider provider) {
                try {
                    props.putAll(provider.buildProps());
                } catch (Exception ignored) {}
            }
        }
        return props;
    }

    private void enrichWithReturn(Map<String, Object> props, Object ret) {
        if (ret == null) return;
        try {
            Long id = tryExtractId(ret);
            if (id != null) {
                String name = ret.getClass().getSimpleName().toLowerCase();
                String key = name.replace("resdto", "") + "_id";
                props.putIfAbsent(key, id);
            }
        } catch (Exception ignored) {}
    }

    private Long tryExtractId(Object dto) {
        if (dto == null) return null;

        try {
            for (var method : dto.getClass().getMethods()) {
                String name = method.getName();
                if (method.getParameterCount() == 0 && name.endsWith("Id")) {
                    Object v = method.invoke(dto);
                    if (v instanceof Number n) return n.longValue();
                }
            }
        } catch (Exception ignore) {}

        try {
            var m1 = dto.getClass().getMethod("id");
            m1.setAccessible(true);
            Object v1 = m1.invoke(dto);
            if (v1 instanceof Number n) return n.longValue();
        } catch (Exception ignore) {}

        try {
            var m2 = dto.getClass().getMethod("getId");
            m2.setAccessible(true);
            Object v2 = m2.invoke(dto);
            if (v2 instanceof Number n) return n.longValue();
        } catch (Exception ignore) {}

        try {
            var f = dto.getClass().getDeclaredField("id");
            f.setAccessible(true);
            Object v3 = f.get(dto);
            if (v3 instanceof Number n) return n.longValue();
        } catch (Exception ignore) {}

        try {
            var f = dto.getClass().getDeclaredField("data");
            f.setAccessible(true);
            Object inner = f.get(dto);
            if (inner != null) return tryExtractId(inner);
        } catch (Exception ignore) {}

        return null;
    }
}