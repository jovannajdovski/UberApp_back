package com.uberTim12.ihor.service.users.impl;

import com.uberTim12.ihor.repository.users.IDriverRepository;
import com.uberTim12.ihor.service.users.interfaces.IDriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DriverService implements IDriverService {
    @Autowired
    private IDriverRepository IDriverRepository;
}
