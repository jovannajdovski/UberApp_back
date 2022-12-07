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

import com.uberTim12.ihor.dto.users.PassengerDTO;
import com.uberTim12.ihor.dto.users.PassengerRegistrationDTO;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.model.users.UserActivation;
import com.uberTim12.ihor.repository.ride.IRideRepository;
import com.uberTim12.ihor.repository.users.IPassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.List;

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
        return new RideResponseDTO(20.0, 450.0);
    }

    @Override
    public Page<Ride> getRides(Integer userId, LocalDateTime start, LocalDateTime end, Pageable page) {
        Optional<Driver> driver = driverRepository.findById(userId);
        if (driver.isPresent()) return rideRepository.findAllInRangeForDriver(userId, start, end, page);

        Optional<Passenger> passenger = passengerRepository.findById(userId);
        return passenger.map(value -> rideRepository.findAllInRangeForPassenger(value, start, end, page)).orElse(null);

    }

    public Ride save(Ride ride){
        return rideRepository.save(ride);
    }

    public Ride findById(Integer id){
        return rideRepository.findById(id).orElseGet(null);
    }

    public Ride findActiveByDriver(Driver driver){
        List<Ride> rides = rideRepository.findActiveByDriver(driver, LocalDateTime.now());
        if (rides.isEmpty()){
            return null;
        } else {
            return rides.get(0);
        }
    }

    public Ride findActiveByPassenger(Passenger passenger){
        List<Ride> rides = rideRepository.findActiveByPassenger(passenger, LocalDateTime.now());
        if (rides.isEmpty()){
            return null;
        } else {
            return rides.get(0);
        }
    }
}
