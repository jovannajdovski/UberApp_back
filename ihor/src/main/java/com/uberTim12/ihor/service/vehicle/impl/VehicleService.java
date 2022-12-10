package com.uberTim12.ihor.service.vehicle.impl;

import com.uberTim12.ihor.model.vehicle.Vehicle;
import com.uberTim12.ihor.repository.vehicle.IVehicleRepository;
import com.uberTim12.ihor.service.vehicle.interfaces.IVehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VehicleService implements IVehicleService {

    private final IVehicleRepository vehicleRepository;

    @Autowired
    VehicleService(IVehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }
    @Override
    public Vehicle save(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }
    @Override
    public void remove(Integer id) {
        vehicleRepository.deleteById(id);
    }
    @Override
    public Vehicle findOne(Integer id) {
        return vehicleRepository.findById(id).orElse(null);
    }
}
