package com.tavemakers.surf.domain.score.controller;

import com.tavemakers.surf.domain.score.dto.response.PersonalScoreWithTop4ResDto;
import com.tavemakers.surf.domain.score.usecase.PersonalScoreUsecase;
import com.tavemakers.surf.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static com.tavemakers.surf.domain.score.controller.ResponseMessage.SCORE_AND_TOP_4_READ;

@RestController
@RequiredArgsConstructor
public class PersonalScoreController {

    private final PersonalScoreUsecase personalScoreUsecase;

    @GetMapping("/v1/member/{memberId}/personal-score/top4")
    public ApiResponse<PersonalScoreWithTop4ResDto> getScoreAndTop4(@PathVariable Long memberId) {
        PersonalScoreWithTop4ResDto response = personalScoreUsecase.findPersonalScoreAndTop4(memberId);
        return ApiResponse.response(HttpStatus.OK, SCORE_AND_TOP_4_READ.getMessage(), response);
    }
}
