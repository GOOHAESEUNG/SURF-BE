package com.tavemakers.surf.domain.reservation.service;

import com.tavemakers.surf.domain.reservation.entity.Reservation;
import com.tavemakers.surf.domain.reservation.entity.ReservationStatus;
import com.tavemakers.surf.domain.reservation.exception.ReservationNotFoundException;
import com.tavemakers.surf.domain.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationGetService {

    private final ReservationRepository reservationRepository;

    public Reservation getReservationById(Long id) {
        return reservationRepository.findByIdAndStatus(id, ReservationStatus.RESERVED)
                .orElseThrow(ReservationNotFoundException::new);
    }

    public List<Reservation> getAllReservation() {
        return reservationRepository.findByStatus(ReservationStatus.RESERVED);
    }

}
