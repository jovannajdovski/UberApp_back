package com.uberTim12.ihor.service.users.impl;

import com.uberTim12.ihor.exception.EmailAlreadyExistsException;
import com.uberTim12.ihor.model.users.Driver;
import com.uberTim12.ihor.repository.users.IDriverRepository;
import com.uberTim12.ihor.service.base.impl.JPAService;
import com.uberTim12.ihor.service.users.interfaces.IDriverService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class DriverService extends JPAService<Driver> implements IDriverService {
    private final IDriverRepository driverRepository;

    @Autowired
    DriverService(IDriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    @Override
    protected JpaRepository<Driver, Integer> getEntityRepository() {
        return driverRepository;
    }

    @Override
    public Driver findByEmail(String email) {
        return driverRepository.findByEmail(email);
    }

    @Override
    public void register(Driver driver) throws EmailAlreadyExistsException {
        if (findByEmail(driver.getEmail()) != null)
            throw new EmailAlreadyExistsException("User with that email already exists!");

        save(driver);
    }

    @Override
    public Driver update(Integer driverId, String name, String surname, String profilePicture,
                         String telephoneNumber, String email, String address, String password) throws EntityNotFoundException {
        Driver driver = get(driverId);
        driver.setName(name);
        driver.setSurname(surname);
        driver.setProfilePicture(profilePicture);
        driver.setTelephoneNumber(telephoneNumber);
        driver.setEmail(email);
        driver.setAddress(address);
        if (password != null)
            driver.setPassword(password);
        return save(driver);
    }
}
