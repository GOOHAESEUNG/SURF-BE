package com.tavemakers.surf.domain.reservation.usecase;

import com.tavemakers.surf.domain.reservation.entity.Reservation;
import com.tavemakers.surf.domain.reservation.service.ReservationSaveService;
import com.tavemakers.surf.domain.reservation.service.ReservationScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationUsecase {

    private final ReservationSaveService reservationSaveService;
    private final ReservationScheduleService scheduleService;

    public void reservePost(Long postId, LocalDateTime reservedAt) {
        Instant publishAt = toInstant(reservedAt);
        Reservation reservation = Reservation.of(postId, publishAt);
        Reservation savedReservation = reservationSaveService.save(reservation);

        scheduleService.schedule(savedReservation.getId(), publishAt);
    }

    private Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.of("Asia/Seoul")).toInstant();
    }

}
