package com.uberTim12.ihor.service.vehicle.interfaces;

import com.uberTim12.ihor.exception.EntityPropertyIsNullException;
import com.uberTim12.ihor.model.route.Location;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.vehicle.Vehicle;
import com.uberTim12.ihor.model.vehicle.VehicleCategory;
import com.uberTim12.ihor.service.base.interfaces.IJPAService;
import jakarta.persistence.EntityNotFoundException;

public interface IVehicleService extends IJPAService<Vehicle> {
    void addVehicleToDriver(Integer driverId, Vehicle vehicle);

    Vehicle updateVehicleForDriver(Integer driverId, VehicleCategory vehicleCategory, String vehicleModel,
                                   String registrationPlate, Location currentLocation, Integer seats,
                                   boolean babiesAllowed, boolean petsAllowed)
            throws EntityNotFoundException, EntityPropertyIsNullException;

    void changeVehicleLocation(Integer vehicleId, Location location) throws EntityPropertyIsNullException;

    Vehicle getVehicleOf(Integer driverId) throws EntityPropertyIsNullException;
    Vehicle save(Vehicle vehicle);
    void remove(Integer id);
    Vehicle findOne(Integer id);

    boolean isVehicleMeetCriteria(Vehicle vehicle, Ride ride);
}
