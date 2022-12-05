package com.uberTim12.ihor.service.users.impl;

import com.uberTim12.ihor.repository.users.IPassengerRepository;
import com.uberTim12.ihor.service.users.interfaces.IPassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PassengerService implements IPassengerService {

    @Autowired
    private IPassengerRepository IPassengerRepository;
}
