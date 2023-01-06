package com.uberTim12.ihor.service.users.impl;

import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.repository.ride.IRideRepository;
import com.uberTim12.ihor.repository.users.IPassengerRepository;
import com.uberTim12.ihor.service.base.impl.JPAService;
import com.uberTim12.ihor.service.users.interfaces.IPassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PassengerService extends JPAService<Passenger> implements IPassengerService {

    private final IPassengerRepository passengerRepository;

    private final IRideRepository rideRepository;

    @Autowired
    public PassengerService(IPassengerRepository passengerRepository, IRideRepository rideRepository) {
        this.passengerRepository = passengerRepository;
        this.rideRepository = rideRepository;
    }

    @Override
    protected JpaRepository<Passenger, Integer> getEntityRepository() {
        return passengerRepository;
    }

    public Page<Ride> findAllById(Integer passengerId, LocalDateTime start, LocalDateTime end, Pageable page){
        Optional<Passenger> passenger=passengerRepository.findById(passengerId);
        return passenger.map(value -> rideRepository.findAllInRangeForPassenger(value, start, end, page)).orElse(null);
    }

    public Page<Ride> findAllById(Passenger passenger, Pageable page){
        return rideRepository.findAllForPassenger(passenger, page);
    }

    @Override
    public boolean exists(String email){
        return passengerRepository.existsByEmail(email);
    }

    @Override
    public Passenger findByIdWithRides(Integer id) {
        return passengerRepository.findByIdWithRides(id);
    }
}
