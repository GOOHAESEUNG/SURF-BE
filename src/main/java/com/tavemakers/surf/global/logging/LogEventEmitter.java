package com.tavemakers.surf.global.logging;


import java.util.Map;

public interface LogEventEmitter {
    void emit(String event, Map<String, Object> props, String message);

    default void emit(String event, Map<String, Object> props) {
        String defaultMsg = "이벤트명: " + event; // ✅ 기본 메시지 자동 포함
        emit(event, props, defaultMsg);
    }
    void emitError(String event, Map<String, Object> props, String msg);  // 실패 이벤트
}
