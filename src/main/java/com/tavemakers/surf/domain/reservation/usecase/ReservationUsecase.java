package com.tavemakers.surf.domain.reservation.usecase;

import com.tavemakers.surf.domain.reservation.entity.Reservation;
import com.tavemakers.surf.domain.reservation.service.ReservationGetService;
import com.tavemakers.surf.domain.reservation.service.ReservationSaveService;
import com.tavemakers.surf.domain.reservation.service.ReservationScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationUsecase {

    private final ReservationSaveService reservationSaveService;
    private final ReservationGetService reservationGetService;
    private final ReservationScheduleService scheduleService;

    public void reservePost(Long postId, LocalDateTime reservedAt) {
        Instant publishAt = toInstant(reservedAt);
        Reservation reservation = Reservation.of(postId, publishAt);
        Reservation savedReservation = reservationSaveService.save(reservation);
        scheduleService.schedule(savedReservation.getId(), publishAt);
    }

    @Transactional
    public void updateReservationPost(Long postId, LocalDateTime changedAt) {
        Reservation existed = reservationGetService.findByPostIdAndStatus(postId);
        if(existed != null) {
            existed.cancel();
        }

        Instant publishAt = toInstant(changedAt);
        Reservation reservation = Reservation.of(postId, publishAt);
        Reservation saved = reservationSaveService.save(reservation);
        scheduleService.schedule(saved.getId(), publishAt);
    }

    private Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.of("Asia/Seoul")).toInstant();
    }

}
