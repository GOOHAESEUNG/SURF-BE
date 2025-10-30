package com.tavemakers.surf.global.common.s3.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {

    PRE_SIGNED_URL_GENERATED("[PreSignedUrl]을 발급했습니다.");

    private final String message;

}
