package com.tavemakers.surf.global.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class LogEventEmitterImpl implements LogEventEmitter {

    private static final Logger log = LoggerFactory.getLogger("api-event");
    private final ObjectMapper objectMapper;

    public LogEventEmitterImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /** 성공 이벤트 적재 (요청 컨텍스트에 쌓아두고, 출력은 필터에서 flush) */
    @Override
    public void emit(String event, Map<String, Object> props) {
        RequestLogContext ctx = RequestLogContext.get();

        Map<String, Object> e = new HashMap<>();
        e.put("event", event);
        e.put("event_type", "INFO");
        // message는 성공 시 보통 없음 -> null이면 키 자체를 생략
        e.put("props", props != null ? props : Collections.emptyMap());

        ctx.events.add(e);
    }

    /** 실패 이벤트 적재 */
    @Override
    public void emitError(String event, Map<String, Object> props, String msg) {
        RequestLogContext ctx = RequestLogContext.get();

        Map<String, Object> e = new HashMap<>();
        e.put("event", event);
        e.put("event_type", "ERROR");
        if (msg != null) e.put("message", msg);
        e.put("props", props != null ? props : Collections.emptyMap());

        ctx.events.add(e);
    }

    /** 요청 종료 시 필터(WebLoggingFilter)에서 호출: 공통 필드와 병합하여 JSON Line 출력 */
    public void flush() {
        RequestLogContext ctx = RequestLogContext.get();

        for (Map<String, Object> e : ctx.events) {
            Map<String, Object> record = new HashMap<>();
            record.put("timestamp", Instant.now().toString());
            record.put("event", e.get("event"));
            record.put("event_type", e.get("event_type"));
            record.put("result", ctx.status >= 400 ? "fail" : "success");
            record.put("status", ctx.status);
            record.put("request_id", ctx.requestId);
            record.put("user_id", ctx.userId);
            record.put("actor_role", ctx.actorRole);
            record.put("http_method", ctx.httpMethod);
            record.put("path", ctx.path);
            record.put("duration_ms", ctx.durationMs);
            if (e.containsKey("message")) {
                record.put("message", e.get("message")); // null이면 Jackson이 null로 직렬화
            }
            record.put("props", e.getOrDefault("props", Collections.emptyMap()));

            try {
                log.info(objectMapper
                        .writerWithDefaultPrettyPrinter()
                        .writeValueAsString(record));
            } catch (Exception ex) {
                log.warn("Failed to write log record: {}", ex.toString());
            }
        }
    }
}