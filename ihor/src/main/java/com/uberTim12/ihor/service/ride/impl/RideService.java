package com.uberTim12.ihor.service.ride.impl;

import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.ride.RideRequestDTO;
import com.uberTim12.ihor.model.ride.RideResponseDTO;
import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.repository.ride.IRideRepository;
import com.uberTim12.ihor.repository.users.IDriverRepository;
import com.uberTim12.ihor.repository.users.IPassengerRepository;
import com.uberTim12.ihor.service.ride.interfaces.IRideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class RideService implements IRideService {

    @Autowired
    private IRideRepository rideRepository;
    @Autowired
    private IDriverRepository driverRepository;
    @Autowired
    private IPassengerRepository passengerRepository;

    @Override
    public Page<Ride> findFilteredRides(Integer driverId, Pageable pageable) {
        return rideRepository.findByDriverId(driverId, pageable);
    }

    @Override
    public Page<Ride> findFilteredRides(Integer driverId, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        return rideRepository.findAllInRangeForDriver(driverId, from, to, pageable);
    }

    @Override
    public RideResponseDTO getEstimatedRoute (RideRequestDTO rideRequestDTO)
    {
        return new RideResponseDTO(LocalDateTime.of(2022,10,10,20,20), 450.0);
    }

    @Override
    public Page<Ride> getRides(Integer userId, LocalDateTime start, LocalDateTime end, Pageable page) {
        Optional<Driver> driver=driverRepository.findById(userId);
        if(driver.isPresent()) return rideRepository.findAllInRangeForDriver(userId,start,end,page);

        Optional<Passenger> passenger=passengerRepository.findById(userId);
        return passenger.map(value -> rideRepository.findAllInRangeForPassenger(value, start, end, page)).orElse(null);

    }
}
