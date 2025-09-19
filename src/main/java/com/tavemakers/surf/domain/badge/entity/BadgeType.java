package com.tavemakers.surf.domain.badge.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BadgeType {

    EXCELLENT_MEMBER_1ST(  "우수회원 1위"),
    EXCELLENT_MEMBER_2ND("우수회원 2위"),
    EXCELLENT_MEMBER_3RD("우수회원 3위"),

    EXCELLENT_STUDY_1ST("우수 스터디"),

    ADVANCED_PROJECT_1ST("심화 프로젝트 대상"),
    ADVANCED_PROJECT_2ND("심화 프로젝트 우수상"),

    COLLABORATIVE_PROJECT_1ST("연합 프로젝트 대상"),
    COLLABORATIVE_PROJECT_2ND("연합 프로젝트 우수상");

    private final String displayName;

}
