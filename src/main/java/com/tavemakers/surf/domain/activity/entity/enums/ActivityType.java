package com.tavemakers.surf.domain.activity.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.tavemakers.surf.domain.activity.entity.enums.ScoreType.*;

@Getter
@AllArgsConstructor
public enum ActivityType {

    // REWARD 상점
    EARLY_BIRD("행사 얼리버드", 5, REWARD),
    ENGAGE_AFTER_PARTY("뒷풀이 참여", 5, REWARD),
    CREATE_FLASH_MEETUP("번개모임 주최", 10, REWARD),
    ENGAGE_FLASH_MEETUP("번개모임 참여", 5, REWARD),
    SHARE_INFORMATION_AGIT("아지트 정보 공유", 3, REWARD),
    ENGAGE_TECH_SEMINAR("기술 세미나 참여", 10, REWARD),
    PRESENT_OUTLINE("기획안 발표", 10, REWARD),
    CREATE_SOCIAL_CLUB("소모임 생성", 15, REWARD),
    ENGAGE_SOCIAL_CLUB("소모임 활동", 3, REWARD),
    WRITE_WIL("WIL 작성", 3, REWARD),
    UPLOAD_INSTAGRAM_STORY("인스타그램 스토리 업로드", 3, REWARD),
    UPLOAD_TAVE_REVIEW("TAVE 활동 후기 업로드", 20, REWARD);

    private String displayName;
    private Integer score;
    private ScoreType scoreType;

}
