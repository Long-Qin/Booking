package com.example.staybooking.service;

import com.example.staybooking.exception.ReservationCollisionException;
import com.example.staybooking.exception.ReservationNotFoundException;
import com.example.staybooking.model.*;
import com.example.staybooking.repository.ReservationRepository;
import com.example.staybooking.repository.StayReservationDateRepository;
import jdk.jfr.TransitionTo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final StayReservationDateRepository stayReservationDateRepository;


    public ReservationService(ReservationRepository reservationRepository, StayReservationDateRepository stayReservationDateRepository) {
        this.reservationRepository = reservationRepository;
        this.stayReservationDateRepository = stayReservationDateRepository;
    }


    public List<Reservation> listByGuest(String username) {
        return reservationRepository.findByGuest(new User.Builder().setUsername(username).build());
    }


    public List<Reservation> listByStay(Long stayId) {
        return reservationRepository.findByStay(new Stay.Builder().setId(stayId).build());
    }


    @Transactional
    public void add(Reservation reservation) throws ReservationCollisionException {
        Set<Long> stayIds = stayReservationDateRepository.findByIdInAndDateBetween(
                List.of(reservation.getStay().getId()),
                reservation.getCheckinDate(),
                reservation.getCheckoutDate().minusDays(1)
        );
        if (!stayIds.isEmpty()) {
            throw new ReservationCollisionException("Duplicate reservation");
        }


        List<StayReservedDate> reservedDates = new ArrayList<>();
        LocalDate start = reservation.getCheckinDate();
        LocalDate end = reservation.getCheckoutDate();
        for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) {
            StayReservedDateKey id = new StayReservedDateKey(reservation.getStay().getId(), date);
            StayReservedDate reservedDate = new StayReservedDate(id, reservation.getStay());
            reservedDates.add(reservedDate);
        }
        stayReservationDateRepository.saveAll(reservedDates);
        reservationRepository.save(reservation);
    }


    @Transactional
    public void delete(Long reservationId, String username) {

        Reservation reservation = reservationRepository.findByIdAndGuest(reservationId, new User.Builder().setUsername(username).build());
        if (reservation == null) {
            throw new ReservationNotFoundException("Reservation is not available");
        }
        LocalDate start = reservation.getCheckinDate();
        LocalDate end = reservation.getCheckoutDate();
        for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) {
            stayReservationDateRepository.deleteById(new StayReservedDateKey(reservation.getStay().getId(), date));
        }
        reservationRepository.deleteById(reservationId);
    }
}

