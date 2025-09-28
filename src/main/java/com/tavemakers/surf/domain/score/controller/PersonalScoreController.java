package com.tavemakers.surf.domain.score.controller;

import com.tavemakers.surf.domain.score.dto.response.PersonalScoreWithPinned5ResDto;
import com.tavemakers.surf.domain.score.usecase.PersonalScoreUsecase;
import com.tavemakers.surf.global.common.response.ApiResponse;
import com.tavemakers.surf.global.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.tavemakers.surf.domain.score.controller.ResponseMessage.SCORE_AND_PINNED_5_READ;

@RestController
@RequiredArgsConstructor
@Tag(name = "활동점수")
public class PersonalScoreController {

    private final PersonalScoreUsecase personalScoreUsecase;

    @Operation(
            summary = "[활동점수] + 고정 5개[활동기록] 조회)",
            description = "[활동점수] + 고정 5개[활동기록] 조회"
    )
    @GetMapping("/v1/user/members/personal-score/pinned5")
    public ApiResponse<PersonalScoreWithPinned5ResDto> getScoreAndPinned5(
    ) {
        PersonalScoreWithPinned5ResDto response =
                personalScoreUsecase.findPersonalScoreAndPinned5(SecurityUtils.getCurrentMemberId());
        return ApiResponse.response(HttpStatus.OK, SCORE_AND_PINNED_5_READ.getMessage(), response);
    }

}
