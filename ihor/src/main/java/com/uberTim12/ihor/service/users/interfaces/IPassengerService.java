package com.uberTim12.ihor.service.users.interfaces;

import com.uberTim12.ihor.model.users.Passenger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IPassengerService {
    Page<Passenger> findAll(Pageable page);

    Passenger findById(Integer id);

    boolean exists(String email);

    Passenger save(Passenger passenger);

    Passenger findByIdWithRides(Integer id);
}
