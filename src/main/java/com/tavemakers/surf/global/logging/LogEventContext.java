package com.tavemakers.surf.global.logging;


import java.util.HashMap;
import java.util.Map;

public class LogEventContext {

    public static void put(String key, Object value) {
        if (key == null || key.isBlank() || value == null) return;

        RequestLogContext.get().pendingProps.put(key, value);
    }

    static Map<String, Object> drain() {
        RequestLogContext ctx = RequestLogContext.get();

        if (ctx.pendingProps.isEmpty()) {
            return Map.of();
        }

        Map<String, Object> copy = new HashMap<>(ctx.pendingProps);
        ctx.pendingProps.clear();
        return copy;
    }
}