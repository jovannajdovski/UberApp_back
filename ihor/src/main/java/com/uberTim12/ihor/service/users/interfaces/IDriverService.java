package com.uberTim12.ihor.service.users.interfaces;

import com.uberTim12.ihor.exception.EmailAlreadyExistsException;
import com.uberTim12.ihor.model.ride.ActiveDriver;
import com.uberTim12.ihor.model.ride.ActiveDriverCriticalRide;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.service.base.interfaces.IJPAService;

public interface IDriverService extends IJPAService<Driver> {
    Driver findByEmail(String email);

    Driver register(Driver driver) throws EmailAlreadyExistsException;

    Driver update(Integer driverId, String name, String surname, String profilePicture,
                  String telephoneNumber, String email, String address, String password);
import java.time.LocalDateTime;
import java.util.List;

public interface IDriverService {
    Driver save(Driver driver);
    Driver findOne(Integer id);
    List<Driver> findAll();
    Driver findByEmail(String email);
    Driver findOneWithDocuments(Integer driverId);
    Vehicle getVehicleFor(Integer driverId);
    Page<Driver> findAll(Pageable page);
    Driver findById(Integer id);

    boolean isDriverAvailable(Driver driver, Ride ride);

    boolean isDriverFreeForRide(Driver driver, Ride ride);

    List<ActiveDriverCriticalRide> sortPerEndOfCriticalRide(List<ActiveDriver> activeDrivers, Ride newRide);
}
