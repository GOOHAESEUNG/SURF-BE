package com.tavemakers.surf.domain.notification.service;

import com.tavemakers.surf.domain.notification.dto.req.DeviceTokenReqDTO;
import com.tavemakers.surf.domain.notification.entity.DeviceToken;
import com.tavemakers.surf.domain.notification.repository.DeviceTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeviceTokenRegisterService {

    private final DeviceTokenRepository deviceTokenRepository;

    @Transactional
    public void register(Long memberId, DeviceTokenReqDTO dto) {
        deviceTokenRepository.findByToken(dto.token())
                .ifPresentOrElse(
                        token -> {
                            //토큰 소유자가 변경된 경우 업데이트
                            if (!token.getMemberId().equals(memberId)) {
                                token.updateOwner(memberId);
                            } else {
                                token.touch();
                            }
                        },
                        () -> deviceTokenRepository.save(
                                DeviceToken.builder()
                                        .memberId(memberId)
                                        .token(dto.token())
                                        .platform(dto.platform())
                                        .build()
                        )
                );
    }
}