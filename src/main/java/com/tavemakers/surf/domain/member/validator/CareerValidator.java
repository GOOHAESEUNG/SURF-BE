package com.tavemakers.surf.domain.member.validator;

import com.tavemakers.surf.domain.member.entity.Career;
import com.tavemakers.surf.domain.member.exception.CareerNotFoundException;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CareerValidator {

    public void validateCareer(Set<Long> reqIds,  List<Career> careersTo) {
        if(careersTo.size() != reqIds.size()) {
            Set<Long> validIds = careersTo.stream()
                    .map(Career::getId)
                    .collect(Collectors.toSet());
            Set<Long> invalidIds = new HashSet<>(reqIds);
            invalidIds.removeAll(validIds);
            throw new CareerNotFoundException("잘못되었거나 권한이 없는 경력 ID: " + invalidIds);
        }
    }
}
