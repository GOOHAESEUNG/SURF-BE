package com.tavemakers.surf.domain.reservation.task;

import com.tavemakers.surf.domain.post.entity.Post;
import com.tavemakers.surf.domain.post.exception.PostAlreadyDeletedException;
import com.tavemakers.surf.domain.post.repository.PostRepository;
import com.tavemakers.surf.domain.post.service.support.PostPublishedEvent;
import com.tavemakers.surf.domain.reservation.entity.Reservation;
import com.tavemakers.surf.domain.reservation.exception.ReservationCanceledException;
import com.tavemakers.surf.domain.reservation.service.ReservationGetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostPublishRunner {

    private final ReservationGetService reservationGetService;
    private final PostRepository postRepository;
    private final ApplicationEventPublisher eventPublisher;


    @Transactional(noRollbackFor = PostAlreadyDeletedException.class)
    public void publishPost(Long reservationId) {
        Reservation reservation = reservationGetService.getReservationById(reservationId);
        validateReservation(reservation);

        Post post = postRepository.findById(reservation.getPostId()).orElse(null);
        cancelReservationIfPostDeleted(post, reservation);

        log.info("예약 번호 {}번 예약 작업 수행", reservationId);
        post.publish();
        reservation.publish();

        eventPublisher.publishEvent(
                new PostPublishedEvent(post.getId())
        );

    }

    private void cancelReservationIfPostDeleted(Post post, Reservation reservation) {
        if(post == null) {
            reservation.cancel();
            throw new PostAlreadyDeletedException();
        }
    }

    private void validateReservation(Reservation reservation) {
        if(reservation == null) {
            throw new ReservationCanceledException();
        }
    }

}
