package com.tavemakers.surf.domain.activity.controller;

import com.tavemakers.surf.domain.activity.dto.reqeust.ActivityRecordReqDTO;
import com.tavemakers.surf.domain.activity.dto.response.ActivityRecordSliceResDTO;
import com.tavemakers.surf.domain.activity.entity.enums.ScoreType;
import com.tavemakers.surf.domain.activity.usecase.ActivityRecordUsecase;
import com.tavemakers.surf.global.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.tavemakers.surf.domain.activity.controller.ResponseMessage.*;

@RestController
@RequiredArgsConstructor
public class ActivityRecordController {

    private final ActivityRecordUsecase activityRecordUsecase;

    @PostMapping("/v1/manager/activity-record")
    public ApiResponse<Void> createActivityRecord(@RequestBody @Valid ActivityRecordReqDTO dto) {
        activityRecordUsecase.createActivityRecordList(dto);
        return ApiResponse.response(HttpStatus.CREATED, ACTIVITY_RECORD_CREATED.getMessage(), null);
    }

    @GetMapping("/v1/member/{memberId}/activity-record")
    public ApiResponse<ActivityRecordSliceResDTO> getActivityRecord(
            @PathVariable Long memberId,
            @RequestParam ScoreType scoreType,
            @RequestParam int pageSize,
            @RequestParam int pageNum
    ) {
        ActivityRecordSliceResDTO response = activityRecordUsecase.getActivityRecordList(memberId, scoreType, pageSize, pageNum);
        return ApiResponse.response(HttpStatus.CREATED, ACTIVITY_RECORD_READ.getMessage(), response);
    }

}
