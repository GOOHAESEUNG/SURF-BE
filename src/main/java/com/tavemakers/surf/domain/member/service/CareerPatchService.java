package com.tavemakers.surf.domain.member.service;

import com.tavemakers.surf.domain.member.dto.request.CareerUpdateReqDTO;
import com.tavemakers.surf.domain.member.entity.Career;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.exception.CareerNotFoundException;
import com.tavemakers.surf.domain.member.repository.CareerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CareerPatchService {
    private final CareerRepository careerRepository;

    @Transactional
    public void updateCareer(Member member, List<CareerUpdateReqDTO> dtos) {
        Set<Long> requestedIds = dtos.stream()
                .map(CareerUpdateReqDTO::getCareerId)
                .collect(Collectors.toSet());

        List<Career> CareersToUpdate = careerRepository.findAllByMemberAndIdIn(member, requestedIds);

        if (CareersToUpdate.size() != requestedIds.size()) {
            Set<Long> validIds = CareersToUpdate.stream()
                    .map(Career::getId)
                    .collect(Collectors.toSet());
            requestedIds.removeAll(validIds);
            throw new CareerNotFoundException("잘못되었거나 권한이 없는 경력 ID: " + requestedIds);
        }

        Map<Long, Career> validCareerMap = CareersToUpdate.stream()
                .collect(Collectors.toMap(Career::getId, career -> career));

        dtos.forEach(dto -> {
            Career careerToUpdate = validCareerMap.get(dto.getCareerId());
            careerToUpdate.update(dto);
        });
    }
}