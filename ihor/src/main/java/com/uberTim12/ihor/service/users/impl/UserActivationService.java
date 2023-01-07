package com.uberTim12.ihor.service.users.impl;

import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.model.users.UserActivation;
import com.uberTim12.ihor.model.vehicle.Vehicle;
import com.uberTim12.ihor.repository.users.IPassengerRepository;
import com.uberTim12.ihor.repository.users.IUserActivationRepository;
import com.uberTim12.ihor.service.base.impl.JPAService;
import com.uberTim12.ihor.service.users.interfaces.IUserActivationService;
import com.uberTim12.ihor.service.vehicle.interfaces.IVehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserActivationService extends JPAService<UserActivation> implements IUserActivationService {
    private final IUserActivationRepository userActivationRepository;

    @Autowired
    public UserActivationService(IUserActivationRepository userActivationRepository) {
        this.userActivationRepository = userActivationRepository;
    }

    @Override
    protected JpaRepository<UserActivation, Integer> getEntityRepository() {
        return userActivationRepository;
    }

    @Override
    public UserActivation save(Passenger passenger) {
        UserActivation userActivation = new UserActivation();
        userActivation.setUser(passenger);
        userActivation.setCreationDate(LocalDateTime.now());
        userActivation.setExpiryDate(userActivation.getCreationDate().plusYears(1));

        return userActivationRepository.save(userActivation);
    }
}
