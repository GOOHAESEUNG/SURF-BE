package com.tavemakers.surf.global.logging;


import java.util.HashMap;
import java.util.Map;

public class LogEventContext {

    public static void put(String key, Object value) {
        if (value == null) return;

        RequestLogContext ctx = RequestLogContext.get();
        if (ctx.events.isEmpty()) return;

        Map<String, Object> lastEvent = ctx.events.get(ctx.events.size() - 1);

        Object propsObj = lastEvent.get("props");
        Map<String, Object> props;

        if (propsObj instanceof Map<?, ?> map) {
            props = new HashMap<>();
            map.forEach((k, v) -> {
                if (k instanceof String) {
                    props.put((String) k, v);
                }
            });
        } else {
            props = new HashMap<>();
        }

        props.put(key, value);
        lastEvent.put("props", props);
    }
}