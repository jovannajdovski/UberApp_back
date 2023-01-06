package com.uberTim12.ihor.service.users.interfaces;

import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.model.users.UserActivation;
import com.uberTim12.ihor.model.vehicle.Vehicle;
import com.uberTim12.ihor.service.base.interfaces.IJPAService;

public interface IUserActivationService extends IJPAService<UserActivation> {
    UserActivation save(Passenger passenger);
}
