package com.tavemakers.surf.domain.notification.service;

import com.google.firebase.messaging.*;
import com.tavemakers.surf.domain.notification.entity.DeviceToken;
import com.tavemakers.surf.domain.notification.repository.DeviceTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FcmService {

    private final DeviceTokenRepository deviceTokenRepository;

    public void sendToMember(Long memberId, String body, String deeplink) {
        List<DeviceToken> tokens = deviceTokenRepository.findAllByMemberIdAndEnabledTrue(memberId);
        if (tokens.isEmpty()) return;

        List<String> tokenStrings = tokens.stream().map(DeviceToken::getToken).toList();

        MulticastMessage message = MulticastMessage.builder()
                .setNotification(Notification.builder()
                        .setTitle("SURF")
                        .setBody(body)
                        .build())
                .putData("deepLink", deeplink)
                // data payload는 나중에 딥링크/타입 붙일 때 추가하면 됨
                .addAllTokens(tokenStrings)
                .build();

        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);

            // 실패한 토큰 처리
            List<SendResponse> responses = response.getResponses();
            for (int i = 0; i < responses.size(); i++) {
                SendResponse r = responses.get(i);
                if (r.isSuccessful()) continue;

                FirebaseMessagingException ex = r.getException();
                String failedToken = tokenStrings.get(i);

                // 흔한 무효 토큰 케이스: UNREGISTERED / INVALID_ARGUMENT 등
                if (isInvalidToken(ex)) {
                    deviceTokenRepository.findByToken(failedToken)
                            .ifPresent(DeviceToken::disable);
                }
            }
        } catch (FirebaseMessagingException e) {
            // 여기서는 일단 런타임으로 던져서 로그/모니터링 하게 하는 게 보통
            throw new RuntimeException("FCM send failed", e);
        }
    }

    private boolean isInvalidToken(FirebaseMessagingException ex) {
        // Firebase Admin SDK에서 MessagingErrorCode를 제공
        MessagingErrorCode code = ex.getMessagingErrorCode();
        return code == MessagingErrorCode.UNREGISTERED
                || code == MessagingErrorCode.INVALID_ARGUMENT;
    }
}