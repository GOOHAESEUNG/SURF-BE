package com.tavemakers.surf.global.logging;


import java.util.Map;

public interface LogEventEmitter {
    void emit(String event, Map<String, Object> props);                   // 성공 이벤트
    void emitError(String event, Map<String, Object> props, String msg);  // 실패 이벤트
}
