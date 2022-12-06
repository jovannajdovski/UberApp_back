package com.uberTim12.ihor.service.users.impl;

import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.repository.ride.IRideRepository;
import com.uberTim12.ihor.repository.users.IDriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DriverService {

    @Autowired
    private IDriverRepository driverRepository;

    public Driver findById(Integer id){
        return driverRepository.findById(id).orElseGet(null);
    }
}
