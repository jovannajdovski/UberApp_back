package com.uberTim12.ihor.service.ride.impl;

import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.repository.ride.IRideRepository;
import com.uberTim12.ihor.service.ride.interfaces.IRideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class RideService implements IRideService {

    private IRideRepository rideRepository;
    @Autowired
    RideService(IRideRepository rideRepository) {
        this.rideRepository = rideRepository;
    }

    @Override
    public Page<Ride> findFilteredRides(Integer driverId, Pageable pageable) {
        return rideRepository.findByDriverId(driverId, pageable);
    }

    @Override
    public Page<Ride> findFilteredRides(Integer driverId, LocalDate from, LocalDate to, Pageable pageable) {
        return rideRepository.findByDriverIdAndDateRange(driverId, from, to, pageable);
    }
}
