package com.tavemakers.surf.domain.home.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {

    HOME_CONTENT_READ("[홈 화면 메시지]를 조회했습니다."),
    HOME_CONTENT_UPSERTED("[홈 화면 메시지]를 수정했습니다."),

    HOME_BANNERS_READ("[홈 배너] 목록을 조회했습니다."),
    HOME_BANNER_CREATED("[홈 배너]를 생성했습니다."),
    HOME_BANNER_DELETED("[홈 배너]를 삭제했습니다."),
    HOME_BANNER_REORDERED("[홈 배너] 순서를 변경했습니다."),
    HOME_BANNER_UPDATED("[홈 배너]를 수정했습니다.");

    private final String message;
}
