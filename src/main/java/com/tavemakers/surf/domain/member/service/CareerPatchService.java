package com.tavemakers.surf.domain.member.service;

import com.tavemakers.surf.domain.member.dto.request.CareerUpdateReqDTO;
import com.tavemakers.surf.domain.member.entity.Career;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.repository.CareerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CareerPatchService {
    private final CareerRepository careerRepository;

    //기존 경력 수정
    @Transactional
    public void updateCareer(Member member, List<CareerUpdateReqDTO> dtos){
        Map<Long, CareerUpdateReqDTO> careerDtoMap
                =dtos.stream()
                .collect(Collectors.toMap(CareerUpdateReqDTO::getCareerId, dto ->dto));

        //해당 회원이 소유한 경력 중, 수정 대상에 포함된 경력들만 DB에서 조회
        List<Career> careersToUpdate = careerRepository.findAllByMemberAndIdIn(member, careerDtoMap.keySet());

        careersToUpdate.forEach(career -> {
            CareerUpdateReqDTO dto = careerDtoMap.get(career.getId());
            career.update(dto);
        });

    }
}
