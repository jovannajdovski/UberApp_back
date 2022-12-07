package com.uberTim12.ihor.service.ride.interfaces;

import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.ride.RideRequestDTO;
import com.uberTim12.ihor.model.ride.RideResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface IRideService {

    Page<Ride> findFilteredRides(Integer driverId, Pageable pageable);

    Page<Ride> findFilteredRides(Integer driverId, LocalDateTime from, LocalDateTime to, Pageable pageable);

    RideResponseDTO getEstimatedRoute(RideRequestDTO rideRequestDTO);

    Page<Ride> getRides(Integer userId, LocalDateTime start, LocalDateTime end, Pageable page);
}
