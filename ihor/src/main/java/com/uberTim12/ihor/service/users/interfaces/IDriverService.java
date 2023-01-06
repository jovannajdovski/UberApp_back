package com.uberTim12.ihor.service.users.interfaces;

import com.uberTim12.ihor.exception.EmailAlreadyExistsException;
import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.model.vehicle.Vehicle;
import com.uberTim12.ihor.service.base.interfaces.IJPAService;

public interface IDriverService extends IJPAService<Driver> {
    Driver findByEmail(String email);

    void register(Driver driver) throws EmailAlreadyExistsException;

    Driver update(Integer driverId, String name, String surname, String profilePicture,
                  String telephoneNumber, String email, String address, String password);
}
