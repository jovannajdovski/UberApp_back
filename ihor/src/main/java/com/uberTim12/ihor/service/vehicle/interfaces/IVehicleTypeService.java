package com.uberTim12.ihor.service.vehicle.interfaces;

import com.uberTim12.ihor.model.vehicle.VehicleType;

public interface IVehicleTypeService {
    VehicleType save(VehicleType vehicleType);
    void remove(Integer id);
}
