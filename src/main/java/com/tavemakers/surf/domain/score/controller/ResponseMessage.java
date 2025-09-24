package com.tavemakers.surf.domain.score.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {

    SCORE_AND_PINNED_READ("[개인활동점수]와 [고정활동내역]을 조회합니다.");

    private final String message;

}
