package com.tavemakers.surf.domain.home.controller;

import com.tavemakers.surf.domain.home.dto.res.HomeResDTO;
import com.tavemakers.surf.domain.home.service.HomeService;
import com.tavemakers.surf.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "홈 화면 렌더링")
public class HomeController {

    private final HomeService homeService;

    @GetMapping("/home")
    public ApiResponse<HomeResDTO> home() {
        HomeResDTO response = homeService.getHome();
        return ApiResponse.response(HttpStatus.OK, "홈 화면 렌더링 성공", response);
    }
}