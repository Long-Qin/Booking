package com.example.staybooking.exception;

import com.example.staybooking.repository.ReservationRepository;

public class ReservationCollisionException extends RuntimeException{
    public ReservationCollisionException(String message){
        super(message);
    }
}
