package com.uberTim12.ihor.service.users.impl;

import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.model.vehicle.Vehicle;
import com.uberTim12.ihor.repository.users.IDriverRepository;
import com.uberTim12.ihor.service.base.impl.JPAService;
import com.uberTim12.ihor.service.users.interfaces.IDriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverService extends JPAService<Driver> implements IDriverService {
    private IDriverRepository driverRepository;

    @Autowired
    DriverService(IDriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    @Override
    protected JpaRepository<Driver, Integer> getEntityRepository() {
        return driverRepository;
    }

    @Override
    public Driver findByEmail(String email) {
        return driverRepository.findByEmail(email);
    }

    @Override
    public Vehicle getVehicleFor(Integer driverId) {
        Driver driver = get(driverId);
        if (driver != null)
            return driver.getVehicle();

        return null;
    }
}
