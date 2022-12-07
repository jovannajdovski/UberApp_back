package com.uberTim12.ihor.service.communication.impl;

import com.uberTim12.ihor.model.communication.Panic;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.repository.communication.IPanicRepository;
import com.uberTim12.ihor.repository.ride.IRideRepository;
import com.uberTim12.ihor.service.communication.interfaces.IPanicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PanicService implements IPanicService {

    @Autowired
    private IPanicRepository panicRepository;

    @Override
    public Panic save(Panic panic){
        return panicRepository.save(panic);
    }

    @Override
    public List<Panic> findAll(){
        return panicRepository.findAll();
    }
}
