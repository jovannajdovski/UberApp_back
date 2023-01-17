package com.uberTim12.ihor.service.users.interfaces;

import com.uberTim12.ihor.exception.EmailAlreadyExistsException;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.service.base.interfaces.IJPAService;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

import java.time.LocalDateTime;

public interface IPassengerService extends IJPAService<Passenger> {

    Passenger register(Passenger passenger) throws EmailAlreadyExistsException, MessagingException, UnsupportedEncodingException;

    Passenger update(Integer passengerId, String name, String surname, String profilePicture,
                     String telephoneNumber, String email, String address, String password)
            throws EntityNotFoundException;

    Passenger findByEmail(String email);

    Page<Ride> findAllById(Integer passengerId, LocalDateTime start, LocalDateTime end, Pageable page);

    Page<Ride> findAllById(Passenger passenger, Pageable page);

    Passenger findByIdWithRides(Integer id);

    Optional<Passenger> findByIdWithFavorites(Integer id);

    Passenger findByEmailWithFavorites(String email);

    boolean isPassengersFree(Ride ride);
}
