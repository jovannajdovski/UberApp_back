package com.uberTim12.ihor.service.vehicle.impl;

import com.uberTim12.ihor.model.vehicle.VehicleType;
import com.uberTim12.ihor.repository.vehicle.IVehicleTypeRepository;
import com.uberTim12.ihor.service.vehicle.interfaces.IVehicleTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VehicleTypeService implements IVehicleTypeService {

    private final IVehicleTypeRepository vehicleTypeRepository;

    @Autowired
    VehicleTypeService(IVehicleTypeRepository vehicleTypeRepository) {
        this.vehicleTypeRepository = vehicleTypeRepository;
    }

    @Override
    public VehicleType save(VehicleType vehicleType) {
        return vehicleTypeRepository.saveAndFlush(vehicleType);
    }

    @Override
    public void remove(Integer id) {
        vehicleTypeRepository.deleteById(id);
    }
}
