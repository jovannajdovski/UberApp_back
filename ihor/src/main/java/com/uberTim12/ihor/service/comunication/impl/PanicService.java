package com.uberTim12.ihor.service.comunication.impl;

import com.uberTim12.ihor.model.comunication.Panic;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.repository.comunication.IPanicRepository;
import com.uberTim12.ihor.repository.ride.IRideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PanicService {

    @Autowired
    private IPanicRepository panicRepository;

    public Panic save(Panic panic){
        return panicRepository.save(panic);
    }
}
