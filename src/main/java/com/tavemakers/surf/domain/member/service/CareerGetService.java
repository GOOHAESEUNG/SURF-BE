package com.tavemakers.surf.domain.member.service;

import com.tavemakers.surf.domain.member.dto.response.CareerResDTO;
import com.tavemakers.surf.domain.member.entity.Career;
import com.tavemakers.surf.domain.member.repository.CareerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CareerGetService {

    private final CareerRepository careerRepository;

    public List<Career> getMyCareers(Long memberId) {
        return careerRepository.findByMemberId(memberId);
    }

}
