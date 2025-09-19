package com.tavemakers.surf.domain.badge.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {

    MEMBER_BADGE_LIST_CREATED("[회원]에게 [뱃지]를 부여했습니다.");

    private final String message;

}
