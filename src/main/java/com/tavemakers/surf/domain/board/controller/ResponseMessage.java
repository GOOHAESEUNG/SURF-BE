package com.tavemakers.surf.domain.board.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {

    BOARD_CREATED("게시글이 성공적으로 생성되었습니다."),
    BOARD_UPDATED("게시글이 성공적으로 수정되었습니다."),
    BOARD_DELETED("게시글이 성공적으로 삭제되었습니다."),
    BOARD_READ("게시글이 성공적으로 조회되었습니다.");

    private final String message;

}