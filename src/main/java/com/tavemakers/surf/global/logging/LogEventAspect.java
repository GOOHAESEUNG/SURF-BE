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

    public LogEventAspect(LogEventEmitter emitter) {
        this.emitter = emitter;
    }

    @Around("@annotation(com.tavemakers.surf.global.logging.LogEvent)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Method m = ((MethodSignature) pjp.getSignature()).getMethod();
        LogEvent ann = m.getAnnotation(LogEvent.class);
        String event = ann.value();
        String msg = ann.message().isBlank() ? null : ann.message();

        // ✅ 1. 파라미터 + DTO 양쪽에서 props 수집
        Map<String, Object> props = collectPropsFromArgs(pjp.getArgs(), m);

        try {
            Object ret = pjp.proceed();

            // ✅ 2. 리턴값에서 ID 자동 보강
            enrichWithReturn(props, ret);

            // ✅ 3. 성공 로그 emit
            emitter.emit(event, props);
            return ret;
        } catch (Exception ex) {
            // ✅ 4. 실패 로그 emit
            Map<String, Object> fail = new HashMap<>(props);
            fail.put("error_code", ex.getClass().getSimpleName());
            fail.put("error_msg", ex.getMessage());
            emitter.emitError(event, fail, msg != null ? msg : "AOP captured exception");
            throw ex;
        }
    }

    /** ✅ LogParam, LogPropsProvider 둘 다 인식 */
    private Map<String, Object> collectPropsFromArgs(Object[] args, Method method) {
        Map<String, Object> props = new HashMap<>();
        var params = method.getParameters();

        for (int i = 0; i < params.length; i++) {
            // 1) @LogParam
            var annotation = params[i].getAnnotation(LogParam.class);
            if (annotation != null) {
                props.put(annotation.value(), args[i]);
            }

            // 2) LogPropsProvider DTO
            if (args[i] instanceof LogPropsProvider provider) {
                try {
                    props.putAll(provider.buildProps());
                } catch (Exception ignored) {}
            }
        }
        return props;
    }

    /** ✅ 리턴 DTO에서 id 필드 자동 보강 */
    private void enrichWithReturn(Map<String, Object> props, Object ret) {
        if (ret == null) return;

        try {
            Long id = tryExtractId(ret);
            if (id != null) {
                // ex) PostResDTO → post_id, CommentResDTO → comment_id
                String name = ret.getClass().getSimpleName().toLowerCase();
                String key = name.replace("resdto", "") + "_id";
                props.put(key, id);
            }
        } catch (Exception ignored) {}
    }

    private Long tryExtractId(Object dto) {
        try {
            var m1 = dto.getClass().getMethod("id");
            Object v1 = m1.invoke(dto);
            if (v1 instanceof Number n) return n.longValue();
        } catch (NoSuchMethodException ignored) {
        } catch (Exception e) {
            return null;
        }

        try {
            var m2 = dto.getClass().getMethod("getId");
            Object v2 = m2.invoke(dto);
            if (v2 instanceof Number n) return n.longValue();
        } catch (NoSuchMethodException ignored) {
        } catch (Exception e) {
            return null;
        }

        return null;
    }
}