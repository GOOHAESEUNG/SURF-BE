package com.tavemakers.surf.domain.member.controller;

import com.tavemakers.surf.domain.member.dto.MemberSearchResDTO;
import com.tavemakers.surf.domain.member.dto.MemberSimpleResDto;
import com.tavemakers.surf.domain.member.usecase.MemberUsecase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/member")
@Tag(name = "회원 조회", description = "회원 조회 관련 API")
public class MemberSearchController {

    private final MemberUsecase memberUsecase;

    //이름 기반 조회
    @Operation(
            summary = "이름 기반 회원 조회",
            description = "파라미터로 특정 이름을 받아 해당하는 회원 리스트를 반환합니다.")
    @GetMapping("/search")
    public ResponseEntity<List<MemberSearchResDTO>> searchMemberByName(
            @RequestParam @NotBlank(message = "검색어(name)은 필수입니다.") String name) {
        return ResponseEntity.ok(memberUsecase.findMemberByNameAndTrack(name));
    }

    //유저 전체 출력시 트랙+기수별 묶어서 출력
    @Operation(
            summary = "활동 중인 회원 전체 출력시 트랙+기수별로 출력 ",
            description = "활동 중인 회원 전체 출력시 트랙+기수별로 출력")
    @GetMapping("/grouped-by-track")
    public ResponseEntity<Map<String, List<MemberSimpleResDto>>> getGroupedMembers() {
        Map<String, List<MemberSimpleResDto>> groupedMembers = memberUsecase.getMembersGroupedByTrack();
        return ResponseEntity.ok(groupedMembers);
    }
}
