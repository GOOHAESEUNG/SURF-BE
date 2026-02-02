package com.tavemakers.surf.domain.badge.service;

import com.tavemakers.surf.domain.badge.dto.request.MemberBadgeReqDTO;
import com.tavemakers.surf.domain.badge.entity.MemberBadge;
import com.tavemakers.surf.domain.badge.repository.MemberBadgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberBadgeCreateService {

    private final MemberBadgeRepository memberBadgeRepository;

    /** 다수 회원에게 배지 일괄 저장 */
    @Transactional
    public void saveMemberBadgeList(MemberBadgeReqDTO dto) {
        List<MemberBadge> badgeList = new LinkedList<>();
        dto.memberIdList().forEach(
                        memberId -> badgeList.add(MemberBadge.of(dto, memberId))
                );
        memberBadgeRepository.saveAll(badgeList);
    }

}
