package com.uberTim12.ihor.service.ride.interfaces;

import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.ride.RideRequestDTO;
import com.uberTim12.ihor.model.ride.RideResponseDTO;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;

public interface IRideService {
    RideResponseDTO getEstimatedRoute(RideRequestDTO rideRequestDTO);

    Page<Ride> getRides(Integer id, Pageable page);
}
