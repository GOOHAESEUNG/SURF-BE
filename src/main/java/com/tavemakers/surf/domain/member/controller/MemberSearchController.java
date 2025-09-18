package com.tavemakers.surf.domain.member.controller;

import com.tavemakers.surf.domain.member.dto.MemberSearchResDTO;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.service.MemberGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/member")
public class MemberSearchController {

    private final MemberGetService memberGetService;

    @GetMapping("/search")
    public ResponseEntity<MemberSearchResDTO> searchMemberByName(@RequestParam String name) {
        return ResponseEntity.ok(memberGetService.getMemberByName(name));
    }
}
