package com.tavemakers.surf.domain.home.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

public record HomeResDTO(

        @Schema(description = "홈 문구", example = "TAVE 신규 회원을 환영합니다.")
        String mainText,

        @Schema(description = "홈 배너 목록")
        List<HomeBannerResDTO> banners,

        @Schema(description = "회원 이름", example = "홍길동")
        String memberName,

        @Schema(description = "회원 기수", example = "17")
        Integer memberGeneration,

        @Schema(description = "회원 파트", example = "프론트엔드")
        String memberPart,

        @Schema(description = "다음 일정 이름", example = "만남의장")
        String nextScheduleTitle,

        @Schema(description = "다음 일정 날짜", example = "2024-07-15")
        String nextScheduleDate
) {
}