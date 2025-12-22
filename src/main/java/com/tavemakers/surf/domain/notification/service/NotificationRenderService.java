package com.tavemakers.surf.domain.notification.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tavemakers.surf.domain.notification.entity.Notification;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationRenderService {
    private final ObjectMapper objectMapper;

    public String renderBody(Notification notification) {
        Map<String, Object> payload = parsePayload(notification.getPayload());
        return replaceTemplate(notification.getType().getTemplate(), payload);
    }

    public String renderDeeplink(Notification notification) {
        Map<String, Object> payload = parsePayload(notification.getPayload());
        return replaceTemplate(notification.getType().getDeeplink(), payload);
    }

    private Map<String, Object> parsePayload(String payloadJson) {
        try {
            return objectMapper.readValue(payloadJson, new TypeReference<>() {});
        } catch (Exception e) {
            throw new IllegalStateException("Invalid notification payload", e);
        }
    }

    private String replaceTemplate(String template, Map<String, Object> payload) {
        String result = template;
        for (Map.Entry<String, Object> entry : payload.entrySet()) {
            result = result.replace(
                    "{" + entry.getKey() + "}",
                    String.valueOf(entry.getValue())
            );
        }
        return result;
    }
}
