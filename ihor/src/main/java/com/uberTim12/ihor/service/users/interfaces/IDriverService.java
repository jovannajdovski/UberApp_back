package com.uberTim12.ihor.service.users.interfaces;

import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.model.vehicle.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IDriverService {
    Driver save(Driver driver);
    Driver findOne(Integer id);
    Driver findByEmail(String email);
    Driver findOneWithDocuments(Integer driverId);
    Vehicle getVehicleFor(Integer driverId);
    Page<Driver> findAll(Pageable page);
    Driver findById(Integer id);
}
