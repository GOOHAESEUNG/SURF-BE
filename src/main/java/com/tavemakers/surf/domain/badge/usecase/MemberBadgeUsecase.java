package com.tavemakers.surf.domain.badge.usecase;

import com.tavemakers.surf.domain.badge.dto.request.MemberBadgeReqDTO;
import com.tavemakers.surf.domain.badge.service.MemberBadgeSaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberBadgeUsecase {

    private final MemberBadgeSaveService memberBadgeSaveService;

    public void saveMemberBadgeList(MemberBadgeReqDTO dto) {
        memberBadgeSaveService.saveMemberBadgeList(dto);
    }

}
