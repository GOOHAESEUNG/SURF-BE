package com.tavemakers.surf.domain.activity.controller;

import com.tavemakers.surf.domain.activity.dto.ActivityRecordReqDTO;
import com.tavemakers.surf.domain.activity.entity.ActivityRecord;
import com.tavemakers.surf.domain.activity.usecase.ActivityRecordUsecase;
import com.tavemakers.surf.global.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.tavemakers.surf.domain.activity.controller.ResponseMessage.*;

@RestController
@RequiredArgsConstructor
public class ActivityRecordController {

    private final ActivityRecordUsecase activityRecordUsecase;

    @PostMapping("/v1/manager/activity_record")
    public ApiResponse<Void> createActivityRecord(@RequestBody @Valid ActivityRecordReqDTO dto) {

        activityRecordUsecase.createActivityRecordList(dto);

        return ApiResponse.response(HttpStatus.CREATED, ACTIVITY_RECORD_CREATED.getMessage(), null);
    }

}
