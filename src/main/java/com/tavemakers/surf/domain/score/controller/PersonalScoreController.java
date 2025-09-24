package com.tavemakers.surf.domain.score.controller;

import com.tavemakers.surf.domain.score.dto.response.PersonalScoreWithPinnedResDto;
import com.tavemakers.surf.domain.score.usecase.PersonalScoreUsecase;
import com.tavemakers.surf.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static com.tavemakers.surf.domain.score.controller.ResponseMessage.*;

@RestController
@RequiredArgsConstructor
public class PersonalScoreController {

    private final PersonalScoreUsecase personalScoreUsecase;

    @GetMapping("/v1/member/{memberId}/personal-score/pin")
    public ApiResponse<PersonalScoreWithPinnedResDto> getScoreAndPinned5(@PathVariable Long memberId) {
        PersonalScoreWithPinnedResDto response = personalScoreUsecase.findPersonalScoreAndPinned(memberId);
        return ApiResponse.response(HttpStatus.OK, SCORE_AND_PINNED_READ.getMessage(), response);
    }
}
