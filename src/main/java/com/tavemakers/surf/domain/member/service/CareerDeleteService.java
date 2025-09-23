package com.tavemakers.surf.domain.member.service;

import com.tavemakers.surf.domain.member.entity.Career;
import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.exception.CareerNotFoundException;
import com.tavemakers.surf.domain.member.repository.CareerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CareerDeleteService {

    private final CareerRepository careerRepository;

    //경력 삭제
    @Transactional
    public void deleteCareer(Member member, List<Long> careerIds){

        List<Career> careersToDelete = careerRepository.findAllByMemberAndIdIn(member, careerIds);

        if (careersToDelete.size() != careerIds.size()) {
            Set<Long> validIds = careersToDelete.stream()
                    .map(Career::getId)
                    .collect(Collectors.toSet());
            careerIds.removeAll(validIds);
            throw new CareerNotFoundException("잘못되었거나 권한이 없는 경력 ID: " + careerIds);
        }

        careerRepository.deleteAll(careersToDelete);
    }
}
