package com.uberTim12.ihor.service.users.interfaces;

import com.uberTim12.ihor.model.users.Administrator;
import com.uberTim12.ihor.service.base.interfaces.IJPAService;
import jakarta.persistence.EntityNotFoundException;

public interface IAdministratorService extends IJPAService<Administrator> {
    Administrator update(Integer adminId, String name, String surname, String profilePicture,
                         String telephoneNumber, String email, String address, String password)
            throws EntityNotFoundException;
}
