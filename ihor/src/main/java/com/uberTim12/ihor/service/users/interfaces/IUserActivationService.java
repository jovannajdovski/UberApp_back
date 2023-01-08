package com.uberTim12.ihor.service.users.interfaces;

import com.uberTim12.ihor.exception.UserActivationExpiredException;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.model.users.User;
import com.uberTim12.ihor.model.users.UserActivation;
import com.uberTim12.ihor.model.vehicle.Vehicle;
import com.uberTim12.ihor.service.base.interfaces.IJPAService;
import jakarta.persistence.EntityNotFoundException;

public interface IUserActivationService extends IJPAService<UserActivation> {
    UserActivation create(User user);
    void activate(Integer activationId) throws EntityNotFoundException, UserActivationExpiredException;
}
