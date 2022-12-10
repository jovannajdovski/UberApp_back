package com.uberTim12.ihor.service.vehicle.interfaces;

import com.uberTim12.ihor.model.vehicle.Vehicle;

public interface IVehicleService {

    Vehicle save(Vehicle vehicle);
    void remove(Integer id);
    Vehicle findOne(Integer id);
}
