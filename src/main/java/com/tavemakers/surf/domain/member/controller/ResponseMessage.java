package com.tavemakers.surf.domain.member.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {

    //회원가입 온보딩 확인 여부 체크
    MEMBER_ONBOARDING_STATUS_CHECK_SUCCESS("[회원]의 추가 회원가입 정보 입력 필요 여부를 확인했습니다."),

    // 이름 규칙 변경 및 메시지에 Placeholder(%s) 추가
    MEMBER_SEARCH_SUCCESS("'%s'(으)로 활동 중인 [회원]을 조회했습니다."),
    MEMBER_GROUP_SUCCESS("[트랙]별로 현재 활동 중인 [회원]을 조회했습니다."),

    // 프로필 조회
    MYPAGE_PROFILE_READ("마이페이지에서 [프로필 정보]를 조회합니다."),

    // 프로필 수정
    MYPAGE_PROFILE_UPDATE_SUCCESS("마이페이지에서 [프로필 정보]를 수정했습니다.");

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
