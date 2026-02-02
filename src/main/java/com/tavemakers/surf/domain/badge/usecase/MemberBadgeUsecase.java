package com.tavemakers.surf.domain.badge.usecase;

import com.tavemakers.surf.domain.badge.dto.request.MemberBadgeReqDTO;
import com.tavemakers.surf.domain.badge.dto.response.MemberBadgeResDTO;
import com.tavemakers.surf.domain.badge.dto.response.MemberBadgeSliceResDTO;
import com.tavemakers.surf.domain.badge.entity.MemberBadge;
import com.tavemakers.surf.domain.badge.service.MemberBadgeGetService;
import com.tavemakers.surf.domain.badge.service.MemberBadgeCreateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberBadgeUsecase {

    private final MemberBadgeCreateService memberBadgeCreateService;
    private final MemberBadgeGetService memberBadgeGetService;

    /** 다수 회원에게 배지 부여 */
    public void saveMemberBadgeList(MemberBadgeReqDTO dto) {
        memberBadgeCreateService.saveMemberBadgeList(dto);
    }

    /** 회원의 배지 목록 페이징 조회 */
    public MemberBadgeSliceResDTO getMemberBadgeWithSlice(Long memberId, int pageSize, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "awardedAt"));
        Slice<MemberBadge> slice
                = memberBadgeGetService.findMemberBadgeWithSlice(memberId, pageable);
        return MemberBadgeSliceResDTO.from(slice.map(MemberBadgeResDTO::from));
    }

}
