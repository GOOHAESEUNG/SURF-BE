package com.tavemakers.surf.global.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class LogEventEmitterImpl implements LogEventEmitter {
    private static final Logger log = LoggerFactory.getLogger("api-event");
    private final ObjectMapper om;

    public LogEventEmitterImpl(ObjectMapper om) { this.om = om; }

    @Override
    public void emit(String event, Map<String, Object> props) {
        RequestLogContext.get().events.add(Map.of(
                "event", event, "event_type", "INFO", "message", null, "props", props
        ));
    }

    @Override
    public void emitError(String event, Map<String, Object> props, String msg) {
        RequestLogContext.get().events.add(Map.of(
                "event", event, "event_type", "ERROR", "message", msg, "props", props
        ));
    }

    /** 필터에서 호출: 요청 종료 시 공통필드와 병합해 JSON Line 출력 */
    public void flush() {
        var ctx = RequestLogContext.get();
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
            record.put("message", e.get("message"));
            record.put("props", e.get("props"));
            try { log.info(om.writeValueAsString(record)); }
            catch (Exception ex) { log.warn("log write failed: {}", ex.toString()); }
        }
    }
}