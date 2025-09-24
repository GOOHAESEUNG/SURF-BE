package com.tavemakers.surf.domain.member.service;

import com.tavemakers.surf.domain.member.dto.request.CareerUpdateReqDTO;
import com.tavemakers.surf.domain.member.entity.Career;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.exception.CareerNotFoundException;
import com.tavemakers.surf.domain.member.repository.CareerRepository;
import com.tavemakers.surf.domain.member.validator.CareerValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CareerPatchService {
    private final CareerRepository careerRepository;
    private final CareerValidator careerValidator;

    @Transactional
    public void updateCareer(Member member, List<CareerUpdateReqDTO> dtos) {
        Set<Long> requestedIds = dtos.stream()
                .map(CareerUpdateReqDTO::getCareerId)
                .collect(Collectors.toSet());
        List<Career> careersToUpdate = careerRepository.findAllByMemberAndIdIn(member, requestedIds);
        log.info(careersToUpdate.toString());
        careerValidator.validateCareer(requestedIds, careersToUpdate);

        Map<Long, Career> validCareerMap = careersToUpdate.stream()
                .collect(Collectors.toMap(Career::getId, career -> career));

        dtos.forEach(dto -> {
            Career careerToUpdate = validCareerMap.get(dto.getCareerId());
            careerToUpdate.update(dto);
        });
    }
}