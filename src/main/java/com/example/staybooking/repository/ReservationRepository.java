package com.example.staybooking.repository;

import com.example.staybooking.model.Reservation;
import com.example.staybooking.model.Stay;
import com.example.staybooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByGuest(User guest);

    List<Reservation> findByStay(Stay stay);

    Reservation findByIdAndGuest(Long id, User guest);


}
