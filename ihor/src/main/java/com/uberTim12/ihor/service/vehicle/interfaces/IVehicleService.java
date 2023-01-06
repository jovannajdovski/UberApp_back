package com.uberTim12.ihor.service.vehicle.interfaces;

import com.uberTim12.ihor.exception.EntityPropertyIsNullException;
import com.uberTim12.ihor.model.route.Location;
import com.uberTim12.ihor.model.vehicle.Vehicle;
import com.uberTim12.ihor.service.base.interfaces.IJPAService;

public interface IVehicleService extends IJPAService<Vehicle> {
    void addVehicleToDriver(Integer driverId, Vehicle vehicle);

    void changeVehicleLocation(Integer vehicleId, Location location) throws EntityPropertyIsNullException;
}
