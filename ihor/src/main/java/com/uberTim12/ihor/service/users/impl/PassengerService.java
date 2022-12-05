package com.uberTim12.ihor.service.users.impl;

import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.repository.users.IPassengerRepository;
import com.uberTim12.ihor.service.users.interfaces.IPassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PassengerService implements IPassengerService {

    @Autowired
    private IPassengerRepository passengerRepository;

    public Page<Passenger> findAll(Pageable page){
        return passengerRepository.findAll(page);
    }

    public Passenger findById(Integer id) {
        return passengerRepository.findById(id).orElseGet(null);
    }

    public Passenger save(Passenger passenger) {
        return passengerRepository.save(passenger);
    }
}
