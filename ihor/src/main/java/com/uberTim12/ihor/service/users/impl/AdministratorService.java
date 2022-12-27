package com.uberTim12.ihor.service.users.impl;

import com.uberTim12.ihor.model.users.Administrator;
import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.repository.users.IAdministratorRepository;
import com.uberTim12.ihor.service.users.interfaces.IAdministratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdministratorService implements IAdministratorService {
    @Autowired
    private IAdministratorRepository administratorRepository;

    @Override
    public Administrator findById(Integer id) {
        return administratorRepository.findById(id).orElse(null);
    }

    @Override
    public Administrator save(Administrator administrator) {
        return administratorRepository.save(administrator);
    }
}
