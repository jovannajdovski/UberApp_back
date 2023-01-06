package com.uberTim12.ihor.service.users.impl;

import com.uberTim12.ihor.model.users.Administrator;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.model.vehicle.Vehicle;
import com.uberTim12.ihor.repository.users.IAdministratorRepository;
import com.uberTim12.ihor.service.base.impl.JPAService;
import com.uberTim12.ihor.service.users.interfaces.IAdministratorService;
import com.uberTim12.ihor.service.vehicle.interfaces.IVehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class AdministratorService extends JPAService<Administrator> implements IAdministratorService {
    private final IAdministratorRepository administratorRepository;

    @Autowired
    public AdministratorService(IAdministratorRepository administratorRepository) {
        this.administratorRepository = administratorRepository;
    }

    @Override
    protected JpaRepository<Administrator, Integer> getEntityRepository() {
        return administratorRepository;
    }
}
