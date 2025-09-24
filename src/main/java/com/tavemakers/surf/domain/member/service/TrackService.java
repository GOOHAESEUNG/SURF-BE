package com.tavemakers.surf.domain.member.service;

import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.entity.Track;
import com.tavemakers.surf.domain.member.entity.enums.Part;
import com.tavemakers.surf.domain.member.exception.MemberNotFoundException;
import com.tavemakers.surf.domain.member.exception.TrackNotFoundException;
import com.tavemakers.surf.domain.member.repository.MemberRepository;
import com.tavemakers.surf.domain.member.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TrackService {

    private final TrackRepository trackRepository;
    private final MemberRepository memberRepository;

    /** 트랙 추가 (관리자만 가능) */
    @PreAuthorize("hasAnyRole('ROOT','PRESIDENT','MANAGER')")
    @Transactional
    public void addTrackToMember(Long memberId, Integer generation, Part part) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        member.addTrack(generation, part); // Member 편의 메서드 활용
    }

    /** 트랙 수정 (관리자만 가능) */
    @PreAuthorize("hasAnyRole('ROOT','PRESIDENT','MANAGER')")
    @Transactional
    public void updateTrack(Long trackId, Integer generation, Part part) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(TrackNotFoundException::new);
        track.update(generation, part);
    }

    /** 트랙 삭제 (관리자만 가능) */
    @PreAuthorize("hasAnyRole('ROOT','PRESIDENT','MANAGER')")
    @Transactional
    public void deleteTrack(Long trackId) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(TrackNotFoundException::new);
        trackRepository.delete(track);
    }
}
