package com.uberTim12.ihor.service.ride.interfaces;

import com.uberTim12.ihor.model.ride.Ride;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface IRideService {

    Page<Ride> findFilteredRides(Integer driverId, Pageable pageable);
    Page<Ride> findFilteredRides(Integer driverId, LocalDate from, LocalDate to, Pageable pageable);
}
