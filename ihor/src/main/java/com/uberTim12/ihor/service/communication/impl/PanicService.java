package com.uberTim12.ihor.service.communication.impl;

import com.uberTim12.ihor.model.communication.Panic;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.repository.communication.IPanicRepository;
import com.uberTim12.ihor.repository.ride.IRideRepository;
import com.uberTim12.ihor.service.communication.interfaces.IPanicService;
import com.uberTim12.ihor.service.ride.interfaces.IRideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PanicService implements IPanicService {

    @Autowired
    private IPanicRepository panicRepository;

    @Autowired
    private IRideService rideService;

    @Override
    public Panic save(Panic panic){
        return panicRepository.save(panic);
    }

    @Override
    public List<Panic> findAll(){
        List<Panic> panics = panicRepository.findAll();
        for (Panic panic : panics){
            Ride curRide = panic.getCurrentRide();
            Set<Passenger> passengers = new HashSet<>(rideService.findPassengersForRide(curRide.getId()));
            curRide.setPassengers(passengers);
        }
        return panics;
    }
}
