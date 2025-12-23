package com.tavemakers.surf.global.logging;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestLogContext {
    private static final ThreadLocal<RequestLogContext> LOCAL =
            ThreadLocal.withInitial(RequestLogContext::new);

    public String requestId;
    public String userId;
    public String actorRole;
    public String httpMethod;
    public String path;
    public Instant startAt;
    public long durationMs;
    public int status;

    public final List<Map<String, Object>> events = new ArrayList<>(); // event 레코드 모음
    public final Map<String, Object> pendingProps = new HashMap<>();

    public static RequestLogContext get() { return LOCAL.get(); }
    public static void clear() { LOCAL.remove(); }
}
