package com.uberTim12.ihor.service.users.interfaces;

import com.uberTim12.ihor.model.users.Administrator;

public interface IAdministratorService {

    Administrator findById(Integer id);

    Administrator save(Administrator administrator);
}
