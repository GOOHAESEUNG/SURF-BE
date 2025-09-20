package com.tavemakers.surf.domain.member.service;

import com.tavemakers.surf.domain.member.entity.Track;
import com.tavemakers.surf.domain.member.exception.TrackNotFoundException;
import com.tavemakers.surf.domain.member.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrackGetService {

    private final TrackRepository trackRepository;

    public Track getTrack(Long memberId) {

        return trackRepository.findByMemberId(memberId)
                .orElseThrow(TrackNotFoundException::new);
    }

    //트랙과 함께 모든 회원 반환
    public List<Track> getAllTracksWithMember() {
        return trackRepository.findAllWithActiveMember();
    }

}
