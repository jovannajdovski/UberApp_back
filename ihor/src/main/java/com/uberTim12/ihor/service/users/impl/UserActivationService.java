package com.uberTim12.ihor.service.users.impl;

import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.model.users.UserActivation;
import com.uberTim12.ihor.repository.users.IPassengerRepository;
import com.uberTim12.ihor.repository.users.IUserActivationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserActivationService {
    @Autowired
    private IUserActivationRepository userActivationRepository;

    public UserActivation save(UserActivation userActivation) {
        return userActivationRepository.save(userActivation);
    }
}
