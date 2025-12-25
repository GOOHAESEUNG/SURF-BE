package com.tavemakers.surf.global.logging;

import java.util.HashMap;
import java.util.Map;

public class LogEventContext {

    private static final String OVERRIDE_EVENT_KEY = "__override_event";
    private static final String OVERRIDE_MESSAGE_KEY = "__override_message";

    public static void put(String key, Object value) {
        if (key == null || key.isBlank() || value == null) return;
        RequestLogContext.get().pendingProps.put(key, value);
    }

    // 이벤트명 런타임 교체
    public static void overrideEvent(String event) {
        if (event == null || event.isBlank()) return;
        RequestLogContext.get().pendingProps.put(OVERRIDE_EVENT_KEY, event);
    }

    // 메시지 런타임 교체
    public static void overrideMessage(String message) {
        if (message == null || message.isBlank()) return;
        RequestLogContext.get().pendingProps.put(OVERRIDE_MESSAGE_KEY, message);
    }

    static Map<String, Object> drain() {
        RequestLogContext ctx = RequestLogContext.get();

        if (ctx.pendingProps.isEmpty()) return Map.of();

        Map<String, Object> copy = new HashMap<>(ctx.pendingProps);
        ctx.pendingProps.clear();
        return copy;
    }

    // drain 결과에서 override_event 추출 (+props에 남지 않게 remove)
    static String extractOverrideEvent(Map<String, Object> drained) {
        Object v = drained.remove(OVERRIDE_EVENT_KEY);
        return (v instanceof String s && !s.isBlank()) ? s : null;
    }

    // drain 결과에서 override_message 추출 (+props에 남지 않게 remove)
    static String extractOverrideMessage(Map<String, Object> drained) {
        Object v = drained.remove(OVERRIDE_MESSAGE_KEY);
        return (v instanceof String s && !s.isBlank()) ? s : null;
    }
}