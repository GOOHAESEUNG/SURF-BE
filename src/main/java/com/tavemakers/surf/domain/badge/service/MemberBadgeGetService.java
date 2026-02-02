package com.tavemakers.surf.domain.badge.service;

import com.tavemakers.surf.domain.badge.entity.MemberBadge;
import com.tavemakers.surf.domain.badge.repository.MemberBadgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberBadgeGetService {

    private final MemberBadgeRepository memberBadgeRepository;

    /** 회원의 배지 목록을 페이징 조회 */
    public Slice<MemberBadge> findMemberBadgeWithSlice(Long memberId, Pageable pageable) {
        return memberBadgeRepository.findMemberBadgeListByMemberId(memberId, pageable);
    }



}
