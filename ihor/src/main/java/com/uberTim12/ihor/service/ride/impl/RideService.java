package com.uberTim12.ihor.service.ride.impl;

import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.ride.RideRequestDTO;
import com.uberTim12.ihor.model.ride.RideResponseDTO;
import com.uberTim12.ihor.repository.ride.IRideRepository;
import com.uberTim12.ihor.service.ride.interfaces.IRideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;

@Service
public class RideService implements IRideService {
    @Autowired
    private IRideRepository IRideRepository;

    @Override
    public RideResponseDTO getEstimatedRoute(RideRequestDTO rideRequestDTO)
    {
        return new RideResponseDTO(LocalDateTime.of(2022,10,10,20,20), 450.0);
    }

    @Override
    public Page<Ride> getRides(Integer id, Pageable page) {
        return IRideRepository.findById(id, page);
    }
}
