package com.tavemakers.surf.domain.member.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {

    // 이름 규칙 변경 및 메시지에 Placeholder(%s) 추가
    MEMBER_SEARCH_SUCCESS("'%s'(으)로 활동 중인 회원을 조회했습니다."),
    MEMBER_GROUP_SUCCESS("트랙별로 현재 활동 중인 회원을 조회했습니다."),

    // 프로필 조회
    MYPAGE_PROFILE_READ("마이페이지에서 [프로필 정보]를 조회합니다.");

    private final String message;

    // String.format을 사용하여 동적인 메시지를 생성하는 헬퍼 메서드
    public String getFormattedMessage(Object... args) {
        // 메시지에 Placeholder가 없으면 그대로 반환, 있으면 값을 채워서 반환
        if (args == null || args.length == 0) {
            return this.message;
        }
        return String.format(this.message, args);
    }
}
