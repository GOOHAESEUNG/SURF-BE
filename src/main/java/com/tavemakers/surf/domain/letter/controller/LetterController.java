package com.tavemakers.surf.domain.letter.controller;

import com.tavemakers.surf.domain.letter.dto.req.LetterCreateRequest;
import com.tavemakers.surf.domain.letter.dto.res.LetterResponse;
import com.tavemakers.surf.domain.letter.facade.LetterFacade;
import com.tavemakers.surf.global.common.response.ApiResponse;
import com.tavemakers.surf.domain.member.entity.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "쪽지", description = "쪽지 전송 기능에 대한 API 입니다.")
public class LetterController {

    private final LetterFacade letterFacade;

    @PostMapping("/v1/user/api/letters")
    @Operation(summary = "쪽지 생성", description = "로그인한 사용자가 다른 회원에게 쪽지를 전송합니다.")
    public ApiResponse<LetterResponse> createLetter(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody LetterCreateRequest request
    ) {
        // senderId는 토큰에서 가져옴
        Long senderId = user.getId();

        // Facade 처리 후 최종 응답 반환
        LetterResponse response = letterFacade.createLetter(senderId, request);

        return ApiResponse.response(HttpStatus.OK, "쪽지 전송 성공", response);
    }

}
