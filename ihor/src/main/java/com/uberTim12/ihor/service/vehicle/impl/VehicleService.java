package com.uberTim12.ihor.service.vehicle.impl;

import com.uberTim12.ihor.exception.EntityPropertyIsNullException;
import com.uberTim12.ihor.model.route.Location;
import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.model.ride.Ride;
import com.uberTim12.ihor.model.vehicle.Vehicle;
import com.uberTim12.ihor.model.vehicle.VehicleCategory;
import com.uberTim12.ihor.repository.vehicle.IVehicleRepository;
import com.uberTim12.ihor.service.base.impl.JPAService;
import com.uberTim12.ihor.service.route.interfaces.ILocationService;
import com.uberTim12.ihor.service.users.interfaces.IDriverService;
import com.uberTim12.ihor.service.vehicle.interfaces.IVehicleService;
import com.uberTim12.ihor.service.vehicle.interfaces.IVehicleTypeService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class VehicleService extends JPAService<Vehicle> implements IVehicleService {
    private final IVehicleRepository vehicleRepository;
    private final IDriverService driverService;
    private final ILocationService locationService;
    private final IVehicleTypeService vehicleTypeService;

    @Autowired
    VehicleService(IVehicleRepository vehicleRepository, IDriverService driverService, ILocationService locationService, IVehicleTypeService vehicleTypeService) {
        this.vehicleRepository = vehicleRepository;
        this.driverService = driverService;
        this.locationService = locationService;
        this.vehicleTypeService = vehicleTypeService;
    }

    @Override
    protected JpaRepository<Vehicle, Integer> getEntityRepository() {
        return vehicleRepository;
    }

    @Override
    public void addVehicleToDriver(Integer driverId, Vehicle vehicle) throws EntityNotFoundException {
        Driver driver = driverService.get(driverId);

        locationService.save(vehicle.getCurrentLocation());
        vehicleTypeService.save(vehicle.getVehicleType());
        vehicle.setDriver(driver);
        vehicle = save(vehicle);

        driver.setVehicle(vehicle);
        driverService.save(driver);
    }

    @Override
    public Vehicle updateVehicleForDriver(Integer driverId, VehicleCategory vehicleCategory, String vehicleModel,
                                          String registrationPlate, Location currentLocation, Integer seats,
                                          boolean babiesAllowed, boolean petsAllowed)
            throws EntityNotFoundException, EntityPropertyIsNullException {
        Vehicle vehicle = getVehicleOf(driverId);
        vehicle.getVehicleType().setVehicleCategory(vehicleCategory);
        vehicle.setVehicleModel(vehicleModel);
        vehicle.setRegistrationPlate(registrationPlate);
        vehicle.setSeats(seats);
        vehicle.setBabiesAllowed(babiesAllowed);
        vehicle.setPetsAllowed(petsAllowed);

        vehicle.setCurrentLocation(locationService.save(currentLocation));
        vehicleTypeService.save(vehicle.getVehicleType());
        return save(vehicle);
    }

    @Override
    public void changeVehicleLocation(Integer vehicleId, Location location) throws EntityPropertyIsNullException {
        Vehicle vehicle = get(vehicleId);
        if (vehicle.getDriver() == null)
            throw new EntityPropertyIsNullException("Vehicle is not assigned to driver!");

        location = locationService.save(location);
        vehicle.setCurrentLocation(location);
        saveAndFlush(vehicle);
    }

    @Override
    public Vehicle getVehicleOf(Integer driverId) throws EntityNotFoundException, EntityPropertyIsNullException {
        Driver driver = driverService.get(driverId);
        if (driver.getVehicle() == null)
            throw new EntityPropertyIsNullException("Driver does not have assigned vehicle!");

        return driver.getVehicle();
    }

    @Override
    public boolean isVehicleMeetCriteria(Vehicle vehicle, Ride ride) {
        VehicleCategory choosenVehicleCategory=ride.getVehicleType().getVehicleCategory();
        if((choosenVehicleCategory!=null && vehicle.getVehicleType().getVehicleCategory()!=choosenVehicleCategory)
            || (ride.isPetsAllowed() && !vehicle.isPetsAllowed())
            || (ride.isBabiesAllowed() && !vehicle.isBabiesAllowed()))
            return false;
        return true;
    }

    public void assignVehicleToDriver(int driverId, int vehicleId) {
        Driver driver = driverService.get(driverId);
        Vehicle vehicle = get(vehicleId);
        driver.setVehicle(vehicle);
        driverService.save(driver);
    }
}
