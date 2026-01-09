package com.tavemakers.surf.domain.notification.service;

import com.tavemakers.surf.domain.member.entity.Member;
import com.tavemakers.surf.domain.member.entity.enums.MemberStatus;
import com.tavemakers.surf.domain.member.repository.MemberRepository;
import com.tavemakers.surf.domain.notification.entity.NotificationType;
import com.tavemakers.surf.domain.post.entity.Post;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationUseCase {

    private final MemberRepository memberRepository;
    private final NotificationCreateService notificationCreateService;


    /**
     * 공지 게시글 발행 시
     * 이번 활동 기수(또는 활성 유저) 전체에게 알림 전송
     */
    @Transactional
    public void notifyNoticePost(Post post) {

        // 방어 로직 (이중 체크)
        if (!post.getBoard().isNotice()) {
            return;
        }

        // 1 알림 대상 조회
        List<Member> targets = memberRepository.findByActivityStatusTrueAndStatusNot(MemberStatus.WITHDRAWN);

        if (targets.isEmpty()) {
            log.info("[NoticeNotification] no target members, postId={}", post.getId());
            return;
        }

        // 2 Notification 엔티티 생성
        for (Member member : targets) {
            notificationCreateService.createAndSend(
                    member.getId(),                    // 알림 받는 사람
                    NotificationType.NOTICE,            // 공지 알림 타입
                    Map.of(
                            "boardId", post.getBoard().getId(),
                            "postId", post.getId()
                    )
            );
        }

        log.info(
                "[NoticeNotification] sent to {} members, postId={}",
                targets.size(),
                post.getId()
        );
    }
}
