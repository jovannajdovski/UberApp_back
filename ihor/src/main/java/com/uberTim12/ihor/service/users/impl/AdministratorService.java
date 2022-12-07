package com.uberTim12.ihor.service.users.impl;

import com.uberTim12.ihor.repository.users.IAdministratorRepository;
import com.uberTim12.ihor.service.users.interfaces.IAdministratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdministratorService implements IAdministratorService {
    @Autowired
    private IAdministratorRepository IAdministratorRepository;
}
