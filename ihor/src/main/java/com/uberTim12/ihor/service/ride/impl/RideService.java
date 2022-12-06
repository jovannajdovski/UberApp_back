package com.uberTim12.ihor.service.ride.impl;

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
public class RideService {

    @Autowired
    private IRideRepository rideRepository;

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
