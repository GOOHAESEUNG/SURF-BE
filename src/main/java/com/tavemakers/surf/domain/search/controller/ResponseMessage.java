package com.tavemakers.surf.domain.search.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {

    SEARCH_COMPLETED("검색이 성공적으로 완료되었습니다."),
    RECENT_SEARCH_READ("최근 검색어 목록이 성공적으로 조회되었습니다."),
    RECENT_SEARCH_DELETED("최근 검색어가 성공적으로 삭제되었습니다.");

    private final String message;
}
