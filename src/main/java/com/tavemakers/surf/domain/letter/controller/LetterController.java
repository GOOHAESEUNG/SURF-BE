package com.tavemakers.surf.domain.letter.controller;

import com.tavemakers.surf.domain.letter.dto.req.LetterCreateReqDTO;
import com.tavemakers.surf.domain.letter.dto.res.LetterResDTO;
import com.tavemakers.surf.domain.letter.facade.LetterFacade;
import com.tavemakers.surf.global.common.response.ApiResponse;
import com.tavemakers.surf.global.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.tavemakers.surf.domain.letter.controller.ResponseMessage.LETTER_CREATED;
import static com.tavemakers.surf.domain.letter.controller.ResponseMessage.LETTER_SENT_READ;

@RestController
@RequiredArgsConstructor
@Tag(name = "쪽지", description = "회원 간 쪽지 전송 및 조회에 대한 API 입니다.")
public class LetterController {

    private final LetterFacade letterFacade;

    @PostMapping("/v1/user/letters")
    @Operation(summary = "쪽지 전송", description = "로그인한 사용자가 다른 회원에게 쪽지를 전송합니다.")
    public ApiResponse<LetterResDTO> createLetter(
            @Valid @RequestBody LetterCreateReqDTO request
    ) {
        Long senderId = SecurityUtils.getCurrentMemberId();

        return ApiResponse.response(
                HttpStatus.OK,
                LETTER_CREATED.getMessage(),
                letterFacade.createLetter(senderId, request)
        );
    }

    @GetMapping("/v1/user/letters/sent")
    @Operation(summary = "쪽지 조회", description = "자신이 보낸 쪽지 목록을 확인합니다.")
    public ApiResponse<Slice<LetterResDTO>> getSentLetters(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long senderId = SecurityUtils.getCurrentMemberId();

        Pageable pageable = PageRequest.of(
                page, size, Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return ApiResponse.response(
                HttpStatus.OK,
                LETTER_SENT_READ.getMessage(),
                letterFacade.getSentLetters(senderId, pageable)
        );
    }

}
