package com.uberTim12.ihor.service.users.impl;

import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.model.users.UserActivation;
import com.uberTim12.ihor.repository.users.IPassengerRepository;
import com.uberTim12.ihor.repository.users.IUserActivationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserActivationService {
    @Autowired
    private IUserActivationRepository userActivationRepository;

    public UserActivation save(Passenger passenger) {

        UserActivation userActivation = new UserActivation();
        userActivation.setUser(passenger);
        userActivation.setCreationDate(LocalDateTime.now());
        userActivation.setExpiryDate(userActivation.getCreationDate().plusYears(1));

        return userActivationRepository.save(userActivation);
    }

    public UserActivation findById(Integer id){
        return userActivationRepository.findById(id).orElseGet(null);
    }

    public void remove(Integer id){
        userActivationRepository.deleteById(id);
    }
}
