package com.uberTim12.ihor.service.users.interfaces;

import com.uberTim12.ihor.model.users.Passenger;
import com.uberTim12.ihor.service.base.interfaces.IJPAService;

import java.util.Optional;

public interface IPassengerService extends IJPAService<Passenger> {

    Passenger findByIdWithRides(Integer id);

    Optional<Passenger> findByIdWithFavorites(Integer id);

    Passenger findByEmailWithFavorites(String email);
}
