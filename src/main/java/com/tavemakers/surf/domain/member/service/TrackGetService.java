package com.tavemakers.surf.domain.member.service;

import com.tavemakers.surf.domain.member.entity.Track;
import com.tavemakers.surf.domain.member.exception.TrackNotFoundException;
import com.tavemakers.surf.domain.member.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrackGetService {

    private final TrackRepository trackRepository;

    public Track getTrack(Long memberId) {

        return trackRepository.findById(memberId).orElseThrow(TrackNotFoundException::new);

    }
}
