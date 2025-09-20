package com.tavemakers.surf.domain.member.controller;

import com.tavemakers.surf.domain.member.dto.MemberSearchResDTO;
import com.tavemakers.surf.domain.member.usecase.MemberUsecase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/member")
public class MemberSearchController {

    private final MemberUsecase memberUsecase;

    @GetMapping("/search")
    public ResponseEntity<List<MemberSearchResDTO>> searchMemberByName(@RequestParam String name) {
        return ResponseEntity.ok(memberUsecase.findMemberByNameAndTrack(name));
    }
}
