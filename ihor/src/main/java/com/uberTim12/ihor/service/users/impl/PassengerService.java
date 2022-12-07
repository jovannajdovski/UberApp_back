package com.uberTim12.ihor.service.users.impl;

import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.repository.ride.IRideRepository;
import com.uberTim12.ihor.repository.users.IPassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PassengerService implements com.uberTim12.ihor.service.users.interfaces.IPassengerService {

    @Autowired
    private IPassengerRepository passengerRepository;

    private IRideRepository rideRepository;

    @Override
    public Page<Passenger> findAll(Pageable page){
        return passengerRepository.findAll(page);
    }

    public Page<Ride> findAllById(Integer passengerId, LocalDateTime start, LocalDateTime end, Pageable page){
        Optional<Passenger> passenger=passengerRepository.findById(passengerId);
        return passenger.map(value -> rideRepository.findAllInRangeForPassenger(value, start, end, page)).orElse(null);
    }

    @Override
    public Passenger findById(Integer id) {
        return passengerRepository.findById(id).orElse(null);
    }

    @Override
    public boolean exists(String email){
        return passengerRepository.existsByEmail(email);
    }

    @Override
    public Passenger save(Passenger passenger) {
        return passengerRepository.save(passenger);
    }


    @Override
    public Passenger findByIdWithRides(Integer id) {
        return passengerRepository.findByIdWithRides(id);
    }
}
