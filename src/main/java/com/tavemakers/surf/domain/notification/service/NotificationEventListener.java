package com.tavemakers.surf.domain.notification.service;

import static com.tavemakers.surf.domain.post.exception.ErrorMessage.POST_NOT_FOUND;

import com.tavemakers.surf.domain.post.entity.Post;
import com.tavemakers.surf.domain.post.exception.PostNotFoundException;
import com.tavemakers.surf.domain.post.repository.PostRepository;
import com.tavemakers.surf.domain.post.service.PostPublishedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final PostRepository postRepository;
    private final NotificationUseCase notificationUseCase;

    @Async
    @EventListener
    public void handle(PostPublishedEvent event) {
        Post post = postRepository.findById(event.getPostId())
                .orElseThrow(PostNotFoundException::new);

        if (!post.getBoard().isNotice()) {
            return;
        }

        notificationUseCase.notifyNoticePost(post);

        log.info("[NoticeNotification] sent for postId={}", post.getId());
    }
}