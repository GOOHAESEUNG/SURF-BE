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

    //유저의 가장 최신 기수 트랙을 가져옴
    public List<Track> getTrack(List<Long> memberIds) {
        return trackRepository.findLatestTracksByMemberIds(memberIds);
    }

    //트랙과 함께 모든 회원 반환
    public List<Track> getAllTracksWithMember() {
        return trackRepository.findAllWithActiveMember();
    }

}
