package com.uberTim12.ihor.service.vehicle.impl;

import com.uberTim12.ihor.model.vehicle.VehicleType;
import com.uberTim12.ihor.repository.vehicle.IVehicleTypeRepository;
import com.uberTim12.ihor.service.base.impl.JPAService;
import com.uberTim12.ihor.service.vehicle.interfaces.IVehicleTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class VehicleTypeService extends JPAService<VehicleType> implements IVehicleTypeService {

    private final IVehicleTypeRepository vehicleTypeRepository;

    @Autowired
    VehicleTypeService(IVehicleTypeRepository vehicleTypeRepository) {
        this.vehicleTypeRepository = vehicleTypeRepository;
    }

    @Override
    protected JpaRepository<VehicleType, Integer> getEntityRepository() {
        return vehicleTypeRepository;
    }
}
