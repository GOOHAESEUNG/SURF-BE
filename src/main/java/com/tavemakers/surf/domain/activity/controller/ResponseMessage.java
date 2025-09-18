package com.tavemakers.surf.domain.activity.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {

    ACTIVITY_RECORD_CREATED("[활동 기록]을 적용했습니다.");

    private final String message;
}
