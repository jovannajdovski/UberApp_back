package com.uberTim12.ihor.service.users.interfaces;

import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.model.vehicle.Vehicle;
import com.uberTim12.ihor.service.base.interfaces.IJPAService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IDriverService extends IJPAService<Driver> {
    Driver findByEmail(String email);
    Vehicle getVehicleFor(Integer driverId);
}
