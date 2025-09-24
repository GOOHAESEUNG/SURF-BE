package com.tavemakers.surf.domain.member.service;

import com.tavemakers.surf.domain.member.dto.request.CareerCreateReqDTO;
import com.tavemakers.surf.domain.member.entity.Career;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.repository.CareerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CareerPostService {

    private final CareerRepository careerRepository;

    //경력 신규 생성
    @Transactional
    public void createCareer(Member member, List<CareerCreateReqDTO> dtos) {
        List<Career> newCareers = dtos.stream()
                .map(dto -> Career.from(dto, member))
                .toList();
        careerRepository.saveAll(newCareers);
    }
}
