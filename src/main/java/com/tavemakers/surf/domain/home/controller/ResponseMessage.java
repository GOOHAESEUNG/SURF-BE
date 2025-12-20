package com.tavemakers.surf.domain.home.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {

    HOME_CONTENT_READ("[홈 화면 메시지]를 조회했습니다."),
    HOME_CONTENT_UPSERTED("[홈 화면 메시지]를 수정했습니다.");

    private final String message;
}
