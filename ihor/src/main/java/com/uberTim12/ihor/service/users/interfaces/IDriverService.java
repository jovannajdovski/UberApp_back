package com.uberTim12.ihor.service.users.interfaces;

import com.uberTim12.ihor.exception.EmailAlreadyExistsException;
import com.uberTim12.ihor.model.ride.ActiveDriver;
import com.uberTim12.ihor.model.ride.ActiveDriverCriticalRide;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.service.base.interfaces.IJPAService;
import java.util.List;

public interface IDriverService extends IJPAService<Driver> {
    Driver findByEmail(String email);
    Driver register(Driver driver) throws EmailAlreadyExistsException;
    Driver update(Integer driverId, String name, String surname, String profilePicture,
    String telephoneNumber, String email, String address);
    boolean isDriverFreeForRide(Driver driver, Ride ride);

    boolean isDriverFreeForRide(Driver driver);

    List<ActiveDriverCriticalRide> sortPerEndOfCriticalRide(List<ActiveDriver> activeDrivers, Ride newRide);

    List<ActiveDriver> getActiveDrivers();
}
