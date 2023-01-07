package com.uberTim12.ihor.service.users.impl;

import com.uberTim12.ihor.model.users.Administrator;
import com.uberTim12.ihor.repository.users.IAdministratorRepository;
import com.uberTim12.ihor.service.base.impl.JPAService;
import com.uberTim12.ihor.service.users.interfaces.IAdministratorService;
import jakarta.persistence.EntityNotFoundException;
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

    @Override
    public Administrator update(Integer adminId, String name, String surname, String profilePicture,
                                String telephoneNumber, String email, String address, String password)
            throws EntityNotFoundException {
        Administrator admin = get(adminId);

        admin.setName(name);
        admin.setSurname(surname);
        admin.setProfilePicture(profilePicture);
        admin.setTelephoneNumber(telephoneNumber);
        admin.setEmail(email);
        admin.setAddress(address);
        if (password != null)
            admin.setPassword(password);

        return save(admin);
    }
}
