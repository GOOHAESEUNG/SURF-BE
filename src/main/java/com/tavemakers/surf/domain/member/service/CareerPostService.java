package com.tavemakers.surf.domain.member.service;

import com.tavemakers.surf.domain.member.dto.request.CareerCreateReqDTO;
import com.tavemakers.surf.domain.member.repository.CareerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CareerPostService {

    private final CareerRepository careerRepository;

    //경력 신규 생성
    public void createCareer(Long memberId, CareerCreateReqDTO dto){

    }
}
